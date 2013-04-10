package ca.sickkids.nopainapp;

import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class GoalsActivity extends Activity {

	public static Activity activity = null;
	EditText goalInput = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		activity=this;
		setContentView(R.layout.goals);
		goalInput = (EditText)findViewById(R.id.txtInputGoal);
	}

	public void onFacebookShareClickHandler(View v)
	{
//		Intent intent = new Intent("android.intent.category.LAUNCHER");
//		intent.setClassName("com.facebook.katana", "com.facebook.katana.ShareLinkActivity");
//		startActivity(intent); 
		ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE); 
		ClipData clip = ClipData.newPlainText("NOpain goal", goalInput.getText().toString());
		clipboard.setPrimaryClip(clip);
		
		Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "NOpain Goal");
        sharingIntent.putExtra(Intent.EXTRA_TEXT, goalInput.getText().toString());
        sharingIntent.setPackage("com.facebook.katana");
        Toast.makeText(activity, R.string.copiedGoalClipboard, Toast.LENGTH_SHORT);
        startActivity(sharingIntent);
	}
	
	public void onTwitterShareClickHandler(View v)
	{
//		Intent intent = new Intent(Intent.ACTION_VIEW);
//		intent.setData(Uri.parse("http://twitter.com/?status=" + Uri.encode(goalInput.getText().toString())));
//		startActivity(intent);
//		Intent sharingIntent = new Intent(Intent.ACTION_SEND);
//        sharingIntent.setClassName("com.twitter.android","com.twitter.android.PostActivity");
//        sharingIntent.putExtra(Intent.EXTRA_TEXT, goalInput.getText().toString());
//        startActivity(sharingIntent);

		ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE); 
		ClipData clip = ClipData.newPlainText("NOpain goal", goalInput.getText().toString());
		clipboard.setPrimaryClip(clip);
		
		Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "NOpain Goal");
        sharingIntent.putExtra(Intent.EXTRA_TEXT, goalInput.getText().toString());
        sharingIntent.setPackage("com.twitter.android");
        Toast.makeText(activity, R.string.copiedGoalClipboard, Toast.LENGTH_SHORT);
        startActivity(sharingIntent);
	}

}
