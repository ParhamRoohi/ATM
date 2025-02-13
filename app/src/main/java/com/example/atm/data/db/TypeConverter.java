package com.example.atm.data.db;

import java.lang.Number;


public class TypeConverter {
    @androidx.room.TypeConverter
    public static double fromNumber(Number number) {
        return number != null ? number.doubleValue() : 0.0;
    }

    @androidx.room.TypeConverter
    public static Number toNumber(double value) {
        return value;
    }
}
