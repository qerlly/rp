package pl.agora.radiopogoda.infrastructure.ads

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager

object IABTCFProvider {
    fun provideIABTCF(context: Context): Pair<String?, Int> {
        val preferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val consentString = preferences.getString("IABTCF_TCString", "")
        val gdprApplies = preferences.getInt("IABTCF_gdprApplies", 0)
        return Pair(consentString, gdprApplies)
    }
}