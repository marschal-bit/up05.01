package ru.goloskokov.backgroundnotification;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;

public class BackgroundDemoActivity extends AppCompatActivity {

    // ==================== ТАЙМЕР ====================
    private TextView mTimerText;
    private Button mStartTimerButton;
    private Button mStopTimerButton;
    private Handler mHandler = new Handler();
    private int mSeconds = 0;
    private boolean mTimerRunning = false;
    private Runnable mTimerRunnable;

    // ==================== ФОНОВАЯ ЗАГРУЗКА ====================
    private ProgressBar mProgressBar;
    private TextView mProgressText;
    private TextView mResultText;
    private Button mStartLoadingButton;
    private Button mCancelLoadingButton;
    private LoadingTask mLoadingTask;

    // ==================== ПАРАЛЛЕЛЬНЫЕ ЗАДАЧИ (Задание 3) ====================
    private Button mStartParallelTasksButton;
    private TextView mParallelResultText;
    private ParallelTask[] mParallelTasks;
    private ProgressBar[] mParallelProgressBars;
    private TextView[] mParallelProgressTexts;
    private int completedTasks = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_background_demo);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Фоновые задачи");
        }

        initTimerUI();
        initLoadingUI();
        initParallelTasksUI();
    }

    // ==================== ТАЙМЕР ====================
    private void initTimerUI() {
        mTimerText = findViewById(R.id.timer_text);
        mStartTimerButton = findViewById(R.id.start_timer_button);
        mStopTimerButton = findViewById(R.id.stop_timer_button);

        mStartTimerButton.setOnClickListener(v -> startTimer());
        mStopTimerButton.setOnClickListener(v -> stopTimer());
    }

    private void startTimer() {
        mTimerRunning = true;
        mSeconds = 0;
        mStartTimerButton.setEnabled(false);
        mStopTimerButton.setEnabled(true);
        runTimer();
    }

    private void runTimer() {
        mTimerRunnable = new Runnable() {
            @Override
            public void run() {
                if (mTimerRunning) {
                    mSeconds++;
                    mTimerText.setText(mSeconds + " секунд");
                    mHandler.postDelayed(this, 1000);
                }
            }
        };
        mHandler.post(mTimerRunnable);
    }

    private void stopTimer() {
        mTimerRunning = false;
        mStartTimerButton.setEnabled(true);
        mStopTimerButton.setEnabled(false);
        if (mTimerRunnable != null) {
            mHandler.removeCallbacks(mTimerRunnable);
        }
    }

    // ==================== ФОНОВАЯ ЗАГРУЗКА (AsyncTask) ====================
    private void initLoadingUI() {
        mProgressBar = findViewById(R.id.progress_bar);
        mProgressText = findViewById(R.id.progress_text);
        mResultText = findViewById(R.id.result_text);
        mStartLoadingButton = findViewById(R.id.start_loading_button);
        mCancelLoadingButton = findViewById(R.id.cancel_loading_button);

        mStartLoadingButton.setOnClickListener(v -> startLoading());
        mCancelLoadingButton.setOnClickListener(v -> cancelLoading());
    }

    private void startLoading() {
        mStartLoadingButton.setEnabled(false);
        mCancelLoadingButton.setEnabled(true);
        mResultText.setText("");
        mProgressBar.setProgress(0);
        mProgressText.setText("Прогресс: 0%");

        mLoadingTask = new LoadingTask();
        mLoadingTask.execute(100);
    }

    private void cancelLoading() {
        if (mLoadingTask != null && mLoadingTask.getStatus() == AsyncTask.Status.RUNNING) {
            mLoadingTask.cancel(true);
            mResultText.setText("Загрузка отменена пользователем");
            mStartLoadingButton.setEnabled(true);
            mCancelLoadingButton.setEnabled(false);
            Toast.makeText(this, "Загрузка отменена", Toast.LENGTH_SHORT).show();
        }
    }

    private class LoadingTask extends AsyncTask<Integer, Integer, String> {
        private Random mRandom = new Random();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mResultText.setText("Начинаем загрузку...");
        }

        @Override
        protected String doInBackground(Integer... params) {
            int max = params[0];
            try {
                for (int i = 1; i <= max; i++) {
                    if (isCancelled()) {
                        return "Загрузка прервана";
                    }

                    Thread.sleep(100);

                    if (mRandom.nextInt(10) == 0) {
                        return "Ошибка сети! Не удалось загрузить данные.";
                    }

                    publishProgress(i, max);
                }
            } catch (InterruptedException e) {
                return "Загрузка прервана";
            }
            return "Загрузка завершена! Загружено " + max + " элементов";
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            int current = values[0];
            int total = values[1];
            int percent = (current * 100) / total;
            mProgressBar.setProgress(percent);
            mProgressText.setText("Прогресс: " + percent + "% (" + current + "/" + total + ")");
        }

        @Override
        protected void onPostExecute(String result) {
            mResultText.setText(result);
            mStartLoadingButton.setEnabled(true);
            mCancelLoadingButton.setEnabled(false);

            if (result.startsWith("Ошибка")) {
                Toast.makeText(BackgroundDemoActivity.this, result, Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected void onCancelled(String result) {
            if (result != null) {
                mResultText.setText(result);
            } else {
                mResultText.setText("Загрузка отменена");
            }
            mStartLoadingButton.setEnabled(true);
            mCancelLoadingButton.setEnabled(false);
        }
    }


    private void initParallelTasksUI() {
        mStartParallelTasksButton = findViewById(R.id.start_parallel_tasks_button);
        mParallelResultText = findViewById(R.id.parallel_result_text);

        // Создаём ProgressBar'ы динамически
        mParallelProgressBars = new ProgressBar[3];
        mParallelProgressTexts = new TextView[3];

        for (int i = 0; i < 3; i++) {
            final int taskId = i + 1;

            TextView titleText = new TextView(this);
            titleText.setText("Задача " + taskId + ":");
            titleText.setPadding(0, 16, 0, 0);
            ((android.widget.LinearLayout) mParallelResultText.getParent()).addView(titleText);

            ProgressBar pb = new ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal);
            pb.setLayoutParams(new android.widget.LinearLayout.LayoutParams(
                    android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
                    android.widget.LinearLayout.LayoutParams.WRAP_CONTENT));
            mParallelProgressBars[i] = pb;
            ((android.widget.LinearLayout) mParallelResultText.getParent()).addView(pb);

            TextView pt = new TextView(this);
            pt.setText("Прогресс: 0%");
            pt.setTextAlignment(TextView.TEXT_ALIGNMENT_CENTER);
            pt.setTextSize(12);
            mParallelProgressTexts[i] = pt;
            ((android.widget.LinearLayout) mParallelResultText.getParent()).addView(pt);
        }

        mStartParallelTasksButton.setOnClickListener(v -> startParallelTasks());
    }

    private void startParallelTasks() {
        mStartParallelTasksButton.setEnabled(false);
        completedTasks = 0;
        mParallelResultText.setText("Выполнение 3 параллельных задач...");

        for (int i = 0; i < mParallelProgressBars.length; i++) {
            if (mParallelProgressBars[i] != null) {
                mParallelProgressBars[i].setProgress(0);
            }
            if (mParallelProgressTexts[i] != null) {
                mParallelProgressTexts[i].setText("Прогресс: 0%");
            }
        }

        mParallelTasks = new ParallelTask[3];
        for (int i = 0; i < 3; i++) {
            mParallelTasks[i] = new ParallelTask(i);
            mParallelTasks[i].execute(100);
        }
    }

    private class ParallelTask extends AsyncTask<Integer, Integer, String> {
        private int taskId;
        private Random mRandom = new Random();

        public ParallelTask(int id) {
            this.taskId = id;
        }

        @Override
        protected String doInBackground(Integer... params) {
            int max = params[0];
            try {
                for (int i = 1; i <= max; i++) {
                    if (isCancelled()) {
                        return "Задача " + (taskId + 1) + " прервана";
                    }
                    // Разная скорость выполнения задач
                    Thread.sleep(80 + taskId * 30);
                    publishProgress(i, max);
                }
            } catch (InterruptedException e) {
                return "Задача " + (taskId + 1) + " прервана";
            }
            return "Задача " + (taskId + 1) + " завершена!";
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            int current = values[0];
            int total = values[1];
            int percent = (current * 100) / total;

            if (mParallelProgressBars[taskId] != null) {
                mParallelProgressBars[taskId].setProgress(percent);
            }
            if (mParallelProgressTexts[taskId] != null) {
                mParallelProgressTexts[taskId].setText("Прогресс: " + percent + "%");
            }
        }

        @Override
        protected void onPostExecute(String result) {
            completedTasks++;
            mParallelResultText.setText("Выполнено задач: " + completedTasks + "/3\n" +
                    result + (completedTasks == 3 ? "\nВсе задачи завершены!" : ""));

            if (completedTasks == 3) {
                mStartParallelTasksButton.setEnabled(true);
                Toast.makeText(BackgroundDemoActivity.this,
                        "Все 3 параллельные задачи завершены!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mTimerRunning = false;
        if (mTimerRunnable != null) {
            mHandler.removeCallbacks(mTimerRunnable);
        }
        if (mLoadingTask != null) {
            mLoadingTask.cancel(true);
        }
        if (mParallelTasks != null) {
            for (ParallelTask task : mParallelTasks) {
                if (task != null) {
                    task.cancel(true);
                }
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}