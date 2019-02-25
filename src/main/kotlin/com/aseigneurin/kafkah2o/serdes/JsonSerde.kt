package com.aseigneurin.kafkah2o.serdes

import com.aseigneurin.kafkah2o.House
import com.aseigneurin.kafkah2o.jsonMapper
import com.sun.xml.internal.ws.encoding.soap.DeserializationException
import org.apache.kafka.common.errors.SerializationException
import org.apache.kafka.common.serialization.Deserializer
import org.apache.kafka.common.serialization.Serde
import org.apache.kafka.common.serialization.Serializer

class JsonSerde<T>(val typeOfT: Class<T>) : Serde<T> {
    override fun configure(configs: MutableMap<String, *>?, isKey: Boolean) {}
    override fun deserializer(): Deserializer<T> = JsonDeserializer(typeOfT)
    override fun serializer(): Serializer<T> = JsonSerializer()
    override fun close() {}
}

class JsonSerializer<T> : Serializer<T> {

    override fun serialize(topic: String, data: T): ByteArray {
        try {
            return jsonMapper.writeValueAsBytes(data)
        } catch (e: Exception) {
            throw SerializationException("Error serializing Json", e)
        }
    }

    override fun configure(configs: MutableMap<String, *>?, isKey: Boolean) {}
    override fun close() {}
}

class JsonDeserializer<T>(val typeOfT: Class<T>) : Deserializer<T> {

    override fun deserialize(topic: String, data: ByteArray): T {
        try {
            val res = jsonMapper.readValue<T>(data, typeOfT)
            return res
        } catch (e: Exception) {
            throw DeserializationException("Error deserializing Json", e)
        }
    }

    override fun configure(configs: MutableMap<String, *>?, isKey: Boolean) {}
    override fun close() {}
}