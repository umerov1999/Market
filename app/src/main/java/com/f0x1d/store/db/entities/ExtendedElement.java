package com.f0x1d.store.db.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class ExtendedElement {

    @PrimaryKey
    public long id;
    public long ownerId;
    public boolean reserved;
    public String text;
}
