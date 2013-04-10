package ca.sickkids.nopainapp;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class DiseaseInfoActivity extends Activity{
	
	TextView tv;
	String cancer = "Cancer is not just one disease. There are over 100 different types of cancer, each with" +
					"different names, effects, and treatments. Even though each type of cancer is different" +
					"there are some basic things that are similar to most cancers." +
					"In order to understand more about cancer, you first need to understand a bit about" +
					"cells.";
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.education_mod);
		//addListenerOnButton();
		 tv = (TextView) findViewById(R.id.textView1);

         tv.setText(cancer);

	}
}
