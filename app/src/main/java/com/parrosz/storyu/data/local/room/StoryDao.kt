package com.parrosz.storyu.data.local.room

import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.parrosz.storyu.data.remote.responses.StoryEntity

@Dao
interface StoryDao {
    @Query("SELECT * FROM story")
    fun getStoriesAsLiveData(): LiveData<List<StoryEntity>>

    @Query("SELECT * FROM story")
    fun getStories(): PagingSource<Int, StoryEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertStories(stories: List<StoryEntity>)

    @Query("DELETE FROM story")
    fun deleteAllStories()

    @Query("SELECT COUNT(id) FROM story")
    fun getCount(): Int
}