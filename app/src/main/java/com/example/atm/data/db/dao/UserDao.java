package com.example.atm.data.db.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.atm.data.models.User;

import java.util.List;
@Dao
public interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertUser(User user);

    @Query("SELECT * FROM User WHERE username = :username AND password = :password")
    List<User> getUserBasedOnCredentials(String username, String password);
}
