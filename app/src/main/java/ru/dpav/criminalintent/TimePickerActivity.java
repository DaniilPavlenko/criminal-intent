package ru.dpav.criminalintent;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

import java.util.Date;

public class TimePickerActivity extends SingleFragmentActivity {
    private static final String EXTRA_TIME = "ru.dpav.criminalintent.time";

    @Override
    protected Fragment createFragment() {
        Date date = (Date) getIntent().getSerializableExtra(EXTRA_TIME);
        return TimePickerFragment.newInstance(date);
    }

    public static Intent newIntent(Context packageContext, Date date){
        Intent intent = new Intent(packageContext, TimePickerActivity.class);
        intent.putExtra(EXTRA_TIME, date);
        return intent;
    }

}
