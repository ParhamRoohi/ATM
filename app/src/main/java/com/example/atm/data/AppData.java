package com.example.atm.data;

import android.app.Application;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import com.example.atm.data.models.User;

import java.util.Locale;

public class AppData extends Application {
    private static AppData appData;

    private User currentUser;

    public static AppData getInstance() {
        return appData;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        appData = this;

    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

}
