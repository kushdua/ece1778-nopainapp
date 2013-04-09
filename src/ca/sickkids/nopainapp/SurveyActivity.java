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
import android.widget.Toast;

public class SurveyActivity extends Activity implements OnItemSelectedListener {
	private int currQuestion = 1;
	//private static final int MAX_QUESTION_NUMBER = 11;
	private static final int MAX_QUESTION_NUMBER = 13;
	public static ArrayList<String> answers = new ArrayList<String>();
	private static int numsurvey = 0;
	
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
		/*if(numsurvey==0) {
			currQuestion+=2;
		}*/
		
		
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
		if(!(currQuestion == 0 && nextQuestion == 1) && currQuestion==5 || currQuestion==6 || currQuestion==7 || currQuestion==8 || currQuestion ==13)
		{
			answers.set(currQuestion-1, Integer.toString(painBar.getProgress()));
			//Log.w("INFO","Storing " + Integer.toString(painBar.getProgress()) + " at position " + (currQuestion-1));
		}
		//else if(!(currQuestion == 0 && nextQuestion == 1) && currQuestion==1 || currQuestion==2 ||currQuestion==7 || currQuestion==8 || currQuestion==9 || currQuestion==10)
		else if(!(currQuestion == 0 && nextQuestion == 1) && currQuestion==1 || currQuestion==2 || currQuestion==3 || currQuestion==4 || currQuestion==9|| currQuestion==10|| currQuestion==11|| currQuestion==12)
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
		if(numsurvey==0) {
			nextQuestion+=2;
			currQuestion+=2;
			questionHeader.setText(String.format(getResources().getString(R.string.surveyQuestionNumber), nextQuestion-2));
		}
		else 	
			questionHeader.setText(String.format(getResources().getString(R.string.surveyQuestionNumber), nextQuestion));

		ArrayAdapter<CharSequence> adapter = null;
		
		//TODO: Include other field if so selected in q 5/7?
		switch(nextQuestion)
		{
			default:
			 //code committed temporarily 
			 
			case 1: 
				  questionContents.setText(R.string.q1prev);
				  adapter = ArrayAdapter.createFromResource(
		                this, R.array.q2choice, android.R.layout.simple_spinner_item);

				 adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				 choicesSpinner.setAdapter(adapter);
		      break;
			 
			 case 2: 
				  questionContents.setText(R.string.q1next);
				  adapter = ArrayAdapter.createFromResource(
		                this, R.array.q2choice, android.R.layout.simple_spinner_item);

				 adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				 choicesSpinner.setAdapter(adapter);
			break;
			 
			 case 3: //change it to 3 later
				 questionContents.setText(R.string.q1yna);
				 adapter = ArrayAdapter.createFromResource(
		                this, R.array.q1choice, android.R.layout.simple_spinner_item);

				 adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				 choicesSpinner.setAdapter(adapter);
			 break;
	
			 case 4: // change it to 4 later and so one
				 questionContents.setText(R.string.q1ynb);
				 adapter = ArrayAdapter.createFromResource(
			                this, R.array.q1choice, android.R.layout.simple_spinner_item);

					 adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
					 choicesSpinner.setAdapter(adapter);
			 break;
				  	
			//change the rest of the cases accordingly during code testing
			case 5:
				questionContents.setText(R.string.q1Text);
				break;
			case 6:
				questionContents.setText(R.string.q2Text);
				break;
			case 7:
				questionContents.setText(R.string.q3Text);
				break;
			case 8:
				questionContents.setText(R.string.q4Text);
				break;
			case 9:
				questionContents.setText(R.string.q5aText);
				adapter = ArrayAdapter.createFromResource(
		                this, R.array.q5aMedicationsAnswers, android.R.layout.simple_spinner_item);

				adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				choicesSpinner.setAdapter(adapter);
				break;
			case 10:
				questionContents.setText(R.string.q5bText);
				adapter = ArrayAdapter.createFromResource(
		                this, R.array.q56bMedicationsAnswers, android.R.layout.simple_spinner_item);

				adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				choicesSpinner.setAdapter(adapter);
				break;
			case 11:
				questionContents.setText(R.string.q6aText);
				adapter = ArrayAdapter.createFromResource(
		                this, R.array.q6aMedicationsAnswers, android.R.layout.simple_spinner_item);

				adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				choicesSpinner.setAdapter(adapter);
				break;
			case 12:
				questionContents.setText(R.string.q6bText);
				adapter = ArrayAdapter.createFromResource(
		                this, R.array.q56bMedicationsAnswers, android.R.layout.simple_spinner_item);

				adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				choicesSpinner.setAdapter(adapter);
				break;
			case 13:
				questionContents.setText(R.string.q7Text);
				break;
		}

		String storedAnswer = "";
		//Update visibilities and question answers if previously saved
		if(nextQuestion==5 || nextQuestion==6 || nextQuestion==7 || nextQuestion==8 || nextQuestion==13)
		//if(nextQuestion==3 || nextQuestion==4 || nextQuestion==5 || nextQuestion==6 || nextQuestion==11)
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
		else if(nextQuestion==1 || nextQuestion==2 || nextQuestion==3 || nextQuestion==4 || nextQuestion==9 || nextQuestion==10 || nextQuestion==11 || nextQuestion==12)
		//else if(nextQuestion==1 || nextQuestion==2 || nextQuestion==7 || nextQuestion==8 || nextQuestion==9 || nextQuestion==10)
		{
			//if(nextQuestion==1 && currQuestion==0)
			if(nextQuestion==1 && currQuestion==0)
			{
				//Initialization of survey
				prevButton.setVisibility(View.INVISIBLE);
			}
			painBarContainer.setVisibility(View.INVISIBLE);
			choicesSpinner.setVisibility(View.VISIBLE);
			storedAnswer = answers.get(nextQuestion-1).toString();
			String arr[] = null;
			if(nextQuestion==1 || nextQuestion==2)
			{
				arr=getResources().getStringArray(R.array.q2choice);
			}
			if(nextQuestion==3 || nextQuestion==4) {
				arr=getResources().getStringArray(R.array.q1choice);
			}	
			else if(nextQuestion==10 || nextQuestion==12)
			//else if(nextQuestion==8 || nextQuestion==10)
			{
				arr=getResources().getStringArray(R.array.q56bMedicationsAnswers);
			}
			//else if(nextQuestion==7) 
			else if(nextQuestion==9)
			{
				arr=getResources().getStringArray(R.array.q5aMedicationsAnswers);
			}
			//else if(nextQuestion==9) 
			else if(nextQuestion==11)
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
		else if(nextQuestion==14)
		//else if(nextQuestion==12)
		{
			/*painBarContainer.setVisibility(View.INVISIBLE);
			choicesSpinner.setVisibility(View.INVISIBLE);
			questionHeader.setText(R.string.recommendationHeader);
			
			//TODO: Algorithm for recommendations
			questionContents.setText(R.string.recommendationTalkToParents);*/
		}
		
		surveyProgress.setMax(100);
		surveyProgress.setProgress(nextQuestion*100/MAX_QUESTION_NUMBER);
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
			//saveAnswerAndUpdateQuestion(currQuestion, currQuestion+1);
			
			if(currQuestion==3 || currQuestion==4) {
				//if(currQuestion==1 || currQuestion==2) {
				if(choicesSpinner.getSelectedItem().toString().equalsIgnoreCase("")) 
					Toast.makeText(this, R.string.invalidinput, Toast.LENGTH_SHORT).show();
				else {
					saveAnswerAndUpdateQuestion(currQuestion, currQuestion+1);
					currQuestion++;
				}
			}
			else {
				saveAnswerAndUpdateQuestion(currQuestion, currQuestion+1);
				currQuestion++;
			}			
			
			prevButton.setVisibility(View.VISIBLE);
			prevButton.setText(R.string.btnPrevText);
			
			if(currQuestion==3 && numsurvey==1) {
				if(answers.get(currQuestion-2).equalsIgnoreCase("YES")) 
					Recommendation.freq[Recommendation.chosen_advice_index]=Recommendation.freq[Recommendation.chosen_advice_index]+1;
				else if (answers.get(currQuestion-2).equalsIgnoreCase("NO"))
					if(Recommendation.freq[Recommendation.chosen_advice_index]>2)
						Recommendation.freq[Recommendation.chosen_advice_index]=Recommendation.freq[Recommendation.chosen_advice_index]-2;
					else if (Recommendation.freq[Recommendation.chosen_advice_index]==2)
						Recommendation.freq[Recommendation.chosen_advice_index]=Recommendation.freq[Recommendation.chosen_advice_index]-1;
				else {
					//DO NOTHING
					//Recommendation.freq[Recommendation.chosen_advice_index]=Recommendation.freq[Recommendation.chosen_advice_index]+1;

				}
				
			}

			if(currQuestion==5 || (numsurvey==0 && currQuestion==3)) 
			//if(currQuestion==3) 
			{
				/*
				 * Check if the answer to Pain questions are YES/NO
				 *Q1. Did you have PAIN in the last 12 hours?
				 *Q2. Do you have PAIN now?
				 *If Yes then continue else skip the rest of the survey
				 */
				if(answers.get(currQuestion-2).equalsIgnoreCase("NO") && answers.get(currQuestion-3).equalsIgnoreCase("NO")) {
					currQuestion= MAX_QUESTION_NUMBER;
					Toast.makeText(this, R.string.nopainreported, Toast.LENGTH_SHORT).show();
					finish();
					/*
					 * TODO: Part of advanced algorithm .. check if there exist previous assessment 
					 * and ask user if the previous advice was useful/or not
					 * 
					 */
				}	
			}
			//if(currQuestion==11) 
			if(currQuestion==13 || (numsurvey==0 && currQuestion==11))
			{
				//Change text for next to get recommendation
				nextButton.setText(R.string.btnGetRecommendationText);
			}
			//else if(currQuestion==12) 
			else if(currQuestion==14|| (numsurvey==0 && currQuestion==12))
			{
				//Display recommendation
				nextButton.setVisibility(View.INVISIBLE);
				Intent intent = new Intent(v.getContext(), Recommendation.class);
				startActivity(intent);
				//After suggestion is given finish the survey activity as well
				Toast.makeText(this, "Please choose one of the following advice by clicking on it", Toast.LENGTH_SHORT).show();
				numsurvey=1;
				finish();
				
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
