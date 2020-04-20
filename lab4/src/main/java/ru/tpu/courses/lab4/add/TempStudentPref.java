package ru.tpu.courses.lab4.add;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class TempStudentPref {

    private static final String PREF_FIRST_NAME = "first_name";
    private static final String PREF_SECOND_NAME = "second_name";
    private static final String PREF_LAST_NAME = "last_name";
    private static final String PREF_GROUP_ID = "group_id";

    private final SharedPreferences prefs;

    public TempStudentPref(@NonNull Context context) {
        prefs = context.getSharedPreferences("temp_student", Context.MODE_PRIVATE);
    }

    @Nullable
    public String getFirstName() {
        return prefs.getString(PREF_FIRST_NAME, null);
    }

    @Nullable
    public String getSecondName() {
        return prefs.getString(PREF_SECOND_NAME, null);
    }

    @Nullable
    public String getLastName() {
        return prefs.getString(PREF_LAST_NAME, null);
    }

    @Nullable
    public int getGroupId() {
        return prefs.getInt(PREF_GROUP_ID, -1);
    }

    public void set(
            @Nullable String firstName,
            @Nullable String secondName,
            @Nullable String lastName,
            @Nullable int groupId
    ) {
        prefs.edit()
                .putString(PREF_FIRST_NAME, firstName)
                .putString(PREF_SECOND_NAME, secondName)
                .putString(PREF_LAST_NAME, lastName)
                .putInt(PREF_GROUP_ID, groupId)
                .apply();
    }

    public void clear() {
        prefs.edit().clear().apply();
    }
}
