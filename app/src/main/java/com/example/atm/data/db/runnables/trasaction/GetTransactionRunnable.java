package com.example.atm.data.db.runnables.trasaction;

import android.content.Context;

import com.example.atm.R;
import com.example.atm.ResultListener;
import com.example.atm.data.db.DbManager;
import com.example.atm.data.models.Transaction;

import java.util.List;

public class GetTransactionRunnable implements Runnable {
    private final Context context;
    private final ResultListener<List<Transaction>> dbResponse;

    public GetTransactionRunnable(Context context, ResultListener<List<Transaction>> dbResponse) {
        this.context = context;
        this.dbResponse = dbResponse;
    }

    @Override
    public void run() {
        DbManager dbManager = DbManager.getInstance(context);
        List<Transaction> transactions = dbManager.transactionDao().getAllTransactions();
        if (transactions == null) {
            dbResponse.onError(new Error("Something went wrong"));
        } else {
            dbResponse.onSuccess(transactions);
        }
    }
}
