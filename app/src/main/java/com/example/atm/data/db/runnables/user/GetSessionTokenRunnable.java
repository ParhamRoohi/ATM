package com.example.atm.data.db.runnables.user;

import android.content.Context;

import com.example.atm.ResultListener;
import com.example.atm.data.db.DbManager;

public class GetSessionTokenRunnable implements Runnable {
    private final Context context;
    private final String userId;
    private final ResultListener<String> dbResponse;

    public GetSessionTokenRunnable(Context context, String userId, ResultListener<String> dbResponse) {
        this.context = context;
        this.userId = userId;
        this.dbResponse = dbResponse;
    }

    @Override
    public void run() {
        DbManager dbManager = DbManager.getInstance(context);
        String sessionToken = dbManager.userDao().getSessionTokenById(userId);

        if (sessionToken == null || sessionToken.isEmpty()) {
            dbResponse.onError(new Error("Session token not found"));
        } else {
            dbResponse.onSuccess(sessionToken);
        }
    }
}
