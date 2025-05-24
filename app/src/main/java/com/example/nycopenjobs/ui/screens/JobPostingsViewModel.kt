package com.example.nycopenjobs.ui.screens

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.nycopenjobs.data.AppRepository
import com.example.nycopenjobs.model.JobPost
import com.example.nycopenjobs.util.NycOpenJobsApplication
import com.example.nycopenjobs.util.TAG
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

/**
 * UI state
 *
 * This sealed interface defines the UI state used by the app.
 *
 * @property JobPostingsUIState.Success the data is successfully loaded
 * @property JobPostingsUIState.Error the data failed to load
 * @property JobPostingsUIState.Loading the data is loading
 * @property JobPostingsUIState.Ready the data is ready to be loaded
 */
sealed interface JobPostingsUIState {
    /**
     * Success state
     *
     * @property data the data that was successfully loaded
     */
    data class Success(val data: List<JobPost>) : JobPostingsUIState
    data object Error : JobPostingsUIState
    data object Loading : JobPostingsUIState
    data object Ready : JobPostingsUIState
}

/**
 * view model
 *
 * This view model is responsible for providing data to the UI.
 *
 * @param repository the repository to get data from
 */
class JobPostingsViewModel(private val repository: AppRepository) : ViewModel() {

    var jobPostingsUIState: JobPostingsUIState by mutableStateOf(JobPostingsUIState.Ready)
        private set

    init {
        getJobPostings()
    }

    /**
     * get job postings
     *
     * This function will get job postings from the repository.
     */
    fun getJobPostings() {
        viewModelScope.launch {
            jobPostingsUIState = JobPostingsUIState.Loading
            jobPostingsUIState = try {
                JobPostingsUIState.Success(repository.getJobPostings())
            } catch (e: IOException) {
                e.message?.let { Log.e(TAG, it) }
                JobPostingsUIState.Error
            } catch (e: HttpException) {
                e.message?.let { Log.e(TAG, it) }
                JobPostingsUIState.Error
            }
        }
    }

    /**
     * get scroll position
     *
     * This function will get the scroll position from the repository.
     */
    fun getScrollPosition(): Int {
        return repository.getScrollPosition()
    }

    /**
     * set scroll position
     *
     * This function will set the scroll position in the repository.
     * @param position the position to set
     */
    fun setScrollingPosition(position: Int) {
        return repository.setScrollPosition(position)
    }

    /**
     * This singleton object is used to create a view model factory.
     *
     * It implements the ViewModelProvider.Factory interface by providing a create method
     */
    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            /**
             * This method will create a view model.
             *
             * @param modelClass the class of the view model
             * @param extras the extras to pass to the view model
             */
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(
                modelClass: Class<T>,
                extras: CreationExtras
            ): T {
                Log.i(TAG, "view model factory: getting application repository")
                val application = checkNotNull(extras[APPLICATION_KEY]) as NycOpenJobsApplication
                val nycJobsRepository = application.container.appRepository
                return JobPostingsViewModel(repository = nycJobsRepository) as T
            }
        }
    }
}