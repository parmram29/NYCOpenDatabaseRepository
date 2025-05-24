package com.example.nycopenjobs.data

import android.content.Context
import android.util.Log
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.Upsert
import com.example.nycopenjobs.model.JobPost
import com.example.nycopenjobs.util.TAG
import kotlinx.coroutines.flow.Flow

/**
 * Job Post Data Access Object (DAO)
 *
 * Defines the data access operations for the JobPost entity.
 */
@Dao
interface JobPostDao {

    @Query("SELECT * FROM JobPost ORDER BY postingLastUpdated DESC")
    fun getAll(): Flow<List<JobPost>>

    @Query("SELECT * FROM JobPost WHERE jobId = :id")
    fun get(id: Int): Flow<JobPost>

    @Upsert(entity = JobPost::class)
    suspend fun upsert(jobPostings: List<JobPost>)
}

/**
 * Local Database
 *
 * Defines the local Room database for the app.
 */
@Database( entities = [JobPost::class], version = 1, exportSchema = false )
abstract class LocalDatabase : RoomDatabase() {

    abstract fun jobPostDao(): JobPostDao

    companion object {
        private const val DATABASE = "local_database"

        @Volatile
        private var Instance: LocalDatabase? = null

        /**
         * Returns the singleton database instance.
         *
         * @param context Application context
         */
        fun getDatabase(context: Context): LocalDatabase {
            Log.i(TAG, "Getting database instance")
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, LocalDatabase::class.java, DATABASE)
                    .fallbackToDestructiveMigration().build().also { Instance = it }
            }
        }
    }
}
