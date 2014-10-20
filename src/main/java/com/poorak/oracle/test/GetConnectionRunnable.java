package com.poorak.oracle.test;

import java.beans.PropertyVetoException;
import java.sql.SQLException;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.mchange.v2.c3p0.DataSources;

public class GetConnectionRunnable implements Runnable {

	private ComboPooledDataSource cpds;
	private static final int MIN_POOL_SIZE  = 8;
	private static final int MAX_POOL_SIZE = 8;
	private CyclicBarrier cyclicBarrier;

	public GetConnectionRunnable(CyclicBarrier cyclicBarrier) {
		super();
		this.cyclicBarrier = cyclicBarrier;
	
	}

	@Override
	public void run() {

		cpds = new ComboPooledDataSource();
		long startTime = 0;
		try {	
																	
			cpds.setDriverClass("oracle.jdbc.driver.OracleDriver");													// driver
			cpds.setJdbcUrl("jdbc:oracle:thin:@localhost:1521:PMTEST");
			//PRODUCTION STRING- cpds.setJdbcUrl("jdbc:oracle:thin:@prod-db6a.mqube.us:1521:MSGDB6");
			cpds.setUser("cmpadmin");
			cpds.setPassword("cmpadmin");

			// the settings below are optional -- c3p0 can work with defaults
			cpds.setMinPoolSize(MIN_POOL_SIZE);
			cpds.setAcquireIncrement(5);
			cpds.setMaxPoolSize(MAX_POOL_SIZE);

			System.out.println(Thread.currentThread().getName()
					+ " - Reached barrier");

			cyclicBarrier.await();

			startTime = System.currentTimeMillis();

			System.out.println(Thread.currentThread().getName()
					+ " - getting connections - " + System.currentTimeMillis());

			for (int i = 0; i < MIN_POOL_SIZE; i++) {
				cpds.getConnection();
			}

			System.out.println(Thread.currentThread().getName()
					+ " - num_connections: "
					+ cpds.getNumConnectionsDefaultUser());
			System.out.println(Thread.currentThread().getName()
					+ " - num_busy_connections: "
					+ cpds.getNumBusyConnectionsDefaultUser());
			System.out.println(Thread.currentThread().getName()
					+ " - num_idle_connections: "
					+ cpds.getNumIdleConnectionsDefaultUser());
			

		} catch (PropertyVetoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (BrokenBarrierException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {

			System.out.println(Thread.currentThread().getName()
					+ " - closing connections");
			try {
				DataSources.destroy(cpds);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			long endTime = System.currentTimeMillis();
			System.out.println(Thread.currentThread().getName()
					+ " - total time taken  - " + (endTime - startTime));
			System.out.println();
		}

	}

}
