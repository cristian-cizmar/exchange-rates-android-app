package com.cristiancizmar.exchangerates.ui.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.cristiancizmar.exchangerates.R
import com.cristiancizmar.exchangerates.databinding.FragmentHistoryBinding
import com.cristiancizmar.exchangerates.util.EUR
import com.cristiancizmar.exchangerates.util.GBP
import com.cristiancizmar.exchangerates.util.USD
import com.cristiancizmar.exchangerates.util.viewBinding
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HistoryFragment : Fragment() {

    private val binding by viewBinding(FragmentHistoryBinding::bind)
    private val viewModel: HistoryViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_history, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.chartTitleEur.text =
            String.format(getString(R.string.concat_slash), viewModel.getBaseCurrency(), EUR)
        binding.chartTitleUsd.text =
            String.format(getString(R.string.concat_slash), viewModel.getBaseCurrency(), USD)
        binding.chartTitleGbp.text =
            String.format(getString(R.string.concat_slash), viewModel.getBaseCurrency(), GBP)

        initChart(binding.chartEur)
        initChart(binding.chartUsd)
        initChart(binding.chartGbp)

        observeChartData()
        viewModel.generateHistoricRequests()
    }

    private fun initChart(chart: LineChart) {
        with(chart) {
            legend.isEnabled = false
            axisRight.isEnabled = false
            description.isEnabled = false
            invalidate()
        }
    }

    private fun observeChartData() {
        viewModel.historicDaysLiveData.observe(viewLifecycleOwner) { data ->
            val ronArray: ArrayList<Entry> = ArrayList()
            val usdArray: ArrayList<Entry> = ArrayList()
            val bgnArray: ArrayList<Entry> = ArrayList()
            for (i in data.indices) {
                val valEur = data[i][EUR]
                if (valEur != null && valEur != 0f) {
                    ronArray.add(Entry(i.toFloat(), valEur.toFloat()))
                }
                val valUsd = data[i][USD]
                if (valUsd != null && valUsd != 0f) {
                    usdArray.add(Entry(i.toFloat(), valUsd.toFloat()))
                }
                val valGbp = data[i][GBP]
                if (valGbp != null && valGbp != 0f) {
                    bgnArray.add(Entry(i.toFloat(), valGbp.toFloat()))
                }
            }
            setDataToChart(binding.chartEur, ronArray)
            setDataToChart(binding.chartUsd, usdArray)
            setDataToChart(binding.chartGbp, bgnArray)
        }
    }

    private fun setDataToChart(chart: LineChart, dataArray: ArrayList<Entry>) {
        val lineDataSet = LineDataSet(dataArray, "")
        val data = LineData(lineDataSet)
        data.setDrawValues(false)
        chart.data = data
        chart.invalidate()
    }
}