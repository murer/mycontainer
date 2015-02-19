package com.googlecode.mycontainer.test.ejb;

import static org.junit.Assert.assertNotNull;

import java.util.concurrent.Future;

import javax.naming.InitialContext;

import org.junit.Test;

import com.googlecode.mycontainer.kernel.boot.ContainerBuilder;
import com.googlecode.mycontainer.kernel.deploy.AsyncDeployer;
import com.googlecode.mycontainer.mail.MailDeployer;

public class AsyncDeployerTest {

	private ContainerBuilder builder;

	private AsyncDeployer asyncDeployer;

	@Test
	public void testAsyncDeploy() throws Exception {
		builder = new ContainerBuilder();

		asyncDeployer = new AsyncDeployer();

		MailDeployer mail = builder.createDeployer(MailDeployer.class);
		mail.setName("java:/Mail");

		Future<Void> future = asyncDeployer.deploy(mail);
		future.get();

		InitialContext ctx = builder.getContext();
		assertNotNull(ctx.lookup("java:/Mail"));
	}

}
