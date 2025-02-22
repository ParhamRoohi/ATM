package com.example.atm.data.db.runnables.user;

import android.content.Context;

import com.example.atm.ResultListener;
import com.example.atm.data.db.DbManager;
import com.example.atm.data.models.User;

public class UpdateUserBalanceRunnable implements Runnable{

    private final Context context;
    private final User user;
    private final ResultListener<User> resultListener;

    public UpdateUserBalanceRunnable(Context context, User user, ResultListener<User> resultListener) {
        this.context = context;
        this.user = user;
        this.resultListener = resultListener;
    }

    @Override
    public void run() {
        DbManager dbManager = DbManager.getInstance(context);
        long count = dbManager.userDao().update(user);

        if (count > 0) {
            resultListener.onSuccess(user);
        } else {
            resultListener.onError(new Error("Update Failed"));
        }
    }
}
