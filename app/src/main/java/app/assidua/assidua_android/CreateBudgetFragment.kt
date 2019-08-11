package app.assidua.assidua_android

import app.assidua.assidua_android.data.Budget
import app.assidua.assidua_android.data.ExpenditureViewModel
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.fragment_create_budget.view.*
import java.math.BigDecimal

class CreateBudgetFragment : Fragment() {
    private lateinit var viewModel: ExpenditureViewModel
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = LayoutInflater.from(context).inflate(R.layout.fragment_create_budget, container, false)

        if (null != activity) {
            // Attempt to share the ViewModel between the activity and all its fragments.
            viewModel = ViewModelProviders.of(activity!!).get(ExpenditureViewModel::class.java)
        } else {
            viewModel = ViewModelProviders.of(this).get(ExpenditureViewModel::class.java)
        }

        view.createBudgetButton.setOnClickListener {
            viewModel.addBudget(
                    Budget(view.budgetNameEditText.text.toString(), BigDecimal(0), mutableListOf()))
        }

        return view
    }
}