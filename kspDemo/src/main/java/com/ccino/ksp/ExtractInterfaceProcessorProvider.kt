package com.ccino.ksp

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.google.devtools.ksp.validate

private const val TAG = "ExtractInterfaceSymbol"

class ExtractInterfaceProcessorProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        return ExtractInterfaceProcessor(
            environment.options,
            environment.logger,
            environment.codeGenerator
        )
    }
}

class BindViewProcessor(private val logger: KSPLogger, private val generator: CodeGenerator) : SymbolProcessor {
    override fun process(resolver: Resolver): List<KSAnnotated> {
        val annotate = BindView::class.qualifiedName as String
        val symbols = resolver.getSymbolsWithAnnotation(annotate)
        logger.warn("symbol=$annotate, size=${symbols.count()}")
        val retList = mutableListOf<KSAnnotated>()
        val pendingList = mutableListOf<KSPropertyDeclaration>()
        symbols.forEach {
            if (!it.validate()) {
                retList.add(it)
                return@forEach
            }
            if (it is KSPropertyDeclaration) pendingList.add(it)
        }
        return retList
    }
}