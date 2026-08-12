package com.plantain.intelligentagent.data.preferences

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "settings")

object InferModePreferences {

    private val KEY_INFER_MODE = stringPreferencesKey("infer_mode")

    val defaultMode: String = "network"

    fun inferModeFlow(context: Context): Flow<String> {
        return context.dataStore.data.map { prefs ->
            prefs[KEY_INFER_MODE] ?: defaultMode
        }
    }

    suspend fun saveInferMode(context: Context, mode: String) {
        context.dataStore.edit { prefs ->
            prefs[KEY_INFER_MODE] = mode
        }
    }
}
