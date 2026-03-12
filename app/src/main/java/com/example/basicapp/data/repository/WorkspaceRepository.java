package com.example.basicapp.data.repository;

import android.content.Context;

import com.example.basicapp.data.local.WorkspaceLocalDataSource;
import com.example.basicapp.data.local.entity.WorkspaceEntity;

import java.util.List;

public class WorkspaceRepository {

    private final WorkspaceLocalDataSource localDataSource;

    public WorkspaceRepository(Context context) {
        this.localDataSource = new WorkspaceLocalDataSource(context);
    }

    // CREATE
    public long insert(WorkspaceEntity workspace) {
        return localDataSource.insert(workspace);
    }

    public List<Long> insertAll(List<WorkspaceEntity> workspaces) {
        return localDataSource.insertAll(workspaces);
    }

    // READ
    public List<WorkspaceEntity> getAll() {
        return localDataSource.getAll();
    }

    public WorkspaceEntity getById(long id) {
        return localDataSource.getById(id);
    }

    public List<WorkspaceEntity> searchByName(String query) {
        return localDataSource.searchByName(query);
    }

    public List<WorkspaceEntity> getActiveWorkspaces() {
        return localDataSource.getActiveWorkspaces();
    }

    public List<WorkspaceEntity> getInactiveWorkspaces() {
        return localDataSource.getInactiveWorkspaces();
    }

    // UPDATE
    public void update(WorkspaceEntity workspace) {
        localDataSource.update(workspace);
    }

    public void updateActiveStatus(long id, boolean isActive) {
        localDataSource.updateActiveStatus(id, isActive);
    }

    // DELETE
    public void delete(WorkspaceEntity workspace) {
        localDataSource.delete(workspace);
    }

    public void deleteById(long id) {
        localDataSource.deleteById(id);
    }

    public void deleteAll() {
        localDataSource.deleteAll();
    }

    // COUNT
    public int getCount() {
        return localDataSource.getCount();
    }

    public int getActiveCount() {
        return localDataSource.getActiveCount();
    }
}
