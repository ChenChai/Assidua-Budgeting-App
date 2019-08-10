package ai.assidua.assidua_android

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import app.assidua.assidua_android.R

class BudgetSettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_budget_settings)

        supportActionBar?.title = "Settings"

        // Show the back button in the top corner
        // that will bring the user back.
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        supportFragmentManager.beginTransaction().add(
                R.id.settings,
                BudgetSettingsFragment())
                .commit()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
