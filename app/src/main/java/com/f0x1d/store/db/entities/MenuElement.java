package com.f0x1d.store.db.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class MenuElement {

    @PrimaryKey
    public long id;
    public String description;
    public long editTime = System.currentTimeMillis();
    public String imageSource;
    public long inFolderId;
    public boolean isFolder;
    public String name;

    public MenuElement() {

    }

    public static MenuElement create_elem(long id, long inFolderId, String name, String imageSource, boolean isFolder, String description) {
        MenuElement elem = new MenuElement();
        elem.id = id;
        elem.inFolderId = inFolderId;
        elem.isFolder = isFolder;
        elem.name = name;
        elem.imageSource = imageSource;
        elem.description = description;
        return elem;
    }
}
