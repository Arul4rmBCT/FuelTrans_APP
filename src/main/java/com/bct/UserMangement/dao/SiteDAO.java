package com.bct.UserMangement.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.bct.UserMangement.bo.Role;
import com.bct.UserMangement.bo.Site;
import com.bct.UserMangement.utils.DBConnector;

public class SiteDAO {
	
	private Connection conn = null;
	private DBConnector dbc = null;
	private String schema = null;
	
	public SiteDAO() {
		dbc = new DBConnector();
		conn = dbc.getConnection();
		schema = dbc.getSchema();
	}
	
	public String createRole(Site site) {
	
		try {
			if (conn == null || conn.isClosed()) 
				conn = dbc.getConnection();
			
			String query = " INSERT INTO "+ schema +".\"MS_SITE\"(\r\n" + 
					"					\"SITE_ID\", \"SITE_NAME\", \"SITE_TYPE\", \"CLIENT_NAME\", \r\n" + 
					"					\"DEALER_NAME\", \"ADDRESS1\", \"ADDRESS2\", \"ADDRESS3\", \r\n" + 
					"					\"CITY\", \"DISTRICT\", \"STATE\", \"REGION\", \r\n" + 
					"					\"COUNTRY\", \"PIN_CODE\", \"SAP_CODE\",\"SITE_STATUS\", \"CREATION_TIME\", \r\n" + 
					"					\"CREATED_BY\") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?,now(), ?)";
            
			PreparedStatement pstmt = conn.prepareStatement(query);
			pstmt.setString(1, site.getSiteID());
			pstmt.setString(2, site.getSiteName());
			pstmt.setString(3, site.getSiteType());
			pstmt.setString(4, site.getClientId()); //client name 
			pstmt.setString(5, site.getDealerName());
			pstmt.setString(6, site.getAddress1());
			pstmt.setString(7, site.getAddress2());
			pstmt.setString(8, site.getAddress3());
			pstmt.setString(9, site.getCity());
			pstmt.setString(10, site.getDistrict());
			pstmt.setString(11, site.getState());
			pstmt.setString(12, site.getRegion());
			pstmt.setString(13, site.getCountry());
			pstmt.setString(14, site.getPinCode());
			pstmt.setString(15, site.getSiteID()); //sap code 
			pstmt.setString(16, site.getRequestFlag()); // site status 
			pstmt.setString(17, site.getRequestingUser()); // created by 
			
		
			
			int result = pstmt.executeUpdate();
			System.out.println(result);
	        return "";
			
		} catch(Exception e) {
			e.printStackTrace();
			
			return e.getMessage();
		}
	}
	
	
	
	public String getSiteID(String userName,String role){
		String siteId = null;
		try {
			if (conn == null || conn.isClosed()) 
				conn = dbc.getConnection();
			
			String query = " SELECT site_id FROM \"BCT\".user_sites where user_id = '"+userName+"' and role_title= '"+role+"' limit 1 ";
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			while (rs.next()) {
				siteId = rs.getString("site_id");
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		return siteId;
	}
	
	

}
