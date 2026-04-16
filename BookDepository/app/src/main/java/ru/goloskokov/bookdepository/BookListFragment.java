package ru.goloskokov.bookdepository;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.UUID;


public class BookListFragment extends Fragment {

    private RecyclerView mBookRecyclerView;
    private BookAdapter mAdapter;
    private boolean mSubtitleVisible = false;
    private String uuidString;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_book_list, container, false);

        mBookRecyclerView = view.findViewById(R.id.book_recycler_view);
        mBookRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        updateUI();
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_book_list, menu);

        MenuItem subtitleItem = menu.findItem(R.id.menu_item_show_subtitle);
        if (mSubtitleVisible) {
            subtitleItem.setTitle(R.string.hide_subtitle);
        } else {
            subtitleItem.setTitle(R.string.show_subtitle);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.menu_item_new_book) {
            // Создаем новую книгу
            Book book = new Book();
            BookLab.get(getActivity()).addBook(book);
            // Открываем детальный просмотр
            Intent intent = BookActivity.newIntent(getActivity(), book.getId());
            startActivity(intent);
            return true;
        } else if (itemId == R.id.menu_item_show_subtitle) {
            mSubtitleVisible = !mSubtitleVisible;
            getActivity().invalidateOptionsMenu();
            updateSubtitle();
            return true;
        } else if (itemId == R.id.menu_item_share_list) {
            shareBookList();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void updateSubtitle() {
        BookLab bookLab = BookLab.get(getActivity());
        int bookCount = bookLab.getBooks().size();
        String subtitle = getString(R.string.subtitle_format, bookCount);

        if (!mSubtitleVisible) {
            subtitle = null;
        }

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity.getSupportActionBar() != null) {
            activity.getSupportActionBar().setSubtitle(subtitle);
        }
    }

    private void updateUI() {
        BookLab bookLab = BookLab.get(getActivity());
        List<Book> books = bookLab.getBooks();

        if (mAdapter == null) {
            mAdapter = new BookAdapter(books);
            mBookRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.setBooks(books);
        }

        updateSubtitle();
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    // Метод для отправки списка книг
    private void shareBookList() {
        BookLab bookLab = BookLab.get(getActivity());
        List<Book> books = bookLab.getBooks();

        if (books.isEmpty()) {
            Toast.makeText(getActivity(), "Список книг пуст", Toast.LENGTH_SHORT).show();
            return;
        }

        StringBuilder listBuilder = new StringBuilder();
        listBuilder.append("Мои книги:\n\n");

        for (int i = 0; i < books.size(); i++) {
            Book book = books.get(i);
            String readedStatus = book.isReaded() ? "✓ Прочитана" : "○ Не прочитана";
            listBuilder.append(i + 1).append(". ")
                    .append(book.getTitle())
                    .append(" — ")
                    .append(readedStatus)
                    .append("\n");
        }

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, listBuilder.toString());
        intent.putExtra(Intent.EXTRA_SUBJECT, "Список моих книг");

        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivity(Intent.createChooser(intent, "Поделиться списком книг"));
        } else {
            Toast.makeText(getActivity(), "Нет приложений для отправки", Toast.LENGTH_SHORT).show();
        }
    }

    private class BookHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private Book mBook;
        private TextView mTitleTextView;
        private TextView mDateTextView;
        private CheckBox mReadedCheckBox;

        public BookHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_book, parent, false));
            itemView.setOnClickListener(this);

            mTitleTextView = itemView.findViewById(R.id.list_item_book_title_text_view);
            mDateTextView = itemView.findViewById(R.id.list_item_book_date_text_view);
            mReadedCheckBox = itemView.findViewById(R.id.list_item_book_readed_check_box);
        }

        public void bind(Book book) {
            mBook = book;
            mTitleTextView.setText(mBook.getTitle());

            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
            mDateTextView.setText(sdf.format(mBook.getDate()));

            mReadedCheckBox.setChecked(mBook.isReaded());
        }

        @Override
        public void onClick(View v) {
            Intent intent = BookActivity.newIntent(getActivity(), mBook.getId());
            startActivity(intent);
        }
    }

    // ОБНОВЛЁННЫЙ BookAdapter с методом setBooks() и notifyDataSetChanged()
    private class BookAdapter extends RecyclerView.Adapter<BookHolder> {
        private List<Book> mBooks;

        public BookAdapter(List<Book> books) {
            mBooks = books;
        }

        // НОВЫЙ МЕТОД: обновляет список книг и уведомляет адаптер
        public void setBooks(List<Book> books) {
            mBooks = books;
            notifyDataSetChanged();
        }

        @Override
        public BookHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View view = inflater.inflate(R.layout.list_item_book, parent, false);
            return new BookHolder(inflater, parent);
        }

        @Override
        public void onBindViewHolder(BookHolder holder, int position) {
            Book book = mBooks.get(position);
            holder.bind(book);
        }

        @Override
        public int getItemCount() {
            return mBooks.size();
        }
    }
}