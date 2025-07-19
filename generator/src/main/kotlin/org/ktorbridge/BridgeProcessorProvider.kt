package org.ktorbridge

import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider
import org.ktorbridge.processor.BridgeProcessor

class BridgeProcessorProvider : SymbolProcessorProvider {

    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        return BridgeProcessor(environment.codeGenerator)
    }
}