package com.example.atm.data.db.runnables.user;

import android.content.Context;

import com.example.atm.ResultListener;
import com.example.atm.data.db.DbManager;
import com.example.atm.data.models.User;

public class GetUsernameByIdRunnable implements Runnable{
    private final Context context;
    private final String userId;
    private final ResultListener<User> dbResponse;

    public GetUsernameByIdRunnable(Context context, String userId, ResultListener<User> dbResponse) {
        this.context = context;
        this.userId = userId;
        this.dbResponse = dbResponse;
    }

    @Override
    public void run() {
        DbManager dbManager = DbManager.getInstance(context);
        User user = dbManager.userDao().getUserById(userId);

        if (user == null) {
            dbResponse.onError(new Error("User not found"));
        } else {
            dbResponse.onSuccess(user);
        }
    }
}
