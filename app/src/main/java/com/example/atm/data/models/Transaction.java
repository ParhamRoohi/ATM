package com.example.atm.data.models;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.example.atm.R;
import com.example.atm.utils.TypeConstant;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;
@Entity(
        foreignKeys = @ForeignKey(
                entity = User.class,
                parentColumns = "id",
                childColumns = "userId",
                onDelete = ForeignKey.CASCADE
        ),
        indices = {@Index(value = "userId")}
)
public class Transaction implements Serializable, Comparable<Transaction> {

    @PrimaryKey()
    @SerializedName("objectId")
    @NonNull
    private String id;

    private String accountNumber;
    private String userId;
    private String cardNumber;
    private String cvv2;
    private Date expirationDate;
    private Date transactionDate;

    private Long currentBalance;
    private String transactionType;


    @ColumnInfo(name = "session_token")
    private String sessionToken;


    public Transaction(@NonNull String id, String accountNumber, String cardNumber, String userId, String cvv2, Date expirationDate, Long currentBalance, String sessionToken,String transactionType,Date transactionDate) {
        this.id = id;
        this.accountNumber = accountNumber;
        this.cardNumber = cardNumber;
        this.userId = userId;
        this.cvv2 = cvv2;
        this.expirationDate = expirationDate;
        this.currentBalance = currentBalance;
        this.sessionToken = sessionToken;
        this.transactionType = transactionType;
        this.transactionDate = transactionDate;
    }

    @Ignore
    public Transaction(String accountNumber, String cardNumber, String userId, String cvv2, Date expirationDate, Long currentBalance, String sessionToken,String transactionType,Date transactionDate) {
        this.accountNumber = accountNumber;
        this.cardNumber = cardNumber;
        this.userId = userId;
        this.cvv2 = cvv2;
        this.expirationDate = expirationDate;
        this.currentBalance = currentBalance;
        this.sessionToken = sessionToken;
        this.transactionType = transactionType;
        this.transactionDate = transactionDate;
    }


    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getCvv2() {
        return cvv2;
    }

    public void setCvv2vv2(String cvv2) {
        this.cvv2 = cvv2;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getExpirationDate() {
        return expirationDate;
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

    public String getSessionToken() {
        return sessionToken;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public void setSessionToken(String sessionToken) {
        this.sessionToken = sessionToken;
    }

    public Date getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(Date transactionDate) {
        this.transactionDate = transactionDate;
    }

    @Override
    public int compareTo(Transaction transaction) {
        return 0;
    }

    public int getTypeColor() {
        switch (transactionType) {
            case TypeConstant.CTC:
                return R.color.orange_700;
            case TypeConstant.WITHDRAW:
                return R.color.blue_500;

        }
        return 0;
    }


}
