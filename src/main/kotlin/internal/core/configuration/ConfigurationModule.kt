package internal.core.configuration

import Main
import internal.core.exception.Exceptions.internal
import java.util.*

class Configuration constructor(private val properties: Properties) {

    fun string(key: String): String = properties[key]?.toString() ?: throw internal("Missed property '$key'.")
    fun int(key: String): Int = string(key).toInt()
}

fun buildConfiguration(fileName: String): Configuration {
    val properties = Properties()
    Main::class.java.classLoader.getResourceAsStream(fileName)
        .use { properties.load(it) }
    return Configuration(properties)
}