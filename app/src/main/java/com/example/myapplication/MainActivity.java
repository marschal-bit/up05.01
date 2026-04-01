package com.example.myapplication;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    // Константы для логирования и сохранения состояния
    private static final String TAG = "QuestActivity";
    private static final String KEY_INDEX = "current_index";

    // Поля класса
    private Question[] mQuestionBank;
    private int mCurrentIndex = 0;
    private TextView mQuestionTextView;
    private Button mTrueButton;
    private Button mFalseButton;
    private Button mNextButton;
    private Button mPrevButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "=== onCreate(Bundle) вызван ===");
        Log.d(TAG, "savedInstanceState = " + (savedInstanceState != null ? "не null" : "null"));

        setContentView(R.layout.activity_main);

        // Инициализация массива вопросов
        mQuestionBank = new Question[]{
                new Question(R.string.question_text, true),
                new Question(R.string.question_2, false),
                new Question(R.string.question_3, true),
                new Question(R.string.question_4, true),
                new Question(R.string.question_5, false)
        };
        Log.d(TAG, "Массив вопросов инициализирован. Всего вопросов: " + mQuestionBank.length);

        // Находим виджеты
        mQuestionTextView = findViewById(R.id.question_text_view);
        mTrueButton = findViewById(R.id.true_button);
        mFalseButton = findViewById(R.id.false_button);
        mNextButton = findViewById(R.id.next_button);
        mPrevButton = findViewById(R.id.prev_button);
        Log.d(TAG, "Виджеты инициализированы");

        // Восстановление сохраненного индекса
        if (savedInstanceState != null) {
            mCurrentIndex = savedInstanceState.getInt(KEY_INDEX, 0);
            Log.d(TAG, "Восстановлен индекс вопроса: " + mCurrentIndex);
        } else {
            Log.d(TAG, "Нет сохраненного состояния, индекс по умолчанию: " + mCurrentIndex);
        }

        updateQuestion();
        setListeners();

        Log.d(TAG, "onCreate завершен, текущий вопрос: " + (mCurrentIndex + 1));
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "=== onStart() вызван ===");
        Log.d(TAG, "Activity становится видимой");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "=== onResume() вызван ===");
        Log.d(TAG, "Activity получает фокус и готова к взаимодействию");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "=== onPause() вызван ===");
        Log.d(TAG, "Activity теряет фокус");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "=== onStop() вызван ===");
        Log.d(TAG, "Activity больше не видна");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "=== onDestroy() вызван ===");
        Log.d(TAG, "Activity уничтожается");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(TAG, "=== onSaveInstanceState() вызван ===");
        Log.d(TAG, "Сохраняем текущий индекс: " + mCurrentIndex);
        outState.putInt(KEY_INDEX, mCurrentIndex);
        Log.d(TAG, "Индекс сохранен в Bundle");
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.d(TAG, "=== onRestoreInstanceState() вызван ===");
        if (savedInstanceState != null) {
            int restoredIndex = savedInstanceState.getInt(KEY_INDEX, 0);
            Log.d(TAG, "Восстанавливаем индекс из onRestoreInstanceState: " + restoredIndex);
        }
    }

    private void updateQuestion() {
        int question = mQuestionBank[mCurrentIndex].getTextResId();
        mQuestionTextView.setText(question);
        Log.d(TAG, "Обновлен вопрос. Индекс: " + mCurrentIndex +
                ", ID ресурса: " + question);
    }

    private void setListeners() {
        // Обработчик для кнопки "Да"
        mTrueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Нажата кнопка 'Да' для вопроса " + (mCurrentIndex + 1));
                checkAnswer(true);
            }
        });

        // Обработчик для кнопки "Нет"
        mFalseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Нажата кнопка 'Нет' для вопроса " + (mCurrentIndex + 1));
                checkAnswer(false);
            }
        });

        // Обработчик для кнопки "Далее"
        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int oldIndex = mCurrentIndex;
                mCurrentIndex = (mCurrentIndex + 1) % mQuestionBank.length;
                Log.d(TAG, "Нажата кнопка 'Далее'. Переход с вопроса " +
                        (oldIndex + 1) + " на вопрос " + (mCurrentIndex + 1));
                updateQuestion();
            }
        });

        // Обработчик для кнопки "Назад"
        mPrevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int oldIndex = mCurrentIndex;
                mCurrentIndex = (mCurrentIndex - 1 + mQuestionBank.length) % mQuestionBank.length;
                Log.d(TAG, "Нажата кнопка 'Назад'. Переход с вопроса " +
                        (oldIndex + 1) + " на вопрос " + (mCurrentIndex + 1));
                updateQuestion();
            }
        });

        Log.d(TAG, "Обработчики кнопок установлены");
    }

    // Метод для проверки ответа
    private void checkAnswer(boolean userPressedTrue) {
        boolean answerIsTrue = mQuestionBank[mCurrentIndex].isAnswerTrue();
        int messageResId;

        Log.d(TAG, "Проверка ответа. Пользователь выбрал: " + (userPressedTrue ? "Да" : "Нет") +
                ", Правильный ответ: " + (answerIsTrue ? "Да" : "Нет"));

        if (userPressedTrue == answerIsTrue) {
            messageResId = R.string.correct_toast;
            Log.d(TAG, "Результат: ПРАВИЛЬНО!");
        } else {
            messageResId = R.string.incorrect_toast;
            Log.d(TAG, "Результат: НЕПРАВИЛЬНО!");
        }

        Toast.makeText(this, messageResId, Toast.LENGTH_SHORT).show();
    }
}