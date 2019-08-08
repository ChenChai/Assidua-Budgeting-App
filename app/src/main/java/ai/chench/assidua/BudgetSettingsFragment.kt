package ai.chench.assidua

import ai.chench.assidua.data.Budget
import ai.chench.assidua.data.ExpenditureViewModel
import ai.chench.assidua.util.BackPressable
import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import androidx.preference.PreferenceFragmentCompat
import java.lang.StringBuilder
import java.util.*

class BudgetSettingsFragment : PreferenceFragmentCompat(), BackPressable {
    companion object {
        const val ARGUMENT_BUDGET_UUID = "ARGUMENT_BUDGET_UUID"
    }

    var budget: Budget? = null

    private lateinit var budgetId: UUID
    private lateinit var viewModel: ExpenditureViewModel

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

        if (budget == null) {
            onBackPressed()
        }

        setupActionBar()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)
        // Set background color to white
        view?.setBackgroundColor(resources.getColor(android.R.color.white))

        // Hide soft keyboard when settings opened
        (context?.getSystemService(Activity.INPUT_METHOD_SERVICE) as? InputMethodManager)?.
                hideSoftInputFromWindow(activity?.currentFocus?.windowToken, 0)

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

    override fun onBackPressed(): Boolean {
        // Return action bar to its original state.
        resetActionBar()

        // TODO save information? Or we could save it whenever the user does anything at all
        fragmentManager?.beginTransaction()?.remove(this)?.commitAllowingStateLoss()

        // We handled the click for the calling class, so return true
        return true
    }
}