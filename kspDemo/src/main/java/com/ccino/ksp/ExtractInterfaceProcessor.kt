package com.ccino.ksp

import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.getAnnotationsByType
import com.google.devtools.ksp.getDeclaredFunctions
import com.google.devtools.ksp.isConstructor
import com.google.devtools.ksp.isPublic
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSValueParameter
import com.google.devtools.ksp.symbol.Modifier
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.ksp.toAnnotationSpec
import com.squareup.kotlinpoet.ksp.toKModifier
import com.squareup.kotlinpoet.ksp.toTypeName
import java.io.OutputStreamWriter
import java.nio.charset.StandardCharsets

private const val TAG = "ExtractInterfaceProcess"

class ExtractInterfaceProcessor(
    private val options: Map<String, String>,
    private val logger: KSPLogger,
    private val codeGenerator: CodeGenerator
) : SymbolProcessor {
    override fun process(resolver: Resolver): List<KSAnnotated> {
        logger.info("option: ${options["parameter"]}")
        resolver.getSymbolsWithAnnotation(ExtractorInterface::class.qualifiedName.toString())
            .filterIsInstance<KSClassDeclaration>()
            .forEach(::generateInterface)
        return emptyList()
    }

    private fun classWithParents(
        classDeclaration: KSClassDeclaration
    ): List<KSClassDeclaration> =
        classDeclaration.superTypes
            .map { it.resolve().declaration }
            .filterIsInstance<KSClassDeclaration>()
            .flatMap { classWithParents(it) }
            .toList()
            .plus(classDeclaration)

    @OptIn(KspExperimental::class)
    private fun generateInterface(annotateClass: KSClassDeclaration) {
        val interfacePackage = annotateClass.qualifiedName?.getQualifier().orEmpty()
        val interfaceName = annotateClass.getAnnotationsByType(ExtractorInterface::class).single().name
        val publicMethods = annotateClass.getDeclaredFunctions().filter { it.isPublic() && !it.isConstructor() }

        val fileSpec = buildInterfaceFile(interfacePackage, interfaceName, publicMethods)

        val containingFile = annotateClass.containingFile
//        annotateClass.annotations.
        val superType = annotateClass.superTypes.first()

        val aggregating = superType.toString() != "Any"
        val sources = classWithParents(annotateClass)
            .mapNotNull { it.containingFile }
            .toTypedArray()
        logger.warn("containing = $containingFile, aggregating=${aggregating}, sources=${sources.joinToString()}")
        val dependencies = Dependencies(
            aggregating = aggregating,
            *sources
        )
        val file = codeGenerator.createNewFile(dependencies, fileSpec.packageName, fileSpec.name)
        OutputStreamWriter(file, StandardCharsets.UTF_8).use(fileSpec::writeTo)
    }

    private fun buildInterfaceFile(
        interfacePackage: String,
        interfaceName: String,
        publicMethods: Sequence<KSFunctionDeclaration>,
    ): FileSpec = FileSpec
        .builder(interfacePackage, interfaceName)
        .addType(buildInterface(interfaceName, publicMethods))
        .build()

    private fun buildInterface(
        interfaceName: String,
        publicMethods: Sequence<KSFunctionDeclaration>,
    ): TypeSpec = TypeSpec
        .interfaceBuilder(interfaceName)
        .addFunctions(
            publicMethods
                .map(::buildInterfaceMethod).toList()
        )
        .build()

    private fun buildInterfaceMethod(
        function: KSFunctionDeclaration,
    ): FunSpec = FunSpec
        .builder(function.simpleName.getShortName())
        .addModifiers(buildFunctionModifiers(function.modifiers))
        .addParameters(
            function.parameters
                .map(::buildInterfaceMethodParameter)
        )
        .returns(function.returnType!!.toTypeName())
        .addAnnotations(
            function.annotations
                .map { it.toAnnotationSpec() }
                .toList()
        )
        .build()

    private fun buildInterfaceMethodParameter(
        variableElement: KSValueParameter,
    ): ParameterSpec = ParameterSpec
        .builder(
            variableElement.name!!.getShortName(),
            variableElement.type.toTypeName(),
        )
        .addAnnotations(
            variableElement.annotations
                .map { it.toAnnotationSpec() }.toList()
        )
        .build()


    private fun buildFunctionModifiers(
        modifiers: Set<Modifier>
    ) = modifiers
        .filterNot { it in IGNORED_MODIFIERS }
        .plus(Modifier.ABSTRACT)
        .mapNotNull { it.toKModifier() }
}

private val IGNORED_MODIFIERS = listOf(Modifier.OPEN, Modifier.OVERRIDE)