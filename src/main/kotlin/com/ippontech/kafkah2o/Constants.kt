package com.ippontech.kafkah2o

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.util.StdDateFormat
import com.fasterxml.jackson.module.kotlin.registerKotlinModule

val housesTopic = "housing"
val predictionsTopic = "predictions"

val jsonMapper = ObjectMapper().apply {
    registerKotlinModule()
    disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
    setDateFormat(StdDateFormat())
}

fun Boolean.toYesNo(): String = when (this) {
    true -> "yes"
    else -> "no"
}
