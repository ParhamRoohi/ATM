package com.example.atm.network.services;

import com.example.atm.data.models.Transaction;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface TransactionService {

    @POST("classes/transaction")
    Call<Transaction> insertTransaction(@Body Transaction transaction);
}
