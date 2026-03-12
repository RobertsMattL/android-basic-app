package com.example.basicapp.data.local.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.basicapp.data.local.entity.LocationEntity;

import java.util.List;

@Dao
public interface LocationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(LocationEntity location);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    List<Long> insertAll(List<LocationEntity> locations);

    @Update
    void update(LocationEntity location);

    @Delete
    void delete(LocationEntity location);

    @Query("DELETE FROM locations WHERE id = :id")
    void deleteById(long id);

    @Query("SELECT * FROM locations ORDER BY timestamp DESC")
    List<LocationEntity> getAll();

    @Query("SELECT * FROM locations WHERE id = :id")
    LocationEntity getById(long id);

    @Query("SELECT * FROM locations WHERE name LIKE '%' || :query || '%'")
    List<LocationEntity> searchByName(String query);

    @Query("DELETE FROM locations")
    void deleteAll();
}
