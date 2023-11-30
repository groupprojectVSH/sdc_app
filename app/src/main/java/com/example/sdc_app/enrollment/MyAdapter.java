package com.example.sdc_app.enrollment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sdc_app.R;
import com.example.sdc_app.profile.AddCourse;

import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyCourseHolder> {
    private List<AddCourse> courseList;
    private OnItemClickListener listener;

    public MyAdapter(List<AddCourse> courseList, OnItemClickListener listener) {
        this.courseList = courseList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MyCourseHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.course_layout, parent, false);
        return new MyCourseHolder(view);
    }

    @Override
    public void onBindViewHolder(MyCourseHolder holder, int position) {
        AddCourse course = courseList.get(position);
        holder.courseName.setText(course.getName());
        holder.courseOfferedBy.setText(course.getOfferedBy());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onItemClick(course);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return courseList.size();
    }

    public interface OnItemClickListener {
        void onItemClick(AddCourse item);
    }
}

