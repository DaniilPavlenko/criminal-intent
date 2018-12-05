package ru.dpav.criminalintent.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import ru.dpav.criminalintent.database.CrimeDbSchema.CrimeTable;

public class CrimeBaseHelper extends SQLiteOpenHelper {
    public static final int VERSION = 1;
    public static final String DATABASE_NAME = "crimeBase.db";


    public CrimeBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + CrimeTable.NAME + "(" +
                " _id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                CrimeTable.Cols.UUID + " INTEGER, " +
                CrimeTable.Cols.TITLE + " VARCHAR(150), " +
                CrimeTable.Cols.DATE + " DATE, " +
                CrimeTable.Cols.SOLVED + " BOOLEAN, " +
                CrimeTable.Cols.REQUIRES_POLICE + " BOOLEAN" +
                ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
