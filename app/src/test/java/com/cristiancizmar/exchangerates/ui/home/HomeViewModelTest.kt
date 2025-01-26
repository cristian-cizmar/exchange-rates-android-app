package com.cristiancizmar.exchangerates.ui.home

import com.cristiancizmar.exchangerates.BaseViewModelTest
import com.cristiancizmar.exchangerates.data.ExchangeRepository
import com.cristiancizmar.exchangerates.data.PreferencesRepository
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.mockito.junit.MockitoJUnitRunner
import org.powermock.reflect.Whitebox

@RunWith(MockitoJUnitRunner::class)
class HomeViewModelTest : BaseViewModelTest() {

    private var exchangeRepository: ExchangeRepository = mock()
    private var preferencesRepository: PreferencesRepository = mock()
    private lateinit var viewModel: HomeViewModel

    @Before
    fun setUp() {
        viewModel = HomeViewModel(exchangeRepository, preferencesRepository)
    }

    @Test
    fun updatePreferences_updatesRefreshRate() {
        val refreshRate = 100
        Mockito.`when`(preferencesRepository.getRefreshRate()).thenReturn(refreshRate)

        viewModel.updatePreferences()

        val result = Whitebox.getInternalState<Int>(viewModel, "refreshRate")
        Assert.assertEquals(refreshRate, result)
    }

    @Test
    fun getMainCurrency_returnCurrency() {
        val currency = "USD"
        Mockito.`when`(preferencesRepository.getMainCurrency()).thenReturn(currency)

        val result = viewModel.getMainCurrency()

        Assert.assertEquals(currency, result)
    }
}