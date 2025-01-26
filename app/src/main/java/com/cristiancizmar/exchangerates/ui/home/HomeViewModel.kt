package com.cristiancizmar.exchangerates.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cristiancizmar.exchangerates.data.ExchangeRepository
import com.cristiancizmar.exchangerates.data.PreferencesRepository
import com.cristiancizmar.exchangerates.model.ExchangeRate
import com.cristiancizmar.exchangerates.util.DEFAULT_REFRESH_RATE
import com.cristiancizmar.exchangerates.util.State
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

    private val _rates = MutableLiveData<State<ExchangeRate?>>()
    val rates: LiveData<State<ExchangeRate?>> = _rates

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
    }

    private fun getRates() {
        disposable.add(
            exchangeRepository.getExchangeRates(getMainCurrency())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { _rates.value = State.Loading }
                .subscribe({
                    _rates.value = State.Success(it)
                }, {
                    _rates.value = State.Error(it)
                })
        )
    }

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }
}