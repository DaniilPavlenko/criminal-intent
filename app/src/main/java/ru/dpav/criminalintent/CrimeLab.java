package ru.dpav.criminalintent;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import ru.dpav.criminalintent.database.CrimeBaseHelper;

class CrimeLab {
    private static CrimeLab sCrimeLab;
    private Map<UUID, Crime> mCrimes;
    private Context mContext;
    private SQLiteDatabase mDatabase;

    private CrimeLab(Context context) {
        mContext = context;
        mDatabase = new CrimeBaseHelper(context).getWritableDatabase();
        mCrimes = new LinkedHashMap<>();
//        for (int i = 0; i < 2; i++) {
//            Crime crime = new Crime();
//            crime.setTitle("Crime #" + i);
//            crime.setSolved(i % 2 == 0);
//            crime.setRequiresPolice(i % 5 == 0);
//            mCrimes.put(crime.getId(), crime);
//        }
    }

    static CrimeLab get(Context context) {
        if (sCrimeLab == null) {
            sCrimeLab = new CrimeLab(context);
        }
        return sCrimeLab;
    }

    List<Crime> getCrimes() {
        return new ArrayList<>(mCrimes.values());
    }

    Crime getCrime(UUID id) {
        return mCrimes.get(id);
    }

    public void addCrime(Crime crime) {
        mCrimes.put(crime.getId(), crime);
    }

    public void removeCrime(Crime crime) {
        mCrimes.remove(crime.getId());
    }
}
