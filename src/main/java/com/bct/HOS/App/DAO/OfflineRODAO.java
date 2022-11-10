package com.bct.HOS.App.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import com.bct.HOS.App.BO.FuelSalesBO;
import com.bct.HOS.App.BO.FuelSalesHeaderBO;
import com.bct.HOS.App.BO.OfflineROInventoryBO;
import com.bct.HOS.App.BO.TankProductInventoryBO;
import com.bct.HOS.App.utils.DBConnector;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.sf.json.JSONArray;

public class OfflineRODAO {

	private Connection conn = null;
	private DBConnector dbc = null;
	private String schema = null;

	public OfflineRODAO() {
		dbc = new DBConnector();
		conn = dbc.getConnection();
		schema = dbc.getSchema();
	}
	
	public JsonObject storeInventoryDetails(String site_id, OfflineROInventoryBO details) {
	
		String SQL = "INSERT INTO " + schema + ".\"INVENTORY\" (" + 
				" \"SITE_ID\",\"INVENTORY_ID\",\"INVENTORY_DATE\",\"TANK_NO\",\"VOLUME\",\"CREATED_BY\",\"MODIFIED_BY\") " +
				" VALUES (?, ?, ?, ?, ?, ?, ?)";
		
		try {
			System.out.println("time"+details.getTransaction_time());
            LocalDate datePart = LocalDate.parse(details.getTransaction_date());
		    LocalTime timePart = LocalTime.parse(details.getTransaction_time(),DateTimeFormatter.ofPattern("HH:mm"));
		    LocalDateTime dt = LocalDateTime.of(datePart, timePart);
		    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		    Timestamp ts = Timestamp.valueOf(dt.format(dtf));
		           
            String tSQL = " SELECT \"INVENTORY_ID\" FROM "+schema+ ".\"INVENTORY\"  WHERE \"SITE_ID\" ='" + site_id + "'"
					+ " ORDER BY \"ID\" DESC limit 1";
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(tSQL);
			int inventoryId = 1;
			if(rs.next())
				inventoryId = Integer.parseInt(rs.getString(1)) + 1;
			details.setTransaction_no(String.valueOf(inventoryId));
			
			PreparedStatement statement = conn.prepareStatement(SQL);
            if(details.getInventorydetails().size() > 0 ) {
							
				 for(TankProductInventoryBO tankDetails : details.getInventorydetails() ) {
					
					 statement.setString(1, details.getSite_id());
					 statement.setString(2, details.getTransaction_no());
					 statement.setTimestamp(3, ts);
					 statement.setInt(4, Integer.parseInt(tankDetails.getTank()));
					 statement.setDouble(5, tankDetails.getVolume());
					 statement.setString(6, details.getSite_id());
					 statement.setString(7, details.getSite_id());
					 statement.addBatch();
				 }
				 statement.executeBatch();
			}
			
				
			String json = new Gson().toJson(details);
			System.out.println(json);
			return new JsonParser().parse(json).getAsJsonObject();
				
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("ErrOR @ Inventory-set ::" + ex.getMessage());
			
		} finally {
			try {
				dbc.closeConnection(conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	public JSONArray getInventorySummary(String siteID) {
		JSONArray json = null;

		try {
			String SQL = " SELECT inv.\"SITE_ID\",\"INVENTORY_ID\",\"INVENTORY_DATE\",inv.\"TANK_NO\",\"PRODUCT_NAME\",\"VOLUME\"::numeric(10,3) as \"VOLUME\" "
					+" FROM  " + schema + ".\"INVENTORY\" inv JOIN " + schema +".\"MS_TANK\"  tank " 
					+"ON inv.\"TANK_NO\" = tank.\"TANK_NO\" AND inv.\"SITE_ID\"= tank.\"SITE_ID\" "
					+" JOIN " + schema +".\"MS_PRODUCTS\" pro ON tank.\"PRODUCT_NO\" = pro.\"PRODUCT_NO\" AND tank.\"SITE_ID\"= pro.\"SITE_ID\" "
					+" WHERE inv.\"SITE_ID\" = '" + siteID + "' AND inv.\"INVENTORY_DATE\" > (CURRENT_DATE - INTERVAL '30 days') "
					+"ORDER BY \"ID\" DESC";
			
			Statement stmt = conn.createStatement();
			//System.out.println(SQL);
			ResultSet rs = stmt.executeQuery(SQL);
			json = dbc.parseRS(rs);
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("ErrOR @ OfflineRODAO ::" + ex.getMessage());
		} finally {
			try {
				dbc.closeConnection(conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return json;
	}
	
	public JSONArray getTanklistBySiteID(String siteID) {
		JSONArray json = null;

		try {
			String SQL = "SELECT \"TANK_NO\",tank.\"PRODUCT_NO\",\"PRODUCT_NAME\" FROM " + schema + ".\"MS_TANK\" tank" + 
					" JOIN " + schema + ".\"MS_PRODUCTS\" pro " + 
					" ON tank.\"PRODUCT_NO\" = pro.\"PRODUCT_NO\" and tank.\"SITE_ID\" = pro.\"SITE_ID\" " + 
					" WHERE tank.\"SITE_ID\" = '"+ siteID +"' ORDER BY \"TANK_NO\" ASC";
			
			Statement stmt = conn.createStatement();
			//System.out.println(SQL);
			ResultSet rs = stmt.executeQuery(SQL);
			json = dbc.parseRS(rs);
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("ErrOR @ OfflineRODAO ::" + ex.getMessage());
		} finally {
			try {
				dbc.closeConnection(conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return json;
	}

	public JSONArray getProductlistBySiteID(String siteID) {
		JSONArray json = null;

		try {
			String SQL = "SELECT \"PRODUCT_NO\",\"PRODUCT_NAME\" FROM "+ schema +".\"MS_PRODUCTS\" "
					+ " WHERE \"SITE_ID\" = '" + siteID +"'";
			
			Statement stmt = conn.createStatement();
			//System.out.println(SQL);
			ResultSet rs = stmt.executeQuery(SQL);
			json = dbc.parseRS(rs);
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("ErrOR @ OfflineRODAO ::" + ex.getMessage());
		} finally {
			try {
				dbc.closeConnection(conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return json;
	}
	
	public JSONArray getNozzleListBySiteID(String siteID, int productNo) {
		JSONArray json = null;

		try {
			String SQL = "SELECT \"DU_NO\",\"PUMP_NO\",\"TANK_NO\",\"NOZZLE_NO\",\"PRODUCT_NO\" FROM " + schema 
					+ ".\"MS_NOZZLE_LIST\" WHERE \"SITE_ID\" = '" + siteID + "' "
					+" AND \"PRODUCT_NO\" =" +productNo +" ORDER BY \"NOZZLE_NO\" ASC";
			
			Statement stmt = conn.createStatement();
			System.out.println(SQL);
			ResultSet rs = stmt.executeQuery(SQL);
			json = dbc.parseRS(rs);
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("ErrOR @ OfflineRODAO ::" + ex.getMessage());
		} finally {
			try {
				dbc.closeConnection(conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return json;
	}

	public JSONArray getFuelSalesSummary(String siteID) {
		JSONArray json = null;

		try {
			String SQL = "  SELECT \"TRANSACTION_DATE\", t.\"PRODUCT_NO\", p.\"PRODUCT_NAME\", " + 
					"\"UNIT_PRICE\"::numeric(10,3) as \"UNIT_PRICE\", SUM(\"VOLUME\")::numeric(10,3) as totalvolume, SUM(\"AMOUNT\")::numeric(10,3) as totalprice "+ 
					"FROM "+ schema +".\"TRANSACTIONS\"  t JOIN " + schema + ".\"MS_PRODUCTS\" p " + 
					"ON p.\"PRODUCT_NO\" = t.\"PRODUCT_NO\" AND p.\"SITE_ID\" = t.\"SITE_ID\" " +
					"WHERE t.\"SITE_ID\" = '" + siteID +"' " + 
					"AND \"TRANSACTION_DATE\" > (CURRENT_DATE - INTERVAL '30 days') " +
					"GROUP BY \"TRANSACTION_DATE\", t.\"PRODUCT_NO\",p.\"PRODUCT_NAME\",\"UNIT_PRICE\" "+
					"ORDER BY \"TRANSACTION_DATE\" DESC " ;
			
			Statement stmt = conn.createStatement();
			System.out.println(SQL);
			ResultSet rs = stmt.executeQuery(SQL);
			json = dbc.parseRS(rs);
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("ErrOR @ OfflineRODAO ::" + ex.getMessage());
		} finally {
			try {
				dbc.closeConnection(conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return json;
	}
	
	public JsonObject storeFuelSales(String site_id, FuelSalesHeaderBO details) {
		
		String SQL = "INSERT INTO " + schema + ".\"TRANSACTIONS\" (\"SITE_ID\"," + 
				"\"TRANSACTION_ID\", \"TRANSACTION_DATE\", \"TANK_NO\", \"PUMP_NO\", \"NOZZLE_NO\", \"PRODUCT_NO\", " +
				"\"UNIT_PRICE\", \"VOLUME\", \"AMOUNT\", \"END_TOTALIZER\", \"START_TOTALIZER\" ,\"CREATED_BY\",\"MODIFIED_BY\""
				+ ",\"TRANSACTION_TYPE\", \"TRANSACTION_SUB_TYPE\") " +
				" VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		
		try {
			
			Timestamp ts = Timestamp.valueOf(details.getTransaction_date() != null ? details.getTransaction_date() : String.valueOf(java.time.LocalDateTime.now()));
           	PreparedStatement statement = conn.prepareStatement(SQL);
                   	
           	
			if(details.getSales().size() > 0 ) {
				String tSQL = " SELECT \"TRANSACTION_ID\" FROM "+schema+ ".\"TRANSACTIONS\"  WHERE \"SITE_ID\" ='" + site_id + "'"
						+ " ORDER BY \"ID\" DESC limit 1";
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(tSQL);
				int transactionId = 1;
				if(rs.next())
					transactionId = Integer.parseInt(rs.getString(1)) + 1;
				
				 for(FuelSalesBO sales : details.getSales() ) {
					 sales.setTransaction_no(String.valueOf(transactionId ++));	
			         System.out.println("NO"+ sales.getTransaction_no());
					 statement.setString(1, details.getSite_id());
					 statement.setString(2, sales.getTransaction_no());
					 statement.setTimestamp(3, ts);
					 statement.setInt(4, sales.getTank_no());
					 statement.setInt(5, sales.getPump_no());
					 statement.setInt(6, sales.getNozzle_no());
					 statement.setInt(7, details.getProduct_no());
					 statement.setDouble(8, details.getUnit_price());
					 statement.setDouble(9, sales.getVolume());
					 statement.setDouble(10, sales.getVolume()*details.getUnit_price());
					 statement.setDouble(12, sales.getOpening_reading());
					 statement.setDouble(11, sales.getClosing_reading());
					 statement.setString(13, details.getSite_id());
					 statement.setString(14, details.getSite_id());
					 statement.setInt(15, 1);
					 statement.setInt(16, 0);
					 statement.addBatch();
				 }
				 statement.executeBatch();
			}
			
			
			
			String json = new Gson().toJson(details);
			System.out.println(json);
			return new JsonParser().parse(json).getAsJsonObject();
				
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("ErrOR @ Fuel sales-set ::" + ex.getMessage());
			
		} finally {
			try {
				dbc.closeConnection(conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	public JSONArray getFuelSalesSummary(String siteID, String transaction_date) {
		JSONArray json = null;

		try {
			String SQL = " SELECT t.\"SITE_ID\", \"TRANSACTION_DATE\", t.\"TANK_NO\", t.\"PUMP_NO\", t.\"NOZZLE_NO\",t.\"PRODUCT_NO\", "
					+ "\"PRODUCT_NAME\",\"UNIT_PRICE\"::numeric(10,3) as \"UNIT_PRICE\", \"VOLUME\"::numeric(10,3) as \"VOLUME\", \"AMOUNT\"::numeric(10,3) as \"AMOUNT\", "
					+ " \"END_TOTALIZER\",\"START_TOTALIZER\", nl.\"DU_NO\" "
					+ "FROM " + schema + ".\"TRANSACTIONS\" t JOIN " + schema + ".\"MS_PRODUCTS\" p "
					+ "ON p.\"PRODUCT_NO\" = t.\"PRODUCT_NO\" AND p.\"SITE_ID\" = t.\"SITE_ID\" " 
					+ "JOIN "+ schema +".\"MS_NOZZLE_LIST\" nl "
					+ "ON nl.\"TANK_NO\" = t.\"TANK_NO\" AND nl.\"SITE_ID\" = t.\"SITE_ID\" "
					+ "WHERE t.\"SITE_ID\" = '" + siteID + "' AND \"TRANSACTION_DATE\" = '" + transaction_date + "' "
					+ "group by t.\"SITE_ID\", \"TRANSACTION_DATE\", t.\"TANK_NO\", t.\"PUMP_NO\", t.\"NOZZLE_NO\", "
					+ "t.\"PRODUCT_NO\",\"PRODUCT_NAME\",\"UNIT_PRICE\", \"VOLUME\", \"AMOUNT\", "
					+ "\"END_TOTALIZER\",\"START_TOTALIZER\", nl.\"DU_NO\" ";
					
			
			Statement stmt = conn.createStatement();
			System.out.println(SQL);
			ResultSet rs = stmt.executeQuery(SQL);
			json = dbc.parseRS(rs);
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("ErrOR @ OfflineRODAO ::" + ex.getMessage());
		} finally {
			try {
				dbc.closeConnection(conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return json;
	}
}
