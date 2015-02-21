package camelinaction;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;

public class PublisherContext extends DefaultCamelContext {
	public RouteBuilder state;
	
	RouteBuilder getState() {
		return state;
	}
	void setState(RouteBuilder newState) {
		state = newState;
	}
	void handle() throws Exception {
		addRoutes(state);
	}
}
