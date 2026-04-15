package ru.goloskokov.bookdepository;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BookLab {

    private static BookLab sBookLab;
    private List<Book> mBooks;

    private BookLab(Context context) {
        mBooks = new ArrayList<>();
        // Generate 100 test books
        for (int i = 0; i < 4; i++) {
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
    public void deleteBook(UUID id) {
        for (int i = 0; i < mBooks.size(); i++) {
            if (mBooks.get(i).getId().equals(id)) {
                mBooks.remove(i);
                break;
            }
        }
    }
    public List<Book> getBooks() {
        return mBooks;
    }

    public Book getBook(UUID id) {
        for (Book book : mBooks) {
            if (book.getId().equals(id)) {
                return book;
            }
        }
        return null;
    }

    // Day 12: Method to add a new book
    public void addBook(Book book) {
        mBooks.add(book);
    }
}
