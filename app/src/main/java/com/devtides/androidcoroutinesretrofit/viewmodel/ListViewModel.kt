package com.devtides.androidcoroutinesretrofit.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.devtides.androidcoroutinesretrofit.api.CountriesService
import com.devtides.androidcoroutinesretrofit.model.Country
import kotlinx.coroutines.*
import retrofit2.HttpException

class ListViewModel: ViewModel() {

    //For Api Call
    val countriesService=CountriesService.getCountriesService()
    var job: Job?=null

    //Exception handling
    //Creating exception handler in advance
    val exceptionHandler= CoroutineExceptionHandler{
        coroutineContext, throwable ->
        onError("Exception :${throwable.localizedMessage}")
    }


    //Live data handling
    val countries = MutableLiveData<List<Country>>()
    val countryLoadError = MutableLiveData<String?>()
    val loading = MutableLiveData<Boolean>()

    fun refresh() {
        fetchCountries()
    }

    private fun fetchCountries() {
        loading.value = true

        job=CoroutineScope(Dispatchers.IO +exceptionHandler).launch {
            val response=countriesService.getCountries()
            withContext(Dispatchers.Main){
                if(response.isSuccessful){
                    countries.value=response.body()
                    countryLoadError.value=""
                    loading.value=false
                }
                else{
                    onError("Error :${response.message()}")
                }
            }

        }
        /*val dummyData = generateDummyCountries()
        countries.value = dummyData
        countryLoadError.value = ""
        loading.value = false*/
    }

    private fun generateDummyCountries(): List<Country> {
        val countries = arrayListOf<Country>()
        countries.add(Country("dummyCountry1",  "dummyCapital1",""))
        countries.add(Country("dummyCountry2",  "dummyCapital2",""))
        countries.add(Country("dummyCountry3",  "dummyCapital3",""))
        countries.add(Country("dummyCountry4",  "dummyCapital4",""))
        countries.add(Country("dummyCountry5",  "dummyCapital5",""))
        return countries
    }

    private fun onError(message: String) {
        countryLoadError.value = message
        loading.value = false
    }

    override fun onCleared() {
        super.onCleared()
        job?.cancel()
    }

}