package ru.tpu.courses.lab4.db;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.lang.reflect.Array;

/**
 * <p>
 * Класс, который является входной точкой для описания БД для Room. В Аннотации к классу
 * указывается список сущностей-таблиц (в нашем случае это только класс Student), версия БД
 * (необходима для вызова миграции). Флаг exportSchema отключает выгрузку в json файл схемы БД (в
 * обычном случае его оставляют включённым и этот файл добавляют в репозиторий, для сохранения
 * истории изменения схемы БД).
 * </p>
 * <p>
 * Для всех Data access objects (DAO) (см. {@link StudentDao}) должны быть написаны абстрактные
 * методы, возвращающие их {@link #studentDao()}
 * </p>
 * <p>
 * Библиотека Room использует эти классы за основу для кодогенарации методов работы с БД,
 * используя информацию, которую мы указываем в аннотациях. Когда мы создаём инстанс нашей БД
 * через {@link Room#databaseBuilder(Context, Class, String)} (см. метод {@link #getInstance(Context)})
 * Room возвращает нам инстанс уже сгенерированной реализации БД.
 * </p>
 */
@Database(entities = {Student.class, Group.class}, version = 4, exportSchema = false)
public abstract class Lab4Database extends RoomDatabase {

    private static Lab4Database db;


    public static final Migration MIGRATION_1_2 = new Migration(1,2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE `group` (" +
                    "id INTEGER PRIMARY KEY NOT NULL," +
                    "group_name TEXT NOT NULL)");

            database.execSQL("CREATE TABLE new_Student (" +
                    "id INTEGER PRIMARY KEY NOT NULL," +
                    "first_name TEXT NOT NULL," +
                    "second_name TEXT NOT NULL," +
                    "last_name TEXT NOT NULL," +
                    "group_id INTEGER NOT NULL DEFAULT 0)");

            database.execSQL("INSERT INTO new_Student (id, first_name,second_name,last_name) " +
                    "SELECT id, first_name, second_name, last_name FROM student");
            database.execSQL("DROP TABLE student");
            database.execSQL("ALTER TABLE new_Student RENAME TO student");
        }
    };

    public static final Migration MIGRATION_2_3 = new Migration(2,3) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("UPDATE `group` SET group_name='Без группы' " +
                    "WHERE id=0;");
        }
    };

    public static final Migration MIGRATION_3_4 = new Migration(3,4) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("INSERT INTO `group` (id, group_name)" +
                    "VALUES (0, 'Без группы');");
        }
    };

    @NonNull
    public static Lab4Database getInstance(@NonNull Context context) {
        if (db == null) {
            synchronized (Lab4Database.class) {
                if (db == null) {
                    db = Room.databaseBuilder(
                            context.getApplicationContext(),
                            Lab4Database.class,
                            "lab4_database"
                    )
                            .addMigrations(new Migration[]{MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4})
                            .allowMainThreadQueries()
                            .build();
                }
            }
        }
        return db;
    }

    public abstract StudentDao studentDao();
    public abstract GroupDao groupDao();


}
