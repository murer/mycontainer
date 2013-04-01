/*
 * Copyright 2008 Whohoo Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable
 * law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */
package com.googlecode.mycontainer.ejb;

import java.util.List;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Session;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NamingException;

import com.googlecode.mycontainer.kernel.deploy.DefaultIntercetorDeployer;
import com.googlecode.mycontainer.kernel.deploy.DeployException;
import com.googlecode.mycontainer.kernel.deploy.Deployer;
import com.googlecode.mycontainer.kernel.naming.MyNameParser;
import com.googlecode.mycontainer.kernel.naming.ObjectProvider;
import com.googlecode.mycontainer.kernel.reflect.proxy.ContextInterceptor;
import com.googlecode.mycontainer.kernel.reflect.proxy.ProxyEngine;

public class MessageDrivenDepoyer extends Deployer implements ObjectProvider,
		MessageListener {

	private static final long serialVersionUID = -1537694308246275686L;

	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
			.getLogger(MessageDrivenDepoyer.class);

	// Lets use the session interceptors for while
	private String intercetorDeployerName = SessionInterceptorDeployer.DEFAULT_NAME;

	private Connection connection = null;
	private Session session = null;
	private Class<? extends MessageListener> messageListener;
	private String destinationName;

	public void shutdown() {
		LOG.info("Stoping MDB: " + messageListener.getName());
		close(session);
		close(connection);
	}

	public Object provide(Name name) {
		try {
			Context ctx = getContext();
			MessageListener impl = messageListener.newInstance();
			ProxyEngine<MessageListener> engine = new ProxyEngine<MessageListener>(
					MessageListener.class, impl, getContextName());
			engine.addInterface(StatelessCallback.class);

			DefaultIntercetorDeployer interceptors = (DefaultIntercetorDeployer) ctx
					.lookup(intercetorDeployerName);
			List<ContextInterceptor> list = interceptors.createInterceptors();
			for (ContextInterceptor contextInterceptor : list) {
				engine.addInterceptor(contextInterceptor);
			}

			StatelessCallback ret = (StatelessCallback) engine.create();
			ret.ejbPreConstruct();
			ret.ejbPostConstruct();

			return ret;
		} catch (InstantiationException e) {
			throw new DeployException(e);
		} catch (IllegalAccessException e) {
			throw new DeployException(e);
		} catch (NamingException e) {
			throw new DeployException(e);
		}
	}

	private String getContextName() {
		String name = getName();
		return " MessageDrivenContext/" + name;
	}

	public String getName() {
		String name = messageListener.getSimpleName() + "/mdb";
		return name;
	}

	public void config(Class<? extends MessageListener> resource) {
		MessageDriven mdb = resource.getAnnotation(MessageDriven.class);
		if (mdb == null) {
			throw new DeployException("@MessageDriven not found");
		}
		this.messageListener = resource;

		for (ActivationConfigProperty config : mdb.activationConfig()) {
			if (config.propertyName().equals("destination")) {
				destinationName = config.propertyValue();
				break;
			}
		}

		if (destinationName == null) {
			throw new DeployException(
					"@ActivationConfigProperty setted to destination not found");
		}

		Context ctx = getContext();
		try {
			ConnectionFactory connectionFactory = (ConnectionFactory) ctx
					.lookup(MyNameParser.parseClassName("resource",
							ConnectionFactory.class));
			Destination destination = (Destination) ctx.lookup(destinationName);
			createConsumer(connectionFactory, destination);
		} catch (NamingException e) {
			throw new DeployException(e);
		}
	}

	public static boolean isMessageDriven(Class<?> clazz) {
		boolean assignableFrom = MessageListener.class.isAssignableFrom(clazz);
		MessageDriven annotation = clazz.getAnnotation(MessageDriven.class);
		return (assignableFrom && annotation != null);
	}

	public void deploy(Class<? extends MessageListener> resource) {
		config(resource);
		try {
			Context ctx = getContext();

			String name = getName();
			LOG.info("Deploying: " + name + " " + resource.getSimpleName());
			String contextName = getContextName();
			ctx.createSubcontext(contextName);
			ctx.bind(name, this);
			getKernel().addShutdownHook(this);
		} catch (NamingException e) {
			throw new DeployException(e);
		}
	}

	private void createConsumer(ConnectionFactory connectionFactory,
			Destination destination) {

		try {
			connection = connectionFactory.createConnection();
			session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

			MessageConsumer consumer = session.createConsumer(destination);
			// Set myself to message listener and I fire the MDB to create all
			// context
			consumer.setMessageListener(this);
			connection.start();
		} catch (JMSException e) {
			throw new RuntimeException(e);
		}
	}

	public void onMessage(Message message) {
		MessageListener listener;
		try {
			listener = (MessageListener) getContext().lookup(getName());
		} catch (NamingException e) {
			throw new RuntimeException(e);
		}
		listener.onMessage(message);
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
