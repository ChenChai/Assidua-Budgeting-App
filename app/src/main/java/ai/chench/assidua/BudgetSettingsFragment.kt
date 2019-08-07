package ai.chench.assidua

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat

class BudgetSettingsFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.budget_settings, rootKey)
    }
}