package ai.chench.assidua

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_display_budget.*
import kotlinx.android.synthetic.main.fragment_display_budget.view.*
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.*

class DisplayBudgetFragment : Fragment() {

    companion object {
        // Used for passing in the budget UUID via arguments to this fragment
        public const val ARGUMENT_BUDGET = "ARGUMENT_BUDGET"
    }

    private lateinit var viewModel: ExpenditureViewModel
    private lateinit var adapter: ExpenditureAdapter
    private lateinit var budget: Budget // Id of the budget this fragment is displaying

    private val clickListener = View.OnClickListener { view ->
        when (view) {
            addExpenditureButton -> {
                var expenditureValue: BigDecimal
                try {
                    expenditureValue = BigDecimal(expenditureCostEditText.text.toString()).setScale(2, RoundingMode.HALF_DOWN)
                } catch (e: NumberFormatException) {
                    // check that the input value is valid
                    expenditureCostEditText.setError(getString(R.string.error_not_a_number))
                    return@OnClickListener
                }

                var name = expenditureNameEditText.text.toString().trim()
                name = if (name == "") getString(R.string.untitled_expenditure) else name

                viewModel.addExpenditure(
                        Expenditure(name, expenditureValue, Date(), budget.id)
                        , budget)
                expenditureNameEditText.setText("")
                expenditureCostEditText.setText("")
            }

            undoExpenditureButton -> {
                // TODO re-implement undoing expenditures
                Toast.makeText(context, "Feature coming soon!", Toast.LENGTH_LONG).show()
                //viewModel.undoLastExpenditure()
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        // TODO pass a UUID and grab the LiveData<Budget> associated.
        val budgetJSON = arguments!!.getString(ARGUMENT_BUDGET)
        budget = Gson().fromJson(budgetJSON, Budget::class.java)

        val view = inflater.inflate(R.layout.fragment_display_budget, container, false)

        view.remainingMoneyTextView.setText(budget.balance.toPlainString())

        if (null != activity) {
            // Attempt to share the ViewModel between the activity and all its fragments.
            viewModel = ViewModelProviders.of(activity!!).get(ExpenditureViewModel::class.java)
        } else {
            viewModel = ViewModelProviders.of(this).get(ExpenditureViewModel::class.java)
        }

        view.addExpenditureButton.setOnClickListener(clickListener)
        view.undoExpenditureButton.setOnClickListener(clickListener)

//        viewModel.balance.observe(this, Observer { balance ->
//            if (balance > BigDecimal(0)) {
//                remainingMoneyTextView.setTextColor(resources.getColor(R.color.colorPositive));
//            } else {
//                remainingMoneyTextView.setTextColor(resources.getColor(R.color.colorNegative));
//            }
//
//            remainingMoneyTextView.text = String.format(Locale.CANADA, balance.setScale(2, RoundingMode.HALF_DOWN).toString())
//            adapter.notifyDataSetChanged()
//        })

        viewModel.getExpenditures(budget.id).observe(this, Observer {
            adapter.setExpenditures(it)

            // Scroll to the top to see the most recently added transaction
            expendituresRecyclerView.smoothScrollToPosition(
                    if (adapter.itemCount - 1 < 0) 0
                    else adapter.itemCount - 1
            )
        })

        adapter = ExpenditureAdapter()

        val layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, true)
        layoutManager.stackFromEnd = true

        view.expendituresRecyclerView.adapter = adapter
        view.expendituresRecyclerView.layoutManager = layoutManager

        return view
    }
}