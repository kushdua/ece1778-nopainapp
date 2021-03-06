package ca.sickkids.nopainapp;

import java.io.File;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class VideoActivity extends Activity {
//      private Cursor videocursor;
//      private int video_column_index;
      ListView videolist;
      ArrayList<String> items = new ArrayList<String>();
      ArrayList<Long> sizes = new ArrayList<Long>();
//      //put the uri here
//      Uri parcialUri = Uri.parse("content://media/external/video/media");
//      //AdapterView videolist;
//
//      int count;

      /** Called when the activity is first created. */
      @Override
      public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.journal_view_videos);
            init_phone_video_grid();
      }

      @SuppressWarnings("deprecation")
	private void init_phone_video_grid() {
            //System.gc();
            String[] proj = { MediaStore.Video.Media._ID,
            		MediaStore.Video.Media.DATA,
					MediaStore.Video.Media.DISPLAY_NAME,
					MediaStore.Video.Media.SIZE };
            
            //videocursor = managedQuery(MediaStore.Video.Media.INTERNAL_CONTENT_URI, proj, null, null, null);
//            videocursor = managedQuery(parcialUri, proj, null, null, null);
//            count = videocursor.getCount();
    		File mfile=new File(Environment.getExternalStorageDirectory().getPath()+"/DCIM/"+"NoPainVideos");
    		if(mfile!=null)
    		{
    			File[] list=mfile.listFiles();
    			if(list!=null)
    			{
    				items = new ArrayList<String>();
    				
    				for(int i=0; i<list.length; i++)
    				{
    					items.add(i,list[i].getName());
    					sizes.add(i, Long.valueOf(list[i].length()));
    				}
    			}
    		}
            
            videolist = (ListView) findViewById(R.id.journalGallery);
            videolist.setAdapter(new VideoAdapter(getApplicationContext()));
            videolist.setOnItemClickListener(videogridlistener);
      }

      private OnItemClickListener videogridlistener = new OnItemClickListener() {
            public void onItemClick(AdapterView parent, View v, int position, long id) {
                  //System.gc();
//                  video_column_index = videocursor
//.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
//                  videocursor.moveToPosition(position);
//                  String filename = videocursor.getString(video_column_index); 
                  Intent intent = new Intent(VideoActivity.this, ViewVideo.class);
                  intent.putExtra("videofilename", Environment.getExternalStorageDirectory().getPath()+"/DCIM/"+"NoPainVideos/"+
                		  items.get(position));
                  startActivity(intent);
            }
      };

      public class VideoAdapter extends BaseAdapter {
            private Context vContext;

            public VideoAdapter(Context c) {
                  vContext = c;
            }

            public int getCount() {
                  //return count;
            	return items.size();
            }

            public Object getItem(int position) {
                  return position;
            }

            public long getItemId(int position) {
                  return position;
            }

            public View getView(int position, View convertView, ViewGroup parent) {
                  //System.gc();
                  TextView tv = new TextView(vContext.getApplicationContext());
                  tv.setLines(2);
                  String id = null;
                  if (convertView == null) {
//                        video_column_index = videocursor
//.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME);
//                        videocursor.moveToPosition(position);
//                        id = videocursor.getString(video_column_index);
//                        video_column_index = videocursor
//.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE);
//                        videocursor.moveToPosition(position);
//                        id += " Size(KB):" + videocursor.getString(video_column_index);
                	  	id=items.get(position) + " Size: " + (sizes.get(position)/(1024)) + "KB";
                        tv.setText(id);
                  } else
                        tv = (TextView) convertView;
                  return tv;
            }
      }
}