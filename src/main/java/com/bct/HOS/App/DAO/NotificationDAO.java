package com.bct.HOS.App.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

import com.bct.HOS.App.utils.DBConnector;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class NotificationDAO {

	private Connection conn = null;
	private String schema = null;
	DBConnector dbc = null;
	public NotificationDAO() {
		dbc=new DBConnector();
		conn = dbc.getConnection();
		schema = dbc.getSchema();
	}

	/**
	 * 
	 * 
	 * @return
	 */
	public JSONArray getNotifications(String siteIDs,String notifyType,String limit) {
		JSONArray json = new JSONArray();
		try {
			String SQL = " SELECT NT.\"ID\",NT.\"SITE_ID\",NT.\"NOTIFICATION_ID\",NT.\"NOTIFICATION_TYPE\",NT.\"NOTIFICATION_DATE\",  " + 
					" NT.\"NOTIFICATION_TITLE\",NT.\"NOTIFICATION_MSG\",NT.\"BGCOLOUR\",  " + 
					" NT.\"PARAMATER1\",NT.\"PARAMATER2\",  " + 
					" NT.\"PARAMATER3\",NT.\"STATUS\",  " + 
					" NT.\"CREATED_BY\",NT.\"CREATED_DATE\",  " + 
					" NT.\"MODIFIED_BY\",NT.\"MODIFIED_DATE\", MNT.\"MESSAGE\" FROM  " + 
					" "+schema+".\"NOTIFICATIONS\" NT   " + 
					" INNER JOIN "+schema+".\"MS_NOTIFICATION_TYPES\" MNT  ON NT.\"NOTIFICATION_TYPE\" = MNT.\"ID\"    " + 
					" WHERE 1=1 AND NT.\"NOTIFICATION_DATE\"::date=NOW()::date ";

			if (siteIDs != null)
				SQL += " AND NT.\"SITE_ID\" IN (" + siteIDs + ") ";
			
			SQL += " AND MNT.\"MESSAGE\" IN ('" + notifyType + "') ";

			SQL += " ORDER BY NT.\"NOTIFICATION_DATE\",NT.\"NOTIFICATION_TYPE\" ";
			
			if(limit!=null)
				SQL += " limit "+limit;
			
			//System.out.println("SQL::" + SQL);
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(SQL);
			json=dbc.parseRS(rs);
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("ErrOR @ AlarmsDAO-getAlarms ::" + ex.getMessage());
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
	 * @param notifyType
	 * @param limit
	 * @param fromDate
	 * @param toDate
	 * @return
	 */
	public JSONArray getNotificationGroup(String siteIDs,String notifyType,boolean isGrouping,String limit,String fromDate,String toDate) {
		JSONArray json = new JSONArray();
		try {
			String SQL = " SELECT \"SITE_ID\",NT.\"NOTIFICATION_TYPE\",\"NOTIFICATION_DATE\"::DATE,NTT.\"TITLE\" AS \"NOTIFICATION\"," +
					" NTT.\"MESSAGE\" AS \"DESCRIPTION\" "; 
			
			if(!isGrouping)
				SQL +=	" ,\"PARAMATER1\", CASE WHEN MAX(\"PARAMATER2\") IS NULL THEN '' ELSE MAX(\"PARAMATER2\") END AS \"PARAMATER2\","
						+ "CASE WHEN MAX(\"PARAMATER3\") IS NULL THEN '' ELSE MAX(\"PARAMATER3\") END AS \"PARAMATER3\" ";
			
			//,\"PARAMATER4\",\"PARAMATER5\", \"PARAMATER6\",\"PARAMATER7\",\"PARAMATER8\"
			
				SQL +=" ,COUNT(*) \"TOTAL_NOTIFICATIONS_RECEIVED\" FROM "+schema+".\"NOTIFICATIONS\" NT  " + 
					" INNER JOIN "+schema+".\"MS_NOTIFICATION_TYPES\" NTT ON NTT.\"ID\"=NT.\"NOTIFICATION_TYPE\"  " + 
					" WHERE \"NOTIFICATION_DATE\"::DATE BETWEEN ('"+fromDate+"') AND ('"+toDate+"')  " + 
					" AND \"IS_ACT\" IS NULL ";

			if (siteIDs != null)
				SQL += " AND \"SITE_ID\" IN (" + siteIDs + ") ";
			
			
			if (notifyType != null)
				SQL += " AND NTT.\"TITLE\" = '" + notifyType + "' ";

			if(isGrouping) {
				SQL += " GROUP BY \"SITE_ID\",NT.\"NOTIFICATION_TYPE\",NTT.\"TITLE\",NTT.\"MESSAGE\",\"NOTIFICATION_DATE\"::DATE ";
			}else {
				SQL += " GROUP BY \"SITE_ID\",NT.\"NOTIFICATION_TYPE\",NTT.\"TITLE\",NTT.\"MESSAGE\",\"NOTIFICATION_DATE\"::DATE," +
						" \"PARAMATER1\" ";
				//,\"PARAMATER2\",\"PARAMATER3\",\"PARAMATER4\",\"PARAMATER5\",\"PARAMATER6\",\"PARAMATER7\",\"PARAMATER8\"
			}
			
			SQL += " ORDER BY \"NOTIFICATION_DATE\" DESC ";
			
			if(limit!=null)
				SQL += " limit "+limit;
			
			//System.out.println("SQL::" + SQL);
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(SQL);
			json=dbc.parseRS(rs);
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("Error @ NotificationDAO-getNotificationGroup ::" + ex.getMessage());
		} finally {
			try {
				dbc.closeConnection(conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return json;
	}
	
	
	public JSONArray updateNotificationGroup(String siteID,String notifyType,String date,
			String param1,String param2,String param3,String param4,String param5,
			String param6,String param7,String param8) {
		JSONObject jsonObj = new JSONObject();
		JSONArray json = new JSONArray();
		try {
			String SQL = " UPDATE "+schema+".\"NOTIFICATIONS\" SET \"IS_ACT\" = 1 ,\"ACT_BY\" = 'HOS', \"ACT_ON\" = NOW()::TIMESTAMP  " + 
					" WHERE \"ID\" IN (  " + 
					"	SELECT NT.\"ID\"	FROM "+schema+".\"NOTIFICATIONS\" NT  " + 
					"	INNER JOIN "+schema+".\"MS_NOTIFICATION_TYPES\" NTT ON NTT.\"ID\"=NT.\"NOTIFICATION_TYPE\"  " + 
					"	WHERE \"NOTIFICATION_DATE\"::DATE ='"+date+"'  " +
					" 	AND \"SITE_ID\" = "+siteID+" " +
					"	AND NT.\"NOTIFICATION_TYPE\" =   " + notifyType;
			
			if(param1!=null)
				if(param1.length()>1)
					SQL +="	AND \"PARAMATER1\" = '"+param1+"'  ";
			
			if(param2!=null)
				if(param2.length()>1)
					SQL+="	AND \"PARAMATER2\" = '"+param2+"'  ";
			
					//"	AND \"PARAMATER3\" = '"+param3+"'  " + 
					//"	AND \"PARAMATER4\" = '"+param4+"'  " + 
					//"	AND \"PARAMATER5\" = '"+param5+"'  " + 
					//"	AND \"PARAMATER6\" = '"+param6+"'  " + 
					//"	AND \"PARAMATER7\" = '"+param7+"'  " + 
					//"	AND \"PARAMATER8\" = '"+param8+"'  " + 
					SQL+="	AND \"IS_ACT\" IS NULL  " + 
					"	GROUP BY NT.\"ID\"  " + 
					"	ORDER BY \"NOTIFICATION_DATE\" DESC  " + 
					" )";

			//System.out.println("SQL::" + SQL);
			Statement stmt = conn.createStatement();
			stmt.execute(SQL);
			jsonObj.put("Result", "Sucess");
			json.add(jsonObj);
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("Error @ NotificationDAO-updateNotificationGroup ::" + ex.getMessage());
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
	 * @param javaDate
	 * @return
	 */
	public static String convertToJsonDateTime(String javaDate) {
		String result = null;
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd HH:mm:ss");
		Date currentDate = null;
		try {
			if (javaDate != null) {
				dateFormat.setTimeZone(TimeZone.getTimeZone("Etc/UTC"));
				currentDate = dateFormat.parse(javaDate);
				long time = currentDate.getTime();
				result = String.valueOf(time);
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
	
	public JSONArray getNotificationCount(String siteIDs) {
		JSONArray json = new JSONArray();
		try {
			String SQL = "SELECT MNT.\"MESSAGE\",  " + 
					"NR.\"NOTIFICATION_DATE\"::date, count(*) as \"NOTIFICOUNT\"   " + 
					"FROM "+schema+".\"NOTIFICATIONS\" NR  " + 
					"INNER JOIN "+schema+".\"MS_NOTIFICATION_TYPES\" MNT  " + 
					"ON NR.\"NOTIFICATION_TYPE\" = MNT.\"ID\"  " + 
					"WHERE NR.\"NOTIFICATION_DATE\"::date=NOW()::date";
			
			if(siteIDs!=null)
				SQL += " AND NR.\"SITE_ID\" IN ("+siteIDs+") ";
			
				SQL += "GROUP BY MNT.\"MESSAGE\",NR.\"NOTIFICATION_DATE\"::date";
			//System.out.println(SQL);
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(SQL);
			json = dbc.parseRS(rs);

		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("ErrOR @ NotificationDAO-getROList ::" + ex.getMessage());
		} finally {
			try {
				dbc.closeConnection(conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return json;
	}
	
	
	public HashMap getDayNotificationCount(String siteIDs) {
		HashMap map = new HashMap();
		try {
			String SQL = "SELECT NR.\"SITE_ID\", " + 
					"count(*) as \"NOTIFICOUNT\" " + 
					"FROM "+schema+".\"NOTIFICATIONS\" NR " + 
					"WHERE NR.\"NOTIFICATION_DATE\"::date=NOW()::date " +
					" AND \"SITE_ID\" IN ("+siteIDs+") " +
					" GROUP BY NR.\"SITE_ID\" ";
			
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(SQL);
			while(rs.next()) {
				map.put(rs.getString("SITE_ID"), rs.getString("NOTIFICOUNT"));
			}

		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("ErrOR @ NotificationDAO-getDayNotificationCount ::" + ex.getMessage());
		} finally {
			try {
				dbc.closeConnection(conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return map;
	}
	


}
