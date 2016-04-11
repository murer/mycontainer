package com.googlecode.mycontainer.jms;

import java.util.ArrayList;
import java.util.Collection;

import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.naming.NamingException;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerFactory;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.command.ActiveMQDestination;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.ActiveMQTopic;

import com.googlecode.mycontainer.kernel.deploy.NamingDeployer;
import com.googlecode.mycontainer.kernel.deploy.SimpleDeployer;
import com.googlecode.mycontainer.kernel.naming.MyNameParser;

public class JMSDeployer extends NamingDeployer implements SimpleDeployer {

	private static final long serialVersionUID = 3150792465673952927L;

	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
			.getLogger(JMSDeployer.class);

	private BrokerService broker;

	private String uri = "broker:(vm://localhost)";

	private String connectionUri = "vm://localhost";

	private Collection<QueueDeployer> queueDeployers = new ArrayList<QueueDeployer>();

	private Collection<TopicDeployer> topicDeployers = new ArrayList<TopicDeployer>();

	public String getConnectionUri() {
		return connectionUri;
	}

	public void setConnectionUri(String connectionUri) {
		this.connectionUri = connectionUri;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public BrokerService getBroker() {
		if (broker == null) {
			try {
				broker = BrokerFactory.createBroker(uri);
				if (!uri.contains("useJmx")) {
					broker.setUseJmx(false);
				}
				if (!uri.contains("persistent")) {
					broker.setPersistent(false);
				}
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		return broker;
	}

	@Override
	public void shutdown() {
		super.shutdown();
		try {
			if (broker != null) {
				LOG.info("Stopping JMS Server");
				broker.stop();
				LOG.info("Done");
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	protected Object getResource() {
		return new ActiveMQConnectionFactory(connectionUri);
	}

	public void createQueue(String queueName) {
		ActiveMQQueue queue = new ActiveMQQueue(queueName);

		QueueDeployer queueDeployer = new QueueDeployer(queue);
		queueDeployer.setContext(getContext());

		queueDeployers.add(queueDeployer);
	}

	public void createTopic(String topicName) {
		ActiveMQTopic topic = new ActiveMQTopic(topicName);

		TopicDeployer topicDeployer = new TopicDeployer(topic);
		topicDeployer.setContext(getContext());

		topicDeployers.add(topicDeployer);
	}

	@Override
	public void deploy() {
		try {
			setName(MyNameParser.parseClassName("resource",
					ConnectionFactory.class));
			super.deploy();

			Collection<ActiveMQDestination> q = deployQueues();
			Collection<ActiveMQDestination> t = deployTopics();
			if (uri != null) {
				Collection<ActiveMQDestination> all = new ArrayList<ActiveMQDestination>();
				all.addAll(q);
				all.addAll(t);
				ActiveMQDestination[] a = all
						.toArray(new ActiveMQDestination[0]);
				getBroker().setDestinations(a);

				getBroker().start();
			}
		} catch (NamingException e) {
			throw new RuntimeException(e);
		} catch (JMSException e) {
			throw new RuntimeException(e);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private Collection<ActiveMQDestination> deployQueues() {
		Collection<ActiveMQDestination> queues = new ArrayList<ActiveMQDestination>();

		for (QueueDeployer deployer : queueDeployers) {
			deployer.deploy();
			queues.add((ActiveMQDestination) deployer.getQueue());
		}

		return queues;
	}

	private Collection<ActiveMQDestination> deployTopics() {
		Collection<ActiveMQDestination> topics = new ArrayList<ActiveMQDestination>();

		for (TopicDeployer deployer : topicDeployers) {
			deployer.deploy();
			topics.add((ActiveMQDestination) deployer.getTopic());
		}

		return topics;
	}
}
