package ai.chench.assidua

import ai.chench.assidua.data.Budget
import ai.chench.assidua.data.ExpenditureViewModel
import ai.chench.assidua.util.BackPressable
import ai.chench.assidua.util.CsvBudgetIoUtil
import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
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
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import java.io.OutputStream
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
        setPreferencesFromResource(R.xml.budget_settings, rootKey)
        budgetId = UUID.fromString(arguments!!.getString(ARGUMENT_BUDGET_UUID))

        // Try to share a viewModel between this fragment and the parent activity.
        if (activity != null) {
            viewModel = ViewModelProviders.of(activity!!).get(ExpenditureViewModel::class.java)
        } else {
            viewModel = ViewModelProviders.of(this).get(ExpenditureViewModel::class.java)
        }

        budget = viewModel.getBudget(budgetId)

        // set the preference to display the budget name.
        findPreference<EditTextPreference>(getString(R.string.preference_budget_name_key))?.text = budget?.name
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
//        Log.d(TAG, "onCreateView")

        val view = super.onCreateView(inflater, container, savedInstanceState)
        // Set background color to white
        view?.setBackgroundColor(resources.getColor(android.R.color.white))

        // Hide soft keyboard when settings opened
        (context?.getSystemService(Activity.INPUT_METHOD_SERVICE) as? InputMethodManager)?.
                hideSoftInputFromWindow(activity?.currentFocus?.windowToken, 0)
        setupActionBar()


        // Define it here as otherwise, getString() may fail as it requires a context.
        sharedPrefsListener = SharedPreferences.OnSharedPreferenceChangeListener { sharedPrefs: SharedPreferences, key: String->

            when (key) {
                getString(R.string.preference_budget_name_key) -> {
                    // Check if user just changed the title

                    val newTitle = sharedPrefs.getString(key, "")

                    newTitle?.let {
                        // update the name of the budget
                        viewModel.setBudgetName(budgetId, it)
                    }

                    // update the action bar label
                    (activity as? AppCompatActivity)?.supportActionBar?.title = newTitle
                }
            }
        }

        findPreference<Preference>(getString(R.string.preference_budget_export_key))
                ?.onPreferenceClickListener = Preference.OnPreferenceClickListener {

            CsvBudgetIoUtil.getExportUri(budget, this)
            return@OnPreferenceClickListener true
        }


        // Listen for when the user tries to click the delete budget button.
        findPreference<Preference>(getString(R.string.preference_budget_delete_key))
                ?.onPreferenceClickListener = Preference.OnPreferenceClickListener {

            AlertDialog.Builder(context)
                    .setTitle(String.format(getString(R.string.confirm_delete_budget), if (budget?.name != null) budget?.name else ""))
                    .setPositiveButton(getString(R.string.yes_delete_budget)
                    ) { dialogInterface: DialogInterface?, which: Int ->
                        // Delete budget

                        if (viewModel.deleteBudget(budgetId)) {
                            // Success!
                            Toast.makeText(context, getString(R.string.delete_budget_success), Toast.LENGTH_LONG).show()

                            // Close the fragment, since the corresponding budget no longer exists.
                            onBackPressed()
                        } else {
                            // Failed to delete budget!
                            Toast.makeText(context, getString(R.string.delete_budget_failure), Toast.LENGTH_LONG).show()
                        }
                    }
                    .setNegativeButton(getString(R.string.no_delete_budget), null)
                    .show()

            return@OnPreferenceClickListener true
        }

        if (budget == null) {
            Log.e(TAG, "Could not find budget associated with budget ID $budgetId")
            onBackPressed()
        }

        return view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        // User just selected a place to save CSV
        if (requestCode == CsvBudgetIoUtil.EXPORT_CSV_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val uri: Uri? = data?.data?.also{
                // Find the output stream to write to
                val outputStream: OutputStream? = context?.applicationContext?.contentResolver?.openOutputStream(it)

                if (outputStream != null) {
                    // write to that output stream!
                    CsvBudgetIoUtil.saveBudget(budget, outputStream)
                } else {
                    Toast.makeText(context, getString(R.string.export_failed), Toast.LENGTH_LONG).show()
                }
            }

            if (uri == null) {
                Toast.makeText(context, getString(R.string.export_failed), Toast.LENGTH_LONG).show()
            }
        }
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
//        Log.d(TAG, "onResume")

        preferenceManager.sharedPreferences.registerOnSharedPreferenceChangeListener(sharedPrefsListener)
        super.onResume()
    }

    override fun onPause() {
//        Log.d(TAG, "onPause")

        preferenceManager.sharedPreferences.unregisterOnSharedPreferenceChangeListener(sharedPrefsListener)
        super.onPause()
    }


    override fun onBackPressed(): Boolean {
        // Return action bar to its original state.
        resetActionBar()

        // Record whether the fragment was resumed before we call remove
        val wasResumed = isResumed

        // Call remove to remove the fragment. Fragment will now be paused.
        fragmentManager?.beginTransaction()?.remove(this)?.commitAllowingStateLoss()

        // We handled the click for the calling class if we started out resumed.
        // If we were paused already, just let the parent activity handle the back.
        return wasResumed
    }
}