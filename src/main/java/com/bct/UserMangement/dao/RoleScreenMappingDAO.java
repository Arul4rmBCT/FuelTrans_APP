package com.bct.UserMangement.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.bct.UserMangement.bo.RoleScreenMapping;
import com.bct.UserMangement.utils.DBConnector;

public class RoleScreenMappingDAO {

	private Connection conn = null;
	private DBConnector dbc = null;
	
	public RoleScreenMappingDAO() {
		dbc = new DBConnector();
		conn = dbc.getConnection();
	}

	public String createRoleScreenMapping(RoleScreenMapping[] roles) {
	
		try {
			if (conn == null || conn.isClosed()) 
				conn = dbc.getConnection();
			
			String query = "INSERT INTO \"BCT\".role_screen_mapping (roleid,component_id,screen_id,permission,seq_no,send_conf_flag) VALUES (?,?,?,?,?,?)";
            
			PreparedStatement pstmt = conn.prepareStatement(query);
			for(RoleScreenMapping role: roles) {
				//pstmt.clearParameters();
				pstmt.setInt(1, role.getRole_id());
				pstmt.setString(2, role.getComponent_id());
				pstmt.setString(3, role.getScreen_id());
				pstmt.setString(4, role.getPermission());
				pstmt.setInt(5,role.getSeq_no());
				pstmt.setInt(6,role.getSend_conf_flag());
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
	
	public void updateRoleScreenMapping(RoleScreenMapping role) {
		
		try {
			if (conn == null || conn.isClosed()) 
				conn = dbc.getConnection();
			
			String query = "UPDATE \"BCT\".role_screen_mapping SET component_id=?,screen_id=?,permission=? WHERE roleid=?";
            
			PreparedStatement pstmt = conn.prepareStatement(query);
			pstmt.setString(1, role.getComponent_id());
			pstmt.setString(3, role.getPermission());
			pstmt.setString(2,  role.getScreen_id());
			pstmt.setInt(4, role.getRole_id());
		    pstmt.executeUpdate();
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public boolean deleteRoleScreenMapping(Integer role_id){
		try {
			if (conn == null || conn.isClosed()) 
				conn = dbc.getConnection();
			
			String query = " DELETE FROM \"BCT\".role_screen_mapping WHERE roleid ='" + role_id + "'";
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			return true;
			
		} catch(Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public List<RoleScreenMapping> getRoleScreenMappingList(){
		
		List<RoleScreenMapping> roles = new ArrayList();
		try {
			if (conn == null || conn.isClosed()) 
				conn = dbc.getConnection();
			
			String query = " Select * from \"BCT\".role_screen_mapping";
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			while (rs.next()) {
				RoleScreenMapping role = new RoleScreenMapping();
				role.setRole_id(rs.getInt(1));
				role.setComponent_id(rs.getString(2));
				role.setScreen_id(rs.getString(3));
				role.setPermission(rs.getString(4));
				role.setSeq_no(rs.getInt(6));
				role.setSend_conf_flag(rs.getInt(7));
				roles.add(role);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		return roles;
	}

	public RoleScreenMapping getRoleScreenMapping(Integer role_id){
		RoleScreenMapping role = null;
		try {
			if (conn == null || conn.isClosed()) 
				conn = dbc.getConnection();
			
			String query = " SELECT * FROM \"BCT\".role_screen_mapping WHERE roleid='"+role_id +"'";
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			while (rs.next()) {
				role = new RoleScreenMapping();
				role.setRole_id(rs.getInt(1));
				role.setComponent_id(rs.getString(2));
				role.setScreen_id(rs.getString(3));
				role.setPermission(rs.getString(4));
				role.setSeq_no(rs.getInt(5));
				role.setSend_conf_flag(rs.getInt(6));
				
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		return role;
	}

}
