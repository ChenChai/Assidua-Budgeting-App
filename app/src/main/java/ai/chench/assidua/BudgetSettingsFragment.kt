package ai.chench.assidua

import ai.chench.assidua.data.Budget
import ai.chench.assidua.data.ExpenditureViewModel
import ai.chench.assidua.util.BackPressable
import android.app.Activity
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import androidx.preference.EditTextPreference
import androidx.preference.PreferenceFragmentCompat
import java.lang.StringBuilder
import java.util.*

class BudgetSettingsFragment : PreferenceFragmentCompat(), BackPressable {
    companion object {
        const val ARGUMENT_BUDGET_UUID = "ARGUMENT_BUDGET_UUID"
        const val TAG = "BudgetSettingsFragment"
    }

    var budget: Budget? = null

    private lateinit var budgetId: UUID
    private lateinit var viewModel: ExpenditureViewModel
    private lateinit var sharedPrefsListener: SharedPreferences.OnSharedPreferenceChangeListener


    private var originalActionBarTitle: CharSequence? = ""

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        Log.d(TAG, "onCreatePreferences")
        setPreferencesFromResource(R.xml.budget_settings, rootKey)
        budgetId = UUID.fromString(arguments!!.getString(ARGUMENT_BUDGET_UUID))

        // Try to share a viewModel between this fragment and the parent activity.
        if (activity != null) {
            viewModel = ViewModelProviders.of(activity!!).get(ExpenditureViewModel::class.java)
        } else {
            viewModel = ViewModelProviders.of(this).get(ExpenditureViewModel::class.java)
        }

        budget = viewModel.getBudget(budgetId)

        if (budget == null) {
            onBackPressed()
        }

        // set the preference to display the budget name.
        findPreference<EditTextPreference>(getString(R.string.preference_budget_name_key))?.text = budget?.name
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.d(TAG, "onCreateView")

        val view = super.onCreateView(inflater, container, savedInstanceState)
        // Set background color to white
        view?.setBackgroundColor(resources.getColor(android.R.color.white))

        // Hide soft keyboard when settings opened
        (context?.getSystemService(Activity.INPUT_METHOD_SERVICE) as? InputMethodManager)?.
                hideSoftInputFromWindow(activity?.currentFocus?.windowToken, 0)
        setupActionBar()


        // Define it here as otherwise, getString() may fail as it requires a context.
        sharedPrefsListener = SharedPreferences.OnSharedPreferenceChangeListener { sharedPrefs: SharedPreferences, key: String->
            // Check if user just changed the title
            if (key == getString(R.string.preference_budget_name_key)) {
                val newTitle = sharedPrefs.getString(key, "")
                Toast.makeText(context, "Changed budget_name to $newTitle", Toast.LENGTH_LONG).show()

                (activity as? AppCompatActivity)?.supportActionBar?.title = newTitle
            }
        }

        return view
    }

    private fun setupActionBar() {
        // Safe cast to AppCompatActivity to access action bar
        (activity as? AppCompatActivity)?.supportActionBar?.apply {
            // Enable the back button on the action bar
            setDisplayShowHomeEnabled(true)
            setDisplayHomeAsUpEnabled(true)

            // convert title to string
            originalActionBarTitle = title
            title = budget?.name
        }
    }

    private fun resetActionBar() {
        (activity as? AppCompatActivity)?.supportActionBar?.apply {
            setDisplayShowHomeEnabled(false)
            setDisplayHomeAsUpEnabled(false)

            title = originalActionBarTitle
        }
    }


    override fun onResume() {
        Log.d(TAG, "onResume")

        preferenceManager.sharedPreferences.registerOnSharedPreferenceChangeListener(sharedPrefsListener)
        super.onResume()
    }

    override fun onPause() {
        Log.d(TAG, "onPause")

        preferenceManager.sharedPreferences.unregisterOnSharedPreferenceChangeListener(sharedPrefsListener)
        super.onPause()
    }


    override fun onBackPressed(): Boolean {
        // Return action bar to its original state.
        resetActionBar()

        // TODO save information? Or we could save it whenever the user does anything at all
        fragmentManager?.beginTransaction()?.remove(this)?.commitAllowingStateLoss()

        // We handled the click for the calling class, so return true
        return true
    }
}