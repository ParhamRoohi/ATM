package com.example.atm.network.services;

import com.example.atm.data.models.Transaction;
import com.example.atm.data.models.User;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface UserService {
    @POST("/users")
    @Headers("X-Parse-Revocable-Session:1")
    Call<User> signupUser(@Body User user);

    @GET("/login")
    @Headers("X-Parse-Revocable-Session:1")
    Call<User> loginUser(@Query("username") String username, @Query("password") String password);

    @POST("/logout")
    Call<Void> logout(@Query("username") String username, @Query("session_token") String sessionToken);

    @PUT("/users/{id}")
    @Headers("X-Parse-Revocable-Session:1")
    Call<User> updateUserBalance(@Header("X-Parse-Session-Token") String sessionToken, @Body User user, @Path("id") String id);
}
