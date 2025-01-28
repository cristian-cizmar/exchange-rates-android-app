package com.cristiancizmar.exchangerates.data

import android.content.SharedPreferences
import androidx.core.content.edit
import com.cristiancizmar.exchangerates.util.DEFAULT_CURRENCY
import com.cristiancizmar.exchangerates.util.DEFAULT_REFRESH_RATE
import com.cristiancizmar.exchangerates.util.PACKAGE
import javax.inject.Inject

private const val KEY_REFRESH_RATE_SECONDS = "$PACKAGE.refreshRateSeconds"
private const val KEY_MAIN_CURRENCY = "$PACKAGE.mainCurrency"

class AppSharedPreferences @Inject constructor(private val sharedPreferences: SharedPreferences) {

    fun getRefreshRate() = sharedPreferences.getInt(KEY_REFRESH_RATE_SECONDS, DEFAULT_REFRESH_RATE)

    fun saveRefreshRate(refreshRate: Int) {
        sharedPreferences.edit { putInt(KEY_REFRESH_RATE_SECONDS, refreshRate) }
    }

    fun getMainCurrency() = sharedPreferences.getString(KEY_MAIN_CURRENCY, DEFAULT_CURRENCY)

    fun saveMainCurrency(currency: String) {
        sharedPreferences.edit { putString(KEY_MAIN_CURRENCY, currency) }
    }
}