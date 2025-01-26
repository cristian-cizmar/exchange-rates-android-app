package com.cristiancizmar.exchangerates.ui.settings

import com.cristiancizmar.exchangerates.BaseViewModelTest
import com.cristiancizmar.exchangerates.data.ExchangeRepository
import com.cristiancizmar.exchangerates.data.PreferencesRepository
import com.cristiancizmar.exchangerates.util.State
import io.reactivex.Single
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class SettingsViewModelTest : BaseViewModelTest() {

    private var exchangeRepository: ExchangeRepository = mock()
    private var preferencesRepository: PreferencesRepository = mock()
    private lateinit var viewModel: SettingsViewModel

    @Before
    fun setUp() {
        viewModel = SettingsViewModel(exchangeRepository, preferencesRepository)
    }

    @Test
    fun getSavedRefreshRate_updatesRefreshRate() {
        val refreshRate = 100
        Mockito.`when`(preferencesRepository.getRefreshRate()).thenReturn(refreshRate)

        viewModel.getSavedRefreshRate()

        Assert.assertEquals(refreshRate, viewModel.refreshRate.value)
    }

    @Test
    fun setSavedRefreshRate_updatesRefreshRate() {
        val refreshRate = 100

        viewModel.setSavedRefreshRate(refreshRate)

        verify(preferencesRepository).saveRefreshRate(refreshRate)
    }

    @Test
    fun getSavedCurrency_updatesMainCurrency() {
        val currency = "USD"
        Mockito.`when`(preferencesRepository.getMainCurrency()).thenReturn(currency)

        viewModel.getSavedCurrency()

        Assert.assertEquals(currency, viewModel.mainCurrency.value)
    }

    @Test
    fun setSavedCurrency_updatesMainCurrency() {
        val currency = "USD"

        viewModel.setSavedCurrency(currency)

        verify(preferencesRepository).saveMainCurrency(currency)
    }

    @Test
    fun getCurrencies_updatesCurrencies() {
        val short = "USD"
        val long = "United States dollar"
        val currencies = Single.just(mapOf(short to long))
        Mockito.`when`(exchangeRepository.getCurrencies()).thenReturn(currencies)

        viewModel.getCurrencies()

        val expected = listOf(Pair(short, long))
        Assert.assertTrue(viewModel.currencies.value is State.Success<*>)
        Assert.assertEquals(expected, (viewModel.currencies.value as State.Success).data)
    }
}