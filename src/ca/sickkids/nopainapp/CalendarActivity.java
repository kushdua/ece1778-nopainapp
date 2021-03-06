package ca.sickkids.nopainapp;

import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.graphics.Color;
import android.location.Address;
import android.media.audiofx.BassBoost.Settings;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Events;
import android.provider.CalendarContract.Reminders;

import java.text.DateFormat;

import android.text.InputType;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemLongClickListener;

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
	
	ArrayList<AppointmentRecord> list = new ArrayList<AppointmentRecord>();
	ListRecordAdapter adapter = null;
	
	private static final String recurrenceRRule = "FREQ=DAILY;";
	
	private static int selectedListItem = -1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.calendar_activity);
		
		activity = this;
		
		//Concatenate entered (on top) and loaded lists for display
		ArrayList<AppointmentRecord> entriesLoadedFromDB = initializeCalendarID(this, CALENDAR_NAME, true);
		
		list.addAll(entriesLoadedFromDB);
		
		adapter = new ListRecordAdapter(this, R.layout.row_view_events, R.id.rowViewEventName, list);
		
		setListAdapter(adapter);
	}
	
    protected void onResume() {
        super.onResume();
        
        list.clear();
        list.addAll(initializeCalendarID(activity, CALENDAR_NAME, true));
        adapter.notifyDataSetChanged();
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
				if(calendarCursor.getString(3).compareTo(CALENDAR_NAME + ":" + Integer.toString(LoginActivity.userID))==0)
				{
					//TO MAKE SURE CREATED CALENDAR IS PICKED UP HAVE THIS UNCOMMENTED
					foundOurs = true;
					CalendarActivity.accountName = calendarCursor.getString(1);
					CalendarActivity.accountType = calendarCursor.getString(2);
					CalendarActivity.calendarID = calendarCursor.getInt(0);
					break;
				}
				else if(calendarCursor.getString(3).compareTo(CALENDAR_NAME)==0)
				{
					ContentValues values = new ContentValues();
					Uri deleteUrl = ContentUris.withAppendedId(CalendarContract.Calendars.CONTENT_URI, calendarCursor.getInt(0));
					int rows = activity.getContentResolver().delete(deleteUrl, null, null);
					continue;
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
				values.put(CalendarContract.Calendars.NAME, CALENDAR_NAME + ":" + Integer.toString(LoginActivity.userID));
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
			//calendarCursor.close();
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
		       CalendarContract.Events.DTSTART,
		       CalendarContract.Events.RRULE
		};

		Cursor eventCursor = activity.managedQuery(eventsUri, eventProjection, CalendarContract.Events.CALENDAR_ID+"=?",new String[]{Integer.toString(calendarID)}, null);
		
		ArrayList<AppointmentRecord> resultList = new ArrayList<AppointmentRecord>();
		
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
				String type = (split.length==2) ? split[0].trim() : "";
				String name = (split.length==2) ? split[1].trim() : "";
				//DateFormat df = DateFormat.getDateInstance(DateFormat.LONG, Locale.getDefault());
				Date beginTime = new Date((long)Math.abs(eventCursor.getLong(2)));
				record.setId(eventCursor.getInt(0));
				record.setName(name);
				record.setType(type);
				record.setDate(beginTime.getTime());//df.format(beginTime)));
				String rrule = eventCursor.getString(3);
				record.setRecurring(false);
				if(rrule!=null && rrule.contains(CalendarActivity.recurrenceRRule))
				{
					record.setRecurring(true);
				}
				//Only display upcoming events... - DISPLAY ALL - ONES IN PAST MIGHT BE RECURRING...
				if(returnApptList) //&& beginTime.getTime() > System.currentTimeMillis())
				{
					resultList.add(record);
				}
			}
			//eventCursor.close();
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
		if(morningRecurringEventID==-1)
		{
    		morningRecurringEventID = getEventIDBasedOnTimeAndName(activity, calendarID, defaultSurveyTitle, morningHour, morningMinute, true, false, -1, true, true, true);
				//(activity, calendarID, defaultSurveyTitle, 
				//morningHour, morningMinute, -1, false, true);
		}
		else
		{
			//Update survey time...
			updateEventTime(activity, morningRecurringEventID, morningHour, morningMinute, true, -1);
		}
		
		//See if evening survey exists based on time + grab ID
		if(eveningRecurringEventID==-1)
		{
			eveningRecurringEventID = getEventIDBasedOnTimeAndName(activity, calendarID, defaultSurveyTitle, eveningHour, eveningMinute, false, true, -1, true, true, true);
				//(activity, calendarID, defaultSurveyTitle, 
				//eveningHour, eveningMinute, morningEventID, false, true);
		}
		else
		{
			//Update survey time...
			updateEventTime(activity, eveningRecurringEventID, eveningHour, eveningMinute, true, -1);
		}
	}
	
	public static void updateEventTime(Activity activity, int eventID, int newHour, int newMinute, boolean isRecurring, int nonRecurringDurationMinutes)
	{
		ContentResolver cr = activity.getContentResolver();
		ContentValues values = new ContentValues();
		Uri updateUri = null;
		//Update starting time for event
		long nowTime = System.currentTimeMillis();
		Date now = new Date(System.currentTimeMillis());
		long newTime = new Date(now.getYear(), now.getMonth(), now.getDay(), newHour, newMinute).getTime();
    	values.put(Events.DTSTART, newTime);
    	if(!isRecurring && nonRecurringDurationMinutes!=-1)
    	{
    		values.put(Events.DTEND, newTime+nonRecurringDurationMinutes*CalendarActivity.NUMBER_MILLISECONDS_IN_MINUTE);
    	}
    	else if(!isRecurring)
    	{
    		//Default 5 minutes event duration if not specified as param
    		values.put(Events.DTEND, newTime+5*CalendarActivity.NUMBER_MILLISECONDS_IN_MINUTE);
    	}
    	//values.put(Events.DTEND, now+5*CalendarActivity.NUMBER_MILLISECONDS_IN_MINUTE);
		updateUri = ContentUris.withAppendedId(Events.CONTENT_URI, eventID);
		int rows = activity.getContentResolver().update(updateUri, values, null, null);
	}
	
	public static int getEventIDBasedOnTimeAndName(Activity activity, int calendarID, String eventName, int hours, int minutes, boolean isMorningRecurring, boolean isEveningRecurring, int excludeID, boolean compareTimes, boolean createIfDoesntExist, boolean createRecurring)
	{
		int result = -1;
		
		//Check CalendarContract DB table for Events
		Uri eventsUri = CalendarContract.Events.CONTENT_URI;
		String[] eventProjection = new String[] {
		       CalendarContract.Events._ID,
		       CalendarContract.Events.TITLE,
		       CalendarContract.Events.DTSTART,
		       CalendarContract.Events.RRULE
		};

		Cursor eventCursor = activity.managedQuery(eventsUri, eventProjection, CalendarContract.Events.CALENDAR_ID+"=?",new String[]{Integer.toString(calendarID)}, null);
		
		if(eventCursor!=null)
		{
			while(eventCursor.moveToNext())
			{
				if(eventCursor.getString(1).contains("noPain:"))//compareTo(eventName)==0)
				{
					Date eventTime = new Date(eventCursor.getLong(2));
					if((compareTimes || (excludeID!=-1 && eventCursor.getInt(0)!=excludeID)) &&
					   eventTime.getHours()==hours && eventTime.getMinutes()==minutes)
					{
						//If event is said to be recurring but one in DB is not, continue searching for the event the user specified
						if((isMorningRecurring || isEveningRecurring || createRecurring) &&
						   eventCursor.getString(3)!=null && eventCursor.getString(3).compareTo(CalendarActivity.recurrenceRRule)!=0)
						{
							continue;
						}
						result = eventCursor.getInt(0);
						break;
					}
				}
			}
			//eventCursor.close();
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
        	eventDate = new Date(now.getYear(), now.getMonth(), now.getDay(), hours, minutes);
//        	if(isMorningRecurring ||
//        	   (now.getHours()>morningHour && now.getHours()<eveningHour) ||
//        	   (now.getHours()==morningHour && now.getMinutes()>=morningMinute) ||
//        	   (now.getHours()==eveningHour && now.getMinutes()<eveningMinute))
//        	{
//        		//Insert evening
//        		eventDate = new Date(now.getYear(),	now.getMonth(), now.getDay(), eveningHour, eveningMinute);
//        	}
//        	else
//        	{
//	        	if(isEveningRecurring)
//	        	{
//	        		//Insert morning
//	        		eventDate = new Date(now.getYear(),	now.getMonth(), now.getDay(), morningHour, morningMinute);
//	        	}
//        	}
        	values.put(Events.DTSTART, eventDate.getTime());
        	if(isMorningRecurring || isEveningRecurring || createRecurring)
        	{
	        	values.put(Events.RRULE, 
	        	      CalendarActivity.recurrenceRRule);//COUNT=20;BYDAY=MO,TU,WE,TH,FR;WKST=MO");
	        	//5 minute duration by default
	        	values.put(Events.DURATION, "P5M");
        	}
        	else
        	{
            	values.put(Events.DTEND, eventDate.getTime()+5*CalendarActivity.NUMBER_MILLISECONDS_IN_MINUTE);
        	}
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
        	
        	ContentValues valuesReminder = new ContentValues();
        	valuesReminder.put(Reminders.EVENT_ID, result);
        	valuesReminder.put(Reminders.MINUTES, (SettingsActivity.reminderMinutes>0)?SettingsActivity.reminderMinutes:30);
        	valuesReminder.put(Reminders.METHOD, Reminders.METHOD_ALERT);
        	long reminderID = new Long(activity.getContentResolver().insert(Reminders.CONTENT_URI, valuesReminder).getLastPathSegment());
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
//			if(position!=selectedListItem)
//			{
//				row.setBackgroundColor(Color.TRANSPARENT);
//			}
//			else
//			{
//				row.setBackgroundColor(Color.LTGRAY);
//			}

			CalendarRowViewHolder holder=(CalendarRowViewHolder)row.getTag();
			if (holder==null) {
				holder=new CalendarRowViewHolder(row, false);
				row.setTag(holder);
			}

			AppointmentRecord record = (AppointmentRecord)getListAdapter().getItem(position);
			
			Date now = new Date(System.currentTimeMillis());
			Date date = new Date(record.getDate());
			
			holder.recurring=record.isRecurring();
			holder.name.setText(getResources().getText(R.string.calendarRowNamePrefix) + " " + record.getName());
			holder.type.setText(getResources().getText(R.string.calendarRowTypePrefix) + " " + record.getType() + ", " + 
						(record.isRecurring() ? "Recurring daily" : "One Time"));
			if(!holder.recurring)
			{
				holder.date.setText(getResources().getText(R.string.calendarRowDatePrefix) + " " + new Date(record.getDate()).toLocaleString());
			}
			else
			{
				int hour = date.getHours();
				int minutes = date.getMinutes();
				holder.date.setText(getResources().getText(R.string.calendarRowDatePrefix) + " " + (hour>12?hour-12:hour) + ":" + (minutes<10 ? "0"+minutes : minutes) + (hour<12 ? " AM" : " PM"));
			}
			
			if(!holder.recurring)
			{
				holder.distance.setText(getResources().getText(R.string.calendarRowDurationUntilPrefix) + " " + DateUtils.getRelativeTimeSpanString(date.getTime(), System.currentTimeMillis(),DateUtils.MINUTE_IN_MILLIS, DateUtils.FORMAT_ABBREV_RELATIVE).toString());
				//holder.distance.setVisibility(View.VISIBLE);
			}
			else
			{
				//as future improvement fix distance to appear proper and not 7 days from now
				holder.distance.setVisibility(View.INVISIBLE);
				if(now.getHours() > date.getHours())
				{
					holder.distance.setText(getResources().getText(R.string.calendarRowDurationUntilPrefix) + " " + DateUtils.getRelativeTimeSpanString(date.getTime()+86400000, System.currentTimeMillis(),DateUtils.MINUTE_IN_MILLIS, DateUtils.FORMAT_ABBREV_RELATIVE).toString());
				}
				else
				{
					holder.distance.setText(getResources().getText(R.string.calendarRowDurationUntilPrefix) + " " + DateUtils.getRelativeTimeSpanString(date.getTime(), System.currentTimeMillis(),DateUtils.MINUTE_IN_MILLIS, DateUtils.FORMAT_ABBREV_RELATIVE).toString());
				}
			}
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
    	
    	if((selectedListItem==-1 || selectedListItem>=list.size()) && 
    	   (item.getItemId()==R.id.calendar_menu_edit_event || item.getItemId()==R.id.calendar_menu_deleteEvent))
    	{
    		Toast.makeText(activity, R.string.err_calendar_no_event_selected, Toast.LENGTH_SHORT);
    		return false;
    	}
     	
     	Date now = new Date(System.currentTimeMillis());
        switch (item.getItemId()) {
        case R.id.calendar_menu_edit_event:
        	if(selectedListItem!=-1)
        	{
        		Intent intent = new Intent(Intent.ACTION_EDIT)
        			.setData(ContentUris.withAppendedId(Events.CONTENT_URI, ((AppointmentRecord)(getListAdapter().getItem(selectedListItem))).getId()));
        		startActivity(intent);
        	}
        	break;
        case R.id.calendar_menu_deleteCalendar:
        	ContentValues values = new ContentValues();
			Uri deleteUrl = ContentUris.withAppendedId(CalendarContract.Calendars.CONTENT_URI, calendarID);
			int rows = activity.getContentResolver().delete(deleteUrl, null, null);
			CalendarActivity.calendarID=-1;
			CalendarActivity.morningRecurringEventID=-1;
			CalendarActivity.eveningRecurringEventID=-1;
			list.clear();
			initializeCalendarID(activity, CALENDAR_NAME, false);
			loadTimesOrCreateRecurringMorningEveningEvents(activity, SettingsActivity.morningAlarm,	SettingsActivity.eveningAlarm, false);
			HomeActivity.activity.runOnUiThread(new Runnable(){
				public void run()
				{
					list=initializeCalendarID(activity, CALENDAR_NAME, true);
					adapter.notifyDataSetChanged();
				}
			});
        	break;
        case R.id.calendar_menu_deleteEvent:
        	if(selectedListItem!=-1)
        	{
        		AlertDialog.Builder alert = new AlertDialog.Builder(this);
        		
            	alert.setTitle("Delete event?");
            	alert.setMessage("Do you want to delete the selected event?");
        
            	alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            		public void onClick(DialogInterface dialog, int whichButton) {
            			long eventID = ((AppointmentRecord)(getListAdapter().getItem(selectedListItem))).getId();
            			ContentResolver cr = getContentResolver();
            			ContentValues values = new ContentValues();
            			Uri deleteUri = null;
            			deleteUri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, eventID);
            			int rows = getContentResolver().delete(deleteUri, null, null);
            			
            			if(rows==1)
            			{
            				Toast.makeText(activity, R.string.successDeleteEvent, Toast.LENGTH_SHORT).show();
            				activity.runOnUiThread(new Runnable() {
        						@Override
        						public void run() {
        							if(selectedListItem<list.size())
        							{
        								list.remove(selectedListItem);
        								adapter.notifyDataSetChanged();
        							}
        						}
            				});
            			}
            			else
            			{
            				Toast.makeText(activity, R.string.errorDeleteEvent, Toast.LENGTH_SHORT).show();
            			}
            		}
            	});
        
            	alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
            		public void onClick(DialogInterface dialog, int whichButton) {
            			//TODO do nothing
            		}
            	});
            	
            	alert.show();
        	}
        	break;
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
        	valuesSurvey.put(Events.DESCRIPTION, "noPain: One time survey");
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
        	
        	ContentValues valuesReminder = new ContentValues();
        	valuesReminder.put(Reminders.EVENT_ID, eventSurveyId);
        	valuesReminder.put(Reminders.MINUTES, (SettingsActivity.reminderMinutes>0)?SettingsActivity.reminderMinutes:30);
        	valuesReminder.put(Reminders.METHOD, Reminders.METHOD_ALERT);
        	long reminderID = new Long(getContentResolver().insert(Reminders.CONTENT_URI, valuesReminder).getLastPathSegment());
        	
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
        	
        	ContentValues valuesReminderAppt = new ContentValues();
        	valuesReminderAppt.put(Reminders.EVENT_ID, eventId);
        	valuesReminderAppt.put(Reminders.MINUTES, (SettingsActivity.reminderMinutes>0)?SettingsActivity.reminderMinutes:30);
        	valuesReminderAppt.put(Reminders.METHOD, Reminders.METHOD_ALERT);
        	long reminderApptID = new Long(getContentResolver().insert(Reminders.CONTENT_URI, valuesReminderAppt).getLastPathSegment());
        	
        	//Edit newly created event
    		Intent apptIntent = new Intent(Intent.ACTION_EDIT)
    	    	.setData(ContentUris.withAppendedId(Events.CONTENT_URI, eventId));
    		startActivity(apptIntent);
        	
        	break;
        default:
            return super.onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);
    }
	

	@Override
	public void onListItemClick(ListView parent, View v, int position,long id)
	{
		selectedListItem = position;
//		v.setBackgroundColor(Color.LTGRAY);
//		final int itemPos = position;
//		AlertDialog.Builder alert = new AlertDialog.Builder(this);
//		
//    	alert.setTitle("Delete event?");
//    	alert.setMessage("Do you want to delete the selected event?");
//
//    	alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//    		public void onClick(DialogInterface dialog, int whichButton) {
//    			long eventID = ((AppointmentRecord)(getListAdapter().getItem(itemPos))).getId();
//    			ContentResolver cr = getContentResolver();
//    			ContentValues values = new ContentValues();
//    			Uri deleteUri = null;
//    			deleteUri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, eventID);
//    			int rows = getContentResolver().delete(deleteUri, null, null);
//    			
//    			if(rows==1)
//    			{
//    				Toast.makeText(activity, R.string.successDeleteEvent, Toast.LENGTH_SHORT).show();
//    				activity.runOnUiThread(new Runnable() {
//						@Override
//						public void run() {
//							if(itemPos<list.size())
//							{
//								list.remove(itemPos);
//								adapter.notifyDataSetChanged();
//							}
//						}
//    				});
//    			}
//    			else
//    			{
//    				Toast.makeText(activity, R.string.errorDeleteEvent, Toast.LENGTH_SHORT).show();
//    			}
//    		}
//    	});
//
//    	alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
//    		public void onClick(DialogInterface dialog, int whichButton) {
//    			//TODO do nothing
//    		}
//    	});
//    	
//    	alert.show();
//		Intent intent = new Intent(Intent.ACTION_EDIT)
//			.setData(ContentUris.withAppendedId(Events.CONTENT_URI, ((AppointmentRecord)(getListAdapter().getItem(position))).getId()));
//		startActivity(intent);
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