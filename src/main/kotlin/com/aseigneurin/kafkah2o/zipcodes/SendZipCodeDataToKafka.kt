package com.aseigneurin.kafkah2o.zipcodes

import com.aseigneurin.kafkah2o.jsonMapper
import com.aseigneurin.kafkah2o.zipCodesTopic
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.StringSerializer
import java.util.*

fun main() {
    val props = Properties()
    props["bootstrap.servers"] = "localhost:9092"
    props["key.serializer"] = StringSerializer::class.java
    props["value.serializer"] = StringSerializer::class.java
    val producer = KafkaProducer<String, String>(props)

    val data = ZipCodeDataLoader().loadData()
    data.forEach {
        println("${it.key} -> ${it.value.count()} keys")
        val value = jsonMapper.writeValueAsString(it.value)
        producer.send(ProducerRecord(zipCodesTopic, it.key, value))
    }

    producer.close()
}