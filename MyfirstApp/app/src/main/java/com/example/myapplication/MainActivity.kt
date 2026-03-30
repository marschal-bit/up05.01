package com.example.myapplication  // Пакет приложения (следует заменить на ваш)

import android.os.Bundle  // Для сохранения состояния Activity
import androidx.activity.enableEdgeToEdge  // Для отображения контента под системные панели
import androidx.appcompat.app.AppCompatActivity  // Базовый класс с поддержкой обратной совместимости
import androidx.core.view.ViewCompat  // Утилиты для работы с View
import androidx.core.view.WindowInsetsCompat  // Для работы с системными отступами (статус-бар, навигация)

class MainActivity : AppCompatActivity() {  // Главный экран приложения
    override fun onCreate(savedInstanceState: Bundle?) {  // Вызывается при создании Activity
        super.onCreate(savedInstanceState)  // Вызов родительского метода (обязательно)
        enableEdgeToEdge()  // Включает режим "от края до края" (контент под статус-баром)
        setContentView(R.layout.activity_main)  // Привязка XML-разметки к Activity

        // Настройка обработчика отступов для системных панелей
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}