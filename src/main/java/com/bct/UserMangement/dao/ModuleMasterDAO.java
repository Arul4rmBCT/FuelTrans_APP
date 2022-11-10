package com.bct.UserMangement.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.bct.UserMangement.bo.ModuleMaster;
import com.bct.UserMangement.utils.DBConnector;

public class ModuleMasterDAO {

	private Connection conn = null;
	private DBConnector dbc = null;
	
	public ModuleMasterDAO() {
		dbc = new DBConnector();
		conn = dbc.getConnection();
	}
	
	public String createModule(ModuleMaster module) {
		
		try {
			if (conn == null || conn.isClosed()) 
				conn = dbc.getConnection();
			
			String query = "INSERT INTO \"BCT\".module_master (application_id,module_id,module_desc,language_id,type,visibility) VALUES (?,?,?,?,?,?)";
            
			PreparedStatement pstmt = conn.prepareStatement(query);
			pstmt.setString(1, module.getApplication_id());
			pstmt.setString(2, module.getModule_id());
			pstmt.setString(3, module.getModule_desc());
			pstmt.setString(4, module.getLanguage_id());
			pstmt.setString(5, module.getType());
			pstmt.setInt(6, module.getVisibility());
			int result = pstmt.executeUpdate();
			System.out.println(result);
	        return "";
			
		} catch(Exception e) {
			e.printStackTrace();
			
			return e.getMessage();
		}
	}
	
	public void updateModule(ModuleMaster module) {
		
		try {
			if (conn == null || conn.isClosed()) 
				conn = dbc.getConnection();
			
			String query = "UPDATE \"BCT\".module_master SET application_id=?,module_desc=?,language_id=?,type=?,visibility=? WHERE module_id=?";
            
			PreparedStatement pstmt = conn.prepareStatement(query);
			pstmt.setString(1, module.getApplication_id());
			pstmt.setString(2, module.getModule_desc());
			pstmt.setInt(5,  module.getVisibility());
			pstmt.setString(3, module.getLanguage_id());
			pstmt.setString(4, module.getType());
		    pstmt.executeUpdate();
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public boolean deleteModule(String module_id){
		try {
			if (conn == null || conn.isClosed()) 
				conn = dbc.getConnection();
			
			String query = " DELETE FROM \"BCT\".module_master WHERE module_id ='" + module_id + "'";
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			return true;
			
		} catch(Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public List<ModuleMaster> getModuleList(){
		
		List<ModuleMaster> modules = new ArrayList();
		try {
			if (conn == null || conn.isClosed()) 
				conn = dbc.getConnection();
			
			String query = " Select * from \"BCT\".module_master";
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			while (rs.next()) {
				ModuleMaster module = new ModuleMaster();
				module.setApplication_id(rs.getString(1));
				module.setModule_id(rs.getString(2));
				module.setModule_desc(rs.getString(3));
				module.setLanguage_id(rs.getString(4));
				module.setType(rs.getString(5));
				module.setVisibility(rs.getInt(6));
				modules.add(module);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		return modules;
	}

	public ModuleMaster getModule(String module_id){
		ModuleMaster module = null;
		try {
			if (conn == null || conn.isClosed()) 
				conn = dbc.getConnection();
			
			String query = " SELECT * FROM \"BCT\".module_master WHERE module_id='"+module_id +"'";
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			while (rs.next()) {
				module = new ModuleMaster();
				module.setApplication_id(rs.getString(1));
				module.setModule_id(rs.getString(2));
				module.setModule_desc(rs.getString(3));
				module.setLanguage_id(rs.getString(4));
				module.setType(rs.getString(5));
				module.setVisibility(rs.getInt(6));
					
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		return module;
	}


}
