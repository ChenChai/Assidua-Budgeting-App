package ai.chench.assidua;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.util.Log;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import ai.chench.assidua.data.Budget;
import ai.chench.assidua.data.ExpenditureViewModel;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TabLayout tabLayout = findViewById(R.id.tabLayout);
        ViewPager viewPager = findViewById(R.id.viewPager);
        ExpenditureViewModel viewModel = ViewModelProviders.of(this).get(ExpenditureViewModel.class);

        BudgetPagerAdapter adapter = new BudgetPagerAdapter(
                getSupportFragmentManager(),
                //FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
                // causes a crash when replacing fragments due to lifecycle issues
                FragmentStatePagerAdapter.BEHAVIOR_SET_USER_VISIBLE_HINT,
                new ArrayList<>());

        viewModel.getBudgets().observe(this, (Map<UUID, Budget> budgetMap) -> {
            Log.d(TAG, "new budget size: " + budgetMap.size() + " Notifying adapter!");

            List<Budget> budgetList = new ArrayList<>(budgetMap.values());

            adapter.setBudgets(budgetList);
            adapter.notifyDataSetChanged();
        });

        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
    }
}
