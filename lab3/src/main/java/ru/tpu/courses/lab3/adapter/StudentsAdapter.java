package ru.tpu.courses.lab3.adapter;

import android.view.ViewGroup;
import android.graphics.Color;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.widget.Filter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import ru.tpu.courses.lab3.Student;

/**
 * Задача Адаптера - управление View, которые содержатся в RecyclerView, с учётом его жизненного цикла.
 * Адаптер работает не с View, а с {@link RecyclerView.ViewHolder}. Этот класс содержит не только
 * View, которая будет показана на экране, но и дополнительную информацию, вроде позиции элемента
 * в списке.
 * <p>
 * Сначала мы переопределяем метод {@link #getItemCount()}. В нём необходимо вернуть количество
 * элементов в списке. В нашем случае это количество студентов помноженное на 2, т.к. на каждого
 * студента идёт 2 отдельных View. Одна - с номером студента, другая - с его ФИО.
 * <p>
 * Т.к. у нас идёт 2 разных типа View, то мы переопределяем метод {@link #getItemViewType(int)},
 * в котором должны вернуть номер типа View для переданной в методе позиции списка.
 * <p>
 * В методе {@link #onCreateViewHolder(ViewGroup, int)} мы создаём ViewHolder для
 * соответствующего типа View. Здесь мы производим инфлейт View из XML и ищем нужные нам View
 * в их иерархии.
 * <p>
 * В методе {@link #onBindViewHolder(RecyclerView.ViewHolder, int)} мы описываем заполнение
 * ViewHolder-а данными, соответствующими переданной нам позиции.
 * <p>
 * Когда мы только вызвали {@link RecyclerView#setAdapter(RecyclerView.Adapter)}, согласно алгоритму
 * лэйаута описанному в LayoutManager, RecyclerView начинает вызывать методы адаптера,
 * чтобы расположить столько элементов, сколько помещается на экране.
 * Для каждого такого элемента вызывается сначала getItemViewType, с полученным itemViewType
 * вызывается метод onCreateViewHolder и созданный viewHolder передаётся в onBindViewHolder для заполнения
 * данными. После этого измеряются размеры элемента и добавляются в RecyclerView. Как только мы вышли
 * за пределы размеров RecyclerView, процесс останавливается. При скролле списка вниз, верхние
 * ViewHolder, которые стали не видны, открепляются от RecyclerView и добавляются в
 * {@link RecyclerView.RecycledViewPool}. Снизу же, когда появляется пустое пространство, в
 * RecyclerViewPool ищется ViewHolder для viewType следующего элемента. Если такой найден, то для него
 * вызывается onBindViewHolder и ViewHolder добавляется снизу.
 * <p>
 * Для того, чтобы сказать RecyclerView, что список был обновлён, используются методы
 * {@link #notifyDataSetChanged()}, {@link #notifyItemInserted(int)} и т.д. notifyDataSetChanged
 * обновляет весь список, а остальные методы notify... говорят об изменении конкретного элемента и
 * что с ним произошло, что позволяет делать анимированные изменения в списке. При этом RecyclerView
 * всё также будет работать с теми же закэшированными ViewHolder и не будет пересоздавать все View.
 */
public class StudentsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int TYPE_NUMBER = 0;
    public static final int TYPE_STUDENT = 1;

    private List<Student> students = new ArrayList<>();
    //хранит студентов, прошедших фильтр (этот список и выводится на экран)
    private List<Student> filteredStudents = new ArrayList<>();
    @Override
    @NonNull
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_NUMBER:
                return new NumberHolder(parent);
            case TYPE_STUDENT:
                return new StudentHolder(parent);
        }
        throw new IllegalArgumentException("unknown viewType = " + viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case TYPE_NUMBER:
                NumberHolder numberHolder = (NumberHolder) holder;
                // Высчитыванием номер студента начиная с 1
                numberHolder.bind((position + 1) / 2);
                break;
            case TYPE_STUDENT:
                StudentHolder studentHolder = (StudentHolder) holder;
                Student student = students.get(position / 2);
                studentHolder.student.setText(
                        student.shownName
                );
                break;
        }
    }

    @Override
    public int getItemCount() {
        return students.size() * 2;
    }

    @Override
    public int getItemViewType(int position) {
        return position % 2 == 0 ? TYPE_NUMBER : TYPE_STUDENT;
    }

    public void setStudents(List<Student> students) {
        this.students = students;
        this.filteredStudents = students;
    }

    //задать фильтрованный список как список с пустым фильтром
    public void setLists()
    {
        filteredStudents = students;
    }

    //ищет совпадения в ФИО с запросом
    //возвращает список из чисел, представляющих собой пары "начало-конец соответствия"
    public List<Integer> findWord (String word, String string)
    {
        List<Integer> list = new ArrayList<Integer>();

        for (int i = 0; i < string.length(); i++)
        {
            if ((word.charAt(0) == string.charAt(i))&&( (i+word.length()) < string.length()+1 ))
            {
                int[] positions = findFromPosition(word, string, i);
                list.add(positions[0]);
                list.add(positions[1]);
            }
        }
        return list;
    }

    //проверяет, является ли совпадение первых букв началом всего совпадения
    //если да - возвращает пару чисел - начало и конец совпадения
    //если нет - возвращает две "-1"
    public int[] findFromPosition (String word, String string, int pos)
    {
        boolean flag = true;
        for (int i = 0; i < word.length(); i++)
        {
            if (word.charAt(i) != string.charAt(pos + i))
            {
                flag = false;
                break;
            }
        }

        int[] result = new int[2];
        if (flag == true)
        {
            result[0] = pos;
            result[1] = pos + word.length() - 1;
        }
        else
        {
            result[0] = -1;
            result[1] = -1;
        }
        return result;
    }

    //стереть оформление цветом
    public void clearColor (Student student)
    {
        ForegroundColorSpan[] spans = student.shownName.getSpans(0, student.shownName.length(), ForegroundColorSpan.class);
        for (int i = 0; i < spans.length; i++)
        {
            student.shownName.removeSpan(spans[i]);
        }
    }

    //фильтрация списка студентов по введенному запросу
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                //получение запроса в виде строки
                String charString = charSequence.toString();

                //проверка на пустоту
                if (charString.isEmpty()) {
                    //если запрос пустой - сбросить выделение у всех студентов
                    //и очистить фильтр в списке студентов
                    for (Student student : students) {
                        clearColor(student);
                    }
                    filteredStudents = students;
                } else {
                    //создание списка для отфильтрованных студентов
                    ArrayList<Student> filteredList = new ArrayList<>();
                    for (Student student : students) {
                        //обнаружение студента, который проходит фильтр
                        if (student.getName().toLowerCase().contains(charString)) {
                            //стереть предыдущие выделения
                            clearColor(student);
                            //нахождение интервалов совпадений в ФИО студента с запросом
                            List<Integer> positions = findWord(charString, student.shownName.toString().toLowerCase());
                            //задание выделения цветом для всех совпадений в ФИО
                            for (int i = 0; i < positions.size()-1; i = i + 2)
                            {
                                if (positions.get(i) != -1)
                                    student.shownName.setSpan(new ForegroundColorSpan(Color.RED), positions.get(i),
                                            positions.get(i+1)+1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                            }
                            filteredList.add(student);
                        }
                    }
                    filteredStudents = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = filteredStudents;
                return filterResults;
            }

            //передача отфильтрованного списка студентов с обновлением их вывода на экран
            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                filteredStudents = (ArrayList<Student>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }
}
