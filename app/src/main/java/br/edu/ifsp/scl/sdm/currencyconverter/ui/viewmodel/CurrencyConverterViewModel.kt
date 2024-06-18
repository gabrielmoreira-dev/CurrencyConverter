package br.edu.ifsp.scl.sdm.currencyconverter.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.edu.ifsp.scl.sdm.currencyconverter.model.api.CurrencyConverterApiClient
import br.edu.ifsp.scl.sdm.currencyconverter.model.livedata.CurrencyConverterLiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.HttpURLConnection

class CurrencyConverterViewModel : ViewModel() {
    fun getCurrencies() = viewModelScope.launch(Dispatchers.IO) {
        CurrencyConverterApiClient.service.getCurrencies().execute().also { response ->
            if (response.code() == HttpURLConnection.HTTP_OK) {
                response.body()?.also { currencyList ->
                    CurrencyConverterLiveData.currenciesLiveData.postValue(currencyList)
                }
            }
        }
    }

    fun convert(from: String, to: String, amount: String) = viewModelScope.launch(Dispatchers.IO) {
        CurrencyConverterApiClient.service.convert(from, to, amount).execute().also { response ->
            if (response.code() == HttpURLConnection.HTTP_OK) {
                response.body()?.let {
                    CurrencyConverterLiveData.conversionResultLiveData.postValue(it)
                }
            }
        }
    }
}