package com.aseigneurin.kafkah2o

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonInclude.Include

@JsonInclude(Include.NON_NULL)
data class House(
        val date: String,
        val price: Double? = null,
        val bedrooms: Int,
        val bathrooms: Double,
        val sqft_living: Int,
        val sqft_lot: Int,
        val floors: Double,
        val waterfront: String,
        val view: String,
        val condition: String,
        val sqft_above: Int,
        val sqft_basement: Int,
        val yr_built: Int,
        val yr_renovated: Int,
        val street: String,
        val city: String,
        val statezip: String,
        val country: String)
