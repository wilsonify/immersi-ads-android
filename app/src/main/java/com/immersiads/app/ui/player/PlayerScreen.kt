package com.immersiads.app.ui.player

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ClosedCaption
import androidx.compose.material.icons.filled.ClosedCaptionDisabled
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackParameters
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import kotlinx.coroutines.delay

@Suppress("kotlin:S108")
@OptIn(ExperimentalMaterial3Api::class)
@androidx.annotation.OptIn(UnstableApi::class)
@Composable
fun PlayerScreen(
    viewModel: PlayerViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val snackbarHostState = remember { SnackbarHostState() }
    val showSpeedDialogState = remember { mutableStateOf(false) }

    val exoPlayer = remember {
        ExoPlayer.Builder(context).build()
    }

    LaunchedEffect(uiState.advertisement) {
        uiState.advertisement?.let { ad ->
            val mediaItem = MediaItem.fromUri(ad.videoUrl)
            exoPlayer.setMediaItem(mediaItem)
            exoPlayer.prepare()
            exoPlayer.play()
        }
    }

    LaunchedEffect(uiState.playbackSpeed) {
        exoPlayer.playbackParameters = PlaybackParameters(uiState.playbackSpeed)
    }

    LaunchedEffect(uiState.snackbarMessage) {
        uiState.snackbarMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearSnackbar()
        }
    }

    PlayerPositionTracker(exoPlayer = exoPlayer, onPositionUpdate = viewModel::updatePosition)

    LifecycleAwarePlayerPauseResume(
        lifecycleOwner = lifecycleOwner,
        exoPlayer = exoPlayer,
        hasMedia = uiState.advertisement != null
    )

    DisposableEffect(Unit) {
        val listener = object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                if (playbackState == Player.STATE_ENDED) {
                    viewModel.onAdCompleted()
                }
            }
            override fun onPlayerError(error: androidx.media3.common.PlaybackException) {
                viewModel.onPlayerError(error)
            }
        }
        exoPlayer.addListener(listener)
        onDispose {
            exoPlayer.removeListener(listener)
            exoPlayer.release()
        }
    }

    PlayerErrorDialog(uiState.errorMessage, viewModel::clearError, onNavigateBack)

    Scaffold(
        topBar = {
            PlayerTopBar(
                title = uiState.advertisement?.title,
                subtitlesEnabled = uiState.subtitlesEnabled,
                onNavigateBack = onNavigateBack,
                onSpeedClick = { showSpeedDialogState.value = true },
                onToggleSubtitles = viewModel::toggleSubtitles
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        PlayerContent(
            paddingValues = paddingValues,
            exoPlayer = exoPlayer,
            subtitlesEnabled = uiState.subtitlesEnabled,
            currentSubtitle = uiState.currentSubtitle?.text,
            advertisement = uiState.advertisement,
            onWordClick = viewModel::onWordSelected
        )
    }

    if (uiState.showTranslationPopup) {
        TranslationDialog(
            word = uiState.selectedWord ?: "",
            translation = uiState.selectedWordTranslation ?: "",
            onSave = viewModel::saveWordToVocabulary,
            onDismiss = viewModel::dismissTranslationPopup
        )
    }

    if (showSpeedDialogState.value) {
        SpeedSelectionDialog(
            currentSpeed = uiState.playbackSpeed,
            onSpeedSelected = { speed ->
                viewModel.setPlaybackSpeed(speed)
                showSpeedDialogState.value = false
            },
            onDismiss = { showSpeedDialogState.value = false }
        )
    }
}

@Composable
private fun PlayerPositionTracker(
    exoPlayer: ExoPlayer,
    onPositionUpdate: (Long) -> Unit
) {
    LaunchedEffect(Unit) {
        while (true) {
            delay(200L)
            if (exoPlayer.isPlaying) {
                onPositionUpdate(exoPlayer.currentPosition)
            }
        }
    }
}

@Suppress("kotlin:S108")
@Composable
private fun LifecycleAwarePlayerPauseResume(
    lifecycleOwner: androidx.lifecycle.LifecycleOwner,
    exoPlayer: ExoPlayer,
    hasMedia: Boolean
) {
    DisposableEffect(lifecycleOwner) {
        val lifecycleObserver = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_PAUSE -> exoPlayer.pause()
                Lifecycle.Event.ON_RESUME -> {
                    if (hasMedia) {
                        exoPlayer.play()
                    }
                }
                else -> { }
            }
        }
        lifecycleOwner.lifecycle.addObserver(lifecycleObserver)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(lifecycleObserver)
        }
    }
}

@Composable
private fun PlayerErrorDialog(
    errorMessage: String?,
    onClearError: () -> Unit,
    onNavigateBack: () -> Unit
) {
    if (errorMessage != null) {
        AlertDialog(
            onDismissRequest = onClearError,
            title = { Text("Playback Error") },
            text = { Text(errorMessage) },
            confirmButton = {
                Button(onClick = { onClearError(); onNavigateBack() }) {
                    Text("Go Back")
                }
            },
            dismissButton = {
                TextButton(onClick = onClearError) {
                    Text("Dismiss")
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PlayerTopBar(
    title: String?,
    subtitlesEnabled: Boolean,
    onNavigateBack: () -> Unit,
    onSpeedClick: () -> Unit,
    onToggleSubtitles: () -> Unit
) {
    TopAppBar(
        title = {
            Text(
                title ?: "Playing",
                style = MaterialTheme.typography.titleMedium
            )
        },
        navigationIcon = {
            IconButton(onClick = onNavigateBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
        },
        actions = {
            IconButton(
                onClick = onSpeedClick,
                modifier = Modifier.semantics { contentDescription = "Playback speed" }
            ) {
                Icon(Icons.Default.Speed, contentDescription = null)
            }
            IconButton(
                onClick = onToggleSubtitles,
                modifier = Modifier.semantics { contentDescription = "Toggle subtitles" }
            ) {
                Icon(
                    if (subtitlesEnabled)
                        Icons.Default.ClosedCaption
                    else
                        Icons.Default.ClosedCaptionDisabled,
                    contentDescription = null
                )
            }
        }
    )
}

@Composable
private fun PlayerVideoContent(
    modifier: Modifier,
    exoPlayer: ExoPlayer
) {
    Box(
        modifier = modifier
    ) {
        AndroidView(
            factory = { ctx ->
                PlayerView(ctx).apply {
                    player = exoPlayer
                    useController = true
                }
            },
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
private fun PlayerSubtitleSection(
    subtitlesEnabled: Boolean,
    currentSubtitle: String?,
    onWordClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    if (subtitlesEnabled && currentSubtitle != null) {
        Box(modifier = modifier) {
            SubtitleBar(
                subtitle = currentSubtitle,
                onWordClick = onWordClick
            )
        }
    }
}

@Composable
private fun PlayerAdInfo(
    advertisement: com.immersiads.app.data.model.Advertisement?,
    modifier: Modifier = Modifier
) {
    advertisement?.let { ad ->
        Column(modifier = modifier.padding(16.dp)) {
            Text(
                text = ad.brand,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = ad.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun PlayerContent(
    paddingValues: androidx.compose.foundation.layout.PaddingValues,
    exoPlayer: ExoPlayer,
    subtitlesEnabled: Boolean,
    currentSubtitle: String?,
    advertisement: com.immersiads.app.data.model.Advertisement?,
    onWordClick: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
    ) {
        PlayerVideoContent(
            modifier = Modifier.fillMaxSize(),
            exoPlayer = exoPlayer
        )
        PlayerSubtitleSection(
            subtitlesEnabled = subtitlesEnabled,
            currentSubtitle = currentSubtitle,
            onWordClick = onWordClick,
            modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth()
        )
        PlayerAdInfo(
            advertisement = advertisement,
            modifier = Modifier.align(Alignment.TopStart)
        )
    }
}

@Composable
private fun SubtitleBar(
    subtitle: String,
    onWordClick: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Black.copy(alpha = 0.7f))
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        val words = subtitle.split(" ")
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            words.forEachIndexed { index, word ->
                if (index > 0) Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = word,
                    color = Color.White,
                    fontSize = 16.sp,
                    modifier = Modifier
                        .clickable { onWordClick(word.replace(Regex("[^\\w]"), "")) }
                        .padding(horizontal = 2.dp)
                        .semantics { contentDescription = "Tap to translate: $word" }
                )
            }
        }
    }
}

@Composable
private fun TranslationDialog(
    word: String,
    translation: String,
    onSave: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(word, fontWeight = FontWeight.Bold) },
        text = { Text(translation) },
        confirmButton = {
            Button(onClick = onSave) {
                Text("Save to Vocabulary")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Dismiss")
            }
        }
    )
}

@Composable
private fun SpeedSelectionDialog(
    currentSpeed: Float,
    onSpeedSelected: (Float) -> Unit,
    onDismiss: () -> Unit
) {
    val speeds = listOf(0.5f, 0.75f, 1.0f, 1.25f, 1.5f, 2.0f)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Playback Speed") },
        text = {
            Column {
                speeds.forEach { speed ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSpeedSelected(speed) }
                            .padding(vertical = 12.dp, horizontal = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "${speed}x",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = if (speed == currentSpeed) FontWeight.Bold else FontWeight.Normal
                        )
                        if (speed == currentSpeed) {
                            Text("✓", color = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
