package ai.chench.assidua;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import java.util.List;

/**
 * ViewPager adapter that creates fragments which each display a budget.
 * It will also create one fragment after the last budget, which will be a fragment
 * that will allow the user to make a new budget.
 */
public class BudgetPagerAdapter extends FragmentStatePagerAdapter {
    private static final String TAG = "BudgetPagerAdapter";
    private FragmentManager fragmentManager;

    private List<Budget> budgets;

    public BudgetPagerAdapter(FragmentManager fm, int behaviour, List<Budget> budgets) {
        super(fm, behaviour);
        this.fragmentManager = fm;
        this.budgets = budgets;
    }

    public void setBudgets(List<Budget> budgets) {
        this.budgets = budgets;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        if (position == budgets.size()) {
            Log.d(TAG, "setting position " + position + " to a create budget fragment");
            return new CreateBudgetFragment();
        } else {
            Log.d(TAG, "setting position " + position + " to a display budget fragment");

            DisplayBudgetFragment fragment = new DisplayBudgetFragment();
            Bundle args = new Bundle();
            args.putString(
                    DisplayBudgetFragment.ARGUMENT_BUDGET_UUID,
                    budgets.get(position).getId().toString());

            fragment.setArguments(args);
            return fragment;
        }
    }

    @Override
    public int getCount() {
        return budgets.size() + 1;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        if (position == budgets.size()) {
            return "+";
        } else {
            return budgets.get(position).getName();
        }
    }
}
