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

package com.googlecode.mycontainer.test.ejb;

import static org.junit.Assert.assertEquals;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TemporaryQueue;
import javax.naming.InitialContext;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.googlecode.mycontainer.ejb.MessageDrivenScannableDeployer;
import com.googlecode.mycontainer.ejb.SessionInterceptorDeployer;
import com.googlecode.mycontainer.ejb.StatelessScannableDeployer;
import com.googlecode.mycontainer.jms.JMSDeployer;
import com.googlecode.mycontainer.jta.MyTransactionManagerDeployer;
import com.googlecode.mycontainer.kernel.ShutdownCommand;
import com.googlecode.mycontainer.kernel.boot.ContainerBuilder;
import com.googlecode.mycontainer.kernel.deploy.NamingAliasDeployer;
import com.googlecode.mycontainer.kernel.deploy.ScannerDeployer;
import com.googlecode.mycontainer.kernel.naming.MyNameParser;

public class MessageDrivenBeanTest {

	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(MessageDrivenBeanTest.class);

	private ContainerBuilder builder;
	private InitialContext ctx;

	@Test
	public void testMDB() throws Exception {
		ConnectionFactory connectionFactory = (ConnectionFactory) ctx.lookup(MyNameParser.parseClassName("resource", ConnectionFactory.class));

		Queue queue = (Queue) ctx.lookup("queue/testQueue");

		Connection connection = null;
		Session session = null;
		try {
			connection = connectionFactory.createConnection();
			session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

			MessageProducer sender = session.createProducer(queue);
			TemporaryQueue tempQueue = session.createTemporaryQueue();

			ObjectMessage message = session.createObjectMessage(new Integer[] { 20, 10 });
			message.setJMSReplyTo(tempQueue);

			MessageConsumer consumer = session.createConsumer(tempQueue);
			sender.send(message);

			connection.start();

			ObjectMessage response = (ObjectMessage) consumer.receive(5000);
			assertEquals(2, (Integer) response.getObject());
		} catch (JMSException e) {
			throw new RuntimeException(e);
		}

	}

	@Before
	public void boot() throws Exception {
		builder = new ContainerBuilder();

		SessionInterceptorDeployer sessionInterceptorDeployer = builder.createDeployer(SessionInterceptorDeployer.class);
		sessionInterceptorDeployer.deploy();

		builder.createDeployer(MyTransactionManagerDeployer.class).setName("TransactionManager").deploy();

		JMSDeployer jmsDeployer = builder.createDeployer(JMSDeployer.class);
		jmsDeployer.createQueue("testQueue");
		jmsDeployer.deploy();

		NamingAliasDeployer alias = builder.createDeployer(NamingAliasDeployer.class);
		alias.setDestination("resource/javax/jms/ConnectionFactory");
		alias.setName("ConnectionFactory");
		alias.deploy();

		ScannerDeployer scanner = builder.createDeployer(ScannerDeployer.class);
		scanner.add(new StatelessScannableDeployer());
		scanner.add(new MessageDrivenScannableDeployer());
		scanner.scan(SumServiceBean.class);
		scanner.deploy();

		ctx = builder.getContext();
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
