package com.googlecode.mycontainer.jms.broker;

import javax.jms.Connection;
import javax.jms.MessageProducer;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQTopic;
import org.junit.Test;

public class JMSTopicProducer {

	@Test
	public void testServer() throws Exception {
		// BrokerService broker = BrokerFactory
		// .createBroker(JMSServer.BROKER_CLIENT);
		ActiveMQTopic topic = new ActiveMQTopic("topicTest");
		// broker.setDestinations(new ActiveMQDestination[] { topic });
		// broker.start();

		ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(
				JMSServer.BROKER_CLIENT);
		Connection conn = factory.createConnection();
		conn.start();
		Session session = conn.createSession(false, Session.AUTO_ACKNOWLEDGE);
		MessageProducer producer = session.createProducer(topic);
		producer.send(session.createObjectMessage("msg test 1"));
		producer.send(session.createObjectMessage("msg test 2"));
		producer.send(session.createObjectMessage("msg test 3"));
		producer.send(session.createObjectMessage("msg test 4"));
		session.close();
		conn.stop();
		conn.close();
	}
}
