package camelinaction;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;

public class DJRoute extends RouteBuilder {
	public void configure() {
		from("jms:queue:ThreeStarQueue").
		process(new Processor() {
			@Override
			public void process(Exchange e) throws Exception {
				String body = e.getIn().getBody(String.class);
				e.getIn().setBody(body + "\n");
			}
		}).
		log("From ThreeStarQueue: jms queue: ${body}from${header.CamelFileNameOnly}").
		to("file:data/outbox?fileName=ThreeStarSongs.csv&fileExist=Append");

		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		//from("file:data/outbox").to("jms:MPCS51050_config_test");
    }
}
