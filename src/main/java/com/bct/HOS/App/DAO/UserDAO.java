package com.bct.HOS.App.DAO;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;

import com.bct.HOS.App.BO.FTAuthResponseBO;
import com.bct.HOS.App.BO.ResponseBO;
import com.bct.HOS.App.BO.UserBO;
import com.bct.HOS.App.BO.consoleReports_array;
import com.bct.HOS.App.BO.consoleTransaction_array;
import com.bct.HOS.App.BO.hdrcache;
import com.bct.HOS.App.BO.siteDetails_array;
import com.bct.HOS.App.utils.DBConnector;
import com.bct.HOS.App.utils.HOSConfig;
import com.google.gson.Gson;
import com.bct.HOS.App.utils.InMem;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class UserDAO {

	private Connection conn = null;
	private DBConnector dbc = null;
	private String schema = null;

	public UserDAO() {
		dbc = new DBConnector();
		conn = dbc.getConnection();
		schema = dbc.getSchema();

	}

	/**
	 * Insert widget data for user
	 * 
	 * @return
	 */
	public String createUserWidget(String userId, String userName, String roleId, String roleName, String data) {
		try {
			if (conn == null || conn.isClosed()) {
				conn = dbc.getConnection();
				schema = dbc.getSchema();
			}

			String SQL = "INSERT INTO " + schema
					+ ".\"USER_WIDGETS\"(USER_ID,USER_NAME,ROLE_ID,ROLE_NAME,MOD_DATETIME,WIDGET_TEXT) " + " VALUES ('"
					+ userId + "','" + userName + "','" + roleId + "','" + roleName + "', current_timestamp,'" + data
					+ "')";
			// System.out.println(SQL);
			Statement stmt = conn.createStatement();
			stmt.execute(SQL);

		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("ErrOR @ USerDAO-createUserWidget ::" + ex.getMessage());
		} finally {
			try {
				dbc.closeConnection(conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return data;
	}

	/**
	 * 
	 * @param userId
	 * @param password
	 * @param oldPassword
	 * @param confPassword
	 * @param emailId
	 * @param ftURL
	 */
	public String changePSW(String userId, String strToken, String password, String oldPassword, String confPassword,
			String emailId, String ftURL) {
		String strMsg = null;
		try {
			URL url = new URL(ftURL);
			JSONObject reqObj = new JSONObject();
			JSONObject reqInObj = new JSONObject();
			// HOSConfig config = new HOSConfig();
			byte[] postDataBytes = null;
			reqObj.put("workFlowName", "CoreAdminService");
			reqInObj.put("methodName", "UserchangePassword");
			reqInObj.put("strScreenName", null);
			reqInObj.put("strToken", strToken);
			reqInObj.put("strUserId", userId);
			reqInObj.put("strPassword", oldPassword);
			reqInObj.put("strNewPassword", password);
			reqInObj.put("strConfPassword", confPassword);
			reqInObj.put("strEmailId", emailId);
			reqObj.put("workFlowParams", reqInObj);
			//System.out.println(reqObj.toString());
			//System.out.println(ftURL);
			postDataBytes = reqObj.toString().getBytes("UTF-8");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
			conn.setDoOutput(true);
			conn.getOutputStream().write(postDataBytes);
			int status = conn.getResponseCode();
			//System.out.println("status>>" + status);
			StringBuilder sb = new StringBuilder();
			if (status == 200 || status == 201) {
				BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
				String line;
				while ((line = br.readLine()) != null) {
					sb.append(line + "\n");
				}
				br.close();
			}
			String responseJson = sb.toString();
			//System.out.println("responseJson>>>" + responseJson);

			Gson gson = new Gson();
			FTAuthResponseBO ftBO = null;
			ftBO = gson.fromJson(responseJson, FTAuthResponseBO.class);
			strMsg = ftBO.getStrFailureMsg();
			if (strMsg == null)
				strMsg = ftBO.getStrSuccessMsg();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return strMsg;
	}

	/**
	 * 
	 * @param userId
	 * @param ftURL
	 * @return
	 */
	public String getPSWOTP(ResponseBO responseObj, String userId, String ftURL) {
		String strMsg = null;
		try {
			URL url = new URL(ftURL);
			JSONObject reqObj = new JSONObject();
			JSONObject reqInObj = new JSONObject();
			// HOSConfig config = new HOSConfig();
			byte[] postDataBytes = null;
			reqObj.put("processType", "ForgotPassword");
			reqObj.put("workFlowName", "CoreLoginService");
			reqInObj.put("methodName", "validateUserPassword");
			reqInObj.put("strScreenName", null);
			reqInObj.put("strUserId", userId);
			reqObj.put("workFlowParams", reqInObj);
			//System.out.println(reqObj.toString());
			//System.out.println(ftURL);
			postDataBytes = reqObj.toString().getBytes("UTF-8");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
			conn.setDoOutput(true);
			conn.getOutputStream().write(postDataBytes);
			int status = conn.getResponseCode();
			//System.out.println("status>>" + status);
			StringBuilder sb = new StringBuilder();
			if (status == 200 || status == 201) {
				BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
				String line;
				while ((line = br.readLine()) != null) {
					sb.append(line + "\n");
				}
				br.close();
			}
			String responseJson = sb.toString();
			//System.out.println("responseJson>>>" + responseJson);

			Gson gson = new Gson();
			FTAuthResponseBO ftBO = null;
			ftBO = gson.fromJson(responseJson, FTAuthResponseBO.class);
			strMsg = ftBO.getStrFailureMsg();
			if (strMsg == null) {
				strMsg = ftBO.getStrSuccessMsg();
				responseObj.setMessage(strMsg);
				responseObj.setError("");
				responseObj.setErrorCode("0");
			} else {
				responseObj.setError(strMsg);
				responseObj.setErrorCode("9999");

			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return strMsg;
	}

	public void ResetPassword(ResponseBO responseObj, String userId, String password, String confPassword, String otp,
			String ftURL) {
		String strMsg = null;
		try {
			URL url = new URL(ftURL);
			JSONObject reqObj = new JSONObject();
			JSONObject reqInObj = new JSONObject();
			// HOSConfig config = new HOSConfig();
			byte[] postDataBytes = null;
			reqObj.put("processType", "ForgotPassword");
			reqObj.put("workFlowName", "CoreLoginService");
			reqInObj.put("methodName", "validateUserPassword");
			reqInObj.put("strPassword", otp);
			reqInObj.put("strNewPassword", password);
			reqInObj.put("strConfPassword", confPassword);
			reqObj.put("workFlowParams", reqInObj);
			//System.out.println(reqObj.toString());
			//System.out.println(ftURL);
			postDataBytes = reqObj.toString().getBytes("UTF-8");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
			conn.setDoOutput(true);
			conn.getOutputStream().write(postDataBytes);
			int status = conn.getResponseCode();
			//System.out.println("status>>" + status);
			StringBuilder sb = new StringBuilder();
			if (status == 200 || status == 201) {
				BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
				String line;
				while ((line = br.readLine()) != null) {
					sb.append(line + "\n");
				}
				br.close();
			}
			String responseJson = sb.toString();
			//System.out.println("responseJson>>>" + responseJson);

			Gson gson = new Gson();
			FTAuthResponseBO ftBO = null;
			ftBO = gson.fromJson(responseJson, FTAuthResponseBO.class);

			strMsg = ftBO.getStrFailureMsg();
			if (strMsg == null) {
				strMsg = ftBO.getStrSuccessMsg();
				responseObj.setMessage(strMsg);
				responseObj.setError("");
				responseObj.setErrorCode("0");
			} else {
				responseObj.setError(strMsg);
				responseObj.setErrorCode("9999");

			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public void ACTContactDetails(ResponseBO responseObj, String userId,String strToken, String mobileNo, String emailId, String otpMobile,String otpemail,
			String ftURL) {
		String strMsg = null;
		try {
			URL url = new URL(ftURL);
			JSONObject reqObj = new JSONObject();
			JSONObject reqInObj = new JSONObject();
			// HOSConfig config = new HOSConfig();
			byte[] postDataBytes = null;
			reqObj.put("processType", "Screen");
			reqObj.put("workFlowName", "CoreAdminService");
			reqInObj.put("methodName", "updateUserContactDetails");
			reqInObj.put("strUserId", userId);
			reqInObj.put("strToken", strToken);
			if(mobileNo!=null)
				reqInObj.put("strMobileNo", mobileNo);
			if(emailId!=null)
				reqInObj.put("strEmailId", emailId);
			if(otpMobile!=null)
				reqInObj.put("strMobOTP", otpMobile);
			if(otpemail!=null)
			reqInObj.put("strEmailOTP", otpemail);
			
			reqObj.put("workFlowParams", reqInObj);
			//System.out.println(reqObj.toString());
			//System.out.println(ftURL);
			postDataBytes = reqObj.toString().getBytes("UTF-8");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
			conn.setDoOutput(true);
			conn.getOutputStream().write(postDataBytes);
			int status = conn.getResponseCode();
			//System.out.println("status>>" + status);
			StringBuilder sb = new StringBuilder();
			if (status == 200 || status == 201) {
				BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
				String line;
				while ((line = br.readLine()) != null) {
					sb.append(line + "\n");
				}
				br.close();
			}
			String responseJson = sb.toString();
			//System.out.println("responseJson>>>" + responseJson);

			Gson gson = new Gson();
			FTAuthResponseBO ftBO = null;
			ftBO = gson.fromJson(responseJson, FTAuthResponseBO.class);

			strMsg = ftBO.getStrFailureMsg();
			if (strMsg == null) {
				strMsg = ftBO.getStrSuccessMsg();
				responseObj.setMessage(strMsg);
				responseObj.setError("");
				responseObj.setErrorCode("0");
			} else {
				responseObj.setError(strMsg);
				responseObj.setErrorCode("9999");

			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
			
	/**
	 * Get widget data for user
	 * 
	 * @return
	 */
	public String getUserWidget(String userId, String roleId, String sites, boolean isWithFleet) {
		String data = null;
		String SQL = " SELECT USER_ID,USER_NAME,ROLE_ID,ROLE_NAME,MOD_DATETIME,WIDGET_TEXT FROM " + schema
				+ ".\"USER_WIDGETS\" WHERE 1=1";
		try {
			if (conn == null || conn.isClosed())
				conn = dbc.getConnection();

			if (userId != null) {
				SQL += " AND USER_ID = '" + userId + "'";
			}

			if (roleId != null) {
				SQL += " AND ROLE_ID = '" + roleId + "'";
			}

			SQL += "LIMIT 1";

			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(SQL);
			while (rs.next()) {
				data = rs.getString("WIDGET_TEXT");
			}
			stmt = null;
			rs = null;

			if (sites != null && isWithFleet == false) {
				// Insert in to DB
				SQL = "DELETE FROM \"BCT\".user_sites WHERE user_id = '" + userId + "'";
				stmt = conn.createStatement();
				stmt.execute(SQL);
				stmt = null;
				rs = null;

				String[] strArr = sites.split(",");
				for (String siteId : strArr) {
					siteId = siteId.replaceAll("'", "");
					//SQL = "INSERT INTO " + schema + ".\"USER_SITES\"(\"USER_ID\",\"SITE_ID\") VALUES('" + userId + "','"+ siteId + "')";
					SQL = " INSERT INTO \"BCT\".user_sites(user_id,site_id,country) SELECT '"+userId+"','"+siteId+"', \"COUNTRY\" FROM "+schema+".\"MS_SITE\" WHERE \"SITE_ID\"='"+siteId+"'";
					//System.out.println(SQL);
					stmt = conn.createStatement();
					stmt.execute(SQL);
				}
			}
		} catch (Exception ex) {
			// System.out.println(ex.getMessage());
		} finally {
			try {
				dbc.closeConnection(conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return data;
	}
	
	/**
	 * Update user site mapping
	 * @param userId
	 * @param sites
	 * @return
	 */
	public boolean updateUserSite(String userId,ArrayList<siteDetails_array> sites) {
		try {
			String SQL = null;
			Statement stmt = null;

			if (conn == null || conn.isClosed())
				conn = dbc.getConnection();
			// Insert in to DB
			SQL = "DELETE FROM \"BCT\".user_sites WHERE user_id = '" + userId + "'";
			stmt = conn.createStatement();
			stmt.execute(SQL);
			stmt = null;
			
			for (siteDetails_array siteIdObj : sites) {
				SQL = " INSERT INTO \"BCT\".user_sites(user_id,site_id,country) "
						+ "SELECT '"+userId+"','"+siteIdObj.getSITE_ID()+"', \"COUNTRY\" "
						+ "FROM "+schema+".\"MS_SITE\" WHERE \"SITE_ID\"='"+siteIdObj.getSITE_ID()+"'";
				stmt = conn.createStatement();
				stmt.execute(SQL);
			}
			stmt = null;
			return true;
		} catch (Exception ex) {
			ex.printStackTrace();
			// System.out.println(ex.getMessage());
		} finally {
			try {
				dbc.closeConnection(conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return false;
	}
	

	/**
	 * Update widget data for user
	 * 
	 * @return
	 */
	public String getUserSites(String userId) {
		// System.out.println("Fleet!....");
		StringBuffer siteList = new StringBuffer();
		String SQL = null;
		try {
			// System.out.println("0");
			if (conn == null || conn.isClosed())
				conn = dbc.getConnection();
			// System.out.println("1");
			Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
			// System.out.println("2");
			SQL = "SELECT site_id as \"SITE_ID\" FROM \"BCT\".user_sites WHERE user_id='" + userId + "'";
			ResultSet rs = stmt.executeQuery(SQL);
			// System.out.println("SQL is ("+userId+") >>"+SQL);
			int size = 0;
			int loop = 0;
			if (rs != null) 
			{
			  rs.last();
			  size = rs.getRow();
			  rs.beforeFirst();
			}
			
			while (rs.next()) {
				// siteList.append("'");
				siteList.append(rs.getString("SITE_ID"));
				loop++;
				if(loop<size)
					siteList.append(',');
				
				// siteList.append("'");
			}
			stmt = null;
			rs = null;
		} catch (Exception ex) {
			ex.printStackTrace();
			// System.out.println(ex.getMessage());
		} finally {
			try {
				dbc.closeConnection(conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return siteList.toString();// .replaceAll("''", "','");
	}

	/**
	 * Update widget data for user
	 * 
	 * @return
	 */
	public boolean updateUserWidget(String userId, String roleId, String data) {
		boolean result = false;
		String SQL = "UPDATE " + schema + ".\"USER_WIDGETS\" SET WIDGET_TEXT = '" + data
				+ "',MOD_DATETIME=current_timestamp WHERE 1=1";
		try {

			if (userId != null) {
				SQL += " AND USER_ID = '" + userId + "'";
			}

			if (roleId != null) {
				SQL += " AND ROLE_ID = '" + roleId + "'";
			}

			Statement stmt = conn.createStatement();
			stmt.execute(SQL);
			result = true;

		} catch (Exception ex) {
			// System.out.println(ex.getMessage());
			result = false;
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
	 * Call FuelTrans Authentication Service
	 * 
	 * @param userId
	 * @param password
	 * @param userbo
	 */
	public UserBO callFTAuthService(String userId, String password, String fturl, String strToken, boolean isToken) {
		UserBO userbo = new UserBO();
		String token = null;
		try {
			// System.out.println("userId = " + userId);
			// System.out.println("password = " + password);
			URL url = new URL(fturl);

			InMem mem = InMem.getInstance();

			JSONObject reqObj = new JSONObject();
			JSONObject reqInObj = new JSONObject();
			HOSConfig config = new HOSConfig();
			String workflowName = config.getValue("FT_WORKFLOW");
			byte[] postDataBytes = null;

			//System.out.println("url is >> " + fturl);

			if (workflowName.equalsIgnoreCase("CoreLoginService")) {
				Map<String, Object> params = new LinkedHashMap();
				params.put("workFlowName", "CoreLoginService");
				params.put("workFlowParams", "{\"methodName\": \"fetchTenantId\",\"strUserId\": \"" + userId
						+ "\",\"strPassword\": \"" + password + "\", \"answer\": \"53xnc\"}");
				params.put("processType", "Screen");
				if (isToken) {
					params.put("strToken", strToken);
					params.put("strToken", "Y");
				}
				StringBuilder postData = new StringBuilder();
				for (Map.Entry<String, Object> param : params.entrySet()) {
					if (postData.length() != 0)
						postData.append('&');
					postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
					postData.append('=');
					postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
				}
				//System.out.println("reqObj is >> " + postData.toString());
				userbo.setWithFleet(true);
				postDataBytes = postData.toString().getBytes("UTF-8");
			} else {
				userbo.setWithFleet(false);
				reqObj.put("workFlowName", config.getValue("FT_WORKFLOW")); // "CoreTenantService"
				reqObj.put("processType", config.getValue("FT_PROCESS_TYPE"));
				reqInObj.put("answer", "");
				reqInObj.put("methodName", config.getValue("FT_METHOD_NAME"));
				reqInObj.put("strPassword", password);
				reqInObj.put("strUserId", userId);
				reqInObj.put("strFCMDeviceKey", "0000000000");
				reqInObj.put("strOSType", "HOS-Web");
				if (isToken) {
					reqInObj.put("strToken", strToken);
					reqInObj.put("isToken", "Y");
				}
				reqObj.put("workFlowParams", reqInObj);
				
				postDataBytes = reqObj.toString().getBytes("UTF-8");
			}

			long startTime = System.nanoTime();
			System.out.println("Before Invoking FT Call connection..");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
			conn.setDoOutput(true);
			conn.getOutputStream().write(postDataBytes);
			// Reader in = new BufferedReader(new InputStreamReader(conn.getInputStream(),
			// "UTF-8"));
			// for (int c; (c = in.read()) >= 0;)
			// System.out.print((char) c);

			int status = conn.getResponseCode();
			System.out.println("After Invoking FT Call connection.."+status);
			StringBuilder sb = new StringBuilder();
			if (status == 200 || status == 201) {
				BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
				String line;
				while ((line = br.readLine()) != null) {
					sb.append(line + "\n");
				}
				br.close();
			}
			String responseJson = sb.toString();
			long stopTime = System.nanoTime();
			double seconds = (double)(stopTime - startTime)/1000000000;
			System.out.println("TT FT call>>"+seconds);

			//System.out.println("url> "+fturl);
			//System.out.println("reqObj is >> " + reqObj.toString());
			//System.out.println("responseJson> " + responseJson);
			
			if (responseJson != null) {
				Gson gson = new Gson();
				FTAuthResponseBO ftBO = null;
				HashMap hmRO = new HashMap();
				ftBO = gson.fromJson(responseJson, FTAuthResponseBO.class);
				if (ftBO != null) {
					String FTerrmsg = ftBO.getStrFailureMsg();
					System.out.println("FT >> ftBO.getStrFailureMsg() :: " + FTerrmsg);
					if (FTerrmsg == null) {
						if (ftBO.getHdrcache() != null) {
							for (hdrcache hdr : ftBO.getHdrcache()) {
								token = hdr.getStrToken();
								if (token == null)
									token = hdr.getHdnAuthToken();

								userbo.setUserEmail(hdr.getStrEmailId());
								userbo.setUserMobile(hdr.getStrMobNumber());
								userbo.setRoleName(hdr.getROLE_NAME());
								userbo.setRoleId(hdr.getStrRoleId());
								userbo.setUserName(hdr.getUSER_NAME());
								userbo.setFtToken(token);
							}

							ArrayList<consoleTransaction_array> userMenu = ftBO.getConsoleTransaction_array();
							JSONArray menuArray = new JSONArray();
							if (userMenu != null) {
								JSONObject menuObj;
								String menuText = null;
								String[] menuFlow = null;
								for (consoleTransaction_array obj : userMenu) {
									if (obj.getEntityType() != null) {
										if (obj.getEntityType().equalsIgnoreCase("HOS")) {
											menuObj = new JSONObject();
											menuObj.put("SLNO", obj.getSLNO());
											menuObj.put("SEQ_NO", obj.getSEQ_NO());
											menuText = obj.getMenuText();
											if (menuText.contains("~")) {
												menuFlow = menuText.split("~");
												if (menuFlow.length == 3) {
													menuObj.put("screenModule", menuFlow[0]);
													menuObj.put("screenSubModule", menuFlow[1]);
													menuObj.put("menuText", menuFlow[2]);
												}
											} else {
												menuObj.put("menuText", menuText);
												menuObj.put("screenModule", obj.getScreenModule());
												menuObj.put("screenSubModule", obj.getScreenSubModule());
											}
											menuObj.put("menuIcon", obj.getMenuIcon());
											menuObj.put("searchJS", obj.getSearchJS());
											menuObj.put("entityType", obj.getEntityType());
											menuArray.add(menuObj);
										}
									}
								}
							}
							userbo.setUserMenu(menuArray);
							
							ArrayList<siteDetails_array> siteList = ftBO.getSiteDetails_array();
							if(siteList!=null) {
								updateUserSite(userId,siteList);
							}
							

							if (userbo.getRoleName().equalsIgnoreCase("RO")
									|| userbo.getRoleId().equalsIgnoreCase("RO")) {
								hmRO.put("HIERARCHY", null);
								hmRO.put("RO_MAP", userId);
							} else {
								hmRO.put("HIERARCHY", ftBO.getConsoleMaster_array());
								// System.out.println("TSM>>>>>>>>>>>>>>>");
								if (!userbo.isWithFleet())
									hmRO.put("RO_MAP", ToStrArray(ftBO.getConsoleReports_array()));
								else
									hmRO.put("RO_MAP", getUserSites(userId));
							}
							hmRO.put("FT_TOKEN", userbo.getFtToken());
							// System.out.println("hmro IS >> " + hmRO);
							mem.put(userId, hmRO);
						}
					} else {
						System.out.println("ErrOR No data===");
						userbo = null;
					}
				}
			}
			// //System.out.println(userbo.getRoleName());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				dbc.closeConnection(conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return userbo;
	}

	private String ToStrArray(ArrayList<consoleReports_array> obj) {
		// System.out.println("No Fleet");
		StringBuffer bf = new StringBuffer();
		String sites = null;
		try {
			if (obj != null) {
				int count, i = 0;
				count = obj.size();
				bf.append("'");
				for (consoleReports_array tmpObj : obj) {
					bf.append(tmpObj.getRO());
					i++;
					if (i < count)
						bf.append(",");
				}
				bf.append("'");
			}

			String result = bf.toString();
			result = result.replaceAll(",", "','");

			// System.out.println("siteID maping for user >>> " + result);
			if (result.contains("'")) {
				String SQL = " SELECT string_agg(\"SITE_ID\"::text, ',') AS \"SITEID\" FROM " + schema + ".\"MS_SITE\" "
						+ " WHERE \"SITE_ID\" IN (" + result + ") ";
				Statement stm = conn.createStatement();
				ResultSet rs = stm.executeQuery(SQL);
				while (rs.next()) {
					sites = rs.getString("SITEID");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sites;
	}

	private String getQuery(List<NameValuePair> params) throws UnsupportedEncodingException {
		StringBuilder result = new StringBuilder();
		boolean first = true;

		for (NameValuePair pair : params) {
			if (first)
				first = false;
			else
				result.append("&");

			result.append(URLEncoder.encode(pair.getName(), "UTF-8"));
			result.append("=");
			result.append(pair.getValue());
		}
		return result.toString();
	}

	/**
	 * 
	 * @param params
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	private String getDataString(HashMap<String, String> params) throws UnsupportedEncodingException {
		StringBuilder result = new StringBuilder();
		boolean first = true;
		for (Map.Entry<String, String> entry : params.entrySet()) {
			if (first)
				first = false;
			else
				result.append("&");
			result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
			result.append("=");
			result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
		}
		return result.toString();
	}

	/**
	 * 
	 * @return
	 */
	public JSONArray getUserCountry(String userId) {
		JSONArray json = new JSONArray();
		JSONArray prdjson = new JSONArray();
		try {
			String SQL = null;
			String siteIds = null;
			InMem mem = InMem.getInstance();
			HashMap hm = (HashMap) mem.get(userId);
			StringBuffer bf = new StringBuffer();
			if (hm != null) {
				bf.append("'");
				bf.append((String) hm.get("RO_MAP"));
				bf.append("'");
				siteIds = bf.toString();
				siteIds = siteIds.replaceAll(",", "','");

				SQL = "  SELECT CNT.\"COUNTRY\",CNT.\"CURRENCY_CODE\" FROM " + schema + ".\"MS_SITE\" MS "
						+ " INNER JOIN \"BCT\".\"MS_COUNTRY\" CNT ON CNT.\"COUNTRY\" = MS.\"COUNTRY\" " + " WHERE 1=1 "
						+ " AND MS.\"SITE_ID\" IN (" + siteIds + ")";
				SQL += " GROUP BY CNT.\"COUNTRY\",CNT.\"CURRENCY_CODE\" ";

				// System.out.println(SQL);

				Statement prdstm = conn.createStatement();
				ResultSet prdrs = prdstm.executeQuery(SQL);
				prdjson = dbc.parseRS(prdrs);
				json.add(prdjson);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("ErrOR @ UserDAO-getUserCountry ::" + ex.getMessage());
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
