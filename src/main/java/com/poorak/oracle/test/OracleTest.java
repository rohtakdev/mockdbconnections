package com.poorak.oracle.test;

import java.beans.PropertyVetoException;
import java.sql.SQLException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class OracleTest {

	public static void main(String args[]) throws PropertyVetoException,
			SQLException, InterruptedException {
		long startTime = 0;
		try {

			startTime = System.currentTimeMillis();

			CyclicBarrier cyclicBarrier = new CyclicBarrier(12,
					new CyclicBarrierAction());
			ExecutorService executor = Executors.newFixedThreadPool(12);

			for (int i = 0; i < 12; i++) {
				Runnable worker = new GetConnectionRunnable(cyclicBarrier);
				executor.execute(worker);
			}
			
			
			executor.shutdown();
			
			while (!executor.isTerminated()) {
			}
			
			System.out.println("Finished all threads");

		} finally {

			long endTime = System.currentTimeMillis();
			System.out.println("test ended  - " + System.currentTimeMillis());
			System.out.println("total time taken  - " + (endTime - startTime)
					/ 1000);

		}

	}
}
