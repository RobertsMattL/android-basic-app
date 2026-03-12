package com.example.basicapp.data.local;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.basicapp.data.local.dao.LocationDao;
import com.example.basicapp.data.local.entity.LocationEntity;

@Database(entities = {LocationEntity.class}, version = 1, exportSchema = false)
public abstract class LocationDatabase extends RoomDatabase {

    private static volatile LocationDatabase INSTANCE;

    public abstract LocationDao locationDao();

    public static LocationDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (LocationDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                            context.getApplicationContext(),
                            LocationDatabase.class,
                            "location_database"
                    ).build();
                }
            }
        }
        return INSTANCE;
    }
}
