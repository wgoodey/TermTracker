package com.whitneygoodey.termtracker.UI;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.whitneygoodey.termtracker.Entities.Course;
import com.whitneygoodey.termtracker.R;

import java.util.List;

public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.CourseViewHolder> {

    private List<Course> courseList;
    private final Context context;
    private final LayoutInflater inflater;

    class CourseViewHolder extends RecyclerView.ViewHolder {
        private final TextView courseTitle;
        private final TextView startAndEndDates;

        private CourseViewHolder(View itemView) {
            super(itemView);
            courseTitle = itemView.findViewById(R.id.courseListTitleLabel);
            startAndEndDates = itemView.findViewById(R.id.courseListStartAndEndDates);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    final Course current = courseList.get(position);
                    Intent intent = new Intent(context, CourseDetails.class);
                    intent.putExtra("courseID", current.getID());
                    context.startActivity(intent);
                }
            });
        }

    }

    public CourseAdapter(Context context) {
        inflater = LayoutInflater.from(context);
        this.context = context;
    }

    @Override
    public CourseAdapter.CourseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.course_list_item, parent, false);
        return new CourseViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(CourseAdapter.CourseViewHolder holder, int position) {
        if (courseList != null) {
            Course current = courseList.get(position);
            holder.courseTitle.setText(current.getTitle());
            String dates = context.getString(R.string.start_and_end_dates, current.getStartDate(), current.getEndDate());
            holder.startAndEndDates.setText(dates);
        } else {
            holder.courseTitle.setText("No course title.");
            holder.startAndEndDates.setText("No start or end dates specified.");
        }
    }

    @Override
    public int getItemCount() {
        return courseList.size();
    }

    public void setCourseList(List<Course> courses) {
        courseList = courses;
        notifyDataSetChanged();
    }

}