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
	public DBHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		try
		{
		db.beginTransaction();
		//TODO: Create necessary survey/user tables
		//db.execSQL("CREATE TABLE "+DB_TABLE_NAME+"("+DB_TABLE_COLUMN_NAME+" TEXT, UNIQUE("+DB_TABLE_COLUMN_NAME+") ON CONFLICT REPLACE);");
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