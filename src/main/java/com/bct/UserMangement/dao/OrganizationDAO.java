package com.bct.UserMangement.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.bct.UserMangement.bo.Organisation;
import com.bct.UserMangement.utils.DBConnector;

public class OrganizationDAO {
	
	private Connection conn = null;
	private DBConnector dbc = null;
	
	public OrganizationDAO() {
		dbc = new DBConnector();
		conn = dbc.getConnection();
	}
	
	public void createOrganization(Organisation org) {
	
		try {
			if (conn == null || conn.isClosed()) 
				conn = dbc.getConnection();
			
			String query = "INSERT INTO \"BCT\".organization (organization_code,sap_code,org_title,address1,address2,city,region,state,country_id,pincode,phone_no,phone_no_1,mobile_no,mobile_no_1,attribute1,attribute2,attribute3,attribute4,attribute5,attribute6,attribute7,attribute8,attribute9,timezone_code,description,status,created_by,created_date,modified_by,modified_date) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,now(),?,now())";
            PreparedStatement pstmt = conn.prepareStatement(query);
			pstmt.setString(1, org.getOrganization_code());
			pstmt.setString(2, org.getSap_code());
			pstmt.setString(3, org.getOrg_tile());
			pstmt.setString(4, org.getAddress1());
			pstmt.setString(5, org.getAddress2());
			pstmt.setString(6, org.getCity());
			pstmt.setString(7, org.getRegion());
			pstmt.setString(8, org.getState());
			pstmt.setInt(9, org.getCountryid());
			pstmt.setInt(10, org.getPincode());
			pstmt.setString(11, org.getPhone_no());
			pstmt.setString(12, org.getPhone_no_1());
			pstmt.setInt(13, org.getMobile_no());
			pstmt.setInt(14,  org.getMobile_no_1());
			pstmt.setString(15, org.getAttribute1());
			pstmt.setString(16, org.getAttribute2());
			pstmt.setString(17, org.getAttribute3());
			pstmt.setString(18, org.getAttribute4());
			pstmt.setString(19, org.getAttribute5());
			pstmt.setString(20, org.getAttribute6());
			pstmt.setString(21, org.getAttribute7());
			pstmt.setString(22, org.getAttribute8());
			pstmt.setString(23, org.getAttribute9());
			pstmt.setString(24, org.getTimezone_code());
			pstmt.setString(25, org.getDescription());
			pstmt.setString(26, org.getStatus());
			pstmt.setString(27, org.getCreated_by());
			pstmt.setString(28, org.getModified_by());
		    
			pstmt.executeUpdate();
			
	
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void updateOrganization(Organisation org) {
		
		try {
			if (conn == null || conn.isClosed()) 
				conn = dbc.getConnection();
			
			String query = "UPDATE \"BCT\".organization SET address1=?,address2=?,modified_date=now() WHERE organization_code=?";
			System.out.println("inside update org"+query);
			System.out.println("inside update org"+org);
			PreparedStatement pstmt = conn.prepareStatement(query);
			pstmt.setString(1, org.getAddress1());
			pstmt.setString(2, org.getAddress2());
			pstmt.setString(3, org.getOrganization_code());
		    pstmt.executeUpdate();
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public boolean deleteOrganization(String org_code){
		try {
			if (conn == null || conn.isClosed()) 
				conn = dbc.getConnection();
			
			String query = " DELETE FROM \"BCT\".Organization WHERE organization_code ='" + org_code + "'";
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			return true;
			
		} catch(Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public List<Organisation> getOrganizationList(){
		
		List<Organisation> orgs = new ArrayList();
		try {
			if (conn == null || conn.isClosed()) 
				conn = dbc.getConnection();
			
			String query = " Select * from \"BCT\".organization";
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			while (rs.next()) {
				Organisation org = new Organisation();
				org.setId(rs.getInt(1));
				org.setOrganization_code(rs.getString(2));
				org.setSap_code(rs.getString(3));
				org.setOrg_tile(rs.getString(4));
				org.setAddress1(rs.getString(5));
				org.setAddress2(rs.getString(6));
				org.setCity(rs.getString(7));
				org.setState(rs.getString(9));
				org.setRegion(rs.getString(8));
				org.setCountryid(rs.getInt(10));
				org.setPincode(rs.getInt(11));
				org.setPhone_no(rs.getString(12));
				org.setPhone_no_1(rs.getString(13));
				org.setMobile_no(rs.getInt(14));
				org.setMobile_no_1(rs.getInt(15));
				org.setAttribute1(rs.getString(16));
				org.setAttribute2(rs.getString(17));
				org.setAttribute3(rs.getString(18));
				org.setAttribute4(rs.getString(19));
				org.setAttribute5(rs.getString(20));
				org.setAttribute6(rs.getString(21));
				org.setAttribute7(rs.getString(22));
				org.setAttribute8(rs.getString(23));
				org.setAttribute8(rs.getString(24));
				org.setAttribute9(rs.getString(25));
				org.setDescription(rs.getString(26));
				org.setTimezone_code(rs.getString(25));
				org.setStatus(rs.getString(27));
				org.setCreated_by(rs.getString(29));
				org.setModified_by(rs.getString(31));
				orgs.add(org);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		return orgs;
	}

	public Organisation getOrganization(String org_code){
		Organisation org = new Organisation();
		try {
			if (conn == null || conn.isClosed()) 
				conn = dbc.getConnection();
			
			String query = " SELECT * FROM \"BCT\".organization WHERE organization_code='"+org_code +"'";
			// System.out.println("inside get org"+query);
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			while (rs.next()) {
				org.setId(rs.getInt(1));
				org.setOrganization_code(rs.getString(2));
				org.setSap_code(rs.getString(3));
				org.setOrg_tile(rs.getString(4));
				org.setAddress1(rs.getString(5));
				org.setAddress2(rs.getString(6));
				org.setCity(rs.getString(7));
				org.setState(rs.getString(9));
				org.setRegion(rs.getString(8));
				org.setCountryid(rs.getInt(10));
				org.setPincode(rs.getInt(11));
				org.setPhone_no(rs.getString(12));
				org.setPhone_no_1(rs.getString(13));
				org.setMobile_no(rs.getInt(14));
				org.setMobile_no_1(rs.getInt(15));
				org.setAttribute1(rs.getString(16));
				org.setAttribute2(rs.getString(17));
				org.setAttribute3(rs.getString(18));
				org.setAttribute4(rs.getString(19));
				org.setAttribute5(rs.getString(20));
				org.setAttribute6(rs.getString(21));
				org.setAttribute7(rs.getString(22));
				org.setAttribute8(rs.getString(23));
				org.setAttribute8(rs.getString(24));
				org.setAttribute9(rs.getString(25));
				org.setDescription(rs.getString(26));
				org.setTimezone_code(rs.getString(25));
				org.setStatus(rs.getString(27));
				org.setCreated_by(rs.getString(29));
				org.setModified_by(rs.getString(31));
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		return org;
	}


}