package org.ktorbridge.processor.symbols

import com.google.devtools.ksp.symbol.KSFile

data class ServiceDescriptor(
    val packageName: String,
    val containingFile: KSFile,
    val name: String,
    val endpoints: List<EndpointDescriptor>
)