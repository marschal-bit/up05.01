package ru.goloskokov.bookdepository;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;


public class BookFragment extends Fragment {
    private static final String TAG = "BookFragment";
    private static final String ARG_BOOK_ID = "book_id";

    private static final int REQUEST_DATE = 0;
    private static final String DIALOG_DATE = "DialogDate";

    private Book mBook;
    private EditText mTitleField;
    private CheckBox mReadedCheckBox;
    private Button mDateButton;

    public static BookFragment newInstance(UUID bookId) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_BOOK_ID, bookId);
        BookFragment fragment = new BookFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate() вызван");
        // ДЕНЬ 12, ЗАДАНИЕ 1: Включаем меню для фрагмента
        setHasOptionsMenu(true);

        UUID bookId = (UUID) getArguments().getSerializable(ARG_BOOK_ID);
        mBook = BookLab.get(getActivity()).getBook(bookId);
    }

    // ДЕНЬ 12, ЗАДАНИЕ 1: Создание меню (кнопка удаления)
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_book, menu);
    }

    // ДЕНЬ 12, ЗАДАНИЕ 1: Обработка нажатия на кнопку удаления
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_item_delete_book) {
            // Удаляем книгу из хранилища
            BookLab.get(getActivity()).deleteBook(mBook.getId());
            // Закрываем активность и возвращаемся к списку
            getActivity().finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView() вызван");
        View v = inflater.inflate(R.layout.fragment_book, container, false);

        // Поле названия
        mTitleField = v.findViewById(R.id.book_title);
        mTitleField.setText(mBook.getTitle());
        mTitleField.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                mBook.setTitle(s.toString());
            }
            @Override public void afterTextChanged(Editable s) {}
        });


        mDateButton = v.findViewById(R.id.book_date_button);
        updateDate();
        mDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager = getFragmentManager();
                DatePickerFragment dialog = DatePickerFragment.newInstance(mBook.getDate());

                dialog.setTargetFragment(BookFragment.this, REQUEST_DATE);
                dialog.show(manager, DIALOG_DATE);
            }
        });


        mReadedCheckBox = v.findViewById(R.id.book_readed);
        mReadedCheckBox.setChecked(mBook.isReaded());
        mReadedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mBook.setReaded(isChecked);
            }
        });

        return v;
    }



    private void updateDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        mDateButton.setText(sdf.format(mBook.getDate()));
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) return;
        if (requestCode == REQUEST_DATE) {
            Date date = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            mBook.setDate(date);
            updateDate();
        }
    }

    // Жизненный цикл для логирования
    @Override public void onStart() { super.onStart(); Log.d(TAG, "onStart()"); }
    @Override public void onResume() { super.onResume(); Log.d(TAG, "onResume()"); }
    @Override public void onPause() { super.onPause(); Log.d(TAG, "onPause()"); }
    @Override public void onStop() { super.onStop(); Log.d(TAG, "onStop()"); }
    @Override public void onDestroyView() { super.onDestroyView(); Log.d(TAG, "onDestroyView()"); }
    @Override public void onDestroy() { super.onDestroy(); Log.d(TAG, "onDestroy()"); }
    @Override public void onDetach() { super.onDetach(); Log.d(TAG, "onDetach()"); }
}