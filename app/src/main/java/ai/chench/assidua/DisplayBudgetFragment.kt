package ai.chench.assidua

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_display_budget.*
import kotlinx.android.synthetic.main.fragment_display_budget.view.*
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.*

class DisplayBudgetFragment : Fragment() {

    private lateinit var viewModel: ExpenditureViewModel
    private lateinit var adapter: ExpenditureAdapter

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

                viewModel.addExpenditure(Expenditure(name, expenditureValue, Date()))
                expenditureNameEditText.setText("")
                expenditureCostEditText.setText("")
            }

            undoExpenditureButton -> {
                viewModel.undoLastExpenditure()
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

        view.addExpenditureButton.setOnClickListener(clickListener)
        view.undoExpenditureButton.setOnClickListener(clickListener)

        viewModel.balance.observe(this, Observer { balance ->
            if (balance > BigDecimal(0)) {
                remainingMoneyTextView.setTextColor(resources.getColor(R.color.colorPositive));
            } else {
                remainingMoneyTextView.setTextColor(resources.getColor(R.color.colorNegative));
            }

            remainingMoneyTextView.text = String.format(Locale.CANADA, balance.setScale(2, RoundingMode.HALF_DOWN).toString())
            adapter.notifyDataSetChanged()
        })

        viewModel.expenditures.observe(this, Observer {
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