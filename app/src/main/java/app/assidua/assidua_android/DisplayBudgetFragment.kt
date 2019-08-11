package app.assidua.assidua_android

import android.content.res.Configuration
import app.assidua.assidua_android.data.Budget
import app.assidua.assidua_android.data.ExpenditureViewModel
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_display_budget.*
import kotlinx.android.synthetic.main.fragment_display_budget.view.*
import java.util.*

class DisplayBudgetFragment : Fragment() {

    companion object {
        // Used for passing in the budget UUID via arguments to this fragment
        const val ARGUMENT_BUDGET_UUID = "ARGUMENT_BUDGET_UUID"
        private const val TAG = "DisplayBudgetFragment"
    }

    private lateinit var viewModel: ExpenditureViewModel
    private lateinit var adapter: ExpenditureAdapter
    private var budget: Budget? = null
    private lateinit var budgetId: UUID  // Id of the budget this fragment is displaying

    private var headerViewHolder: HeaderViewHolder? = null

    /**
     * @return Whether this fragment is currently displaying an actual budget or not.
     */
    fun hasValidBudget(): Boolean =
        viewModel.getBudget(budgetId) == null


//    private val clickListener = View.OnClickListener { view ->
//        when (view) {
//            addExpenditureButton -> {
//                // We haven't found the budget yet from the database.
//                // This means that everything's going really slowly or
//                // more likely, something's wrong.
//                if (budget == null) { return@OnClickListener }
//
//                var expenditureValue: BigDecimal
//                try {
//                    // Round input to two decimal places
//                    expenditureValue = BigDecimal(expenditureCostEditText.text.toString()).setScale(2, RoundingMode.HALF_DOWN)
//                } catch (e: NumberFormatException) {
//                    // check that the input value is valid
//                    expenditureCostEditText.setError(getString(R.string.error_not_a_number))
//                    return@OnClickListener
//                }
//
//                // Check to see whether entered expenditure was an expense or income.
//                if (incomeSwitch.isChecked) {
//                    // If switch is checked, it's income. No modifications needed
//                } else {
//                    // Otherwise, it's an expense, so multiply by -1.
//                    expenditureValue = expenditureValue.negate()
//                }
//
//                var name = expenditureNameEditText.text.toString().trim()
//
//                budget?.let {
//                    viewModel.addExpenditure(
//                            Expenditure(name, expenditureValue, Date(), budgetId),
//                            it)
//                }
//                expenditureNameEditText.setText("")
//                expenditureCostEditText.setText("")
//            }
//
//            undoExpenditureButton -> {
//                // Try to fill in the previous editTexts with the last expenditure's info
//                budget?.expenditures?.let {
//                    if (it.isNotEmpty()) {
//                        val expenditure = it.get(it.size - 1)
//                        expenditureNameEditText.setText(expenditure.name)
//                        // Set the value as the absolute value, and just change the income/expense switch
//                        // to account for that.
//                        expenditureCostEditText.setText(expenditure.value.abs().toString())
//                        if (expenditure.value > BigDecimal.ZERO) {
//                            // Transaction was income
//                            incomeSwitch.setChecked(true)
//                        } else if (expenditure.value < BigDecimal.ZERO) {
//                            // Transaction was expense.
//                            incomeSwitch.setChecked(false)
//                        }
//                    }
//                }
//                budget?.let { viewModel.deleteLastExpenditure(it) }
//            }
//        }
//    }

    fun setBudgetId(newId: UUID) {
        if (::budgetId.isInitialized) {
//            Log.d(TAG, "Setting budget id from $budgetId to $newId")
        } else {
//            Log.d(TAG, "setBudgetId called with $newId!! original budget ID was not initialized.")
        }
        this.budgetId = newId
        if (::viewModel.isInitialized) {
            updateViews()
        }
    }

    private fun updateViews() {
        // Budget may be null if budget was just deleted. No big deal
        budget = viewModel.getBudget(budgetId)
        if (budget == null) { return }

//        Log.d(TAG, "Budget '${budget?.name}' just had its observer called! Balance: ${budget?.balance}")

        adapter.setBudget(budget)

        // Scroll to the top after insert, as insertion throws off the recyclerview position
        expendituresRecyclerView.scrollToPosition(
                if (adapter.itemCount - 1 < 0) 0
                else adapter.itemCount - 1
        )
////
//        val balance = budget?.balance
//        activity?.runOnUiThread {
//            remainingMoneyTextView.setText(balance?.setScale(2)?.toPlainString()) // Set the number to always have 2 decimal places
//
//            if (balance != null && balance < BigDecimal(0)) {
//                // user has negative balance, set TextView to a certain color.
//                context?.resources?.let {remainingMoneyTextView.setTextColor(it.getColor(R.color.colorNegative) )}
//            } else {
//                // user is positive in balance! Set TextView to a certain color.
//                context?.resources?.let {remainingMoneyTextView.setTextColor(it.getColor(R.color.colorPositive) )}
//            }
//        }
    }

    private fun getUpdatedBudgetID(): UUID{
        return budgetId
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_display_budget, container, false)

        if (null != activity) {
            // Attempt to share the ViewModel between the activity and all its fragments.
            viewModel = ViewModelProviders.of(activity!!).get(ExpenditureViewModel::class.java)
        } else {
            viewModel = ViewModelProviders.of(this).get(ExpenditureViewModel::class.java)
        }

        budgetId = UUID.fromString(arguments!!.getString(ARGUMENT_BUDGET_UUID))
        val budgets = viewModel.budgets

        budgets.observe(this, Observer {
            // TODO figure out how to only observe one budget instead of literally every single one
            updateViews()
        })
//
//        view.addExpenditureButton.setOnClickListener(clickListener)
//        view.undoExpenditureButton.setOnClickListener(clickListener)

        val budgetLiveData = Transformations.switchMap(budgets){
            val budgetLiveData: MutableLiveData<Budget> = MutableLiveData()
            budgetLiveData.value = viewModel.getBudget(budgetId)
            return@switchMap budgetLiveData
        }

        val orientation = resources.configuration.orientation
        if (orientation == Configuration.ORIENTATION_PORTRAIT || orientation
                == Configuration.ORIENTATION_UNDEFINED) {
            // Show the header to as part of the recyclerView if we're in portrait mode.
            adapter = ExpenditureAdapter(true) { parent ->
                // If portrait orientation, put the header viewholder in the recyclerview
                val baseView = LayoutInflater.from(parent.context).inflate(R.layout.header_viewholder, parent, false)
                headerViewHolder = HeaderViewHolder(baseView, viewModel, budgetLiveData)
                return@ExpenditureAdapter headerViewHolder
            }
        } else {
            adapter = ExpenditureAdapter(false, null)

            val container: FrameLayout = view.findViewById(R.id.ui_header_container)
            LayoutInflater.from(context)?.inflate(
                    R.layout.header_viewholder,
                    null,
                    false
            )?.let {
                container.addView(it)
                headerViewHolder = HeaderViewHolder(it, viewModel, budgetLiveData)
            }
        }


        val layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, true)
        layoutManager.stackFromEnd = true

        view.expendituresRecyclerView.adapter = adapter
        view.expendituresRecyclerView.layoutManager = layoutManager
//
//        view.incomeSwitch.setOnCheckedChangeListener { _, checked ->
//            view.switchTextView.text = if (checked) getString(R.string.income) else getString(R.string.expense)
//        }
//
//
//
//        // When the user finishes with the name entry field, automatically insert the expenditure.
//        // Then, switch the focus back to the expenditure value edit text
//        view.expenditureNameEditText.setOnEditorActionListener { textView, actionId, keyEvent ->
//            return@setOnEditorActionListener when(actionId) {
//                EditorInfo.IME_ACTION_GO -> {
//                    view.addExpenditureButton.performClick()
//                    view.expenditureCostEditText.requestFocus()
//                    true
//                }
//                else -> false
//            }
//        }

        return view
    }
}