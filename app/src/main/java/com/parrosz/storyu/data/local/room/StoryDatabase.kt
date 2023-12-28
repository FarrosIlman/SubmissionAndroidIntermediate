package com.parrosz.storyu.data.local.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.parrosz.storyu.data.local.entity.StoryRemoteKey
import com.parrosz.storyu.data.remote.responses.StoryEntity

@Database(
    entities = [StoryEntity::class, StoryRemoteKey::class] ,
    version = 3 ,
    exportSchema = false
)
abstract class StoryDatabase : RoomDatabase() {
    abstract fun storyDao(): StoryDao
    abstract fun storyRemoteKeyDao() : StoryRemoteKeyDao

    companion object {
        @Volatile
        private var INSTANCE: StoryDatabase? = null

        @JvmStatic
        fun getInstance(context : Context): StoryDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext ,
                    StoryDatabase::class.java , "stories.db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}