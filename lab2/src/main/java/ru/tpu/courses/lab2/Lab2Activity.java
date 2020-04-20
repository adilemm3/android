package ru.tpu.courses.lab2;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * <b>Вёрстка UI. Сохранение состояния.</b>
 * <p/>
 * Андроид старается минимизировать объём занимаемой оперативной памяти, поэтому при любом удобном
 * случае выгружает приложение или Activity из памяти. Например, при повороте экрана (если включен автоповорот),
 * весь объект Activity будет пересоздан с 0. Сохранить введенные данные можно несколькими способами:
 * <ul>
 * <li>Сохранить значения в оперативной памяти. Тогда данные переживут пересоздание Activity,
 * но не переживут освобождение приложения из памяти. Этот пример будет рассмотрен в 3ей лабораторной</li>
 * <li>Сохранить значения в файловой системе. Тогда данные переживут освобождение приложения.
 * Взаимодействие с файловой системой может быть длительной операцией и привносит свои проблемы.
 * Рассмотрено оно будет в 4ой лабораторной</li>
 * <li>Используя встроенную в андроид систему сохранения состояния, которую мы используем в
 * этой лабораторной работе. Перед уничтожением Activity будет вызван метод {@link #onSaveInstanceState(Bundle)},
 * в котором можно записать все необходимые значения в переданный объект Bundle. Стоит учитывать,
 * что дополнительно андроид сохраняет в него состояние всех View на экране по их идентификаторам.
 * Так, многие реализации View автоматически сохраняют свои данные, если им указан идентификатор.
 * Например, {@link android.widget.ScrollView} должен сохранять, на сколько отскроллен его
 * контент и восстанавливать его</li>
 * </ul>
 * <p/>
 */
public class Lab2Activity extends AppCompatActivity {

    public static Intent newIntent(@NonNull Context context) {
        return new Intent(context, Lab2Activity.class);
    }

    private static final String STATE_VIEWS_VALUES = "viewsValues";

    private Lab2ViewsContainer lab2ViewsContainer;
    Button addButton;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lab2_activity);

        setTitle(getString(R.string.lab2_title, getClass().getSimpleName()));

        // findViewById - generic метод https://docs.oracle.com/javase/tutorial/extra/generics/methods.html,
        // который автоматически кастит (class cast) View в указанный класс.
        // Тип вью, в которую происходит каст, не проверяется, поэтому если здесь указать View,
        // отличную от View в XML, то приложение крашнется на вызове этого метода.
        lab2ViewsContainer = findViewById(R.id.container);

        addButton = (Button) findViewById(R.id.btn_add_view);
        addButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                EditText textView = (EditText) findViewById(R.id.lab2_edittext);
                lab2ViewsContainer.addValue(textView.getText().toString());
            }
        });
        findViewById(R.id.btn_reset).setOnClickListener(view -> lab2ViewsContainer.initializeValues());
        //findViewById(R.id.btn_add_view).setOnClickListener(view -> lab2ViewsContainer.incrementViews());
    }
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            lab2ViewsContainer.setViewsValues(savedInstanceState.getDoubleArray(STATE_VIEWS_VALUES));
        }
    }
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putDoubleArray(STATE_VIEWS_VALUES, lab2ViewsContainer.getViewsValues());
    }
}
