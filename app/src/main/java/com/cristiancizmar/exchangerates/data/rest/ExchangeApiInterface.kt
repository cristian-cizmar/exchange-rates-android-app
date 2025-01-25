package com.cristiancizmar.exchangerates.data.rest

import com.google.gson.JsonElement
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path

interface ExchangeApiInterface {

    @GET("/npm/@fawazahmed0/currency-api@latest/v1/currencies.json")
    fun getCurrencies(): Single<Map<String, String>>

    @GET("/npm/@fawazahmed0/currency-api@latest/v1/currencies/{base}.json")
    fun getExchangeRates(@Path("base") baseCurrency: String): Single<JsonElement>

    @GET("/npm/@fawazahmed0/currency-api@{date}/v1/currencies/{base}.json")
    fun getHistoricRates(
        @Path("date") date: String,
        @Path("base") baseCurrency: String
    ): Single<JsonElement>
}