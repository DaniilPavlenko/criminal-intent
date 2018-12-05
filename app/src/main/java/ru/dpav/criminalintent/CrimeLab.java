package ru.dpav.criminalintent;

import android.content.Context;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

class CrimeLab {
    private static CrimeLab sCrimeLab;
    private Map<UUID, Crime> mCrimes;

    static CrimeLab get(Context context) {
        if (sCrimeLab == null) {
            sCrimeLab = new CrimeLab();
        }
        return sCrimeLab;
    }

    private CrimeLab() {
        mCrimes = new LinkedHashMap<>();
//        for (int i = 0; i < 100; i++) {
//            Crime crime = new Crime();
//            crime.setTitle("Crime #" + i);
//            crime.setSolved(i % 2 == 0);
//            crime.setRequiresPolice(i % 5 == 0);
//            mCrimes.put(crime.getId(), crime);
//        }
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
}
