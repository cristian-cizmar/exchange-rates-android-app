package com.cristiancizmar.exchangerates.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cristiancizmar.exchangerates.data.ExchangeRepository
import com.cristiancizmar.exchangerates.data.PreferencesRepository
import com.cristiancizmar.exchangerates.model.ExchangeRate
import com.cristiancizmar.exchangerates.util.DEFAULT_REFRESH_RATE
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val exchangeRepository: ExchangeRepository,
    private val preferencesRepository: PreferencesRepository
) : ViewModel() {

    private val _rates = MutableLiveData<ExchangeRate?>()
    val rates: LiveData<ExchangeRate?> = _rates

    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> = _loading

    private var refreshRate = DEFAULT_REFRESH_RATE

    private val disposable = CompositeDisposable()

    fun updatePreferences() {
        refreshRate = preferencesRepository.getRefreshRate()
    }

    fun getRatesPeriodically() {
        disposable.add(
            Observable.interval(0, refreshRate.toLong(), TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { getRates() }
        )
    }

    fun getMainCurrency() = preferencesRepository.getMainCurrency()

    fun cancelOperations() {
        disposable.clear()
        _loading.value = false
    }

    private fun getRates() {
        if (_loading.value == true) return
        _loading.value = true
        disposable.add(
            exchangeRepository.getExchangeRates(getMainCurrency())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    _loading.value = false
                    _rates.value = it
                }, {
                    _loading.value = false
                    _rates.value = null
                })
        )
    }

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }
}