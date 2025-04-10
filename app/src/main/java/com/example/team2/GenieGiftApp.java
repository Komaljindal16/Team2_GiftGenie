package com.example.team2;

import android.app.Application;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class GenieGiftApp extends Application {
    private static GiftApi giftApi;

    @Override
    public void onCreate() {
        super.onCreate();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://jsonplaceholder.typicode.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        giftApi = retrofit.create(GiftApi.class);
    }

    public static GiftApi getGiftApi() {
        return giftApi;
    }
}