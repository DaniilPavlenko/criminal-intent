package ru.dpav.criminalintent;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import ru.dpav.criminalintent.database.CrimeBaseHelper;
import ru.dpav.criminalintent.database.CrimeCursorWrapper;
import ru.dpav.criminalintent.database.CrimeDbSchema.CrimeTable;

class CrimeLab {
    private static final String TAG = "CrimeLab";
    private static CrimeLab sCrimeLab;
    private Context mContext;
    private boolean isChanged = true;
    private Map<UUID, Crime> mCrimes;
    private SQLiteDatabase mDatabase;

    private CrimeLab(Context context) {
        mContext = context;
        mDatabase = new CrimeBaseHelper(context).getWritableDatabase();
    }

    static CrimeLab get(Context context) {
        if (sCrimeLab == null) {
            sCrimeLab = new CrimeLab(context.getApplicationContext());
        }
        return sCrimeLab;
    }

    Map<UUID, Crime> getCrimes() {
        Map<UUID, Crime> crimes = new LinkedHashMap<>();

        if (mCrimes != null && !isChanged()) {
            return mCrimes;
        }

        CrimeCursorWrapper cursor = queryCrimes(null, null);

        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Crime crime = cursor.getCrime();
                crimes.put(crime.getId(), crime);
                cursor.moveToNext();
            }
            mCrimes = crimes;
            unChanged();
        } catch (Exception ex) {
            Log.e(TAG, ex.getMessage());
        } finally {
            cursor.close();
        }

        return crimes;
    }

    Crime getCrime(UUID id) {
        CrimeCursorWrapper cursor = queryCrimes(
                CrimeTable.Cols.UUID + " = ?",
                new String[]{id.toString()}
        );

        try {
            if (cursor.getCount() == 0) {
                return null;
            }

            cursor.moveToFirst();
            return cursor.getCrime();
        } catch (Exception ex) {
            Log.e(TAG, ex.getMessage());
            return null;
        } finally {
            cursor.close();
        }
    }

    public void addCrime(Crime crime) {
        ContentValues values = getContentValues(crime);
        mDatabase.insert(CrimeTable.NAME, null, values);

        setChanged();
    }

    public void updateCrime(Crime crime) {
        String uuidString = crime.getId().toString();
        ContentValues values = getContentValues(crime);

        mDatabase.update(CrimeTable.NAME, values,
                CrimeTable.Cols.UUID + " = ?",
                new String[]{uuidString});

        setChanged();
    }

    private CrimeCursorWrapper queryCrimes(String whereClause, String[] whereArgs) {
        Cursor cursor = mDatabase.query(
                CrimeTable.NAME,
                null, //If null - select all columns
                whereClause,
                whereArgs,
                null,
                null,
                null
        );
        return new CrimeCursorWrapper(cursor);
    }

    public void deleteCrime(Crime crime) {
        mDatabase.delete(CrimeTable.NAME,
                CrimeTable.Cols.UUID + " = ?",
                new String[]{crime.getId().toString()});

        setChanged();
    }

    private void setChanged() {
        isChanged = true;
    }

    private void unChanged(){
        isChanged = false;
    }

    private boolean isChanged() {
        return isChanged;
    }

    private static ContentValues getContentValues(Crime crime) {
        ContentValues values = new ContentValues();
        values.put(CrimeTable.Cols.UUID, crime.getId().toString());
        values.put(CrimeTable.Cols.TITLE, crime.getTitle());
        values.put(CrimeTable.Cols.DATE, crime.getDate().getTime());
        values.put(CrimeTable.Cols.SOLVED, crime.isSolved() ? 1 : 0);
        values.put(CrimeTable.Cols.REQUIRES_POLICE, crime.isRequiresPolice() ? 1 : 0);
        return values;
    }
}
