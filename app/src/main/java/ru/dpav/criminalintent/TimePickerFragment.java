package ru.dpav.criminalintent;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class TimePickerFragment extends DialogFragment {
    private static final String ARG_TIME = "time";
    public static final String EXTRA_TIME = "ru.dpav.criminalintent.time";
    private TimePicker mTimePicker;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Date date = (Date) getArguments().getSerializable(ARG_TIME);
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        View v = inflater.inflate(R.layout.dialog_time, null);

        mTimePicker = v.findViewById(R.id.dialog_time_picker);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mTimePicker.setHour(calendar.get(Calendar.HOUR));
            mTimePicker.setMinute(calendar.get(Calendar.MINUTE));
        } else {
            mTimePicker.setCurrentHour(calendar.get(Calendar.HOUR));
            mTimePicker.setCurrentMinute(calendar.get(Calendar.MINUTE));
        }

        Button okButton = v.findViewById(R.id.dialog_time_ok_button);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int hour;
                int minute;

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    hour = mTimePicker.getHour();
                    minute = mTimePicker.getMinute();
                } else {
                    hour = mTimePicker.getCurrentHour();
                    minute = mTimePicker.getCurrentMinute();
                }

                Date newDate = new GregorianCalendar(year, month, day, hour, minute).getTime();

                sendResult(Activity.RESULT_OK, newDate);
                getFragmentManager().beginTransaction()
                        .remove(TimePickerFragment.this)
                        .commit();
            }
        });

        return v;
    }

    public static TimePickerFragment newInstance(Date date) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_TIME, date);
        TimePickerFragment fragment = new TimePickerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private void sendResult(int resultCode, Date date) {
        Intent intent = new Intent();
        intent.putExtra(EXTRA_TIME, date);
        if (getTargetFragment() == null) {
            getActivity().setResult(resultCode, intent);
            return;
        }
        getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, intent);
    }
}
