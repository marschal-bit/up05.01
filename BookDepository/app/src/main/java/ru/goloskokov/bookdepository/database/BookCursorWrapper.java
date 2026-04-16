package ru.goloskokov.bookdepository.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import java.util.Date;
import java.util.UUID;

import ru.goloskokov.bookdepository.Book;
import ru.goloskokov.bookdepository.database.BookDbSchema.BookTable;

// CursorWrapper — обёртка над Cursor, которая умеет преобразовывать строку БД в объект Book
public class BookCursorWrapper extends CursorWrapper {

    public BookCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    // Превращает текущую строку курсора в объект Book
    public Book getBook() {
        // Получаем значения из столбцов
        String uuidString = getString(getColumnIndex(BookTable.Cols.UUID));
        String title = getString(getColumnIndex(BookTable.Cols.TITLE));
        long date = getLong(getColumnIndex(BookTable.Cols.DATE));
        int isReaded = getInt(getColumnIndex(BookTable.Cols.READED));

        // Создаём объект Book
        Book book = new Book(UUID.fromString(uuidString));
        book.setTitle(title);
        book.setDate(new Date(date));
        book.setReaded(isReaded != 0);  // 1 → true, 0 → false

        return book;
    }
}