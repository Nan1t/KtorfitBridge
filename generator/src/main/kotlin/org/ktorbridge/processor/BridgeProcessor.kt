package org.ktorbridge.processor

import com.google.devtools.ksp.closestClassDeclaration
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import de.jensklingenberg.ktorfit.http.DELETE
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.HEAD
import de.jensklingenberg.ktorfit.http.HTTP
import de.jensklingenberg.ktorfit.http.OPTIONS
import de.jensklingenberg.ktorfit.http.PATCH
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.PUT
import org.ktorbridge.generator.ServiceGenerator

class BridgeProcessor(generator: CodeGenerator) : SymbolProcessor {

    private val serviceMapper = ServiceMapper()
    private val serviceGenerator = ServiceGenerator(generator)

    override fun process(resolver: Resolver): List<KSAnnotated> {
        val services = getAnnotatedClasses(resolver)
            .map { serviceMapper.map(it.key, it.value) }

        for (service in services) {
            serviceGenerator.generate(service)
        }

        return emptyList()
    }

    private fun getAnnotatedClasses(resolver: Resolver): Map<KSClassDeclaration, List<KSAnnotated>> {
        val getAnnotated = resolver.getSymbolsWithAnnotation(GET::class.java.name).toList()
        val postAnnotated = resolver.getSymbolsWithAnnotation(POST::class.java.name).toList()
        val putAnnotated = resolver.getSymbolsWithAnnotation(PUT::class.java.name).toList()
        val deleteAnnotated = resolver.getSymbolsWithAnnotation(DELETE::class.java.name).toList()
        val headAnnotated = resolver.getSymbolsWithAnnotation(HEAD::class.java.name).toList()
        val optionsAnnotated = resolver.getSymbolsWithAnnotation(OPTIONS::class.java.name).toList()
        val patchAnnotated = resolver.getSymbolsWithAnnotation(PATCH::class.java.name).toList()
        val httpAnnotated = resolver.getSymbolsWithAnnotation(HTTP::class.java.name).toList()

        val ksAnnotatedList =
            getAnnotated +
                    postAnnotated +
                    putAnnotated +
                    deleteAnnotated +
                    headAnnotated +
                    optionsAnnotated +
                    patchAnnotated +
                    httpAnnotated

        return ksAnnotatedList
            .filterIsInstance<KSFunctionDeclaration>()
            .groupBy { it.closestClassDeclaration()!! }
    }
}