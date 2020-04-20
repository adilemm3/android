package ru.tpu.courses.lab2;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.text.TextUtils;

import androidx.annotation.Px;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
/**
 * Простейший пример самописного View. В данном случае мы просто наследуемся от LinearLayout-а и
 * добавляем свою логику, но также есть возможность отнаследоваться от {@link android.view.ViewGroup},
 * если необходимо реализовать контейнер для View полностью с нуля, либо отнаследоваться от {@link android.view.View}.
 * <p/>
 * Здесь можно было бы добавить автоматическое сохранение и восстановление состояния, переопределив методы
 * {@link #onSaveInstanceState()} и {@link #onRestoreInstanceState(Parcelable)}.
 */
public class Lab2ViewsContainer extends LinearLayout {

    private int minViewsCount;
    private List<Double> viewsValues;

    /**
     * Этот конструктор используется при создании View в коде.
     */
    public Lab2ViewsContainer(Context context) {
        this(context, null);
    }

    /**
     * Этот конструктор выдывается при создании View из XML.
     */
    public Lab2ViewsContainer(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * Конструктор, вызывается при инфлейте View, когда у View указан дополнительный стиль.
     * Почитать про стили можно здесь https://developer.android.com/guide/topics/ui/look-and-feel/themes
     *
     * @param attrs атрибуты, указанные в XML. Стандартные android атрибуты обрабатываются внутри родительского класса.
     *              Здесь необходимо только обработать наши атрибуты.
     */
    public Lab2ViewsContainer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        // Свои атрибуты описываются в файле res/values/attrs.xml
        // Эта строчка объединяет возможные применённые к View стили
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.Lab2ViewsContainer, defStyleAttr, 0);

        minViewsCount = a.getInt(R.styleable.Lab2ViewsContainer_lab2_minViewsCount, 0);
        if (minViewsCount < 0) {
            throw new IllegalArgumentException("minViewsCount can't be less than 0");
        }

        // Полученный TypedArray необходимо обязательно очистить.
        a.recycle();

        initializeValues();
    }

    public void initializeValues(){
        setViewsValues(initialValues(minViewsCount));
    }

    private Random rand = new Random();
    private double randomValue(){
        return Math.round(rand.nextDouble()*100)/10.;
    }

    private double[] initialValues(int count){
        double[] initialValues = new double[count];
        for(int i=0;i<count;i++){
            initialValues[i] = randomValue();
        }

        return initialValues;
    }

    public void setViewsValues(double[] values) {
        removeAllViews();
        viewsValues = new ArrayList<>();
        for (int i = 0; i < values.length; i++) {
            viewsValues.add(values[i]);
            addViewValue(values[i], Integer.toString(i+1));
        }
    }

    public double[] getViewsValues() {
        double[] target = new double[viewsValues.size()];
        for (int i = 0; i < target.length; i++) {
            target[i] = viewsValues.get(i);
        }
        return target;
    }

    public void addValue(String text) {
        double val = randomValue();
        viewsValues.add(val);
        addViewValue(val, text);
    }

    public void addViewValue(double viewValue, String name) {
        Lab2View lab2View = new Lab2View(getContext());
        lab2View.setValue(viewValue);
        if (TextUtils.isDigitsOnly(name)) name = "Запись № " + name ;
        lab2View.setTitle(name);
        addView(lab2View);
    }

    /**
     * Метод трансформирует указанные dp в пиксели, используя density экрана.
     */
    @Px
    public int dpToPx(float dp) {
        if (dp == 0) {
            return 0;
        }
        float density = getResources().getDisplayMetrics().density;
        return (int) Math.ceil(density * dp);
    }
}