package com.aseigneurin.kafkah2o

import hex.genmodel.easy.EasyPredictModelWrapper
import hex.genmodel.easy.RowData
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

fun main(args: Array<String>) {
    val rawModel = gbm_b7a697d9_ee22_4c57_9f71_3f6d4825eab9()
    val model = EasyPredictModelWrapper(rawModel)
    val row = RowData().apply {
        put("date", LocalDateTime.parse("2014-05-02 00:00:00", dateTimeFormatter).atOffset(ZoneOffset.UTC).toEpochSecond())
        put("price", 463000.0)
        put("bedrooms", 3.0)
        put("bathrooms", 1.75)
        put("sqft_living", 1710.0)
        put("sqft_lot", 7320.0)
        put("floors", 1.0)
        put("waterfront", "0")
        put("view", "0")
        put("condition", "3")
        put("sqft_above", 1710.0)
        put("sqft_basement", 0.0)
        put("yr_built", 1948.0)
        put("yr_renovated", 1994.0)
        put("street", "Burke-Gilman Trail")
        put("city", "Lake Forest Park")
        put("statezip", "WA 98155")
        put("country", "USA")
    }
    val prediction = model.predictRegression(row)
    println("Price:      \$${row.get("price")}")
    println("Prediction: \$${prediction.value}")
}
