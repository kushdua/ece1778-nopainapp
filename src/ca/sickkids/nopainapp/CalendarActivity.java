package ca.sickkids.nopainapp;

import java.sql.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.CalendarView.OnDateChangeListener;
import android.widget.Toast;

public class CalendarActivity extends Activity {

	private CalendarView calendar = null;
	public static Activity activity = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.calendar_activity);
		calendar = (CalendarView)findViewById(R.id.calendar);
		calendar.setClickable(true);
		calendar.setOnDateChangeListener(new OnDateChangeListener(){

	            @Override
	            public void onSelectedDayChange(CalendarView view, int year,
	                    int month, int dayOfMonth) {
	            	Calendar c = Calendar.getInstance();
	            	c.set(year, month, dayOfMonth);
	                view.setDate(c.getTimeInMillis());
	            }

	    });
		
		activity=this;
		
//		Intent intent = new Intent(Intent.ACTION_EDIT);  
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
		getMenuInflater().inflate(R.menu.activity_calendar_menu, menu);
		return true;
	}
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.calendar_menu_new_survey:
    		Intent intentNewSurvey = new Intent(Intent.ACTION_EDIT);  
        	intentNewSurvey.setType("vnd.android.cursor.item/event");
        	intentNewSurvey.putExtra("title", "noPain: Survey");
        	intentNewSurvey.putExtra("description", "noPain: Daily survey");
        	intentNewSurvey.putExtra("beginTime", System.currentTimeMillis()+43200000);
        	intentNewSurvey.putExtra("endTime", System.currentTimeMillis()+43500000);
        	startActivity(intentNewSurvey);
        	break;
        case R.id.calendar_menu_new_appointment:
    		Intent intentNewAppt = new Intent(Intent.ACTION_EDIT);  
        	intentNewAppt.setType("vnd.android.cursor.item/event");
        	intentNewAppt.putExtra("title", "noPain: Appointment");
        	intentNewAppt.putExtra("description", "noPain: Appointment details");
        	intentNewAppt.putExtra("beginTime", System.currentTimeMillis());
        	intentNewAppt.putExtra("endTime", System.currentTimeMillis()+300000);
        	startActivity(intentNewAppt);
        	break;
        default:
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
