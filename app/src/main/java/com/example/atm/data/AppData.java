package com.example.atm.data;

import android.app.Application;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import com.example.atm.data.models.User;

import java.util.Locale;

public class AppData extends Application {
    private static AppData appData;

    private User currentUser;
    private TextToSpeech textToSpeech;

    public static AppData getInstance() {
        return appData;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        appData = this;
        textToSpeech = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                int result = textToSpeech.setLanguage(Locale.US);
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Log.e("TTS", "Language not supported");
                }
            } else {
                Log.e("TTS", "Initialization failed");
            }
        });
    }

    public TextToSpeech getTextToSpeech() {
        return textToSpeech;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

}
