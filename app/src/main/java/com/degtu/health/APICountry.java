package com.degtu.health;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

interface APIinterface {

    static final String BASE_URL = "https://disease.sh/v3/covid-19/";

    @GET("countries")
    Call<List<CountryData>> getCountryData();
}
