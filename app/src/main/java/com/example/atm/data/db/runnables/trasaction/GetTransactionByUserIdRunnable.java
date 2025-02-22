package com.example.atm.data.db.runnables.trasaction;

import android.content.Context;

import com.example.atm.ResultListener;
import com.example.atm.data.db.DbManager;
import com.example.atm.data.models.Transaction;

import java.util.List;

public class GetTransactionByUserIdRunnable implements Runnable{
    private final Context context;
    private final String userId;
    private final ResultListener<List<Transaction>> dbResponse;

    public GetTransactionByUserIdRunnable(Context context, String userId,
                                           ResultListener<List<Transaction>> dbResponse) {
        this.context = context;
        this.userId = userId;
        this.dbResponse = dbResponse;
    }

    @Override
    public void run() {
        DbManager dbManager = DbManager.getInstance(context);
        List<Transaction> transactions = dbManager.transactionDao().getReportsBasedOnUserId(userId);

        if (transactions == null) {
            dbResponse.onError(new Error("Something went wrong"));
        } else {
            dbResponse.onSuccess(transactions);
        }
    }
}
