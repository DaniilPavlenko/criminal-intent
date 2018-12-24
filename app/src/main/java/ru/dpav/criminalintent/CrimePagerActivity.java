package ru.dpav.criminalintent;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CrimePagerActivity extends AppCompatActivity {
    private static final String EXTRA_CRIME_ID = "ru.dpav.criminalintent.crime_id";

    private ViewPager mViewPager;
    private List<Crime> mCrimes;
    private ImageButton mFirstCrimeButton;
    private ImageButton mLastCrimeButton;

    public static Intent newIntent(Context packageContext, UUID crimeId) {
        Intent intent = new Intent(packageContext, CrimePagerActivity.class);
        intent.putExtra(EXTRA_CRIME_ID, crimeId);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crime_pager);

        UUID crimeId = (UUID) getIntent().getSerializableExtra(EXTRA_CRIME_ID);

        mViewPager = findViewById(R.id.crime_view_pager);

        mFirstCrimeButton = findViewById(R.id.first_crime_button);
        mFirstCrimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewPager.setCurrentItem(0);
            }
        });

        mLastCrimeButton = findViewById(R.id.last_crime_button);
        mLastCrimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewPager.setCurrentItem(mCrimes.size());
            }
        });

        mCrimes = new ArrayList<>(CrimeLab.get(this).getCrimes().values());
        FragmentManager fm = getSupportFragmentManager();
        mViewPager.setAdapter(new FragmentStatePagerAdapter(fm) {
            private int mCurrentFragmentPosition = -1;

            @Override
            public void setPrimaryItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
                super.setPrimaryItem(container, position, object);
                if (position == 0 && position != mCurrentFragmentPosition) {
                    mFirstCrimeButton.setVisibility(View.GONE);
                    mLastCrimeButton.setVisibility(View.VISIBLE);
                } else if (position == mCrimes.size() - 1 && position != mCurrentFragmentPosition) {
                    mFirstCrimeButton.setVisibility(View.VISIBLE);
                    mLastCrimeButton.setVisibility(View.GONE);
                } else if (position > 0 && position < mCrimes.size() - 1) {
                    mFirstCrimeButton.setVisibility(View.VISIBLE);
                    mLastCrimeButton.setVisibility(View.VISIBLE);
                }
                mCurrentFragmentPosition = position;
            }

            @Override
            public Fragment getItem(int position) {
                Crime crime = mCrimes.get(position);
                return CrimeFragment.newInstance(crime.getId());
            }

            @Override
            public int getCount() {
                return mCrimes.size();
            }
        });
        for (int i = 0; i < mCrimes.size(); i++) {
            if (mCrimes.get(i).getId().equals(crimeId)) {
                mViewPager.setCurrentItem(i);
                break;
            }
        }
    }
}