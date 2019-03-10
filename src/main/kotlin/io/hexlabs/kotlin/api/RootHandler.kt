package io.hexlabs.kotlin.api

import com.beust.jcommander.JCommander
import io.hexlabs.kotlin.playground.Configuration
import org.http4k.core.*
import org.http4k.routing.bind
import org.http4k.routing.routes
import org.http4k.filter.CorsPolicy
import org.http4k.filter.ServerFilters
import org.http4k.format.Jackson.auto
import org.http4k.server.SunHttp
import org.http4k.server.asServer
import java.util.*

val RootHandler = { options: Options ->
    val playground = PlaygroundHandler(Configuration(libs = options.libs, disableSecurity = options.disableSecurity, kotlinVersion = options.kotlinVersion))
    Filters.TRACING
        .then(Filters.CATCH_ALL)
        .let { if (options.cors) it.then(ServerFilters.Cors(CorsPolicy(options.corsAllowedOrigins, options.corsAllowedHeaders, options.corsAllowedMethods.map { m -> Method.valueOf(m) }))) else it }
        .then(
            routes(
                *listOfNotNull(
                    if (!options.disableHealth)
                        (options.healthPath bind Method.GET to { Response(Status(options.healthStatus, "Healthy")).body(options.healthBody) })
                            .also { println("Health endpoint initialised. Will respond with status code ${options.healthStatus} on path ${options.healthPath}") }
                    else null,
                    "/kotlinServer" bind playground.playgroundRoutes
                ).toTypedArray()
            )
        )
}

inline fun <reified T: Any> bodyAs(item: T): (Response) -> Response = Body.auto<T>().toLens().of(item)

fun main(args: Array<String>) {
    val properties = Properties().apply { load(Thread.currentThread().contextClassLoader.getResourceAsStream("configuration.properties")) }
    println(properties.toList().joinToString(", "){ (key, value) -> "$key = $value" })
    val kotlinVersion = properties["kotlin.version"]?.toString() ?: "1.3.0"
    Options(kotlinVersion = kotlinVersion).apply { JCommander.newBuilder().addObject(this).build().parse(*args) }.apply {
        RootHandler(this).asServer(SunHttp(port)).start()
        println("Server started on port $port")
    }
}
