package ai.chench.assidua

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_create_budget.*
import kotlinx.android.synthetic.main.fragment_create_budget.view.*

class CreateBudgetFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = LayoutInflater.from(context).inflate(R.layout.fragment_create_budget, container, false)

        view.createBudgetButton.setOnClickListener {
            Toast.makeText(context, "hi", Toast.LENGTH_LONG).show()
        }

        return view
    }
}