package ca.sickkids.nopainapp;

import android.os.Bundle;
import android.app.Activity;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.app.Activity;
import android.os.Bundle;
import android.app.ListActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;


public class Recommendation extends ListActivity {
	private static final String[] items={"Learn about pain", "Learn about your disease", "Talk or write about disease",
        "Get your mind off it", "Set a pain management goal ",
        "Talk to someone about it ", "Use medications"};
	private enum category {
		MILD, MODERATE, SEVERE
	}
	private static String[] finallist=null;
	//private static String[] question = {"q1","q2","q3","q4","q5","q6","q7"};
	private static int[] freq = {1,4,2,6,1,8,9};
	private category painstatus;
	private int getmax=1;
	private int lowest= Integer.MAX_VALUE;
	//private int maxindex;
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.activity_main);
		getreccomendation();
		 ArrayList<RowModel> list=new ArrayList<RowModel>();
		    int count =0;
		    for (String s : finallist) {
		      list.add(new RowModel(s,count));
		      count++;
		    }
		    //ListView list = getListView();

		    setListAdapter(new RatingAdapter(list));
		  }
	


private void getreccomendation() {
		// Check the results of the survey and decide from which category should the 
		// should the advice be chosen and displayed
		int minindex=-1;
		int maxindex=-1;
		int[] freqnew = freq;

		if(SurveyActivity.answers.get(0).equalsIgnoreCase("NO") && SurveyActivity.answers.get(1).equalsIgnoreCase("NO")){
			//don't give any advice
		}
		else if(Integer.parseInt(SurveyActivity.answers.get(2))>70 || Integer.parseInt(SurveyActivity.answers.get(3))>70 || 
				Integer.parseInt(SurveyActivity.answers.get(4))>70 || Integer.parseInt(SurveyActivity.answers.get(5))>70 || 
				Integer.parseInt(SurveyActivity.answers.get(10))<30) {
			painstatus=category.MILD;			
		}
		else if(Integer.parseInt(SurveyActivity.answers.get(2))<30 && Integer.parseInt(SurveyActivity.answers.get(3))<30 && 
				Integer.parseInt(SurveyActivity.answers.get(4))<30 && Integer.parseInt(SurveyActivity.answers.get(5))<30 && 
				Integer.parseInt(SurveyActivity.answers.get(10))>70) {
			painstatus=category.SEVERE;			
		} else {
			painstatus=category.MODERATE;			

		}
		//Get the max number from freq table
		for (int i=0;i<freq.length;i++){
			if(lowest>freq[i]){
				lowest = freq[i];
				minindex =i;
			}
			if(getmax<freq[i]) {
				getmax=freq[i];
				//maxindex=i;
			}
		}
		
		int counter;
		while(minindex>0 && freqnew[minindex]!=-1) {
			int maxval=0;counter=0;
			for (int i=0;i<freqnew.length;i++) {
				if(maxval<freqnew[i]) {
					maxval=freqnew[i];
					maxindex = i;
				}			
			}
			maxval=0;
			freqnew[maxindex]=-1;
			finallist[counter]=items[maxindex];
			counter++;
		}

		
		
	}



private RowModel getModel(int position) {
    return(((RatingAdapter)getListAdapter()).getItem(position));
  }


class RatingAdapter extends ArrayAdapter<RowModel> {
    RatingAdapter(ArrayList<RowModel> list) {
      super(Recommendation.this, R.layout.recommendation, R.id.label, list);
    }
    
    public View getView(int position, View convertView,
                        ViewGroup parent) {
      View row=super.getView(position, convertView, parent);
      RecommendationRowViewHolder holder=(RecommendationRowViewHolder)row.getTag();
                          
      if (holder==null) {   
        holder=new RecommendationRowViewHolder(row);
        row.setTag(holder);
        
        RatingBar.OnRatingBarChangeListener l=
                    new RatingBar.OnRatingBarChangeListener() {
          public void onRatingChanged(RatingBar ratingBar,
                                        float rating,
                                        boolean fromTouch)  {
            Integer myPosition=(Integer)ratingBar.getTag();
            RowModel model=getModel(myPosition);
            
            model.rating=rating;
          
            LinearLayout parent=(LinearLayout)ratingBar.getParent();
            TextView label=(TextView)parent.findViewById(R.id.label);
            Button b1=(Button)parent.findViewById(R.id.choose);
            
            b1.setOnClickListener(new OnClickListener() {

          	    public void onClick(View v) {
          	    	//Handle the click event
          	    	/*
          	    	 * TODO : Check the position of the row and see what item was chosen
          	    	 * And accordingly put that recommendation in database
          	    	 */
            		Toast.makeText(getApplicationContext(), "Thanks for chooosing the recommendation", Toast.LENGTH_SHORT).show();
          	    	finish();
          	    }
          	});
            label.setMovementMethod(new ScrollingMovementMethod());
            label.setText(model.toString());
          }
        };
        
        holder.rate.setOnRatingBarChangeListener(l);
      }

      RowModel model=getModel(position);
      
      holder.rate.setTag(new Integer(position));
      holder.rate.setRating(model.rating);

      return(row);
    }
  }
  
  class RowModel {
    String label;
    float rating;
    
    RowModel(String label, int count) {
      this.rating= (float) Math.round(((float)freq[count]/getmax)*5.0);
      this.label=label;
    }
    
    public String toString() {
      if (rating>=3.0) {
        //return(label.toUpperCase());
      }
      
      return(label);
    }
  }

}
