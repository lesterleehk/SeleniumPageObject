package com.junit;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.runners.Parameterized;
import org.junit.runners.model.RunnerScheduler;

import com.config.Config;

public class Parallelized extends Parameterized {

	private static class ThreadPoolScheduler implements RunnerScheduler {
		private ExecutorService executor;

		public ThreadPoolScheduler() {

			int numThreads = Integer.valueOf(Config.getInstance().getProperty("Parallel")).intValue(); 
			executor = Executors.newScheduledThreadPool(numThreads);
			
			System.out.println("Running with " + numThreads + " threads");
		}

		@Override
		public void finished() {
			executor.shutdown();
			try {
				executor.awaitTermination(999, TimeUnit.MINUTES);
				System.out.println("shutdowning threads");
			} catch (InterruptedException exc) {
 				throw new RuntimeException("Executing thread reaching time limit, the junit testing hang\n "+ exc.getStackTrace().toString());
			}
		}

		@Override
		public void schedule(Runnable childStatement) {
			executor.submit(childStatement);
		}
	}

	public Parallelized(Class klass) throws Throwable {
		super(klass);
		setScheduler(new ThreadPoolScheduler());
	}

}
