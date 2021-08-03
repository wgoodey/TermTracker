package com.whitneygoodey.termtracker.UI;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.whitneygoodey.termtracker.Entities.Term;
import com.whitneygoodey.termtracker.R;

import java.util.List;

public class TermAdapter extends RecyclerView.Adapter<TermAdapter.TermViewHolder> {

    private List<Term> termList;
    private final Context context;
    private final LayoutInflater inflater;

    class TermViewHolder extends RecyclerView.ViewHolder {
        private final TextView termTitle;
        private final TextView startAndEndDates;

        private TermViewHolder(View itemView) {
            super(itemView);
            termTitle = itemView.findViewById(R.id.termListTitleLabel);
            startAndEndDates = itemView.findViewById(R.id.termListStartAndEndDates);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    final Term current = termList.get(position);
                    Intent intent = new Intent(context, TermDetails.class);
                    intent.putExtra("termID", current.getID());
                    context.startActivity(intent);
                }
            });
        }
    }

    public TermAdapter(Context context) {
        inflater = LayoutInflater.from(context);
        this.context = context;
    }

    @Override
    public TermAdapter.TermViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.term_list_item, parent, false);
        return new TermViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(TermAdapter.TermViewHolder holder, int position) {
        if (termList != null) {
            Term current = termList.get(position);
            holder.termTitle.setText(current.getTitle());
            String dates = context.getString(R.string.start_and_end_dates, current.getStartDate(), current.getEndDate());
            holder.startAndEndDates.setText(dates);
        } else {
            holder.termTitle.setText("No term title.");
            holder.startAndEndDates.setText("No start or end dates specified.");
        }
    }

    @Override
    public int getItemCount() {
        return termList.size();
    }

    public void setTermList(List<Term> terms) {
        termList = terms;
        notifyDataSetChanged();
    }
}
