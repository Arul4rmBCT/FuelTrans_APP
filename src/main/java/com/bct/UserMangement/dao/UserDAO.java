package com.bct.UserMangement.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.bct.HOS.App.utils.InMem;
import com.bct.UserMangement.bo.LoginBO;
import com.bct.UserMangement.bo.LoginResponse;
import com.bct.UserMangement.bo.User;
import com.bct.UserMangement.bo.UserMenu;
import com.bct.UserMangement.utils.DBConnector;

public class UserDAO {
	
	private Connection conn = null;
	private DBConnector dbc = null;
	
	public UserDAO() {
		dbc = new DBConnector();
		conn = dbc.getConnection();
	}
	
	public String createUser(User user) {
	
		try {
			if (conn == null || conn.isClosed()) 
				conn = dbc.getConnection();
			
			String query = "INSERT INTO \"BCT\".users (roleid,firstname,middlename,lastname,username,mobile,email,passwordhash,web_access,mobile_access,lastlogin,status,created_by,created_date,modified_by,modified_date) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,now(),?,now())";
            
			PreparedStatement pstmt = conn.prepareStatement(query);
			pstmt.setInt(1, user.getRoleid());
			pstmt.setString(2, user.getFirstname());
			pstmt.setString(3, user.getMiddlename());
			pstmt.setString(4, user.getLastname());
			pstmt.setString(5, user.getUsername());
			pstmt.setString(6, user.getMobile());
			pstmt.setString(7, user.getEmail());
			pstmt.setString(8, user.getPasswordhash());
			pstmt.setString(9, user.getWebaccess());
			pstmt.setString(10, user.getMobileaccess());
			pstmt.setDate(11, user.getLastlogin());
			pstmt.setString(12, user.getStatus());
			pstmt.setString(13, user.getCreated_by());
			pstmt.setString(14, user.getModified_by());
		    
			pstmt.execute();
			return "";
	
			
		} catch(Exception e) {
			e.printStackTrace();
			return e.getMessage();
		}
	}
	
	public void updateUser(User user) {
		
		try {
			if (conn == null || conn.isClosed()) 
				conn = dbc.getConnection();
			
			String query = "UPDATE \"BCT\".users SET passwordhash=?,modified_date=now() WHERE username=?";
            
			PreparedStatement pstmt = conn.prepareStatement(query);
			pstmt.setString(1, user.getPasswordhash());
			pstmt.setString(2, user.getUsername());
		    pstmt.executeUpdate();
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public boolean deleteUser(String userName){
		try {
			if (conn == null || conn.isClosed()) 
				conn = dbc.getConnection();
			
			String query = " DELETE FROM \"BCT\".users WHERE username ='" + userName + "'";
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			return true;
			
		} catch(Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public List<User> getUserList(){
		
		List<User> users = new ArrayList();
		try {
			if (conn == null || conn.isClosed()) 
				conn = dbc.getConnection();
			
			String query = " Select * from \"BCT\".users";
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			while (rs.next()) {
				User user = new User();
				user.setRoleid(rs.getInt(1));
				user.setFirstname(rs.getString(3));
				user.setMiddlename(rs.getString(4));
				user.setLastname(rs.getString(5));
				user.setUsername(rs.getString(6));
				user.setMobile(rs.getString(7));
				user.setEmail(rs.getString(8));
				user.setPasswordhash(rs.getString(9));
				user.setWebaccess(rs.getString(10));
				user.setMobileaccess(rs.getString(11));
				user.setLastlogin(rs.getDate(12));
				user.setStatus(rs.getString(13));
				user.setCreated_by(rs.getString(14));
				user.setCreated_date(rs.getDate(15));
				user.setModified_by(rs.getString(16));
				user.setModified_date(rs.getDate(17));
				
				users.add(user);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		return users;
	}

	public User getUser(String username){
		User user = new User();
		try {
			if (conn == null || conn.isClosed()) 
				conn = dbc.getConnection();
			
			String query = " SELECT * FROM \"BCT\".users WHERE username='"+username +"'";
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			while (rs.next()) {
				
				user.setRoleid(rs.getInt(2));
				user.setFirstname(rs.getString(3));
				user.setMiddlename(rs.getString(4));
				user.setLastname(rs.getString(5));
				user.setUsername(rs.getString(6));
				user.setMobile(rs.getString(7));
				user.setEmail(rs.getString(8));
				user.setPasswordhash(rs.getString(9));
				user.setWebaccess(rs.getString(10));
				user.setMobileaccess(rs.getString(11));
				user.setLastlogin(rs.getDate(12));
				user.setStatus(rs.getString(13));
				user.setCreated_by(rs.getString(14));
				user.setCreated_date(rs.getDate(15));
				user.setModified_by(rs.getString(16));
				user.setModified_date(rs.getDate(17));
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		return user;
	}

	public LoginResponse authenticate(String username, String password) {
		LoginResponse loginRes = null;
		List<LoginBO> list = new ArrayList();
		User user = getUser(username);
		if(user != null && user.getPasswordhash().equals(password)) {
			//System.out.println("user correct"+username);
			loginRes = new LoginResponse();
			LoginBO login = new LoginBO();
			login.setUserId(username);
			login.setRoleId(String.valueOf(user.getRoleid()));
			login.setUserMail(user.getEmail());
			login.setUserMobile(user.getMobile());
			login.setLoginId(username);
			login.setRoleName(new RoleDAO().getRoleById(login.getRoleId()).getRole_title());
			login.setUserMenu(getUserMenus(username));
			if(login.getRoleName().equalsIgnoreCase("RO")) {
				login.setSiteID(new SiteDAO().getSiteID(username,"RO"));
			}else {
				login.setSiteID(username);
			}
			login.setSiteName(user.getFirstname());
			list.add(login);
			loginRes.setData(list);
			
		}
		return loginRes;
	}
	
	public List<UserMenu> getUserMenus(String username){
		//System.out.println("inside get menu user"+username);
		List<UserMenu> UserMenu = new ArrayList();
		try {
			if (conn == null || conn.isClosed()) 
				conn = dbc.getConnection();
			
			String query = "select ROW_NUMBER() OVER (PARTITION BY entityType ORDER BY SEQ_NO) \"SLNO\",SEQ_NO \"SEQ_NO\",menuText \"menuText\",menuIcon \"menuIcon\",searchJS \"searchJS\",entityType \"entityType\",appJS \"appJS\" ,reportJS \"reportJS\",dashboardJS \"dashboardJS\",EASTPANEL_FLAG \"EASTPANEL_FLAG\"\r\n" + 
					",entry_code \"ENTRY_ID\"\r\n" + 
					"from\r\n" + 
					"(\r\n" + 
					"SELECT distinct c.seq_no SEQ_NO, c.entry_name  menuText ,c.entry_icon  menuIcon,c.entry_js  searchJS,c.entry_type\r\n" + 
					"entityType, c.entity_app  appJS,c.entity_report  reportJS,C.Entity_Dashboard Dashboardjs,coalesce(c.EASTPANEL_FLAG,'N') EASTPANEL_FLAG,\r\n" + 
					"c.entry_code\r\n" + 
					"From  \"BCT\".role_screen_mapping A,\r\n" + 
					"      \"BCT\".screen_master B,\r\n" + 
					"      \"BCT\".entry_master C,\r\n" + 
					"      \"BCT\".users D\r\n" + 
					"Where \r\n" + 
					"	A.roleid = D.roleid And   \r\n" + 
					"	UPPER(username) =UPPER('"+username+"') And   \r\n" + 
					"	C.is_active='Y' And   \r\n" + 
					"	B.screen_id=A.screen_id And  \r\n" + 
					"	B.entry_code=C.entry_code And \r\n" + 
					"	A.permission='Yes' \r\n" + 
					"	Order By C.seq_no\r\n" + 
					")SUBQRY order by \"SLNO\",\"menuText\"";
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			while (rs.next()) {
				UserMenu user = new UserMenu();
				user.setSLNO(rs.getString(1));
				user.setSEQ_NO(rs.getString(2));
				user.setMenuText(rs.getString(3));
				user.setMenuIcon(rs.getString(4));
				user.setSearchJS(rs.getString(5));
				user.setEntityType(rs.getString(11));
				user.setScreenModule(rs.getString(6));
				user.setScreenSubModule(rs.getString(6));
				UserMenu.add(user);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}

		return UserMenu;
	}
	
}
