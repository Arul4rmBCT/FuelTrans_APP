package com.bct.HOS.App.DAO;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.bct.HOS.App.BO.consoleMaster_array;
import com.bct.HOS.App.utils.DBConnector;
import com.bct.HOS.App.utils.InMem;

public class HierarchyFT {

	private Connection conn = null;
	private String schema = null;
	DBConnector dbc = null;
	
	public HierarchyFT() {
		dbc= new DBConnector();
		conn = dbc.getConnection();
		schema = dbc.getSchema();
	}

	/**
	 * 
	 * @param userId
	 * @param roleId
	 * @return
	 */
	public String getUserHierarchyFTTemp(String userId, String roleId, String country) {
		String result = null;
		// String SQL = " SELECT SITE_ID FROM "+schema+".\"USER_RO_MAPPING\" WHERE 1=1 "
		// + " AND USER_ID = '" + userId
		// + "' AND ROLE_ID = '" + roleId + "'";
		String SQL = " SELECT SITE_ID FROM \""+schema+"\".\"USER_RO_MAPPING\" USM "
				+ " INNER JOIN \""+schema+"\".\"MS_SITE\" ST ON ST.\"SITE_ID\" = USM.SITE_ID " + " WHERE 1=1  ";
		if (country != null) {
			SQL += " AND ST.\"COUNTRY\" = '" + country + "' ";
		}

		SQL += " AND USM.USER_ID = '" + userId + "' " + " AND ROLE_ID = '" + roleId + "' ";

		////System.out.println("SQL:" + SQL);
		List<String> siteIds = new ArrayList<String>();
		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(SQL);
			while (rs.next()) {
				siteIds.add(rs.getString("SITE_ID"));
			}
			////System.out.println(siteIds);
			result = siteIds.toString();
			result = result.replace("[", "'");
			result = result.replace("]", "'");
			result = result.replace(" ", "");
			result = result.replace(",", "','");

		} catch (Exception ex) {
			ex.printStackTrace();
			//System.out.println(ex.getMessage());
		} finally {
			try {
				dbc.closeConnection(conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return result;
	}


	/**
	 * 
	 * @param userId
	 * @param roleId
	 * @return
	 */
	public String getUserHierarchyFT(String userId, String roleId, String country) {
		String result = null;
		StringBuffer bf = new StringBuffer();
		try {
			InMem mem = InMem.getInstance();
			HashMap hm = (HashMap) mem.get(userId);
			if (hm != null) {
				ArrayList<consoleMaster_array> hierarchyArray = (ArrayList<consoleMaster_array>) hm.get("HIERARCHY");
				bf.append("'");
				bf.append((String) hm.get("RO_MAP"));
				bf.append("'");
				result = bf.toString();
				result = result.replaceAll(",", "','");
				
				////System.out.println("getUserHierarchyFT>>"+result);

				if(country!=null) {
					String SQL = " SELECT \"SITE_ID\" FROM  " + schema + ".\"MS_SITE\"  WHERE 1=1  "
							+ " AND \"SITE_ID\" IN ( " + result + " ) AND \"COUNTRY\" = '"+country+"'";
					
					////System.out.println("getUserHierarchyFT>>>"+SQL);
					List<String> siteIds = new ArrayList<String>();
					try {
						Statement stmt = conn.createStatement();
						ResultSet rs = stmt.executeQuery(SQL);
						while (rs.next()) {
							siteIds.add(rs.getString("SITE_ID"));
						}
						////System.out.println(siteIds);
						result = siteIds.toString();
						result = result.replace("[", "'");
						result = result.replace("]", "'");
						result = result.replace(" ", "");
						result = result.replace(",", "','");
	
					} catch (Exception ex) {
						ex.printStackTrace();
						//System.out.println(ex.getMessage());
					}
				}
			}else {
				//System.out.println("********************* ELSE ************************");
				//System.out.println("******** NO USER Details in MEMORY ****************");
				//System.out.println("***************************************************");
				//result = getUserHierarchyFTTemp(userId, roleId, country);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				dbc.closeConnection(conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return result;
	}

}
