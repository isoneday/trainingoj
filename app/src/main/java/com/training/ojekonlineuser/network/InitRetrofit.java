package com.training.ojekonlineuser.network;

import com.training.ojekonlineuser.helper.MyConstants;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class InitRetrofit {


    public static Retrofit setInit(){
        return new Retrofit.Builder().baseUrl(MyConstants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public static RestApi getintance(){
        return setInit().create(RestApi.class);
    }
}
