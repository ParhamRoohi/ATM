package com.example.atm.data.db.runnables.trasaction;

import android.content.Context;

import com.example.atm.ResultListener;
import com.example.atm.data.db.DbManager;
import com.example.atm.data.models.Transaction;
import com.example.atm.data.models.User;

public class InsertTransactionRunnable implements Runnable{
    private final Context context;
    private final Transaction transaction;
    private final ResultListener<Transaction> resultListener;

    public InsertTransactionRunnable(Context context, Transaction transaction, ResultListener<Transaction> resultListener) {
        this.context = context;
        this.transaction = transaction;
        this.resultListener = resultListener;
    }

    @Override
    public void run() {
        DbManager dbManager = DbManager.getInstance(context);
        long id = dbManager.transactionDao().insertTransaction(transaction);

        if (id > 0) {
//            user.setId(id);
            resultListener.onSuccess(transaction);
        } else {
            resultListener.onError(new Error("Transaction insert Failed"));
        }
    }
}
