package com.cyberverify.ui.component

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Audiotrack
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cyberverify.domain.model.MediaMetadata
import com.cyberverify.domain.model.MediaType
import com.cyberverify.ui.theme.CyberVerifyTheme
import com.cyberverify.util.MediaPickerContracts

@Composable
fun MediaPickerSection(
    activeTab: MediaType,
    selectedMetadata: MediaMetadata?,
    audioPlayerState: com.cyberverify.util.AudioPlayerState,
    onTabSelected: (MediaType) -> Unit,
    onMediaSelected: (Uri) -> Unit,
    onClearMedia: () -> Unit,
    onPlayPauseToggle: () -> Unit,
    onSeekTo: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    // SAF Picker for Image
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.let { onMediaSelected(it) }
    }

    // SAF Picker for Audio
    val audioPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.let { onMediaSelected(it) }
    }

    Column(modifier = modifier.fillMaxWidth()) {
        // Tab Row: Image vs Audio
        TabRow(
            selectedTabIndex = activeTab.ordinal,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
        ) {
            Tab(
                selected = activeTab == MediaType.IMAGE,
                onClick = { onTabSelected(MediaType.IMAGE) },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Image,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Image Verification")
                    }
                }
            )
            Tab(
                selected = activeTab == MediaType.AUDIO,
                onClick = { onTabSelected(MediaType.AUDIO) },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Audiotrack,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Audio Verification")
                    }
                }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Content Area: Picker or Preview
        if (selectedMetadata != null && selectedMetadata.mediaType == activeTab) {
            AnimatedVisibility(
                visible = true,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                when (activeTab) {
                    MediaType.IMAGE -> {
                        ImagePreviewCard(
                            metadata = selectedMetadata,
                            onClearMedia = onClearMedia,
                            onChangeMedia = {
                                imagePickerLauncher.launch(MediaPickerContracts.IMAGE_MIME_TYPES)
                            }
                        )
                    }
                    MediaType.AUDIO -> {
                        AudioPlayerCard(
                            metadata = selectedMetadata,
                            playerState = audioPlayerState,
                            onPlayPauseToggle = onPlayPauseToggle,
                            onSeekTo = onSeekTo,
                            onClearMedia = onClearMedia,
                            onChangeMedia = {
                                audioPickerLauncher.launch(MediaPickerContracts.AUDIO_MIME_TYPES)
                            }
                        )
                    }
                }
            }
        } else {
            // Drop zone / Pick button card
            MediaUploadDropZone(
                mediaType = activeTab,
                onPickMedia = {
                    when (activeTab) {
                        MediaType.IMAGE -> imagePickerLauncher.launch(MediaPickerContracts.IMAGE_MIME_TYPES)
                        MediaType.AUDIO -> audioPickerLauncher.launch(MediaPickerContracts.AUDIO_MIME_TYPES)
                    }
                }
            )
        }
    }
}

@Composable
private fun MediaUploadDropZone(
    mediaType: MediaType,
    onPickMedia: () -> Unit,
    modifier: Modifier = Modifier
) {
    val (title, formatsText, icon) = when (mediaType) {
        MediaType.IMAGE -> Triple(
            "Select or Import Image File",
            "Supported Formats: JPEG, PNG, WEBP",
            Icons.Default.Image
        )
        MediaType.AUDIO -> Triple(
            "Select or Import Audio File",
            "Supported Formats: WAV, MP3, M4A",
            Icons.Default.Audiotrack
        )
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .clickable { onPickMedia() },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 2.dp,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
                    shape = RoundedCornerShape(24.dp)
                )
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CloudUpload,
                        contentDescription = "Upload Icon",
                        modifier = Modifier.size(32.dp),
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = formatsText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val tags = if (mediaType == MediaType.IMAGE) listOf("JPEG", "PNG", "WEBP") else listOf("WAV", "MP3", "M4A")
                    tags.forEach { tag ->
                        FormatPill(label = tag)
                    }
                }
            }
        }
    }
}

@Composable
private fun FormatPill(label: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.6f))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onSecondaryContainer
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun MediaPickerSectionPreview() {
    CyberVerifyTheme {
        MediaPickerSection(
            activeTab = MediaType.IMAGE,
            selectedMetadata = null,
            audioPlayerState = com.cyberverify.util.AudioPlayerState(),
            onTabSelected = {},
            onMediaSelected = {},
            onClearMedia = {},
            onPlayPauseToggle = {},
            onSeekTo = {}
        )
    }
}
