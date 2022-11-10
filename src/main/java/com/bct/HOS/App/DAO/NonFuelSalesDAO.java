package com.bct.HOS.App.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.bct.HOS.App.BO.NFSaleDetailBO;
import com.bct.HOS.App.BO.NFSalesHeaderBO;
import com.bct.HOS.App.utils.DBConnector;
import com.bct.HOS.App.utils.NumberingSeriesUtil;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class NonFuelSalesDAO {

	private Connection conn = null;
	private DBConnector dbc = null;
	private String schema = null;

	public NonFuelSalesDAO() {
		dbc = new DBConnector();
		conn = dbc.getConnection();
		schema = dbc.getSchema();
	}
	
	/*CREATE TABLE IF NOT EXISTS "ALMAHA".Nonfuel_sale_hdr (
	transaction_no SERIAL PRIMARY KEY,
	site_id character varying (15) NOT NULL,
	status character varying (15) NOT NULL,
	transaction_date timestamp without time zone NOT NULL default current_timestamp,
	shift_id character varying (15) NOT NULL,
	product_amt numeric NOT NULL,
	vat_amt numeric,
	total_amt numeric,
	currency character varying (10) NOT NULL,
	created_by character varying (50),
	created_date date,
	modified_by character varying (50),
	modified_date date
	);

	CREATE TABLE "ALMAHA".Nonfuel_sale_dtl (
	transaction_no VARCHAR (50) NOT NULL,
	site_id character varying (15) NOT NULL,
	product_id character varying (50) NOT NULL,
	quantity numeric(10,3) NOT NULL,
	price numeric(10,3) NOT NULL,
	amount numeric(20,3) NOT NULL
	);*/
	
	public JsonObject storeNonFuelSales(String site_id, NFSalesHeaderBO nfSales) {

		String uniText = site_id+"/NFS";
		String transType = "CUSTNFS";
		String unqueId = NumberingSeriesUtil.GenerateNSUniqueId(transType,uniText);
				String SQL = "INSERT INTO " + schema + ".Nonfuel_sale_hdr "
				+ "(transaction_no,\"site_id\",\"status\",\"shift_id\",\"product_amt\",\"vat_amt\",\"total_amt\",\"currency\",\"created_by\",\"created_date\",\"transaction_date\") "
				+ " VALUES (?,?,?,?,?,?,?,?,?,current_timestamp,?) returning transaction_no";
				
		try {
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			Date parsed = format.parse(nfSales.getTransaction_date()!= null ? nfSales.getTransaction_date() : String.valueOf(java.time.LocalDate.now()));
			java.sql.Date transactionDate = new java.sql.Date(parsed.getTime());
			
			PreparedStatement statement = conn.prepareStatement(SQL);
		
			statement.setString(1, unqueId);
			statement.setString(2, nfSales.getRo_id());
			statement.setString(3, nfSales.getStatus());
			statement.setString(4, nfSales.getShift_id());
			statement.setDouble(5, nfSales.getProduct_amt());
			statement.setDouble(6, nfSales.getVat_amt());
			statement.setDouble(7, nfSales.getTotal_amt());
			statement.setString(8, nfSales.getCurrency());
			statement.setString(9, nfSales.getCreated_by());
			statement.setDate(10, transactionDate);
			
			ResultSet rs = statement.executeQuery();
			if(rs.next()) {
				nfSales.setTransaction_no(rs.getString(1));
			}
			nfSales.setStatus(nfSales.getStatus()); 
			if(nfSales.getNFDetails().size() > 0 && nfSales.getTransaction_no() != null) {
			
				 SQL = "INSERT INTO " + schema + ".Nonfuel_sale_dtl "
						+ "(transaction_no,site_id,product_id,quantity,price,amount) "
						+ " VALUES (?,?,?,?,?,?)";
				 
				 statement = conn.prepareStatement(SQL);
				 for(NFSaleDetailBO dtl : nfSales.getNFDetails() ) {
					 statement.setString(1, nfSales.getTransaction_no());
					 statement.setString(2, nfSales.getRo_id());
					 statement.setString(3, dtl.getProduct_id());
					 statement.setDouble(4, dtl.getQuantity());
					 statement.setDouble(5, dtl.getPrice());
					 statement.setDouble(6, dtl.getAmount());
					 statement.addBatch();
				 }
				 statement.executeBatch();
			}
				
			String json = new Gson().toJson(nfSales);
			//System.out.println(json);
			return new JsonParser().parse(json).getAsJsonObject();
			 
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("ErrOR @ NonFuelSales store ::" + ex.getMessage());
			return null;
		} finally {
			try {
				dbc.closeConnection(conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public JsonObject updateNonFuelSales(NFSalesHeaderBO nfSales) {
		
		String SQL = "UPDATE " + schema + ".Nonfuel_sale_hdr SET status = '" + nfSales.getStatus()
		+ "', shift_id ='" + nfSales.getShift_id()
		+ "', vat_amt ='" + nfSales.getVat_amt()
		+ "', total_amt ='" + nfSales.getTotal_amt()
		+ "', product_amt ='" + nfSales.getProduct_amt()
		+ "', transaction_date ='" + nfSales.getTransaction_date()
		+ "', modified_by ='"+ nfSales.getModified_by()
		+ "', modified_date ='" + java.time.LocalDate.now()
		+ "' WHERE transaction_no ='" +  nfSales.getTransaction_no() + "'";
		try {
			System.out.println("TEST LOG: SQL query "+SQL);
			//System.out.println("SQL" + SQL);
			Statement stmt = conn.createStatement();
			stmt.execute(SQL);
			
			SQL = "DELETE FROM " + schema + ".Nonfuel_sale_dtl WHERE transaction_no ='" + nfSales.getTransaction_no() + "';"; 
			//System.out.println("SQL" + SQL);
			stmt = conn.createStatement();
			stmt.execute(SQL);
			
			 SQL = "INSERT INTO " + schema + ".Nonfuel_sale_dtl "
						+ "(transaction_no,site_id,product_id, quantity,price,amount) "
						+ " VALUES (?,?,?,?,?,?)";
			 
			   PreparedStatement statement = conn.prepareStatement(SQL);
			   for(NFSaleDetailBO dtl : nfSales.getNFDetails() ) {
					 statement.setString(1, nfSales.getTransaction_no());
					 statement.setString(2, nfSales.getRo_id());
					 statement.setString(3, dtl.getProduct_id());
					 statement.setDouble(4, dtl.getQuantity());
					 statement.setDouble(5, dtl.getPrice());
					 statement.setDouble(6, dtl.getAmount());
					 statement.addBatch();
				 }
				 statement.executeBatch();
				 System.out.println("TEST LOG: After running qurry "+SQL);
				String json = new Gson().toJson(nfSales);
				//System.out.println(json);
				return new JsonParser().parse(json).getAsJsonObject();
			

		} catch (Exception ex) {
			System.out.println("ErrOR @ NonFuelSales-update ::" +ex.getMessage());
			return null;
		} finally {
			try {
				dbc.closeConnection(conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}

	public boolean deleteNonFuelSalesByRO(String siteID) {
		String SQL = "DELETE FROM " + schema + ".Nonfuel_sale_hdr "
				+ "' WHERE site_id ='" + siteID + "' ;"
				+"DELETE FROM " + schema + ".Nonfuel_sale_dtl "
				+ "' WHERE site_id ='" + siteID + "' ;";
		try {

			Statement stmt = conn.createStatement();
			stmt.execute(SQL);
			return true;

		} catch (Exception ex) {
			System.out.println("ErrOR @ NonFuelSales-delete ::" +ex.getMessage());
			return false;
		} finally {
			try {
				dbc.closeConnection(conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}

	public boolean deleteNonFuelSalesTransNo(String transactionNo) {
		String SQL = "DELETE FROM " + schema + ".Nonfuel_sale_hdr "
				+ "' WHERE transaction_no ='" + transactionNo + "' ;" 
				+ "DELETE FROM " + schema + ".Nonfuel_sale_dtl "
				+ "' WHERE transaction_no ='" + transactionNo + "' ;"; 
		try {

			Statement stmt = conn.createStatement();
			stmt.execute(SQL);
			return true;

		} catch (Exception ex) {
			System.out.println("ErrOR @ BankDeposit-update ::" +ex.getMessage());
			return false;
		} finally {
			try {
				dbc.closeConnection(conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}
	
	
	public JSONArray getNFSalesByRO(String siteID) {
		JSONArray json = new JSONArray();
		
		try {
			String SQL = " SELECT transaction_no,status,(select ms_description from " + schema + ".ms_bank_details where ms_number=shift_id and mastertype ='shift') as shift_id, "
					+ "ROUND(total_amt,3) as total_amt,currency,to_char(transaction_date,'YYYY-MM-DD') as transaction_date "
					+ " FROM " + schema + ".Nonfuel_sale_hdr"
					+ " WHERE site_id = '" + siteID + "' ORDER BY transaction_no DESC ";
			
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(SQL);
			json = dbc.parseRS(rs);
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("ErrOR @ getNFSalesByRO ::" + ex.getMessage());
		} finally {
			try {
				dbc.closeConnection(conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return json;
	}
	
	
	public JSONObject getNFDetailsByTransNo(String siteId, String transNo) {
		JSONObject json = new JSONObject();

		try {
			String SQL = " SELECT transaction_no,to_char(transaction_date,'YYYY-MM-DD') as transaction_date,status,ROUND(product_amt,3) as product_amt,ROUND(vat_amt,3) as vat_amt,ROUND(total_amt,3) as total_amt,shift_id,"
					+ "(select ms_description from " + schema + ".ms_bank_details where ms_number=shift_id and mastertype ='shift') as shift_description,currency "
					+ " FROM " + schema + ".Nonfuel_sale_hdr "
					+ " WHERE  site_id = '"+siteId+"' and transaction_no = '" + transNo + "'";
			
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(SQL);
			
			json = dbc.parseRS(rs).getJSONObject(0);
			 
			SQL =" SELECT product_id,(select \"PRODUCT_NAME\" from " + schema + ".\"MS_NF_PRODUCTS\" where \"PRODUCT_CODE\"=product_id) as prod_name,quantity,ROUND(price,3) as price,ROUND(amount,3) as amount"
					+ " FROM " + schema + ".Nonfuel_sale_dtl"
					+ " WHERE site_id='"+siteId+"' and transaction_no = '" + transNo + "'";
			
			stmt = conn.createStatement();
			rs = stmt.executeQuery(SQL);
			JSONArray nfsDetails = dbc.parseRS(rs);
			json.put("nfDetails",nfsDetails);
			
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("ErrOR @NFDetailsByTransNo ::" + ex.getMessage());
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
