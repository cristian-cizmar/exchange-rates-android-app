package com.cristiancizmar.exchangerates.ui.home

import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.cristiancizmar.exchangerates.R
import com.cristiancizmar.exchangerates.databinding.FragmentHomeBinding
import com.cristiancizmar.exchangerates.model.Rate
import com.cristiancizmar.exchangerates.ui.MainActivity
import com.cristiancizmar.exchangerates.util.HOUR_FORMAT
import com.cristiancizmar.exchangerates.util.State
import com.cristiancizmar.exchangerates.util.displayGeneralErrorToast
import com.cristiancizmar.exchangerates.util.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private val binding by viewBinding(FragmentHomeBinding::bind)
    private val viewModel: HomeViewModel by viewModels()
    private val ratesAdapter = RatesAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAdapter()
        initObservers()
    }

    override fun onResume() {
        super.onResume()
        viewModel.updatePreferences()
        viewModel.getRatesPeriodically()
    }

    override fun onPause() {
        super.onPause()
        viewModel.cancelOperations()
    }

    private fun initAdapter() {
        binding.ratesRv.adapter = ratesAdapter
    }

    private fun initObservers() {
        viewModel.rates.observe(viewLifecycleOwner) { result ->
            when (result) {
                State.Loading -> {
                    (activity as? MainActivity)?.setProgressBarLoading(true)
                }

                is State.Success -> {
                    (activity as? MainActivity)?.setProgressBarLoading(false)
                    result.data?.let { exchangeRate ->
                        updateRates(exchangeRate.rates)
                        updateText(exchangeRate.date)
                    }
                }

                is State.Error -> {
                    (activity as? MainActivity)?.setProgressBarLoading(false)
                    displayGeneralErrorToast(requireContext())
                }
            }

        }
    }

    private fun updateRates(rates: Map<String, Float>) {
        val newData = rates.toList().map {
            Rate(it.first, it.second)
        }
        ratesAdapter.submitList(newData)
    }

    private fun updateText(date: String) {
        val format = SimpleDateFormat(HOUR_FORMAT, Locale.getDefault())
        val deviceDate = format.format(System.currentTimeMillis())
        binding.timestampTv.text =
            String.format(
                getString(R.string.concat_string_api_device),
                date,
                deviceDate
            )
        binding.mainCurrencyTv.text = "1 ${viewModel.getMainCurrency()} ="
    }
}