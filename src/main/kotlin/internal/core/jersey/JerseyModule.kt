package internal.core.jersey

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import internal.core.configuration.Configuration
import internal.core.eventbus.EventHandler
import internal.core.logging.logger
import io.swagger.jaxrs.listing.ApiListingResource
import io.swagger.jaxrs.listing.SwaggerSerializers
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.servlet.DefaultServlet
import org.eclipse.jetty.servlet.ServletContextHandler
import org.eclipse.jetty.servlet.ServletHolder
import org.glassfish.jersey.internal.inject.AbstractBinder
import org.glassfish.jersey.jackson.JacksonFeature
import org.glassfish.jersey.jackson.internal.jackson.jaxrs.json.JacksonJaxbJsonProvider
import org.glassfish.jersey.server.ResourceConfig
import org.glassfish.jersey.servlet.ServletContainer
import org.jvnet.hk2.annotations.Service
import org.reflections.Reflections
import org.reflections.scanners.SubTypesScanner
import java.time.Clock
import javax.inject.Singleton

class JerseyModule constructor(
    private val config: Configuration
) {

    private companion object {
        val logger = logger()
    }

    private val resources = mutableSetOf<Class<out Resource>>()
    private val services = mutableSetOf<Class<*>>()
    private val handlers = mutableSetOf<Class<out EventHandler<*>>>()

    fun resources(root: Package): JerseyModule {
        val reflections = Reflections(root.name, SubTypesScanner::class.java)
        reflections
            .getSubTypesOf(Resource::class.java).asSequence()
            .filter { resource -> !resource.isInterface && !resource.kotlin.isAbstract }
            .forEach { resources.add(it) }
        reflections
            .getTypesAnnotatedWith(Service::class.java).asSequence()
            .forEach { services.add(it) }
        reflections
            .getSubTypesOf(EventHandler::class.java).asSequence()
            .filter { resource -> !resource.isInterface && !resource.kotlin.isAbstract }
            .forEach { handlers.add(it) }
        return this
    }

    fun buildServer(): Server {
        val server = Server(config.int("server.port"))

        // setup servlet context in server
        val context = ServletContextHandler(ServletContextHandler.SESSIONS)
        context.contextPath = "/"
        server.handler = context

        // setup dynamic servlet in context
        val servletContainer = ServletContainer(initResourceConfig())
        val sh = ServletHolder("default", servletContainer)
        context.addServlet(sh, "/*")

        // setup static swagger servlet
        logger.info("Configuring swagger-ui...")
        val apiDocs = javaClass.classLoader.getResource("swagger-ui")
            ?: throw IllegalStateException("No swagger-ui found at /swagger-ui.")
        val holderHome = ServletHolder("swagger", DefaultServlet::class.java)
        holderHome.setInitParameter("resourceBase", apiDocs.toExternalForm())
        holderHome.setInitParameter("pathInfoOnly", "true")
        context.addServlet(holderHome, "/apidocs/*")

        return server
    }

    private fun initResourceConfig(): ResourceConfig {
        val resourceConfig = ResourceConfig()
        resourceConfig.register(AnyExceptionMapper())
        resourceConfig.register(ConstraintViolationExceptionMapper())

        // configure DI
        resources.forEach { resourceConfig.register(it) }
        resourceConfig.register(object : AbstractBinder() {
            override fun configure() {
                services.forEach {
                    bind(it).to(it).`in`(Singleton::class.java)
                }
                handlers.forEach {
                    bind(it).to(EventHandler::class.java).named(it.name).`in`(Singleton::class.java)
                }
                bind(Clock.systemUTC()).to(Clock::class.java).`in`(Singleton::class.java)
            }
        })
        // configure jackson
        val objectMapperProvider = JacksonJaxbJsonProvider()
        objectMapperProvider.setMapper(initObjectMapper())
        resourceConfig.register(objectMapperProvider)
        resourceConfig.register(JacksonFeature::class.java)

        // configure swagger
        resourceConfig.register(ApiListingResource::class.java)
        resourceConfig.register(SwaggerSerializers::class.java)

        return resourceConfig
    }

    private fun initObjectMapper(): ObjectMapper =
        ObjectMapper()
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .registerModule(JavaTimeModule())
            .registerKotlinModule()
}