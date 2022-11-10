package com.bct.HOS.App.DAO;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;

import com.bct.HOS.App.utils.DBConnector;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class AlarmsDAO {

	private Connection conn = null;
	private String schema = null;
	DBConnector dbc= null;
	public AlarmsDAO() {
		dbc=new DBConnector();
		conn = dbc.getConnection();
		schema =dbc.getSchema(); 
	}

	// select * from "CLIENT"."ALARM_LIST"; --
	// ID,SITE_ID,ALARM_ID,DATE,DESCRIPTION,ISCLEAR,CLEAR_TIME,ACK_BY,ACT_TIME,IS_ACK,PRIORITY,UID,DEVICE_ID,DEVICE_TYPE,CREATED_BY,CREATED_DATE,MODIFIED_BY,MODIFIED_DATE
	// select * from "CLIENT"."MS_NOTIFICATION_TYPES"; --
	// ID,TITLE,MESSAGE,BGCOLOUR,TYPE_TEST,CREATED_BY,CREATED_DATE,MODIFIED_BY,MODIFIED_DATE
	// select * from "CLIENT"."NOTIFICATIONS"; --
	// ID,SITE_ID,NOTIFICATION_ID,NOTIFICATION_TYPE,NOTIFICATION_DATE,NOTIFICATION_TITLE,NOTIFICATION_MSG,BGCOLOUR,PARAMATER1,PARAMATER2,PARAMATER3,PARAMATER4,PARAMATER5,PARAMATER6,PARAMATER7,PARAMATER8,STATUS,CREATED_BY,CREATED_DATE,MODIFIED_BY,MODIFIED_DATE
	// SELECT SITE_ID FROM "CLIENT"."USER_RO_MAPPING" WHERE 1=1 AND SITE_ID IN
	// ('10101055_DISABLED','10101013'); -- USER_ID,ROLE_ID,SITE_ID

	// select * from "CLIENT"."NOTIFICATIONS" INNER JOIN "CLIENT"."USER_RO_MAPPING"
	// ON "NOTIFICATIONS"."SITE_ID" = "USER_RO_MAPPING".site_id WHERE 1=1 AND
	// "NOTIFICATIONS"."SITE_ID" IN ('10101055_DISABLED','10101013');
	// select * from "CLIENT"."ALARM_LIST" INNER JOIN "CLIENT"."USER_RO_MAPPING" ON
	// "ALARM_LIST"."SITE_ID" = "USER_RO_MAPPING".site_id where "ISCLEAR" = 0 and
	// "ACK_TIME" is null AND "NOTIFICATIONS"."SITE_ID" IN
	// ('10101055_DISABLED','10101013') order by "PRIORITY";

	/**
	 * 
	 * 
	 * @return
	 */
	public JSONArray getAlarms(String siteIDs,String limit, String fromDate, String toDate,
			String country,String state,String region,String district,
			String city, String pump,String tank,String nozzle) {
		JSONArray json = new JSONArray();
		try {
			String SQL = " SELECT ROW_NUMBER () OVER (ORDER BY ALM.\"SITE_ID\") AS \"SNO\" , "
					+ " ALM.\"SITE_ID\",MST.\"SITE_NAME\",ALM.\"DATE\",ALM.\"DESCRIPTION\","
					+ " ALM.\"CLEAR_TIME\",ALM.\"ACK_BY\",ALM.\"ACK_TIME\", "  
					+ " ALM.\"PRIORITY\",ALM.\"DEVICE_ID\", ALM.\"DEVICE_TYPE\" AS \"DEVICE\" "  
					+ " FROM "+schema+".\"ALARM_LIST\" ALM   "
					+ " JOIN "+schema+".\"MS_SITE\" MST ON MST.\"SITE_ID\" = ALM.\"SITE_ID\" "
					+ " WHERE 1 = 1 AND \"ISCLEAR\" = 0  AND \"IS_ACK\" = 0 ";

			if (siteIDs != null)
				SQL += " AND ALM.\"SITE_ID\" IN (" + siteIDs + ") ";

			
			if (fromDate != null && toDate != null) {
				SQL += " AND ALM.\"DATE\"::timestamp::date BETWEEN ('" + fromDate + "') AND ('" + toDate
						+ "')";
			}
			
			if(country!=null) {
				SQL += " AND MST.\"COUNTRY\" = '"+country+"' ";
			}
			
			if(state!=null) {
				SQL += " AND MST.\"STATE\" = '"+state+"' ";
			}
			
			if(region!=null) {
				SQL += " AND MST.\"REGION\" = '"+region+"' ";
			}
			
			if(district!=null) {
				SQL += " AND MST.\"DISTRICT\" = '"+district+"' ";
			}


			if(city!=null) {
				SQL += " AND MST.\"CITY\" = '"+city+"' ";
			}

						
			if(pump!=null) {
				SQL += " AND TRN.\"PUMP_NO\" = "+pump;
			}
			
			if(tank!=null) {
				SQL += " AND TRN.\"TANK_NO\" = "+tank;
			}
			
			if(nozzle!=null) {
				SQL += " AND TRN.\"NOZZLE_NO\" = "+nozzle;
			}
			
			SQL += " order by \"PRIORITY\",ALM.\"DATE\" DESC ";
			
			if(limit!=null)
				SQL += " limit "+limit;

			
			//System.out.println("getAlarms >>> " + SQL);
			
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
	 * @param javaDate
	 * @return
	 */
	public static String convertToJsonDateTime(String javaDate)
	{
		String result = null;
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd HH:mm:ss");
		Date currentDate = null;
		try {
			if(javaDate!=null) {
				dateFormat.setTimeZone(TimeZone.getTimeZone("Etc/UTC"));
				currentDate = dateFormat.parse(javaDate);
				long time = currentDate.getTime();
				result =  String.valueOf(time);
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
	
	
	public HashMap getDayAlarmCount(String siteIDs) {
		HashMap map = new HashMap();
		try {
			String SQL = "SELECT NR.\"SITE_ID\", " + 
					"count(*) as \"ALARMCOUNT\" " + 
					"FROM "+schema+".\"ALARM_LIST\" NR " + 
					"WHERE NR.\"DATE\"::date=NOW()::date " +
					" AND \"SITE_ID\" IN ("+siteIDs+") " +
					" AND \"ISCLEAR\" = 0  AND \"IS_ACK\" = 0 " +
					"GROUP BY NR.\"SITE_ID\" ";
			
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(SQL);
			while(rs.next()) {
				map.put(rs.getString("SITE_ID"), rs.getString("ALARMCOUNT"));
			}

		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("ErrOR @ AlarmDAO-getDayAlarmCount ::" + ex.getMessage());
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
