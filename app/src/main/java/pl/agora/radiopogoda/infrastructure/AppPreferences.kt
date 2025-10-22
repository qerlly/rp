package pl.agora.radiopogoda.infrastructure

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore(name = "AppPrefs")

private object PreferencesKeys {
    val IS_FIRST_LAUNCH = booleanPreferencesKey("isFirstLaunch")
    val CITY = stringPreferencesKey("city")
}

private fun Flow<Preferences>.catchIoError(): Flow<Preferences> =
    catch { exception ->
        if (exception is IOException) emit(emptyPreferences()) else throw exception
    }

@Singleton
class AppPreferences @Inject constructor(
    @ApplicationContext private val context: Context
) {

    val isFirstLaunch: Flow<Boolean> = context.dataStore.data
        .catchIoError()
        .map { preferences ->
            preferences[PreferencesKeys.IS_FIRST_LAUNCH] ?: false
        }

    val city: Flow<String?> = context.dataStore.data
        .catchIoError()
        .map { preferences ->
            preferences[PreferencesKeys.CITY]
        }

    suspend fun setFirstLaunch(firstLaunch: Boolean) = withContext(Dispatchers.IO) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.IS_FIRST_LAUNCH] = firstLaunch
        }
    }

    suspend fun setCity(city: String) = withContext(Dispatchers.IO) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.CITY] = city
        }
    }
}