package ca.sickkids.nopainapp;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.VideoView;

public class Journaling extends Activity{

	private EditText talkabout = null;
	private Button Jvideo = null, Jtext = null, done = null, view = null;
	public static final int MEDIA_TYPE_IMAGE = 1;
	public static final int MEDIA_TYPE_VIDEO = 2;
	private static final int CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE = 200;
	private VideoView mVideoView;
	private Uri mVideoUri;
	private static String root;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.journaling);
		addListenerOnButton();
	}
	
    public void addListenerOnButton()
    {	
    	talkabout=(EditText) findViewById(R.id.multiAutoCompleteTextView1);
		Jvideo = (Button) findViewById(R.id.btnVideo);
		Jtext = (Button) findViewById(R.id.btnText);
    	done = (Button) findViewById(R.id.btnDone);
    	view = (Button) findViewById(R.id.btnView);

		
		//Text Journal button click handler
		Jtext.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				textjournalhandler(v);
			}
    	});
		
		//Video Journal button click handler
		Jvideo.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				videojournalhandler(v);
			}

    	});	
		
		//Video Journal button click handler
		view.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				/*
				 * Launch a new intent to view the list of videos 
				 */
				//JOURNAL Button Handler
				Intent intent = new Intent(v.getContext(), VideoActivity.class);
				startActivity(intent);	
				
				
			}

    	});
		
    	//EXIT Button Handler
    	done.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
					finish();
			}
    	}); 
    }
    
    public void textjournalhandler(View v)	{

    	if(talkabout.getText().length()==0)
    	{
    		Toast.makeText(this, R.string.erroremptytext, Toast.LENGTH_SHORT).show();
    		talkabout.requestFocus();
    	}
    	
    	else if(talkabout.getText().equals("Talk about your experiences")) {
    		Toast.makeText(this, R.string.errorinvalidtext, Toast.LENGTH_SHORT).show();
    		talkabout.requestFocus();
    	}
    	
       	else
    	{
       		/*
       		 * TODO : Handle the text entered by user in database or something
       		 */
       		clearInputs();
			Toast.makeText(this, R.string.EntrySaved, Toast.LENGTH_SHORT).show();
    	}
    	
    		
    }
    

	private void videojournalhandler(View v) {
		if(ifcamera())	{
			Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
			mVideoUri = getOutputMediaFileUri(MEDIA_TYPE_VIDEO);  // create a file to save the video

			takeVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT, mVideoUri);  // set the video file name
			takeVideoIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1); // set the video image quality to high

			startActivityForResult(takeVideoIntent, CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE);
		}
		else {
			Toast.makeText(this, R.string.Cameranotsupported, Toast.LENGTH_SHORT).show();
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    if (requestCode == CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE) {
	        if (resultCode == RESULT_OK) {
	            // Video captured and saved to fileUri specified in the Intent
	            Toast.makeText(this, "Video saved to:\n" +
	                     data.getData(), Toast.LENGTH_LONG).show();
	        } else if (resultCode == RESULT_CANCELED) {
	            // User cancelled the video capture
				Toast.makeText(this, "Video Capture Cancelled", Toast.LENGTH_SHORT).show();

	        } else {
	            // Video capture failed, advise user
				Toast.makeText(this, "Video Capture Failed: Please try again", Toast.LENGTH_SHORT).show();

	        }
	    }
	}

	
	/** Create a file Uri for saving an image or video */
	private static Uri getOutputMediaFileUri(int type){
	      return Uri.fromFile(getOutputMediaFile(type));
	}
	
	/** Create a File for saving an image or video */
	private static File getOutputMediaFile(int type){
	    // To be safe, you should check that the SDCard is mounted
	    // using Environment.getExternalStorageState() before doing this.

        root = Environment.getExternalStorageDirectory().getPath();
        root +="/DCIM/";
        //root +="media/external/video/media";
	    File mediaStorageDir = new File(root+"NoPainVideos");
	    // This location works best if you want the created images to be shared
	    // between applications and persist after your app has been uninstalled.

	    // Create the storage directory if it does not exist
	    if (! mediaStorageDir.exists()){
	        if (! mediaStorageDir.mkdirs()){
	            Log.d("NoPainVideos", "failed to create directory");
	            return null;
	        }
	    }

	    // Create a media file name
	    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
	    File mediaFile;
	    if (type == MEDIA_TYPE_IMAGE){
	        mediaFile = new File(mediaStorageDir.getPath() + File.separator +
	        "IMG_"+ timeStamp + ".jpg");
	    } else if(type == MEDIA_TYPE_VIDEO) {
	        mediaFile = new File(mediaStorageDir.getPath() + File.separator +
	        "VID_"+ timeStamp + ".mp4");
	    } else {
	        return null;
	    }

	    return mediaFile;
	}
	
	@SuppressLint("NewApi")
	private boolean ifcamera()	{
		int numCameras = Camera.getNumberOfCameras();
		if (numCameras > 0) {
		  return true;
		}
		return false;
	}
	
    public void clearInputs()
    {
    	talkabout.setText("Talk about your experiences");
    }

}
