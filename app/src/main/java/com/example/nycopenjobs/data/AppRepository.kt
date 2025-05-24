package com.example.nycopenjobs.data

import android.content.SharedPreferences
import android.util.Log
import com.example.nycopenjobs.api.NycOpenDataService
import com.example.nycopenjobs.model.JobPost
import com.example.nycopenjobs.util.TAG
import kotlinx.coroutines.flow.first


interface AppRepository {
    fun getScrollPosition(): Int
    fun setScrollPosition(position: Int)
    suspend fun getJobPostings(): List<JobPost>
    suspend fun getJobPost(jobId: Int): JobPost
}

/**
 * App repository implementation
 *
 * the app repo impl is responsible for providing data to the view model
 * this class is responsible for providing data to the view model. It implements the
 * AppRepository interface
 *
 *
 * @param nycOpenDataAPI the api to get data from NYC Open Data
 * @param sharedPreferences the shared preferences to store preference data
 * @param dao the data access object to get data from the local database
 */

class AppRepositoryImpl(
    private val nycOpenDataAPI: NycOpenDataService,
    private val sharedPreferences: SharedPreferences,
    private val dao: JobPostDao,
) : AppRepository {

    private val scrollPositionKey = "scroll_position"
    private val offsetKey = "offset"
    private var offset = sharedPreferences.getInt(offsetKey, 0)
    private var totalJobs = 0


    private fun updateOffset() {
        offset += (totalJobs - offset)
        Log.i(TAG, "offset: $offset")
        sharedPreferences.edit().putInt(offsetKey, offset).apply()
    }

    /**
     * Update totalc jobs
     *
     * total jobs is used to determine if we need to get more data from the API
     *
     * @param newTotalJobs the new total jobs
     */


    private fun updateTotalJobs(newTotalJobs: Int) {
        totalJobs = newTotalJobs
        Log.i(TAG, "total jobs: $totalJobs")
    }

    /**
     * get job postings from the nYC open data API
     *
     * this functiono will get job postings from the nyc open data api. it will then add the job
     * postings to the local database. it will then return the job postings
     *
     * offset and totalJobs are use to determine if we need to go
     * get more data by calling the remote api
     *
     *
     * NOTE:  the first time this function is called. offset is set to 0. if totalJobs is 0 then,
     * well call the remote API to get the job postings. If totalJobs is not 0, well return
     * the job postings from the local database
     */




    override suspend fun getJobPostings(): List<JobPost> {
        Log.i(TAG, "getting job postings")

        updateOffset()

        val localData = dao.getAll().first()
        updateTotalJobs(localData.size)

        if (offset == totalJobs) {

            Log.i(TAG, "getting job postings via API")
            val jobs = nycOpenDataAPI.getJobPostings(limit = 100, offset = offset)

            Log.i(TAG, "The API returned ${jobs.size} jobs. Updating local database")
            dao.upsert(jobs)

            val updatedData = dao.getAll().first()
            updateTotalJobs(updatedData.size)

            return updatedData
        }
           return localData

    }

    /**
     * get job post from local database
     *
     * @param jobId the id of the job post to get
     *
     * @return the job post
     */



    override suspend fun getJobPost(jobId: Int): JobPost {
        Log.i(TAG, "getting job post id $jobId")
        return dao.get(jobId).first()
    }

    /**
     * get scroll position from shared preferences
     *
     * the scroll position if used to scroll to the correct position when the app user
     * closes and reopens the app
     *
     * @return the scroll position
     */


    override fun getScrollPosition(): Int {
        val position = sharedPreferences.getInt(scrollPositionKey, 0)
        Log.i(TAG, "getting scroll position $position")
        return position
    }

    /**
     * set scroll posiion in shared preferences
     *
     * the scroll position is used to scroll to the
     * correct position when the app user closes and reopens the app
     */


    override fun setScrollPosition(position: Int) {
        Log.i(TAG, "setting scroll position to $position")
        sharedPreferences.edit().putInt(scrollPositionKey, position).apply()
    }
}
