package com.bct.HOS.App.DAO;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import com.bct.HOS.App.utils.DBConnector;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class NodeDAO {

	private Connection conn = null;
	private DBConnector dbc = null;
	private String schema = null;

	public NodeDAO() {
		dbc = new DBConnector();
		conn = dbc.getConnection();
		schema = dbc.getSchema();
	}

	/**
	 * 
	 * @return
	 */
	public JSONArray getHierarchialLocation(String country, String region, String state, String division,
			String district, String sub_district, String city) {
		JSONArray json = new JSONArray();
		try {
			String SQL = " SELECT UPPER(\"COUNTRY\") AS \"COUNTRY\" ,UPPER(\"REGION\") AS \"REGION\", "
					+ " UPPER(\"STATE\") AS \"STATE\" ,UPPER(\"DIVISION\") AS \"DIVISION\" ,"
					+ " UPPER(\"DISTRICT\") AS \"DISTRICT\" , "
					+ " UPPER(\"SUB_DISTRICT\") AS \"SUB_DISTRICT\",UPPER(\"CITY\") AS \"CITY\" " 
					+ " FROM " + schema + ".\"MS_RO_LOCATION\"  "
					+ " WHERE 1 = 1 ";

			if (country != null)
				SQL += " AND \"COUNTRY\" = '" + country + "' ";

			if (region != null)
				SQL += " AND \"REGION\" = '" + region + "' ";

			if (state != null)
				SQL += " AND \"STATE\" = '" + state + "' ";

			if (division != null)
				SQL += " AND \"DIVISION\" = '" + division + "' ";

			if (district != null)
				SQL += " AND \"DISTRICT\" = '" + district + "' ";

			if (sub_district != null)
				SQL += " AND \"SUB_DISTRICT\" = '" + sub_district + "' ";

			if (city != null)
				SQL += " AND \"CITY\" = '" + city + "' ";

			SQL += " GROUP BY \"COUNTRY\",\"REGION\",\"STATE\",\"DIVISION\",\"DISTRICT\", \"SUB_DISTRICT\",\"CITY\"";

			SQL += " ORDER BY \"COUNTRY\",\"REGION\",\"STATE\",\"DIVISION\",\"DISTRICT\",\"SUB_DISTRICT\",\"CITY\" ";

			//System.out.println(SQL);

			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(SQL);
			String countryStr = null;
			String preCountryStr = null;
			String regionStr = null;
			String preRegionStr = null;
			String stateStr = null;
			String preStateStr = null;
			String divisionStr = null;
			String preDicisionStr = null;
			String districtStr = null;
			String preDistrictStr = null;
			String subDistrictStr = null;
			String preSubDistrictStr = null;
			String cityStr = null;
			String preCityStr = null;
			JSONArray parent = null;
			JSONArray child = null;
			JSONObject node = null;
			while(rs.next()) {
				countryStr = rs.getString("COUNTRY");
				regionStr = rs.getString("REGION");
				stateStr  = rs.getString("STATE");
				divisionStr = rs.getString("DIVISION");
				districtStr = rs.getString("DISTRICT");
				subDistrictStr  = rs.getString("SUB_DISTRICT");
				cityStr = rs.getString("CITY");
				
				node = new JSONObject();
				node.put("COUNTRY", countryStr);
				node.put("REGION", regionStr);
				node.put("STATE", stateStr);
				node.put("DIVISION", divisionStr);
				node.put("DISTRICT", districtStr);
				node.put("SUB_DISTRICT", subDistrictStr);
				node.put("CITY", cityStr);
				
				
			}
			
			
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("ErrOR @ NodeDAO-getHierarchialLocation ::" + ex.getMessage());
		} finally {
			try {
				dbc.closeConnection(conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return json;
	}

}
