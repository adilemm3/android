package ru.tpu.courses.lab4.db;

import androidx.annotation.NonNull;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface GroupDao {
    @Query("SELECT * FROM `group`")
    List<Group> getAll();

    @Insert
    void insert(@NonNull Group group);

    @Query("SELECT * FROM `group` WHERE "+
            "id = :groupId")
    Group selectGroupById(@NonNull int groupId);

    @Query("SELECT * FROM `group` WHERE "+
            "group_name = :groupName")
    Group selectByName(@NonNull String groupName);

    @Query(
            "SELECT COUNT(*) FROM `group` WHERE " +
                    "group_name = :groupName"
    )
    int count(@NonNull String groupName);

}
