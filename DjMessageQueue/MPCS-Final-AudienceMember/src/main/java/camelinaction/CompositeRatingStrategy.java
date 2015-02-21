package camelinaction;

import java.util.*;

class CompositeRatingStrategy extends RatingStrategy {
	
	ArrayList<RatingStrategy> strategyList = new ArrayList<RatingStrategy>();
	
	//Takes all the generated strategies and averages their combined ratings for each song
	public double rating(String bpm, String pastRating){
		double averageRating = 0;
		
		for(int i=0; i<strategyList.size(); i++){
			RatingStrategy thisStrategy = strategyList.get(i);
			averageRating += thisStrategy.rating(bpm, pastRating);
		}
		
		return averageRating/strategyList.size();		
	}
	
	//Adds a strategy to the array (strategyList)
	public void add(RatingStrategy rs){
		strategyList.add(rs);
	}	
}



