package com.aseigneurin.kafkah2o.serdes

import com.aseigneurin.kafkah2o.jsonMapper
import com.aseigneurin.kafkah2o.zipcodes.ZipCodeData
import com.fasterxml.jackson.core.type.TypeReference
import com.sun.xml.internal.ws.encoding.soap.DeserializationException
import org.apache.kafka.common.errors.SerializationException
import org.apache.kafka.common.serialization.Deserializer
import org.apache.kafka.common.serialization.Serde
import org.apache.kafka.common.serialization.Serializer

class ZipCodeDataSerde : Serde<ZipCodeData> {
    override fun configure(configs: MutableMap<String, *>?, isKey: Boolean) {}
    override fun deserializer(): Deserializer<ZipCodeData> = ZipCodeDataDeserializer()
    override fun serializer(): Serializer<ZipCodeData> = ZipCodeDataSerializer()
    override fun close() {}
}

class ZipCodeDataSerializer : Serializer<ZipCodeData> {

    override fun serialize(topic: String, data: ZipCodeData): ByteArray {
        try {
            return jsonMapper.writeValueAsBytes(data)
        } catch (e: Exception) {
            throw SerializationException("Error serializing ZipCodeData", e)
        }
    }

    override fun configure(configs: MutableMap<String, *>?, isKey: Boolean) {}
    override fun close() {}
}

class ZipCodeDataDeserializer : Deserializer<ZipCodeData> {

    val typeRef = object : TypeReference<ZipCodeData>() {}

    override fun deserialize(topic: String, data: ByteArray): ZipCodeData {
        try {
            val zipCodeData = jsonMapper.readValue<ZipCodeData>(data, typeRef)
            return zipCodeData
        } catch (e: Exception) {
            throw DeserializationException("Error deserializing ZipCodeData", e)
        }
    }

    override fun configure(configs: MutableMap<String, *>?, isKey: Boolean) {}
    override fun close() {}
}