package com.bct.HOS.App.DAO;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import com.bct.HOS.App.BO.NCBO;
import com.bct.HOS.App.utils.DBConnector;

import net.sf.json.JSONArray;

public class NCDAO {

	private Connection conn = null;
	private DBConnector dbc = null;
	private String schema = null;

	public NCDAO() {
		dbc = new DBConnector();
		conn = dbc.getConnection();
		schema = dbc.getSchema();
	}

	/**
	 * 
	 * @return
	 */
	public JSONArray getNotificationAlerts(String ncName, String status, String notificationType) {
		JSONArray json = new JSONArray();
		try {
			String SQL = "  SELECT \"NC_ID\",\"NC_NAME\",\"DESCRIPTION\",\"YML_DATA\",\"STATUS\", "
					+ " \"NOTIFICATION_TYPE\",\"TO_TYPE\",\"TO\",\"CC_TYPE\",\"CC\",\"SUBJECT\",\"TEMPLATE\" "
					+ " FROM  " + schema + ".\"NOTIFICATION_ALERT\" NA " + " INNER JOIN " + schema
					+ ".\"NOTIFICATION_CONFIG\" NC ON NC.\"ID\"=NA.\"NC_ID\" ";

			if (ncName != null)
				SQL += " \"NC_NAME\" = '" + ncName + "'";

			if (status != null)
				SQL += " \"STATUS\" = '" + status + "'";

			if (notificationType != null)
				SQL += " \"NOTIFICATION_TYPE\" = '" + notificationType + "' ";

			SQL += " ORDER BY \"NC_ID\" ";

			//System.out.println(SQL);

			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(SQL);
			json = dbc.parseRS(rs);
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("ErrOR @ NCDAO-getNotificationAlerts ::" + ex.getMessage());
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
	 * @return
	 */
	public boolean setNotificationAlerts(NCBO ncBO) {
		boolean result = false;
		try {
			String SQL = null;
			ResultSet rs = null;
			Statement stmt = null;
			String ncID = null;

			String ncName = ncBO.getNcName();
			String description = ncBO.getDescription();
			String ymlData = ncBO.getYmlData();
			String createdBy = ncBO.getCreatedBy();
			String notificationType = ncBO.getNotificationType();
			String toType = ncBO.getToType();
			String to = ncBO.getTo();
			String ccType = ncBO.getCcType();
			String cc = ncBO.getCc();
			String subject = ncBO.getSubject();
			String template = ncBO.getTemplate();

			SQL = " SELECT \"NC_ID\" FROM  " + schema + ".\"NOTIFICATION_ALERT\" " + " WHERE 1 = 1 AND  \"NC_NAME\" = '"
					+ ncName + "' ";
			stmt = conn.createStatement();
			rs = stmt.executeQuery(SQL);
			while (rs.next()) {
				ncID = rs.getString("NC_ID");
			}

			if (ncID == null) { // Create new configuration

				stmt = null;
				SQL = " INSERT INTO " + schema + ".\"NOTIFICATION_ALERT\" "
						+ " (\"NC_NAME\",\"DESCRIPTION\",\"YML_DATA\",\"STATUS\",\"CREATED_BY\",\"CREATED_DATE\") "
						+ " VALUES('" + ncName + "','" + description + "','" + ymlData + "','ACTIVE','" + createdBy
						+ "',current_timestamp)";
				//System.out.println(SQL);
				stmt = conn.createStatement();
				boolean inserted = stmt.execute(SQL);
				if (inserted) {
					stmt = null;
					SQL = " SELECT \"NC_ID\" FROM  " + schema + ".\"NOTIFICATION_ALERT\" "
							+ " WHERE 1 = 1 AND  \"NC_NAME\" = '" + ncName + "' ";
					stmt = conn.createStatement();
					rs = stmt.executeQuery(SQL);
					while (rs.next()) {
						ncID = rs.getString("NC_ID");
					}
					if (ncID != null) {
						stmt = null;
						SQL = " INSERT INTO " + schema + ".\"NOTIFICATION_CONFIG\" "
								+ " (\"ID\",\"NOTIFICATION_TYPE\",\"TO_TYPE\",\"TO\",\"CC_TYPE\",\"CC\","
								+ " \"SUBJECT\",\"TEMPLATE\") " + " VALUES('" + ncID + "','" + notificationType + "','"
								+ toType + "','" + to + "','" + ccType + "','" + cc + "','" + subject + "','" + template
								+ "') ";
						stmt = conn.createStatement();
						inserted = stmt.execute(SQL);
						if (inserted)
							result = true;
					}
				}

			} else { // Modify existing configuration
				result = true;
			}

		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("ErrOR @ NCDAO-setNotificationAlerts ::" + ex.getMessage());
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
