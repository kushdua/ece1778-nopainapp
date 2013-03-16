package ca.sickkids.nopainapp;

import java.util.ArrayList;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

public class SurveyActivity extends Activity implements OnItemSelectedListener {
	private int currQuestion = 1;
	private static final int MAX_QUESTION_NUMBER = 9;
	//private static final int MAX_QUESTION_NUMBER = 11;
	private ArrayList<String> answers = new ArrayList<String>();
	private boolean stopsurvey = false; //variable used to end survey intermediately
	
	private TextView questionHeader = null;
	private TextView questionContents = null;
	private SeekBar painBar = null;
	private LinearLayout painBarContainer = null;
	private Spinner choicesSpinner = null;
	private Button prevButton = null;
	private Button nextButton = null;
	private ProgressBar surveyProgress = null;
	private EditText otherTextField = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.survey_activity);
		
		questionHeader = (TextView) findViewById(R.id.lblQuestionHeader);
		questionContents = (TextView) findViewById(R.id.lblQuestionContents);
		painBar = (SeekBar) findViewById(R.id.painBar);
		painBarContainer = (LinearLayout) findViewById(R.id.ProgressLabeledLayout);
		choicesSpinner = (Spinner) findViewById(R.id.choiceSpinner);
		choicesSpinner.setOnItemSelectedListener(this);
		prevButton = (Button) findViewById(R.id.btnPrev);
		nextButton = (Button) findViewById(R.id.btnNext);
		surveyProgress = (ProgressBar) findViewById(R.id.surveyProgress);
		otherTextField = (EditText) findViewById(R.id.textOtherField);
		
		for(int i=0; i<MAX_QUESTION_NUMBER; i++)
		{
			answers.add(i, "");
		}
		
		saveAnswerAndUpdateQuestion(0, 1);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_home, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
	    case R.string.menu_settings:
	        //TODO: Go to settings page
	        return true;
	    default:
	        return super.onOptionsItemSelected(item);
	    }
	}
	
	public void saveAnswerAndUpdateQuestion(int currQuestion, int nextQuestion)
	{
		//if(!(currQuestion == 0 && nextQuestion == 1) && currQuestion==3 || currQuestion==4 || currQuestion==5 || currQuestion==6 || currQuestion ==11)
		if(!(currQuestion == 0 && nextQuestion == 1) && currQuestion==1 || currQuestion==2 || currQuestion==3 || currQuestion==4 || currQuestion ==9)
		{
			answers.set(currQuestion-1, Integer.toString(painBar.getProgress()));
			//Log.w("INFO","Storing " + Integer.toString(painBar.getProgress()) + " at position " + (currQuestion-1));
		}
		//else if(!(currQuestion == 0 && nextQuestion == 1) && currQuestion==1 || currQuestion==2 ||currQuestion==7 || currQuestion==8 || currQuestion==9 || currQuestion==10)
		else if(!(currQuestion == 0 && nextQuestion == 1) && currQuestion==5 || currQuestion==6 || currQuestion==7 || currQuestion==8)
		{
			if(otherTextField.getVisibility()==View.VISIBLE)
			{
				answers.set(currQuestion-1, otherTextField.getText().toString());
			}
			else
			{
				answers.set(currQuestion-1, choicesSpinner.getSelectedItem().toString());
			}
		}
		
		//Update question header/contents
		questionHeader.setText(String.format(getResources().getString(R.string.surveyQuestionNumber), nextQuestion));
		
		ArrayAdapter<CharSequence> adapter = null;
		
		//TODO: Include other field if so selected in q 5/7?
		switch(nextQuestion)
		{
			default:
			/* code commited temporarily 
			 case 10: //change it to 1 later
				 questionContents.setText(R.string.q1yna);
				 adapter = ArrayAdapter.createFromResource(
		                this, R.array.q1choice, android.R.layout.simple_spinner_item);

				 adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				 choicesSpinner.setAdapter(adapter);
			 break;
	
			 case 11: // change it to 2 later 
				 questionContents.setText(R.string.q1ynb);
				 adapter = ArrayAdapter.createFromResource(
			                this, R.array.q2choice, android.R.layout.simple_spinner_item);

					 adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
					 choicesSpinner.setAdapter(adapter);
			 break;
			*/	  	
			//change the rest of the cases accordingly during code testing
			case 1:
				questionContents.setText(R.string.q1Text);
				break;
			case 2:
				questionContents.setText(R.string.q2Text);
				break;
			case 3:
				questionContents.setText(R.string.q3Text);
				break;
			case 4:
				questionContents.setText(R.string.q4Text);
				break;
			case 5:
				questionContents.setText(R.string.q5aText);
				adapter = ArrayAdapter.createFromResource(
		                this, R.array.q5aMedicationsAnswers, android.R.layout.simple_spinner_item);

				adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				choicesSpinner.setAdapter(adapter);
				break;
			case 6:
				questionContents.setText(R.string.q5bText);
				adapter = ArrayAdapter.createFromResource(
		                this, R.array.q56bMedicationsAnswers, android.R.layout.simple_spinner_item);

				adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				choicesSpinner.setAdapter(adapter);
				break;
			case 7:
				questionContents.setText(R.string.q6aText);
				adapter = ArrayAdapter.createFromResource(
		                this, R.array.q6aMedicationsAnswers, android.R.layout.simple_spinner_item);

				adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				choicesSpinner.setAdapter(adapter);
				break;
			case 8:
				questionContents.setText(R.string.q6bText);
				adapter = ArrayAdapter.createFromResource(
		                this, R.array.q56bMedicationsAnswers, android.R.layout.simple_spinner_item);

				adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				choicesSpinner.setAdapter(adapter);
				break;
			case 9:
				questionContents.setText(R.string.q7Text);
				break;
		}

		String storedAnswer = "";
		//Update visibilities and question answers if previously saved
		if(nextQuestion==1 || nextQuestion==2 || nextQuestion==3 || nextQuestion==4 || nextQuestion==9)
		{
			if(nextQuestion==1 && currQuestion==0)
			{
				//Initialization of survey
				prevButton.setVisibility(View.INVISIBLE);
			}
			painBarContainer.setVisibility(View.VISIBLE);
			choicesSpinner.setVisibility(View.INVISIBLE);
			otherTextField.setVisibility(View.INVISIBLE);
			storedAnswer = answers.get(nextQuestion-1).toString();
			//Log.w("INFO","Loading " + storedAnswer + " at position " + (nextQuestion-1));
			if(storedAnswer.compareTo("")==0 || storedAnswer.compareTo("0")==0)
			{
				painBar.setProgress(0);
			}
			else
			{
				painBar.setProgress(Integer.parseInt(storedAnswer));
			}
		}
		else if(nextQuestion==5 || nextQuestion==6 || nextQuestion==7 || nextQuestion==8)
		{
			painBarContainer.setVisibility(View.INVISIBLE);
			choicesSpinner.setVisibility(View.VISIBLE);
			storedAnswer = answers.get(nextQuestion-1).toString();
			String arr[] = null;
			if(nextQuestion==6 || nextQuestion==8)
			{
				arr=getResources().getStringArray(R.array.q56bMedicationsAnswers);
			}
			else if(nextQuestion==5)
			{
				arr=getResources().getStringArray(R.array.q5aMedicationsAnswers);
			}
			else if(nextQuestion==7)
			{
				arr=getResources().getStringArray(R.array.q6aMedicationsAnswers);
			}

			boolean setSelection=false;
			for(int i=0; i<arr.length; i++)
			{
				if(storedAnswer.compareTo(arr[i])==0)
				{
					choicesSpinner.setSelection(i);
					setSelection=true;
				}
			}
			
			if(!setSelection)
			{
				if(storedAnswer.compareTo("")!=0)
				{
					choicesSpinner.setSelection(arr.length-1);
					otherTextField.setVisibility(View.VISIBLE);
					otherTextField.setText(storedAnswer);
				}
			}
		}
		else if(nextQuestion==10)
		{
			painBarContainer.setVisibility(View.INVISIBLE);
			choicesSpinner.setVisibility(View.INVISIBLE);
			questionHeader.setText(R.string.recommendationHeader);
			
			//TODO: Algorithm for recommendations
			questionContents.setText(R.string.recommendationTalkToParents);
		}
		
		surveyProgress.setMax(100);
		surveyProgress.setProgress(nextQuestion*100/10);
	}

    public void btnPreviousClickHandler(View v)
    {
		if(currQuestion>1 && currQuestion<=MAX_QUESTION_NUMBER+1)
		{
			//Save answer and proceed with question
			saveAnswerAndUpdateQuestion(currQuestion, currQuestion-1);
			currQuestion--;
			nextButton.setVisibility(View.VISIBLE);
			nextButton.setText(R.string.btnNextText);
		}
		
		if(currQuestion==1)
		{
			//Disable previous button for question 1
			prevButton.setVisibility(View.INVISIBLE);
		}
    }
    
    
    public void btnNextClickHandler(View v)
    {
		if(currQuestion>=1 && currQuestion<=MAX_QUESTION_NUMBER)
		{
			//Save answer and proceed with question
			saveAnswerAndUpdateQuestion(currQuestion, currQuestion+1);
			currQuestion++;
			
			prevButton.setVisibility(View.VISIBLE);
			prevButton.setText(R.string.btnPrevText);
			
			if(currQuestion==9)
			{
				//Change text for next to get recommendation
				nextButton.setText(R.string.btnGetRecommendationText);
			}
			else if(currQuestion==10)
			{
				//Display recommendation
				nextButton.setVisibility(View.INVISIBLE);
				
			}
		}
    }

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int pos,
			long id) {
		String selected = parent.getItemAtPosition(pos).toString();
		if(selected.compareTo("Other (please list)")==0)
		{
			otherTextField.setVisibility(View.VISIBLE);
			otherTextField.requestFocus();
		}
		else
		{
			otherTextField.setVisibility(View.INVISIBLE);
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		//Nothing to be done
	}
}
