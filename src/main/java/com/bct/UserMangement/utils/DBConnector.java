package com.bct.UserMangement.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import com.bct.HOS.App.utils.HOSConfig;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class DBConnector {

	 private static String dbURL = "jdbc:postgresql://localhost:5432/usermangement";
	 private static String username = "postgres";
	 private static String password = "bct1bnco#";
//	 private static String password = "postgres";


	public Connection conn = null;
	public String schema = null;
	public long unitConversion = 0;
	
	public Connection getConnection() {
		createConnection();
		return conn;
	}

	public String getSchema() {
		return schema;
	}

	public long getUnitConversion() {
		return unitConversion;
	}
	
	private void createConnection() {
		try {
			Class.forName("org.postgresql.Driver");
			HOSConfig conf = new HOSConfig();
			conn = DriverManager.getConnection(conf.getValue("HOS_DB_URL"), conf.getValue("HOS_DB_USER"),
					conf.getValue("HOS_DB_PASS"));
			
			//InitialContext initContext = new InitialContext();
			//DataSource ds = (DataSource) initContext.lookup( conf.getValue("HOS_DB_DS"));//java:/comp/env/jdbc/postgres
			//conn = ds.getConnection();
			
			schema = conf.getValue("HOS_SCHEMA");
			unitConversion = Long.parseLong(conf.getValue("FUEL_UNIT_CONVERT"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public JSONArray parseRS(ResultSet rs) {
		JSONArray json = new JSONArray();
		try {
			ResultSetMetaData rsmd = rs.getMetaData();
			while (rs.next()) {
				int numColumns = rsmd.getColumnCount();
				String val = null;
				JSONObject obj = new JSONObject();

				for (int i = 1; i < numColumns + 1; i++) {
					String column_name = rsmd.getColumnName(i);
					
					
					switch (rsmd.getColumnType(i)) {
					case java.sql.Types.ARRAY:
						obj.put(column_name, rs.getArray(column_name));
						break;
					case java.sql.Types.BIGINT:
						obj.put(column_name, rs.getInt(column_name));
						break;
					case java.sql.Types.BOOLEAN:
						obj.put(column_name, rs.getBoolean(column_name));
						break;
					case java.sql.Types.BLOB:
						obj.put(column_name, rs.getBlob(column_name));
						break;
					case java.sql.Types.DOUBLE:
						obj.put(column_name, rs.getDouble(column_name));
						break;
					case java.sql.Types.FLOAT:
						obj.put(column_name, rs.getFloat(column_name));
						break;
					case java.sql.Types.INTEGER:
						obj.put(column_name, rs.getInt(column_name));
						break;
					case java.sql.Types.NVARCHAR:
						obj.put(column_name, rs.getNString(column_name));
						break;
					case java.sql.Types.VARCHAR:
						val = rs.getString(column_name);
						
						if (column_name.equalsIgnoreCase("VOLUME") || column_name.equalsIgnoreCase("AVERAGE")|| column_name.equalsIgnoreCase("AMOUNT")
								|| column_name.equalsIgnoreCase("TOTAL_VALUE") || column_name.equalsIgnoreCase("SalesAmount")
								|| column_name.equalsIgnoreCase("SUM")|| column_name.equalsIgnoreCase("CURRENT_CAPACITY")
								|| column_name.equalsIgnoreCase("CAPACITY")|| column_name.equalsIgnoreCase("CURRENT_CAPACITY")
								|| column_name.equalsIgnoreCase("MIN_CAPACITY")|| column_name.equalsIgnoreCase("MIN_CAPACITY")
								|| column_name.equalsIgnoreCase("MAX_CAPACITY")|| column_name.equalsIgnoreCase("TCVOLUME")
								|| column_name.equalsIgnoreCase("ULLAGE")|| column_name.equalsIgnoreCase("START_VOLUME")
								|| column_name.equalsIgnoreCase("START_TC_VOLUME")|| column_name.equalsIgnoreCase("END_TC_VOLUME")
								|| column_name.equalsIgnoreCase("END_VOLUME")|| column_name.equalsIgnoreCase("NET_VOLUME")
								|| column_name.equalsIgnoreCase("TOTAL_VALUE")|| column_name.equalsIgnoreCase("AVERAGE")
								|| column_name.equalsIgnoreCase("LAST_INVENTORY")|| column_name.equalsIgnoreCase("AVERAGE_SALES")
								|| column_name.equalsIgnoreCase("SALES")|| column_name.equalsIgnoreCase("DELTA(Ltr)")
								
								|| column_name.equalsIgnoreCase("COMPARISION PERIOD 1 SALES(Ltr)")|| column_name.equalsIgnoreCase("COMPARISION PERIOD 2 - SALES(Ltr)")
								) {
							if (!val.equalsIgnoreCase("0")) {
								////System.out.println(val + " --- " + column_name);
								if (isNumeric(val)) {
									if (val.contains("."))
										val = String.format("%,.3f", Double.valueOf(val));
									else
										val = String.format("%,d", Integer.valueOf(val));
								}
							}
						}
						obj.put(column_name, val);
						break;
					case java.sql.Types.TINYINT:
						obj.put(column_name, rs.getInt(column_name));
						break;
					case java.sql.Types.SMALLINT:
						obj.put(column_name, rs.getInt(column_name));
						break;
					case java.sql.Types.DATE:
						// obj.put(column_name, convertToJsonDate(rs.getString(column_name)));
						obj.put(column_name, rs.getString(column_name));
						break;
					case java.sql.Types.TIMESTAMP:
						String tmp = rs.getString(column_name);
						if (tmp != null)
							obj.put(column_name, rs.getString(column_name));
						else
							obj.put(column_name, "");
						// obj.put(column_name, convertToJsonDateTime(rs.getString(column_name)));

						break;
					case java.sql.Types.TIMESTAMP_WITH_TIMEZONE:
						// obj.put(column_name, convertToJsonDateTime(rs.getString(column_name)));
						obj.put(column_name, rs.getString(column_name));
						break;
					default:
						val = rs.getString(column_name);
						if (column_name.equalsIgnoreCase("VOLUME") || column_name.equalsIgnoreCase("AVERAGE")|| column_name.equalsIgnoreCase("AMOUNT")
								|| column_name.equalsIgnoreCase("TOTAL_VALUE") || column_name.equalsIgnoreCase("SalesAmount")
								|| column_name.equalsIgnoreCase("CAPACITY")|| column_name.equalsIgnoreCase("CURRENT_CAPACITY")
								|| column_name.equalsIgnoreCase("SUM")|| column_name.equalsIgnoreCase("CURRENT_CAPACITY")
								|| column_name.equalsIgnoreCase("MIN_CAPACITY")|| column_name.equalsIgnoreCase("MIN_CAPACITY")
								|| column_name.equalsIgnoreCase("MAX_CAPACITY")|| column_name.equalsIgnoreCase("TCVOLUME")
								|| column_name.equalsIgnoreCase("ULLAGE")|| column_name.equalsIgnoreCase("START_VOLUME")
								|| column_name.equalsIgnoreCase("START_TC_VOLUME")|| column_name.equalsIgnoreCase("END_TC_VOLUME")
								|| column_name.equalsIgnoreCase("END_VOLUME")|| column_name.equalsIgnoreCase("NET_VOLUME")
								|| column_name.equalsIgnoreCase("TOTAL_VALUE")|| column_name.equalsIgnoreCase("AVERAGE")
								|| column_name.equalsIgnoreCase("LAST_INVENTORY")|| column_name.equalsIgnoreCase("AVERAGE_SALES")
								|| column_name.equalsIgnoreCase("SALES")|| column_name.equalsIgnoreCase("DELTA(Ltr)")
								|| column_name.equalsIgnoreCase("revalue_amount")|| column_name.equalsIgnoreCase("deposit_amount")
								|| column_name.equalsIgnoreCase("COMPARISION PERIOD 1 SALES(Ltr)")|| column_name.equalsIgnoreCase("COMPARISION PERIOD 2 - SALES(Ltr)")
								) {
							if (!val.equalsIgnoreCase("0")) {
								////System.out.println(val + " --- " + column_name);
								if (isNumeric(val)) {
									if (val.contains("."))
										val = String.format("%,.3f", Double.valueOf(val));
									else
										val = String.format("%,d", Integer.valueOf(val));
								}
							}
						}
						obj.put(column_name, val);
						break;
					}
				}
				json.add(obj);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return json;
	}
	
	public static boolean isNumeric(String strNum) {
		if (strNum == null) {
			return false;
		}
		try {
			double d = Double.parseDouble(strNum);
		} catch (NumberFormatException nfe) {
			return false;
		}
		return true;
	}
}





