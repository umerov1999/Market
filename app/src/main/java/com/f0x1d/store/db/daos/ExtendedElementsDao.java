package com.f0x1d.store.db.daos;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.f0x1d.store.db.entities.ExtendedElement;

import java.util.List;

@Dao
public interface ExtendedElementsDao {
    @Query("DELETE FROM ExtendedElement WHERE id = :id")
    void delete(long id);

    @Query("SELECT * FROM ExtendedElement order by text asc")
    List<ExtendedElement> getAll();

    @Query("SELECT * FROM ExtendedElement WHERE ownerId = :ownerId order by text asc")
    List<ExtendedElement> getAll(long ownerId);

    @Query("SELECT * FROM ExtendedElement WHERE id = :id")
    ExtendedElement getById(long id);

    @Insert
    void insert(ExtendedElement extendedElement);

    @Query("UPDATE ExtendedElement SET text = :text WHERE id = :id")
    void updateText(String text, long id);

    @Query("UPDATE ExtendedElement SET reserved = :reserved WHERE id = :id")
    void updateReserved(boolean reserved, long id);
}
