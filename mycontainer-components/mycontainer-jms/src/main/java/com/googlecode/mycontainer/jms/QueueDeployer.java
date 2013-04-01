package com.googlecode.mycontainer.jms;

import javax.jms.JMSException;
import javax.jms.Queue;

import org.apache.activemq.command.ActiveMQQueue;

import com.googlecode.mycontainer.kernel.deploy.DeployException;
import com.googlecode.mycontainer.kernel.deploy.NamingDeployer;

public class QueueDeployer extends NamingDeployer {

	private static final long serialVersionUID = 6531636587896925296L;

	private Queue queue;

	public QueueDeployer(ActiveMQQueue queue) {
		this.queue = queue;
	}

	@Override
	protected Object getResource() {
		return queue;
	}

	@Override
	public void deploy() {
		setName(generateName());
		super.deploy();
	}

	public Queue getQueue() {
		return queue;
	}

	private String generateName() {
		try {
			return "queue/" + queue.getQueueName();
		} catch (JMSException e) {
			throw new DeployException(e);
		}
	}
}
