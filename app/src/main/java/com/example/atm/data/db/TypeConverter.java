package com.example.atm.data.db;

import java.lang.Number;
import java.util.Date;


public class TypeConverter {
    @androidx.room.TypeConverter
    public static Long dateToLong(Date date) {
        return date == null ? null : date.getTime();
    }

    @androidx.room.TypeConverter
    public static Date longToDate(Long value) {
        return value == null ? null : new Date(value);
    }
}
