package com.cristiancizmar.exchangerates.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.cristiancizmar.exchangerates.R
import com.cristiancizmar.exchangerates.databinding.FragmentSettingsBinding
import com.cristiancizmar.exchangerates.util.REFRESH_15
import com.cristiancizmar.exchangerates.util.REFRESH_3
import com.cristiancizmar.exchangerates.util.REFRESH_5
import com.cristiancizmar.exchangerates.util.indexOfFirstDefault0
import com.cristiancizmar.exchangerates.util.viewBinding
import com.google.android.material.navigation.NavigationBarView.OnItemSelectedListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsFragment : Fragment() {

    private val binding by viewBinding(FragmentSettingsBinding::bind)
    private val viewModel: SettingsViewModel by viewModels()
    private val refreshRateOptions = listOf(REFRESH_3, REFRESH_5, REFRESH_15).map { it.toString() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getSavedRefreshRate()
        viewModel.getSavedCurrency()
        viewModel.getCurrencies()

        initObservers()
    }

    override fun onResume() {
        super.onResume()
        initRefreshRateSpinner()
        initCurrencySpinner()
    }

    private fun initObservers() {
        viewModel.refreshRate.observe(viewLifecycleOwner) { refreshRate ->
            binding.currencySpinner.setSelection(refreshRateOptions.indexOfFirstDefault0(refreshRate.toString()))
        }
        viewModel.mainCurrency.observe(viewLifecycleOwner) { mainCurrency ->
            binding.currencySpinner.setSelection(
                viewModel.currencies.value
                    ?.map { it.first }
                    ?.indexOfFirstDefault0(mainCurrency) ?: 0
            )
        }
    }

    private fun initRefreshRateSpinner() {
        val initialItem =
            refreshRateOptions.indexOfFirstDefault0(viewModel.refreshRate.value.toString())
        setupSpinner(
            spinner = binding.refreshRateSpinner,
            items = refreshRateOptions,
            onClick = { s ->
                viewModel.setSavedRefreshRate(s.toIntOrNull() ?: refreshRateOptions.first().toInt())
            },
            initialItem = initialItem
        )
    }

    private fun initCurrencySpinner() {
        viewModel.currencies.observe(viewLifecycleOwner) { list ->
            if (list == null) return@observe
            val initialItem = viewModel.currencies.value?.map { it.first }
                ?.indexOfFirstDefault0(viewModel.mainCurrency.value) ?: 0
            setupSpinner(
                spinner = binding.currencySpinner,
                items = list.map { "${it.first}: ${it.second}" },
                onClick = { currency -> viewModel.setSavedCurrency(currency) },
                initialItem = initialItem
            )
        }
    }

    private fun setupSpinner(
        spinner: Spinner,
        items: List<String>,
        onClick: (String) -> Unit,
        initialItem: Int
    ) {
        val adapter = ArrayAdapter(requireContext(), R.layout.dropdown_item_main, items)
        adapter.setDropDownViewResource(R.layout.dropdown_item_in_list)
        spinner.adapter = adapter
        spinner.setSelection(initialItem)
        spinner.onItemSelectedListener =
            object : OnItemSelectedListener, AdapterView.OnItemSelectedListener {
                override fun onNavigationItemSelected(item: MenuItem): Boolean {
                    return true
                }

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    onClick(items[position])
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
    }
}