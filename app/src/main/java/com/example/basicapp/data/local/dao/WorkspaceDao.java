package com.example.basicapp.data.local.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.basicapp.data.local.entity.WorkspaceEntity;

import java.util.List;

@Dao
public interface WorkspaceDao {

    // CREATE
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(WorkspaceEntity workspace);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    List<Long> insertAll(List<WorkspaceEntity> workspaces);

    // READ
    @Query("SELECT * FROM workspaces ORDER BY updatedAt DESC")
    List<WorkspaceEntity> getAll();

    @Query("SELECT * FROM workspaces WHERE id = :id")
    WorkspaceEntity getById(long id);

    @Query("SELECT * FROM workspaces WHERE name LIKE '%' || :query || '%' ORDER BY updatedAt DESC")
    List<WorkspaceEntity> searchByName(String query);

    @Query("SELECT * FROM workspaces WHERE isActive = 1 ORDER BY updatedAt DESC")
    List<WorkspaceEntity> getActiveWorkspaces();

    @Query("SELECT * FROM workspaces WHERE isActive = 0 ORDER BY updatedAt DESC")
    List<WorkspaceEntity> getInactiveWorkspaces();

    // UPDATE
    @Update
    void update(WorkspaceEntity workspace);

    @Query("UPDATE workspaces SET isActive = :isActive WHERE id = :id")
    void updateActiveStatus(long id, boolean isActive);

    // DELETE
    @Delete
    void delete(WorkspaceEntity workspace);

    @Query("DELETE FROM workspaces WHERE id = :id")
    void deleteById(long id);

    @Query("DELETE FROM workspaces")
    void deleteAll();

    // COUNT
    @Query("SELECT COUNT(*) FROM workspaces")
    int getCount();

    @Query("SELECT COUNT(*) FROM workspaces WHERE isActive = 1")
    int getActiveCount();
}
