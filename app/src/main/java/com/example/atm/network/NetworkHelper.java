package com.example.atm.network;

import android.content.Context;

import androidx.annotation.NonNull;

import com.example.atm.ResultListener;
import com.example.atm.data.AppData;
import com.example.atm.data.models.User;
import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NetworkHelper {

    public Retrofit buildRetrofit() {
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor().setLevel(
                HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(new CustomInterceptor())
                .addInterceptor(httpLoggingInterceptor).build();
        return new Retrofit.Builder().client(client).addConverterFactory(GsonConverterFactory.create()).baseUrl("https://parseapi.back4app.com").build();
    }

    public <T> void showNetworkError(@NonNull ResultListener<T> resultListener, int resourceId) {
        Context context = AppData.getInstance().getApplicationContext();
        String error = context.getString(resourceId);
        resultListener.onError(new Error(error));
    }
    public <T, U> U convertResponseToError(retrofit2.Response<T> response, Class<U> className) {
        try (ResponseBody responseBody = response.errorBody()) {
            if (responseBody == null) {
                return null;
            }
            String responseStr = responseBody.string();
            return new Gson().fromJson(responseStr, className);
        } catch (IOException e) {
            return null;
        }
    }
}
