package ru.tpu.courses.lab4.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import ru.tpu.courses.lab4.R;
import ru.tpu.courses.lab4.db.Student;

public class StudentHolder extends RecyclerView.ViewHolder {

    public final TextView student;
    private int studentId;
    GroupStudentAdapter.onStudentClickListener onStudentClickListener;

    public StudentHolder(ViewGroup parent, GroupStudentAdapter.onStudentClickListener listener) {
        super(LayoutInflater.from(parent.getContext()).inflate(R.layout.lab4_item_student, parent, false));
        student = itemView.findViewById(R.id.student);

        this.onStudentClickListener = listener;

        student.setOnClickListener(view -> {
            onStudentClickListener.onStudentClick(studentId);
        });
    }

    public void setStudent(Student student){
        this.student.setText(
                student.lastName + " " + student.firstName + " " + student.secondName
        );

        studentId = student.id;
    }
}
