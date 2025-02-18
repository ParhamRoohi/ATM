package com.example.atm.data.models;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;

public class Transaction implements Serializable {

//    @Entity(
//            foreignKeys = @ForeignKey(
//                    entity = User.class,
//                    parentColumns = "id",
//                    childColumns = "publisherId",
//                    onDelete = ForeignKey.CASCADE
//            ),
//            indices = {@Index(value = "publisherId")}
//    )


    @PrimaryKey()
    @SerializedName("objectId")
    @NonNull
    private String id;
    private String phoneNumber;
    private String accountNumber;

    private String userId;
    private String cardNumber;
    private String cvv2;

    private String expirationDate;
    private Double currentBalance;
    @ColumnInfo(name = "session_token")
    private String sessionToken;


    public Transaction(@NonNull String id, String phoneNumber, String accountNumber, String cardNumber, String userId, String cvv2, String expirationDate, Double currentBalance, String sessionToken) {
        this.id = id;
        this.phoneNumber = phoneNumber;
        this.accountNumber = accountNumber;
        this.cardNumber = cardNumber;
        this.userId = userId;
        this.cvv2 = cvv2;
        this.expirationDate = expirationDate;
        this.currentBalance = currentBalance;
        this.sessionToken = sessionToken;

    }

    @Ignore
    public Transaction(String phoneNumber, String accountNumber, String cardNumber, String userId, String cvv2, String expirationDate, Double currentBalance, String sessionToken) {
        this.phoneNumber = phoneNumber;
        this.accountNumber = accountNumber;
        this.cardNumber = cardNumber;
        this.userId = userId;
        this.cvv2 = cvv2;
        this.expirationDate = expirationDate;
        this.currentBalance = currentBalance;
        this.sessionToken = sessionToken;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
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

    public String getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(String expirationDate) {
        this.expirationDate = expirationDate;
    }

    public Double getCurrentBalance() {
        return currentBalance;
    }

    public void setCurrentBalance(Double currentBalance) {
        this.currentBalance = currentBalance;
    }

    public String getSessionToken() {
        return sessionToken;
    }

    public void setSessionToken(String sessionToken) {
        this.sessionToken = sessionToken;
    }

//    @Override
//    public int compareTo(Transaction transaction) {
//        return 0;
//    }

    //    @SuppressLint("SimpleDateFormat")
//    public String getFormattedModifiedDate() {
//        return new SimpleDateFormat("yy/MM/dd").format(modifiedDate);
//    }

//    public int getCategoryColor() {
//        switch (category) {
//            case CategoryConstants.NEWS:
//                return R.color.teal_700;
//            case CategoryConstants.CELEBS:
//                return R.color.blue_500;
//            case CategoryConstants.MOVIES:
//                return R.color.purple_700;
//            case CategoryConstants.EVENTS:
//                return R.color.orange_700;
//            case CategoryConstants.SHOWS:
//                return R.color.red_600;
//        }
//        return 0;
//    }

//    public void setModifiedDate(Date modifiedDate) {
//        this.modifiedDate = modifiedDate;
//    }

//    private Date getDate() {
//        Calendar calendar = Calendar.getInstance();
//        return calendar.getTime();
//    }

//    @Override
//    public int compareTo(Transaction o) {
//        return o.modifiedDate.compareTo(this.modifiedDate);
//    }

//    @NonNull
//    @Override
//    public String toString() {
//        return "Title: " + title +
//                "\nContent: " + content +
//                "\nCategory: " + category +
//                "\nModified Date: " + getFormattedModifiedDate();
//    }
}
