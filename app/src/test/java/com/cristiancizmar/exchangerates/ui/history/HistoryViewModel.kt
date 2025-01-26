package com.cristiancizmar.exchangerates.ui.history

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

@RunWith(MockitoJUnitRunner::class)
class HistoryViewModelTest : BaseViewModelTest() {

    private var exchangeRepository: ExchangeRepository = mock()
    private var preferencesRepository: PreferencesRepository = mock()
    private lateinit var viewModel: HistoryViewModel

    @Before
    fun setUp() {
        viewModel = HistoryViewModel(exchangeRepository, preferencesRepository)
    }

    @Test
    fun getBaseCurrency_returnCurrency() {
        val currency = "USD"
        Mockito.`when`(preferencesRepository.getMainCurrency()).thenReturn(currency)

        val result = viewModel.getBaseCurrency()

        Assert.assertEquals(currency, result)
    }
}