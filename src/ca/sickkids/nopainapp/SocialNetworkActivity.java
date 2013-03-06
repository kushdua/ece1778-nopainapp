package ca.sickkids.nopainapp;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

public class SocialNetworkActivity extends Activity {

	private Button journal = null, exit = null;
	public static Activity activity = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.social_network_activity);
	}

	public void onFacebookClickHandler(View v)
	{
		Intent intent = new Intent("android.intent.category.LAUNCHER");
		intent.setClassName("com.facebook.katana", "com.facebook.katana.LoginActivity");
		startActivity(intent);
	}
	
	public void onTwitterClickHandler(View v)
	{
		Intent intent = new Intent("android.intent.category.LAUNCHER");
		intent.setClassName("com.twitter.android", "com.twitter.android.PostActivity");
		startActivity(intent);
	}

}
