package com.cyberverify

import com.cyberverify.domain.model.MediaType
import com.cyberverify.util.FileMetadataExtractor
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class FileMetadataExtractorTest {

    @Test
    fun `isAllowedImageType validates image mime types and extensions`() {
        assertTrue(FileMetadataExtractor.isAllowedImageType("image/jpeg", "test.jpg"))
        assertTrue(FileMetadataExtractor.isAllowedImageType("image/png", "sample.png"))
        assertTrue(FileMetadataExtractor.isAllowedImageType("image/webp", "image.webp"))
        assertTrue(FileMetadataExtractor.isAllowedImageType(null, "photo.jpeg"))
        assertFalse(FileMetadataExtractor.isAllowedImageType("video/mp4", "video.mp4"))
        assertFalse(FileMetadataExtractor.isAllowedImageType("application/pdf", "document.pdf"))
    }

    @Test
    fun `isAllowedAudioType validates audio mime types and extensions`() {
        assertTrue(FileMetadataExtractor.isAllowedAudioType("audio/wav", "recording.wav"))
        assertTrue(FileMetadataExtractor.isAllowedAudioType("audio/mpeg", "song.mp3"))
        assertTrue(FileMetadataExtractor.isAllowedAudioType("audio/mp4", "speech.m4a"))
        assertTrue(FileMetadataExtractor.isAllowedAudioType(null, "voice.aac"))
        assertFalse(FileMetadataExtractor.isAllowedAudioType("image/jpeg", "image.jpg"))
        assertFalse(FileMetadataExtractor.isAllowedAudioType("text/plain", "notes.txt"))
    }

    @Test
    fun `determineMediaType identifies media types`() {
        assertEquals(MediaType.IMAGE, FileMetadataExtractor.determineMediaType("image/jpeg", "photo.jpg"))
        assertEquals(MediaType.AUDIO, FileMetadataExtractor.determineMediaType("audio/wav", "voice.wav"))
        assertEquals(null, FileMetadataExtractor.determineMediaType("application/pdf", "doc.pdf"))
    }
}
