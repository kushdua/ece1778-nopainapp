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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		addListenerOnButton();
		activity=this;
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


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_home, menu);
		return true;
	}

}
