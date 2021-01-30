package com.kiko.calendar

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.glassfish.jersey.jackson.internal.jackson.jaxrs.json.JacksonJaxbJsonProvider
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset

val FIXED_CLOCK = Clock.fixed(Instant.EPOCH, ZoneOffset.UTC)

val jacksonJsonProvider = JacksonJaxbJsonProvider().apply {
    setMapper(
        ObjectMapper()
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .registerModule(JavaTimeModule())
            .registerKotlinModule()
    )
}