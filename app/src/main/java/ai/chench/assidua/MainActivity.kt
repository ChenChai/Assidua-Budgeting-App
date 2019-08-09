package ai.chench.assidua

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager.widget.ViewPager

import android.os.Bundle
import android.util.Log

import com.google.android.material.tabs.TabLayout

import java.util.ArrayList
import java.util.UUID

import ai.chench.assidua.data.Budget
import ai.chench.assidua.data.ExpenditureViewModel
import ai.chench.assidua.util.BackPressable
import androidx.lifecycle.Observer

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val tabLayout = findViewById<TabLayout>(R.id.tabLayout)
        val viewPager = findViewById<ViewPager>(R.id.viewPager)
        val viewModel = ViewModelProviders.of(this).get(ExpenditureViewModel::class.java)

        val adapter = BudgetPagerAdapter(
                supportFragmentManager,
                //FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
                // causes a crash when replacing fragments due to lifecycle issues
                FragmentStatePagerAdapter.BEHAVIOR_SET_USER_VISIBLE_HINT,
                ArrayList())

        viewModel.budgets.observe(this, Observer { budgetMap: Map<UUID, Budget> ->
            Log.d(TAG, "new budget size: " + budgetMap.size + " Notifying adapter!")

            val budgetList = ArrayList(budgetMap.values)

            adapter.setBudgets(budgetList)
            adapter.notifyDataSetChanged()
        })

        viewPager.setAdapter(adapter)
        tabLayout.setupWithViewPager(viewPager)

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
