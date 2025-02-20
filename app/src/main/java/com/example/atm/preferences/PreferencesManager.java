package com.example.atm.preferences;

import android.content.Context;
import androidx.annotation.NonNull;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import java.util.Date;

public class PreferencesManager {

    private static final String PREF_FILE_NAME = "com.example.atm";
    public static final String PREF_KEY_USERNAME = "username";
    public static final String PREF_KEY_PASSWORD = "password";
    public static final String PREF_KEY_AGE = "age";
    public static final String PREF_KEY_PHONE_NUMBER = "phoneNumber";
    public static final String PREF_KEY_CARD_NUMBER = "cardNumber";
    public static final String PREF_KEY_ACCOUNT_NUMBER = "accountNumber";
    public static final String PREF_KEY_CVV2 = "Cvv2";
    public static final String PREF_KEY_EXPIRATION_DATE = "expirationDate";
    public static final String PREF_KEY_CURRENT_BALANCE = "balance";

    public static final String PREF_KEY_IS_LOGIN = "is_login";

    public static PreferencesManager preferencesManager;

    private final SharedPreferences sharedPreferences;

    private final Gson gson = new Gson();

    private PreferencesManager(@NonNull Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
    }

    public static synchronized PreferencesManager getInstance(Context context) {
        if (preferencesManager == null) {
            preferencesManager = new PreferencesManager(context);
        }
        return preferencesManager;
    }

    public <T> void put(String key, T value) {

        if (value instanceof String) {
            sharedPreferences.edit().putString(key, (String) value).apply();
            return;
        }

        if (value instanceof Integer) {
            sharedPreferences.edit().putInt(key, (Integer) value).apply();
            return;
        }

        if (value instanceof Boolean) {
            sharedPreferences.edit().putBoolean(key, (Boolean) value).apply();
            return;
        }

        if (value instanceof Float) {
            sharedPreferences.edit().putFloat(key, (Float) value).apply();
            return;
        }

        if (value instanceof Long) {
            sharedPreferences.edit().putLong(key, (Long) value).apply();
            return;
        }
    }

    public <T> T get(String key, T defaultValue) {
        if (defaultValue instanceof String) {
            return (T) sharedPreferences.getString(key, (String) defaultValue);
        }

        if (defaultValue instanceof Integer) {
            Integer result = sharedPreferences.getInt(key, (Integer) defaultValue);
            return (T) result;
        }

        if (defaultValue instanceof Boolean) {
            Boolean result = sharedPreferences.getBoolean(key, (Boolean) defaultValue);
            return (T) result;
        }

        if (defaultValue instanceof Float) {
            Float result = sharedPreferences.getFloat(key, (Float) defaultValue);
            return (T) result;
        }

        if (defaultValue instanceof Long) {
            Long result = sharedPreferences.getLong(key, (Long) defaultValue);
            return (T) result;
        }
        return null;
    }
    public <T> void putObj(String key, T t) {
        String json = gson.toJson(t);
        sharedPreferences.edit().putString(key, json).apply();
    }
    public <T> T getObj(String key, Class<T> t) {
        String jsonPreferences = sharedPreferences.getString(key, "");
        return gson.fromJson(jsonPreferences, t);
    }

}
