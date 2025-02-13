package com.example.atm.data.models;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;
@Entity
public class User {
    @PrimaryKey
    @SerializedName("objectId")
    @NonNull
    private String id;
//    @SerializedName("username")
    private String username;
    private String password;
    private int age;
    private Number number;
    @ColumnInfo(name = "session_token")
    private String sessionToken;

    public User(@NonNull String id, String username, String password, int age, Number number) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.age = age;
        this.number = number;
    }

    @Ignore
    public User(String username, String password, int age, Number number) {
        this.username = username;
        this.password = password;
        this.age = age;
        this.number = number;
    }

    @Ignore
    public User(String username, int age) {
        this.username = username;
        this.age = age;
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSessionToken() {
        return sessionToken;
    }

    public Number getNumber() {
        return number;
    }

    public void setNumber(Number number) {
        this.number = number;
    }

    public void setSessionToken(String sessionToken) {
        this.sessionToken = sessionToken;
    }


    @NonNull
    @Override
    public String toString() {
        return   "username= " + username
                + '\n' + "age= " + age;
    }


}

