package com.whitneygoodey.termtracker.UI;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.whitneygoodey.termtracker.Entities.Assessment;
import com.whitneygoodey.termtracker.R;

import java.util.List;

public class AssessmentAdapter extends RecyclerView.Adapter<AssessmentAdapter.AssessmentViewHolder> {

    private List<Assessment> assessmentList;
    private final Context context;
    private  final LayoutInflater inflater;

    class AssessmentViewHolder extends RecyclerView.ViewHolder {
        private TextView assessmentTitle;
        private TextView startAndEndDates;

        private AssessmentViewHolder(View itemView) {
            super(itemView);
            assessmentTitle = itemView.findViewById(R.id.assessmentListTitleLabel);
            startAndEndDates = itemView.findViewById(R.id.assessmentListStartAndEndDates);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    final Assessment current = assessmentList.get(position);

                    Intent intent = new Intent(context, AssessmentDetails.class);
                    intent.putExtra("assessmentID", current.getID());
                    intent.putExtra("courseID", current.getCourseID());
                    context.startActivity(intent);
                }
            });
        }
    }

    public AssessmentAdapter(Context context) {
        inflater = LayoutInflater.from(context);
        this.context = context;
    }

    @Override
    public AssessmentAdapter.AssessmentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.assessment_list_item, parent, false);
        return new AssessmentViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(AssessmentAdapter.AssessmentViewHolder holder, int position) {
        if (assessmentList != null) {
            Assessment current = assessmentList.get(position);
            String title = current.getTitle();
            String type;
            if (current.getType() == Assessment.Type.OBJECTIVE) {
                type = "OA";
            } else {
                type = "PA";
            }

            //set text in ViewHolder
            holder.assessmentTitle.setText(context.getString(R.string.assessment_list_title, title, type));
            String dates;
            if (current.getStartDate().equals(current.getEndDate())) {
                dates = current.getStartDate();
            } else {
                dates = context.getString(R.string.start_and_end_dates, current.getStartDate(), current.getEndDate());
            }
            holder.startAndEndDates.setText(dates);

        } else {
            holder.assessmentTitle.setText("No assessment title.");
            holder.startAndEndDates.setText("No start or end dates specified.");
        }
    }

    @Override
    public int getItemCount() {
        return assessmentList.size();
    }

    public void setAssessmentList(List<Assessment> assessments) {
        assessmentList = assessments;
        notifyDataSetChanged();
    }
}