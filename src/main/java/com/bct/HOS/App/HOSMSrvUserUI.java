package com.bct.HOS.App;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.security.PermitAll;
import javax.servlet.ServletContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import com.bct.HOS.App.BO.ResponseBO;
import com.bct.HOS.App.BO.UserBO;
import com.bct.HOS.App.BO.WidgetData;
import com.bct.HOS.App.DAO.AlarmsDAO;
import com.bct.HOS.App.DAO.HierarchyFT;
import com.bct.HOS.App.DAO.NotificationDAO;
import com.bct.HOS.App.DAO.SiteStatusDAO;
import com.bct.HOS.App.DAO.UserDAO;
import com.bct.HOS.App.utils.HOSConfig;
import com.bct.HOS.App.utils.InMem;
import com.bct.HOS.App.utils.RequestLogger;
import com.bct.HOS.App.utils.RestUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@Path("/v1")
public class HOSMSrvUserUI {

	/*
	 * User Management
	 */
	@PermitAll
	@POST
	@RequestLogger
	@Consumes({ "application/json" })
	@Path("/auth/")
	public Response UserAuth(@Context HttpHeaders headers, @Context UriInfo ui, @Context ServletContext context,
			String reqJsonString) {
		String str = null;
		String token = context.getInitParameter("authKey");
		String htoken = null;
		Gson gson = new Gson();

		ResponseBO responseObj = new ResponseBO();
		try {

			if (headers.getRequestHeader("token") != null && headers.getRequestHeader("token").size() > 0)
				htoken = headers.getRequestHeader("token").get(0);

			if (htoken != null) {
				if (htoken.equalsIgnoreCase(token)) {

					/**
					 * 1. Call FuelTrans for authenticate 2. Check table for widget data, If NULL
					 * <insert default json for role> Else <Fetch the data> 3. Append the widget
					 * data with FuelTrans response
					 */
					UserBO userBo = new UserBO();
					UserDAO userDAO = new UserDAO();
					HOSConfig config = new HOSConfig();
					String widgetDataStr = null;
					WidgetData widgetData = null;
					String userId = null;
					String password = null;
					String roleId = null;
					String userName = null;
					String roleName = null;
					String key = null;
					boolean isToken = false;
					boolean fm=false;
					String strToken = null;

					userBo = gson.fromJson(reqJsonString, UserBO.class);
					userId = userBo.getLoginId();
					password = userBo.getPassword();
					isToken = userBo.isToken();
					strToken = userBo.getStrToken();
					
					//System.out.println("reqJsonString>>"+reqJsonString);
					//System.out.println(isToken);
					//System.out.println(strToken);

					UserBO userBoFT = userDAO.callFTAuthService(userId, password, config.getValue("FT_URL"),strToken,isToken);
					if (userBoFT != null) {
						roleId = userBoFT.getRoleId();
						roleName = userBoFT.getRoleName();
						userName = userBoFT.getLoginId();
						fm = userBoFT.isWithFleet();

						if (roleName != null) {
							Map<String, String> siteDetails = null;
							String siteName = null;
							String isAutomated = "Y";
							if (roleName.equalsIgnoreCase("RO") || roleId.equalsIgnoreCase("RO")) {
								siteDetails = new SiteStatusDAO().getSiteDetails("'"+userId+"'", null, null, null, null, null);
								siteName = siteDetails.get("SITE_NAME");
								isAutomated = siteDetails.get("IS_AUTOMATED");
								//System.out.println("siteName====="+siteName);
							}
							InMem mem = InMem.getInstance();
							HashMap hm = (HashMap) mem.get(userId);
							//System.out.println("RO_MAP: " + hm.get("RO_MAP"));
							//System.out.println("HIERARCHY: " + hm.get("HIERARCHY"));
							//System.out.println("FT_TOKEN: " + hm.get("FT_TOKEN"));
							
							if (roleId != null) {
								widgetDataStr = userDAO.getUserWidget(userId, roleId, hm.get("RO_MAP").toString(),fm);
							}
							//System.out.println("widgetDataStr == " + widgetDataStr);
							if (widgetDataStr == null) {
								//System.out.println("widgetDataStr is NULL.");
								if (roleName.equalsIgnoreCase("CXO")) {
									key = "CXO";
								} else if (roleName.equalsIgnoreCase("RO") || roleId.equalsIgnoreCase("RO")) {
									key = "RO";
								} else {
									key = "TSM";
								}

								widgetDataStr = config.getValue(key);
								//System.out.println("widgetDataStr from prop :" + widgetDataStr);
								widgetDataStr = userDAO.createUserWidget(userId, userName, roleId, roleName,
										widgetDataStr);
								//System.out.println("widgetDataStr after create :" + widgetDataStr);
							}
							

							if (widgetDataStr != null)
								widgetData = gson.fromJson(widgetDataStr, WidgetData.class);
							else
								widgetData = new WidgetData();
							//System.out.println("widgetDataStr ::" + widgetData.getWidgetid());

							if(userBoFT.getUserEmail()!=null) {
								String email = userBoFT.getUserEmail().replaceAll("(^[^@]{3}|(?!^)\\G)[^@]", "$1*");
								userBo.setUserEmail(email);
							}else
								userBo.setUserEmail("");
							
							if(userBoFT.getUserMobile()!=null) {
								String inputPhoneNum = userBoFT.getUserMobile().replaceAll("\\d(?=(?:\\D*\\d){4})", "*");
								userBo.setUserMobile(inputPhoneNum);
							}else
								userBo.setUserMobile("");
							
							userBo.setRoleId(roleId);
							userBo.setUserId(userId);
							userBo.setUserName(userName);
							userBo.setSiteName(siteName);
							userBo.setRoleName(roleName);
							userBo.setWidgetData(widgetData);
							userBo.setSiteAutomated(isAutomated);
							userBo.setPassword(null);
							userBo.setSiteID((String) hm.get("RO_MAP"));
							userBo.setUserMenu(userBoFT.getUserMenu());
							userBo.setFtToken((String) hm.get("FT_TOKEN"));
							ArrayList data = new ArrayList();
							data.add(userBo);
							responseObj.setData(data);
						} else {
							responseObj.setError("Invalid User Name (or) Password @UserAuth");
							responseObj.setErrorCode("9995");
							responseObj.setStatus("ERROR");
						}
					} else {
						responseObj.setError("Invalid User Name (or) Password @UserAuth");
						responseObj.setErrorCode("9996");
						responseObj.setStatus("ERROR");
					}
				} else {
					responseObj.setError("Wrong Token @UserAuth");
					responseObj.setErrorCode("9997");
					responseObj.setStatus("ERROR");
				}
			} else {
				responseObj.setError("Required header detail missing @UserAuth");
				responseObj.setErrorCode("9998");
				responseObj.setStatus("ERROR");
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			responseObj.setError("Unexpected Error @UserAuth");
			responseObj.setErrorCode("9999");
			responseObj.setStatus("ERROR");
		}

		str = gson.toJson(responseObj);
		return RestUtils.getReponse(Response.Status.CREATED, str, "application/json");
	}
	
	
	@POST
	@Consumes({ "application/json" })
	@Path("/auth20/")
	public Response UserAuth4rDemo(@Context HttpHeaders headers, @Context UriInfo ui, @Context ServletContext context,
			String reqJsonString) {
		String str = null;
		String token = context.getInitParameter("authKey");
		String htoken = null;
		Gson gson = new Gson();

		ResponseBO responseObj = new ResponseBO();
		try {

			if (headers.getRequestHeader("token") != null && headers.getRequestHeader("token").size() > 0)
				htoken = headers.getRequestHeader("token").get(0);

			if (htoken != null) {
				if (htoken.equalsIgnoreCase(token)) {

					UserBO userBo = new UserBO();
					UserDAO userDAO = new UserDAO();
					HOSConfig config = new HOSConfig();
					String widgetDataStr = null;
					WidgetData widgetData = null;
					String userId = null;
					String password = null;
					String roleId = null;
					String userName = null;
					String roleName = null;
					String key = null;
					boolean isToken = false;
					boolean fm=false;
					String strToken = null;

					userBo = gson.fromJson(reqJsonString, UserBO.class);
					userId = userBo.getLoginId();
					password = userBo.getPassword();
					isToken = userBo.isToken();
					strToken = userBo.getStrToken();
					
					//System.out.println("reqJsonString>>"+reqJsonString);
					//System.out.println(isToken);
					//System.out.println(strToken);

					UserBO userBoFT = new UserBO();
					userBoFT.setWithFleet(false);
					userBoFT.setUserEmail("test@bahwancybertek.com");
					userBoFT.setUserMobile("1234567890");
					if(userId.contains("TSM")) {
						userBoFT.setRoleName("TSM");
						userBoFT.setRoleId("TSM");
					}else if(userId.contains("CXO")) {
						userBoFT.setRoleName("CXO");
						userBoFT.setRoleId("CXO");
					}else {
						userBoFT.setRoleName("RO");
						userBoFT.setRoleId("RO");
					}
					userBoFT.setUserName(userId);
					userBoFT.setFtToken("-123456789");

					if (userBoFT != null) {
						roleId = userBoFT.getRoleId();
						roleName = userBoFT.getRoleName();
						userName = userBoFT.getLoginId();
						fm = userBoFT.isWithFleet();
						
						JSONArray menuArray = new JSONArray();
						JSONObject menuObj = null;
						
						menuObj = new JSONObject();
						menuObj.put("SLNO", "1");
						menuObj.put("SEQ_NO", "5");
						menuObj.put("menuText", "Product Inventory");
						menuObj.put("screenModule", "Reports");
						menuObj.put("screenSubModule", "Transaction Report");
						menuObj.put("menuIcon", "WetStock.png");
						menuObj.put("searchJS", "WetStock");
						menuObj.put("entityType", "HOS");
						menuArray.add(menuObj);
						
						menuObj = new JSONObject();
						menuObj.put("SLNO", "2");
						menuObj.put("SEQ_NO", "6");
						menuObj.put("menuText", "Sales");
						menuObj.put("screenModule", "Reports");
						menuObj.put("screenSubModule", "Transaction Report");
						menuObj.put("menuIcon", "Sales.png");
						menuObj.put("searchJS", "Sales");
						menuObj.put("entityType", "HOS");
						menuArray.add(menuObj);
						
						menuObj = new JSONObject();
						menuObj.put("SLNO", "3");
						menuObj.put("SEQ_NO", "7");
						menuObj.put("menuText", "Nil Sales");
						menuObj.put("screenModule", "Reports");
						menuObj.put("screenSubModule", "Transaction Report");
						menuObj.put("menuIcon", "NilSales.png");
						menuObj.put("searchJS", "Nil Sales");
						menuObj.put("entityType", "HOS");
						menuArray.add(menuObj);
						
						menuObj = new JSONObject();
						menuObj.put("SLNO", "4");
						menuObj.put("SEQ_NO", "8");
						menuObj.put("menuText", "Alarm Report");
						menuObj.put("screenModule", "Reports");
						menuObj.put("screenSubModule", "Transaction Report");
						menuObj.put("menuIcon", "AlarmReport.png");
						menuObj.put("searchJS", "Alarm Report");
						menuObj.put("entityType", "HOS");
						menuArray.add(menuObj);
						
						menuObj = new JSONObject();
						menuObj.put("SLNO", "5");
						menuObj.put("SEQ_NO", "9");
						menuObj.put("menuText", "Irregular Totalizer Report");
						menuObj.put("screenModule", "Reports");
						menuObj.put("screenSubModule", "Transaction Report");
						menuObj.put("menuIcon", "IrregularTotalizerReport.png");
						menuObj.put("searchJS", "Irregular Totalizer Report");
						menuObj.put("entityType", "HOS");
						menuArray.add(menuObj);
						
						menuObj = new JSONObject();
						menuObj.put("SLNO", "6");
						menuObj.put("SEQ_NO", "10");
						menuObj.put("menuText", "RO Status Report");
						menuObj.put("screenModule", "Reports");
						menuObj.put("screenSubModule", "Transaction Report");
						menuObj.put("menuIcon", "ROStatusReport.png");
						menuObj.put("searchJS", "RO Status Report");
						menuObj.put("entityType", "HOS");
						menuArray.add(menuObj);
						
						menuObj = new JSONObject();
						menuObj.put("SLNO", "7");
						menuObj.put("SEQ_NO", "11");
						menuObj.put("menuText", "Device Status Report");
						menuObj.put("screenModule", "Reports");
						menuObj.put("screenSubModule", "Transaction Report");
						menuObj.put("menuIcon", "DeviceStatusReport.png");
						menuObj.put("searchJS", "Device Status Report");
						menuObj.put("entityType", "HOS");
						menuArray.add(menuObj);
						
						menuObj = new JSONObject();
						menuObj.put("SLNO", "8");
						menuObj.put("SEQ_NO", "12");
						menuObj.put("menuText", "Price Change Report");
						menuObj.put("screenModule", "Reports");
						menuObj.put("screenSubModule", "Transaction Report");
						menuObj.put("menuIcon", "PriceChangeReport.png");
						menuObj.put("searchJS", "Price Change Report");
						menuObj.put("entityType", "HOS");
						menuArray.add(menuObj);
						
						menuObj = new JSONObject();
						menuObj.put("SLNO", "9");
						menuObj.put("SEQ_NO", "13");
						menuObj.put("menuText", "RO Configuration Report");
						menuObj.put("screenModule", "Reports");
						menuObj.put("screenSubModule", "Transaction Report");
						menuObj.put("menuIcon", "ROConfigurationReport.png");
						menuObj.put("searchJS", "RO Configuration Report");
						menuObj.put("entityType", "HOS");
						menuArray.add(menuObj);
						
						menuObj = new JSONObject();
						menuObj.put("SLNO", "10");
						menuObj.put("SEQ_NO", "14");
						menuObj.put("menuText", "Sales Comparison Chart");
						menuObj.put("screenModule", "Reports");
						menuObj.put("screenSubModule", "Analytical Report");
						menuObj.put("menuIcon", "SalesComparisonhart.png");
						menuObj.put("searchJS", "Sales Comparison Chart");
						menuObj.put("entityType", "HOS");
						menuArray.add(menuObj);
						
						menuObj = new JSONObject();
						menuObj.put("SLNO", "11");
						menuObj.put("SEQ_NO", "15");
						menuObj.put("menuText", "Inventory turnover Report");
						menuObj.put("screenModule", "Reports");
						menuObj.put("screenSubModule", "Analytical Report");
						menuObj.put("menuIcon", "InventoryturnoverReport.png");
						menuObj.put("searchJS", "Inventory turnover Report");
						menuObj.put("entityType", "HOS");
						menuArray.add(menuObj);
						
						menuObj = new JSONObject();
						menuObj.put("SLNO", "12");
						menuObj.put("SEQ_NO", "16");
						menuObj.put("menuText", "Alarm");
						menuObj.put("screenModule", "Alarm & Notification");
						menuObj.put("screenSubModule", "Alarm & Notification");
						menuObj.put("menuIcon", "Alarm.png");
						menuObj.put("searchJS", "Alarm");
						menuObj.put("entityType", "HOS");
						menuArray.add(menuObj);
						
						menuObj = new JSONObject();
						menuObj.put("SLNO", "13");
						menuObj.put("SEQ_NO", "17");
						menuObj.put("menuText", "Password");
						menuObj.put("screenModule", "Configuration");
						menuObj.put("screenSubModule", "Configuration");
						menuObj.put("menuIcon", "Password.png");
						menuObj.put("searchJS", "Password");
						menuObj.put("entityType", "HOS");
						menuArray.add(menuObj);
						
						
						userBoFT.setUserMenu(menuArray);
						
						if (roleName != null) {
							String siteName = null;
							if (roleName.equalsIgnoreCase("RO") || roleId.equalsIgnoreCase("RO")) {
								siteName = new SiteStatusDAO().getSiteDetail("'"+userId+"'", null, null, null, null, null);
								//System.out.println("siteName====="+siteName);
							}
							
							InMem mem = InMem.getInstance();
							HashMap hmRO = new HashMap();
							hmRO.put("RO_MAP", new UserDAO().getUserSites(userId));
							mem.put(userId, hmRO);
					
							HashMap hm = (HashMap) mem.get(userId);
							
							//System.out.println("RO_MAP: " + hm.get("RO_MAP"));
							//System.out.println("HIERARCHY: " + hm.get("HIERARCHY"));
							//System.out.println("FT_TOKEN: " + hm.get("FT_TOKEN"));
							
							if (roleId != null) {
								widgetDataStr = userDAO.getUserWidget(userId, roleId, hm.get("RO_MAP").toString(),fm);
							}
							//System.out.println("widgetDataStr == " + widgetDataStr);
							if (widgetDataStr == null) {
								//System.out.println("widgetDataStr is NULL.");
								if (roleName.equalsIgnoreCase("CXO")) {
									key = "CXO";
								} else if (roleName.equalsIgnoreCase("RO") || roleId.equalsIgnoreCase("RO")) {
									key = "RO";
								} else {
									key = "TSM";
								}

								widgetDataStr = config.getValue(key);
								//System.out.println("widgetDataStr from prop :" + widgetDataStr);
								widgetDataStr = userDAO.createUserWidget(userId, userName, roleId, roleName,
										widgetDataStr);
								//System.out.println("widgetDataStr after create :" + widgetDataStr);
							}
							

							if (widgetDataStr != null)
								widgetData = gson.fromJson(widgetDataStr, WidgetData.class);
							else
								widgetData = new WidgetData();
							//System.out.println("widgetDataStr ::" + widgetData.getWidgetid());

							if(userBoFT.getUserEmail()!=null)
								userBo.setUserEmail(userBoFT.getUserEmail());
							else
								userBo.setUserEmail("");
							
							if(userBoFT.getUserMobile()!=null)
								userBo.setUserMobile(userBoFT.getUserMobile());
							else
								userBo.setUserMobile("");
							
							userBo.setRoleId(roleId);
							userBo.setUserId(userId);
							userBo.setUserName(userName);
							userBo.setSiteName(siteName);
							userBo.setRoleName(roleName);
							userBo.setWidgetData(widgetData);
							userBo.setPassword(null);
							userBo.setSiteID((String) hm.get("RO_MAP"));
							userBo.setUserMenu(userBoFT.getUserMenu());
							userBo.setFtToken((String) hm.get("FT_TOKEN"));
							ArrayList data = new ArrayList();
							data.add(userBo);
							responseObj.setData(data);
						} else {
							responseObj.setError("Invalid User Name (or) Password @UserAuth4rDemo");
							responseObj.setErrorCode("9999");
							responseObj.setStatus("ERROR");
						}
					} else {
						responseObj.setError("Invalid User Name (or) Password @UserAuth4rDemo");
						responseObj.setErrorCode("9999");
						responseObj.setStatus("ERROR");
					}
				} else {
					responseObj.setError("Wrong Token @UserAuth4rDemo");
					responseObj.setErrorCode("9997");
					responseObj.setStatus("ERROR");
				}
			} else {
				responseObj.setError("Required header detail missing @UserAuth4rDemo");
				responseObj.setErrorCode("9998");
				responseObj.setStatus("ERROR");
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			responseObj.setError("Unexpected Error @UserAuth4rDemo");
			responseObj.setErrorCode("9999");
			responseObj.setStatus("ERROR");
		}

		str = gson.toJson(responseObj);
		return RestUtils.getReponse(Response.Status.CREATED, str, "application/json");
	}

	@POST
	@Consumes({ "application/json" })
	@Path("/getwidget/")
	public Response GetUserWidget(@Context HttpHeaders headers, @Context UriInfo ui, @Context ServletContext context,
			String reqJsonString) {
		String str = null;
		String token = context.getInitParameter("authKey");
		String htoken = null;
		Gson gson = new Gson();
		UserBO userBo = new UserBO();

		ResponseBO responseObj = new ResponseBO();
		try {

			if (headers.getRequestHeader("token") != null && headers.getRequestHeader("token").size() > 0)
				htoken = headers.getRequestHeader("token").get(0);

			if (htoken != null) {
				if (htoken.equalsIgnoreCase(token)) {
					/**
					 * Fetch from DB - user widget data
					 */
					userBo = gson.fromJson(reqJsonString, UserBO.class);
					UserDAO userDAO = new UserDAO();
					String widgetDataStr = null;
					WidgetData widgetData = null;
					String userId = userBo.getUserId();
					String roleId = userBo.getRoleId();
					widgetDataStr = userDAO.getUserWidget(userId, roleId,null,false);
					widgetData = gson.fromJson(widgetDataStr, WidgetData.class);

					UserBO userBoRes = new UserBO();
					userBoRes.setRoleId(roleId);
					userBoRes.setUserId(userId);
					userBoRes.setWidgetData(widgetData);
					ArrayList data = new ArrayList();
					data.add(userBoRes);
					responseObj.setData(data);

				} else {
					responseObj.setError("Wrong Token @GetUserWidget");
					responseObj.setErrorCode("9997");
					responseObj.setStatus("ERROR");
				}
			} else {
				responseObj.setError("Required header detail missing @GetUserWidget");
				responseObj.setErrorCode("9998");
				responseObj.setStatus("ERROR");
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			responseObj.setError("Unexpected Error @GetUserWidget");
			responseObj.setErrorCode("9999");
			responseObj.setStatus("ERROR");
		}

		str = gson.toJson(responseObj);
		return RestUtils.getReponse(Response.Status.CREATED, str, "application/json");
	}

	@POST
	@Consumes({ "application/json" })
	@Path("/updatewidget/")
	public Response UserWidgetUpdate(@Context HttpHeaders headers, @Context UriInfo ui, @Context ServletContext context,
			String reqJsonString) {
		String str = null;
		String token = context.getInitParameter("authKey");
		String htoken = null;
		Gson gson = new GsonBuilder().serializeNulls().create();
		UserBO userBO = new UserBO();

		ResponseBO responseObj = new ResponseBO();
		try {

			if (headers.getRequestHeader("token") != null && headers.getRequestHeader("token").size() > 0)
				htoken = headers.getRequestHeader("token").get(0);

			if (htoken != null) {
				if (htoken.equalsIgnoreCase(token)) {
					/**
					 * Update table - widget data
					 */
					userBO = gson.fromJson(reqJsonString, UserBO.class);

					UserDAO userDAO = new UserDAO();
					WidgetData widgetData = userBO.getWidgetData();
					String widgetDataJson = gson.toJson(widgetData);
					String userId = userBO.getUserId();
					String roleId = userBO.getRoleId();
					if (userDAO.updateUserWidget(userId, roleId, widgetDataJson)) {
						responseObj.setError(null);
						responseObj.setErrorCode("0000");
						responseObj.setStatus("Success");
					} else {
						responseObj.setError("Unable to store the data");
						responseObj.setErrorCode("1111");
						responseObj.setStatus("Failed");
					}
				} else {
					responseObj.setError("Wrong Token @UserWidgetUpdate");
					responseObj.setErrorCode("9997");
					responseObj.setStatus("ERROR");
				}
			} else {
				responseObj.setError("Required header detail missing @UserWidgetUpdate");
				responseObj.setErrorCode("9998");
				responseObj.setStatus("ERROR");
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			responseObj.setError("Unexpected Error @UserWidgetUpdate");
			responseObj.setErrorCode("9999");
			responseObj.setStatus("ERROR");
		}

		str = gson.toJson(responseObj);
		return RestUtils.getReponse(Response.Status.CREATED, str, "application/json");
	}

	@POST
	@Consumes({ "application/json" })
	@Path("/alarms/")
	public Response UserAlarms(@Context HttpHeaders headers, @Context UriInfo ui, @Context ServletContext context,
			String reqJsonString) {
		String str = null;
		String token = context.getInitParameter("authKey");
		String htoken = null;
		Gson gson = new Gson();
		JSONArray alarmsData = new JSONArray();
		JSONArray notificationData = new JSONArray();
		ResponseBO responseObj = new ResponseBO();
		try {

			if (headers.getRequestHeader("token") != null && headers.getRequestHeader("token").size() > 0)
				htoken = headers.getRequestHeader("token").get(0);

			if (htoken != null) {
				if (htoken.equalsIgnoreCase(token)) {
					String userId = null;
					String roleId = null;
					String limit = null;
					String country = null;
					String siteIDs = null;
					String fromDate = null;
					String toDate = null;
					String state = null;
					String region = null;
					String district = null;
					String city = null;
					String pump = null;
					String tank = null;
					String nozzle = null;
					String notifyType = null;

					UserBO usrBO = gson.fromJson(reqJsonString, UserBO.class);
					userId = usrBO.getUserId();
					roleId = usrBO.getRoleId();
					limit = usrBO.getRecordCount();
					country = usrBO.getCountry();
					siteIDs = usrBO.getSiteID();
					fromDate = usrBO.getFromDate();
					toDate = usrBO.getToDate();
					state = usrBO.getState();
					region = usrBO.getRegion();
					district = usrBO.getDistrict();
					city = usrBO.getCity();
					pump = usrBO.getPump();
					tank = usrBO.getTank();
					nozzle = usrBO.getNozzle();
					notifyType = usrBO.getNotifyType();

					if (siteIDs == null)
						siteIDs = new HierarchyFT().getUserHierarchyFT(userId, roleId, country);
					//System.out.println("siteIDs:" + siteIDs);
					if (siteIDs != null) {
						alarmsData = new JSONArray();
						alarmsData = new AlarmsDAO().getAlarms(siteIDs, limit, fromDate, toDate, country, state, region,
								district, city, pump, tank, nozzle);
						responseObj.setAlarms(alarmsData);

						notificationData = new NotificationDAO().getNotifications(siteIDs, notifyType, limit);
						notificationData = new JSONArray();
						responseObj.setNotifications(notificationData);

						responseObj.setError(null);
						responseObj.setErrorCode("0000");
						responseObj.setStatus("Success");
					} else {
						responseObj.setError("No Data!.");
						responseObj.setErrorCode("0001");
						responseObj.setStatus("Success");
					}
				} else {
					responseObj.setError("Wrong Token @UserAlarms");
					responseObj.setErrorCode("9997");
					responseObj.setStatus("ERROR");
				}
			} else {
				responseObj.setError("Required header detail missing @UserAlarms");
				responseObj.setErrorCode("9998");
				responseObj.setStatus("ERROR");
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			responseObj.setError("Unexpected Error @UserAlarms");
			responseObj.setErrorCode("9999");
			responseObj.setStatus("ERROR");
		}

		str = gson.toJson(responseObj);
		return RestUtils.getReponse(Response.Status.CREATED, str, "application/json");
	}
	
	
	@POST
	@Consumes({ "application/json" })
	@Path("/getNotifications/")
	public Response UserNotifications(@Context HttpHeaders headers, @Context UriInfo ui, @Context ServletContext context,
			String reqJsonString) {
		String str = null;
		String token = context.getInitParameter("authKey");
		String htoken = null;
		Gson gson = new Gson();
		JSONArray alarmsData = new JSONArray();
		JSONArray notificationData = new JSONArray();
		ResponseBO responseObj = new ResponseBO();
		try {

			if (headers.getRequestHeader("token") != null && headers.getRequestHeader("token").size() > 0)
				htoken = headers.getRequestHeader("token").get(0);

			if (htoken != null) {
				if (htoken.equalsIgnoreCase(token)) {
					String userId = null;
					String roleId = null;
					String limit = null;
					String country = null;
					String siteIDs = null;
					String fromDate = null;
					String toDate = null;
					String notifyType = null;
					boolean isGrouping = false;

					UserBO usrBO = gson.fromJson(reqJsonString, UserBO.class);
					userId = usrBO.getUserId();
					roleId = usrBO.getRoleId();
					limit = usrBO.getRecordCount();
					country = usrBO.getCountry();
					siteIDs = usrBO.getSiteID();
					fromDate = usrBO.getFromDate();
					toDate = usrBO.getToDate();
					notifyType = usrBO.getNotifyType();
					isGrouping=usrBO.isGrouping();

					if (siteIDs == null)
						siteIDs = new HierarchyFT().getUserHierarchyFT(userId, roleId, country);
					//System.out.println("siteIDs:" + siteIDs);
					if (siteIDs != null) {
						notificationData = new NotificationDAO().getNotificationGroup(siteIDs, notifyType,isGrouping, limit, fromDate, toDate);
						responseObj.setNotifications(notificationData);

						responseObj.setError(null);
						responseObj.setErrorCode("0000");
						responseObj.setStatus("Success");
					} else {
						responseObj.setError("No Data!.");
						responseObj.setErrorCode("0001");
						responseObj.setStatus("Success");
					}
				} else {
					responseObj.setError("Wrong Token @UserNotifications");
					responseObj.setErrorCode("9997");
					responseObj.setStatus("ERROR");
				}
			} else {
				responseObj.setError("Required header detail missing @UserNotifications");
				responseObj.setErrorCode("9998");
				responseObj.setStatus("ERROR");
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			responseObj.setError("Unexpected Error @UserNotifications");
			responseObj.setErrorCode("9999");
			responseObj.setStatus("ERROR");
		}

		str = gson.toJson(responseObj);
		return RestUtils.getReponse(Response.Status.CREATED, str, "application/json");
	}
	
	
	@POST
	@Consumes({ "application/json" })
	@Path("/actNotifications/")
	public Response AckNotifications(@Context HttpHeaders headers, @Context UriInfo ui, @Context ServletContext context,
			String reqJsonString) {
		String str = null;
		String token = context.getInitParameter("authKey");
		String htoken = null;
		Gson gson = new Gson();
		JSONArray alarmsData = new JSONArray();
		JSONArray notificationData = new JSONArray();
		ResponseBO responseObj = new ResponseBO();
		try {

			if (headers.getRequestHeader("token") != null && headers.getRequestHeader("token").size() > 0)
				htoken = headers.getRequestHeader("token").get(0);

			if (htoken != null) {
				if (htoken.equalsIgnoreCase(token)) {

					String siteID = null;
					String date = null;
					String notifyType = null;
					String param1 = null;
					String param2 = null;
					String param3 = null;
					String param4 = null;
					String param5 = null;
					String param6 = null;
					String param7 = null;
					String param8 = null;

					UserBO usrBO = gson.fromJson(reqJsonString, UserBO.class);

					siteID = usrBO.getSiteID();
					date = usrBO.getDate();
					notifyType = usrBO.getNotifyType();
					param1=usrBO.getParam1();
					param2=usrBO.getParam2();
					param3=usrBO.getParam3();
					param4=usrBO.getParam4();
					param5=usrBO.getParam5();
					param6=usrBO.getParam6();
					param7=usrBO.getParam7();
					param8=usrBO.getParam8();
					//System.out.println("siteIDs:" + siteIDs);
					if (siteID != null) {
						notificationData = new NotificationDAO().updateNotificationGroup(siteID, notifyType, date, param1, param2, param3, param4, param5, param6, param7, param8);
						responseObj.setNotifications(notificationData);

						responseObj.setError(null);
						responseObj.setErrorCode("0000");
						responseObj.setStatus("Success");
					} else {
						responseObj.setError("No Data!.");
						responseObj.setErrorCode("0001");
						responseObj.setStatus("Success");
					}
				} else {
					responseObj.setError("Wrong Token @AckNotifications");
					responseObj.setErrorCode("9997");
					responseObj.setStatus("ERROR");
				}
			} else {
				responseObj.setError("Required header detail missing @AckNotifications");
				responseObj.setErrorCode("9998");
				responseObj.setStatus("ERROR");
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			responseObj.setError("Unexpected Error @AckNotifications");
			responseObj.setErrorCode("9999");
			responseObj.setStatus("ERROR");
		}

		str = gson.toJson(responseObj);
		return RestUtils.getReponse(Response.Status.CREATED, str, "application/json");
	}

	@POST
	@Consumes({ "application/json" })
	@Path("/getSiteProducts/")
	public Response getSiteProducts(@Context HttpHeaders headers, @Context UriInfo ui, @Context ServletContext context,
			String reqJsonString) {
		String str = null;
		String token = context.getInitParameter("authKey");
		String htoken = null;
		Gson gson = new Gson();
		UserBO userBo = new UserBO();
		JSONArray dataSet = new JSONArray();
		ResponseBO responseObj = new ResponseBO();
		try {

			if (headers.getRequestHeader("token") != null && headers.getRequestHeader("token").size() > 0)
				htoken = headers.getRequestHeader("token").get(0);

			if (htoken != null) {
				if (htoken.equalsIgnoreCase(token)) {
					String userId = null;
					String roleId = null;
					String status = null;
					String country = null;
					String siteIDs = null;

					UserBO usrBO = gson.fromJson(reqJsonString, UserBO.class);
					//System.out.println("reqJsonString(getSiteProducts)---" + reqJsonString);
					userId = usrBO.getUserId();
					roleId = usrBO.getRoleId();
					status = usrBO.getStatus();
					country = usrBO.getCountry();
					siteIDs = usrBO.getSiteID();

					//System.out.println("country=" + country);
					if (siteIDs == null)
						siteIDs = new HierarchyFT().getUserHierarchyFT(userId, roleId, country);
					//System.out.println("siteIDs:" + siteIDs);
					if (siteIDs != null) {
						dataSet = new SiteStatusDAO().getSiteProducts(siteIDs, country, userId);
						responseObj.setDataSet(dataSet);
						responseObj.setError(null);
						responseObj.setErrorCode("0000");
						responseObj.setStatus("Success");
					} else {
						responseObj.setError("No Data!.");
						responseObj.setErrorCode("0001");
						responseObj.setStatus("Success");
					}
				} else {
					responseObj.setError("Wrong Token @getSiteProducts");
					responseObj.setErrorCode("9997");
					responseObj.setStatus("ERROR");
				}
			} else {
				responseObj.setError("Required header detail missing @getSiteProducts");
				responseObj.setErrorCode("9998");
				responseObj.setStatus("ERROR");
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			responseObj.setError("Unexpected Error @getSiteProducts");
			responseObj.setErrorCode("9999");
			responseObj.setStatus("ERROR");
		}

		str = gson.toJson(responseObj);
		return RestUtils.getReponse(Response.Status.CREATED, str, "application/json");
	}

	@POST
	@Consumes({ "application/json" })
	@Path("/getUserCountryMapping/")
	public Response getUserCountryMapping(@Context HttpHeaders headers, @Context UriInfo ui,
			@Context ServletContext context, String reqJsonString) {
		String str = null;
		String token = context.getInitParameter("authKey");
		String htoken = null;
		Gson gson = new Gson();
		UserBO userBo = new UserBO();
		JSONArray dataSet = new JSONArray();
		ResponseBO responseObj = new ResponseBO();
		try {

			if (headers.getRequestHeader("token") != null && headers.getRequestHeader("token").size() > 0)
				htoken = headers.getRequestHeader("token").get(0);

			if (htoken != null) {
				if (htoken.equalsIgnoreCase(token)) {
					String userId = null;

					UserBO usrBO = gson.fromJson(reqJsonString, UserBO.class);
					userId = usrBO.getUserId();

					if (userId != null) {
						dataSet = new UserDAO().getUserCountry(userId);
						responseObj.setDataSet(dataSet);
						responseObj.setError(null);
						responseObj.setErrorCode("0000");
						responseObj.setStatus("Success");
					} else {
						responseObj.setError("No Data!.");
						responseObj.setErrorCode("0001");
						responseObj.setStatus("Success");
					}
				} else {
					responseObj.setError("Wrong Token @getUserCountryMapping");
					responseObj.setErrorCode("9997");
					responseObj.setStatus("ERROR");
				}
			} else {
				responseObj.setError("Required header detail missing @getUserCountryMapping");
				responseObj.setErrorCode("9998");
				responseObj.setStatus("ERROR");
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			responseObj.setError("Unexpected Error @getUserCountryMapping");
			responseObj.setErrorCode("9999");
			responseObj.setStatus("ERROR");
		}

		str = gson.toJson(responseObj);
		return RestUtils.getReponse(Response.Status.CREATED, str, "application/json");
	}

	@POST
	@Consumes({ "application/json" })
	@Path("/getSiteStatus/")
	public Response getSiteStatus(@Context HttpHeaders headers, @Context UriInfo ui, @Context ServletContext context,
			String reqJsonString) {
		String str = null;
		String token = context.getInitParameter("authKey");
		String htoken = null;
		Gson gson = new Gson();
		UserBO userBo = new UserBO();

		ResponseBO responseObj = new ResponseBO();
		try {

			if (headers.getRequestHeader("token") != null && headers.getRequestHeader("token").size() > 0)
				htoken = headers.getRequestHeader("token").get(0);

			if (htoken != null) {
				if (htoken.equalsIgnoreCase(token)) {
					String siteId = null;
					String status = null;

					userBo = gson.fromJson(reqJsonString, UserBO.class);
					siteId = userBo.getSiteID();
					status = userBo.getStatus();

					JSONArray dataSet = new SiteStatusDAO().getSiteStatus(siteId, status, null);
					responseObj.setDataSet(dataSet);
					responseObj.setGRID_COLUMN(dataSet);
					responseObj.setErrorCode("0000");
					responseObj.setStatus("");
				} else {
					responseObj.setError("Wrong Token @getSiteStatus");
					responseObj.setErrorCode("9997");
					responseObj.setStatus("ERROR");
				}
			} else {
				responseObj.setError("Required header detail missing @getSiteStatus");
				responseObj.setErrorCode("9998");
				responseObj.setStatus("ERROR");
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			responseObj.setError("Unexpected Error @getSiteStatus");
			responseObj.setErrorCode("9999");
			responseObj.setStatus("ERROR");
		}

		str = gson.toJson(responseObj);
		return RestUtils.getReponse(Response.Status.CREATED, str, "application/json");
	}

	@POST
	@Consumes({ "application/json" })
	@Path("/getNotifictionCount/")
	public Response getNotificationCount(@Context HttpHeaders headers, @Context UriInfo ui,
			@Context ServletContext context, String reqJsonString) {
		String str = null;
		String token = context.getInitParameter("authKey");
		String htoken = null;
		Gson gson = new Gson();
		JSONArray alarmsData = new JSONArray();
		JSONArray notificationData = new JSONArray();
		ResponseBO responseObj = new ResponseBO();
		try {

			if (headers.getRequestHeader("token") != null && headers.getRequestHeader("token").size() > 0)
				htoken = headers.getRequestHeader("token").get(0);

			if (htoken != null) {
				if (htoken.equalsIgnoreCase(token)) {
					String userId = null;
					String roleId = null;
					String limit = null;
					String country = null;
					String siteIDs = null;

					UserBO usrBO = gson.fromJson(reqJsonString, UserBO.class);
					userId = usrBO.getUserId();
					roleId = usrBO.getRoleId();
					limit = usrBO.getRecordCount();
					country = usrBO.getCountry();
					siteIDs = usrBO.getSiteID();

					if (siteIDs == null)
						siteIDs = new HierarchyFT().getUserHierarchyFT(userId, roleId, country);
					//System.out.println("siteIDs:" + siteIDs);
					if (siteIDs != null) {
						notificationData = new NotificationDAO().getNotificationCount(siteIDs);
						responseObj.setNotifications(notificationData);

						responseObj.setError(null);
						responseObj.setErrorCode("0000");
						responseObj.setStatus("Success");
					} else {
						responseObj.setError("No Data!.");
						responseObj.setErrorCode("0001");
						responseObj.setStatus("Success");
					}
				} else {
					responseObj.setError("Wrong Token @UserAlarms");
					responseObj.setErrorCode("9997");
					responseObj.setStatus("ERROR");
				}
			} else {
				responseObj.setError("Required header detail missing @UserAlarms");
				responseObj.setErrorCode("9998");
				responseObj.setStatus("ERROR");
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			responseObj.setError("Unexpected Error @UserAlarms");
			responseObj.setErrorCode("9999");
			responseObj.setStatus("ERROR");
		}

		str = gson.toJson(responseObj);
		return RestUtils.getReponse(Response.Status.CREATED, str, "application/json");
	}
	
	
	
	@POST
	@Consumes({ "application/json" })
	@Path("/ChangePassword/")
	public Response UserChangePassword(@Context HttpHeaders headers, @Context UriInfo ui, @Context ServletContext context,
			String reqJsonString) {
		String str = null;
		String token = context.getInitParameter("authKey");
		String htoken = null;
		Gson gson = new Gson();

		ResponseBO responseObj = new ResponseBO();
		try {

			if (headers.getRequestHeader("token") != null && headers.getRequestHeader("token").size() > 0)
				htoken = headers.getRequestHeader("token").get(0);

			if (htoken != null) {
				if (htoken.equalsIgnoreCase(token)) {

					UserBO userBo = new UserBO();
					UserDAO userDAO = new UserDAO();
					HOSConfig config = new HOSConfig();

					String userId = null;
					String password = null;
					String oldPassword = null;
					String confPassword = null;
					String email = null;
					String strToken = null;
					
					userBo = gson.fromJson(reqJsonString, UserBO.class);
					userId = userBo.getUserId();
					password = userBo.getPassword();
					oldPassword = userBo.getOldPassword();
					confPassword = userBo.getConfPassword();
					email = userBo.getEmail();
					
					InMem mem = InMem.getInstance();
					HashMap hm = (HashMap) mem.get(userId);
					strToken= (String) hm.get("FT_TOKEN");
					
					if(password.equals(confPassword)) {
						String strMsg = userDAO.changePSW(userId,strToken, password, oldPassword, confPassword, email, config.getValue("FT_URL"));
						responseObj.setMessage(strMsg);
					}else {
						responseObj.setError("Invalid New and Confirm Password @UserChangePassword");
						responseObj.setErrorCode("9999");
						responseObj.setStatus("ERROR");
					}
				} else {
					responseObj.setError("Wrong Token @UserChangePassword");
					responseObj.setErrorCode("9997");
					responseObj.setStatus("ERROR");
				}
			} else {
				responseObj.setError("Required header detail missing @UserChangePassword");
				responseObj.setErrorCode("9998");
				responseObj.setStatus("ERROR");
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			responseObj.setError("Unexpected Error @UserChangePassword");
			responseObj.setErrorCode("9999");
			responseObj.setStatus("ERROR");
		}

		str = gson.toJson(responseObj);
		return RestUtils.getReponse(Response.Status.CREATED, str, "application/json");
	}
	
	
	@POST
	@Consumes({ "application/json" })
	@Path("/PSWOTP/")
	public Response getPasswordOTP(@Context HttpHeaders headers, @Context UriInfo ui, @Context ServletContext context,
			String reqJsonString) {
		String str = null;
		String token = context.getInitParameter("authKey");
		String htoken = null;
		Gson gson = new Gson();

		ResponseBO responseObj = new ResponseBO();
		try {

			if (headers.getRequestHeader("token") != null && headers.getRequestHeader("token").size() > 0)
				htoken = headers.getRequestHeader("token").get(0);

			if (htoken != null) {
				if (htoken.equalsIgnoreCase(token)) {

					UserBO userBo = new UserBO();
					UserDAO userDAO = new UserDAO();
					HOSConfig config = new HOSConfig();

					String userId = null;

					userBo = gson.fromJson(reqJsonString, UserBO.class);
					userId = userBo.getUserId();
					
					if(userId!=null) {
						userDAO.getPSWOTP(responseObj,userId,config.getValue("FT_URL"));
						
					}else {
						responseObj.setError("Invalid New and Confirm Password @getPasswordOTP");
						responseObj.setErrorCode("9999");
						responseObj.setStatus("ERROR");
					}
				} else {
					responseObj.setError("Wrong Token @getPasswordOTP");
					responseObj.setErrorCode("9997");
					responseObj.setStatus("ERROR");
				}
			} else {
				responseObj.setError("Required header detail missing @getPasswordOTP");
				responseObj.setErrorCode("9998");
				responseObj.setStatus("ERROR");
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			responseObj.setError("Unexpected Error @getPasswordOTP");
			responseObj.setErrorCode("9999");
			responseObj.setStatus("ERROR");
		}

		str = gson.toJson(responseObj);
		return RestUtils.getReponse(Response.Status.CREATED, str, "application/json");
	}
	
	
	@POST
	@Consumes({ "application/json" })
	@Path("/ResetPassword/")
	public Response ResetPassword(@Context HttpHeaders headers, @Context UriInfo ui, @Context ServletContext context,
			String reqJsonString) {
		String str = null;
		String token = context.getInitParameter("authKey");
		String htoken = null;
		Gson gson = new Gson();

		ResponseBO responseObj = new ResponseBO();
		try {

			if (headers.getRequestHeader("token") != null && headers.getRequestHeader("token").size() > 0)
				htoken = headers.getRequestHeader("token").get(0);

			if (htoken != null) {
				if (htoken.equalsIgnoreCase(token)) {

					UserBO userBo = new UserBO();
					UserDAO userDAO = new UserDAO();
					HOSConfig config = new HOSConfig();

					String userId = null;
					String password = null;
					String confPassword = null;
					String otp = null;
					
					userBo = gson.fromJson(reqJsonString, UserBO.class);
					userId = userBo.getUserId();
					password = userBo.getPassword();
					confPassword = userBo.getConfPassword();
					otp = userBo.getOtp();

					if(password.equals(confPassword) && otp != null) {
						userDAO.ResetPassword(responseObj,userId, password, confPassword, otp, config.getValue("FT_URL"));
						
					}else {
						responseObj.setError("Invalid New and Confirm Password or Invalid OTP @ResetPassword");
						responseObj.setErrorCode("9999");
						responseObj.setStatus("ERROR");
					}
				} else {
					responseObj.setError("Wrong Token @ResetPassword");
					responseObj.setErrorCode("9997");
					responseObj.setStatus("ERROR");
				}
			} else {
				responseObj.setError("Required header detail missing @ResetPassword");
				responseObj.setErrorCode("9998");
				responseObj.setStatus("ERROR");
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			responseObj.setError("Unexpected Error @ResetPassword");
			responseObj.setErrorCode("9999");
			responseObj.setStatus("ERROR");
		}

		str = gson.toJson(responseObj);
		return RestUtils.getReponse(Response.Status.CREATED, str, "application/json");
	}
	
	
	@POST
	@Consumes({ "application/json" })
	@Path("/ACTContactDetails/")
	public Response SaveContactDetails(@Context HttpHeaders headers, @Context UriInfo ui, @Context ServletContext context,
			String reqJsonString) {
		String str = null;
		String token = context.getInitParameter("authKey");
		String htoken = null;
		Gson gson = new Gson();

		ResponseBO responseObj = new ResponseBO();
		try {

			if (headers.getRequestHeader("token") != null && headers.getRequestHeader("token").size() > 0)
				htoken = headers.getRequestHeader("token").get(0);

			if (htoken != null) {
				if (htoken.equalsIgnoreCase(token)) {

					UserBO userBo = new UserBO();
					UserDAO userDAO = new UserDAO();
					HOSConfig config = new HOSConfig();

					String userId = null;
					String mobileNo = null;
					String emailId = null;
					String otpMobile = null;
					String strToken = null;
					String otpemail = null;
					
					userBo = gson.fromJson(reqJsonString, UserBO.class);
					userId = userBo.getUserId();
					mobileNo = userBo.getUserMobile();
					emailId = userBo.getUserEmail();
					otpMobile = userBo.getStrMobOTP();
					otpemail = userBo.getStrEmailOTP();
					
					InMem mem = InMem.getInstance();
					HashMap hm = (HashMap) mem.get(userId);
					strToken =hm.get("FT_TOKEN").toString(); 
					
					//if(otpemail==null && otpMobile==null) {
						//responseObj.setError("Wrong OTP @ Contact Details");
						//responseObj.setErrorCode("9997");
						//responseObj.setStatus("ERROR");
					//}else {
						userDAO.ACTContactDetails(responseObj,userId,strToken, mobileNo, emailId, otpMobile,otpemail, config.getValue("FT_URL"));	
					//}
				} else {
					responseObj.setError("Wrong Token @SaveContactDetails");
					responseObj.setErrorCode("9997");
					responseObj.setStatus("ERROR");
				}
			} else {
				responseObj.setError("Required header detail missing @SaveContactDetails");
				responseObj.setErrorCode("9998");
				responseObj.setStatus("ERROR");
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			responseObj.setError("Unexpected Error @SaveContactDetails");
			responseObj.setErrorCode("9999");
			responseObj.setStatus("ERROR");
		}

		str = gson.toJson(responseObj);
		return RestUtils.getReponse(Response.Status.CREATED, str, "application/json");
	}

}
