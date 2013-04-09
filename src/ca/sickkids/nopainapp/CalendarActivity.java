package ca.sickkids.nopainapp;

import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import android.app.Activity;
import android.app.ListActivity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.location.Address;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Events;

import java.text.DateFormat;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class CalendarActivity extends ListActivity {
	public Activity activity = null;
	public static int calendarID = -1;
	public static int morningRecurringEventID = -1;
	public static int eveningRecurringEventID = -1;
	protected static final String CALENDAR_NAME="NOpain";
	private static String accountName = "";
	private static String accountType = "";
//	private String allowedReminders = CalendarContract.Reminders.METHOD_ALARM+","+CalendarContract.Reminders.METHOD_ALERT;
	
	private static String defaultSurveyTitle = "noPain: Survey";
	private static String defaultEventTitle = "noPain: Appointment";
	
	protected final static int NUMBER_MILLISECONDS_IN_MINUTE = 60000;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.calendar_activity);
		
		activity = this;
		
		//Concatenate entered (on top) and loaded lists for display
		ArrayList<AppointmentRecord> list = new ArrayList<AppointmentRecord>();
		
		ArrayList<AppointmentRecord> entriesLoadedFromDB = initializeCalendarID(this, CALENDAR_NAME, true);
		
		list.addAll(entriesLoadedFromDB);
		
		setListAdapter(new ListRecordAdapter(this, R.layout.row_view_events, R.id.rowViewEventName, list));
	}
	
	public static ArrayList<AppointmentRecord> initializeCalendarID(Activity activity, String calendarName, boolean returnApptList)
	{
		Log.w("CALENDAR","Entering initializeCalendarID");
		
		Uri uri = CalendarContract.Calendars.CONTENT_URI;
		String[] projection = new String[] {
		       CalendarContract.Calendars._ID,
		       CalendarContract.Calendars.ACCOUNT_NAME,
		       CalendarContract.Calendars.ACCOUNT_TYPE,
		       CalendarContract.Calendars.NAME
		};

		//Cursor calendarCursor = activity.managedQuery(uri, projection, CalendarContract.Calendars.NAME+"=?", new String[]{calendarName}, null);
		Cursor calendarCursor = activity.managedQuery(uri, projection, null, null, null);
		if(calendarCursor.getCount()>0)
		{
			//Try to find our calendar ID (NoPain)
			boolean foundOurs = false;
			while(calendarCursor.moveToNext())
			{
				//Take account info from first calendar returned
				if(calendarCursor.isFirst())
				{
					CalendarActivity.accountName = calendarCursor.getString(1);
					CalendarActivity.accountType = calendarCursor.getString(2);
				}
				
				//Indicate whether we found our NOpain calendar or not...
				if(calendarCursor.getString(3).compareTo(CALENDAR_NAME)==0)
				{
					foundOurs = true;
					CalendarActivity.accountName = calendarCursor.getString(1);
					CalendarActivity.accountType = calendarCursor.getString(2);
					break;
				}
				else if(calendarCursor.getString(3).compareTo("NoPain")==0)
				{
					//Delete previous calendars...
					ContentValues values = new ContentValues();
					Uri deleteUrl = ContentUris.withAppendedId(CalendarContract.Calendars.CONTENT_URI, calendarCursor.getInt(0));
					int rows = activity.getContentResolver().delete(deleteUrl, null, null);
				}
			}

			//Calendar does not exist => create and record id
			if(!foundOurs)
			{
				ContentResolver cr = activity.getContentResolver();
				ContentValues values = new ContentValues();
				values.put(CalendarContract.Calendars.NAME, CALENDAR_NAME);
				values.put(CalendarContract.Calendars.CALENDAR_DISPLAY_NAME, CALENDAR_NAME);
				values.put(CalendarContract.Calendars.ACCOUNT_NAME, accountName);
				values.put(CalendarContract.Calendars.OWNER_ACCOUNT, true); //or accountName?
				//Put a nice green color on all our events.
				values.put(CalendarContract.Calendars.CALENDAR_COLOR, 0xFF0ACC0A);
				values.put(CalendarContract.Calendars.CALENDAR_ACCESS_LEVEL, CalendarContract.Calendars.CAL_ACCESS_OWNER);
				values.put(CalendarContract.Calendars.CALENDAR_TIME_ZONE, TimeZone.getDefault().getID());
				values.put(CalendarContract.Calendars.ACCOUNT_TYPE, accountType);
				Uri calUri = CalendarContract.Calendars.CONTENT_URI;
				calUri = calUri.buildUpon()
					    .appendQueryParameter(CalendarContract.CALLER_IS_SYNCADAPTER, "true")
					    .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_NAME, accountName)
					    .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_TYPE, CalendarContract.ACCOUNT_TYPE_LOCAL)
					    .build();
				Uri rowUri = cr.insert(calUri, values);
				try
				{
					CalendarActivity.calendarID = Integer.parseInt(rowUri.getLastPathSegment());
				}
				catch(NumberFormatException e)
				{
					Log.w("CALENDAR", "Unable to retrieve ID of newly inserted calendar entry with existing account name and type. Error: "+e.toString());
				}
			}
			calendarCursor.close();
		}
		else
		{
			Log.e("CALENDAR", "Cannot find any calendars. Uh oh!!");
			//Toast.makeText(activity, R.string.errorFindingCalendars, Toast.LENGTH_SHORT).show();

			//Calendar does not exist => create and record id
			ContentResolver cr = activity.getContentResolver();
			ContentValues values = new ContentValues();
			values.put(CalendarContract.Calendars.NAME, CALENDAR_NAME);
			values.put(CalendarContract.Calendars.CALENDAR_DISPLAY_NAME, CALENDAR_NAME);
			values.put(CalendarContract.Calendars.ACCOUNT_NAME, CalendarContract.ACCOUNT_TYPE_LOCAL);
			values.put(CalendarContract.Calendars.OWNER_ACCOUNT, true);
			//Put a nice green color on all our events.
			values.put(CalendarContract.Calendars.CALENDAR_COLOR, 0xFF0ACC0A);
			values.put(CalendarContract.Calendars.CALENDAR_ACCESS_LEVEL, CalendarContract.Calendars.CAL_ACCESS_OWNER);
			values.put(CalendarContract.Calendars.CALENDAR_TIME_ZONE, TimeZone.getDefault().getID());
			values.put(CalendarContract.Calendars.ACCOUNT_TYPE, accountType);
			values.put(CalendarContract.Calendars.VISIBLE, 1);
			values.put(CalendarContract.Calendars.SYNC_EVENTS, 1);
			Uri calUri = CalendarContract.Calendars.CONTENT_URI;
			calUri = calUri.buildUpon()
				    .appendQueryParameter(CalendarContract.CALLER_IS_SYNCADAPTER, "true")
				    .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_NAME, accountName)
				    .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_TYPE, CalendarContract.ACCOUNT_TYPE_LOCAL)
				    .build();
			Uri rowUri = cr.insert(calUri, values);
			try
			{
				CalendarActivity.calendarID = Integer.parseInt(rowUri.getLastPathSegment());
			}
			catch(NumberFormatException e)
			{
				Log.w("CALENDAR", "Unable to retrieve ID of newly inserted default calendar entry. Error: "+e.toString());
			}
		}
		
		Uri eventsUri = CalendarContract.Events.CONTENT_URI;
		String[] eventProjection = new String[] {
		       CalendarContract.Events._ID,
		       CalendarContract.Events.TITLE,
		       CalendarContract.Events.DTSTART
		};

		Cursor eventCursor = activity.managedQuery(eventsUri, eventProjection, CalendarContract.Events.CALENDAR_ID+"=?",new String[]{Integer.toString(calendarID)}, null);
		
		ArrayList<AppointmentRecord> resultList = new ArrayList<AppointmentRecord>();
		
		Cursor reminderCursor = null;
		if(eventCursor!=null)
		{
			while(eventCursor.moveToNext())
			{
				//Not displaying reminder info right now
//				Uri remindersUri = CalendarContract.Reminders.CONTENT_URI;
//				String[] remindersProjection = new String[] {
//						CalendarContract.Reminders._ID,
//						CalendarContract.Reminders.MINUTES
//				};
//				
//				reminderCursor = managedQuery(remindersUri, remindersProjection, CalendarContract.Reminders.EVENT_ID + "=?", new String[]{ eventCursor.getString(1) }, null);
				
				AppointmentRecord record = new AppointmentRecord();
				String rowName = eventCursor.getString(1);
				String[] split = rowName.split(":");
				String type = (split.length==2) ? split[0] : "";
				String name = (split.length==2) ? split[1] : "";
				DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.getDefault());
				Date beginTime = new Date(eventCursor.getInt(2));
				record.setId(eventCursor.getInt(0));
				record.setName(name);
				record.setType(type);
				record.setDate(Long.parseLong(df.format(beginTime)));
				record.setDistanceDuration(Long.parseLong(df.format(new Date(System.currentTimeMillis() - beginTime.getTime()))));
				
				if(returnApptList)
				{
					resultList.add(record);
				}
			}
			eventCursor.close();
		}
		return resultList;
	}
	
	public static void loadTimesOrCreateRecurringMorningEveningEvents(Activity activity, String morningSurveyTime, String eveningSurveyTime, boolean populateValuesFromDB)
	{
		//Populate SettingsActivity values from OUR DB about morning and evening events (reload if need be; don't assume already loaded)
		if(populateValuesFromDB)
		{
			SettingsActivity.loadSettingsValuesFromDatabase(activity, false);
		}
		
		String[] morningTime = SettingsActivity.morningAlarm.split(":");
    	String[] eveningTime = SettingsActivity.eveningAlarm.split(":");
    	
    	int morningHour = -1, morningMinute = -1;
    	int eveningHour = -1, eveningMinute = -1;
    	
    	try
    	{
    		morningHour = Integer.parseInt(morningTime[0]);
    		morningMinute = Integer.parseInt(morningTime[1]);
    		eveningHour = Integer.parseInt(eveningTime[0]);
    		eveningMinute = Integer.parseInt(eveningTime[1]);
    	}
    	catch(NumberFormatException e)
    	{
    		Log.e("CALENDAR", "Could not format alarm start hour/minutes.");
    	}
		
		//See if morning survey exists based on name (if it doesn't, create it) + grab ID
		int morningEventID = getEventIDBasedOnTimeAndName(activity, CALENDAR_NAME, defaultSurveyTitle, 
				morningHour, morningMinute, -1, false, true);
		
		//Update survey time...
		updateEventTime(activity, morningEventID, morningHour, morningMinute);
		
		//See if evening survey exists based on time + grab ID
		int eveningEventID = getEventIDBasedOnTimeAndName(activity, CALENDAR_NAME, defaultSurveyTitle, 
				eveningHour, eveningMinute, morningEventID, false, true);
		//Update survey time...
		updateEventTime(activity, eveningEventID, eveningHour, eveningMinute);
	}
	
	public static void updateEventTime(Activity activity, int eventID, int newHour, int newMinute)
	{
		ContentResolver cr = activity.getContentResolver();
		ContentValues values = new ContentValues();
		Uri updateUri = null;
		//Update starting time for event
		long now = System.currentTimeMillis();
    	values.put(Events.DTSTART, now);
    	values.put(Events.DTEND, now+5*CalendarActivity.NUMBER_MILLISECONDS_IN_MINUTE);
		updateUri = ContentUris.withAppendedId(Events.CONTENT_URI, eventID);
		int rows = activity.getContentResolver().update(updateUri, values, null, null);
	}
	
	public static int getEventIDBasedOnTimeAndName(Activity activity, String calendarName, String eventName, int hours, int minutes, int excludeID, boolean compareTimes, boolean createIfDoesntExist)
	{
		int result = -1;
		
		//Check CalendarContract DB table for Events
		Uri eventsUri = CalendarContract.Events.CONTENT_URI;
		String[] eventProjection = new String[] {
		       CalendarContract.Events._ID,
		       CalendarContract.Events.TITLE,
		       CalendarContract.Events.DTSTART
		};

		Cursor eventCursor = activity.managedQuery(eventsUri, eventProjection, CalendarContract.Calendars.NAME+"=?",new String[]{calendarName}, null);
		
		if(eventCursor!=null)
		{
			while(eventCursor.moveToNext())
			{
				if(eventCursor.getString(1).contains("noPain:"))//compareTo(eventName)==0)
				{
					Date eventTime = new Date(eventCursor.getInt(2));
					if(compareTimes && (excludeID!=-1 && eventCursor.getInt(0)!=excludeID) &&
					   eventTime.getHours()==hours && eventTime.getMinutes()==minutes)
					{
						result = eventCursor.getInt(0);
						break;
					}
				}
			}
			eventCursor.close();
		}
		
		if(result==-1 && createIfDoesntExist)
		{
			//Create recurring event and return its ID
        	ContentValues values = new ContentValues();
        	Date now = new Date(System.currentTimeMillis());
        	
        	Date eventDate = now;
        	
        	String[] morningTime = SettingsActivity.morningAlarm.split(":");
        	String[] eveningTime = SettingsActivity.eveningAlarm.split(":");
        	
        	int morningHour = -1, morningMinute = -1;
        	int eveningHour = -1, eveningMinute = -1;
        	
        	try
        	{
        		morningHour = Integer.parseInt(morningTime[0]);
        		morningMinute = Integer.parseInt(morningTime[1]);
        		eveningHour = Integer.parseInt(eveningTime[0]);
        		eveningMinute = Integer.parseInt(eveningTime[1]);
        	}
        	catch(NumberFormatException e)
        	{
        		Log.e("CALENDAR", "Could not format alarm start hour/minutes.");
        	}
        	
        	//See what survey to start from - whether morning or evening is up next
        	if(now.getHours()>morningHour && now.getHours()<eveningHour ||
        	   (now.getHours()==morningHour && now.getMinutes()>=morningMinute) ||
        	   (now.getHours()==eveningHour && now.getMinutes()<eveningMinute))
        	{
        		//Insert evening
        		eventDate = new Date(now.getYear(),	now.getMonth(), now.getDay(), eveningHour, eveningMinute);
        	}
        	else
        	{
        		//Insert morning
        		eventDate = new Date(now.getYear(),	now.getMonth(), now.getDay(), morningHour, morningMinute);
        	}
        	values.put(Events.DTSTART, eventDate.getTime());
        	values.put(Events.DTEND, eventDate.getTime()+5*CalendarActivity.NUMBER_MILLISECONDS_IN_MINUTE);
        	values.put(Events.RRULE, 
        	      "FREQ=DAILY;");//COUNT=20;BYDAY=MO,TU,WE,TH,FR;WKST=MO");
        	values.put(Events.TITLE, defaultSurveyTitle);
        	values.put(Events.CALENDAR_ID, calendarID);
        	values.put(Events.EVENT_TIMEZONE, TimeZone.getDefault().getID());
        	values.put(Events.DESCRIPTION, "noPain: Daily survey");
        	// reasonable defaults exist:
        	values.put(Events.ACCESS_LEVEL, Events.ACCESS_PRIVATE);
        	values.put(Events.SELF_ATTENDEE_STATUS,
        	      Events.STATUS_CONFIRMED);
        	//values.put(Events.GUESTS_CAN_MODIFY, 1);
        	values.put(Events.AVAILABILITY, Events.AVAILABILITY_BUSY);
        	Uri uri = activity.getContentResolver().
        	            insert(Events.CONTENT_URI, values);
        	result = Long.valueOf(uri.getLastPathSegment()).intValue();
		}
		
		return result;
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
				holder=new CalendarRowViewHolder(row, false);
				row.setTag(holder);
			}
			
			AppointmentRecord record = (AppointmentRecord)getListAdapter().getItem(position);
			holder.recurring=record.isRecurring();
			holder.name.setText(getResources().getText(R.string.calendarRowNamePrefix) + record.getName());
			holder.type.setText(getResources().getText(R.string.calendarRowTypePrefix) + record.getType() + ", " + 
						(record.isRecurring() ? "Recurring" : "One Time"));
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
    	if (calendarID == -1) {
     	   // no calendar account; react meaningfully
     	   return false;
     	}
     	
     	Date now = new Date(System.currentTimeMillis());
     	String[] morningReminderTime = SettingsActivity.morningAlarm.split(SettingsActivity.REMINDER_TIME_DELIMITER);
     	String[] eveningReminderTime = SettingsActivity.eveningAlarm.split(SettingsActivity.REMINDER_TIME_DELIMITER);
        switch (item.getItemId()) {
        case R.id.calendar_menu_new_survey:
//    		Intent intentNewSurvey = new Intent(Intent.ACTION_EDIT);  
//        	intentNewSurvey.setType("vnd.android.cursor.item/event");
//        	intentNewSurvey.putExtra("title", "noPain: Survey");
//        	intentNewSurvey.putExtra("description", "noPain: Daily survey");
//        	intentNewSurvey.putExtra("beginTime", System.currentTimeMillis()+43200000);
//        	intentNewSurvey.putExtra("endTime", System.currentTimeMillis()+43500000);
//        	startActivity(intentNewSurvey);
        	
        	/*//How to insert recurring event
        	ContentValues values = new ContentValues();
        	values.put(Events.DTSTART, now.getTime());
        	values.put(Events.DTEND, now.getTime());
        	values.put(Events.RRULE, 
        	      "FREQ=DAILY;");//COUNT=20;BYDAY=MO,TU,WE,TH,FR;WKST=MO");
        	values.put(Events.TITLE, "noPain: Survey");
        	values.put(Events.CALENDAR_ID, calendarID);
        	values.put(Events.EVENT_TIMEZONE, TimeZone.getDefault().getID());
        	values.put(Events.DESCRIPTION, "noPain: Daily survey");
        	// reasonable defaults exist:
        	values.put(Events.ACCESS_LEVEL, Events.ACCESS_PRIVATE);
        	values.put(Events.SELF_ATTENDEE_STATUS,
        	      Events.STATUS_CONFIRMED);
        	//values.put(Events.GUESTS_CAN_MODIFY, 1);
        	values.put(Events.AVAILABILITY, Events.AVAILABILITY_BUSY);
        	Uri uri = 
        	      getContentResolver().
        	            insert(Events.CONTENT_URI, values);
        	long eventId = new Long(uri.getLastPathSegment());
        	*/
    		//Intent intentNewAppt = new Intent(Intent.ACTION_EDIT);
        	//intentNewAppt.setType("vnd.android.cursor.item/event");
        	
        	ContentValues valuesSurvey = new ContentValues();
        	valuesSurvey.put(Events.DTSTART, now.getTime() + 60*NUMBER_MILLISECONDS_IN_MINUTE);
        	valuesSurvey.put(Events.DTEND, now.getTime() + 65*NUMBER_MILLISECONDS_IN_MINUTE);
        	valuesSurvey.put(Events.TITLE, defaultSurveyTitle);
        	valuesSurvey.put(Events.CALENDAR_ID, calendarID);
        	valuesSurvey.put(Events.EVENT_TIMEZONE, TimeZone.getDefault().getID());
        	valuesSurvey.put(Events.DESCRIPTION, "noPain: Daily survey");
        	// reasonable defaults exist:
        	valuesSurvey.put(Events.ACCESS_LEVEL, Events.ACCESS_PRIVATE);
        	valuesSurvey.put(Events.SELF_ATTENDEE_STATUS,
        	      Events.STATUS_CONFIRMED);
        	//values.put(Events.GUESTS_CAN_MODIFY, 1);
        	valuesSurvey.put(Events.AVAILABILITY, Events.AVAILABILITY_BUSY);
        	Uri eventUri = 
        	      getContentResolver().
        	            insert(Events.CONTENT_URI, valuesSurvey);
        	long eventSurveyId = new Long(eventUri.getLastPathSegment());
        	
        	//Edit newly created event
    		Intent intent = new Intent(Intent.ACTION_EDIT)
    	    	.setData(ContentUris.withAppendedId(Events.CONTENT_URI, eventSurveyId));
    		startActivity(intent);
        	
        	break;
        case R.id.calendar_menu_new_appointment:
//    		Intent intentNewAppt = new Intent(Intent.ACTION_EDIT);
//        	intentNewAppt.setType("vnd.android.cursor.item/event");
//        	intentNewAppt.putExtra("title", "noPain: Appointment");
//        	intentNewAppt.putExtra("description", "noPain: Appointment details");
//        	intentNewAppt.putExtra("beginTime", System.currentTimeMillis());
//        	intentNewAppt.putExtra("endTime", System.currentTimeMillis()+300000);
//        	startActivity(intentNewAppt);
        	
        	ContentValues valuesAppt = new ContentValues();
        	valuesAppt.put(Events.DTSTART, now.getTime() + 60*NUMBER_MILLISECONDS_IN_MINUTE);
        	valuesAppt.put(Events.DTEND, now.getTime() + 90*NUMBER_MILLISECONDS_IN_MINUTE);
        	valuesAppt.put(Events.TITLE, defaultEventTitle);
        	valuesAppt.put(Events.CALENDAR_ID, calendarID);
        	valuesAppt.put(Events.EVENT_TIMEZONE, TimeZone.getDefault().getID());
        	valuesAppt.put(Events.DESCRIPTION, "noPain: Appointment");
        	// reasonable defaults exist:
        	valuesAppt.put(Events.ACCESS_LEVEL, Events.ACCESS_PRIVATE);
        	valuesAppt.put(Events.SELF_ATTENDEE_STATUS,
        	      Events.STATUS_CONFIRMED);
        	//values.put(Events.GUESTS_CAN_MODIFY, 1);
        	valuesAppt.put(Events.AVAILABILITY, Events.AVAILABILITY_BUSY);
        	Uri apptUri = 
        	      getContentResolver().
        	            insert(Events.CONTENT_URI, valuesAppt);
        	long eventId = new Long(apptUri.getLastPathSegment());
        	
        	//Edit newly created event
    		Intent apptIntent = new Intent(Intent.ACTION_EDIT)
    	    	.setData(ContentUris.withAppendedId(Events.CONTENT_URI, eventId));
    		startActivity(apptIntent);
        	
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
	boolean recurring=false;
//	TextView reminderPeriod=null;
	
	CalendarRowViewHolder(View row, boolean recurring) {
		this.name=(TextView)row.findViewById(R.id.rowViewEventName);
		this.type=(TextView)row.findViewById(R.id.rowViewEventType);
		this.date=(TextView)row.findViewById(R.id.rowViewEventDate);
		this.distance=(TextView)row.findViewById(R.id.rowViewEventDistance);
		this.recurring=recurring;
//		this.reminderPeriod=(TextView)row.findViewById(R.id.rowViewEventReminderPeriod);
	}
}