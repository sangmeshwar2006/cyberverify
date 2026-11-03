package com.cyberverify

import com.cyberverify.data.local.ScanDao
import com.cyberverify.data.local.ScanEntity
import com.cyberverify.data.repository.ScanRepository
import com.cyberverify.domain.model.MediaMetadata
import com.cyberverify.domain.model.MediaType
import com.cyberverify.presentation.viewmodel.ScanAnalysisState
import com.cyberverify.presentation.viewmodel.ScanViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ScanViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var scanDao: FakeScanDao
    private lateinit var repository: ScanRepository
    private lateinit var viewModel: ScanViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        scanDao = FakeScanDao()
        repository = ScanRepository(scanDao)
        viewModel = ScanViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is Idle with IMAGE active tab`() {
        assertEquals(MediaType.IMAGE, viewModel.activeTab.value)
        assertNull(viewModel.selectedMediaUri.value)
        assertNull(viewModel.selectedFileName.value)
        assertNull(viewModel.selectedMetadata.value)
        assertEquals(ScanAnalysisState.Idle, viewModel.analysisState.value)
    }

    @Test
    fun `selectMedia with string and name populates state`() {
        viewModel.selectMedia(
            uri = "content://media/external/images/1",
            fileName = "test_image.jpg",
            mediaType = MediaType.IMAGE
        )

        assertEquals("content://media/external/images/1", viewModel.selectedMediaUri.value)
        assertEquals("test_image.jpg", viewModel.selectedFileName.value)
        assertEquals(MediaType.IMAGE, viewModel.activeTab.value)
        assertNotNull(viewModel.selectedMetadata.value)
        assertEquals(ScanAnalysisState.Idle, viewModel.analysisState.value)
    }

    @Test
    fun `selectMediaWithMetadata populates state and metadata`() {
        val metadata = MediaMetadata(
            uri = "content://media/audio/1",
            fileName = "recording.wav",
            fileSize = 1024L * 500,
            mimeType = "audio/wav",
            mediaType = MediaType.AUDIO,
            durationMs = 60_000L
        )

        viewModel.selectMediaWithMetadata(context = null, metadata = metadata)

        assertEquals("content://media/audio/1", viewModel.selectedMediaUri.value)
        assertEquals("recording.wav", viewModel.selectedFileName.value)
        assertEquals(MediaType.AUDIO, viewModel.activeTab.value)
        assertEquals(metadata, viewModel.selectedMetadata.value)
    }

    @Test
    fun `clearSelectedMedia resets media state and releases audio`() {
        viewModel.selectMedia(
            uri = "content://media/external/images/1",
            fileName = "test_image.jpg",
            mediaType = MediaType.IMAGE
        )

        viewModel.clearSelectedMedia()

        assertNull(viewModel.selectedMediaUri.value)
        assertNull(viewModel.selectedFileName.value)
        assertNull(viewModel.selectedMetadata.value)
        assertEquals(ScanAnalysisState.Idle, viewModel.analysisState.value)
    }

    @Test
    fun `setActiveTab clears media state and changes active tab`() {
        viewModel.selectMedia(
            uri = "content://media/external/images/1",
            fileName = "test_image.jpg",
            mediaType = MediaType.IMAGE
        )

        viewModel.setActiveTab(MediaType.AUDIO)

        assertEquals(MediaType.AUDIO, viewModel.activeTab.value)
        assertNull(viewModel.selectedMediaUri.value)
        assertNull(viewModel.selectedFileName.value)
    }

    @Test
    fun `startAnalysis transitions state from Analyzing to Success in demo mode`() = runTest {
        viewModel.selectMedia(
            uri = "content://media/external/images/1",
            fileName = "deepfake_sample.jpg",
            mediaType = MediaType.IMAGE
        )

        viewModel.startAnalysis()

        // Advance dispatcher to run background coroutine
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(viewModel.analysisState.value is ScanAnalysisState.Success)
        val successState = viewModel.analysisState.value as ScanAnalysisState.Success
        assertEquals("deepfake_sample.jpg", successState.report.fileName)
    }
}

private class FakeScanDao : ScanDao {
    private val scans = mutableMapOf<String, ScanEntity>()
    private val scansFlow = MutableStateFlow<List<ScanEntity>>(emptyList())

    override fun getAllScans(): Flow<List<ScanEntity>> = scansFlow

    override suspend fun getScanById(id: String): ScanEntity? = scans[id]

    override fun getScansByRisk(riskLevel: String): Flow<List<ScanEntity>> {
        return scansFlow.map { list -> list.filter { it.resultClassification == riskLevel } }
    }

    override suspend fun insertScan(scan: ScanEntity) {
        scans[scan.id] = scan
        scansFlow.value = scans.values.toList()
    }

    override suspend fun deleteScanById(id: String) {
        scans.remove(id)
        scansFlow.value = scans.values.toList()
    }

    override suspend fun clearAllScans() {
        scans.clear()
        scansFlow.value = emptyList()
    }
}
