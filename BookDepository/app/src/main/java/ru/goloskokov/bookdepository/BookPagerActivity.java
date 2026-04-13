package ru.goloskokov.bookdepository;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import java.util.List;
import java.util.UUID;

public class BookPagerActivity extends FragmentActivity {

    private static final String EXTRA_BOOK_ID = "ru.rsue.android.bookdepository.book_id";
    private ViewPager mViewPager;
    private List<Book> mBooks;

    public static Intent newIntent(Context packageContext, UUID bookId) {
        Intent intent = new Intent(packageContext, BookPagerActivity.class);
        intent.putExtra(EXTRA_BOOK_ID, bookId);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_pager);

        UUID bookId = (UUID) getIntent().getSerializableExtra(EXTRA_BOOK_ID);

        mViewPager = findViewById(R.id.activity_book_pager_view_pager);
        mBooks = BookLab.get(this).getBooks();

        FragmentManager fragmentManager = getSupportFragmentManager();


        mViewPager.setAdapter(new FragmentStatePagerAdapter(fragmentManager,
                FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

            @NonNull
            @Override
            public Fragment getItem(int position) {
                Book book = mBooks.get(position);
                return BookFragment.newInstance(book.getId());
            }

            @Override
            public int getCount() {
                return mBooks.size();
            }
        });

        // Устанавливаем позицию на выбранной книге
        for (int i = 0; i < mBooks.size(); i++) {
            if (mBooks.get(i).getId().equals(bookId)) {
                mViewPager.setCurrentItem(i);
                break;
            }
        }
    }
}