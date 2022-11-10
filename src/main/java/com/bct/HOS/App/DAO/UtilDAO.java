package com.bct.HOS.App.DAO;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

import com.bct.HOS.App.BO.FTAuthResponseBO;
import com.bct.HOS.App.BO.TSMBO;
import com.bct.HOS.App.BO.UserBO;
import com.bct.HOS.App.BO.consoleTransaction_array;
import com.bct.HOS.App.BO.hdrcache;
import com.bct.HOS.App.utils.DBConnector;
import com.bct.HOS.App.utils.InMem;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class UtilDAO {

	private Connection conn = null;
	private DBConnector dbc = null;
	private String schema = null;
	
	public UtilDAO() {
		dbc = new DBConnector();
		conn = dbc.getConnection();
		schema = dbc.getSchema();
	}
	
	/**
	 * 
	 * @param langCode
	 * @param fileLocation
	 * @return
	 */
	public String getLanguageFile(String langCode,String fileLocation) {
		String responseStr = null;
		try {
			responseStr=new String(Files.readAllBytes(Paths.get(fileLocation.concat(langCode.toUpperCase())+"_Labels.json")));

		}catch(Exception e) {
			e.printStackTrace();
		}
		return responseStr;
	}
	
	/**
	 * 
	 * @return
	 */
	public JSONArray getCountryList(String siteIDs) {
		JSONArray json = new JSONArray();
		try {
			String SQL = " SELECT DISTINCT MRL.\"COUNTRY\", MC.\"CURRENCY_CODE\" FROM "+schema+".\"MS_RO_LOCATION\" MRL " + 
					"INNER JOIN "+schema+".\"MS_SITE\" MS ON MS.\"COUNTRY\" = MRL.\"COUNTRY\" " +
					"INNER JOIN \"BCT\".\"MS_COUNTRY\" MC ON MS.\"COUNTRY\" = MC.\"COUNTRY\" " +
					" WHERE \"SITE_ID\" IN ("+siteIDs+") ";
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(SQL);
			json = dbc.parseRS(rs);
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("ErrOR @ UtilDAO-getCountryList ::" + ex.getMessage());
		} finally {
			try {
				dbc.closeConnection(conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return json;
	}
	
	
	public JSONArray getUserCountryList(String userId) {
		JSONArray json = new JSONArray();
		try {
			String SQL = " SELECT DISTINCT MRL.\"COUNTRY\", MC.\"CURRENCY_CODE\" FROM "+schema+".\"MS_RO_LOCATION\" MRL " + 
					"INNER JOIN "+schema+".\"MS_SITE\" MS ON MS.\"COUNTRY\" = MRL.\"COUNTRY\" " +
					"INNER JOIN \"BCT\".user_sites USS ON USS.site_id = MS.\"SITE_ID\" " +
					"INNER JOIN \"BCT\".\"MS_COUNTRY\" MC ON MS.\"COUNTRY\" = MC.\"COUNTRY\" " +
					" WHERE user_id IN ('"+userId+"') ";
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(SQL);
			json = dbc.parseRS(rs);
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("ErrOR @ UtilDAO-getUserCountryList ::" + ex.getMessage());
		} finally {
			try {
				dbc.closeConnection(conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return json;
	}
	
	
	/**
	 * 
	 * @return
	 */
	public JSONArray getStateList(String siteIDs,String county) {
		JSONArray json = new JSONArray();
		try {
			String SQL = " SELECT DISTINCT MRL.\"STATE\" FROM "+schema+".\"MS_RO_LOCATION\" MRL INNER JOIN "+schema+".\"MS_SITE\" MS ON MS.\"COUNTRY\" = MRL.\"COUNTRY\" WHERE \"SITE_ID\" IN ("+siteIDs+") ";
			
			if(county!=null)
				SQL += " AND MRL.\"COUNTRY\" = '"+county+"' ";

			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(SQL);
			json = dbc.parseRS(rs);
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("ErrOR @ UtilDAO-getStateList ::" + ex.getMessage());
		} finally {
			try {
				dbc.closeConnection(conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return json;
	}
	
	/**
	 * 
	 * @return
	 */
	public JSONArray getRegionList(String siteIDs,String county) {
		JSONArray json = new JSONArray();
		try {
			String SQL = " SELECT DISTINCT MRL.\"REGION\" FROM "+schema+".\"MS_RO_LOCATION\" MRL INNER JOIN "+schema+".\"MS_SITE\" MS ON MS.\"COUNTRY\" = MRL.\"COUNTRY\" WHERE \"SITE_ID\" IN ("+siteIDs+") ";
			
			if(county!=null)
				SQL += " AND MRL.\"COUNTRY\" = '"+county+"' ";

			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(SQL);
			json = dbc.parseRS(rs);
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("ErrOR @ UtilDAO-getRegionList ::" + ex.getMessage());
		} finally {
			try {
				dbc.closeConnection(conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return json;
	}
	
	
	/**
	 * 
	 * @return
	 */
	public JSONArray getDivisionList(String siteIDs,String county,String state) {
		JSONArray json = new JSONArray();
		try {
			String SQL = " SELECT DISTINCT MRL.\"DIVISION\" FROM "+schema+".\"MS_RO_LOCATION\" MRL INNER JOIN "+schema+".\"MS_SITE\" MS ON MS.\"COUNTRY\" = MRL.\"COUNTRY\" WHERE \"SITE_ID\" IN ("+siteIDs+") ";
			
			if(county!=null)
				SQL += " AND MRL.\"COUNTRY\" = '"+county+"' ";
			
			if(state !=null)
				SQL += " AND MRL.\"STATE\" = '"+state+"' ";

			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(SQL);
			json = dbc.parseRS(rs);
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("ErrOR @ UtilDAO-getDivisionList ::" + ex.getMessage());
		} finally {
			try {
				dbc.closeConnection(conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return json;
	}
	
	
	/**
	 * 
	 * @return
	 */
	public JSONArray getSubDistrictList(String siteIDs,String county,String state,String district) {
		JSONArray json = new JSONArray();
		try {
			String SQL = " SELECT DISTINCT MRL.\"SUB_DISTRICT\" FROM "+schema+".\"MS_RO_LOCATION\" MRL INNER JOIN "+schema+".\"MS_SITE\" MS ON MS.\"COUNTRY\" = MRL.\"COUNTRY\" WHERE MS.\"SITE_ID\" IN ("+siteIDs+") ";
			
			if(county!=null)
				SQL += " AND MRL.\"COUNTRY\" = '"+county+"' ";

			if(state !=null)
				SQL += " AND MRL.\"STATE\" = '"+state+"' ";

			if(district !=null)
				SQL += " AND MRL.\"DISTRICT\" = '"+district+"' ";

			
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(SQL);
			json = dbc.parseRS(rs);
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("ErrOR @ UtilDAO-getSubDistrictList ::" + ex.getMessage());
		} finally {
			try {
				dbc.closeConnection(conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return json;
	}
	
	/**
	 * 
	 * @return
	 */
	public JSONArray getDistrictList(String siteIDs,String county,String state) {
		JSONArray json = new JSONArray();
		try {
			String SQL = " SELECT DISTINCT MRL.\"DISTRICT\" FROM "+schema+".\"MS_RO_LOCATION\" MRL INNER JOIN "+schema+".\"MS_SITE\" MS ON MS.\"COUNTRY\" = MRL.\"COUNTRY\" WHERE MS.\"SITE_ID\" IN ("+siteIDs+") ";
			
			if(county!=null)
				SQL += " AND MRL.\"COUNTRY\" = '"+county+"' ";

			if(state !=null)
				SQL += " AND MRL.\"STATE\" = '"+state+"' ";
			
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(SQL);
			json = dbc.parseRS(rs);
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("ErrOR @ UtilDAO-getDistrictList ::" + ex.getMessage());
		} finally {
			try {
				dbc.closeConnection(conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return json;
	}
	
	
	
	/**
	 * 
	 * @return
	 */
	public JSONArray getCityList(String siteIDs,String county,String state) {
		JSONArray json = new JSONArray();
		try {
			String SQL = " SELECT DISTINCT MRL.\"CITY\" FROM "+schema+".\"MS_RO_LOCATION\" MRL INNER JOIN "+schema+".\"MS_SITE\" MS ON MS.\"COUNTRY\" = MRL.\"COUNTRY\" WHERE MS.\"SITE_ID\" IN ("+siteIDs+") ";
			
			if(county!=null)
				SQL += " AND MRL.\"COUNTRY\" = '"+county+"' ";

			if(state !=null)
				SQL += " AND MRL.\"STATE\" = '"+state+"' ";

			
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(SQL);
			json = dbc.parseRS(rs);
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("ErrOR @ UtilDAO-getCityList ::" + ex.getMessage());
		} finally {
			try {
				dbc.closeConnection(conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return json;
	}
	
	
	/**
	 * 
	 * @return
	 */
	public JSONArray getProductList(String siteIDs,String county) {
		JSONArray json = new JSONArray();
		try {
			String SQL = null;
			if(siteIDs!=null)
				SQL = " SELECT DISTINCT UPPER(PRD.\"PRODUCT_NAME\") AS \"PRODUCT_NAME\" FROM "+schema+".\"MS_SITE\" MS INNER JOIN "+schema+".\"MS_PRODUCTS\" PRD ON PRD.\"SITE_ID\" = MS.\"SITE_ID\" WHERE MS.\"SITE_ID\" IN ("+siteIDs+") ";
			else
				SQL = " SELECT DISTINCT UPPER(PRD.\"PRODUCT_NAME\") AS \"PRODUCT_NAME\" FROM "+schema+".\"MS_SITE\" MS INNER JOIN "+schema+".\"MS_PRODUCTS\" PRD ON PRD.\"SITE_ID\" = MS.\"SITE_ID\" WHERE 1 = 1 ";			
			
			SQL += " AND PRD.\"ADRM_STATUS\" != 'D' ";
			//SQL += " AND PRD.\"MODIFIED_DATE\" = (SELECT MAX(\"MODIFIED_DATE\") FROM "+schema+".\"MS_PRODUCTS\" PRD1 WHERE PRD1.\"PRODUCT_NO\" = PRD.\"PRODUCT_NO\" AND PRD1.\"SITE_ID\" = PRD.\"SITE_ID\" ) ";			
			if(county!=null)
				SQL += " AND MS.\"COUNTRY\" = '"+county+"' ";
			
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(SQL);
			json = dbc.parseRS(rs);
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("ErrOR @ UtilDAO-getCityList ::" + ex.getMessage());
		} finally {
			try {
				dbc.closeConnection(conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return json;
	}
	
	
	
	/**
	 * 
	 * @return
	 */
	public JSONArray getPumpList(String siteIDs) {
		JSONArray json = new JSONArray();
		try {
			String SQL = " SELECT DISTINCT \"PUMP_NO\" FROM "+schema+".\"MS_SITE\" MS " + 
					" INNER JOIN "+schema+".\"MS_PUMP_LIST\" PL ON PL.\"SITE_ID\" = MS.\"SITE_ID\" " + 
					" WHERE MS.\"SITE_ID\" IN ("+siteIDs+") " + 
					" AND PL.\"MODIFIED_TIME\" = (SELECT MAX(\"MODIFIED_TIME\") FROM "+schema+".\"MS_PUMP_LIST\" PL1 WHERE PL1.\"SITE_ID\"=PL.\"SITE_ID\" AND PL1.\"PUMP_NO\"=PL.\"PUMP_NO\" ) " + 
					" ORDER BY \"PUMP_NO\"";
			
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(SQL);
			json = dbc.parseRS(rs);
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("ErrOR @ UtilDAO-getPumpList ::" + ex.getMessage());
		} finally {
			try {
				dbc.closeConnection(conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return json;
	}
	
	
	
	/**
	 * 
	 * @return
	 */
	public JSONArray getTankList(String siteIDs,String productName) {
		JSONArray json = new JSONArray();
		try {
			String SQL = " SELECT DISTINCT \"TANK_NO\" FROM "+schema+".\"MS_SITE\" MS " + 
					" INNER JOIN "+schema+".\"MS_PRODUCTS\" PRD ON PRD.\"SITE_ID\" = MS.\"SITE_ID\" " + 
					" INNER JOIN "+schema+".\"MS_TANK\" PL ON PL.\"SITE_ID\" = MS.\"SITE_ID\" AND PL.\"PRODUCT_NO\"=PRD.\"PRODUCT_NO\" " + 
					" WHERE MS.\"SITE_ID\" IN ("+siteIDs+") " ;
			//SQL += " AND PRD.\"MODIFIED_DATE\" = (SELECT MAX(\"MODIFIED_DATE\") FROM "+schema+".\"MS_PRODUCTS\" PRD1 WHERE PRD1.\"PRODUCT_NO\" = PRD.\"PRODUCT_NO\" AND PRD1.\"SITE_ID\" = PRD.\"SITE_ID\" ) ";
			//SQL += " AND PL.\"MODIFIED_TIME\" = (SELECT MAX(\"MODIFIED_TIME\") FROM "+schema+".\"MS_TANK\" TNK1 WHERE TNK1.\"SITE_ID\" = PL.\"SITE_ID\"  AND TNK1.\"TANK_NO\"=PL.\"TANK_NO\" ) ";
			SQL += "AND PRD.\"ADRM_STATUS\" != 'D' ";
			SQL += "AND PL.\"ADRM_STATUS\" != 'D' ";
			

			if(productName!=null)
					SQL+=" AND UPPER(PRD.\"PRODUCT_NAME\") = UPPER('"+productName+"') ";
			
			SQL += " ORDER BY \"TANK_NO\"";
			
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(SQL);
			json = dbc.parseRS(rs);
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("ErrOR @ UtilDAO-getTankList ::" + ex.getMessage());
		} finally {
			try {
				dbc.closeConnection(conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return json;
	}
	
	
	/**
	 * 
	 * @return
	 */
	public JSONArray getNozzleList(String siteIDs) {
		JSONArray json = new JSONArray();
		try {
			String SQL = " SELECT DISTINCT \"NOZZLE_NO\" FROM "+schema+".\"MS_SITE\" MS " + 
					" INNER JOIN "+schema+".\"MS_PRODUCTS\" PRD ON PRD.\"SITE_ID\" = MS.\"SITE_ID\" " + 
					" INNER JOIN "+schema+".\"MS_NOZZLE_LIST\" NL ON NL.\"SITE_ID\" = MS.\"SITE_ID\"  AND NL.\"PRODUCT_NO\"=PRD.\"PRODUCT_NO\" " + 
					" WHERE MS.\"SITE_ID\" IN ("+siteIDs+") " +
					" AND PRD.\"ADRM_STATUS\" != 'D' AND NL.\"ADRM_STATUS\" != 'D' " +
					//" AND PRD.\"MODIFIED_DATE\" = (SELECT MAX(\"MODIFIED_DATE\") FROM "+schema+".\"MS_PRODUCTS\" PRD1 WHERE PRD1.\"PRODUCT_NO\" = PRD.\"PRODUCT_NO\" AND PRD1.\"SITE_ID\" = PRD.\"SITE_ID\" ) "+
					//" AND NL.\"MODIFIED_DATE\" = (SELECT MAX(\"MODIFIED_DATE\") FROM "+schema+".\"MS_NOZZLE_LIST\" NL1 WHERE NL1.\"SITE_ID\"=NL.\"SITE_ID\" AND NL1.\"NOZZLE_NO\"=NL.\"NOZZLE_NO\" ) "+
					" ORDER BY \"NOZZLE_NO\"  " ;
			
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(SQL);
			json = dbc.parseRS(rs);
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("ErrOR @ UtilDAO-getNozzleList ::" + ex.getMessage());
		} finally {
			try {
				dbc.closeConnection(conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return json;
	}
	
	
	/**
	 * 
	 * @return
	 */
	public JSONArray getDUList(String siteIDs) {
		JSONArray json = new JSONArray();
		try {
			String SQL = " SELECT DISTINCT \"DU_NO\" FROM "+schema+".\"MS_SITE\" MS " + 
					" INNER JOIN "+schema+".\"MS_PRODUCTS\" PRD ON PRD.\"SITE_ID\" = MS.\"SITE_ID\" " + 
					" INNER JOIN "+schema+".\"MS_DISPENSER\" PL ON PL.\"SITE_ID\" = MS.\"SITE_ID\" " + 
					" WHERE MS.\"SITE_ID\" IN ("+siteIDs+") " + 
					" AND PRD.\"ADRM_STATUS\" != 'D' AND PL.\"ADRM_STATUS\" != 'D' " +
					//" AND PRD.\"MODIFIED_DATE\" = (SELECT MAX(\"MODIFIED_DATE\") FROM "+schema+".\"MS_PRODUCTS\" PRD1 WHERE PRD1.\"PRODUCT_NO\" = PRD.\"PRODUCT_NO\" AND PRD1.\"SITE_ID\" = PRD.\"SITE_ID\" ) "+
					//" AND PL.\"MODIFIED_DATE\" = (SELECT MAX(\"MODIFIED_DATE\") FROM "+schema+".\"MS_DISPENSER\" DSP1 WHERE DSP1.\"SITE_ID\"=PL.\"SITE_ID\" AND DSP1.\"DU_NO\"=PL.\"DU_NO\" ) " +
					" ORDER BY \"DU_NO\" ";
			
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(SQL);
			json = dbc.parseRS(rs);
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("ErrOR @ UtilDAO-getDUList ::" + ex.getMessage());
		} finally {
			try {
				dbc.closeConnection(conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return json;
	}
	
	
	/**
	 * 
	 * @return
	 */
	public JSONArray getROList(String siteIDs, TSMBO tsmBO) {
		JSONArray json = new JSONArray();
		try {
			/*
			String SQL = " SELECT DISTINCT \"SITE_ID\",\"SITE_NAME\" FROM "+schema+".\"MS_SITE\" MS " + 
					" INNER JOIN "+schema+".\"MS_RO_LOCATION\" MRL ON MRL.\"COUNTRY\" = MS.\"COUNTRY\" " + 
					" WHERE 1=1 " ;
			*/
			String country = tsmBO.getCountry();
			String state = tsmBO.getState();
			String city = tsmBO.getCity();
			String region = tsmBO.getRegion();
			String district = tsmBO.getDistrict();
			String clientName = tsmBO.getClientName();
			
			String SQL = " SELECT DISTINCT \"SITE_ID\",\"SITE_NAME\",\"COUNTRY\" FROM "+schema+".\"MS_SITE\" MS " + 
					" WHERE 1=1 " ;
			
			if(siteIDs!=null)
						SQL += " AND MS.\"SITE_ID\" IN ("+siteIDs+") ";
					
			if(country!=null)
				SQL += " AND MS.\"COUNTRY\" = '"+country+"' ";
			
			if(state!=null)
				SQL += " AND MS.\"STATE\" = '"+state+"' ";
			
			if(city!=null)
				SQL += " AND MS.\"CITY\" = '"+city+"' ";
			
			if(region!=null)
				SQL += " AND MS.\"REGION\" = '"+region+"' ";
			

			if(district!=null) {				
				SQL += " AND MS.\"DISTRICT\" = '"+district+"' ";
			
			}
			
			if(clientName!=null) {				
				SQL += " AND MS.\"CLIENT_NAME\" = '"+clientName+"' ";
			}
			
			SQL += " ORDER BY \"SITE_NAME\" ASC";
			
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(SQL);
			json = dbc.parseRS(rs);
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("ErrOR @ UtilDAO-getROList ::" + ex.getMessage());
		} finally {
			try {
				dbc.closeConnection(conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return json;
	}
	
	
	public JSONArray callFTROValidateService(String siteId,String user, String fturl) {
		JSONArray jsonArray = new JSONArray();
		JSONObject jsonObj = null;
		try {
			URL url = new URL(fturl);
			InMem mem = InMem.getInstance();
			HashMap memMap=(HashMap) mem.get(user);
			String token=(String) memMap.get("FT_TOKEN");
			
			siteId = siteId.replaceAll("'", "");
			
			//System.out.println(siteId +"--"+ user);
			JSONObject reqObj = new JSONObject();
			JSONObject reqInObj = new JSONObject();
			reqObj.put("workFlowName", "CoreCustMstService");
			reqInObj.put("answer", "");
			reqInObj.put("methodName", "fetchCustomerCO");
			reqInObj.put("strToken", token);
			reqInObj.put("strCustomerCode", siteId);
			reqInObj.put("strFCMDeviceKey", "0000000000");
			reqInObj.put("strOSType", "HOS-Web");
			reqInObj.put("strUserId", user);
			reqObj.put("workFlowParams",reqInObj);
			
			
			byte[] postDataBytes = reqObj.toString().getBytes("UTF-8");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
			conn.setDoOutput(true);
			conn.getOutputStream().write(postDataBytes);

			int status = conn.getResponseCode();
			StringBuilder sb = new StringBuilder();
			if (status == 200 || status == 201 ) {
				BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
				String line;
				while ((line = br.readLine()) != null) {
					sb.append(line + "\n");
				}
				br.close();
			}
			String responseJson = sb.toString();
			//System.out.println("responseJson>  "+responseJson);
			if (responseJson != null) {
				Gson gson = new Gson();
				FTAuthResponseBO ftBO = null;
				HashMap hmRO = new HashMap();
				ftBO = gson.fromJson(responseJson, FTAuthResponseBO.class);
				if(ftBO!=null) {
					String FTerrmsg = ftBO.getStrFailureMsg();
					//System.out.println("ftBO.getStrFailureMsg() :: "+FTerrmsg);
					if(FTerrmsg==null) {
						if (ftBO.getHdrcache() != null) {
							for (hdrcache hdr : ftBO.getHdrcache()) {
								jsonObj = new JSONObject();
								jsonObj.put("SITE_NO", hdr.getStrSiteNo());
								jsonObj.put("CUSTOMER_CODE", hdr.getStrCustomerCode());
								jsonObj.put("CUSTOMER_NAME", hdr.getStrCustomerName());
								jsonObj.put("PHONE1", hdr.getStrPhone1());
								jsonObj.put("PHONE2", hdr.getStrPhone2());
								jsonObj.put("EMAIL", hdr.getStrEmail());
								jsonObj.put("ADDRESS", hdr.getStrAddress());
								jsonObj.put("AREA", hdr.getStrArea());
								jsonObj.put("CITY", hdr.getStrCity());
								jsonObj.put("STATE", hdr.getStrState());
								jsonObj.put("COUNTRY", hdr.getStrCountry());
								jsonObj.put("ZIPCODE", hdr.getStrZipCode());
								jsonObj.put("ORGANIZATION", hdr.getStrOrgId());
								jsonObj.put("STATUS", hdr.getStrStatus());
								jsonObj.put("CATEGORY", hdr.getStrCustomerCategory());
								jsonObj.put("ERP_CODE", hdr.getStrORPICCustCode());
								jsonArray.add(jsonObj);
							}
						}
					}
				}
			}
			////System.out.println(userbo.getRoleName());
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			try {
				dbc.closeConnection(conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return jsonArray;
	}
	
	
	/**
	 * 
	 */
	public void runReportSync() {
		try {
			String SQL = " call " + schema + ".\"REPORTSYNC_TRNX_PRC\"(1) ";
			Statement stmt = conn.createStatement();
			stmt.executeUpdate(SQL);
			System.out.println("Report Data Sync Running....");
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("ErrOR @ UtilDAO-runReportSync ::" + ex.getMessage());
		} finally {
			try {
				dbc.closeConnection(conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
}
