package ru.goloskokov.bookdepository;

import android.content.Context;
import java.util.ArrayList;
import java.util.List;

public class BookLab {
    private static BookLab sBookLab;
    private List<Book> mBooks;

    private BookLab(Context context) {
        mBooks = new ArrayList<>();
        // Генерируем 100 тестовых книг
        for (int i = 0; i < 100; i++) {
            Book book = new Book();
            book.setTitle("Книга " + i);
            book.setReaded(i % 2 == 0);
            mBooks.add(book);
        }
    }

    public static BookLab get(Context context) {
        if (sBookLab == null) {
            sBookLab = new BookLab(context);
        }
        return sBookLab;
    }

    public List<Book> getBooks() {
        return mBooks;
    }
}