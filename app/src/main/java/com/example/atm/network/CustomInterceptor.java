package com.example.atm.network;

import androidx.annotation.NonNull;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class CustomInterceptor implements Interceptor {

    @NonNull
    @Override
    public Response intercept(@NonNull CustomInterceptor.Chain chain) throws IOException {
        Request original = chain.request();
        Request newRequest = original.newBuilder()
                .header("X-Parse-Application-Id", "w5uVgjGLDxMx5pJrURKllakyTNJFk9gu1Y6zJmiV")
                .header("X-Parse-REST-API-Key", "30qfUFUNojNx6WKpydIEnthGKii5CH2bF78WnNpf")
                .build();
        return chain.proceed(newRequest);
    }
}
