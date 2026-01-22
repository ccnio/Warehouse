package com.ccino.ksp.extract

import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider

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
