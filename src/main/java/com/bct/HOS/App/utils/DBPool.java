package com.bct.HOS.App.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.apache.commons.dbcp2.ConnectionFactory;
import org.apache.commons.dbcp2.PoolableConnection;
import org.apache.commons.dbcp2.PoolableConnectionFactory;
import org.apache.commons.dbcp2.PoolingDriver;
import org.apache.commons.dbcp2.Utils;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

public class DBPool {

	private static DBPool dbpool_instance = null;

	HOSConfig conf = null;
	private static String URI = null;
	private static String user = null;
	private static String password = null;
    private GenericObjectPool<PoolableConnection> connectionPool = null;
    
	public DBPool() {
		conf = new HOSConfig();
		URI = conf.getValue("HOS_DB_URL");
		user = conf.getValue("HOS_DB_USER");
		password = conf.getValue("HOS_DB_PASS");
		initializePool();
	}

	public static DBPool getInstance() {
		if (dbpool_instance == null)
			dbpool_instance = new DBPool();

		return dbpool_instance;
	}

	public void initializePool() {
		// 1. Register the Driver to the jbdc.driver java property
		PoolObjectFactory.registerJDBCDriver(PoolObjectFactory.POSTGRESQL_DRIVER);

		// 2. Create the Connection Factory (DriverManagerConnectionFactory)
		ConnectionFactory connectionFactory = PoolObjectFactory.getConnFactory(URI, user, password);

		// 3. Instantiate the Factory of Pooled Objects
		PoolableConnectionFactory poolfactory = new PoolableConnectionFactory(connectionFactory, null);
		
		// Configuration for Pool
	    final GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();
	    poolConfig.setMinIdle(3);
	    poolConfig.setMaxTotal(25);
	    
		// 4. Create the Pool with the PoolableConnection objects
		connectionPool = new GenericObjectPool(poolfactory,poolConfig);

		// 5. Set the objectPool to enforces the association (prevent bugs)
		poolfactory.setPool(connectionPool);

		// 6. Get the Driver of the pool and register them
		PoolingDriver dbcpDriver = PoolObjectFactory.getDBCPDriver();
		dbcpDriver.registerPool("hos-web", connectionPool);
	}
	
	private GenericObjectPool<PoolableConnection> getConnectionPool() {
        return connectionPool;
    }
	
	private void printPoolStatus() {
		System.out.println("Max   : " + getConnectionPool().getNumActive() + "; " +
	            "Active: " + getConnectionPool().getNumActive() + "; " +
	            "Idle  : " + getConnectionPool().getNumIdle());
	}

	public Connection getConnection() {
		Connection connJCG = null;
		// 7. Get a connection and use them
		try {
			connJCG = DriverManager.getConnection("jdbc:apache:commons:dbcp:hos-web");
			
			// Print Some Properties.
			printPoolStatus();
			////System.out.println("Hashcode: " + connJCG.hashCode());
			////System.out.println("JDBC Driver: " + connJCG.getMetaData().getDriverName());
			////System.out.println("URI DB: " + connJCG.getMetaData().getURL());
		} catch (SQLException e) {
			System.err.println("There was an error: " + e.getMessage());
		}
		return connJCG;
	}

	public void closeConnection(Connection connJCG) {
		try {
			// 8. Close the connection to return them to the pool. Instead of connJCG.close();
			Utils.closeQuietly(connJCG);
			printPoolStatus();
		} catch (Exception e) {
			System.err.println("There was an error: " + e.getMessage());
		}
	}

}
