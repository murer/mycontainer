package com.googlecode.mycontainer.jms;

import javax.jms.JMSException;
import javax.jms.Topic;

import com.googlecode.mycontainer.kernel.deploy.DeployException;
import com.googlecode.mycontainer.kernel.deploy.NamingDeployer;

public class TopicDeployer extends NamingDeployer {

	private static final long serialVersionUID = 6531636587896925296L;

	private Topic topic;

	public TopicDeployer(Topic topic) {
		this.topic = topic;
	}

	@Override
	protected Object getResource() {
		return topic;
	}

	@Override
	public void deploy() {
		setName(generateName());
		super.deploy();
	}

	public Topic getTopic() {
		return topic;
	}

	public void setTopic(Topic topic) {
		this.topic = topic;
	}

	private String generateName() {
		try {
			return "topic/" + topic.getTopicName();
		} catch (JMSException e) {
			throw new DeployException(e);
		}
	}
}
