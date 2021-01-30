import internal.core.configuration.buildConfiguration
import internal.core.jersey.JerseyModule

object Main {

    @JvmStatic
    fun main(args: Array<String>) {
        JerseyModule(
            buildConfiguration("config.properties")
        )
            .resources(Main::class.java.`package`)
            .buildServer()
            .also { server ->
                server.start()
                server.join()
            }
    }
}