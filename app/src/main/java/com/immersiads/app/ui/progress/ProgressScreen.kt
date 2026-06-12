package com.immersiads.app.ui.progress

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProgressScreen(
    viewModel: ProgressViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Progress") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Learning ${uiState.targetLanguage.uppercase()}",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                StatCard(
                    emoji = "🔥",
                    value = "${uiState.streakCount}",
                    label = "Day Streak",
                    modifier = Modifier
                        .weight(1f)
                        .semantics { contentDescription = "Streak: ${uiState.streakCount} days" }
                )
                StatCard(
                    emoji = "🎬",
                    value = "${uiState.totalAdsWatched}",
                    label = "Ads Watched",
                    modifier = Modifier
                        .weight(1f)
                        .semantics { contentDescription = "Ads watched: ${uiState.totalAdsWatched}" }
                )
            }

            StatCard(
                emoji = "📚",
                value = "${uiState.vocabularyCount}",
                label = "Words Saved",
                modifier = Modifier
                    .fillMaxWidth()
                    .semantics { contentDescription = "Vocabulary: ${uiState.vocabularyCount} words" }
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Milestones",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            MilestoneRow(
                title = "First Ad Watched",
                achieved = uiState.totalAdsWatched >= 1,
                emoji = "🎬"
            )
            MilestoneRow(
                title = "10 Words Saved",
                achieved = uiState.vocabularyCount >= 10,
                emoji = "📖"
            )
            MilestoneRow(
                title = "5 Day Streak",
                achieved = uiState.streakCount >= 5,
                emoji = "🔥"
            )
            MilestoneRow(
                title = "Watch 10 Ads",
                achieved = uiState.totalAdsWatched >= 10,
                emoji = "🎯"
            )
            MilestoneRow(
                title = "50 Words Saved",
                achieved = uiState.vocabularyCount >= 50,
                emoji = "🏆"
            )
        }
    }
}

@Composable
private fun StatCard(
    emoji: String,
    value: String,
    label: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(emoji, fontSize = 32.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun MilestoneRow(
    title: String,
    achieved: Boolean,
    emoji: String
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(emoji, fontSize = 24.sp)
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (achieved) MaterialTheme.colorScheme.onSurface
                    else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = if (achieved) "✅" else "○",
                fontSize = 20.sp
            )
        }
    }
}
