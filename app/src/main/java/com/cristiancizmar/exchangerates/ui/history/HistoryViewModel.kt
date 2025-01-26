package com.cristiancizmar.exchangerates.ui.history

import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cristiancizmar.exchangerates.data.ExchangeRepository
import com.cristiancizmar.exchangerates.data.PreferencesRepository
import com.cristiancizmar.exchangerates.util.EUR
import com.cristiancizmar.exchangerates.util.GBP
import com.cristiancizmar.exchangerates.util.State
import com.cristiancizmar.exchangerates.util.USD
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.util.Locale
import javax.inject.Inject

private const val DAYS_IN_HISTORY = 10

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val exchangeRepository: ExchangeRepository,
    private val preferencesRepository: PreferencesRepository
) : ViewModel() {

    private val _historicDaysLiveData: MutableLiveData<State<MutableList<Map<String, Float>>>> =
        MutableLiveData(initData())
    val historicDaysLiveData: LiveData<State<MutableList<Map<String, Float>>>> =
        _historicDaysLiveData

    private val disposable = CompositeDisposable()

    fun getBaseCurrency() = preferencesRepository.getMainCurrency()

    /**
     * Makes multiple API requests to get the exchange rates from the most recent days.
     * The API doesn't allow getting data from multiple days using a single request
     */
    fun generateHistoricRequests() {
        val cal = Calendar.getInstance()
        repeat(DAYS_IN_HISTORY) { repeatCount ->
            cal.add(Calendar.DAY_OF_MONTH, -1)
            val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val dateString = format.format(cal.time)
            getHistoricRate(dateString, DAYS_IN_HISTORY - 1 - repeatCount)
        }
    }

    /**
     * Makes an API request to get the exchange rates from a specific day and saves the result
     * at a specific position of a list
     */
    private fun getHistoricRate(date: String, idx: Int) {
        disposable.add(
            exchangeRepository.getHistoricRates(date, getBaseCurrency())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ result ->
                    if (_historicDaysLiveData.value !is State.Success) {
                        _historicDaysLiveData.value = initData()
                    }
                    _historicDaysLiveData.value =
                        (_historicDaysLiveData.value as? State.Success).apply {
                            this?.data?.set(idx, result.rates)
                        }
                }, {
                    _historicDaysLiveData.value = State.Error(it)
                })
        )
    }

    private fun initData() =
        State.Success(MutableList(DAYS_IN_HISTORY) { mapOf(EUR to 0f, USD to 0f, GBP to 0f) })
}