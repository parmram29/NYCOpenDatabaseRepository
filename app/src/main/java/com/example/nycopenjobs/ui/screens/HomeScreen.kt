package com.example.nycopenjobs.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.nycopenjobs.R
import com.example.nycopenjobs.model.JobPost
import com.example.nycopenjobs.util.capitalizeWords
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce

@Composable
fun HomeScreen(
    viewModel: JobPostingsViewModel,
    modifier: Modifier = Modifier
) {
    when (val uiState = viewModel.jobPostingsUIState) {
        is JobPostingsUIState.Loading ->
            LoadingSpinner()

        is JobPostingsUIState.Success -> {
            JobPostList(
                uiState.data,
                { viewModel.getJobPostings() },
                { scrollPosition -> viewModel.setScrollingPosition(scrollPosition) },
                viewModel.getScrollPosition(),
                modifier.fillMaxSize()
            )
        }
        is JobPostingsUIState.Error ->
            ToastMessage(stringResource(R.string.data_failed))
        else -> ToastMessage(stringResource(R.string.Loaded))
    }
}

@OptIn(FlowPreview::class)
@Composable
fun JobPostList(
    jobPostings: List<JobPost>,
    loadMoreData: () -> Unit,
    updateScrollPosition: (Int) -> Unit,
    scrollPosition: Int,
    modifier: Modifier
) {
    val firstVisibleIndex = if (scrollPosition > jobPostings.size) 0 else scrollPosition
    val listState: LazyListState = rememberLazyListState(firstVisibleIndex)

 //fix from here dont mess up } placements
    val favorites = remember { mutableStateListOf<JobPost>() }
    var selectedJob by remember { mutableStateOf<JobPost?>(null) }
    var showFavorites by remember { mutableStateOf(false) }
    var searchText by remember { mutableStateOf("") }
    val filteredJobs = remember(searchText, jobPostings) {
        jobPostings.filter { it.businessTitle.contains(searchText, ignoreCase = true) }
    }
    val jobListProxy by remember(showFavorites, filteredJobs, favorites) {
        mutableStateOf(if (showFavorites) favorites.toList() else filteredJobs)
    }


    if (selectedJob != null) {
        DetailScreen(
            jobPost = selectedJob!!,
            isFavorite = favorites.contains(selectedJob),
            onBackClick = { selectedJob = null },
            onFavoriteClick = {
                if (favorites.contains(selectedJob)) favorites.remove(selectedJob!!)
                else favorites.add(selectedJob!!)
            },
            modifier = Modifier.fillMaxSize()
        )
        return
    }

    Column(modifier = modifier) {
        OutlinedTextField(
            value = searchText,
            onValueChange = { searchText = it },
            label = { Text("Search Jobs") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )

        val filteredJobs = jobPostings.filter {
            it.businessTitle.contains(searchText, ignoreCase = true)
        }
        val jobList = if (showFavorites) favorites else filteredJobs

        LazyColumn(
            modifier = Modifier.weight(1f),
            state = listState,
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(jobPostings) { jobPost: JobPost ->
                //fixed todo
                if (!jobListProxy.contains(jobPost)) return@items
                val formattedTitle = jobPost.businessTitle.capitalizeWords()
                Text(text = formattedTitle)
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { selectedJob = jobPost },
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    ListItem(
                        headlineContent = {
                            Text(jobPost.businessTitle.capitalizeWords())
                        },
                        supportingContent = {
                            Text("${jobPost.agency} • ${jobPost.careerLevel}")
                        },
                        trailingContent = {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = "View Details"
                            )
                        },
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Text(
                text = "🏠",
                modifier = Modifier.clickable { showFavorites = false },
                style = MaterialTheme.typography.headlineMedium
            )
            Text(
                text = "❤",
                modifier = Modifier.clickable { showFavorites = true },
                style = MaterialTheme.typography.headlineMedium
            )
        }
    }

    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
            .debounce(timeoutMillis = 500L)
            .collect { lastVisibleItemIndex ->
                updateScrollPosition(listState.firstVisibleItemIndex)
                if (lastVisibleItemIndex != null && lastVisibleItemIndex >= jobPostings.size - 1) {
                    loadMoreData()
                }
            }
    }
}
