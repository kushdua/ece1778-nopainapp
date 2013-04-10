package ca.sickkids.nopainapp;

import java.util.ArrayList;
import java.util.List;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.RangeCategorySeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.text.Layout;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

public class GoalsActivity extends Activity {

	public static Activity activity = null;
	EditText goalInput = null;
	DBHelper dbHelper = null;
	
	/** The main dataset that includes all the series that go into a chart. */
	private XYMultipleSeriesDataset mDataset = new XYMultipleSeriesDataset();
	/** The main renderer that includes all the renderers customizing a chart. */
	private XYMultipleSeriesRenderer mRenderer = new XYMultipleSeriesRenderer();
	//	  /** The most recently added series. */
	//	  private XYSeries mCurrentSeries;
	//	  /** The most recently created renderer, customizing the current series. */
	//	  private XYSeriesRenderer mCurrentRenderer;
	/** The chart view that displays the data. */
	private GraphicalView mChartView = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		activity=this;
		setContentView(R.layout.goals);
		goalInput = (EditText)findViewById(R.id.txtInputGoal);

	    List<double[]> seriesVar = new ArrayList<double[]>();
	    String[] titles = {"Pain level"};
	    List<double[]> x = new ArrayList<double[]>();

		dbHelper = new DBHelper(this, HomeActivity.DB_NAME, null, HomeActivity.DB_VERSION);
	    SQLiteDatabase db = dbHelper.getReadableDatabase();
	    try
		{
			String args[] = { Integer.toString(LoginActivity.userID) };
			//Most recent surveys read first
			int count=0;
			double ser_count[]=new double[10];
			Cursor result = db.rawQuery("SELECT q5 FROM survey WHERE userID=? ORDER BY id ASC LIMIT 10;", args);
			if(result != null && result.getCount() != -1)
			{
				while(result.moveToNext())
				{
					//seriesVar.add((double)(result.getInt(0)));
					ser_count[count++]=(double)(result.getInt(0));
				}
			}
			double ser[]=new double[count];
			double x_title[]=new double[count];
			for(int i=0; i<count; i++)
			{
				ser[i]=ser_count[i];
				x_title[i]=i+1;
			}

		    for (int j = 0; j < titles.length; j++) {
		      x.add(x_title);
		    }
			seriesVar.add(ser);
		}
		catch(SQLException e)
		{
			Log.e("GOALS", "Error retrieving past pain " + e.getMessage());
		}
		finally
		{
			db.close();
		}
	    
	    addXYSeries(mDataset, titles, x, seriesVar, 0);

//	    XYSeries series = dataset.getSeriesAt(0);
	    
	    // set some properties on the main renderer
	    mRenderer.setApplyBackgroundColor(true);
	    mRenderer.setBackgroundColor(Color.argb(100, 50, 50, 50));
	    mRenderer.setAxisTitleTextSize(16);
	    mRenderer.setChartTitleTextSize(20);
	    mRenderer.setLabelsTextSize(15);
	    mRenderer.setLegendTextSize(15);
	    mRenderer.setMargins(new int[] { 20, 30, 15, 20 });
	    mRenderer.setZoomButtonsVisible(true);
	    mRenderer.setPointSize(5);
	    mRenderer.setChartTitle("Pain Levels in past " + mDataset.getSeriesAt(0).getItemCount() + " surveys");
	    
	    mRenderer.addSeriesRenderer(new XYSeriesRenderer());

	    createOrUpdateChart(this);
	    ChartFactory.getLineChartIntent(this, mDataset, mRenderer);
	    mChartView.repaint();
	}
	
	public void createOrUpdateChart(Activity activity)
	{
		if (mChartView == null) {
			LinearLayout layout = (LinearLayout)activity.findViewById(R.id.chart);
			mChartView = ChartFactory.getLineChartView(this, mDataset, mRenderer);
			// disable the chart click events
			mRenderer.setClickEnabled(false);
			mRenderer.setSelectableBuffer(10);
			layout.addView(mChartView, new LayoutParams(LayoutParams.FILL_PARENT,
					LayoutParams.FILL_PARENT));
			boolean enabled = mDataset.getSeriesCount() > 0;
		} else {
			mChartView.repaint();
		}
	}
	
	@Override
	public void onResume()
	{
		createOrUpdateChart(this);
		super.onResume();
	}
	
	public void addXYSeries(XYMultipleSeriesDataset dataset, String[] titles, List<double[]> xValues,
			List<double[]> yValues, int scale) {
		int length = titles.length;
		for (int i = 0; i < length; i++) {
			XYSeries series = new XYSeries(titles[i], scale);
			double[] xV = xValues.get(i);
			double[] yV = yValues.get(i);
			int seriesLength = xV.length;
			for (int k = 0; k < seriesLength; k++) {
				series.add(xV[k], yV[k]);
			}
			dataset.addSeries(series);
		}
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
