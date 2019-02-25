package com.aseigneurin.kafkah2o

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
            val sqft = 1000 + random.nextInt(2000)
            val house = House(
                    date = "2014-05-02 00:00:00",
                    bedrooms = random.nextInt(5),
                    bathrooms = random.nextInt(3).toDouble(),
                    sqft_living = sqft,
                    sqft_lot = sqft * 2,
                    floors = random.nextInt(3).toDouble(),
                    waterfront = "0",
                    view = "0",
                    condition = random.nextInt(4).toString(),
                    sqft_above = sqft,
                    sqft_basement = 0,
                    yr_built = 1900 + random.nextInt(80),
                    yr_renovated = 1980 + random.nextInt(25),
                    street = "Burke-Gilman Trail",
                    city = "Seattle",
                    statezip = "WA 98155",
                    country = "USA"
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
