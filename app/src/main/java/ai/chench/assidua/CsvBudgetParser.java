package ai.chench.assidua;

import android.util.Log;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class CsvBudgetParser {

    private static final String TAG = "CsvBudgetParser";

    // Variables for determining order of CSV inputs:
    private static final int BUDGET_UUID = 0;
    private static final int BUDGET_NAME = 1;
    private static final int BUDGET_BALANCE = 2;

    private static final int EXPENDITURE_UUID = 0;
    private static final int EXPENDITURE_NAME = 1;
    private static final int EXPENDITURE_VALUE = 2;
    private static final int EXPENDITURE_DATE = 3;

    // Formatting string: Fill with expenditure UUID, expenditure name, expenditure date, and expenditure balance

    public static Budget parseBudget(File csv) {
        String budgetName;
        String budgetBalance;
        Budget budget;

        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(csv));
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
            }

            budget = new Budget(budgetName, new BigDecimal(budgetBalance), expenditures, budgetId);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return budget;
    }

    public static boolean saveBudget(Budget budget, File output) {
        Log.d(TAG, "Saving budget " + budget.getName() + " to " + output.getAbsolutePath());
        try {
            PrintWriter printWriter = new PrintWriter(output);
            CSVWriter csvWriter = new CSVWriter(printWriter);

            // Write the budget as the first line of the csv
            String[] budgetRecord = new String[3];
            budgetRecord[BUDGET_UUID] = budget.getId().toString();
            budgetRecord[BUDGET_BALANCE] = budget.getBalance().toString();
            budgetRecord[BUDGET_NAME] = budget.getName();

            csvWriter.writeNext(budgetRecord);

            // write each expenditure as the next
            for (Expenditure expenditure : budget.getExpenditures()) {

                String[] expenditureRecord = new String[4];
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
}
