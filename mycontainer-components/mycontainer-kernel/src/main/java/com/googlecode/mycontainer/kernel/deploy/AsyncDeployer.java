package com.googlecode.mycontainer.kernel.deploy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class AsyncDeployer {

	private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(AsyncDeployer.class);

	public Future<Void> deployAll(SimpleDeployer... deployers) {
		final List<Future<Void>> futures = new ArrayList<Future<Void>>();
		for (SimpleDeployer deployer : deployers) {
			Future<Void> future = deploy(deployer);
			futures.add(future);
		}
		return new CollectionFuture(futures);
	}

	public Future<Void> deployAll(Collection<SimpleDeployer> deployers) {
		final List<Future<Void>> futures = new ArrayList<Future<Void>>();
		for (SimpleDeployer deployer : deployers) {
			Future<Void> future = deploy(deployer);
			futures.add(future);
		}
		return new CollectionFuture(futures);
	}

	public Future<Void> deploy(final SimpleDeployer deployer) {
		final Thread t = new Thread("AsyncDeployer " + deployer) {
			@Override
			public void run() {
				LOGGER.info("Executing deployer " + deployer);
				deployer.deploy();
				LOGGER.info("Executed deployer " + deployer);
			}
		};
		Future<Void> ret = new DeployerFuture(t);
		t.start();
		return ret;
	}

	private final class CollectionFuture implements Future<Void> {

		private final Collection<Future<Void>> futures;

		private CollectionFuture(Collection<Future<Void>> futures) {
			this.futures = futures;
		}

		public boolean cancel(boolean mayInterruptIfRunning) {
			for (Future<Void> future : futures) {
				if (!future.cancel(true)) {
					return false;
				}
			}
			return true;
		}

		public boolean isCancelled() {
			for (Future<Void> future : futures) {
				if (!future.isCancelled()) {
					return false;
				}
			}
			return true;
		}

		public boolean isDone() {
			for (Future<Void> future : futures) {
				if (!future.isDone()) {
					return false;
				}
			}
			return true;
		}

		public Void get() throws InterruptedException, ExecutionException {
			for (Future<Void> future : futures) {
				future.get();
			}
			return null;
		}

		public Void get(long timeout, TimeUnit unit)
				throws InterruptedException, ExecutionException,
				TimeoutException {
			for (Future<Void> future : futures) {
				future.get(timeout, unit);
			}
			return null;
		}
	}

	private final class DeployerFuture implements Future<Void> {
		private final Thread t;

		private DeployerFuture(Thread t) {
			this.t = t;
		}

		public boolean cancel(boolean mayInterruptIfRunning) {
			throw new RuntimeException("unsupported");
		}

		public boolean isCancelled() {
			return false;
		}

		public boolean isDone() {
			return !t.isAlive();
		}

		public Void get() throws InterruptedException, ExecutionException {
			LOGGER.info("Waiting deployer to finish");
			if (t.isAlive()) {
				t.join();
			}
			return null;
		}

		public Void get(long timeout, TimeUnit unit)
				throws InterruptedException, ExecutionException,
				TimeoutException {
			LOGGER.info("Waiting deployer to finish");
			if (t.isAlive()) {
				t.join(unit.toMillis(timeout));
			}
			return null;
		}
	}
}
