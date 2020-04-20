package ru.tpu.courses.lab4.add;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.IOException;

import ru.tpu.courses.lab4.Const;
import ru.tpu.courses.lab4.R;
import ru.tpu.courses.lab4.db.Lab4Database;
import ru.tpu.courses.lab4.db.Student;
import ru.tpu.courses.lab4.db.StudentDao;
import ru.tpu.courses.lab4.db.Group;
import ru.tpu.courses.lab4.db.GroupDao;

public class AddStudentActivity extends AppCompatActivity {

    private static final String EXTRA_STUDENT = "student";
    private static final String EXTRA_GROUP_NAME = "group_name";

    private static final int REQUEST_CAMERA = 0;
    private static int oldStudentId;

    public static Intent newIntent(@NonNull Context context) {
        return new Intent(context, AddStudentActivity.class);
    }

    public static Intent newIntent(@NonNull Context context, Student student) {
        Intent ourIntent = newIntent(context);

        ourIntent.putExtra(EXTRA_STUDENT, student);
        oldStudentId = student.id;
        return ourIntent;
    }

    public static Student getResultStudent(@NonNull Intent intent) {
        return intent.getParcelableExtra(EXTRA_STUDENT);
    }

    private StudentDao studentDao;
    private GroupDao groupDao;

    private TempStudentPref studentPref;

    private EditText firstName;
    private EditText secondName;
    private EditText lastName;
    private Spinner groupName;

    private boolean skipSaveToPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lab4_activity_add_student);

        studentPref = new TempStudentPref(this);
        studentDao = Lab4Database.getInstance(this).studentDao();
        groupDao = Lab4Database.getInstance(this).groupDao();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        firstName = findViewById(R.id.first_name);
        secondName = findViewById(R.id.second_name);
        lastName = findViewById(R.id.last_name);
        groupName = findViewById(R.id.group_spinner);

        firstName.setText(studentPref.getFirstName());
        secondName.setText(studentPref.getSecondName());
        lastName.setText(studentPref.getLastName());

        ArrayAdapter<Group> adapter = new ArrayAdapter<Group>(getApplicationContext(), R.layout.lab4_item_group, R.id.group);
        adapter.addAll(groupDao.getAll());
        groupName.setAdapter(adapter);

        Group selectedGroup = groupDao.selectGroupById(studentPref.getGroupId());
        if(selectedGroup != null)
            selectSpinnerItemByValue(groupName, selectedGroup.groupName);

        Bundle arguments = getIntent().getExtras();
        if(arguments!=null){
            Student student = arguments.getParcelable(EXTRA_STUDENT);
            firstName.setText(student.firstName);
            lastName.setText(student.lastName);
            secondName.setText(student.secondName);
        }
    }

    private static void selectSpinnerItemByValue(Spinner spnr, String value) {
        SpinnerAdapter adapter = (SpinnerAdapter) spnr.getAdapter();
        for (int position = 0; position < adapter.getCount(); position++) {
            if(adapter.getItem(position).toString() == value) {
                spnr.setSelection(position);
                return;
            }
        }
    }

    public static int getOldStudentId(){
        return oldStudentId;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (!skipSaveToPrefs) {
            Object selectedGroupName = groupName.getSelectedItem();
            int selectedGroupId = -1;
            if(selectedGroupName != null) {
                selectedGroupId = groupDao.selectByName(selectedGroupName.toString()).id;
            }
            studentPref.set(
                    firstName.getText().toString(),
                    secondName.getText().toString(),
                    lastName.getText().toString(),
                    selectedGroupId
            );
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.lab4_add_student, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        if (item.getItemId() == R.id.action_save) {
            saveStudent();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CAMERA && resultCode == RESULT_OK) {
            try {
                String groupName = data.getStringExtra(EXTRA_GROUP_NAME);
                Group group = groupDao.selectByName(groupName);

            } catch (Exception e) {
                Toast.makeText(this, "Выбранная группа не найдена в базе", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void saveStudent() {
        Student student = new Student(
                firstName.getText().toString(),
                secondName.getText().toString(),
                lastName.getText().toString()
        );

        // Проверяем, что все поля были указаны
        if (TextUtils.isEmpty(student.firstName) ||
                TextUtils.isEmpty(student.secondName) ||
                TextUtils.isEmpty(student.lastName)) {
            Toast.makeText(this, R.string.lab4_error_empty_fields, Toast.LENGTH_LONG).show();
            return;
        }

        Object selectedGroupName = groupName.getSelectedItem();
        if(selectedGroupName != null) {
            Group selectedGroup = groupDao.selectByName(selectedGroupName.toString());
            if(selectedGroup != null)
                student.groupId = selectedGroup.id;
        }

        if (studentDao.count(student.firstName, student.secondName, student.lastName, student.groupId) > 0) {
            Toast.makeText(
                    this,
                    R.string.lab4_error_already_exists,
                    Toast.LENGTH_LONG
            ).show();
            return;
        }



        skipSaveToPrefs = true;

        studentPref.clear();

        Intent data = new Intent();
        data.putExtra(EXTRA_STUDENT, student);
        setResult(RESULT_OK, data);
        finish();
    }
}
