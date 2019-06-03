package ai.chench.monthlyentertainmentbudget;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ExpenditureAdapter extends RecyclerView.Adapter<ExpenditureAdapter.ViewHolder> {
    static class ViewHolder extends RecyclerView.ViewHolder{

        public ViewHolder(View v) {
            super(v);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }
}
