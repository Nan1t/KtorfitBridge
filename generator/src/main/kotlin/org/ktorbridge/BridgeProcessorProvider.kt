package org.ktorbridge

import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider
import org.ktorbridge.processor.BridgeProcessor

class BridgeProcessorProvider : SymbolProcessorProvider {

    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        val basePackage = environment.options["bridgeBasePackage"]
            ?: error("Base package for ktorfit bridge is not specified")
        val overridePackage = environment.options["bridgeOverridePackage"]
            ?: error("Override package for ktorfit bridge is not specified")

        return BridgeProcessor(environment.codeGenerator, basePackage, overridePackage)
    }
}