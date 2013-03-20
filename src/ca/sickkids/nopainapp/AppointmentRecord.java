package ca.sickkids.nopainapp;

public class AppointmentRecord {
	private int id = -1;
	private int userID = -1;
	private String type = "";
	private long date = -1;
	private int reminderDurationMinutes = -1;
	
	//Default constructor
	public AppointmentRecord()
	{
		id = -1;
		userID = -1;
		type = Types.TYPE_SURVEY.toString();
		date = System.currentTimeMillis();
		reminderDurationMinutes = 5;
	}
	
	//Constructor for db record to memory structure or when saving event perhaps
	public AppointmentRecord(int id, int userID, String type, long date, int reminderDurationMinutes)
	{
		this.id = id;
		this.userID = userID;
		this.type = type;
		this.date = date;
		this.reminderDurationMinutes = reminderDurationMinutes;
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
		return reminderDurationMinutes;
	}

	public void setReminderDuration(int reminderDurationMinutes) {
		this.reminderDurationMinutes = reminderDurationMinutes;
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