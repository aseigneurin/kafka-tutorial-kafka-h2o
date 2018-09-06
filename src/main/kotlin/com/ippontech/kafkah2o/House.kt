package com.ippontech.kafkah2o

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonInclude.Include

@JsonInclude(Include.NON_NULL)
data class House(
        val sqft: Int,
        val lot_size_acres: Float,
        val stories: Int,
        val number_bedrooms: Int,
        val number_bathrooms: Int,
        val attached_garage: Boolean,
        val has_pool: Boolean,
        val has_kitchen_island: Boolean,
        val main_flooring_type: String,
        val has_granite_counters: Boolean,
        val house_age_years: Int,
        val selling_price: Int? = null)
