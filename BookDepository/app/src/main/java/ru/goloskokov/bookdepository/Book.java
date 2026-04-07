package ru.goloskokov.bookdepository;

import java.util.UUID;

public class Book {

    private UUID mId;
    private String mTitle;

    public Book() {

        mId = UUID.randomUUID();
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
}
