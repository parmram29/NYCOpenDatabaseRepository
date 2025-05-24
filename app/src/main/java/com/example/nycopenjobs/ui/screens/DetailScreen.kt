package com.example.nycopenjobs.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.nycopenjobs.model.JobPost
import com.example.nycopenjobs.util.capitalizeWords

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    jobPost: JobPost,
    isFavorite: Boolean,
    onBackClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(jobPost.businessTitle.capitalizeWords()) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = onFavoriteClick) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Filled.Favorite
                            else Icons.Outlined.FavoriteBorder,
                            contentDescription = if (isFavorite) "Unfavorite" else "Favorite"
                        )
                    }
                }
            )
        },
        content = { innerPadding ->
            LazyColumn(
                modifier = modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item { DetailRow("Agency:", jobPost.agency) }
                item { DetailRow("Job ID:", jobPost.jobId.toString()) }
                item { DetailRow("Posting Date:", jobPost.postingDate) }
                item { DetailRow("Business Title:", jobPost.businessTitle.capitalizeWords()) }
                item { DetailRow("Career Level:", jobPost.careerLevel) }
                item {
                    DetailRow(
                        "Salary Range:",
                        "$${jobPost.salaryRangeFrom.toInt()} - $${jobPost.salaryRangeTo.toInt()} ${jobPost.salaryFrequency}"
                    )
                }
                item { DetailRow("Job Category:", jobPost.jobCategory) }
                item { DetailRow("Location:", jobPost.agencyLocation) }
                item { DetailRow("Division:", jobPost.divisionWorkUnit) }

                item {
                    DetailRow(
                        "Description:",
                        fixSpecialCharacters(jobPost.jobDescription)
                    )
                }
            }
        }
    )
}

fun fixSpecialCharacters(text: String): String {
    var fixedText = text.replace("â¢", "•")

    fixedText = fixedText.replace("Cityâ", "City")

    return fixedText
}

@Composable
fun DetailRow(label: String, value: String) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(bottom = 8.dp)) {
        Text(text = label, style = MaterialTheme.typography.labelSmall)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = value, style = MaterialTheme.typography.bodyMedium)
    }
}