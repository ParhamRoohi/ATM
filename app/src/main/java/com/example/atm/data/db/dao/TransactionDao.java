package com.example.atm.data.db.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.atm.data.models.Transaction;

import java.util.List;

@Dao
public interface TransactionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertTransaction(Transaction transaction);

    @Delete
    int delete(Transaction transaction);

    @Query("SELECT * FROM`Transaction`ORDER BY expirationDate DESC")
    List<Transaction> getAllTransactions();

    @Query("SELECT * FROM `Transaction` WHERE userId = :userId ORDER BY expirationDate DESC")
    List<Transaction> getReportsBasedOnUserId(String userId);


}


