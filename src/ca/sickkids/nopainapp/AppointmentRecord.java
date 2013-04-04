package ca.sickkids.nopainapp;

public class AppointmentRecord {
	private int id = -1;
	private String name = "";
	private int userID = -1;
	private String type = "";
	private long date = -1;
	private long distanceDuration = -1;
	private int reminderDurationPeriodMinutes = -1;
	private boolean isRecurring = false;
	
	//Default constructor
	public AppointmentRecord()
	{
		id = -1;
		name = "";
		userID = -1;
		type = Types.TYPE_SURVEY.toString();
		date = System.currentTimeMillis();
		setDistanceDuration(0);
		reminderDurationPeriodMinutes = 5;
		setRecurring(false);
	}
	
	//Constructor for db record to memory structure or when saving event perhaps
	public AppointmentRecord(int id, String name, int userID, String type, long date, long distanceDuration, int reminderDurationMinutes, boolean isRecurring)
	{
		this.id = id;
		this.name = name;
		this.userID = userID;
		this.type = type;
		this.date = date;
		this.setDistanceDuration(distanceDuration);
		this.reminderDurationPeriodMinutes = reminderDurationMinutes;
		this.setRecurring(isRecurring);
	}
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}

	public int getUserID() {
		return userID;
	}

	public void setUserID(int userID) {
		this.userID = userID;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public long getDate() {
		return date;
	}

	public void setDate(long date) {
		this.date = date;
	}

	public int getReminderDuration() {
		return reminderDurationPeriodMinutes;
	}

	public void setReminderDuration(int reminderDurationMinutes) {
		this.reminderDurationPeriodMinutes = reminderDurationMinutes;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getDistanceDuration() {
		return distanceDuration;
	}

	public void setDistanceDuration(long distanceDuration) {
		this.distanceDuration = distanceDuration;
	}

	public boolean isRecurring() {
		return isRecurring;
	}

	public void setRecurring(boolean isRecurring) {
		this.isRecurring = isRecurring;
	}

	public enum Types {
	    TYPE_SURVEY("SURVEY"),
	    TYPE_APPOINTMENT("APPOINTMENT")
	    ;
	    /**
	     * @param text
	     */
	    private Types(final String text) {
	        this.text = text;
	    }

	    private final String text;

	    /* (non-Javadoc)
	     * @see java.lang.Enum#toString()
	     */
	    @Override
	    public String toString() {
	        return text;
	    }
	}
}