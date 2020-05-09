package ch.pete.appconfigapp.db

import androidx.lifecycle.LiveData
import androidx.room.*
import ch.pete.appconfigapp.model.Config
import ch.pete.appconfigapp.model.ConfigEntry
import ch.pete.appconfigapp.model.ExecutionResult
import ch.pete.appconfigapp.model.KeyValue

@Dao
interface AppConfigDao {
    @Transaction
    @Query("SELECT * FROM config")
    fun fetchConfigEntries(): LiveData<List<ConfigEntry>>

    @Transaction
    @Query("SELECT * FROM config WHERE config.id = :configId")
    fun fetchConfigEntryById(configId: Long): LiveData<ConfigEntry>

    @Transaction
    @Query("SELECT * FROM execution_result WHERE configId = :configId ORDER BY timestamp DESC")
    fun fetchExecutionResultEntriesByConfigId(configId: Long): LiveData<List<ExecutionResult>>

    @Transaction
    @Query("SELECT * FROM key_value WHERE configId = :configId ORDER BY `key`")
    fun keyValueEntriesByConfigId(configId: Long): LiveData<List<KeyValue>>

    @Transaction
    suspend fun insertConfigEntry(configEntry: ConfigEntry) {
        val configId = insertConfig(configEntry.config)

        // https://issuetracker.google.com/issues/62848977
        configEntry.keyValues.forEach { it.configId = configId }
        configEntry.executionResults.forEach { it.configId = configId }

        insertKeyValues(configEntry.keyValues)
        insertExecutionResult(configEntry.executionResults)
    }

    @Transaction
    suspend fun deleteConfigEntry(configEntry: ConfigEntry) {
        deleteConfig(configEntry.config)
        deleteKeyValues(configEntry.keyValues)
        deleteExecutionResults(configEntry.executionResults)
    }

    @Insert
    suspend fun insertConfig(config: Config): Long

    @Insert
    suspend fun insertKeyValues(keyValues: List<KeyValue>)

    @Insert
    suspend fun insertExecutionResult(executionResults: List<ExecutionResult>)

    @Update
    suspend fun updateConfig(config: Config): Int

    @Update
    suspend fun updateKeyValues(keyValues: List<KeyValue>): Int

    @Update
    suspend fun updateExecutionResult(executionResults: List<ExecutionResult>): Int

    @Delete
    suspend fun deleteConfig(config: Config): Int

    @Delete
    suspend fun deleteKeyValues(keyValues: List<KeyValue>): Int

    @Delete
    suspend fun deleteExecutionResults(executionResults: List<ExecutionResult>): Int
}
