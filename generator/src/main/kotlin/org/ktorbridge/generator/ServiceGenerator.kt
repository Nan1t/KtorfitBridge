package org.ktorbridge.generator

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ksp.toTypeName
import com.squareup.kotlinpoet.ksp.writeTo
import org.ktorbridge.processor.symbols.EndpointParameter
import org.ktorbridge.processor.symbols.ServiceDescriptor
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.FunSpec
import org.ktorbridge.processor.symbols.HttpMethodType

class ServiceGenerator(
    private val generator: CodeGenerator
) {

    fun generate(descriptor: ServiceDescriptor) {
        val pkg = "${descriptor.packageName}.server"
        val interfaceName = descriptor.name
        val builder = FileSpec.builder(pkg, interfaceName)

        builder.addType(generateServices(descriptor))
        builder.addFunction(generateExtensions(descriptor))
        builder.build().writeTo(
            codeGenerator = generator,
            Dependencies(
                aggregating = false,
                sources = arrayOf(descriptor.containingFile)
            )
        )
    }

    private fun generateServices(descriptor: ServiceDescriptor): TypeSpec {
        val interfaceBuilder = TypeSpec.interfaceBuilder(descriptor.name)
            .addModifiers(KModifier.PUBLIC)

        descriptor.endpoints.forEach { ep ->
            val funBuilder = FunSpec.builder(ep.name)
                .addModifiers(KModifier.ABSTRACT)

            if (ep.isSuspended) {
                funBuilder.addModifiers(KModifier.SUSPEND)
            }

            ep.parameters.forEach { param ->
                val typeName: TypeName = when (param) {
                    is EndpointParameter.Body ->
                        if (param.type.declaration.qualifiedName?.asString()
                            == "io.ktor.client.request.forms.MultiPartFormDataContent"
                        ) {
                            ClassName("io.ktor.http.content", "MultiPartData")
                        } else {
                            param.type.toTypeName()
                        }
                    else -> param.type.toTypeName()
                }

                funBuilder.addParameter(param.name, typeName)
            }

            val returnTypeName = ep.returnType.parameterType.toTypeName()
            funBuilder.returns(returnTypeName)

            interfaceBuilder.addFunction(funBuilder.build())
        }

        return interfaceBuilder.build()
    }

    private fun generateExtensions(descriptor: ServiceDescriptor): FunSpec {
        val pkg = "${descriptor.packageName}.server"

        val getFn = MemberName("io.ktor.server.routing", "get")
        val postFn = MemberName("io.ktor.server.routing", "post")
        val putFn = MemberName("io.ktor.server.routing", "put")
        val deleteFn = MemberName("io.ktor.server.routing", "delete")
        val headFn = MemberName("io.ktor.server.routing", "head")
        val optionsFn = MemberName("io.ktor.server.routing", "options")
        val patchFn = MemberName("io.ktor.server.routing", "patch")
        val respondFn = MemberName("io.ktor.server.response", "respond")
        val receiveFn = MemberName("io.ktor.server.request", "receive")
        val receiveMultipartFn = MemberName("io.ktor.server.request", "receiveMultipart")

        val routeClass = ClassName("io.ktor.server.routing", "Route")
        val serviceInterface = ClassName(pkg, descriptor.name)
        val funName = "route${descriptor.name}"

        val funBuilder = FunSpec.builder(funName)
            .addModifiers(KModifier.PUBLIC)
            .receiver(routeClass)
            .addParameter("impl", serviceInterface)

        descriptor.endpoints.forEach { ep ->
            val methodMember = when (ep.httpMethod) {
                HttpMethodType.GET     -> getFn
                HttpMethodType.POST    -> postFn
                HttpMethodType.PUT     -> putFn
                HttpMethodType.DELETE  -> deleteFn
                HttpMethodType.HEAD    -> headFn
                HttpMethodType.OPTIONS -> optionsFn
                HttpMethodType.PATCH   -> patchFn
            }

            funBuilder.beginControlFlow("%M(%S)", methodMember, ep.path)

            ep.parameters.forEach { param ->
                when (param) {
                    is EndpointParameter.Path -> {
                        val base = "call.pathParameters[%S]!!"
                        val expr = when (param.type.declaration.simpleName.asString()) {
                            "Int"     -> "$base.toInt()"
                            "Long"    -> "$base.toLong()"
                            "Double"  -> "$base.toDouble()"
                            "Boolean" -> "$base.toBoolean()"
                            else      -> base
                        }
                        funBuilder.addStatement("val %N = $expr", param.name, param.key)
                    }
                    is EndpointParameter.Query -> {
                        val base = "call.request.queryParameters[%S]!!"
                        val expr = when (param.type.declaration.simpleName.asString()) {
                            "Int"     -> "$base.toInt()"
                            "Long"    -> "$base.toLong()"
                            "Double"  -> "$base.toDouble()"
                            "Boolean" -> "$base.toBoolean()"
                            else      -> base
                        }
                        funBuilder.addStatement("val %N = $expr", param.name, param.key)
                    }
                    is EndpointParameter.Body -> {
                        val qName = param.type.declaration.qualifiedName?.asString()
                        if (qName == "io.ktor.client.request.forms.MultiPartFormDataContent") {
                            funBuilder.addStatement("val %N = call.%M()", param.name, receiveMultipartFn)
                        } else {
                            val t = param.type.toTypeName()
                            funBuilder.addStatement("val %N = call.%M<%T>()", param.name, receiveFn, t)
                        }
                    }
                }
            }

            val argsList = ep.parameters.joinToString(", ") { it.name }

            funBuilder.addStatement("val result = impl.%N($argsList)", ep.name)
            funBuilder.addStatement("call.%M(result)", respondFn)

            funBuilder.endControlFlow()
        }

        return funBuilder.build()
    }

}