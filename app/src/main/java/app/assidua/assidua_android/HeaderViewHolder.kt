package app.assidua.assidua_android

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.lifecycle.LiveData

import androidx.recyclerview.widget.RecyclerView
import app.assidua.assidua_android.data.Budget
import app.assidua.assidua_android.data.Expenditure

import app.assidua.assidua_android.data.ExpenditureViewModel
import kotlinx.android.synthetic.main.fragment_display_budget.view.*
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.*

class HeaderViewHolder(view: View, viewModel: ExpenditureViewModel, budgetLiveData: LiveData<Budget>) : RecyclerView.ViewHolder(view) {
    private val context: Context = view.context
    private val addExpenditureButton: Button = view.findViewById(R.id.addExpenditureButton)
    private val undoExpenditureButton: Button = view.findViewById(R.id.undoExpenditureButton)
    private val expenditureCostEditText: EditText = view.findViewById(R.id.expenditureCostEditText)
    private val expenditureNameEditText: EditText = view.findViewById(R.id.expenditureNameEditText)
    private val remainingMoneyTextView: TextView = view.findViewById(R.id.remainingMoneyTextView)
    private val incomeSwitch: SwitchCompat = view.findViewById(R.id.incomeSwitch)
    private val switchTextView: TextView = view.findViewById(R.id.switchTextView)

    private val settingsButton: ImageView = view.findViewById(R.id.settingsButton)
    private val clickListener = View.OnClickListener { view ->
        when (view) {
            addExpenditureButton -> {
                // We haven't found the budget yet from the database.
                // This means that everything's going really slowly or
                // more likely, something's wrong.
                if (budgetLiveData.value == null) { return@OnClickListener }

                var expenditureValue: BigDecimal
                try {
                    // Round input to two decimal places
                    expenditureValue = BigDecimal(expenditureCostEditText.text.toString()).setScale(2, RoundingMode.HALF_DOWN)
                } catch (e: NumberFormatException) {
                    // check that the input value is valid
                    expenditureCostEditText.setError(context.getString(R.string.error_not_a_number))
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

                budgetLiveData.value?.let {
                    budgetLiveData.value?.let {budget ->
                        viewModel.addExpenditure(
                                Expenditure(name, expenditureValue, Date(), budget.id),
                                it)
                    }

                }
                expenditureNameEditText.setText("")
                expenditureCostEditText.setText("")
            }

            undoExpenditureButton -> {
                // Try to fill in the previous editTexts with the last expenditure's info
                budgetLiveData.value?.expenditures?.let {
                    if (it.isNotEmpty()) {
                        val expenditure = it.get(it.size - 1)
                        expenditureNameEditText.setText(expenditure.name)
                        // Set the value as the absolute value, and just change the income/expense switch
                        // to account for that.
                        expenditureCostEditText.setText(expenditure.value.abs().toString())
                        if (expenditure.value > BigDecimal.ZERO) {
                            // Transaction was income
                            incomeSwitch.setChecked(true)
                        } else if (expenditure.value < BigDecimal.ZERO) {
                            // Transaction was expense.
                            incomeSwitch.setChecked(false)
                        }
                    }
                }
                budgetLiveData.value?.let { viewModel.deleteLastExpenditure(it) }
            }

            settingsButton -> {
                // Launch the settings fragment to allow the user to modify this budget
                val settingsFragment = BudgetSettingsFragment()

                // Put UUID as argument to the fragment
                val args = Bundle()
//            Log.d(TAG, "Launching settings fragment with budget id ${getUpdatedBudgetID()}")
                args.putString(BudgetSettingsFragment.ARGUMENT_BUDGET_UUID, budgetLiveData.value?.id.toString())

                settingsFragment.arguments = args

                (context as? AppCompatActivity)?.supportFragmentManager?.beginTransaction()?.replace(
                        R.id.settings,
                        settingsFragment
                )?.commitAllowingStateLoss()
            }
        }
    }

    init {
        addExpenditureButton.setOnClickListener(clickListener)
        undoExpenditureButton.setOnClickListener(clickListener)
        settingsButton.setOnClickListener(clickListener)

        (view.context as? AppCompatActivity)?.let {activity ->
            budgetLiveData.observe(activity, androidx.lifecycle.Observer { budget ->
                if (budget == null) { return@Observer }
                val balance = budget.balance
                remainingMoneyTextView.setText(balance.setScale(2)?.toPlainString()) // Set the number to always have 2 decimal places

                if (balance < BigDecimal(0)) {
                    // user has negative balance, set TextView to a certain color.
                    context.resources.let {remainingMoneyTextView.setTextColor(it.getColor(R.color.colorNegative) )}
                } else {
                    // user is positive in balance! Set TextView to a certain color.
                    context.resources.let {remainingMoneyTextView.setTextColor(it.getColor(R.color.colorPositive) )}
                }
            })
        }


        incomeSwitch.setOnCheckedChangeListener { _, checked ->
            switchTextView.text = if (checked) context.getString(R.string.income) else context.getString(R.string.expense)
        }



        // When the user finishes with the name entry field, automatically insert the expenditure.
        // Then, switch the focus back to the expenditure value edit text
        expenditureNameEditText.setOnEditorActionListener { textView, actionId, keyEvent ->
            return@setOnEditorActionListener when(actionId) {
                EditorInfo.IME_ACTION_GO -> {
                    addExpenditureButton.performClick()
                    expenditureCostEditText.requestFocus()
                    true
                }
                else -> false
            }
        }

    }

}
