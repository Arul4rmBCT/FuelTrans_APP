package com.bct.UserMangement.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.bct.UserMangement.bo.ScreenMaster;
import com.bct.UserMangement.utils.DBConnector;

public class ScreenMasterDAO {

	private Connection conn = null;
	private DBConnector dbc = null;
	
	public ScreenMasterDAO() {
		dbc = new DBConnector();
		conn = dbc.getConnection();
	}
	
	public String createScreen(ScreenMaster screen) {
		
		try {
			if (conn == null || conn.isClosed()) 
				conn = dbc.getConnection();
			
			String query = "INSERT INTO \"BCT\".screen_master (application_id,module_id,screen_id,screen_desc,language_id,entry_code,screen_type,display_flag) VALUES (?,?,?,?,?,?,?,?)";
            
			PreparedStatement pstmt = conn.prepareStatement(query);
			pstmt.setString(1, screen.getApplication_id());
			pstmt.setString(2, screen.getModule_id());
			pstmt.setString(3, screen.getScreen_id());
			pstmt.setString(4, screen.getScreen_desc());
			pstmt.setString(5, screen.getLanguage_id());
			pstmt.setString(6, screen.getEntry_code());
			pstmt.setString(7, screen.getScreen_type());
			pstmt.setString(8, screen.getDisplay_flag());
			int result = pstmt.executeUpdate();
			System.out.println(result);
	        return "";
			
		} catch(Exception e) {
			e.printStackTrace();
			
			return e.getMessage();
		}
	}
	
	public void updateScreen(ScreenMaster screen) {
		
		try {
			if (conn == null || conn.isClosed()) 
				conn = dbc.getConnection();
			
			String query = "UPDATE \"BCT\".screen_master SET application_id=?,module_id=?,screen_desc=?,language_id=?,entry_code=?,screen_type=?,display_flag=? WHERE screen_id=?";
            
			PreparedStatement pstmt = conn.prepareStatement(query);
			pstmt.setString(1, screen.getApplication_id());
			pstmt.setString(2, screen.getModule_id());
			pstmt.setString(3, screen.getScreen_desc());
			pstmt.setString(4, screen.getLanguage_id());
			pstmt.setString(5, screen.getEntry_code());
			pstmt.setString(6, screen.getScreen_type());
			pstmt.setString(7, screen.getDisplay_flag());
			pstmt.setString(8, screen.getScreen_id());
			pstmt.executeUpdate();
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public boolean deleteScreen(String screen_id){
		try {
			if (conn == null || conn.isClosed()) 
				conn = dbc.getConnection();
			
			String query = " DELETE FROM \"BCT\".screen_master WHERE screen_id ='" + screen_id + "'";
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			return true;
			
		} catch(Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public List<ScreenMaster> getScreenList(){
		
		List<ScreenMaster> screens = new ArrayList();
		try {
			if (conn == null || conn.isClosed()) 
				conn = dbc.getConnection();
			
			String query = " Select * from \"BCT\".screen_master";
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			while (rs.next()) {
				ScreenMaster screen = new ScreenMaster();
				screen.setSeq_no(rs.getInt(1));
				screen.setApplication_id(rs.getString(2));
				screen.setModule_id(rs.getString(3));
				screen.setScreen_id(rs.getString(4));
				screen.setScreen_desc(rs.getString(5));
				screen.setLanguage_id(rs.getString(6));
				screen.setEntry_code(rs.getString(7));
				screen.setScreen_type(rs.getString(8));
				screen.setDisplay_flag(rs.getString(9));
				screens.add(screen);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		return screens;
	}

	public ScreenMaster getScreen(String screen_id){
		ScreenMaster screen = null;
		try {
			if (conn == null || conn.isClosed()) 
				conn = dbc.getConnection();
			
			String query = " SELECT * FROM \"BCT\".screen_master WHERE screen_id='"+screen_id +"'";
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			while (rs.next()) {
				screen = new ScreenMaster();
				screen.setSeq_no(rs.getInt(1));
				screen.setApplication_id(rs.getString(2));
				screen.setModule_id(rs.getString(3));
				screen.setScreen_id(rs.getString(4));
				screen.setScreen_desc(rs.getString(5));
				screen.setLanguage_id(rs.getString(6));
				screen.setEntry_code(rs.getString(7));
				screen.setScreen_type(rs.getString(8));
				screen.setDisplay_flag(rs.getString(9));
				
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		return screen;
	}

}
