package com.example.atm.network.impl;

import androidx.annotation.NonNull;

import com.example.atm.R;
import com.example.atm.ResultListener;
import com.example.atm.data.models.User;
import com.example.atm.network.NetworkHelper;
import com.example.atm.network.services.UserService;
import com.example.atm.data.models.ServerError;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class UserServiceImpl {

    private final UserService userService;

    private final NetworkHelper networkHelper;

    public UserServiceImpl() {
        networkHelper = new NetworkHelper();
        Retrofit retrofit = networkHelper.buildRetrofit();
        this.userService = retrofit.create(UserService.class);
    }
    public void signupUser(User user, ResultListener<User> resultListener) {
        Call<User> call = userService.signupUser(user);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                if (response.isSuccessful()) {
                    User received = response.body();
                    if (received == null) {
                        resultListener.onError(new Error("Response body is null"));
                        return;
                    }
                    received.setUsername(user.getUsername());
                    received.setPassword(user.getPassword());
                    resultListener.onSuccess(received);
                    return;
                }
                ServerError serverError = networkHelper.convertResponseToError(response, ServerError.class);
                if (serverError == null) {
                    resultListener.onError(new Error("Response body is null"));
                    return;
                }
                resultListener.onError(new Error(serverError.getError()));
            }
            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable throwable) {
                resultListener.onError(throwable);
            }
        });
    }

    public void loginUser(User user, ResultListener<User> resultListener) {

        Call<User> call = userService.loginUser(user.getUsername(), user.getPassword());
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                if (response.isSuccessful()) {
                    User received = response.body();
                    if (received == null) {
                        networkHelper.showNetworkError(resultListener, R.string.network_general_error);
                        return;
                    }
                    received.setPassword(user.getPassword());
                    received.setUsername(user.getUsername());
                    received.setAge(user.getAge());
                    received.setPhoneNumber(user.getPhoneNumber());
                    resultListener.onSuccess(received);
                    return;
                }
                ServerError serverError = networkHelper.convertResponseToError(response, ServerError.class);
                if (serverError == null) {
                    networkHelper.showNetworkError(resultListener, R.string.network_json_error);
                    return;
                }
                resultListener.onError(new Error(serverError.getError()));
            }

            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable throwable) {
                networkHelper.showNetworkError(resultListener, R.string.network_general_error);
            }
        });
    }

}
