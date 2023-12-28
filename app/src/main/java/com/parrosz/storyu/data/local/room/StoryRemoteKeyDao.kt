package com.parrosz.storyu.data.local.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.parrosz.storyu.data.local.entity.StoryRemoteKey

@Dao
interface StoryRemoteKeyDao {

    @Query("SELECT * FROM story_remote_keys WHERE id = :id")
    fun getRemoteKeys(id: String): StoryRemoteKey?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addRemoteKeys(remoteKeys: List<StoryRemoteKey>)

    @Query("DELETE FROM story_remote_keys")
    fun deleteRemoteKeys()
}