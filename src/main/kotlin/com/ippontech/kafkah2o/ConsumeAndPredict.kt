package com.ippontech.kafkah2o

import hex.genmodel.easy.EasyPredictModelWrapper
import hex.genmodel.easy.RowData
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.KafkaStreams
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.kstream.Consumed
import org.apache.kafka.streams.kstream.KStream
import org.apache.kafka.streams.kstream.Produced
import org.apache.log4j.LogManager
import java.util.*

// $ kafka-topics --zookeeper localhost:2181 --create --topic predictions --replication-factor 1 --partitions 4

fun main(args: Array<String>) {
    StreamsProcessor("localhost:9092").process()
}

class StreamsProcessor(val brokers: String) {

    private val logger = LogManager.getLogger(javaClass)
    private val rawModel = gbm_2760b04d_9dfd_436c_b780_7bdb2f372d85()
    private val model = EasyPredictModelWrapper(rawModel)

    fun process() {
        val streamsBuilder = StreamsBuilder()

        val housesStream: KStream<String, House> = streamsBuilder
                .stream(housesTopic, Consumed.with(Serdes.String(), Serdes.String()))
                .mapValues { v ->
                    val house = jsonMapper.readValue(v, House::class.java)
                    logger.info("House: $house")
                    house
                }

        val predictionsStream: KStream<String, House> = housesStream.mapValues { _, h ->
            val row = RowData().apply {
                put("sqft", h.sqft.toDouble())
                put("lot_size_acres", h.lot_size_acres.toDouble())
                put("stories", h.stories.toDouble())
                put("number_bedrooms", h.number_bedrooms.toDouble())
                put("number_bathrooms", h.number_bathrooms.toDouble())
                put("attached_garage", h.attached_garage)
                put("has_pool", h.has_pool.toYesNo())
                put("has_kitchen_island", h.has_kitchen_island.toYesNo())
                put("main_flooring_type", h.main_flooring_type)
                put("has_granite_counters", h.has_granite_counters.toYesNo())
                put("house_age_years", h.house_age_years.toDouble())
            }
            val prediction = model.predictRegression(row).value.toInt()
            logger.info("Prediction: $prediction")
            h.copy(selling_price = prediction)
        }

        predictionsStream
                .mapValues { _, p -> jsonMapper.writeValueAsString(p) }
                .to(predictionsTopic, Produced.with(Serdes.String(), Serdes.String()))

        val topology = streamsBuilder.build()

        val props = Properties()
        props["bootstrap.servers"] = brokers
        props["application.id"] = "kafka-tutorial"
        val streams = KafkaStreams(topology, props)
        streams.start()
    }
}
