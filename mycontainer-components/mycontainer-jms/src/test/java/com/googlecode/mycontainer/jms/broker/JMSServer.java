package com.googlecode.mycontainer.jms.broker;

import org.apache.activemq.broker.BrokerFactory;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.command.ActiveMQDestination;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.ActiveMQTopic;
import org.junit.Test;

public class JMSServer {

	public static final String BROKER_SERVER = "broker:(tcp://localhost:61616)?persistent=false&useJmx=true";
	public static final String BROKER_CLIENT = "tcp://localhost:61616";

	@Test
	public void testClient() throws Exception {
		BrokerService broker = BrokerFactory.createBroker(BROKER_SERVER);
		ActiveMQTopic topic = new ActiveMQTopic("topicTest");
		ActiveMQQueue queue = new ActiveMQQueue("queueTest");
		broker.setDestinations(new ActiveMQDestination[] { topic, queue });
		broker.start();
		Thread.sleep(Long.MAX_VALUE);
	}

}
