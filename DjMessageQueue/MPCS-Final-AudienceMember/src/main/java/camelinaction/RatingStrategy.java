package camelinaction;

abstract class RatingStrategy {
	
	abstract double rating(String bpm, String pastRating);
	
	String setToString(double rating) {
		int numStars = (int)Math.round(rating);
		
		if(numStars == 3){
			return " THREESTARS (" + numStars + ")";
		} else {
		if(numStars == 2){
			return " TWOSTARS (" + numStars + ")";
		} else {
			return " ONESTAR (" + numStars + ")";
			}
		} 
	}	
}
