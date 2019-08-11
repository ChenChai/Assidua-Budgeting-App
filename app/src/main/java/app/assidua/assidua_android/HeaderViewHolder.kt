package app.assidua.assidua_android

import android.content.Context
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Switch
import android.widget.TextView
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
    val context: Context = view.context
    val addExpenditureButton: Button = view.findViewById(R.id.addExpenditureButton)
    val undoExpenditureButton: Button = view.findViewById(R.id.undoExpenditureButton)

    val expenditureCostEditText: EditText = view.findViewById(R.id.expenditureCostEditText)

    val incomeSwitch: SwitchCompat = view.findViewById(R.id.incomeSwitch)
    val expenditureNameEditText: EditText = view.findViewById(R.id.expenditureNameEditText)

    val remainingMoneyTextView: TextView = view.findViewById(R.id.remainingMoneyTextView)

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
        }
    }

    init {
        view.addExpenditureButton.setOnClickListener(clickListener)
        view.undoExpenditureButton.setOnClickListener(clickListener)

        (view.context as? AppCompatActivity)?.let {activity ->
            budgetLiveData.observe(activity, androidx.lifecycle.Observer { budget ->
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

    }

}
