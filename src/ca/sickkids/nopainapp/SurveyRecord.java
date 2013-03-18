package ca.sickkids.nopainapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.util.Log;

public class SurveyRecord {
	private static final SurveyRecord instance = new SurveyRecord();
	private static final DBHelper dbHelper = new DBHelper(HomeActivity.activity.getApplicationContext(), "NOPAIN", null, 1);
	
	private float a1=0.0f;
	
	public SurveyRecord getInstance()
	{
		return instance;
	}
	
	public SurveyRecord()
	{
		//Init vars
	}

	public DBHelper getDbHelper() {
		return dbHelper;
	}
}

class DBHelper extends SQLiteOpenHelper
{
	//TODO add ID AUTOINC to tables, date of survey taken/submitted
	private final String createUserTable = "CREATE TABLE users(name TEXT, pass TEXT, UNIQUE(name) ON CONFLICT ROLLBACK);";
	private final String createSurveyTable = "CREATE TABLE survey(pain12Hours TEXT, interference12Hours TEXT, control12Hours TEXT, " +
											 "q4 TEXT, q5 TEXT, q6 TEXT, q7 TEXT, q8 TEXT, q9 TEXT);";
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