package com.example.atm.utils;

import androidx.annotation.NonNull;

public class Validator {
    public static boolean isPasswordValid(@NonNull String s) {
        String regex = "[a-zA-Z0-9]{3,25}$";
        return s.matches(regex);
    }

    public static boolean isEmailValid(@NonNull String s) {
        String regex = "[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,4}";
        return s.matches(regex);
    }
}
