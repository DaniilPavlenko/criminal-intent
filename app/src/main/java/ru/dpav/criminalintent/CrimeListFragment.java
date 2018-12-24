package ru.dpav.criminalintent;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class CrimeListFragment extends Fragment {

    private static final String TAG = "CrimeListFragment";
    private static final String SAVED_SUBTITLE_VISIBLE = "subtitle";
    private int mAdapterPosition = -1;
    private RecyclerView mCrimeRecyclerView;
    private CrimeAdapter mAdapter;
    private LinearLayout mEmptyCrimes;
    private boolean mSubtitleVisible;
    private Button mAddNewCrimeButton;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_crime_list, container, false);

        mCrimeRecyclerView = view.findViewById(R.id.crime_recycler_view);
        mCrimeRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        if (savedInstanceState != null) {
            mSubtitleVisible = savedInstanceState.getBoolean(SAVED_SUBTITLE_VISIBLE);
        }

        mEmptyCrimes = view.findViewById(R.id.empty_crimes_linear_layout);

        mAddNewCrimeButton = view.findViewById(R.id.empty_crimes_new_crime);
        mAddNewCrimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newCrime();
            }
        });

        updateUI();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_crime_list, menu);

        MenuItem subtitleItem = menu.findItem(R.id.show_subtitle);
        subtitleItem.setTitle(mSubtitleVisible ? R.string.hide_subtitle : R.string.show_subtitle);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.new_crime:
                newCrime();
                return true;
            case R.id.show_subtitle:
                mSubtitleVisible = !mSubtitleVisible;
                getActivity().invalidateOptionsMenu();
                updateSubtitle();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void newCrime() {
        Crime crime = new Crime();
        CrimeLab.get(getActivity()).addCrime(crime);
        Intent intent = CrimePagerActivity.newIntent(getActivity(), crime.getId());
        startActivity(intent);
        mAdapter.notifyItemInserted(CrimeLab.get(getActivity()).getCrimes().size() - 1);
    }

    private void updateUI() {
        CrimeLab crimeLab = CrimeLab.get(getActivity());
        Map<UUID, Crime> crimes = crimeLab.getCrimes();
        if (crimes == null || crimes.size() == 0) {
            mEmptyCrimes.setVisibility(View.VISIBLE);
        } else {
            mEmptyCrimes.setVisibility(View.GONE);
        }
        if (mAdapter == null) {
            mAdapter = new CrimeAdapter(crimes);
            mCrimeRecyclerView.setAdapter(mAdapter);
        } else {
//            if (mAdapterPosition >= 0) {
//                mAdapter.notifyItemChanged(mAdapterPosition);
//            } else {
            mAdapter.setCrimes(crimes);
            mAdapter.notifyDataSetChanged();
//            }
        }
        updateSubtitle();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(SAVED_SUBTITLE_VISIBLE, mSubtitleVisible);
    }

    private void updateSubtitle() {
        CrimeLab crimeLab = CrimeLab.get(getActivity());
        int crimeCount = crimeLab.getCrimes().size();
        String subtitle = getResources().getQuantityString(R.plurals.crimes_plurals, crimeCount, crimeCount);

        if (!mSubtitleVisible) {
            subtitle = null;
        }

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.getSupportActionBar().setSubtitle(subtitle);
    }

    private class CrimeHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView mTitleTextView, mDateTextView;
        private ImageView mSolvedImageView;
        private ImageView mPoliceButton;
        private Crime mCrime;

        public CrimeHolder(View view) {
            super(view);
            itemView.setOnClickListener(this);
            mTitleTextView = itemView.findViewById(R.id.crime_title);
            mDateTextView = itemView.findViewById(R.id.crime_date);
            mPoliceButton = itemView.findViewById(R.id.crime_call);
            mSolvedImageView = itemView.findViewById(R.id.crime_solved);
        }

        public void bind(Crime crime) {
            mCrime = crime;
            mTitleTextView.setText(mCrime.getTitle());
            mDateTextView.setText(mCrime.getDate().toString());
            mDateTextView.setText(DateFormat.format("H:mm EE d.M.yy ", mCrime.getDate()));
            mSolvedImageView.setVisibility(crime.isSolved() ? View.VISIBLE : View.INVISIBLE);
            if (mPoliceButton != null) {
                mPoliceButton.setOnClickListener(this);
            }
        }

        @Override
        public void onClick(View item) {
            if (item.getId() == R.id.crime_call) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:+79515025439"));
                startActivity(intent);
            } else {
                mAdapterPosition = getAdapterPosition();
                Intent intent = CrimePagerActivity.newIntent(getActivity(), mCrime.getId());
                startActivity(intent);
            }
        }
    }

    private class CrimeAdapter extends RecyclerView.Adapter<CrimeHolder> {

        private List<Crime> mCrimes;

        public CrimeAdapter(Map<UUID, Crime> crimes) {
            mCrimes = new ArrayList<>();
            mCrimes.addAll(crimes.values());
        }

        @NonNull
        @Override
        public CrimeHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            Log.d(TAG, "CreateViewHolder");
            View v;
            switch (viewType) {
                case R.layout.list_item_crime_requires_police:
                    v = layoutInflater.inflate(R.layout.list_item_crime_requires_police, parent, false);
                    break;
                default:
                    v = layoutInflater.inflate(R.layout.list_item_crime, parent, false);
                    break;
            }
            return new CrimeHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull CrimeHolder holder, int position) {
            Log.d(TAG, "OnBindViewHolder");
            Crime crime = mCrimes.get(position);
            holder.bind(crime);
        }

        @Override
        public int getItemCount() {
            return mCrimes.size();
        }

        public void setCrimes(Map<UUID, Crime> crimes) {
            mCrimes = new ArrayList<>();
            mCrimes.addAll(crimes.values());
        }

        @Override
        public int getItemViewType(int position) {
            return mCrimes.get(position).isRequiresPolice()
                    ? R.layout.list_item_crime_requires_police
                    : R.layout.list_item_crime;
        }
    }
}
