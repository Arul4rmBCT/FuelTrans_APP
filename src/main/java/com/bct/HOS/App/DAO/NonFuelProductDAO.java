package com.bct.HOS.App.DAO;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import com.bct.HOS.App.utils.DBConnector;

import net.sf.json.JSONArray;

public class NonFuelProductDAO {

	private Connection conn = null;
	private DBConnector dbc = null;
	private String schema = null;

	public NonFuelProductDAO() {
		dbc = new DBConnector();
		conn = dbc.getConnection();
		schema = dbc.getSchema();
	}
	/*
	 
	 CREATE TABLE IF NOT EXISTS "ALMAHA"."MS_NF_PRODUCTS"
(
    "PRODUCT_ID" bigint NOT NULL DEFAULT nextval('"ALMAHA"."MS_NF_PRODUCTS_PRODUCT_ID_seq"'::regclass),
    "PRODUCT_CODE" character varying NOT NULL,
    "PRODUCT_NAME" character varying,
	"PRODUCT_DISC" character varying,
	"STOCK_TYPE" character varying(10),
	"BASE_UNIT" character varying(15),
	"INVENTORY" character varying(10),
	"INV_GROUP" character varying(20),
	"VAT_GROUP" character varying(20),
	"GEN_PROD_GROUP" character varying(20),
    "SALES_UNIT" character varying(15),
	"PURCH_UNIT" character varying(15),
    "TYPE" character varying(15),
    "COST_ADJUST" character,
    "PRICE" numeric(10,4),
    "ACTIVE" character default 'T',
    "CREATED_BY" character varying(50),
    "CREATED_DATE" timestamp without time zone,
    "MODIFIED_BY" character varying(50),
    "MODIFIED_DATE" timestamp without time zone,
    CONSTRAINT "NF_PRODUCTS_PK" PRIMARY KEY ("PRODUCT_ID")
)
	 */

	public JSONArray getNonFuelProductList() {
		JSONArray json = new JSONArray();

		try {
			String SQL = " SELECT \"PRODUCT_CODE\" AS product_code,\"PRODUCT_NAME\" AS product_name FROM "+schema+".\"MS_NF_PRODUCTS\" where \"ACTIVE\"='T'";
			
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(SQL);
			json = dbc.parseRS(rs);
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("ErrOR @ getNonFuelProductList ::" + ex.getMessage());
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
