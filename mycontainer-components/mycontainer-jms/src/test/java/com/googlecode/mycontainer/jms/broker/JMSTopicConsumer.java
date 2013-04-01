package com.googlecode.mycontainer.jms.broker;

import javax.jms.Connection;
import javax.jms.MessageConsumer;
import javax.jms.ObjectMessage;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQTopic;
import org.junit.Test;

public class JMSTopicConsumer {

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
		MessageConsumer consumer = session.createConsumer(topic);
		for (int i = 0; i < 3; i++) {
			ObjectMessage msg = (ObjectMessage) consumer.receive();
			System.out.println("" + i + "\t" + msg.getObject());
		}
		session.close();
		conn.stop();
		conn.close();
	}
}
