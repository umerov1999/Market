package com.f0x1d.store.db.daos;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.f0x1d.store.db.entities.MenuElement;

import java.util.List;

@Dao
public interface MenuElementsDao {
    @Query("UPDATE MenuElement SET editTime = :editTime WHERE id = :id")
    void changeEditTime(long editTime, long id);

    @Query("DELETE FROM MenuElement WHERE id = :id")
    void delete(long id);

    @Query("DELETE FROM ExtendedElement WHERE ownerId = :id")
    void deleteAllExtendElements(long id);

    @Query("SELECT * FROM MenuElement order by editTime desc")
    List<MenuElement> getAll();

    @Query("SELECT * FROM MenuElement WHERE inFolderId = :inFolderId order by editTime desc")
    List<MenuElement> getAll(long inFolderId);

    @Query("SELECT * FROM MenuElement WHERE id = :id")
    MenuElement getById(long id);

    @Insert
    void insert(MenuElement menuElement);

    @Query("UPDATE MenuElement SET description = :description WHERE id = :id")
    void updateDescription(String description, long id);

    @Query("UPDATE MenuElement SET imageSource = :imageSource WHERE id = :id")
    void updateImage(String imageSource, long id);

    @Query("UPDATE MenuElement SET name = :name WHERE id = :id")
    void updateName(String name, long id);
}
