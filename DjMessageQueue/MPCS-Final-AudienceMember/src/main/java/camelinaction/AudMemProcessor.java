package camelinaction;
import java.util.*;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

public class AudMemProcessor implements Processor {
	public void process(Exchange e) throws Exception {

		Random r = new Random();
		int rand = r.nextInt(4)+1;
		
		String [] array = e.getIn().getBody(String.class)
                .replaceAll("\\[","").replaceAll("\\]","").split("-");

        String artist = array[0];
        String title = array[1];
        String bpm = array[2];
        String pastRating = array[3];

        StringBuilder sb = new StringBuilder();
        
        CompositeRatingStrategy s = new CompositeRatingStrategy();
        RatingStrategy s_temp;
        
        //Random number generator creates a random strategy to use
        if(rand == 4){
        	s_temp = new BandwagonerStrategy();
        } else {
        if(rand == 3){
        	s_temp = new EDMFanStrategy();
        } else {
        if(rand == 2){
        	s_temp = new HipsterStrategy();
        } else {
        	s_temp = new SlowJammerStrategy();
        		}
        	}
        }

        //Adds the strategy to the Composite
        s.add(s_temp);
        
    	double rating = s.rating(bpm, pastRating);
    	String ratingText = s.setToString(rating);  
    	
        sb.append("").append(artist.trim());
        sb.append("-").append(title.trim());
        sb.append(ratingText);

        e.getIn().setBody(sb.toString());       		
	}
}
