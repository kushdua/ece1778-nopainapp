package ca.sickkids.nopainapp;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.app.ListActivity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;

import java.text.DateFormat;
import android.text.format.DateUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class CalendarActivity extends ListActivity {
	
	public static int calendarID = -1;
	private final String CALENDAR_NAME="NoPain";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.calendar_activity);
		
		//Concatenate entered (on top) and loaded lists for display
		ArrayList<AppointmentRecord> list = new ArrayList<AppointmentRecord>();
		
		ArrayList<AppointmentRecord> entriesLoadedFromDB = new ArrayList<AppointmentRecord>();

//		Uri calendar_events_URI = Uri.parse("content://calendar/calendars");
//		Uri.Builder builder = calendar_events_URI.buildUpon();
//		//long now1 = new Date().getTime();
//		long timeNow = System.currentTimeMillis();
//		ContentUris.appendId(builder, timeNow);
//		ContentUris.appendId(builder, timeNow + DateUtils.WEEK_IN_MILLIS);
//		String description = "";
//		Cursor eventCursor = getContentResolver().query(builder.build(),
//				new String[] {"event_id", "title", "begin", "description" }, "description != ?",
//				new String[] {DatabaseUtils.sqlEscapeString(description)}, "startDay ASC, startMinute ASC");

//		ContentResolver cr = getContentResolver();
//		ContentValues values = new ContentValues();
//		values.put(CalendarContract.Events.DTSTART, startMillis);
//		values.put(CalendarContract.Events.DTEND, endMillis);
//		values.put(CalendarContract.Events.TITLE, "Walk The Dog");
//		values.put(CalendarContract.Events.DESCRIPTION, "My dog is bored, so we're going on a really long walk!");
//		values.put(CalendarContract.Events.CALENDAR_ID, 3);
//		Uri uri = cr.insert(CalendarContract.Events.CONTENT_URI, values);
		
		//TODO check if our calendar exists already and insert if not
//		When inserting a new calendar the following fields must be included:
//			ACCOUNT_NAME
//			ACCOUNT_TYPE
//			NAME
//			CALENDAR_DISPLAY_NAME
//			CALENDAR_COLOR
//			CALENDAR_ACCESS_LEVEL
//			OWNER_ACCOUNT
//			The following fields are not required when inserting a Calendar but are generally a good idea to include:
//			SYNC_EVENTS set to 1
//			CALENDAR_TIME_ZONE
//			ALLOWED_REMINDERS
//			ALLOWED_AVAILABILITY
//			ALLOWED_ATTENDEE_TYPES
		
		Uri uri = CalendarContract.Calendars.CONTENT_URI;
		String[] projection = new String[] {
		       CalendarContract.Calendars._ID,
		       CalendarContract.Calendars.ACCOUNT_NAME,
		       CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,
		       CalendarContract.Calendars.NAME,
		       CalendarContract.Calendars.CALENDAR_COLOR
		};

		Cursor calendarCursor = managedQuery(uri, projection, null, null, null);
		String accountName = "";
		if(calendarCursor.getCount()>0)
		{
			//Calendar does not exist => create and record id
			ContentResolver cr = getContentResolver();
			ContentValues values = new ContentValues();
			values.put(CalendarContract.Calendars.NAME, CALENDAR_NAME);
//			values.put
//			Uri uri = cr.insert(CalendarContract.Calendars.CONTENT_URI, values);
			calendarCursor.close();
		}
		else
		{
			calendarID = calendarCursor.getInt(0);
			calendarCursor.close();
		}
		
		
		ContentResolver cr = getContentResolver();
		ContentValues values = new ContentValues();
//		values.put(CalendarContract.Events.DTSTART, startMillis);
//		values.put(CalendarContract.Events.DTEND, endMillis);
//		values.put(CalendarContract.Events.TITLE, "Walk The Dog");
//		values.put(CalendarContract.Events.DESCRIPTION, "My dog is bored, so we're going on a really long walk!");
//		values.put(CalendarContract.Events.CALENDAR_ID, 3);
//		Uri uri = cr.insert(CalendarContract.Calendars.CONTENT_URI, values);
		Cursor eventCursor = null;
		
		if(eventCursor!=null)
		{
			while(eventCursor.moveToNext())
			{
				AppointmentRecord record = new AppointmentRecord();
				String rowName = eventCursor.getString(1);
				String[] split = rowName.split(":");
				String type = (split.length==2) ? split[0] : "";
				String name = (split.length==2) ? split[1] : "";
				DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.getDefault());
				Date beginTime = new Date(eventCursor.getString(2));
				record.setId(eventCursor.getInt(0));
				record.setName(name);
				record.setType(type);
				record.setDate(Long.parseLong(df.format(beginTime)));
				record.setDistanceDuration(Long.parseLong(df.format(new Date(System.currentTimeMillis() - beginTime.getTime()))));
			}
		}
		
		list.addAll(entriesLoadedFromDB);
		
		setListAdapter(new ListRecordAdapter(this, R.layout.row_view_events, R.id.rowViewEventName, list));
	}
	
	class ListRecordAdapter extends ArrayAdapter<AppointmentRecord> {

		public ListRecordAdapter(Context context, int resource, int textViewResourceId,
				List<AppointmentRecord> objects) {
			super(context, resource, textViewResourceId, objects);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			
			View row=super.getView(position, convertView, parent);
			CalendarRowViewHolder holder=(CalendarRowViewHolder)row.getTag();
			if (holder==null) {
				holder=new CalendarRowViewHolder(row);
				row.setTag(holder);
			}
			
			AppointmentRecord record = (AppointmentRecord)getListAdapter().getItem(position);
			holder.name.setText(getResources().getText(R.string.calendarRowNamePrefix) + record.getName());
			holder.type.setText(getResources().getText(R.string.calendarRowTypePrefix) + record.getType());
			holder.date.setText(getResources().getText(R.string.calendarRowDatePrefix) + Long.toString(record.getDate()));
			holder.distance.setText(getResources().getText(R.string.calendarRowDurationUntilPrefix) + Long.toString(record.getDistanceDuration()));
//			holder.reminderPeriod.setText(record.getReminderDuration());
			
			return(row);
		}
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

class CalendarRowViewHolder {
	TextView name=null;
	TextView type=null;
	TextView date=null;
	TextView distance=null;
//	TextView reminderPeriod=null;
	
	CalendarRowViewHolder(View row) {
		this.name=(TextView)row.findViewById(R.id.rowViewEventName);
		this.type=(TextView)row.findViewById(R.id.rowViewEventType);
		this.date=(TextView)row.findViewById(R.id.rowViewEventDate);
		this.distance=(TextView)row.findViewById(R.id.rowViewEventDistance);
//		this.reminderPeriod=(TextView)row.findViewById(R.id.rowViewEventReminderPeriod);
	}
}