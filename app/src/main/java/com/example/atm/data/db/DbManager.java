package com.example.atm.data.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.example.atm.data.db.dao.TransactionDao;
import com.example.atm.data.db.dao.UserDao;
import com.example.atm.data.models.Transaction;
import com.example.atm.data.models.User;


    @Database(entities = {User.class, Transaction.class}, version = 2)
    @TypeConverters({DataTypeConverter.class})
    public abstract class DbManager extends RoomDatabase {

        private final static String DB_NAME = "ATM_DB";

        private static DbManager instance;

        public abstract UserDao userDao();

        public abstract TransactionDao transactionDao();

        public static synchronized DbManager getInstance(Context context) {
            if (instance == null) {
                instance = Room.databaseBuilder(context, DbManager.class, DB_NAME)
                        .fallbackToDestructiveMigration().build();
            }
            return instance;
        }
    }

