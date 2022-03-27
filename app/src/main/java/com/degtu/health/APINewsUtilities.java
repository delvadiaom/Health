package com.degtu.health;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class APINewsUtilities {

    private static Retrofit retrofit = null;

    public static APINews getAPINewsInterface(){
        if (retrofit == null){
            retrofit = new Retrofit.Builder().baseUrl(APINews.BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
        }
        return retrofit.create(APINews.class);
    }
}
