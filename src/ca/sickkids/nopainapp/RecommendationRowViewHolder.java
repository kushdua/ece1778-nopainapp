package ca.sickkids.nopainapp;

import android.view.View;
import android.widget.RatingBar;

public class RecommendationRowViewHolder {
	  RatingBar rate=null;
	  
	  RecommendationRowViewHolder(View base) {
	    this.rate=(RatingBar)base.findViewById(R.id.rate);
	  }
}
