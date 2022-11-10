package com.bct.HOS.App.DAO;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.sql.Connection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.postgresql.largeobject.LargeObject;
import org.postgresql.largeobject.LargeObjectManager;

import com.bct.HOS.App.BO.BOSBO;
import com.bct.HOS.App.utils.DBConnector;
import com.google.gson.Gson;

import net.sf.json.JSONArray;

public class BOSDAO {

	private Connection conn = null;
	private DBConnector dbc = null;
	private String schema = null;

	public BOSDAO() {
		dbc = new DBConnector();
		conn = dbc.getConnection();
		schema = dbc.getSchema();
	}

	/**
	 * 
	 * @param siteIDs
	 * @param date
	 * @return
	 */
	public JSONArray getDailyBookDetails(String siteIDs, String date,String fileLocation,String fileURL) {
		JSONArray json = new JSONArray();
		Gson gson=new Gson();
		try {
			LargeObjectManager lobj = ((org.postgresql.PGConnection)conn).getLargeObjectAPI();

			
			String SQL = " SELECT DB.\"SITE_ID\",\"SITE_NAME\",\"ENTRY_DATE\",\"CURRENCY_CODE\",\"DEPOSIT\",\"EXPENSES\" , \"IMG_OBJ\" , \"FILE_NAME\" "
					+ " FROM " + schema + ".\"SITE_DAILYBOOK\" DB " + " INNER JOIN " + schema
					+ ".\"MS_SITE\" MS ON MS.\"SITE_ID\" = DB.\"SITE_ID\" " + " WHERE 1 = 1 AND DB.\"SITE_ID\" IN ("
					+ siteIDs + ")" + " AND \"ENTRY_DATE\" = '" + date + "' ";

			////System.out.println("getDailyBookDetails=" + SQL);
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(SQL);
			//json = dbc.parseRS(rs);
			BOSBO objBO=new BOSBO();
			String fileName = null;
			String siteId = null;
			String siteName = null;
			String currenctCode = null;
			String deposit = null;
			String expenses = null;
			String entryDate = null;
			while(rs.next()) {
				siteId = rs.getString("SITE_ID");
				siteName = rs.getString("SITE_NAME");
				entryDate = rs.getString("ENTRY_DATE");
				currenctCode = rs.getString("CURRENCY_CODE");
				deposit = rs.getString("DEPOSIT");
				expenses = rs.getString("EXPENSES");
				fileName = rs.getString("FILE_NAME");
				
				objBO.setSiteId(siteId);
				objBO.setSiteName(siteName);
				objBO.setEntryDate(entryDate);
				objBO.setCurrenctCode(currenctCode);
				objBO.setDeposit(deposit);
				objBO.setExpenses(expenses);

                FileOutputStream os = new FileOutputStream(new File(fileLocation.concat(fileName)));
                os.write(rs.getBytes("IMG_OBJ"));
                os.flush();
                os.close();

			    objBO.setFileURL(fileURL.concat(fileName));
			    objBO.setFileName(fileName);

			    
			    json.add(gson.toJson(objBO));

			}
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("ErrOR @ BOSDAO-getDailyBookDetails ::" + ex.getMessage());
		} finally {
			try {
				dbc.closeConnection(conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return json;
	}

	/**
	 * 
	 * @param siteIDs
	 * @param date
	 * @return
	 */
	public boolean entryDailyBookDetails(String siteIDs, String date, String currencyCode, double deposit,
			double expenses, String fileName) {
		boolean result = false;
		String SQL = null;
		try {
			if (fileName != null) {
				SQL = " INSERT INTO " + schema + ".\"SITE_DAILYBOOK\" "
						+ " (\"SITE_ID\",\"ENTRY_DATE\",\"CURRENCY_CODE\",\"DEPOSIT\",\"EXPENSES\",\"IMG_OBJ\",\"FILE_NAME\" ) "
						+ " VALUES (?,?,?,?,?,?,?)";

				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
				Date parsed = format.parse(date);
				java.sql.Date sql = new java.sql.Date(parsed.getTime());

				File file = new File(fileName);
				FileInputStream fis = new FileInputStream(file);

				////System.out.println("entryDailyBookDetails=" + SQL);
				PreparedStatement statement = conn.prepareStatement(SQL);
				statement.setString(1, siteIDs);
				statement.setDate(2, sql);
				statement.setString(3, currencyCode);
				statement.setDouble(4, deposit);
				statement.setDouble(5, expenses);
				statement.setBinaryStream(6, fis, (int) file.length());
				statement.setString(7, file.getName());

				statement.addBatch();

				statement.executeBatch();
				result = true;
				////System.out.println("result>>"+result);
				if(result)
					file.delete();
				
			} else {
				SQL = " INSERT INTO " + schema + ".\"SITE_DAILYBOOK\" "
						+ " (\"SITE_ID\",\"ENTRY_DATE\",\"CURRENCY_CODE\",\"DEPOSIT\",\"EXPENSES\" ) "
						+ " VALUES (?,?,?,?,?)";

				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
				Date parsed = format.parse(date);
				java.sql.Date sql = new java.sql.Date(parsed.getTime());

				//System.out.println("entryDailyBookDetails=" + SQL);
				PreparedStatement statement = conn.prepareStatement(SQL);
				statement.setString(1, siteIDs);
				statement.setDate(2, sql);
				statement.setString(3, currencyCode);
				statement.setDouble(4, deposit);
				statement.setDouble(5, expenses);

				statement.addBatch();

				statement.executeBatch();
				result = true;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("ErrOR @ BOSDAO-entryDailyBookDetails ::" + ex.getMessage());
		} finally {
			try {
				dbc.closeConnection(conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	/**
	 * 
	 * @param siteIDs
	 * @param date
	 * @return
	 */
	public boolean checkEntry(String siteId, String date) {
		boolean result = false;
		try {
			String SQL = " SELECT COUNT(*) AS \"COUNT\" " + " FROM " + schema
					+ ".\"SITE_DAILYBOOK\" WHERE 1 = 1 AND \"SITE_ID\" IN ('" + siteId + "')"
					+ " AND \"ENTRY_DATE\" = '" + date + "' ";

			////System.out.println("checkEntry=" + SQL);
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(SQL);
			int count = 0;
			while (rs.next()) {
				count = rs.getInt("COUNT");
			}

			if (count > 0)
				result = true;
			else
				result = false;

		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("ErrOR @ BOSDAO-checkEntry ::" + ex.getMessage());
		} finally {
			try {
				dbc.closeConnection(conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return result;
	}

}
