package com.example.rapiertech.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private static final String IP = "http://192.168.1.66/";
    private static final String BASE_URL = IP + "rapiertech/public/api/";
    private static final String STORAGE_URL = IP + "rapiertech/public/storage/";

    private static Retrofit retrofit;

    public static Retrofit getClient() {
        if (retrofit == null){
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    public static String getStorage() {
        return STORAGE_URL;
    }
}
