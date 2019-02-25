package com.aseigneurin.kafkah2o

import com.aseigneurin.kafkah2o.serdes.JsonSerde
import com.aseigneurin.kafkah2o.serdes.ZipCodeDataSerde
import com.aseigneurin.kafkah2o.zipcodes.ZipCodeData
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.KafkaStreams
import org.apache.kafka.streams.KeyValue
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.kstream.*
import java.util.*

// $ kafka-topics --zookeeper localhost:2181 --create --topic predictions --replication-factor 1 --partitions 4

fun main(args: Array<String>) {
    EnrichHouseData("localhost:9092").process()
}

data class HouseAndZipCodeData(
        val house: House,
        val zipCodeData: ZipCodeData
)

class EnrichHouseData(val brokers: String) {
    fun process() {
        val streamsBuilder = StreamsBuilder()

        val housesStream: KStream<String, House> = streamsBuilder
                .stream(housesTopic, Consumed.with(Serdes.String(), JsonSerde(House::class.java)))
                .map { _, house -> KeyValue(house.statezip.substringAfter(" "), house) }

        val zipCodesTable: KTable<String, Map<String, String>> = streamsBuilder
                .table(zipCodesTopic, Consumed.with(Serdes.String(), ZipCodeDataSerde()))

        housesStream.join(zipCodesTable,
                { house, zipCodeData -> HouseAndZipCodeData(house, zipCodeData) },
                Joined.with(Serdes.String(), JsonSerde(House::class.java), ZipCodeDataSerde()))
                .to(enrichedHousesTopic, Produced.with(Serdes.String(), JsonSerde(HouseAndZipCodeData   ::class.java)))

        val topology = streamsBuilder.build()

        val props = Properties()
        props["bootstrap.servers"] = brokers
        props["application.id"] = "kafka-tutorial"
        val streams = KafkaStreams(topology, props)
        streams.start()
    }
}
