package ru.goloskokov.bookdepository.database;

public class BookDbSchema {

    // Внутренний класс для описания таблицы
    public static final class BookTable {
        public static final String NAME = "books";  // Имя таблицы

        // Внутренний класс для названий столбцов
        public static final class Cols {
            public static final String UUID = "uuid";      // Уникальный ID книги
            public static final String TITLE = "title";    // Название книги
            public static final String DATE = "date";      // Дата добавления
            public static final String READED = "readed";  // Прочтена или нет (0/1)
        }
    }
}