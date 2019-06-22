package ai.chench.monthlyentertainmentbudget;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
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

        try {
            expenditureValue = Float.parseFloat(expenditureCostEditText.getText().toString());
        } catch (NumberFormatException e) {
            expenditureCostEditText.setError(getString(R.string.error_not_a_number));
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
        viewModel = ViewModelProviders.of(this).get(ExpenditureViewModel.class);

        balanceTextView = findViewById(R.id.remainingMoneyTextView);

        viewModel.getBalance().observe(this, (balance) -> {

            // change the color of the textView based on whether the balance is positive or negative
            if (Math.round(balance) == 0) {
                balanceTextView.setTextColor(getResources().getColor(R.color.colorNeutral));
            } else if (balance > 0) {
                balanceTextView.setTextColor(getResources().getColor(R.color.colorPositive));
            } else {
                balanceTextView.setTextColor(getResources().getColor(R.color.colorNegative));
            }

            balanceTextView.setText(String.format(Locale.CANADA, "$%.2f", balance));

            adapter.notifyDataSetChanged();
        });

        expenditureCostEditText = findViewById(R.id.expenditureCostEditText);
        expenditureNameEditText = findViewById(R.id.expenditureNameEditText);

        addExpenditureButton = findViewById(R.id.addExpenditureButton);
        addExpenditureButton.setOnClickListener(addClickListener);

        undoExpenditureButton = findViewById(R.id.undoExpenditureButton);
        undoExpenditureButton.setOnClickListener(undoClickListener);

        RecyclerView recyclerView = findViewById(R.id.expendituresRecyclerView);
        adapter = new ExpenditureAdapter(viewModel.getExpenditures().getValue());

        viewModel.getExpenditures().observe(this, expenditures -> {
            adapter.notifyDataSetChanged();
            Log.e("CHEN", "Expenditures Changed!");
        });

        recyclerView.setAdapter(adapter);

        // reversed layout so most recent items show up at the top
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);
    }
}
