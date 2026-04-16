package ru.goloskokov.bookdepository.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import ru.goloskokov.bookdepository.database.BookDbSchema.BookTable;

public class BookBaseHelper extends SQLiteOpenHelper {

    private static final int VERSION = 1;                    // Версия БД
    private static final String DATABASE_NAME = "bookBase.db"; // Имя файла БД

    public BookBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    // Вызывается 1 раз — когда БД создаётся впервые
    @Override
    public void onCreate(SQLiteDatabase db) {
        // SQL-запрос для создания таблицы
        db.execSQL("create table " + BookTable.NAME + "(" +
                "_id integer primary key autoincrement, " +   // _id обязателен для RecyclerView
                BookTable.Cols.UUID + ", " +
                BookTable.Cols.TITLE + ", " +
                BookTable.Cols.DATE + ", " +
                BookTable.Cols.READED +
                ")"
        );
    }

    // Вызывается при обновлении версии БД
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Простой способ: удалить старую таблицу и создать новую
        // db.execSQL("drop table if exists " + BookTable.NAME);
        // onCreate(db);
    }
}