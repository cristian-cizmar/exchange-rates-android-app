package com.cristiancizmar.exchangerates.data.rest

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

/**
 * API: https://github.com/fawazahmed0/exchange-api
 */

private const val BASE_URL = "https://cdn.jsdelivr.net/npm/@fawazahmed0/"

class ExchangeService : ExchangeApiInterface {

    private val exchangeApi: ExchangeApiInterface by lazy {
        val interceptor = HttpLoggingInterceptor()
        interceptor.apply { interceptor.level = HttpLoggingInterceptor.Level.BODY }
        val client: OkHttpClient = OkHttpClient.Builder().addInterceptor(interceptor).build()
        val gson = GsonBuilder().setLenient().create()
        val retrofit = Retrofit.Builder().baseUrl(BASE_URL).client(client)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create()).build()
        retrofit.create(ExchangeApiInterface::class.java)
    }

    override fun getCurrencies() = exchangeApi.getCurrencies()

    override fun getExchangeRates(baseCurrency: String) = exchangeApi.getExchangeRates(baseCurrency)

    override fun getHistoricRates(date: String, baseCurrency: String) =
        exchangeApi.getHistoricRates(date, baseCurrency)
}