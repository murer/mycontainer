package com.googlecode.mycontainer.jms;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.QueueConnectionFactory;
import javax.jms.Session;
import javax.jms.TemporaryQueue;
import javax.jms.TextMessage;
import javax.jms.Topic;

@Stateless
public class TesterServiceBean implements TesterService {

	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(TesterServiceBean.class);

	@Resource
	private ConnectionFactory connectionFactory;

	@Resource(mappedName = "queue/queueRaoni")
	private Queue queue;

	@Resource(mappedName = "topic/topicRaoni")
	private Topic topic;

	public void sendMsg(String msg) {
		Connection connection = null;
		Session session = null;
		try {
			connection = connectionFactory.createConnection();
			session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

			MessageProducer sender = session.createProducer(queue);
			TemporaryQueue tempQueue = session.createTemporaryQueue();

			TextMessage txtMessage = session.createTextMessage(msg);
			txtMessage.setJMSReplyTo(tempQueue);

			MessageConsumer consumer = session.createConsumer(tempQueue);
			sender.send(txtMessage);

			connection.start();

			consumer.receive();
		} catch (JMSException e) {
			throw new RuntimeException(e);
		} finally {
			close(session);
			close(connection);
		}
	}

	public void sendMsgToTopic(String msg) {
		Connection connection = null;
		Session session = null;
		try {
			connection = ((QueueConnectionFactory) connectionFactory)
					.createQueueConnection();
			session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

			MessageProducer sender = session.createProducer(topic);
			TemporaryQueue tempQueue = session.createTemporaryQueue();

			TextMessage txtMessage = session.createTextMessage(msg);
			txtMessage.setJMSReplyTo(tempQueue);

			MessageConsumer consumer = session.createConsumer(tempQueue);
			sender.send(txtMessage);

			connection.start();

			consumer.receive();
		} catch (JMSException e) {
			throw new RuntimeException(e);
		} finally {
			close(session);
			close(connection);
		}
	}

	private void close(Connection conn) {
		try {
			if (conn != null) {
				conn.close();
			}
		} catch (JMSException e) {
			LOG.error("Error closing jms connection", e);
		}
	}

	private void close(Session session) {
		try {
			if (session != null) {
				session.close();
			}
		} catch (JMSException e) {
			LOG.error("Error closing jms session", e);
		}
	}
}
