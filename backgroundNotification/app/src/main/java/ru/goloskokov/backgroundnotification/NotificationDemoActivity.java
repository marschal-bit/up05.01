package ru.goloskokov.backgroundnotification;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

public class NotificationDemoActivity extends AppCompatActivity {

    private static final String CHANNEL_ID = "demo_channel";
    private static final String CHANNEL_NAME = "Демонстрационный канал";

    // ЗАДАНИЕ 3: Каналы для группировки
    private static final String GROUP_CHANNEL_ID = "group_channel";
    private static final String GROUP_CHANNEL_NAME = "Групповые уведомления";
    private static final String GROUP_KEY = "my_notification_group";

    private static final int NOTIFICATION_ID = 1001;
    private static final int REQUEST_CODE_POST_NOTIFICATIONS = 100;

    private NotificationManager mNotificationManager;
    private Handler mHandler = new Handler();
    private boolean notificationsEnabled = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_demo);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Уведомления");
        }

        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        createNotificationChannels();
        requestNotificationPermission();
        handleNotificationAction();
        initButtons();
    }

    private void createNotificationChannels() {
        // Основной канал
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Канал для демонстрации уведомлений");
            channel.enableVibration(true);
            channel.enableLights(true);
            mNotificationManager.createNotificationChannel(channel);
        }

        // ЗАДАНИЕ 3: Канал для групповых уведомлений
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel groupChannel = new NotificationChannel(
                    GROUP_CHANNEL_ID,
                    GROUP_CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            groupChannel.setDescription("Канал для группировки уведомлений");
            mNotificationManager.createNotificationChannel(groupChannel);
        }
    }

    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.POST_NOTIFICATIONS},
                        REQUEST_CODE_POST_NOTIFICATIONS);
            } else {
                notificationsEnabled = true;
            }
        } else {
            notificationsEnabled = true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_POST_NOTIFICATIONS) {
            notificationsEnabled = grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED;
            Toast.makeText(this, notificationsEnabled ? "Разрешение получено" : "Разрешение не получено", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleNotificationAction() {
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("action")) {
            String action = intent.getStringExtra("action");
            if ("OK".equals(action)) {
                Toast.makeText(this, "Нажата кнопка OK", Toast.LENGTH_SHORT).show();
            } else if ("CANCEL".equals(action)) {
                Toast.makeText(this, "Уведомление закрыто", Toast.LENGTH_SHORT).show();
                mNotificationManager.cancel(NOTIFICATION_ID);
            }
        }
    }

    private void initButtons() {
        Button simpleButton = findViewById(R.id.simple_notification_button);
        Button actionButton = findViewById(R.id.action_notification_button);
        Button progressButton = findViewById(R.id.progress_notification_button);
        Button bigTextButton = findViewById(R.id.big_text_notification_button);
        Button bigPictureButton = findViewById(R.id.big_picture_notification_button);
        Button groupButton = findViewById(R.id.group_notification_button);
        Button cancelButton = findViewById(R.id.cancel_notification_button);

        simpleButton.setOnClickListener(v -> checkAndShowNotification(() -> showSimpleNotification()));
        actionButton.setOnClickListener(v -> checkAndShowNotification(() -> showActionNotification()));
        progressButton.setOnClickListener(v -> checkAndShowNotification(() -> showProgressNotification()));
        bigTextButton.setOnClickListener(v -> checkAndShowNotification(() -> showBigTextNotification()));
        bigPictureButton.setOnClickListener(v -> checkAndShowNotification(() -> showBigPictureNotification()));
        groupButton.setOnClickListener(v -> checkAndShowNotification(() -> showGroupedNotifications()));
        cancelButton.setOnClickListener(v -> {
            mNotificationManager.cancel(NOTIFICATION_ID);
            mNotificationManager.cancel(NOTIFICATION_ID + 1);
            mNotificationManager.cancel(NOTIFICATION_ID + 2);
            mNotificationManager.cancel(NOTIFICATION_ID + 3);
            mNotificationManager.cancel(NOTIFICATION_ID + 4);
            mNotificationManager.cancel(NOTIFICATION_ID + 5);
            Toast.makeText(this, "Все уведомления отменены", Toast.LENGTH_SHORT).show();
        });
    }

    private void checkAndShowNotification(Runnable showAction) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                requestNotificationPermission();
                Toast.makeText(this, "Сначала дайте разрешение на уведомления", Toast.LENGTH_LONG).show();
                return;
            }
        }
        showAction.run();
    }

    private PendingIntent getMainPendingIntent() {
        Intent intent = new Intent(this, NotificationDemoActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        return PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
    }

    private void sendNotification(int id, android.app.Notification notification) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)
                    == PackageManager.PERMISSION_GRANTED) {
                mNotificationManager.notify(id, notification);
                Toast.makeText(this, "Уведомление отправлено!", Toast.LENGTH_SHORT).show();
            }
        } else {
            mNotificationManager.notify(id, notification);
            Toast.makeText(this, "Уведомление отправлено!", Toast.LENGTH_SHORT).show();
        }
    }

    private void showSimpleNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("Простое уведомление")
                .setContentText("Это пример простого уведомления")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(getMainPendingIntent())
                .setAutoCancel(true);
        sendNotification(NOTIFICATION_ID, builder.build());
    }

    private void showActionNotification() {
        Intent okIntent = new Intent(this, NotificationDemoActivity.class);
        okIntent.putExtra("action", "OK");
        PendingIntent okPendingIntent = PendingIntent.getActivity(this, 1, okIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        Intent cancelIntent = new Intent(this, NotificationDemoActivity.class);
        cancelIntent.putExtra("action", "CANCEL");
        PendingIntent cancelPendingIntent = PendingIntent.getActivity(this, 2, cancelIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("Уведомление с действиями")
                .setContentText("Выберите действие")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .addAction(android.R.drawable.ic_input_add, "OK", okPendingIntent)
                .addAction(android.R.drawable.ic_delete, "Отмена", cancelPendingIntent)
                .setAutoCancel(true);
        sendNotification(NOTIFICATION_ID, builder.build());
    }

    private void showProgressNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("Загрузка файла")
                .setContentText("Идёт загрузка...")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setProgress(100, 0, false);
        sendNotification(NOTIFICATION_ID, builder.build());

        mHandler.postDelayed(() -> updateProgress(30), 1000);
        mHandler.postDelayed(() -> updateProgress(60), 2000);
        mHandler.postDelayed(() -> {
            updateProgress(100);
            showProgressComplete();
        }, 3000);
    }

    private void updateProgress(int progress) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("Загрузка файла")
                .setContentText("Загружено " + progress + "%")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setProgress(100, progress, false);
        sendNotification(NOTIFICATION_ID, builder.build());
    }

    private void showProgressComplete() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("Загрузка завершена")
                .setContentText("Файл успешно загружен")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setProgress(0, 0, false)
                .setAutoCancel(true);
        sendNotification(NOTIFICATION_ID, builder.build());
    }

    private void showBigTextNotification() {
        String longText = "Это очень длинный текст уведомления. " +
                "Обычное уведомление показывает только первую строку, " +
                "но если использовать BigTextStyle, можно показать весь текст. " +
                "Пользователь может развернуть уведомление, чтобы увидеть полное содержимое. " +
                "Это полезно для отображения подробных сообщений, логов или ошибок.";

        NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle()
                .bigText(longText)
                .setBigContentTitle("Большое уведомление")
                .setSummaryText("Дополнительная информация");

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("Уведомление с большим текстом")
                .setContentText(longText.substring(0, 50) + "...")
                .setStyle(bigTextStyle)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(getMainPendingIntent())
                .setAutoCancel(true);
        sendNotification(NOTIFICATION_ID, builder.build());
    }


    private void showBigPictureNotification() {
        Bitmap picture = Bitmap.createBitmap(400, 200, Bitmap.Config.ARGB_8888);
        NotificationCompat.BigPictureStyle bigPictureStyle = new NotificationCompat.BigPictureStyle()
                .bigPicture(picture)
                .setBigContentTitle("Уведомление с изображением")
                .setSummaryText("Разверните, чтобы увидеть изображение");

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("ЗАДАНИЕ 1: BigPictureStyle")
                .setContentText("Нажмите, чтобы развернуть и увидеть изображение")
                .setStyle(bigPictureStyle)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(getMainPendingIntent())
                .setAutoCancel(true);
        sendNotification(NOTIFICATION_ID + 1, builder.build());
    }

    private void showGroupedNotifications() {
        NotificationCompat.Builder summaryBuilder = new NotificationCompat.Builder(this, GROUP_CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("Группа уведомлений")
                .setContentText("У вас 3 новых уведомления")
                .setGroup(GROUP_KEY)
                .setGroupSummary(true);  // Это уведомление-сводка
        sendNotification(NOTIFICATION_ID + 2, summaryBuilder.build());

        mHandler.postDelayed(() -> {
            String[] titles = {"Сообщение 1", "Сообщение 2", "Сообщение 3"};
            String[] texts = {"Первое уведомление в группе", "Второе уведомление в группе", "Третье уведомление в группе"};

            for (int i = 0; i < titles.length; i++) {
                NotificationCompat.Builder builder = new NotificationCompat.Builder(this, GROUP_CHANNEL_ID)
                        .setSmallIcon(android.R.drawable.ic_dialog_info)
                        .setContentTitle(titles[i])
                        .setContentText(texts[i])
                        .setGroup(GROUP_KEY)
                        .setAutoCancel(true);
                sendNotification(NOTIFICATION_ID + 3 + i, builder.build());
            }
        }, 500);

        Toast.makeText(this, "Отправлена группа уведомлений", Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}