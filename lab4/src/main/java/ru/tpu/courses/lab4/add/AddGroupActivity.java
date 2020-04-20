package ru.tpu.courses.lab4.add;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import ru.tpu.courses.lab4.Const;
import ru.tpu.courses.lab4.R;
import ru.tpu.courses.lab4.db.Lab4Database;
import ru.tpu.courses.lab4.db.Student;
import ru.tpu.courses.lab4.db.StudentDao;
import ru.tpu.courses.lab4.db.Group;
import ru.tpu.courses.lab4.db.GroupDao;

public class AddGroupActivity extends AppCompatActivity {

    private static final String EXTRA_GROUP = "group";

    public static Intent newIntent(@NonNull Context context) {
        return new Intent(context, AddGroupActivity.class);
    }

    public static Group getResultGroup(@NonNull Intent intent) {
        return intent.getParcelableExtra(EXTRA_GROUP);
    }

    private GroupDao groupDao;

    private EditText groupName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lab4_activity_add_group);


        groupDao = Lab4Database.getInstance(this).groupDao();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        groupName = findViewById(R.id.group_name);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.lab4_add_group, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * Этот метод вызывается когда пользователь нажимает на любую из созданных ранее меню.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Если пользователь нажал "назад", то просто закрываем Activity
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        // Если пользователь нажал "Сохранить"
        if (item.getItemId() == R.id.action_save) {
            // Создаём объект группы
            Group group = new Group(
                    groupName.getText().toString()
            );

            // Проверяем, что все поля были указаны
            if (TextUtils.isEmpty(group.groupName)){
                // Класс Toast позволяет показать системное уведомление поверх всего UI
                Toast.makeText(this, R.string.lab4_error_empty_fields, Toast.LENGTH_LONG).show();
                return true;
            }

            // Проверяем, что такой группы в списке нет
            if (groupDao.count(group.groupName) > 0) {
                Toast.makeText(this, R.string.lab4_error_already_exists, Toast.LENGTH_LONG).show();
                return true;
            }

            // Сохраняем Intent с инфорамцией от этой Activity, который будет передан в onActivityResult
            // вызвавшей его Activity.
            Intent data = new Intent();
            // Сохраяем объект группы. Для того, чтобы сохранить объект класса, он должен реализовывать
            // интерфейс Parcelable или Serializable, т.к. Intent передаётся в виде бинарных данных
            data.putExtra(EXTRA_GROUP, group);
            // Указываем resultCode и сам Intent, которые будут переданы вызвавшей нас Activity в методе
            // onActivityResult
            setResult(RESULT_OK, data);
            // Закрываем нашу Activity
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
