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
import com.cristiancizmar.exchangerates.util.HOUR_FORMAT
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
        viewModel.loading.observe(viewLifecycleOwner) { loading ->
            binding.loadingPb.visibility = if (loading) View.VISIBLE else View.GONE
        }
        viewModel.rates.observe(viewLifecycleOwner) { result ->
            result?.let { exchangeRate ->
                updateRates(exchangeRate.rates)
                updateText(exchangeRate.date)
            }
        }
    }

    private fun updateRates(rates: Map<String, Float>) {
        val newData = rates.toList().map {
            Rate(
                "${viewModel.getMainCurrency()} / ${it.first}",
                it.second
            )
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
                deviceDate,
                viewModel.getMainCurrency()
            )
    }
}