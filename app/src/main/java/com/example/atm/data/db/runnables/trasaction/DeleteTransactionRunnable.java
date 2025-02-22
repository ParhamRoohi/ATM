package com.example.atm.data.db.runnables.trasaction;

import android.content.Context;

import com.example.atm.ResultListener;
import com.example.atm.data.db.DbManager;
import com.example.atm.data.models.Transaction;

public class DeleteTransactionRunnable implements Runnable{

    private final Context context;
    private final Transaction transaction;
    private final ResultListener<Transaction> dbResponse;

    public DeleteTransactionRunnable(Context context, Transaction transaction, ResultListener<Transaction> dbResponse) {
        this.context = context;
        this.transaction = transaction;
        this.dbResponse = dbResponse;
    }

    @Override
    public void run() {
        DbManager dbManager = DbManager.getInstance(context);
        int count = dbManager.transactionDao().delete(transaction);

        if (count > 0) {
            dbResponse.onSuccess(transaction);
        } else {
            dbResponse.onError(new Error("Insert Failed"));
        }
    }

}
