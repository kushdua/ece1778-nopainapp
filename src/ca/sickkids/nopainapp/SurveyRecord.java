package ca.sickkids.nopainapp;

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