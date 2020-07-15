package com.f0x1d.store.db;

import androidx.room.RoomDatabase;

import com.f0x1d.store.App;
import com.f0x1d.store.db.daos.ExtendedElementsDao;
import com.f0x1d.store.db.daos.MenuElementsDao;
import com.f0x1d.store.db.entities.ExtendedElement;
import com.f0x1d.store.db.entities.MenuElement;

@androidx.room.Database(entities = {MenuElement.class, ExtendedElement.class}, version = 1, exportSchema = false)
public abstract class Database extends RoomDatabase {
    /*
    public static Migration MIGRATION_1_2 = new Migration(1, 2) {
        public void migrate(SupportSQLiteDatabase supportSQLiteDatabase) {
            supportSQLiteDatabase.execSQL("ALTER TABLE MenuElement ADD COLUMN editTime INTEGER DEFAULT " + System.currentTimeMillis() + " NOT NULL");
        }
    };

     */
    public static long getMenuLastId() {
        long j = 0;
        for (MenuElement next : App.getInstance().getDatabase().menuElementsDao().getAll()) {
            if (next.id > j) {
                j = next.id;
            }
        }
        return j + 1;
    }

    public static long getExtendedLastId() {
        long j = 0;
        for (ExtendedElement next : App.getInstance().getDatabase().extendedElementsDao().getAll()) {
            if (next.id > j) {
                j = next.id;
            }
        }
        return j + 1;
    }

    public abstract ExtendedElementsDao extendedElementsDao();

    public abstract MenuElementsDao menuElementsDao();
}
