package com.example.atm.data.db;

import androidx.room.TypeConverter;

import java.util.Date;


public class DataTypeConverter {
    @TypeConverter
    public static Long dateToLong(Date date) {
        return date == null ? null : date.getTime();
    }

    @TypeConverter
    public static Date longToDate(Long value) {
        return value == null ? null : new Date(value);
    }
}
