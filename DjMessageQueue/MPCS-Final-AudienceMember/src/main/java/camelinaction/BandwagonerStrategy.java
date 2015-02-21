package camelinaction;

public class BandwagonerStrategy extends RatingStrategy {
	
	public double rating(String bpm, String pastRating){ 
		
		if(pastRating.equals("***")){
			return 3;
		} else {
		if(pastRating.equals("**")){
			return 2;
			} 
			return 1;
		}	
	}
}
