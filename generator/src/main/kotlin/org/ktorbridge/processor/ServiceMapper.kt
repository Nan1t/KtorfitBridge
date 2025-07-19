package org.ktorbridge.processor

import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.Modifier
import org.ktorbridge.processor.symbols.EndpointDescriptor
import org.ktorbridge.processor.symbols.EndpointParameter
import org.ktorbridge.processor.symbols.HttpMethodType
import org.ktorbridge.processor.symbols.ReturnTypeDescriptor
import org.ktorbridge.processor.symbols.ServiceDescriptor

class ServiceMapper {

    fun map(serviceClass: KSClassDeclaration, methods: List<KSAnnotated>): ServiceDescriptor {
        val serviceName = serviceClass.simpleName.asString()
        val servicePackage = serviceClass.packageName.asString()
        val serviceFile = serviceClass.containingFile ?: error("No containing file for service $serviceName")

        val endpoints = methods
            .filterIsInstance<KSFunctionDeclaration>()
            .map { func ->
                val httpAnn = func.annotations.first { ann ->
                    val pkg = ann.annotationType.resolve().declaration.qualifiedName?.asString()
                    pkg?.startsWith("de.jensklingenberg.ktorfit.http") == true
                }
                val annName = httpAnn.annotationType.resolve().declaration.simpleName.asString()

                val httpMethodType = if (annName == "HTTP") {
                    val methodArg = httpAnn.arguments
                        .first { it.name?.asString() == "method" }
                        .value as String
                    HttpMethodType.valueOf(methodArg)
                } else {
                    HttpMethodType.valueOf(annName)
                }

                val pathArgName = if (annName == "HTTP") "path" else "value"
                val path = httpAnn.arguments
                    .first { it.name?.asString() == pathArgName }
                    .value as String

                val params = func.parameters.mapNotNull { param ->
                    val pAnn = param.annotations.firstOrNull { ann ->
                        val short = ann.annotationType
                            .resolve()
                            .declaration
                            .simpleName
                            .asString()
                        short in listOf("Path", "Query", "Body")
                    } ?: return@mapNotNull null

                    val shortName = pAnn.annotationType
                        .resolve()
                        .declaration
                        .simpleName
                        .asString()
                    val paramName = param.name!!.asString()
                    val paramType = param.type.resolve()

                    when (shortName) {
                        "Path" -> {
                            val key = pAnn.arguments
                                .first { it.name?.asString() == "value" }
                                .value as String
                            EndpointParameter.Path(key, paramName, paramType)
                        }
                        "Query" -> {
                            val key = pAnn.arguments
                                .first { it.name?.asString() == "value" }
                                .value as String
                            EndpointParameter.Query(key, paramName, paramType)
                        }
                        "Body" -> EndpointParameter.Body(paramName, paramType)
                        else -> null
                    }
                }

                val returnTypeRef = func.returnType?.resolve()
                    ?: error("Method ${func.simpleName.asString()} has no returnType")
                val returnDescriptor = ReturnTypeDescriptor(
                    name = returnTypeRef.declaration.simpleName.asString(),
                    parameterType = returnTypeRef
                )
                val isSuspended = func.modifiers.find { it == Modifier.SUSPEND } != null

                EndpointDescriptor(
                    name = func.simpleName.asString(),
                    path = path,
                    httpMethod = httpMethodType,
                    parameters = params,
                    returnType = returnDescriptor,
                    isSuspended = isSuspended
                )
            }

        // один ServiceDescriptor на один KSClassDeclaration
        return ServiceDescriptor(
            servicePackage,
            serviceFile,
            serviceName,
            endpoints
        )
    }

}