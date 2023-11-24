package org.ithd.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import org.ithd.database.dao.UserDao
import org.ithd.database.model.UserEntity
import org.ithd.database.util.InstantConverter

@Database(
    entities = [
        UserEntity::class
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(
    InstantConverter::class
)
abstract class MediaEditorDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
}