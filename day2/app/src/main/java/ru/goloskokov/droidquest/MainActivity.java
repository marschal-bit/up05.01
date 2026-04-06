package ru.goloskokov.droidquest;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_DECEIT = 0;  // Код запроса для DeceitActivity
    private static final String KEY_IS_DECEIVER = "is_deceiver";  // Ключ для сохранения состояния

    private TextView mQuestionTextView;
    private Button mTrueButton;
    private Button mFalseButton;
    private Button mPrevButton;
    private Button mNextButton;
    private Button mDeceitButton;

    private Question[] mQuestionBank = {
            new Question(R.string.question_text, true),
            new Question(R.string.question_2, false),
            new Question(R.string.question_3, true),
            new Question(R.string.question_4, true),
            new Question(R.string.question_5, false)
    };

    private int mCurrentIndex = 0;
    private boolean mIsDeceiver = false;  // Флаг, подсмотрел ли пользователь ответ

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Восстановление состояния при повороте экрана
        if (savedInstanceState != null) {
            mIsDeceiver = savedInstanceState.getBoolean(KEY_IS_DECEIVER, false);
        }

        // Инициализация виджетов
        mQuestionTextView = findViewById(R.id.question_text_view);
        mTrueButton = findViewById(R.id.true_button);
        mFalseButton = findViewById(R.id.false_button);
        mPrevButton = findViewById(R.id.prev_button);
        mNextButton = findViewById(R.id.next_button);
        mDeceitButton = findViewById(R.id.deceit_button);

        updateQuestion();

        // Обработчик кнопки "Да"
        mTrueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswer(true);
            }
        });

        // Обработчик кнопки "Нет"
        mFalseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswer(false);
            }
        });

        // Обработчик кнопки "Назад"
        mPrevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentIndex = (mCurrentIndex - 1 + mQuestionBank.length) % mQuestionBank.length;
                mIsDeceiver = false;  // Сбрасываем флаг при смене вопроса
                updateQuestion();
            }
        });

        // Обработчик кнопки "Далее"
        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentIndex = (mCurrentIndex + 1) % mQuestionBank.length;
                mIsDeceiver = false;  // Сбрасываем флаг при смене вопроса
                updateQuestion();
            }
        });


        mDeceitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean answerIsTrue = mQuestionBank[mCurrentIndex].isAnswerTrue();
                Intent intent = DeceitActivity.newIntent(MainActivity.this, answerIsTrue);
                startActivityForResult(intent, REQUEST_CODE_DECEIT);
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (resultCode != RESULT_OK) {
            return;
        }

        if (requestCode == REQUEST_CODE_DECEIT) {
            if (data != null) {
                mIsDeceiver = DeceitActivity.wasAnswerShown(data);
            }
        }
    }

    // Сохранение состояния при повороте экрана
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(KEY_IS_DECEIVER, mIsDeceiver);
    }

    // Обновление текста вопроса
    private void updateQuestion() {
        int questionId = mQuestionBank[mCurrentIndex].getTextResId();
        mQuestionTextView.setText(questionId);
    }


    private void checkAnswer(boolean userPressedTrue) {
        boolean answerIsTrue = mQuestionBank[mCurrentIndex].isAnswerTrue();

        int messageResId;


        if (mIsDeceiver) {
            messageResId = R.string.judgment_toast;
        } else {
            if (userPressedTrue == answerIsTrue) {
                messageResId = R.string.correct_toast;
            } else {
                messageResId = R.string.incorrect_toast;
            }
        }

        Toast.makeText(this, messageResId, Toast.LENGTH_SHORT).show();
    }
}