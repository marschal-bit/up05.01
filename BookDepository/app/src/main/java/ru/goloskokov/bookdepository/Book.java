package ru.goloskokov.bookdepository;

import java.util.Date;
import java.util.UUID;

public class Book {
    private UUID mId;
    private String mTitle;
    private Date mDate;
    private boolean mReaded;

    // Конструктор для новых книг (создаёт случайный UUID)
    public Book() {
        this(UUID.randomUUID());
    }

    // Конструктор для книг из БД (принимает существующий UUID)
    public Book(UUID uuid) {
        mId = uuid;
        mDate = new Date();
        mTitle = "";
    }

    public UUID getId() {
        return mId;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public Date getDate() {
        return mDate;
    }

    public void setDate(Date date) {
        mDate = date;
    }

    public boolean isReaded() {
        return mReaded;
    }

    public void setReaded(boolean readed) {
        mReaded = readed;
    }

    public String getPhotoFilename() {
        return "IMG_" + getId().toString() + ".jpg";
    }
}