package com.cristiancizmar.exchangerates.data

import com.cristiancizmar.exchangerates.data.rest.ExchangeService
import com.cristiancizmar.exchangerates.model.ExchangeRate
import com.cristiancizmar.exchangerates.util.EUR
import com.cristiancizmar.exchangerates.util.GBP
import com.cristiancizmar.exchangerates.util.USD
import com.cristiancizmar.exchangerates.util.addItemsFirst
import com.cristiancizmar.exchangerates.util.toExchangeRate
import io.reactivex.Single

class ExchangeRepository(private val exchangeService: ExchangeService) {

    /**
     * The API requires and returns lowercase currencies
     * But in the app we only display uppercase currencies
     */

    private val priorityCurrencies = listOf(EUR, USD, GBP)

    fun getCurrencies(): Single<Map<String, String>> =
        exchangeService.getCurrencies()
            .map { it.mapKeys { keys -> keys.key.uppercase() } }
            .map { result -> result.addItemsFirst(priorityCurrencies) }

    fun getExchangeRates(baseCurrency: String) =
        exchangeService.getExchangeRates(baseCurrency.lowercase())
            .map { it.toExchangeRate() }
            .map { ExchangeRate(it.date, it.rates.addItemsFirst(priorityCurrencies)) }

    fun getHistoricRates(date: String, baseCurrency: String) =
        exchangeService.getHistoricRates(date, baseCurrency.lowercase())
            .map { it.toExchangeRate() }
}