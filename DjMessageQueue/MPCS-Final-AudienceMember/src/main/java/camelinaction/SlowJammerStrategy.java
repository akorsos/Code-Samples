package camelinaction;

public class SlowJammerStrategy extends RatingStrategy{
	public double rating(String bpm, String pastRating){
		int beats = Integer.parseInt(bpm);
		
		if(beats < 128){
			return 3;
		} else {
			return 1;
		}
	}
}
