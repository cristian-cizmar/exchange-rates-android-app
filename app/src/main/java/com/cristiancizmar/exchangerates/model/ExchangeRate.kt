package com.cristiancizmar.exchangerates.model

data class ExchangeRate(
    var date: String,
    var rates: Map<String, Float>,
)