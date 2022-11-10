package com.bct.HOS.App.utils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimerTask;

public class InventoryReportTimer extends TimerTask {
	
		 @Override
	    public void run() {
	        System.out.println("Inventory Report Timer task started at:"+new Date());
	        Connection con = null;
	        Statement st = null;
	        DBConnector dbc = null;
	        ResultSet rs = null;
	        try {
	        	dbc = new DBConnector();
	        	con = dbc.getConnection();
	    		String schema = dbc.getSchema();
		        String pattern = "yyyy-MM-dd";
		        Calendar cal = Calendar.getInstance();
		        cal.setTime(new Date());
		        cal.add(Calendar.DATE, -1);
		        Date dateBefore1Day = cal.getTime();
		        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		        String dateStr = simpleDateFormat.format(dateBefore1Day);
		        String sqlQuery = "SELECT "+schema+".update_cumm_sales('"+dateStr+"')";
				st = con.createStatement();
				rs = st.executeQuery(sqlQuery);
				System.out.println("Inventory Report Query executed successfully: "+sqlQuery);
	        } catch(Exception ex) {
	        	ex.printStackTrace();
	        	System.out.println("Exception occurred while executing Inventory Report Query :"+ex.getMessage());
	        } finally {
				try {
					if(rs != null) {
						rs.close();
					}
					if(st != null) {
						st.close();
					}
					dbc.closeConnection(con);
				} catch(Exception ex) {
					
				}
			}
	        System.out.println("Inventory Report Timer task Ended at:"+new Date());
	    }
}
