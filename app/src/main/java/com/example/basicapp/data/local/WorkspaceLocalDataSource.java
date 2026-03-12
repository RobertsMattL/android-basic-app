package com.example.basicapp.data.local;

import android.content.Context;

import com.example.basicapp.data.local.dao.WorkspaceDao;
import com.example.basicapp.data.local.entity.WorkspaceEntity;

import java.util.List;

public class WorkspaceLocalDataSource {

    private final WorkspaceDao workspaceDao;

    public WorkspaceLocalDataSource(Context context) {
        LocationDatabase database = LocationDatabase.getInstance(context);
        this.workspaceDao = database.workspaceDao();
    }

    // CREATE
    public long insert(WorkspaceEntity workspace) {
        return workspaceDao.insert(workspace);
    }

    public List<Long> insertAll(List<WorkspaceEntity> workspaces) {
        return workspaceDao.insertAll(workspaces);
    }

    // READ
    public List<WorkspaceEntity> getAll() {
        return workspaceDao.getAll();
    }

    public WorkspaceEntity getById(long id) {
        return workspaceDao.getById(id);
    }

    public List<WorkspaceEntity> searchByName(String query) {
        return workspaceDao.searchByName(query);
    }

    public List<WorkspaceEntity> getActiveWorkspaces() {
        return workspaceDao.getActiveWorkspaces();
    }

    public List<WorkspaceEntity> getInactiveWorkspaces() {
        return workspaceDao.getInactiveWorkspaces();
    }

    // UPDATE
    public void update(WorkspaceEntity workspace) {
        workspaceDao.update(workspace);
    }

    public void updateActiveStatus(long id, boolean isActive) {
        workspaceDao.updateActiveStatus(id, isActive);
    }

    // DELETE
    public void delete(WorkspaceEntity workspace) {
        workspaceDao.delete(workspace);
    }

    public void deleteById(long id) {
        workspaceDao.deleteById(id);
    }

    public void deleteAll() {
        workspaceDao.deleteAll();
    }

    // COUNT
    public int getCount() {
        return workspaceDao.getCount();
    }

    public int getActiveCount() {
        return workspaceDao.getActiveCount();
    }
}
