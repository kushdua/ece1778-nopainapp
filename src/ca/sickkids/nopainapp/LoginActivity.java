package ca.sickkids.nopainapp;

import java.io.File;
import java.util.concurrent.ExecutionException;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.text.Editable;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends Activity {

	private Button btnLogin = null, btnRegister = null;
	private EditText userName = null, pass = null;
	public static Activity activity = null;
	DBHelper dbHelper = null;
	
	public static int userID = -1;
	public static String user_name = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_screen);
		
		btnLogin = (Button)findViewById(R.id.btnLogin);
		btnRegister = (Button)findViewById(R.id.btnRegister);
		userName = (EditText)findViewById(R.id.fieldUser);
		pass = (EditText)findViewById(R.id.fieldPass);
		
		activity=this;
		dbHelper = new DBHelper(this, HomeActivity.DB_NAME, null, HomeActivity.DB_VERSION);
		
		userName.requestFocus();
	}
    
    public void onLoginClickHandler(View v)
    {
		//Check with DB records and redirect to Home or clear + focus on fields here
    	SQLiteDatabase db = dbHelper.getReadableDatabase();
    	if(db!=null)
    	{
			String args[] = { userName.getText().toString(), pass.getText().toString() };
	    	//db.execSQL("SELECT name, pass FROM users WHERE name=? AND pass=?;", args);
			Cursor result = db.rawQuery("SELECT id, name, pass FROM users WHERE name=? AND pass=?;", args);
			if(result != null && result.getCount()==1 && result.moveToNext())
			{
				userID = result.getInt(0);
				result.close();
				LoginActivity.user_name = userName.getText().toString();
				this.pass.setText("");
				this.userName.setText("");
				Toast.makeText(activity, R.string.successLogin, Toast.LENGTH_SHORT).show();
				Intent intent = new Intent(v.getContext(), HomeActivity.class);
				startActivity(intent);
			}
			else
			{
				Toast.makeText(activity, R.string.errorLoggingIn, Toast.LENGTH_SHORT).show();
			}
    	}
    	else
    	{
			Toast.makeText(activity, R.string.errorLoggingIn, Toast.LENGTH_SHORT).show();
    	}
    }
    
    public void registerUser(String name, String pass)
    {
    	SQLiteDatabase db = dbHelper.getWritableDatabase();
    	if(db!=null)
    	{
    		try
    		{
				//String args[] = { userName.getText().toString(), pass.getText().toString() };
		    	//db.execSQL("INSERT INTO users VALUES(?,?);", args);
    			
    			String args[] = { name, pass };
    	    	//db.execSQL("SELECT name, pass FROM users WHERE name=? AND pass=?;", args);
    			Cursor result = db.rawQuery("SELECT name, pass FROM users WHERE name=? AND pass=?;", args);
    			if(result != null && result.getCount() != -1 && result.getCount()==1)
    			{
					this.pass.setText("");
					this.userName.setText("");
					
					Cursor selectResult = db.rawQuery("SELECT id FROM users WHERE name=? AND pass=?;", args);
					if(selectResult != null && selectResult.getCount()==1 && selectResult.moveToNext())
					{
						userID = selectResult.getInt(0);
						selectResult.close();
					}
					
					Toast.makeText(activity, R.string.errorRegisteringUsernameTaken, Toast.LENGTH_SHORT).show();
					return;
    			}
    			
				ContentValues values = new ContentValues(2);
				values.put("name", name);
				values.put("pass", pass);
				long numRows = db.insertOrThrow("users", null, values);
				if(numRows != -1)
				{
					this.pass.setText("");
					this.userName.setText("");
					
					Cursor selectResult = db.rawQuery("SELECT id FROM users WHERE name=? AND pass=?;", args);
					if(selectResult != null && selectResult.getCount()==1 && selectResult.moveToNext())
					{
						userID = selectResult.getInt(0);
						selectResult.close();
					}
					
					ContentValues settingsValues = new ContentValues(5);
					settingsValues.put("userID", userID);
					settingsValues.put("disease", "");
					settingsValues.put("reminder", "30 minutes");
					settingsValues.put("morningSurveyTime", "10:00");
					settingsValues.put("eveningSurveyTime", "22:00");
					long numSettingsRows = db.insertOrThrow("settings", null, settingsValues);
					if(numSettingsRows == -1 || numSettingsRows!=1)
					{
						Log.e("LOGIN SETTINGS","Cannot save default settings for new user. "+numSettingsRows+" rows updated.");
					}
					
					LoginActivity.user_name = name;
					Toast.makeText(activity, R.string.successRegistering, Toast.LENGTH_SHORT).show();
					Intent intent = new Intent(activity, HomeActivity.class);
					startActivity(intent);
				}
				else
				{
					//Error registering user
					this.pass.setText("");
					Toast.makeText(activity, R.string.errorRegisteringUnknown, Toast.LENGTH_SHORT).show();
				}
    		}
    		catch(SQLException e)
    		{
    			Log.e("LOGIN", "Error registering user " + e.getMessage());
				this.pass.setText("");
				Toast.makeText(activity, R.string.errorRegisteringUnknown, Toast.LENGTH_SHORT).show();
    		}
    	}
    	else
    	{
			this.pass.setText("");
			Toast.makeText(activity, R.string.errorRegisteringUnknown, Toast.LENGTH_SHORT).show();
    	}
    }
    
    public void onRegisterClickHandler(View v)
    {
    	//Insert into DB; if successful redirect + Toast indicating success
    	final String enteredUserName = userName.getText().toString();
    	
    	AlertDialog.Builder alert = new AlertDialog.Builder(this);
    	
    	alert.setTitle("Confirm Password");
    	alert.setMessage("Please re-enter the password you entered for registering user \"" + enteredUserName +"\".");

    	// Set an EditText view to get user input 
    	final EditText input = new EditText(this);
    	input.setSingleLine();
    	
    	input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
    	alert.setView(input);

    	alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
    		public void onClick(DialogInterface dialog, int whichButton) {
				if(input.getText().toString().compareTo(enteredUserName)==0)
				{
					registerUser(enteredUserName, pass.getText().toString());
				}
				else
				{
					Toast.makeText(activity, R.string.errorConfirmingPassword, Toast.LENGTH_SHORT).show();
	    			pass.setText("");
	    			pass.requestFocus();
				}
    		}
    	});

    	alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
    		public void onClick(DialogInterface dialog, int whichButton) {
    			pass.setText("");
    			pass.requestFocus();
    		}
    	});
    	
    	alert.show();
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_home, menu);
		return true;
	}

}
