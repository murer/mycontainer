package com.googlecode.mycontainer.jms;

import static org.junit.Assert.assertEquals;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.QueueConnectionFactory;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.googlecode.mycontainer.ejb.SessionInterceptorDeployer;
import com.googlecode.mycontainer.ejb.StatelessDeployer;
import com.googlecode.mycontainer.jta.MyTransactionManagerDeployer;
import com.googlecode.mycontainer.kernel.ShutdownCommand;
import com.googlecode.mycontainer.kernel.boot.ContainerBuilder;
import com.googlecode.mycontainer.kernel.naming.MyNameParser;

public class JMSDeployerTest {

	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(JMSDeployerTest.class);

	private class MyListener implements MessageListener {
		private String msg = null;

		public void onMessage(Message message) {
			TextMessage text = (TextMessage) message;
			try {
				this.msg = text.getText();

				Destination replyTo = text.getJMSReplyTo();
				MessageProducer producer = session.createProducer(replyTo);
				producer.send(session.createMessage());
			} catch (JMSException e) {
				throw new RuntimeException(e);
			}
		}

		public String getMsg() {
			return msg;
		}

	};

	private InitialContext ctx;

	private Connection conn;
	private Session session;
	private Queue queue;
	private Topic topic;
	private ConnectionFactory connectionFactory;
	private TesterService service;

	@Before
	public void bootMycontainer() throws Exception {
		ContainerBuilder builder = new ContainerBuilder();

		SessionInterceptorDeployer sessionInterceptorDeployer = builder.createDeployer(SessionInterceptorDeployer.class);
		sessionInterceptorDeployer.deploy();

		builder.createDeployer(MyTransactionManagerDeployer.class).setName("TransactionManager").deploy();

		JMSDeployer jmsDeployer = builder.createDeployer(JMSDeployer.class);
		jmsDeployer.createQueue("queueRaoni");
		jmsDeployer.createTopic("topicRaoni");
		jmsDeployer.deploy();

		StatelessDeployer statelessDeployer = builder.createDeployer(StatelessDeployer.class);
		statelessDeployer.deploy(TesterServiceBean.class);

		ctx = builder.getContext();

		connectionFactory = (QueueConnectionFactory) ctx.lookup(MyNameParser.parseClassName("resource", ConnectionFactory.class));
		queue = (Queue) ctx.lookup("queue/queueRaoni");
		topic = (Topic) ctx.lookup("topic/topicRaoni");
		conn = connectionFactory.createConnection();
		session = conn.createSession(false, Session.AUTO_ACKNOWLEDGE);

		service = (TesterService) ctx.lookup(MyNameParser.parseClassName("ejb", TesterService.class));
	}

	@Test
	public void testCreateProducer() throws NamingException, JMSException {
		String msg = "Test";

		MyListener listener = new MyListener();

		MessageConsumer consumer = session.createConsumer(queue);
		consumer.setMessageListener(listener);
		conn.start();

		service.sendMsg(msg);

		assertEquals(msg, listener.getMsg());
	}

	@Test
	public void testCreateTopicProducer() throws NamingException, JMSException {
		String msg = "Test";

		MyListener listener = new MyListener();

		MessageConsumer consumer = session.createConsumer(topic);
		consumer.setMessageListener(listener);
		conn.start();

		service.sendMsgToTopic(msg);

		assertEquals(msg, listener.getMsg());
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

	@After
	public void shutdown() {
		close(session);
		close(conn);
		try {

			ShutdownCommand shutdown = new ShutdownCommand();
			shutdown.setContext(new InitialContext());
			shutdown.shutdown();
		} catch (Exception e) {
			LOG.error("Error shutdown", e);
		}
	}
}