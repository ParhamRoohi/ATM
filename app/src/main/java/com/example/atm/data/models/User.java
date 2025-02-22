package com.example.atm.data.models;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@Entity
public class User implements Serializable, Comparable<User>{
    @PrimaryKey
    @SerializedName("objectId")
    @NonNull
    private String id;
    private String username;
    private String password;
    private int age;
    private String phoneNumber;
    private String accountNumber;
    private String cardNumber;
    private String cvv2;
    private Date expirationDate;
    private Long currentBalance;
    @ColumnInfo(name = "session_token")
    private String sessionToken;

    public User(@NonNull String id, String username, String password, int age, String phoneNumber,String accountNumber,String cardNumber,String cvv2,Date expirationDate,Long currentBalance) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.age = age;
        this.cvv2 = cvv2;
        this.phoneNumber = phoneNumber;
        this.accountNumber = accountNumber;
        this.cardNumber = cardNumber;
        this.expirationDate = expirationDate;
        this.currentBalance = currentBalance;
    }

    @Ignore
    public User(String username, String password, int age, String phoneNumber,String accountNumber,String cardNumber,String cvv2,Date expirationDate,Long currentBalance) {
        this.username = username;
        this.password = password;
        this.age = age;
        this.cvv2 = cvv2;
        this.phoneNumber = phoneNumber;
        this.accountNumber = accountNumber;
        this.cardNumber = cardNumber;
        this.expirationDate = expirationDate;
        this.currentBalance = currentBalance;
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

    public @NonNull String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    public String getSessionToken() {
        return sessionToken;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }
    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }
    public String getCvv2() {
        return cvv2;
    }

    public void setCvv2(String cvv2) {
        this.cvv2 = cvv2;
    }
    public Date getExpirationDate() {
        return expirationDate;
    }
    @SuppressLint("SimpleDateFormat")
    public String getFormattedModifiedDate() {
        return new SimpleDateFormat("yy/MM/dd").format(expirationDate);
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }

    public Long getCurrentBalance() {
        return currentBalance;
    }

    public void setCurrentBalance(Long currentBalance) {
        this.currentBalance = currentBalance;
    }


    public void setSessionToken(String sessionToken) {
        this.sessionToken = sessionToken;
    }

    @Override
    public int compareTo(User o) {
        return o.expirationDate.compareTo(this.expirationDate);
    }

    @NonNull
    @Override
    public String toString() {
        return   "username= " + username
                + '\n' + "age= " + age;
    }


}

