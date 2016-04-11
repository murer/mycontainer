package com.googlecode.mycontainer.kernel.deploy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class AsyncDeployer {

	private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(AsyncDeployer.class);

	private static final long TIMEOUT = 60;

	private ExecutorService executorService;

	public AsyncDeployer() {
		this(Executors.newCachedThreadPool());
	}

	public AsyncDeployer(ExecutorService executorService) {
		super();
		this.executorService = executorService;
	}

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

	public Future<Void> getAll(Collection<Future<Void>> futures) {
		return new CollectionFuture(futures);
	}

	public Future<Void> getAll(Future<Void>... futures) {
		return new CollectionFuture(Arrays.asList(futures));
	}

	@SuppressWarnings("unchecked")
	public Future<Void> deploy(final SimpleDeployer deployer) {
		return (Future<Void>) executorService.submit(new DeployTask(deployer));
	}

	public void shutdown() {
		try {
			this.executorService.awaitTermination(TIMEOUT, TimeUnit.SECONDS);
			this.executorService.shutdown();
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	public static class CollectionFuture implements Future<Void> {

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

		public Void get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
			for (Future<Void> future : futures) {
				future.get(timeout, unit);
			}
			return null;
		}
	}

	private final class DeployTask implements Runnable {

		private SimpleDeployer deployer;

		public DeployTask(SimpleDeployer deployer) {
			super();
			this.deployer = deployer;
		}

		public void run() {
			LOGGER.info("Executing deployer " + deployer);
			deployer.deploy();
			LOGGER.info("Executed deployer " + deployer);
		}

	}

}
