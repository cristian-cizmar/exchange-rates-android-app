package com.cristiancizmar.exchangerates.data

import com.cristiancizmar.exchangerates.util.DEFAULT_CURRENCY

class PreferencesRepository(private val preferencesApp: AppSharedPreferences) {

    fun getRefreshRate() = preferencesApp.getRefreshRate()

    fun saveRefreshRate(refreshRate: Int) {
        preferencesApp.saveRefreshRate(refreshRate)
    }

    fun getMainCurrency() = preferencesApp.getMainCurrency()?.uppercase() ?: DEFAULT_CURRENCY

    fun saveMainCurrency(currency: String) {
        preferencesApp.saveMainCurrency(currency.uppercase().substringBefore(":"))
    }
}