package ru.tpu.courses.lab3;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuItemCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import ru.tpu.courses.lab3.adapter.StudentsAdapter;

/**
 * <b>RecyclerView, взаимодействие между экранами. Memory Cache.</b>
 * <p>
 * View, добавленные в {@link android.widget.ScrollView} отрисовываются все разом, при этом выводится
 * пользователю только та часть, до которой доскроллил пользователь. Соответственно, это замедляет
 * работу приложения и в случае с особо большим количеством View может привести к
 * {@link OutOfMemoryError}, краша приложение, т.к. система не может уместить все View в памяти.
 * </p>
 * <p>
 * {@link RecyclerView} - компонент для работы со списками, содержащими большое количество данных,
 * который призван исправить эту проблему. Это точно такой же {@link android.view.ViewGroup}, как и
 * ScrollView, но он содержит только те View, которые сейчас видимы пользователю. Работать с ним
 * намного сложнее, чем с ScrollView, поэтому если известно, что контент на экране статичен и не
 * содержит много элементов, то для простоты лучше воспользоваться ScrollView.
 * </p>
 * <p>
 * Для работы RecyclerView необходимо подключить отдельную библиотеку (см. build.gradle)
 * </p>
 */
public class Lab3Activity extends AppCompatActivity {

    private static final int REQUEST_STUDENT_ADD = 1;

    public static Intent newIntent(@NonNull Context context) {
        return new Intent(context, Lab3Activity.class);
    }

    private final StudentsCache studentsCache = StudentsCache.getInstance();

    private RecyclerView list;
    private FloatingActionButton fab;

    private StudentsAdapter studentsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle(getString(R.string.lab3_title, getClass().getSimpleName()));

        setContentView(R.layout.lab3_activity);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        list = findViewById(android.R.id.list);
        fab = findViewById(R.id.fab);

        /*
        Здесь идёт инициализация RecyclerView. Первое, что необходимо для его работы, это установить
        реализацию LayoutManager-а. Он содержит логику размещения View внутри RecyclerView. Так,
        LinearLayoutManager, который используется ниже, располагает View последовательно, друг за
        другом, по аналогии с LinearLayout-ом. Из альтернатив можно например использовать
        GridLayoutManager, который располагает View в виде таблицы. Необходимость написания своего
        LayoutManager-а возникает очень редко и при этом является весьма сложным процессом, поэтому
        рассматриваться в лабораторной работе не будет.
         */
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        list.setLayoutManager(layoutManager);

        /*
        Следующий ключевой компонент - это RecyclerView.Adapter. В нём описывается вся информация,
        необходимая для заполнения RecyclerView. В примере мы выводим пронумерованный список
        студентов, подробнее о работе адаптера в документации к классу StudentsAdapter.
         */
        list.setAdapter(studentsAdapter = new StudentsAdapter());
        studentsAdapter.setStudents(studentsCache.getStudents());

        studentsAdapter.setLists();

        /*
        При нажатии на кнопку мы переходим на Activity для добавления студента. Обратите внимание,
        что здесь используется метод startActivityForResult. Этот метод позволяет организовывать
        передачу данных обратно от запущенной Activity. В нашем случае, после закрытия AddStudentActivity,
        у нашей Activity будет вызван метод onActivityResult, в котором будут данные, которые мы
        указали перед закрытием AddStudentActivity.
         */
        fab.setOnClickListener(
                v -> startActivityForResult(
                        AddStudentActivity.newIntent(this),
                        REQUEST_STUDENT_ADD
                )
        );
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.lab3_search_student, menu);

        //задание элемента поиска в тулбаре
        MenuItem search = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(search);
        search(searchView);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    //функция, срабатывающая при вводе поискового запроса
    private void search(SearchView searchView) {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            //произошло нажатие "подтвердить"
            @Override
            public boolean onQueryTextSubmit(String query) {
                studentsAdapter.getFilter().filter(query);
                return false;
            }

            //произошло любое изменение текста
            @Override
            public boolean onQueryTextChange(String newText) {
                studentsAdapter.getFilter().filter(newText);
                return true;
            }
        });
    }



    /**
     * Этот метод вызывается после того, как мы ушли с запущенной с помощью метода
     * {@link #startActivityForResult(Intent, int)} Activity.
     *
     * @param requestCode переданный в метод startActivityForResult requestCode, для случаев,
     *                    когда с нашей активитизапускается несколько различных активити. По этому
     *                    идентификатору мы их различаем.
     * @param resultCode  идентификатор, описывающий, с каким результатом запущенная активити была
     *                    завершена. Если пользователь просто закрыл Activity, то по умолчанию будет
     *                    {@link #RESULT_CANCELED}.
     * @param data        даные переданные нам от запущенной Activity.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_STUDENT_ADD && resultCode == RESULT_OK) {
            Student student = AddStudentActivity.getResultStudent(data);

            studentsCache.addStudent(student);

            studentsAdapter.setStudents(studentsCache.getStudents());
            studentsAdapter.notifyItemRangeInserted(studentsAdapter.getItemCount() - 2, 2);
            list.scrollToPosition(studentsAdapter.getItemCount() - 1);
        }
    }
}
