package com.example.basicapp.data.local;

import android.content.Context;

import com.example.basicapp.data.local.dao.LocationDao;
import com.example.basicapp.data.local.entity.LocationEntity;

import java.util.List;

public class LocationLocalDataSource {

    private final LocationDao locationDao;

    public LocationLocalDataSource(Context context) {
        LocationDatabase database = LocationDatabase.getInstance(context);
        this.locationDao = database.locationDao();
    }

    public long insertLocation(LocationEntity location) {
        return locationDao.insert(location);
    }

    public List<Long> insertLocations(List<LocationEntity> locations) {
        return locationDao.insertAll(locations);
    }

    public void updateLocation(LocationEntity location) {
        locationDao.update(location);
    }

    public void deleteLocation(LocationEntity location) {
        locationDao.delete(location);
    }

    public void deleteLocationById(long id) {
        locationDao.deleteById(id);
    }

    public List<LocationEntity> getAllLocations() {
        return locationDao.getAll();
    }

    public LocationEntity getLocationById(long id) {
        return locationDao.getById(id);
    }

    public List<LocationEntity> searchLocations(String query) {
        return locationDao.searchByName(query);
    }

    public void deleteAllLocations() {
        locationDao.deleteAll();
    }
}
