package com.bct.HOS.App.utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.UUID;

public class NumberingSeriesUtil {

	
	public static String GenerateNSUniqueId(String transType, String uniqueDisc) {
		
		DBConnector dbc = new DBConnector();
		Connection conn = dbc.getConnection();
		String schema = dbc.getSchema();
		UUID uuid = UUID.randomUUID();
		String uIdStr = null;
		try {
			String SQL = "INSERT INTO "+schema+".JMS_NUMBERING_SERIES_WRAPPER (GUID,OU,TRAN_TYPE,PARAMCODE,PARAMDESC) "
				+ "values (?,1,?,'CUSTCODE', ?)";
			PreparedStatement statement = conn.prepareStatement(SQL);
			statement.setString(1,uuid.toString());
			statement.setString(2,transType);
			statement.setString(3,uniqueDisc);
			int ct = statement.executeUpdate();
			
			if(ct > 0) {
				SQL = "select  "+schema+".GET_NUMBERING_SERIES(?,?,'SUPERADMIN'::text) as result";
				statement = conn.prepareStatement(SQL);
				statement.setString(1,uuid.toString());
				statement.setString(2,transType);
				ResultSet rs = statement.executeQuery();
				if(rs.next()) {
					uIdStr = rs.getString("result");
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("ErrOR @ Numbering Series generate ::" + ex.getMessage());
			return null;
		} finally {
			try {
				dbc.closeConnection(conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return uIdStr;
	}
	/*
	 INSERT INTO "ALMAHA".JMS_NUMBERING_SERIES_WRAPPER (GUID,OU,TRAN_TYPE,PARAMCODE,PARAMDESC)
	  SELECT'10001',1,'CUSTNFS','CUSTCODE', 'ALMFS104/NFS' ;
	  select  "ALMAHA".GET_NUMBERING_SERIES('10001'::text,'CUSTNFS'::text,'SUPERADMIN'::text);
	 */
}
