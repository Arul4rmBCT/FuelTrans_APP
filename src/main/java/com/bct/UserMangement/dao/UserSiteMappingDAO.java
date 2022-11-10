package com.bct.UserMangement.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.bct.HOS.App.BO.TSMBO;
import com.bct.UserMangement.bo.UserSiteMapping;
import com.bct.UserMangement.utils.DBConnector;

import net.sf.json.JSONArray;

public class UserSiteMappingDAO {

	private Connection conn = null;
	private DBConnector dbc = null;
	private String schema = null;
	
	public UserSiteMappingDAO() {
		dbc = new DBConnector();
		conn = dbc.getConnection();
		schema = dbc.getSchema();
	}

	public String createUserSiteMapping(UserSiteMapping[] usersites) {
//		System.out.println("token"+usersites);
		try {
			if (conn == null || conn.isClosed()) 
				conn = dbc.getConnection();
			
			String query = "INSERT INTO \"BCT\".user_sites (user_id,site_id,role_title,status,country) VALUES (?,?,?,?,?)";
            
			PreparedStatement pstmt = conn.prepareStatement(query);
			for(UserSiteMapping usersite: usersites) {
				pstmt.setString(1, usersite.getUser_id());
				pstmt.setString(3, usersite.getRole_title());
				pstmt.setString(2,  usersite.getSite_id());
				pstmt.setString(4, usersite.getStatus());
				pstmt.setString(5, usersite.getCountry());
				pstmt.addBatch();
			}
			int result[] = pstmt.executeBatch();
			System.out.println(result);
	        return "";
			
		} catch(Exception e) {
			e.printStackTrace();
			return e.getMessage();
		}
	}
	
	public void updateUserSiteMapping(UserSiteMapping usersite) {
		
		try {
			if (conn == null || conn.isClosed()) 
				conn = dbc.getConnection();
			
			String query = "UPDATE \"BCT\".user_sites SET site_id,=?role_title=?,status=? WHERE user_id=?";
            
			PreparedStatement pstmt = conn.prepareStatement(query);
			pstmt.setString(4, usersite.getUser_id());
			pstmt.setString(1, usersite.getRole_title());
			pstmt.setString(2,  usersite.getSite_id());
			pstmt.setString(3, usersite.getStatus());
		    pstmt.executeUpdate();
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public boolean deleteUserSiteMapping(UserSiteMapping[] usersites){
		try {
			if (conn == null || conn.isClosed()) 
				conn = dbc.getConnection();
			
			String query = " DELETE FROM \"BCT\".user_sites WHERE user_id=? and site_id=?";
			PreparedStatement pstmt = conn.prepareStatement(query);
			for(UserSiteMapping usersite: usersites) {
				pstmt.setString(1, usersite.getUser_id());
				pstmt.setString(2,  usersite.getSite_id());
				pstmt.addBatch();
			}
			int result[] = pstmt.executeBatch();
			return true;
			
		} catch(Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public List<UserSiteMapping> getUserSiteMappingList(){
		
		List<UserSiteMapping> usersites = new ArrayList();
		try {
			if (conn == null || conn.isClosed()) 
				conn = dbc.getConnection();
			
			String query = " Select * from \"BCT\".user_sites";
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			while (rs.next()) {
				UserSiteMapping usersite = new UserSiteMapping();
				usersite.setUser_id(rs.getString(1));
				usersite.setRole_title(rs.getString(2));
				usersite.setSite_id(rs.getString(3));
				usersite.setStatus(rs.getString(4));
				usersite.setCountry(rs.getString(5));
				usersites.add(usersite);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		return usersites;
	}

	public UserSiteMapping getUserSiteMapping(String user_id){
		UserSiteMapping usersite = null;
		try {
			if (conn == null || conn.isClosed()) 
				conn = dbc.getConnection();
			
			String query = " SELECT * FROM \"BCT\".user_sites WHERE user_id='"+user_id +"'";
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			while (rs.next()) {
				usersite = new UserSiteMapping();
				usersite.setUser_id(rs.getString(1));
				usersite.setRole_title(rs.getString(2));
				usersite.setSite_id(rs.getString(3));
				usersite.setStatus(rs.getString(4));
				usersite.setCountry(rs.getString(5));
				
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		return usersite;
	}
	
	public JSONArray newROList(String user_id) {
		JSONArray json = new JSONArray();
		try {
					
			String SQL = " SELECT DISTINCT \"SITE_ID\",\"SITE_NAME\",\"COUNTRY\" FROM \"FTHOS\".\"MS_SITE\"\r\n" + 
					"WHERE 1=1 and \"SITE_ID\" not in(Select site_id from \"BCT\".user_sites where user_id='"+user_id+"') " ;
			
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(SQL);
			json = dbc.parseRS(rs);
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("ErrOR @ UtilDAO-getROList ::" + ex.getMessage());
		} 
		return json;
	}
	
	public JSONArray existingROList(String user_id) {
		JSONArray json = new JSONArray();
		try {
					
			String SQL = " SELECT DISTINCT \"SITE_ID\",\"SITE_NAME\",\"COUNTRY\" FROM \"FTHOS\".\"MS_SITE\" MS \r\n" + 
					"INNER JOIN \"BCT\".user_sites US ON MS.\"SITE_ID\"=US.site_id \r\n" + 
					"WHERE 1=1 AND US.user_id='"+user_id+"' " ;
			
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(SQL);
			json = dbc.parseRS(rs);
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("ErrOR @ UtilDAO-getROList ::" + ex.getMessage());
		} 
		return json;
	}

}
