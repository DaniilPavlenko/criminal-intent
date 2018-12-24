package ru.dpav.criminalintent.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import java.util.Date;
import java.util.UUID;

import ru.dpav.criminalintent.Crime;

import static ru.dpav.criminalintent.database.CrimeDbSchema.*;

public class CrimeCursorWrapper extends CursorWrapper {
    public CrimeCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Crime getCrime() {
        String uuidString = getString(getColumnIndex(CrimeTable.Cols.UUID));
        String title = getString(getColumnIndex(CrimeTable.Cols.TITLE));
        long date = getLong(getColumnIndex(CrimeTable.Cols.DATE));
        int isSolved = getShort(getColumnIndex(CrimeTable.Cols.SOLVED));
        int isRequiresPolice = getInt(getColumnIndex(CrimeTable.Cols.REQUIRES_POLICE));

        Crime crime = new Crime(UUID.fromString(uuidString));
        crime.setTitle(title);
        crime.setDate(new Date(date));
        crime.setSolved(isSolved != 0);
        crime.setRequiresPolice(isRequiresPolice != 0);

        return crime;
    }
}
