package ca.sickkids.nopainapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper
{
	private final String createUserTable = "CREATE TABLE users(id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL, pass TEXT NOT NULL, UNIQUE(name) ON CONFLICT ROLLBACK);";
	//q1 = pain now (INT); q2 = pain worst in past 12 hours (INT); q3 = in way of sleep past 12 (INT)
	//; q4 = in way of things past 12 (INT); q5 (5a) = medications in past 12 (TEXT); q6 (5b) - how effective were they (TEXT)
	//q7 (6a) - other strategies past 12 (TEXT); q8 (6b) - how effective were those strategies (TEXT); q9 (7) - control past 12 (INT)
	private final String createSurveyTable = "CREATE TABLE survey(id INTEGER PRIMARY KEY AUTOINCREMENT, userID INTEGER, q1 TEXT, q2 TEXT, q3 TEXT, " +
											 "q4 TEXT, q5 TEXT, q6 TEXT, q7 TEXT, q8 TEXT, q9 TEXT, q10 TEXT, q11 TEXT, q12 TEXT, q13 TEXT, recommendation TEXT, FOREIGN KEY(userID) REFERENCES users(id) ON DELETE CASCADE);";
	private final String createRecommendationTable = "CREATE TABLE recommendation(id INTEGER PRIMARY KEY AUTOINCREMENT, userID INTEGER, text TEXT NOT NULL, count INTEGER DEFAULT 1, FOREIGN KEY(userID) REFERENCES users(id) ON DELETE CASCADE);";
	//private final String createAppointmentsTable = "CREATE TABLE appointments(id INTEGER PRIMARY KEY, userID INTEGER, type TEXT NOT NULL, date INTEGER NOT NULL, reminderMinutes INTEGER DEFAULT 5, FOREIGN KEY(userID) REFERENCES users(id) ON DELETE CASCADE);";
	private final String createSettingsTable = "CREATE TABLE settings(userID INTEGER PRIMARY KEY, disease TEXT, reminder TEXT, morningSurveyTime TEXT, eveningSurveyTime TEXT, FOREIGN KEY(userID) REFERENCES users(id) ON DELETE CASCADE);";
	
	
	public DBHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		try
		{
			db.beginTransaction();
			db.execSQL(createUserTable);
			db.execSQL(createSurveyTable);
			db.execSQL(createRecommendationTable);
			ContentValues values = new ContentValues();
			for(String item : Recommendation.items)
			{
				values.clear();
				values.put("userID", LoginActivity.userID);
				values.put("text", item);
				db.insertOrThrow("recommendation", null, values);
			}
			//db.execSQL(createAppointmentsTable);
			db.execSQL(createSettingsTable);
			db.setTransactionSuccessful();
		}finally{
			db.endTransaction();
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.e("ERROR","Upgrade not supported, why are we here? Aborting...");
	}
	
}
