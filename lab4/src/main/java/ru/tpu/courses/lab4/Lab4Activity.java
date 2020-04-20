package ru.tpu.courses.lab4;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import ru.tpu.courses.lab4.adapter.GroupStudentAdapter;
import ru.tpu.courses.lab4.add.AddStudentActivity;
import ru.tpu.courses.lab4.add.AddGroupActivity;
import ru.tpu.courses.lab4.add.TempActivityPref;
import ru.tpu.courses.lab4.add.TempStudentPref;
import ru.tpu.courses.lab4.db.Lab4Database;
import ru.tpu.courses.lab4.db.Student;
import ru.tpu.courses.lab4.db.StudentDao;
import ru.tpu.courses.lab4.db.Group;
import ru.tpu.courses.lab4.db.GroupDao;

/**
 * <b>Взаимодействие с файловой системой, SQLite</b>
 * <p>
 * В лабораторной работе вместо сохранения студентов в оперативную память будем сохранять их в
 * базу данных SQLite, которая интегрирована в ОС Android. Для более удобного взаимодействия с
 * ней будем использовать ORM библиотеку Room (подключение см. в build.gradle).
 * </p>
 * <p>
 * В {@link AddStudentActivity} введенные поля теперь сохраняются в
 * {@link android.content.SharedPreferences} - удобный способ для хранения небольших данных в
 * файловой системе, а также напрямую поработаем с {@link java.io.File} для работы с фото,
 * полученного с камеры.
 * </p>
 */
public class Lab4Activity extends AppCompatActivity implements GroupStudentAdapter.onStudentClickListener{

    private static final int REQUEST_STUDENT_ADD = 1;
    private static final int REQUEST_STUDENT_EDIT = 2;
    private static final int REQUEST_GROUP_ADD = 3;

    private TempActivityPref activityPref;

    public static Intent newIntent(@NonNull Context context) {
        return new Intent(context, Lab4Activity.class);
    }

    private StudentDao studentDao;
    private GroupDao groupDao;

    private RecyclerView list;
    private FloatingActionButton fab,fabGroup;

    private GroupStudentAdapter groupStudentAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activityPref = new TempActivityPref(this);
        /*
        Получаем объект для выполнения запросов к БД. См. Lab4Database.
         */
        studentDao = Lab4Database.getInstance(this).studentDao();
        groupDao = Lab4Database.getInstance(this).groupDao();

        setTitle(getString(R.string.lab4_title, getClass().getSimpleName()));

        setContentView(R.layout.lab4_activity);
        list = findViewById(android.R.id.list);
        fab = findViewById(R.id.fab);
        fabGroup = findViewById(R.id.fab_group);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        list.setLayoutManager(layoutManager);

        // Точно такой же список, как и в lab3, но с добавленным выводом фото
        list.setAdapter(groupStudentAdapter = new GroupStudentAdapter(this));
        groupStudentAdapter.setData(studentDao.getAll(), groupDao.getAll());

        list.scrollToPosition(activityPref.getPosition());

        fab.setOnClickListener(
                v -> startActivityForResult(
                        AddStudentActivity.newIntent(this),
                        REQUEST_STUDENT_ADD
                )
        );

        fabGroup.setOnClickListener(
                v -> startActivityForResult(
                        AddGroupActivity.newIntent(this),
                        REQUEST_GROUP_ADD
                )
        );
    }

    @Override
    protected void onPause() {
        super.onPause();

        View firstChild = list.getChildAt(0);
        int firstVisiblePosition = list.getChildAdapterPosition(firstChild);
        int offset = firstChild.getTop();

        activityPref.set(firstVisiblePosition);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if(requestCode == REQUEST_GROUP_ADD){
                Group group = AddGroupActivity.getResultGroup(data);

                groupDao.insert(group);
            }else{
                Student student = AddStudentActivity.getResultStudent(data);

                if(requestCode == REQUEST_STUDENT_EDIT){
                    int oldStudentId = AddStudentActivity.getOldStudentId();
                    Student oldStudent = studentDao.selectStudentById(oldStudentId);
                    studentDao.deleteStudent(oldStudent);
                }
                studentDao.insert(student);
            }
            groupStudentAdapter.setData(studentDao.getAll(), groupDao.getAll());
            groupStudentAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onStudentClick(int studentId) {
        Student student = studentDao.selectStudentById(studentId);
        startActivityForResult(
                AddStudentActivity.newIntent(this, student),
                REQUEST_STUDENT_EDIT
        );
    }
}
