package com.example.basicapp.data.repository;

import android.content.Context;

import com.example.basicapp.data.local.LocationLocalDataSource;
import com.example.basicapp.data.local.entity.LocationEntity;

import java.util.List;

public class LocationRepository {

    private final LocationLocalDataSource localDataSource;

    public LocationRepository(Context context) {
        this.localDataSource = new LocationLocalDataSource(context);
    }

    public long saveLocation(LocationEntity location) {
        return localDataSource.insertLocation(location);
    }

    public List<Long> saveLocations(List<LocationEntity> locations) {
        return localDataSource.insertLocations(locations);
    }

    public void updateLocation(LocationEntity location) {
        localDataSource.updateLocation(location);
    }

    public void deleteLocation(LocationEntity location) {
        localDataSource.deleteLocation(location);
    }

    public void deleteLocationById(long id) {
        localDataSource.deleteLocationById(id);
    }

    public List<LocationEntity> getAllLocations() {
        return localDataSource.getAllLocations();
    }

    public LocationEntity getLocationById(long id) {
        return localDataSource.getLocationById(id);
    }

    public List<LocationEntity> searchLocations(String query) {
        return localDataSource.searchLocations(query);
    }

    public void deleteAllLocations() {
        localDataSource.deleteAllLocations();
    }
}
