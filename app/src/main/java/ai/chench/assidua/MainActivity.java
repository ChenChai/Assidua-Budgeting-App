package ai.chench.assidua;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.math.BigInteger;
import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private TextView balanceTextView;

    private EditText expenditureCostEditText;
    private EditText expenditureNameEditText;

    private Button addExpenditureButton;
    private Button undoExpenditureButton;

    private RecyclerView recyclerView;
    private ExpenditureAdapter adapter;

    private float balance;

    private SharedPreferences sharedPreferences;
    private ExpenditureViewModel viewModel;

    private View.OnClickListener addClickListener = (View v) -> {
        float expenditureValue;
        BigInteger i = new BigInteger("5");

        try {
            expenditureValue = Float.parseFloat(expenditureCostEditText.getText().toString());
        } catch (NumberFormatException e) {
            expenditureCostEditText.setError(getString(R.string.error_not_a_number));
            return;
        }
        if (Float.isInfinite(expenditureValue) || Float.isNaN(expenditureValue)) {
            expenditureCostEditText.setError(getString(R.string.error_number_too_big));
            return;
        }

        String name = expenditureNameEditText.getText().toString().trim();

        viewModel.addExpenditure(new Expenditure(name.equals("") ? getString(R.string.untitled_expenditure) : name,
                expenditureValue, Calendar.getInstance().getTime()));

        expenditureNameEditText.setText("");
        expenditureCostEditText.setText("");
    };

    private View.OnClickListener undoClickListener = (View v) -> {
        viewModel.undoLastExpenditure();
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ViewPager viewPager = findViewById(R.id.viewPager);
        viewPager.setAdapter(new FragmentStatePagerAdapter(getSupportFragmentManager(), FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
            @NonNull
            @Override
            public Fragment getItem(int position) {
                return new BudgetFragment();
            }

            @Override
            public int getCount() {
                return 1;
            }
        });

    }
}
