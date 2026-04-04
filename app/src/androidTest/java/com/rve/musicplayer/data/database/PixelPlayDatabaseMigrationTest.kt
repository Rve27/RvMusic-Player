package com.theveloper.pixelplay.data.database

import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PixelPlayDatabaseMigrationTest {

    @get:Rule
    val helper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        PixelPlayDatabase::class.java.canonicalName,
        FrameworkSQLiteOpenHelperFactory()
    )

    @After
    fun tearDown() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        for (version in 25..33) {
            context.deleteDatabase(databaseNameFor(version))
        }
        context.deleteDatabase(DB_NAME_33_TO_34)
    }

    @Test
    fun migrateEveryExportedSchemaToLatest() {
        for (startVersion in 25..33) {
            helper.createDatabase(databaseNameFor(startVersion), startVersion).close()

            helper.runMigrationsAndValidate(
                databaseNameFor(startVersion),
                PixelPlayDatabaseVersion.LATEST,
                true,
                *ALL_MIGRATIONS
            ).close()
        }
    }

    @Test
    fun migration33To34AddsArtistsJsonColumnToSongs() {
        helper.createDatabase(DB_NAME_33_TO_34, 33).close()

        helper.runMigrationsAndValidate(
            DB_NAME_33_TO_34,
            PixelPlayDatabaseVersion.LATEST,
            true,
            PixelPlayDatabase.MIGRATION_33_34
        ).let { db ->
            val cursor = db.query("PRAGMA table_info(`songs`)")
            try {
                val nameIndex = cursor.getColumnIndex("name")
                val defaultValueIndex = cursor.getColumnIndex("dflt_value")
                var foundArtistsJson = false
                var defaultValue: String? = null

                while (cursor.moveToNext()) {
                    if (cursor.getString(nameIndex) == "artists_json") {
                        foundArtistsJson = true
                        defaultValue = cursor.getString(defaultValueIndex)
                        break
                    }
                }

                assertTrue(foundArtistsJson)
                assertEquals("NULL", defaultValue)
            } finally {
                cursor.close()
                db.close()
            }
        }
    }

    private fun databaseNameFor(startVersion: Int): String = "migration-test-$startVersion"

    private object PixelPlayDatabaseVersion {
        const val LATEST = 34
    }

    companion object {
        private const val DB_NAME_33_TO_34 = "migration-test-33-to-34"

        private val ALL_MIGRATIONS = arrayOf(
            PixelPlayDatabase.MIGRATION_25_26,
            PixelPlayDatabase.MIGRATION_26_27,
            PixelPlayDatabase.MIGRATION_27_28,
            PixelPlayDatabase.MIGRATION_28_29,
            PixelPlayDatabase.MIGRATION_29_30,
            PixelPlayDatabase.MIGRATION_30_31,
            PixelPlayDatabase.MIGRATION_31_32,
            PixelPlayDatabase.MIGRATION_32_33,
            PixelPlayDatabase.MIGRATION_33_34
        )
    }
}
