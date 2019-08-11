package app.assidua.assidua_android

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager.widget.ViewPager

import android.os.Bundle

import com.google.android.material.tabs.TabLayout

import java.util.ArrayList

import app.assidua.assidua_android.data.Budget
import app.assidua.assidua_android.data.ExpenditureViewModel
import app.assidua.assidua_android.util.BackPressable
import android.util.Log
import androidx.lifecycle.Observer

class MainActivity : AppCompatActivity() {

    // Number of budgets last observed
    var previousBudgetOrder: MutableList<Pair<String, String>> = mutableListOf()


    lateinit var tabLayout: TabLayout
    lateinit var viewPager: ViewPager
    lateinit var viewModel: ExpenditureViewModel

    lateinit var adapter: BudgetPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tabLayout = findViewById<TabLayout>(R.id.tabLayout)
        viewPager = findViewById<ViewPager>(R.id.viewPager)
        viewModel = ViewModelProviders.of(this).get(ExpenditureViewModel::class.java)

        adapter = BudgetPagerAdapter(
                supportFragmentManager,
                //FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
                // causes a crash when replacing fragments due to lifecycle issues
                FragmentStatePagerAdapter.BEHAVIOR_SET_USER_VISIBLE_HINT,
                ArrayList())


        viewModel.budgets.observe(this, Observer { budgetList:List<Budget> ->

            // List of budgets to display, in the correct displaying order as well.
//            Log.d(TAG, "Budget List: $budgetList")

            var budgetOrderChanged = false

            // Loop through each of the budgets' UUIDs and names and compare them to the ones we have stored.
            // If any of them have changed, then we need to update the UI. Otherwise, we can actually
            // skip updating it, as each fragment will take care of updating of individual expenditures

            // Check if previous budget order is same or different size
            // If budget list size has changed, then we know for sure that the UI needs to be refreshed.
            if (previousBudgetOrder.size != budgetList.size) {
                budgetOrderChanged = true
            } else {
                for (i in 0 until budgetList.size) {
                    // Check that neither the budget order nor name jave changed
                    if (previousBudgetOrder[i].first != budgetList[i].id.toString()
                            || previousBudgetOrder[i].second != budgetList[i].name) {
                        // If either has, refresh the UI.
                        budgetOrderChanged = true
                        break
                    }
                }
            }

            if (budgetOrderChanged) {
//                Log.d(TAG, "Budgets changed!")
                refreshUI(budgetList)
            }

//            Log.d(TAG, "Old List: $previousBudgetOrder")

            // Update the last-seen budgets
            previousBudgetOrder.clear()
            for (budget in budgetList) {
                previousBudgetOrder.add(
                        Pair(budget.id.toString(), budget.name))
            }

//            Log.d(TAG, "New List: $previousBudgetOrder")
        })

        viewPager.setAdapter(adapter)
        tabLayout.setupWithViewPager(viewPager)

    }

    private fun refreshUI(budgetList: List<Budget>) {

        // Allow the adapter to create any necessary budgets
        adapter.setBudgets(budgetList)
        adapter.notifyDataSetChanged()

        // loop from zero to budgetList.size - 1
        for (i in 0 until budgetList.size) {
            val fragment = adapter.getItem(i)

            // Update the each fragment to display the correct budget after changes
            if (fragment is DisplayBudgetFragment) {
                fragment.setBudgetId(budgetList[i].id)
            }
        }
    }

    companion object {
        private val TAG = "MainActivity"
    }

    override fun onBackPressed() {
        // Let the settings fragment be closed upon back press if it is open.
        val settingsFragment = supportFragmentManager.findFragmentById(R.id.settings)

        // Assume the settings fragment didn't handle the press
        // Do this since pressHandled might otherwise end up as null
        var pressHandled = false

        // Send back press to settings fragment
        (settingsFragment as? BackPressable)?.onBackPressed()?.let {
            pressHandled = it
        }

        // If the fragment didn't handle the press, we'll handle it normally instead.
        if (!pressHandled) {
            super.onBackPressed()
        }
    }


    // If the settings fragment needs to use the action bar's
    // navigate up button, we'll simulate a back press with it.
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
