package com.ippontech.kafkah2o

import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.Producer
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.StringSerializer
import org.apache.log4j.LogManager
import java.util.*

// $ kafka-topics --zookeeper localhost:2181 --create --topic housing --replication-factor 1 --partitions 4

fun main(args: Array<String>) {
    ProduceMockData("localhost:9092").produce()
}

class ProduceMockData(brokers: String) {

    private val logger = LogManager.getLogger(javaClass)
    private val producer = createProducer(brokers)

    private fun createProducer(brokers: String): Producer<String, String> {
        val props = Properties()
        props["bootstrap.servers"] = brokers
        props["key.serializer"] = StringSerializer::class.java
        props["value.serializer"] = StringSerializer::class.java
        return KafkaProducer<String, String>(props)
    }

    fun produce() {
        val random = Random()
        while (true) {
            val house = House(
                    sqft = 1000 + random.nextInt(2000),
                    lot_size_acres = random.nextFloat(),
                    stories = random.nextInt(3),
                    number_bedrooms = random.nextInt(5),
                    number_bathrooms = random.nextInt(4),
                    attached_garage = random.nextBoolean(),
                    has_pool = random.nextBoolean(),
                    has_kitchen_island = random.nextBoolean(),
                    main_flooring_type = "hardwood",
                    has_granite_counters = random.nextBoolean(),
                    house_age_years = random.nextInt(20)
            )

            val houseJson = jsonMapper.writeValueAsString(house)
            logger.debug("JSON data: $houseJson")

            val futureResult = producer.send(ProducerRecord(housesTopic, houseJson))
            logger.debug("Sent a record")

            Thread.sleep(3000)

            // wait for the write acknowledgment
            futureResult.get()
        }
    }
}
