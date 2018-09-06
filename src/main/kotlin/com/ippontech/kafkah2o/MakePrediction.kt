package com.ippontech.kafkah2o

import hex.genmodel.easy.EasyPredictModelWrapper
import hex.genmodel.easy.RowData

fun main(args: Array<String>) {
    val rawModel = gbm_2760b04d_9dfd_436c_b780_7bdb2f372d85()
    val model = EasyPredictModelWrapper(rawModel)
    val row = RowData().apply {
        put("sqft", 2342.0)
        put("lot_size_acres", 0.4)
        put("stories", 1.0)
        put("number_bedrooms", 3.0)
        put("number_bathrooms", 3.0)
        put("attached_garage", "yes")
        put("has_pool", "no")
        put("has_kitchen_island", "yes")
        put("main_flooring_type", "hardwood")
        put("has_granite_counters", "yes")
        put("house_age_years", 4.0)
    }
    val prediction = model.predictRegression(row)
    println("Prediction: \$${prediction.value}")
}
