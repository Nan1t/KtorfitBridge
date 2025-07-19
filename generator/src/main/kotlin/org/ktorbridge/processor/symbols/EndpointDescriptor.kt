package org.ktorbridge.processor.symbols

import com.google.devtools.ksp.symbol.KSType

data class EndpointDescriptor(
    val name: String,
    val path: String,
    val httpMethod: HttpMethodType,
    val parameters: List<EndpointParameter>,
    val returnType: ReturnTypeDescriptor,
    val isSuspended: Boolean
)

data class ReturnTypeDescriptor(
    val name: String,
    val parameterType: KSType
)

enum class HttpMethodType {
    GET,
    POST,
    PUT,
    DELETE,
    HEAD,
    OPTIONS,
    PATCH
}