package org.ktorbridge.processor.symbols

import com.google.devtools.ksp.symbol.KSType

sealed class EndpointParameter(
    /**
     * Name of the parameter
     */
    open val name: String,
    /**
     * Type of the parameter
     */
    open val type: KSType
) {
    /**
     * Represents @Path annotation for method argument
     * @param key A value of @Path annotation
     */
    data class Path(
        val key: String,
        override val name: String,
        override val type: KSType
    ) : EndpointParameter(name, type)

    /**
     * Represents @Query annotation for method argument
     * @param key A value of @Query annotation
     */
    data class Query(
        val key: String,
        override val name: String,
        override val type: KSType
    ) : EndpointParameter(name, type)

    /**
     * Represents @Body annotation for method argument
     */
    data class Body(
        override val name: String,
        override val type: KSType
    ) : EndpointParameter(name, type)
}