package ca.sickkids.nopainapp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.os.Bundle;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

public class SettingsActivity extends Activity {

	private Spinner diseaseSpinner = null, reminderSpinner = null;
	private TimePicker morningAlarmPicker = null, eveningAlarmPicker = null;
	public static Activity activity = null;
	DBHelper dbHelper = null;
	
	public static String disease = "";
	public static String reminder = "";
	public static int reminderMinutes = -1;
	public static String morningAlarm = "";
	public static String eveningAlarm = "";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.social_network_activity);
		diseaseSpinner = (Spinner)findViewById(R.id.spinnerDisease);
		reminderSpinner = (Spinner)findViewById(R.id.spinnerReminders);
		morningAlarmPicker = (TimePicker)findViewById(R.id.morningAlarmTimePicker);
		eveningAlarmPicker = (TimePicker)findViewById(R.id.eveningAlarmTimePicker);
		
		//TODO Load saved settings values from DB + set values to that
		dbHelper = new DBHelper(this, HomeActivity.DB_NAME, null, HomeActivity.DB_VERSION);
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		try
		{
			String args[] = {};//{ //1,//2 };
	    	//db.execSQL("SELECT name, pass FROM users WHERE name=? AND pass=?;", args);
			Cursor result = db.rawQuery("SELECT disease, reminder, morningSurveyTime, eveningSurveyTime FROM settings WHERE userID=?;", new String[]{Integer.toString(LoginActivity.userID)});
			if(result != null && result.getCount()==1 && result.moveToNext())
			{
				disease=result.getString(0);
	
				String[] diseasesArray = getResources().getStringArray(R.array.diseaseList);
				List<String> list = new ArrayList<String>();
			    list = Arrays.asList(diseasesArray);
			    diseaseSpinner.setSelection(list.indexOf(disease));
				
				reminder=result.getString(1);
				
				String[] remindersArray = getResources().getStringArray(R.array.diseaseList);
				List<String> list2 = new ArrayList<String>();
			    list2 = Arrays.asList(remindersArray);
			    reminderSpinner.setSelection(list2.indexOf(reminder));
			    
			    reminderMinutes = reminderStringToMinutes(reminder);
			    
				morningAlarm=result.getString(2);
				String[] array = morningAlarm.split(":");
				morningAlarmPicker.setCurrentHour(Integer.parseInt(array[0]));
				morningAlarmPicker.setCurrentMinute(Integer.parseInt(array[1]));
				
				eveningAlarm=result.getString(3);
				array = morningAlarm.split(":");
				eveningAlarmPicker.setCurrentHour(Integer.parseInt(array[0]));
				eveningAlarmPicker.setCurrentMinute(Integer.parseInt(array[1]));
			}
			else
			{
				Log.e("SETTINGS", "Could not load saved values from database - wrong number of results returned. Proceeding with page defaults.");
			}
		}
		catch(SQLException e){
			Log.e("SETTINGS", "Could not load saved settings from DB: " + e.toString());
		}
		finally
		{
			db.close();
		}
	}
	
	@Override
	protected void onDestroy()
	{
		saveSelections();
	}
	
	public int reminderStringToMinutes(String in)
	{
		try
		{
			return Integer.parseInt(in)*(in.contains("day")?1440:1);
		}
		catch(NumberFormatException e)
		{
			//Log error and return default of 5 minutes below
			Log.e("SETTINGS", "Could not parse " + in + " to minutes." + e.toString());
		}
		
		//Return default of 5 minutes reminder
		return 5;
	}

	public void saveSelections()
	{
    	SQLiteDatabase db = dbHelper.getWritableDatabase();
		disease = diseaseSpinner.getSelectedItem().toString();
		reminder = reminderSpinner.getSelectedItem().toString();
		morningAlarm = morningAlarmPicker.getCurrentHour() + ":" + morningAlarmPicker.getCurrentMinute();
		eveningAlarm = eveningAlarmPicker.getCurrentHour() + ":" + eveningAlarmPicker.getCurrentMinute();
		
		try
		{
			ContentValues values = new ContentValues(2);
			values.put("disease", disease);
			values.put("reminder", reminder);
			values.put("morningSurveyTime", morningAlarm);
			values.put("eveningSurveyTime", eveningAlarm);
			long numRows = db.update("settings", values, "id=?", new String[]{Integer.toString(LoginActivity.userID)});
			if(numRows!=1)
			{
				Log.e("SETTINGS", "Unable to save diseaseSpinner setting to DB - wrong number of affected rows returned - " + numRows);
				Toast.makeText(activity, R.string.errorSavingSettingValue, Toast.LENGTH_SHORT).show();
			}
		}
		catch(SQLException e)
		{
			Log.e("SETTINGS", "Unable to save diseaseSpinner setting to DB " + e.toString());
			Toast.makeText(activity, R.string.errorSavingSettingValue, Toast.LENGTH_SHORT).show();
		}
		finally
		{
			db.close();
		}
	}
}
