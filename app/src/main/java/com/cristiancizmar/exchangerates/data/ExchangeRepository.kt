package com.cristiancizmar.exchangerates.data

import com.cristiancizmar.exchangerates.data.rest.ExchangeService
import com.cristiancizmar.exchangerates.model.ExchangeRate
import com.cristiancizmar.exchangerates.util.AUD
import com.cristiancizmar.exchangerates.util.BTC
import com.cristiancizmar.exchangerates.util.CAD
import com.cristiancizmar.exchangerates.util.CHF
import com.cristiancizmar.exchangerates.util.CNY
import com.cristiancizmar.exchangerates.util.ETH
import com.cristiancizmar.exchangerates.util.EUR
import com.cristiancizmar.exchangerates.util.GBP
import com.cristiancizmar.exchangerates.util.INR
import com.cristiancizmar.exchangerates.util.JPY
import com.cristiancizmar.exchangerates.util.USD
import com.cristiancizmar.exchangerates.util.addItemsFirst
import com.cristiancizmar.exchangerates.util.toExchangeRate
import io.reactivex.Single

class ExchangeRepository(private val exchangeService: ExchangeService) {

    /**
     * The API requires and returns lowercase currencies
     * But in the app we only display uppercase currencies
     */

    private val priorityCurrencies = listOf(EUR, USD, GBP, JPY, CNY, AUD, CAD, CHF, INR, BTC, ETH)

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