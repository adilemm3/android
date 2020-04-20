package ru.tpu.courses.lab4.db;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Group extends StudentGroupListItem implements Parcelable {

    @PrimaryKey(autoGenerate = true)
    public int id;
    @NonNull
    @ColumnInfo(name = "group_name")
    public String groupName;

    public Group(
            @NonNull String groupName
    ) {
        this.groupName = groupName;
    }

    protected Group(Parcel in) {
        id = in.readInt();
        groupName = in.readString();
    }

    public static final Creator<Group> CREATOR = new Creator<Group>() {
        @Override
        public Group createFromParcel(Parcel in) {
            return new Group(in);
        }

        @Override
        public Group[] newArray(int size) {
            return new Group[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(groupName);
    }

    @NonNull
    @Override
    public String toString() {
        return groupName;
    }

    @Override
    public int getType() {
        return TYPE_GROUP;
    }
}
