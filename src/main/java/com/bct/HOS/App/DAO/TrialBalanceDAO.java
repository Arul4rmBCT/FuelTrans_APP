package com.bct.HOS.App.DAO;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.bct.HOS.App.utils.DBConnector;

public class TrialBalanceDAO {

	private Connection conn = null;
	private DBConnector dbc = null;
	private String schema = null;
	
	public TrialBalanceDAO() {
		dbc = new DBConnector();
		conn = dbc.getConnection();
		schema = dbc.getSchema();
	}
	
	public String trialBalanceRep(String startDate, String endDate, String stationId) {
		String fResult = null;
		try {
			Statement st = null;
			ResultSet rs = null;
			
			String SQL = "SELECT "+schema+".Generate_Trial_Balance('"+startDate+"'" +
	",'"+endDate+"','"+stationId+"') AS RESULT";
			
			System.out.println("SQL == "+SQL);
			st = conn.createStatement();
			rs = st.executeQuery(SQL);
			while(rs.next()) {
				fResult = rs.getString("result");
			}
			System.out.println("main result >>> "+fResult);
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("ErrOR @ Get Trial Balance ::" + ex.getMessage());
		} finally {
			try {
				dbc.closeConnection(conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return fResult;
	}
}
