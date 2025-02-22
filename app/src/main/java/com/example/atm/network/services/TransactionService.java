package com.example.atm.network.services;

import com.example.atm.data.models.Transaction;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import androidx.room.Query;
import java.util.List;


public interface TransactionService {

    @POST("classes/transaction")
    Call<Transaction> insertTransaction(@Body Transaction transaction);


    @Query("SELECT * FROM Transaction ORDER BY expirationDate DESC")
    List<Transaction> getAllTransactions();
}
