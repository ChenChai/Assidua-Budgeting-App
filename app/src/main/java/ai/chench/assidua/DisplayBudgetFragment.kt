package ai.chench.assidua

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_create_budget.view.*
import kotlinx.android.synthetic.main.fragment_display_budget.*
import kotlinx.android.synthetic.main.fragment_display_budget.view.*
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.*
import kotlin.concurrent.thread

class DisplayBudgetFragment : Fragment() {

    companion object {
        // Used for passing in the budget UUID via arguments to this fragment
        public const val ARGUMENT_BUDGET_UUID = "ARGUMENT_BUDGET_UUID"
    }

    private lateinit var viewModel: ExpenditureViewModel
    private lateinit var adapter: ExpenditureAdapter
    private lateinit var budget: LiveData<Budget> // Id of the budget this fragment is displaying
    private lateinit var budgetUUID: UUID

    private var balance = BigDecimal(0)
    private var expenditures: MutableList<Expenditure> = ArrayList()// A most-recent list of expenditures

    private val clickListener = View.OnClickListener { view ->
        when (view) {
            addExpenditureButton -> {
                // We haven't found the budget yet from the database.
                // This means that everything's going really slowly or
                // more likely, something's wrong.
                if (budget.value == null) { return@OnClickListener }

                var expenditureValue: BigDecimal
                try {
                    // Round input to two decimal places
                    expenditureValue = BigDecimal(expenditureCostEditText.text.toString()).setScale(2, RoundingMode.HALF_DOWN)
                } catch (e: NumberFormatException) {
                    // check that the input value is valid
                    expenditureCostEditText.setError(getString(R.string.error_not_a_number))
                    return@OnClickListener
                }

                // Check to see whether entered expenditure was an expense or income.
                if (incomeSwitch.isChecked) {
                    // If switch is checked, it's income. No modifications needed
                } else {
                    // Otherwise, it's an expense, so multiply by -1.
                    expenditureValue = expenditureValue.negate()
                }

                var name = expenditureNameEditText.text.toString().trim()
                name = if (name == "") getString(R.string.untitled_expenditure) else name

                viewModel.addExpenditure(
                        Expenditure(name, expenditureValue, Date(), budgetUUID),
                        budget.value!!)
                expenditureNameEditText.setText("")
                expenditureCostEditText.setText("")
            }

            undoExpenditureButton -> {
                if (expenditures.isNotEmpty() && budget.value != null) {
                    viewModel.deleteLastExpenditure(budget.value!!)
                }
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {


        val view = inflater.inflate(R.layout.fragment_display_budget, container, false)

        if (null != activity) {
            // Attempt to share the ViewModel between the activity and all its fragments.
            viewModel = ViewModelProviders.of(activity!!).get(ExpenditureViewModel::class.java)
        } else {
            viewModel = ViewModelProviders.of(this).get(ExpenditureViewModel::class.java)
        }


        budgetUUID = UUID.fromString(arguments!!.getString(ARGUMENT_BUDGET_UUID))
        budget = viewModel.getBudget(budgetUUID)

        budget.observe(this, Observer {
            // TODO figure out if the budget should actually hold info on
            //  balance, or if we should just recalculate it each time
        })

        view.addExpenditureButton.setOnClickListener(clickListener)
        view.undoExpenditureButton.setOnClickListener(clickListener)

        viewModel.getExpenditures(budgetUUID).observe(this, Observer {
            adapter.setExpenditures(it)

            // update fragment reference to list of expenditures
            expenditures = it.toMutableList()

            // Scroll to the top to see the most recently added transaction
            expendituresRecyclerView.smoothScrollToPosition(
                    if (adapter.itemCount - 1 < 0) 0
                    else adapter.itemCount - 1
            )

            var balance = BigDecimal(0)

            thread(start = true) {
                for (expenditure in expenditures) {
                    balance = balance.add(expenditure.value)
                }

                activity?.runOnUiThread {
                    view.remainingMoneyTextView.setText(balance.setScale(2).toPlainString()) // Set the number to always have 2 decimal places

                    if (balance >= BigDecimal(0)) {
                        context?.resources?.let {view.remainingMoneyTextView.setTextColor(it.getColor(R.color.colorPositive) )}
                    } else {
                        context?.resources?.let {view.remainingMoneyTextView.setTextColor(it.getColor(R.color.colorNegative) )}
                    }
                }
            }
        })

        adapter = ExpenditureAdapter()

        val layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, true)
        layoutManager.stackFromEnd = true

        view.expendituresRecyclerView.adapter = adapter
        view.expendituresRecyclerView.layoutManager = layoutManager

        view.incomeSwitch.setOnCheckedChangeListener { _, checked ->
            view.switchTextView.text = if (checked) getString(R.string.income) else getString(R.string.expense)
        }

        view.settingsButton.setOnClickListener {
            val intent = Intent(context, BudgetSettingsActivity::class.java)
            startActivity(intent)
        }

        // When the user finishes with the name entry field, automatically insert the expenditure.
        // Then, switch the focus back to the expenditure value edit text
        view.expenditureNameEditText.setOnEditorActionListener { textView, actionId, keyEvent ->
            return@setOnEditorActionListener when(actionId) {
                EditorInfo.IME_ACTION_GO -> {
                    view.addExpenditureButton.performClick()
                    view.expenditureCostEditText.requestFocus()
                    true
                }
                else -> false
            }
        }

        return view
    }
}