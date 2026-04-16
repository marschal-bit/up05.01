package ru.goloskokov.bookdepository;


import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;
import java.io.File;



import android.Manifest;
import android.graphics.Bitmap;
import android.provider.MediaStore;

public class BookFragment extends Fragment {
    private static final String TAG = "BookFragment";
    private static final String ARG_BOOK_ID = "book_id";

    private static final int REQUEST_DATE = 0;
    private static final int REQUEST_PICK_IMAGE = 1;
    private static final int REQUEST_PERMISSION_READ_STORAGE = 2;
    private static final int REQUEST_PHOTO = 3;
    private static final int REQUEST_CAMERA_PERMISSION = 100;
    private static final String DIALOG_DATE = "DialogDate";

    private Book mBook;
    private EditText mTitleField;
    private CheckBox mReadedCheckBox;
    private Button mDateButton;
    private Button mReportButton;
    private Button mBrowserButton;
    private Button mCoverButton;
    private ImageView mCoverPreview;
    private Uri mCoverUri;
    private ImageButton mPhotoButton;
    private ImageView mPhotoView;
    private File mPhotoFile;

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
        setHasOptionsMenu(true);

        UUID bookId = (UUID) getArguments().getSerializable(ARG_BOOK_ID);
        mBook = BookLab.get(getActivity()).getBook(bookId);
        mPhotoFile = BookLab.get(getActivity()).getPhotoFile(mBook);

        // Логируем путь к файлу для фото
        if (mPhotoFile != null) {
            Log.d(TAG, "Photo file path: " + mPhotoFile.getAbsolutePath());
        } else {
            Log.d(TAG, "mPhotoFile is null");
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_book, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_item_delete_book) {
            BookLab.get(getActivity()).deleteBook(mBook.getId());
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

        // Кнопка даты
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

        // Чекбокс "Прочитана"
        mReadedCheckBox = v.findViewById(R.id.book_readed);
        mReadedCheckBox.setChecked(mBook.isReaded());
        mReadedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mBook.setReaded(isChecked);
            }
        });

        // Кнопка отправки отчёта
        mReportButton = v.findViewById(R.id.book_report);
        mReportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendReport();
            }
        });

        // Кнопка открытия в браузере
        mBrowserButton = v.findViewById(R.id.book_browser);
        mBrowserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openBookInBrowser();
            }
        });

        // Кнопка выбора обложки
        mCoverButton = v.findViewById(R.id.book_cover);
        mCoverPreview = v.findViewById(R.id.book_cover_preview);

        mCoverButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermissionAndPickImage();
            }
        });

        // ========== КОД ДЛЯ КАМЕРЫ (ОБНОВЛЁННЫЙ) ==========
        mPhotoButton = v.findViewById(R.id.book_camera);
        mPhotoView = v.findViewById(R.id.book_photo);

        mPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Проверяем разрешение на камеру
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)
                            != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
                        return;
                    }
                }
                takePhoto();
            }
        });

        updatePhotoView();
        // ==================================================

        return v;
    }

    private void takePhoto() {
        Log.d(TAG, "takePhoto() вызван");

        // Проверяем mPhotoFile
        if (mPhotoFile == null) {
            Log.e(TAG, "mPhotoFile is null");
            Toast.makeText(getActivity(), "Ошибка: не удалось создать файл для фото", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d(TAG, "mPhotoFile path: " + mPhotoFile.getAbsolutePath());

        final Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        PackageManager packageManager = getActivity().getPackageManager();

        // Логируем результат проверки
        boolean hasCameraApp = captureImage.resolveActivity(packageManager) != null;
        Log.d(TAG, "resolveActivity result: " + hasCameraApp);

        // Создаём URI для сохранения фото
        Uri uri;
        if (Build.VERSION.SDK_INT < 24) {
            uri = Uri.fromFile(mPhotoFile);
        } else {
            uri = FileProvider.getUriForFile(getActivity(),
                    "ru.goloskokov.bookdepository.provider", mPhotoFile);
        }
        Log.d(TAG, "URI: " + uri.toString());

        captureImage.putExtra(MediaStore.EXTRA_OUTPUT, uri);

        // Запускаем камеру даже если resolveActivity вернул false (попытка в любом случае)
        try {
            startActivityForResult(captureImage, REQUEST_PHOTO);
        } catch (Exception e) {
            Log.e(TAG, "Ошибка запуска камеры: " + e.getMessage());
            Toast.makeText(getActivity(), "Ошибка: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void updateDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        mDateButton.setText(sdf.format(mBook.getDate()));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            Log.d(TAG, "onActivityResult: resultCode != RESULT_OK, code=" + resultCode);
            return;
        }

        if (requestCode == REQUEST_DATE) {
            Date date = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            mBook.setDate(date);
            updateDate();
        } else if (requestCode == REQUEST_PICK_IMAGE && data != null) {
            mCoverUri = data.getData();
            mCoverPreview.setImageURI(mCoverUri);
            mCoverPreview.setVisibility(View.VISIBLE);
        } else if (requestCode == REQUEST_PHOTO) {
            Log.d(TAG, "Фото сделано, обновляем View");
            updatePhotoView();
            // Сохраняем книгу после добавления фото
            BookLab.get(getActivity()).updateBook(mBook);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        BookLab.get(getActivity()).updateBook(mBook);
    }

    private String getBookReport() {
        String readedString;
        if (mBook.isReaded()) {
            readedString = "Книга прочитана";
        } else {
            readedString = "Книга не прочитана";
        }

        String dateString = android.text.format.DateFormat.getDateFormat(getActivity()).format(mBook.getDate());

        return "Книга: " + mBook.getTitle() + "\n" +
                "Дата добавления: " + dateString + "\n" +
                readedString;
    }

    private void sendReport() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, getBookReport());
        intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.book_report_subject));

        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            Intent chooserIntent = Intent.createChooser(intent, getString(R.string.send_report));
            startActivity(chooserIntent);
        } else {
            Toast.makeText(getActivity(), "Нет приложений для отправки сообщений", Toast.LENGTH_SHORT).show();
        }
    }

    private void openBookInBrowser() {
        String bookTitle = mBook.getTitle();
        if (bookTitle == null || bookTitle.trim().isEmpty()) {
            Toast.makeText(getActivity(), "Сначала введите название книги", Toast.LENGTH_SHORT).show();
            return;
        }

        String query = bookTitle.trim().replace(" ", "+");
        Uri webPage = Uri.parse("https://www.google.com/search?q=" + query);
        Intent intent = new Intent(Intent.ACTION_VIEW, webPage);

        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Toast.makeText(getActivity(), "Нет браузера для открытия", Toast.LENGTH_SHORT).show();
        }
    }

    private void checkPermissionAndPickImage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_MEDIA_IMAGES)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_MEDIA_IMAGES}, REQUEST_PERMISSION_READ_STORAGE);
            } else {
                pickImage();
            }
        } else {
            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERMISSION_READ_STORAGE);
            } else {
                pickImage();
            }
        }
    }

    private void pickImage() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_PICK_IMAGE);
        } else {
            Toast.makeText(getActivity(), "Нет приложения для выбора изображения", Toast.LENGTH_SHORT).show();
        }
    }

    private void updatePhotoView() {
        if (mPhotoFile == null || !mPhotoFile.exists()) {
            Log.d(TAG, "updatePhotoView: файл не существует");
            mPhotoView.setImageDrawable(null);
        } else {
            Log.d(TAG, "updatePhotoView: загружаем фото из " + mPhotoFile.getAbsolutePath());
            Bitmap bitmap = PictureUtils.getScaledBitmap(mPhotoFile.getPath(), getActivity());
            mPhotoView.setImageBitmap(bitmap);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                takePhoto();
            } else {
                Toast.makeText(getActivity(), "Нет разрешения для использования камеры", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == REQUEST_PERMISSION_READ_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                pickImage();
            } else {
                Toast.makeText(getActivity(), "Нет разрешения для выбора изображения", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Жизненный цикл для логирования
    @Override public void onStart() { super.onStart(); Log.d(TAG, "onStart()"); }
    @Override public void onResume() { super.onResume(); Log.d(TAG, "onResume()"); }
    @Override public void onStop() { super.onStop(); Log.d(TAG, "onStop()"); }
    @Override public void onDestroyView() { super.onDestroyView(); Log.d(TAG, "onDestroyView()"); }
    @Override public void onDestroy() { super.onDestroy(); Log.d(TAG, "onDestroy()"); }
    @Override public void onDetach() { super.onDetach(); Log.d(TAG, "onDetach()"); }
}