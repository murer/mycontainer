package com.googlecode.mycontainer.jms;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.List;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.QueueConnectionFactory;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.InitialContext;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQMessageConsumer;
import org.apache.activemq.RedeliveryPolicy;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.googlecode.mycontainer.ejb.SessionInterceptorDeployer;
import com.googlecode.mycontainer.ejb.StatelessDeployer;
import com.googlecode.mycontainer.jta.MyTransactionManagerDeployer;
import com.googlecode.mycontainer.kernel.ShutdownCommand;
import com.googlecode.mycontainer.kernel.boot.ContainerBuilder;
import com.googlecode.mycontainer.kernel.naming.MyNameParser;

/**
 * There is a bug with redelivery (MessageListener, not transacted and
 * AUTO_ACKNOWLEDGE):
 * <url>https://issues.apache.org/activemq/browse/AMQ-906</url>
 * 
 * I will wait for activemq 5.4 release to final solution.
 * 
 * You can do a <code>consumer.rollback()</code> like the
 * <code>MyListener.onMessage</code> as a workaround.
 * 
 * 
 */
public class JMSDeployerErrorTest {

	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(JMSDeployerErrorTest.class);

	private class MyListener implements MessageListener {
		private ActiveMQConnection conn;
		private Session session;
		private MessageConsumer consumer;
		private final List<String> msgs = new ArrayList<String>();
		private final Object lock = new Object();

		public void onMessage(Message message) {
			TextMessage text = (TextMessage) message;
			try {
				synchronized (lock) {
					String t = text.getText();
					LOG.info("Message received: " + t + " " + message.getJMSRedelivered());
					boolean error = t.startsWith("Error");
					if (error) {
						t = t.split("\\:", 2)[1];
					}
					this.msgs.add(t);
					lock.notifyAll();
					if (error) {
						ActiveMQMessageConsumer amc = (ActiveMQMessageConsumer) consumer;
						amc.rollback();
					}
				}
			} catch (JMSException e) {
				throw new RuntimeException(e);
			}
		}

		public String nextMessage() throws Exception {
			return nextMessage(100l);
		}

		public String nextMessage(long time) throws Exception {
			synchronized (lock) {
				if (this.msgs.isEmpty()) {
					lock.wait(time);
				}
				if (this.msgs.isEmpty()) {
					return null;
				}
				String ret = this.msgs.remove(0);
				return ret;
			}
		}

		public void start(ConnectionFactory connectionFactory, Queue queue) throws Exception {
			conn = (ActiveMQConnection) connectionFactory.createConnection();
			RedeliveryPolicy policy = conn.getRedeliveryPolicy();
			policy.setMaximumRedeliveries(3);
			policy.setInitialRedeliveryDelay(5);
			session = conn.createSession(false, Session.AUTO_ACKNOWLEDGE);
			consumer = session.createConsumer(queue);
			consumer.setMessageListener(this);
			conn.start();
		}

		public void close() {
			c(consumer);
			c(session);
			c(conn);
		}

	};

	private InitialContext ctx;

	private Queue queue;
	private ConnectionFactory connectionFactory;

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
	}

	@Test
	public void testCreateProducer() throws Exception {
		MyListener listener = new MyListener();
		listener.start(connectionFactory, queue);
		send("My message");
		assertEquals("My message", listener.nextMessage());
		send("My message2");
		assertEquals("My message2", listener.nextMessage());
		assertNull(listener.nextMessage());

		send("Error: My message3");
		assertEquals(" My message3", listener.nextMessage());
		assertEquals(" My message3", listener.nextMessage());
		assertEquals(" My message3", listener.nextMessage());
		assertEquals(" My message3", listener.nextMessage());
		assertNull(listener.nextMessage());

		listener.close();
	}

	private void send(String msg) {
		Connection connection = null;
		Session session = null;
		MessageConsumer consumer1 = null;
		MessageProducer sender = null;
		try {
			connection = connectionFactory.createConnection();
			session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			sender = session.createProducer(queue);
			TextMessage txtMessage = session.createTextMessage(msg);
			sender.send(txtMessage);
		} catch (JMSException e) {
			throw new RuntimeException(e);
		} finally {
			c(consumer1);
			c(sender);
			c(session);
			c(connection);
		}
	}

	private void c(Connection conn) {
		try {
			if (conn != null) {
				conn.stop();
				conn.close();
			}
		} catch (JMSException e) {
			LOG.error("Error closing jms connection", e);
		}
	}

	private void c(MessageConsumer o) {
		try {
			if (o != null) {
				o.close();
			}
		} catch (JMSException e) {
			LOG.error("Error closing jms session", e);
		}
	}

	private void c(MessageProducer o) {
		try {
			if (o != null) {
				o.close();
			}
		} catch (JMSException e) {
			LOG.error("Error closing jms session", e);
		}
	}

	private void c(Session session) {
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
		try {

			ShutdownCommand shutdown = new ShutdownCommand();
			shutdown.setContext(new InitialContext());
			shutdown.shutdown();
		} catch (Exception e) {
			LOG.error("Error shutdown", e);
		}
	}
}