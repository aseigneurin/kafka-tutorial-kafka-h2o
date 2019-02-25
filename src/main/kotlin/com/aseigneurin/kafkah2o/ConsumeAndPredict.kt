package com.aseigneurin.kafkah2o

import hex.genmodel.easy.EasyPredictModelWrapper
import hex.genmodel.easy.RowData
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.KafkaStreams
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.kstream.Consumed
import org.apache.kafka.streams.kstream.KStream
import org.apache.kafka.streams.kstream.Produced
import org.apache.log4j.LogManager
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*

// $ kafka-topics --zookeeper localhost:2181 --create --topic predictions --replication-factor 1 --partitions 4

fun main(args: Array<String>) {
    StreamsProcessor("localhost:9092").process()
}

class StreamsProcessor(val brokers: String) {

    private val logger = LogManager.getLogger(javaClass)
    private val rawModel = deeplearning_35684ba5_d9fb_49db_875b_5a33ff090485()
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
                put("date", LocalDateTime.parse(h.date, dateTimeFormatter).atOffset(ZoneOffset.UTC).toEpochSecond())
                put("price", h.price)
                put("bedrooms", h.bedrooms.toDouble())
                put("bathrooms", h.bathrooms)
                put("sqft_living", h.sqft_living.toDouble())
                put("sqft_lot", h.sqft_lot.toDouble())
                put("floors", h.floors)
                put("waterfront", h.waterfront)
                put("view", h.view)
                put("condition", h.condition)
                put("sqft_above", h.sqft_above.toDouble())
                put("sqft_basement", h.sqft_basement.toDouble())
                put("yr_built", h.yr_built.toDouble())
                put("yr_renovated", h.yr_renovated.toDouble())
                put("street", h.street)
                put("city", h.city)
                put("statezip", h.statezip)
                put("country", h.country)
            }
            val prediction = model.predictRegression(row).value
            logger.info("Prediction: $prediction")
            h.copy(price = prediction)
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
