package com.example.chadyeo.animetv.api;


import com.example.chadyeo.animetv.BuildConfig;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;

public class RetrofitAdapter {
    private final static String BASE_URL = "https://api.gitHub.com/example";
    private final static String API_KEY = "API_KEY_HERE";

    private static RestAdapter mRestAdapter;

    public static RestAdapter getRestAdapter() {

        if (mRestAdapter == null) {
            mRestAdapter = new RestAdapter.Builder()
                    .setEndpoint(BASE_URL)
                    .setRequestInterceptor(mRequestInterceptor)
                    .build();
            if (BuildConfig.DEBUG) {
                mRestAdapter.setLogLevel(RestAdapter.LogLevel.FULL);
            }
        }
        return mRestAdapter;
    }

    private static RequestInterceptor mRequestInterceptor = new RequestInterceptor() {
        @Override
        public void intercept(RequestFacade request) {
            request.addQueryParam("api_key", API_KEY);
        }
    };
}
