package camelinaction;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;

public class AudMemRoute extends RouteBuilder {
	public void configure() {
		
		from("jms:queue:TwoStarQueue").process(new Processor() {
			@Override
			public void process(Exchange e) throws Exception {
				String body = e.getIn().getBody(String.class);
				e.getIn().setBody(body + "\n");
			}
		}).
		log("From TwoStarQueue: jms queue: ${body}from${header.CamelFileNameOnly}").
		to("file:data/outbox?fileName=TwoStarSongs.csv&fileExist=Append");
		
		from("jms:queue:OneStarQueue").process(new Processor() {
			@Override
			public void process(Exchange e) throws Exception {
				String body = e.getIn().getBody(String.class);
				e.getIn().setBody(body + "\n");
			}
		}).
		log("From OneStarQueue: jms queue: ${body}from${header.CamelFileNameOnly}").
		to("file:data/outbox?fileName=OneStarSongs.csv&fileExist=Append");

		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		//from("file:data/outbox").to("jms:MPCS51050_config_test");
    }
}
