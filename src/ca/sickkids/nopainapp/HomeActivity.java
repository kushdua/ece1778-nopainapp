package ca.sickkids.nopainapp;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

public class HomeActivity extends Activity {

	private Button journal = null, exit = null;
	public static Activity activity = null;
	
	public static final String DB_NAME = "NOPAINDB";
	public static final int DB_VERSION = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		addListenerOnButton();
		activity=this;
		SettingsActivity.loadSettingsValuesFromDatabase(this, false);
		CalendarActivity.initializeCalendarID(this, CalendarActivity.CALENDAR_NAME, false);
		CalendarActivity.loadTimesOrCreateRecurringMorningEveningEvents(this, SettingsActivity.morningAlarm, SettingsActivity.eveningAlarm, true);
	}
	
	/*
	 * Method to handle all the buttonclicks on the home page screen and starting the respective activity
	 */
    public void addListenerOnButton() {
    	
    	//JOURNAL Button Handler
    	journal = (Button) findViewById(R.id.journal);
    	journal.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(v.getContext(), Journaling.class);
				startActivity(intent);				
			}
    	});
    	
    	
    	//EXIT Button Handler
    	exit = (Button) findViewById(R.id.btnExit);
    	exit.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
					finish();
			}
    	}); 	
    	
    }
    
    public void onSurveyClickHandler(View v)
    {
		Intent intent = new Intent(v.getContext(), SurveyActivity.class);
		startActivity(intent);
    }
    
    public void onSocialNetworkClickHandler(View v)
    {
    	Intent intent = new Intent(v.getContext(), SocialNetworkActivity.class);
    	startActivity(intent);
    }
    
    public void onCalendarClickHandler(View v)
    {
    	Intent intent = new Intent(v.getContext(), CalendarActivity.class);
    	startActivity(intent);
//    	Intent intent = new Intent(Intent.ACTION_EDIT);  
//    	intent.setType("vnd.android.cursor.item/event");
//    	intent.putExtra("title", "Some title");
//    	intent.putExtra("description", "Some description");
//    	intent.putExtra("beginTime", System.currentTimeMillis()+43200000);
//    	intent.putExtra("endTime", System.currentTimeMillis()+43500000);
//    	startActivity(intent);
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_home, menu);
		return true;
	}

}
