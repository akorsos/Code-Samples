package camelinaction;

public class HipsterStrategy extends RatingStrategy {
	
	public double rating(String bpm, String pastRating){
		
		if(pastRating.equals("*")){
			return 3;
		} else {
			return 1;
		}
		
	}

}
