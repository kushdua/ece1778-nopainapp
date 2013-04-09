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
	
	private static final String doctors_suggestion_severe = "You have reported severe pain or pain interference on your last 3 reports. Please consider seeking a healthcare professional’s help";
	private static final String doctors_suggestion_moderate = "You have reported moderate pain or pain interference on your last 5 reports. Please consider seeking a healthcare professional’s help";

	public static int chosen_advice_index;
	
	private static int maxsuggestion = 7;
	private static int[] freq = {5,4,2,6,1,8,9};
	private static int[] frequpdated = new int[maxsuggestion];
	private static int[] advice_index = new int[maxsuggestion];
	private static final String[] survey1 = {"","","yes","yes","10","20","29","10","","","","","90"}; //mild
	private static final String[] survey2 = {"","","yes","yes","10","20","29","10","","","","","90"}; //mild
	private static final String[] survey3 = {"","","yes","yes","50","60","60","65","","","","","50"}; //medium
	private static final String[] survey4 = {"","","yes","yes","30","90","80","5","","","","","10"}; //high
	private static final String[] survey5 = {"","","yes","yes","30","90","80","5","","","","","10"}; //high

	private boolean regular_suggestion = false;
	private enum category {
		MILD, MODERATE, SEVERE
	}
	private category painstatus;
	private int getmax=1;
	private int lowest= Integer.MAX_VALUE;
	//private int maxindex;
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.activity_main);
		findpaincategory();
		parselastsurveys();
		String[] finallist = new String[maxsuggestion];

		getreccomendation(finallist);
		 ArrayList<RowModel> list=new ArrayList<RowModel>();
		    int count =0;
		    for (String s : finallist) {
		      list.add(new RowModel(s,count));
		      count++;
		    }
		    //ListView list = getListView();

		    setListAdapter(new RatingAdapter(list));
		  }
	

	private void findpaincategory () {

		if(SurveyActivity.answers.get(0).equalsIgnoreCase("NO") && SurveyActivity.answers.get(1).equalsIgnoreCase("NO")){
			//don't give any advice
		}
		else if(Integer.parseInt(SurveyActivity.answers.get(2))>70 || Integer.parseInt(SurveyActivity.answers.get(3))>70 || 
				Integer.parseInt(SurveyActivity.answers.get(4))>70 || Integer.parseInt(SurveyActivity.answers.get(5))>70 || 
				Integer.parseInt(SurveyActivity.answers.get(10))<30) {
			painstatus=category.SEVERE;			
		}
		else if(Integer.parseInt(SurveyActivity.answers.get(2))<30 && Integer.parseInt(SurveyActivity.answers.get(3))<30 && 
				Integer.parseInt(SurveyActivity.answers.get(4))<30 && Integer.parseInt(SurveyActivity.answers.get(5))<30 && 
				Integer.parseInt(SurveyActivity.answers.get(10))>70) {
			painstatus=category.MILD;			
		} else {
			painstatus=category.MODERATE;			

		}
		
    }
	
    private void parselastsurveys() {
		//Before computing the ratings ... find if the suggestion is about doctors approval or not
		if(painstatus==category.SEVERE) {
			//check last 2 saved survey records and see what to do
			if((Integer.parseInt(survey5[4])>70 || Integer.parseInt(survey5[5])>70 || Integer.parseInt(survey5[6])>70 ||
			Integer.parseInt(survey5[7])>70 || Integer.parseInt(survey5[12])<30)   //record until survey 5
			&& (Integer.parseInt(survey4[4])>70 || Integer.parseInt(survey4[5])>70 || Integer.parseInt(survey4[6])>70 ||
			Integer.parseInt(survey4[7])>70 || Integer.parseInt(survey4[12])<30))  {
					maxsuggestion = 1;
					regular_suggestion=false;
			}
			else  {
				regular_suggestion=true;
				maxsuggestion = 7;
			}
			
		}
		else if (painstatus==category.MODERATE) {
			//check last 4 saved survey records and see what to do
			if((Integer.parseInt(survey5[4])<30 && Integer.parseInt(survey5[5])<30 && Integer.parseInt(survey5[6])<30 &&
		     Integer.parseInt(survey5[7])<30 && Integer.parseInt(survey5[12])>70)
		     || (Integer.parseInt(survey4[4])<30 && Integer.parseInt(survey4[5])<30 && Integer.parseInt(survey4[6])<30 &&
			 Integer.parseInt(survey4[7])<30 && Integer.parseInt(survey4[12])>70)
			 || (Integer.parseInt(survey3[4])<30 && Integer.parseInt(survey3[5])<30 && Integer.parseInt(survey3[6])<30 &&
			 Integer.parseInt(survey3[7])<30 && Integer.parseInt(survey3[12])>70)
			 || (Integer.parseInt(survey2[4])<30 && Integer.parseInt(survey2[5])<30 && Integer.parseInt(survey2[6])<30 &&
			 Integer.parseInt(survey2[7])<30 && Integer.parseInt(survey2[12])>70)) {
				regular_suggestion=true;
				maxsuggestion = 7;
			}
			else {
				regular_suggestion=false;
				maxsuggestion = 1;
			}
		}
		else {
			regular_suggestion=true;
			maxsuggestion = 7;
		}
		
	}


private void getreccomendation(String[] finallist) {
		//decide what recommendation to give accordingly
		if (regular_suggestion) {
			// Check the results of the survey and decide from which category should the 
			// should the advice be chosen and displayed
			int minindex=-1;
			int maxindex=-1;
			int[] freqnew = new int[maxsuggestion];
	
			//Get the max number from freq table
			for (int i=0;i<freq.length;i++){
				freqnew[i]=freq[i];
				if(lowest>freq[i]){
					lowest = freq[i];
					minindex =i;
				}
				if(getmax<freq[i]) {
					getmax=freq[i];
				}
			}
			
			int counter=0;
			while(minindex>=0 && freqnew[minindex]!=-1) {
				int maxval=0;
				for (int i=0;i<freqnew.length;i++) {
					if(maxval<freqnew[i]) {
						maxval=freqnew[i];
						maxindex = i;
					}			
				}
				maxval=0;
				advice_index[counter]=maxindex;
				frequpdated[counter]=freq[maxindex];
				finallist[counter]=items[maxindex];
				freqnew[maxindex]=-1;
				counter++;
			}
			lowest= Integer.MAX_VALUE;	
		}
		else {
			//add the go to doctor suggestion box here .... 
			if(painstatus==category.SEVERE) {
				finallist[0]=doctors_suggestion_severe;
				frequpdated[0]=0;
				chosen_advice_index=-1;
			}
			else {
				finallist[0]=doctors_suggestion_moderate;
				frequpdated[0]=0;
				chosen_advice_index=-1;
			}
			
		}

	}



private RowModel getModel(int position) {
    return(((RatingAdapter)getListAdapter()).getItem(position));
  }


class RatingAdapter extends ArrayAdapter<RowModel> {
    RatingAdapter(ArrayList<RowModel> list) {
      super(Recommendation.this, R.layout.recommendation, R.id.label, list);
    }
    //code: position modified to final --not sure if it will work
    public View getView(final int position, View convertView,
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
          	    	chosen_advice_index=advice_index[position];
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
      //this.rating= (float) Math.round(((float)freq[count]/getmax)*5.0);
      this.rating= (float) Math.round(((float)frequpdated[count]/getmax)*5.0);
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
