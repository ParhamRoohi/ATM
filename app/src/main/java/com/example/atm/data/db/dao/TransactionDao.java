package com.example.atm.data.db.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;

import com.example.atm.data.models.Transaction;
import com.example.atm.data.models.User;
@Dao
public interface TransactionDao  {


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertTransaction(Transaction transaction);
}
