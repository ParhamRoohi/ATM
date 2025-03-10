package com.example.atm.network.impl;

import static com.example.atm.preferences.PreferencesManager.PREF_KEY_CURRENT_USER;
import static com.example.atm.preferences.PreferencesManager.PREF_KEY_TOKEN;
import static com.example.atm.preferences.PreferencesManager.preferencesManager;

import android.content.Context;

import androidx.annotation.NonNull;

import com.example.atm.R;
import com.example.atm.ResultListener;
import com.example.atm.data.db.runnables.user.GetSessionTokenRunnable;
import com.example.atm.data.models.ServerError;
import com.example.atm.data.models.User;
import com.example.atm.network.NetworkHelper;
import com.example.atm.network.services.UserService;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class UserServiceImpl {

    private final UserService userService;

    private final NetworkHelper networkHelper;
    private Context context;


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
                    preferencesManager.put(PREF_KEY_TOKEN, received.getSessionToken());
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

    public void updateUserBalance(User user, String userId, ResultListener<User> resultListener) {
        String sessionToken = preferencesManager.get(PREF_KEY_TOKEN, "");
        if (sessionToken == null) {
            resultListener.onError(new Error("Session token is null"));
            return;
        }
        Call<User> call = userService.updateUserBalance(sessionToken, user, userId);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                if (response.isSuccessful()) {
                    User updatedUser = response.body();
                    if (updatedUser == null) {
                        networkHelper.showNetworkError(resultListener, R.string.network_general_error);
                        return;
                    }
                    updatedUser.setCurrentBalance(user.getCurrentBalance());
                    resultListener.onSuccess(updatedUser);
                    return;
                }

                if (response.code() == 209) {
                    resultListener.onError(new Error("Session token expired. Please log in again."));
                } else {
                    ServerError serverError = networkHelper.convertResponseToError(response, ServerError.class);
                    if (serverError == null) {
                        networkHelper.showNetworkError(resultListener, R.string.network_json_error);
                        return;
                    }
                    resultListener.onError(new Error(serverError.getError()));
                }
            }

            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable throwable) {
                resultListener.onError(throwable);
            }
        });
    }

    public void logout(String userId, ResultListener<Void> resultListener) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        User user = preferencesManager.getObj(PREF_KEY_CURRENT_USER, User.class);
        executorService.execute(new GetSessionTokenRunnable(context, userId, new ResultListener<String>() {
            @Override
            public void onSuccess(String sessionToken) {
                Call<Void> call = userService.logout(user.getUsername(), sessionToken);
                call.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                        if (response.isSuccessful()) {
                            resultListener.onSuccess(null);
                        } else {
                            ServerError serverError = networkHelper.convertResponseToError(response, ServerError.class);
                            if (serverError == null) {
                                resultListener.onError(new Error("Logout failed"));
                            } else {
                                resultListener.onError(new Error(serverError.getError()));
                            }
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<Void> call, @NonNull Throwable throwable) {
                        resultListener.onError(throwable);
                    }
                });
            }

            @Override
            public void onError(Throwable throwable) {
                resultListener.onError(throwable);
            }
        }));
    }
}