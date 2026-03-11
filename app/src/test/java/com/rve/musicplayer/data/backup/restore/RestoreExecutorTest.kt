package com.rve.musicplayer.data.backup.restore

import android.net.Uri
import com.rve.musicplayer.data.backup.format.BackupReader
import com.rve.musicplayer.data.backup.model.BackupManifest
import com.rve.musicplayer.data.backup.model.BackupModuleInfo
import com.rve.musicplayer.data.backup.model.BackupSection
import com.rve.musicplayer.data.backup.model.BackupValidationResult
import com.rve.musicplayer.data.backup.model.DeviceInfo
import com.rve.musicplayer.data.backup.model.ModuleRestoreDetail
import com.rve.musicplayer.data.backup.model.RestorePlan
import com.rve.musicplayer.data.backup.model.RestoreResult
import com.rve.musicplayer.data.backup.module.BackupModuleHandler
import com.rve.musicplayer.data.backup.validation.ValidationPipeline
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertInstanceOf
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class RestoreExecutorTest {

    private val backupReader: BackupReader = mockk()
    private val validationPipeline: ValidationPipeline = mockk()
    private val favoritesHandler: BackupModuleHandler = mockk(relaxed = true)
    private val playbackHistoryHandler: BackupModuleHandler = mockk(relaxed = true)

    private val executor = RestoreExecutor(
        backupReader = backupReader,
        validationPipeline = validationPipeline,
        handlers = mapOf(
            BackupSection.FAVORITES to favoritesHandler,
            BackupSection.PLAYBACK_HISTORY to playbackHistoryHandler
        )
    )

    private val backupUri: Uri = mockk(relaxed = true)

    @Test
    fun `execute rolls back the module that fails during restore`() = runTest {
        val plan = restorePlan(
            selectedModules = setOf(BackupSection.FAVORITES, BackupSection.PLAYBACK_HISTORY)
        )

        coEvery { favoritesHandler.snapshot() } returns "favorites-snapshot"
        coEvery { playbackHistoryHandler.snapshot() } returns "history-snapshot"
        coEvery { backupReader.readAllModulePayloads(backupUri) } returns Result.success(
            mapOf(
                BackupSection.FAVORITES.key to "favorites-payload",
                BackupSection.PLAYBACK_HISTORY.key to "history-payload"
            )
        )
        every {
            validationPipeline.validateModulePayload(any(), any(), any())
        } returns BackupValidationResult.Valid
        coEvery { favoritesHandler.restore("favorites-payload") } returns Unit
        coEvery { playbackHistoryHandler.restore("history-payload") } throws IllegalStateException("boom")
        coEvery { favoritesHandler.rollback("favorites-snapshot") } returns Unit
        coEvery { playbackHistoryHandler.rollback("history-snapshot") } returns Unit

        val result = executor.execute(backupUri, plan) { }

        val failure = assertInstanceOf(RestoreResult.TotalFailure::class.java, result)
        assertTrue(failure.error.contains("Playback History"))
        coVerify(exactly = 1) { favoritesHandler.rollback("favorites-snapshot") }
        coVerify(exactly = 1) { playbackHistoryHandler.rollback("history-snapshot") }
    }

    @Test
    fun `execute fails when a selected module payload is missing`() = runTest {
        val plan = restorePlan(selectedModules = setOf(BackupSection.FAVORITES))

        coEvery { favoritesHandler.snapshot() } returns "favorites-snapshot"
        coEvery { backupReader.readAllModulePayloads(backupUri) } returns Result.success(emptyMap())

        val result = executor.execute(backupUri, plan) { }

        val failure = assertInstanceOf(RestoreResult.TotalFailure::class.java, result)
        assertEquals(
            "Validation failed: Backup is missing the payload for Favorites.",
            failure.error
        )
        coVerify(exactly = 0) { favoritesHandler.restore(any()) }
    }

    private fun restorePlan(selectedModules: Set<BackupSection>): RestorePlan {
        val modules = selectedModules.associate { section ->
            section.key to BackupModuleInfo(
                checksum = "sha256:test",
                entryCount = 1,
                sizeBytes = 32
            )
        }
        return RestorePlan(
            manifest = BackupManifest(
                schemaVersion = BackupManifest.CURRENT_SCHEMA_VERSION,
                appVersion = "test",
                appVersionCode = 1,
                createdAt = 1_700_000_000_000,
                deviceInfo = DeviceInfo(),
                modules = modules
            ),
            backupUri = backupUri.toString(),
            availableModules = selectedModules,
            selectedModules = selectedModules,
            moduleDetails = selectedModules.associateWith {
                ModuleRestoreDetail(
                    entryCount = 1,
                    sizeBytes = 32,
                    willOverwrite = true
                )
            }
        )
    }
}
