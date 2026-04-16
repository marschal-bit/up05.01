package ru.goloskokov.bookdepository;


// ДЕНЬ 11 ШАГ 2: Импорты для диалогового окна
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;

import androidx.fragment.app.DialogFragment;
import androidx.appcompat.app.AlertDialog;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

// ДЕНЬ 11 ШАГ 2: Класс для диалогового окна выбора даты
public class DatePickerFragment extends DialogFragment {

    // ДЕНЬ 11 ШАГ 9: Константа для передачи даты в результат
    public static final String EXTRA_DATE = "ru.goloskokov.bookdepository.date";

    // ДЕНЬ 11 ШАГ 6: Ключ для аргумента даты
    private static final String ARG_DATE = "date";

    private DatePicker mDatePicker;

    // ДЕНЬ 11 ШАГ 6: Статический метод newInstance для передачи даты
    public static DatePickerFragment newInstance(Date date) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_DATE, date);
        DatePickerFragment fragment = new DatePickerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    // ДЕНЬ 11 ШАГ 3,5,7,10: Создание диалогового окна
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // ДЕНЬ 11 ШАГ 7: Получаем дату из аргументов
        Date date = (Date) getArguments().getSerializable(ARG_DATE);

        // ДЕНЬ 11 ШАГ 7: Инициализация календаря
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // ДЕНЬ 11 ШАГ 5: Надуваем макет с DatePicker
        View v = LayoutInflater.from(getActivity())
                .inflate(R.layout.dialog_date, null);

        mDatePicker = v.findViewById(R.id.dialog_date_picker);
        mDatePicker.init(year, month, day, null);

        return new AlertDialog.Builder(getActivity())
                .setView(v)
                .setTitle(R.string.date_picker_title)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int year = mDatePicker.getYear();
                        int month = mDatePicker.getMonth();
                        int day = mDatePicker.getDayOfMonth();
                        Date selectedDate = new GregorianCalendar(year, month, day).getTime();
                        sendResult(Activity.RESULT_OK, selectedDate);
                    }
                })
                .create();
    }

    // ДЕНЬ 11 ШАГ 9: Отправка результата обратно в целевой фрагмент
    private void sendResult(int resultCode, Date date) {
        if (getTargetFragment() == null) {
            return;
        }
        Intent intent = new Intent();
        intent.putExtra(EXTRA_DATE, date);
        getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, intent);
    }
}
