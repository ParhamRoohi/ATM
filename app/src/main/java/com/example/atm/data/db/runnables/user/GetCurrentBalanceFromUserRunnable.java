package com.example.atm.data.db.runnables.user;

import android.content.Context;

import com.example.atm.ResultListener;
import com.example.atm.data.db.DbManager;

import java.util.List;

public class GetCurrentBalanceFromUserRunnable implements Runnable{
    private final Context context;
    String userName;
    private final ResultListener<Double> resultListener;
    public GetCurrentBalanceFromUserRunnable(Context context,String userName,
                                             ResultListener<Double> resultListener) {
        this.context = context;
        this.userName = userName;
        this.resultListener = resultListener;
    }

    @Override
    public void run() {
        DbManager dbManager = DbManager.getInstance(context);
        Double currentBalance = dbManager.userDao().getCurrentBalanceByUsername(userName);

        if (currentBalance==0) {
            resultListener.onError(new Error("Something went wrong"));
        } else {
            resultListener.onSuccess(currentBalance);
        }
    }
}
