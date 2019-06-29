package ai.chench.assidua

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.fragment_budget.*

class BudgetFragment : Fragment() {

    private val viewModel: ExpenditureViewModel = ViewModelProviders.of(this).get(ExpenditureViewModel::class.java)
    private val addClickListener: View.OnClickListener = View.OnClickListener { view ->
        var expenditureValue: Float
        try {
            expenditureValue = expenditureCostEditText.text.toString().toFloat()
        } catch (e: NumberFormatException) {

        }
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_budget, container, false)
    }
}