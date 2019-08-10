package ai.chench.assidua.util;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;

import androidx.fragment.app.Fragment;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import ai.chench.assidua.data.Budget;
import ai.chench.assidua.data.Expenditure;

public class CsvBudgetIoUtil {

    private static final String TAG = "CsvBudgetIoUtil";

    // Variables for determining order of CSV inputs:
    private static final int BUDGET_UUID = 0;
    private static final int BUDGET_NAME = 1;
    private static final int BUDGET_BALANCE = 2;

    private static final int EXPENDITURE_UUID = 0;
    private static final int EXPENDITURE_NAME = 1;
    private static final int EXPENDITURE_VALUE = 2;
    private static final int EXPENDITURE_DATE = 3;

    // Formatting string: Fill with expenditure UUID, expenditure name, expenditure date, and expenditure balance

    public static Budget parseBudget(InputStream inputStream) {
        String budgetName;
        String budgetBalance;
        Budget budget;

        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream), 1000);
            CSVReader csvReader = new CSVReader(bufferedReader);
            String[] budgetRecord = csvReader.readNext();

            budgetName = budgetRecord[BUDGET_NAME];
            budgetBalance = budgetRecord[BUDGET_BALANCE];
            UUID budgetId = UUID.fromString(budgetRecord[BUDGET_UUID]);

            List<Expenditure> expenditures = new ArrayList<>();

            String[] expenditureRecord;
            while ((expenditureRecord = csvReader.readNext()) != null) {
                expenditures.add(new Expenditure(
                        expenditureRecord[EXPENDITURE_NAME],
                        new BigDecimal(expenditureRecord[EXPENDITURE_VALUE]),
                        new Date(expenditureRecord[EXPENDITURE_DATE]),
                        budgetId,
                        UUID.fromString(expenditureRecord[EXPENDITURE_UUID])
                ));
                Log.d(TAG, "Added Expenditure number " + expenditures.size());
            }

            budget = new Budget(budgetName, new BigDecimal(budgetBalance), expenditures, budgetId);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return budget;
    }

    public static boolean saveBudget(Budget budget, OutputStream outputStream) {
        Log.d(TAG, "Saving budget " + budget.getName());
        try {
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream), 1000);
            CSVWriter csvWriter = new CSVWriter(bufferedWriter);

            // Write the budget as the first line of the csv
            String[] budgetRecord = new String[3];
            budgetRecord[BUDGET_UUID] = budget.getId().toString();
            budgetRecord[BUDGET_BALANCE] = budget.getBalance().toString();
            budgetRecord[BUDGET_NAME] = budget.getName();

            csvWriter.writeNext(budgetRecord);

            String[] expenditureRecord = new String[4];
            // write each expenditure as the next
            for (Expenditure expenditure : budget.getExpenditures()) {

                expenditureRecord[EXPENDITURE_NAME] = expenditure.getName();
                expenditureRecord[EXPENDITURE_UUID] = expenditure.getId().toString();
                expenditureRecord[EXPENDITURE_VALUE] = expenditure.getValue().toString();
                expenditureRecord[EXPENDITURE_DATE] = expenditure.getDate().toString();

                csvWriter.writeNext(expenditureRecord);
            }

            csvWriter.close();

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    public static final int EXPORT_CSV_REQUEST_CODE = 1215;

    /**
     * Gets a CSV mime type uri using intents. Result will be returned through onActivityResult.
     * @param fragment The fragment which will handle the OnActivityResult
     */
    @TargetApi(19)
    public static void getExportUri(Fragment fragment) {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/comma-separated-values");

        fragment.startActivityForResult(intent, EXPORT_CSV_REQUEST_CODE);
    }


}
