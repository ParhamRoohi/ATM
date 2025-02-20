package com.example.atm.data.db;

import java.lang.Number;
import java.util.Date;


public class TypeConverter {
    @androidx.room.TypeConverter
    public static Long dateToLong(Date date) {
        return date.getTime();
    }

    @androidx.room.TypeConverter
    public static Date longToDate(Long value) {
        return new Date(value);
    }
}
