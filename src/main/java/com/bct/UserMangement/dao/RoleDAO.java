package com.bct.UserMangement.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.bct.UserMangement.bo.Role;
import com.bct.UserMangement.utils.DBConnector;

public class RoleDAO {
	
	private Connection conn = null;
	private DBConnector dbc = null;
	
	public RoleDAO() {
		dbc = new DBConnector();
		conn = dbc.getConnection();
	}
	
	public String createRole(Role role) {
	
		try {
			if (conn == null || conn.isClosed()) 
				conn = dbc.getConnection();
			
			String query = "INSERT INTO \"BCT\".roles (role_title,description,hierarchy,status,created_by,created_date,modified_by,modified_date,org_id) VALUES (?,?,?,?,?,now(),?,now(),?)";
            
			PreparedStatement pstmt = conn.prepareStatement(query);
			pstmt.setString(1, role.getRole_title());
			pstmt.setString(2, role.getDescription());
			pstmt.setInt(3, role.getHierarchy());
			pstmt.setString(4, role.getStatus());
			pstmt.setString(5,role.getCreated_by());
			pstmt.setString(6,role.getModified_by());
			pstmt.setInt(7,role.getOrg_id());
			int result = pstmt.executeUpdate();
			System.out.println(result);
	        return "";
			
		} catch(Exception e) {
			e.printStackTrace();
			
			return e.getMessage();
		}
	}
	
	public void updateRole(Role role) {
		
		try {
			if (conn == null || conn.isClosed()) 
				conn = dbc.getConnection();
			
			String query = "UPDATE \"BCT\".roles SET description=?,hierarchy=?,status=?,modified_date=now() WHERE role_title=?";
            
			PreparedStatement pstmt = conn.prepareStatement(query);
			pstmt.setString(1, role.getDescription());
			pstmt.setString(3, role.getStatus());
			pstmt.setInt(2,  role.getHierarchy());
			pstmt.setString(4, role.getRole_title());
		    pstmt.executeUpdate();
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public boolean deleteRole(String role_title){
		try {
			if (conn == null || conn.isClosed()) 
				conn = dbc.getConnection();
			
			String query = " DELETE FROM \"BCT\".roles WHERE role_title ='" + role_title + "'";
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			return true;
			
		} catch(Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public List<Role> getRoleList(){
		
		List<Role> roles = new ArrayList();
		try {
			if (conn == null || conn.isClosed()) 
				conn = dbc.getConnection();
			
			String query = " Select * from \"BCT\".roles";
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			while (rs.next()) {
				Role role = new Role();
				role.setId(rs.getInt(1));
				role.setRole_title(rs.getString(2));
				role.setDescription(rs.getString(3));
				role.setHierarchy(rs.getInt(4));
				role.setStatus(rs.getString(5));
				role.setCreated_by(rs.getString(6));
				role.setCreated_date(rs.getDate(7));
				role.setModified_by(rs.getString(8));
				role.setModified_date(rs.getDate(9));
				role.setOrg_id(rs.getInt(10));
				roles.add(role);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		return roles;
	}

	public Role getRole(String role_title){
		Role role = null;
		try {
			if (conn == null || conn.isClosed()) 
				conn = dbc.getConnection();
			
			String query = " SELECT * FROM \"BCT\".roles WHERE role_title='"+role_title +"'";
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			while (rs.next()) {
				role = new Role();
				role.setId(rs.getInt(1));
				role.setRole_title(rs.getString(2));
				role.setDescription(rs.getString(3));
				role.setHierarchy(rs.getInt(4));
				role.setStatus(rs.getString(5));
				role.setCreated_by(rs.getString(6));
				role.setCreated_date(rs.getDate(7));
				role.setModified_by(rs.getString(8));
				role.setModified_date(rs.getDate(9));
				role.setOrg_id(rs.getInt(10));
				
				
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		return role;
	}

	public Role getRoleById(String id){
		Role role = null;
		try {
			if (conn == null || conn.isClosed()) 
				conn = dbc.getConnection();
			
			String query = " SELECT * FROM \"BCT\".roles WHERE id='"+id +"'";
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			while (rs.next()) {
				role = new Role();
				role.setId(rs.getInt(1));
				role.setRole_title(rs.getString(2));
				role.setDescription(rs.getString(3));
				role.setHierarchy(rs.getInt(4));
				role.setStatus(rs.getString(5));
				role.setCreated_by(rs.getString(6));
				role.setCreated_date(rs.getDate(7));
				role.setModified_by(rs.getString(8));
				role.setModified_date(rs.getDate(9));
				role.setOrg_id(rs.getInt(10));
				
				
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		return role;
	}

}
