package com.example.atm.network.services;

import com.example.atm.data.models.Transaction;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.POST;
import retrofit2.http.Path;

import androidx.room.Query;
import java.util.List;


public interface TransactionService {

    @POST("classes/transaction")
    Call<Transaction> insertTransaction(@Body Transaction transaction);


    @DELETE("/classes/transaction/{id}")
    Call<Transaction> deleteTransaction(@Path("id") String id);
}
