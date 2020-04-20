package ru.tpu.courses.lab4.add;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class TempActivityPref {

    private static final String PREF_POSITION = "position";

    private final SharedPreferences prefs;

    public TempActivityPref(@NonNull Context context) {
        prefs = context.getSharedPreferences("temp_position", Context.MODE_PRIVATE);
    }

    @Nullable
    public int getPosition() {
        return prefs.getInt(PREF_POSITION, 0);
    }


    public void set(
            @Nullable int position
    ) {
        prefs.edit()
                .putInt(PREF_POSITION, position)
                .apply();
    }

    public void clear() {
        prefs.edit().clear().apply();
    }
}
