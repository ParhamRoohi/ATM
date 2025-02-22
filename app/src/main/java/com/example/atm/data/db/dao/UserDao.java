package com.example.atm.data.db.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.atm.data.models.User;

import java.util.List;
@Dao
public interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertUser(User user);

    @Query("SELECT * FROM User WHERE username = :username AND password = :password")
    List<User> getUserBasedOnCredentials(String username, String password);

    @Query("SELECT session_token FROM User WHERE id = :userId")
    String getSessionTokenById(String userId);

    @Query("SELECT * FROM User WHERE id = :userId")
    User getUserById(String userId);

    @Update
    int update(User user);
}
