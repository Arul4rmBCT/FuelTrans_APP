package com.bct.HOS.App.utils;

import java.sql.DriverManager;
import java.sql.SQLException;

import org.apache.commons.dbcp2.ConnectionFactory;
import org.apache.commons.dbcp2.DriverManagerConnectionFactory;
import org.apache.commons.dbcp2.PoolingDriver;
import org.apache.commons.pool2.ObjectPool;

public class PoolObjectFactory {

	public static final String POSTGRESQL_DRIVER = "org.postgresql.Driver";
	public static final String DBCP_DRIVER = "org.apache.commons.dbcp2.PoolingDriver";
	public static Class dirverClass;
	private static PoolingDriver driver;

	/**
	 * 
	 * @param driver
	 */
	public static void registerJDBCDriver(String driver) {
		try {
			dirverClass = Class.forName(driver);
		} catch (ClassNotFoundException e) {
			System.err.println("There was not able to find the driver class");
		}
	}

	/**
	 * Get a Connection Factory, the default implementation is a
	 * DriverManagerConnectionFactory
	 * 
	 * @param connectionURI
	 * @param user
	 * @param password
	 * @return The Factory
	 */
	public static ConnectionFactory getConnFactory(String connectionURI, String user, String password) {
		ConnectionFactory driverManagerConnectionFactory = new DriverManagerConnectionFactory(connectionURI, user,
				password);
		return driverManagerConnectionFactory;
	}

	/**
	 * 
	 * @return the DBCP Driver
	 */
	public static PoolingDriver getDBCPDriver() {
		try {
			Class.forName(DBCP_DRIVER);
		} catch (ClassNotFoundException e) {
			System.err.println("There was not able to find the driver class");
		}
		try {
			driver = (PoolingDriver) DriverManager.getDriver("jdbc:apache:commons:dbcp:");
		} catch (SQLException e) {
			System.err.println("There was an error: " + e.getMessage());
		}
		return driver;
	}

	/**
	 * Registry a Pool in the DBCP Driver
	 * 
	 * @param poolName
	 * @param pool
	 */
	public static void registerPool(String poolName, ObjectPool pool) {
		driver.registerPool(poolName, pool);
	}

}
