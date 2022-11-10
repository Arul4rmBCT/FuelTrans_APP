package com.bct.UserMangement.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.bct.UserMangement.bo.EntryMaster;
import com.bct.UserMangement.utils.DBConnector;

public class EntryMasterDAO {

	private Connection conn = null;
	private DBConnector dbc = null;
	
	public EntryMasterDAO() {
		dbc = new DBConnector();
		conn = dbc.getConnection();
	}
	
	public String createEntry(EntryMaster em) {
		try {
			if (conn == null || conn.isClosed()) 
				conn = dbc.getConnection();
			
			String query = "INSERT INTO \"BCT\".entry_master (entry_code,entry_name,entry_js,entry_icon,entry_type,seq_no,entity_app,entity_report,entity_dashboard,eastpanel_flag,is_active,mob_flag) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";
            
			PreparedStatement pstmt = conn.prepareStatement(query);
			pstmt.setString(1, em.getEntry_code());
			pstmt.setString(2, em.getEntry_name());
			pstmt.setInt(6, em.getSeq_no());
			pstmt.setString(3, em.getEntry_js());
			pstmt.setString(4, em.getEntry_icon());
			pstmt.setString(5, em.getEntry_type());
			pstmt.setString(7, em.getEntity_app());
			pstmt.setString(8, em.getEntity_report());
			pstmt.setString(9, em.getEntity_dashboard());
			pstmt.setString(10, em.getEastpanel_flag());
			pstmt.setString(11, em.getIs_active());
			pstmt.setString(12, em.getMob_flag());

			int result = pstmt.executeUpdate();
			System.out.println(result);
	        return "";
			
		} catch(Exception e) {
			e.printStackTrace();
			return e.getMessage();
		}
	}

	public boolean deleteEntry(String entryCode){
		try {
			if (conn == null || conn.isClosed()) 
				conn = dbc.getConnection();
			
			String query = " DELETE FROM \"BCT\".entry_master WHERE entry_code ='" + entryCode + "'";
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			return true;
			
		} catch(Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public List<EntryMaster> getEntryList(){
		
		List<EntryMaster> entries = new ArrayList();
		try {
			if (conn == null || conn.isClosed()) 
				conn = dbc.getConnection();
			
			String query = " Select * from \"BCT\".entry_master";
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			while (rs.next()) {
				EntryMaster entry = new EntryMaster();
				entry.setEntry_code(rs.getString(1));
				entry.setEntry_name(rs.getString(2));
				entry.setSeq_no(rs.getInt(6));
				entry.setEntry_js(rs.getString(3));
				entry.setEntry_icon(rs.getString(4));
				entry.setEntry_type(rs.getString(5));
				entry.setEntity_app(rs.getString(7));
				entry.setEntity_report(rs.getString(8));
				entry.setEntity_dashboard(rs.getString(9));
				entry.setEastpanel_flag(rs.getString(10));
				entry.setIs_active(rs.getString(11));
				entry.setMob_flag(rs.getString(12));
				entries.add(entry); 
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		return entries;
	}

	public EntryMaster getEntry(String entry_code){
		EntryMaster entry = null;
		try {
			if (conn == null || conn.isClosed()) 
				conn = dbc.getConnection();
			
			String query = " SELECT * FROM \"BCT\".entry_master WHERE entry_code='"+entry_code +"'";
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			while (rs.next()) {
				entry = new EntryMaster();
				entry.setEntry_code(rs.getString(1));
				entry.setEntry_name(rs.getString(2));
				entry.setSeq_no(rs.getInt(6));
				entry.setEntry_js(rs.getString(3));
				entry.setEntry_icon(rs.getString(4));
				entry.setEntry_type(rs.getString(5));
				entry.setEntity_app(rs.getString(7));
				entry.setEntity_report(rs.getString(8));
				entry.setEntity_dashboard(rs.getString(9));
				entry.setEastpanel_flag(rs.getString(10));
				entry.setIs_active(rs.getString(11));
				entry.setMob_flag(rs.getString(12));
	
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		return entry;
	}

}
