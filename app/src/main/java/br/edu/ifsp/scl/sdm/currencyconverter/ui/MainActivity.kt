package br.edu.ifsp.scl.sdm.currencyconverter.ui

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.widget.ArrayAdapter
import androidx.activity.viewModels
import br.edu.ifsp.scl.sdm.currencyconverter.R
import br.edu.ifsp.scl.sdm.currencyconverter.databinding.ActivityMainBinding
import br.edu.ifsp.scl.sdm.currencyconverter.model.livedata.CurrencyConverterLiveData
import br.edu.ifsp.scl.sdm.currencyconverter.service.ConvertService
import br.edu.ifsp.scl.sdm.currencyconverter.service.CurrenciesService
import br.edu.ifsp.scl.sdm.currencyconverter.ui.viewmodel.CurrencyConverterViewModel

class MainActivity : AppCompatActivity() {
    private val amb: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private val ccvm: CurrencyConverterViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(amb.root)
        setSupportActionBar(amb.mainTb.apply { title = getString(R.string.app_name) })

        var fromQuote = ""
        var toQuote = ""
        val currenciesAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1,
            mutableListOf<String>()
        )

        with(amb) {
            fromQuoteMactv.apply {
                setAdapter(currenciesAdapter)
                setOnItemClickListener { _, _, _, _ ->
                    fromQuote = text.toString()
                }
            }
            toQuoteMactv.apply {
                setAdapter(currenciesAdapter)
                setOnItemClickListener { _, _, _, _ ->
                    toQuote = text.toString()
                }
            }
            convertBt.setOnClickListener {
                ccvm.convert(fromQuote, toQuote, amountTiet.text.toString())
            }
        }

        CurrencyConverterLiveData.currenciesLiveData.observe(this) {
            currenciesAdapter.clear()
            currenciesAdapter.addAll(it.currencies.keys.sorted())
            currenciesAdapter.getItem(0)?.also { quote ->
                amb.fromQuoteMactv.setText(quote, false)
                fromQuote = quote
            }
            currenciesAdapter.getItem(currenciesAdapter.count - 1)?.also { quote ->
                amb.toQuoteMactv.setText(quote, false)
                toQuote = quote
            }
        }

        CurrencyConverterLiveData.conversionResultLiveData.observe(this) { conversionResult ->
            with(amb) {
                resultTiet.setText(conversionResult.rates.values.first().rateForAmount)
            }
        }

        ccvm.getCurrencies()
    }
}