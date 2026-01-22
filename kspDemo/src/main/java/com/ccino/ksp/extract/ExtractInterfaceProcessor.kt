package com.ccino.ksp.extract

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
import com.google.devtools.ksp.validate
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.ksp.toAnnotationSpec
import com.squareup.kotlinpoet.ksp.toKModifier
import com.squareup.kotlinpoet.ksp.toTypeName
import java.io.OutputStreamWriter
import java.nio.charset.StandardCharsets

private const val TAG = "ExtractInterfaceProcessor"

/**
 * ExtractInterfaceProcessor - 从类中提取接口
 * 
 * 功能：
 * - 扫描带有 @ExtractorInterface 注解的类
 * - 提取其公共方法
 * - 生成对应的接口
 * 
 * 增量编译支持：
 * - ✅ 正确处理 Dependencies（aggregating 模式）
 * - ✅ 验证符号的有效性
 * - ✅ 返回无法处理的符号供后续处理
 * - ✅ 异常处理和日志记录
 * 
 * @property options 插件选项
 * @property logger KSP 日志记录器
 * @property codeGenerator 代码生成器
 */
class ExtractInterfaceProcessor(
    private val options: Map<String, String>,
    private val logger: KSPLogger,
    private val codeGenerator: CodeGenerator
) : SymbolProcessor {
    
    /**
     * 处理带有 @ExtractorInterface 注解的类
     * 
     * @param resolver 符号解析器
     * @return 无法处理的符号列表（用于增量编译）
     */
    override fun process(resolver: Resolver): List<KSAnnotated> {
        logger.info("[$TAG] Starting processing with options: $options")
        
        // 获取所有带有 @ExtractorInterface 注解的符号
        val symbols = resolver
            .getSymbolsWithAnnotation(ExtractorInterface::class.qualifiedName.toString())
            .filterIsInstance<KSClassDeclaration>()
        
        // 分离有效和无效的符号（增量编译支持）
        val validSymbols = mutableListOf<KSClassDeclaration>()
        val invalidSymbols = mutableListOf<KSAnnotated>()
        
        symbols.forEach { symbol ->
            if (symbol.validate()) {
                validSymbols.add(symbol)
            } else {
                invalidSymbols.add(symbol)
                logger.info("[$TAG] Symbol ${symbol.qualifiedName?.asString()} is not valid yet, deferring")
            }
        }
        
        // 处理所有有效的符号
        logger.info("[$TAG] Processing ${validSymbols.size} valid symbols, deferring ${invalidSymbols.size} invalid symbols")
        
        validSymbols.forEach { classDeclaration ->
            try {
                generateInterface(classDeclaration)
            } catch (e: Exception) {
                logger.error("[$TAG] Error processing ${classDeclaration.qualifiedName?.asString()}: ${e.message}", classDeclaration)
            }
        }
        
        logger.info("[$TAG] Processing completed")
        
        // 返回无法处理的符号，让 KSP 在下一轮处理
        return invalidSymbols
    }

    /**
     * 递归获取类及其所有父类
     * 用于确定 Dependencies 的 aggregating 模式
     * 
     * @param classDeclaration 目标类
     * @return 包含该类及所有父类的列表
     */
    private fun classWithParents(
        classDeclaration: KSClassDeclaration
    ): List<KSClassDeclaration> {
        val parents = classDeclaration.superTypes
            .map { it.resolve().declaration }
            .filterIsInstance<KSClassDeclaration>()
            .flatMap { classWithParents(it) }
            .toList()
        
        return parents.plus(classDeclaration)
    }

    /**
     * 生成接口文件
     * 
     * @param annotateClass 带有 @ExtractorInterface 注解的类
     */
    @OptIn(KspExperimental::class)
    private fun generateInterface(annotateClass: KSClassDeclaration) {
        // 1. 获取接口信息
        val interfacePackage = annotateClass.qualifiedName?.getQualifier().orEmpty()
        val annotation = annotateClass.getAnnotationsByType(ExtractorInterface::class).firstOrNull()
        
        if (annotation == null) {
            logger.error("[$TAG] No @ExtractorInterface annotation found on ${annotateClass.qualifiedName?.asString()}", annotateClass)
            return
        }
        
        val interfaceName = annotation.name
        
        if (interfaceName.isBlank()) {
            logger.error("[$TAG] Interface name cannot be blank for ${annotateClass.qualifiedName?.asString()}", annotateClass)
            return
        }
        
        logger.info("[$TAG] Generating interface '$interfaceName' for class ${annotateClass.simpleName.asString()}")
        
        // 2. 提取公共方法
        val publicMethods = annotateClass
            .getDeclaredFunctions()
            .filter { it.isPublic() && !it.isConstructor() }
            .toList()
        
        logger.info("[$TAG] Found ${publicMethods.size} public methods to extract")
        
        // 3. 构建接口代码
        val fileSpec = buildInterfaceFile(interfacePackage, interfaceName, publicMethods.asSequence())
        
        // 4. 确定依赖关系（用于增量编译）
        // aggregating = true: 生成的文件依赖于多个源文件（类 + 父类）
        // aggregating = false: 生成的文件只依赖于单个源文件
        val superType = annotateClass.superTypes.firstOrNull()
        val hasParent = superType?.toString() != "Any" && superType != null
        
        // 收集所有相关的源文件（包括父类）
        val sources = if (hasParent) {
            classWithParents(annotateClass)
                .mapNotNull { it.containingFile }
                .toTypedArray()
        } else {
            // 只依赖当前文件
            annotateClass.containingFile?.let { arrayOf(it) } ?: emptyArray()
        }
        
        logger.info("[$TAG] Dependencies - aggregating: $hasParent, sources: ${sources.size} files")
        
        val dependencies = Dependencies(
            aggregating = hasParent,
            *sources
        )
        
        // 5. 生成文件
        try {
            val file = codeGenerator.createNewFile(
                dependencies,
                fileSpec.packageName,
                fileSpec.name
            )
            
            OutputStreamWriter(file, StandardCharsets.UTF_8).use { writer ->
                fileSpec.writeTo(writer)
            }
            
            logger.info("[$TAG] Successfully generated interface: ${fileSpec.packageName}.${fileSpec.name}")
        } catch (e: Exception) {
            logger.error("[$TAG] Failed to write file ${fileSpec.packageName}.${fileSpec.name}: ${e.message}", annotateClass)
            throw e
        }
    }
    

    /**
     * 构建接口文件（FileSpec）
     * 
     * @param interfacePackage 接口包名
     * @param interfaceName 接口名称
     * @param publicMethods 公共方法列表
     * @return 生成的 FileSpec
     */
    private fun buildInterfaceFile(
        interfacePackage: String,
        interfaceName: String,
        publicMethods: Sequence<KSFunctionDeclaration>,
    ): FileSpec {
        val fileBuilder = FileSpec.builder(interfacePackage, interfaceName)
        
        // 添加文件注释（注意：需要换行符）
        fileBuilder.addFileComment("Auto-generated by ExtractInterfaceProcessor\n")
        fileBuilder.addFileComment("Do not modify this file manually")
        
        // 添加接口类型
        fileBuilder.addType(buildInterface(interfaceName, publicMethods))
        
        return fileBuilder.build()
    }

    /**
     * 构建接口类型（TypeSpec）
     * 
     * @param interfaceName 接口名称
     * @param publicMethods 公共方法列表
     * @return 生成的 TypeSpec
     */
    private fun buildInterface(
        interfaceName: String,
        publicMethods: Sequence<KSFunctionDeclaration>,
    ): TypeSpec {
        val interfaceBuilder = TypeSpec.interfaceBuilder(interfaceName)
        
        // 添加所有公共方法
        val methods = publicMethods.map(::buildInterfaceMethod).toList()
        interfaceBuilder.addFunctions(methods)
        
        return interfaceBuilder.build()
    }

    /**
     * 构建接口方法（FunSpec）
     * 
     * @param function 原始函数声明
     * @return 生成的接口方法规范
     */
    private fun buildInterfaceMethod(
        function: KSFunctionDeclaration,
    ): FunSpec {
        val methodBuilder = FunSpec.builder(function.simpleName.getShortName())
        
        // 添加修饰符（过滤掉不需要的）
        methodBuilder.addModifiers(buildFunctionModifiers(function.modifiers))
        
        // 添加参数
        val parameters = function.parameters.map(::buildInterfaceMethodParameter)
        methodBuilder.addParameters(parameters)
        
        // 添加返回类型
        function.returnType?.let { returnType ->
            methodBuilder.returns(returnType.toTypeName())
        }
        
        // 添加注解
        val annotations = function.annotations
            .map { it.toAnnotationSpec() }
            .toList()
        methodBuilder.addAnnotations(annotations)
        
        return methodBuilder.build()
    }

    /**
     * 构建方法参数（ParameterSpec）
     * 
     * @param variableElement 参数声明
     * @return 生成的参数规范
     */
    private fun buildInterfaceMethodParameter(
        variableElement: KSValueParameter,
    ): ParameterSpec {
        val paramName = variableElement.name?.getShortName() 
            ?: throw IllegalArgumentException("Parameter name cannot be null")
        
        val paramType = variableElement.type.toTypeName()
        
        val paramBuilder = ParameterSpec.builder(paramName, paramType)
        
        // 添加参数注解
        val annotations = variableElement.annotations
            .map { it.toAnnotationSpec() }
            .toList()
        paramBuilder.addAnnotations(annotations)
        
        return paramBuilder.build()
    }

    /**
     * 构建函数修饰符
     * 移除不适合接口的修饰符（OPEN, OVERRIDE），添加 ABSTRACT
     * 
     * @param modifiers 原始修饰符集合
     * @return 适用于接口的修饰符列表
     */
    private fun buildFunctionModifiers(
        modifiers: Set<Modifier>
    ) = modifiers
        .filterNot { it in IGNORED_MODIFIERS }
        .plus(Modifier.ABSTRACT)
        .mapNotNull { it.toKModifier() }
}

private val IGNORED_MODIFIERS = listOf(Modifier.OPEN, Modifier.OVERRIDE)
