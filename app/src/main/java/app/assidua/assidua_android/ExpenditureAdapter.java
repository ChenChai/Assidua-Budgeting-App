package app.assidua.assidua_android;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import app.assidua.assidua_android.data.Budget;
import app.assidua.assidua_android.data.Expenditure;

public class ExpenditureAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "ExpenditureAdapter";

    // Item view types
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_EXPENDITURE = 1;

    public final boolean showHeader;

    static class ExpenditureViewHolder extends RecyclerView.ViewHolder{
        public TextView expenditureName;
        public TextView expenditureValue;
        public TextView expenditureDate;

        public ExpenditureViewHolder(View v) {
            super(v);
            expenditureName = v.findViewById(R.id.expenditureName);
            expenditureValue = v.findViewById(R.id.expenditureValue);
            expenditureDate = v.findViewById(R.id.expenditureDate);
        }
    }

    public interface GetHeaderViewHolder {
        /**
         * Constructs a new viewholder to use.
         * @param parent Base view to inflate with
         * @return the new header viewHolder
         */
        RecyclerView.ViewHolder getHeader(@NonNull ViewGroup parent);
    }


    private List<Expenditure> expenditures = new ArrayList<>();
    // Start off with a random budget
    private Budget budget = null;

    private GetHeaderViewHolder getHeaderViewHolder;

    public ExpenditureAdapter(boolean showHeader, GetHeaderViewHolder getHeaderViewHolder) {
        this.getHeaderViewHolder = getHeaderViewHolder;
        this.showHeader = showHeader;
    }

    public void setBudget(Budget budget) {
        this.expenditures = budget.getExpenditures();
        this.budget = budget;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER) {
            return getHeaderViewHolder.getHeader(parent);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.expenditure_viewholder, parent, false);
            return new ExpenditureViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder genericHolder, int position) {
        // The actual position in the expenditure list is different from the
        // passed position if the top position (0) is a header.
//        int actualPosition = showHeader ? position - 1 : position;
        int actualPosition = position;

        if (genericHolder instanceof ExpenditureViewHolder) {
            ExpenditureViewHolder holder = (ExpenditureViewHolder) genericHolder;
            Expenditure expenditure = expenditures.get(actualPosition);
            holder.expenditureName.setText(
                    // Set text to "untitled" if there's no title involved.
                    expenditure.getName().trim().equals("") ?
                            holder.itemView.getContext().getString(R.string.untitled_expenditure) :
                            expenditure.getName());

            // use String.format here to set locale
            holder.expenditureValue.setText(String.format(Locale.CANADA, expenditure.getValue().setScale(2, RoundingMode.HALF_DOWN).toString()));
            holder.expenditureDate.setText(expenditure.getDate().toString());

            holder.itemView.setOnClickListener((View v) -> {
                Log.d(TAG, "Clicked expenditure with UUID: " + expenditures.get(actualPosition).getId().toString());
            });
        } else if (genericHolder instanceof HeaderViewHolder) {
            // TODO inflate?

        }
    }

    @Override
    public int getItemViewType(int position) {
        // put the header in the "last" slot since we're in reverse
        if ((expenditures == null || expenditures.isEmpty() || position == expenditures.size()) && showHeader) {
            return TYPE_HEADER;
        } else {
            return TYPE_EXPENDITURE;
        }
    }

    @Override
    public int getItemCount() {
        if (showHeader) {
            return expenditures.size() + 1;
        } else {
            return expenditures.size();
        }
    }
}
