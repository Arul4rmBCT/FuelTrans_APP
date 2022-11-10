package com.bct.HOS.App;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.ServletContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpRequest;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

import com.bct.HOS.App.BO.PriceBO;
import com.bct.HOS.App.BO.ResponseBO;
import com.bct.HOS.App.BO.TSMBO;
import com.bct.HOS.App.DAO.HierarchyFT;
import com.bct.HOS.App.DAO.PriceDAO;
import com.bct.HOS.App.DAO.SalesDAO;
import com.bct.HOS.App.DAO.UtilDAO;
import com.bct.HOS.App.utils.Excel2JSON;
import com.bct.HOS.App.utils.HOSConfig;
import com.bct.HOS.App.utils.RequestLogger;
import com.bct.HOS.App.utils.RestUtils;
import com.google.gson.Gson;

import net.sf.json.JSONArray;

@Path("/v1")
public class PriceMSrvServices {

	@POST
	@Consumes({ "application/json" })
	@Path("/getPriceDetails/")
	@RequestLogger
	public Response getPriceDetails(@Context HttpHeaders headers, @Context UriInfo ui, @Context ServletContext context,
			String reqJsonString) {
		String str = null;
		String token = context.getInitParameter("authKey");
		String htoken = null;
		Gson gson = new Gson();
		JSONArray dataSet = new JSONArray();
		ResponseBO responseObj = new ResponseBO();
		try {

			if (headers.getRequestHeader("token") != null && headers.getRequestHeader("token").size() > 0)
				htoken = headers.getRequestHeader("token").get(0);

			if (htoken != null) {
				if (htoken.equalsIgnoreCase(token)) {
					String userId = null;
					String roleId = null;
					String productName = null;
					String dayDiff = null;
					String country = null;
					String siteIDs = null;

					TSMBO tsmBO = gson.fromJson(reqJsonString, TSMBO.class);
					userId = tsmBO.getUserId();
					roleId = tsmBO.getRoleId();
					siteIDs = tsmBO.getSiteID();
					dayDiff = tsmBO.getDayDiff();
					productName = tsmBO.getProductName();
					country = tsmBO.getCountry();

					if (siteIDs == null)
						siteIDs = new HierarchyFT().getUserHierarchyFT(userId, roleId, country);
					//System.out.println("siteIDs:" + siteIDs);
					if (siteIDs != null) {
						dataSet = new PriceDAO().getPriceDetails(siteIDs, productName, dayDiff);
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
					responseObj.setError("Wrong Token @getPriceDetails");
					responseObj.setErrorCode("9997");
					responseObj.setStatus("ERROR");
				}
			} else {
				responseObj.setError("Required header detail missing @getPriceDetails");
				responseObj.setErrorCode("9998");
				responseObj.setStatus("ERROR");
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			responseObj.setError("Unexpected Error @getPriceDetails");
			responseObj.setErrorCode("9999");
			responseObj.setStatus("ERROR");
		}

		str = gson.toJson(responseObj);
		return RestUtils.getReponse(Response.Status.CREATED, str, "application/json");
	}

	@POST
	@Consumes({ "application/json" })
	@Path("/getPriceHistory/")
	@RequestLogger
	public Response getPriceHistory(@Context HttpHeaders headers, @Context UriInfo ui, @Context ServletContext context,
			String reqJsonString) {
		String str = null;
		String token = context.getInitParameter("authKey");
		String htoken = null;
		Gson gson = new Gson();
		JSONArray dataSet = new JSONArray();
		ResponseBO responseObj = new ResponseBO();
		try {

			if (headers.getRequestHeader("token") != null && headers.getRequestHeader("token").size() > 0)
				htoken = headers.getRequestHeader("token").get(0);

			if (htoken != null) {
				if (htoken.equalsIgnoreCase(token)) {
					String userId = null;
					String roleId = null;
					String productName = null;
					String fromDate = null;
					String toDate = null;
					String country = null;
					String siteIDs = null;
					String state = null;
					String region = null;
					String district = null;
					String city = null;
					String status = null;
					
					TSMBO tsmBO = gson.fromJson(reqJsonString, TSMBO.class);
					userId = tsmBO.getUserId();
					roleId = tsmBO.getRoleId();
					fromDate = tsmBO.getFromDate();
					toDate = tsmBO.getToDate();
					productName = tsmBO.getProductName();
					country = tsmBO.getCountry();
					siteIDs = tsmBO.getSiteID();
					state = tsmBO.getState();
					region = tsmBO.getRegion();
					district = tsmBO.getDistrict();
					city = tsmBO.getCity();
					status = tsmBO.getStatus();
					
					if (siteIDs == null)
						siteIDs = new HierarchyFT().getUserHierarchyFT(userId, roleId, country);
					//System.out.println("siteIDs:" + siteIDs);
					if (siteIDs != null) {
						dataSet = new PriceDAO().getPriceHistory(siteIDs, fromDate, toDate, productName, country, state,
								region, district, city,status);
						responseObj.setDataSet(dataSet);
						responseObj.setGRID_COLUMN(dataSet);
						responseObj.setError(null);
						responseObj.setErrorCode("0000");
						responseObj.setStatus("Success");
					} else {
						responseObj.setError("No Data!.");
						responseObj.setErrorCode("0001");
						responseObj.setStatus("Success");
					}
				} else {
					responseObj.setError("Wrong Token @getPriceHistory");
					responseObj.setErrorCode("9997");
					responseObj.setStatus("ERROR");
				}
			} else {
				responseObj.setError("Required header detail missing @getPriceHistory");
				responseObj.setErrorCode("9998");
				responseObj.setStatus("ERROR");
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			responseObj.setError("Unexpected Error @getPriceHistory");
			responseObj.setErrorCode("9999");
			responseObj.setStatus("ERROR");
		}

		str = gson.toJson(responseObj);
		return RestUtils.getReponse(Response.Status.CREATED, str, "application/json");
	}

	@POST
	@Consumes({ "application/json" })
	@Path("/getRSP/")
	@RequestLogger
	public Response getRSP(@Context HttpHeaders headers, @Context UriInfo ui, @Context ServletContext context,
			String reqJsonString) {
		String str = null;
		String token = context.getInitParameter("authKey");
		String htoken = null;
		Gson gson = new Gson();
		JSONArray dataSet = new JSONArray();
		ResponseBO responseObj = new ResponseBO();
		try {

			if (headers.getRequestHeader("token") != null && headers.getRequestHeader("token").size() > 0)
				htoken = headers.getRequestHeader("token").get(0);

			if (htoken != null) {
				if (htoken.equalsIgnoreCase(token)) {
					String userId = null;
					String roleId = null;
					String siteIDs = null;
					String state = null;

					TSMBO tsmBO = gson.fromJson(reqJsonString, TSMBO.class);
					userId = tsmBO.getUserId();
					roleId = tsmBO.getRoleId();
					siteIDs = tsmBO.getSiteID();
					state = tsmBO.getState();

					if (siteIDs == null)
						siteIDs = new HierarchyFT().getUserHierarchyFT(userId, roleId, null);
					//System.out.println("siteIDs:" + siteIDs);
					if (siteIDs != null) {
						dataSet = new PriceDAO().getRSP(siteIDs,state);
						responseObj.setDataSet(dataSet);
						responseObj.setGRID_COLUMN(dataSet);
						responseObj.setError(null);
						responseObj.setErrorCode("0000");
						responseObj.setStatus("Success");
					} else {
						responseObj.setError("No Data!.");
						responseObj.setErrorCode("0001");
						responseObj.setStatus("Success");
					}
				} else {
					responseObj.setError("Wrong Token @getRSP");
					responseObj.setErrorCode("9997");
					responseObj.setStatus("ERROR");
				}
			} else {
				responseObj.setError("Required header detail missing @getRSP");
				responseObj.setErrorCode("9998");
				responseObj.setStatus("ERROR");
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			responseObj.setError("Unexpected Error @getRSP");
			responseObj.setErrorCode("9999");
			responseObj.setStatus("ERROR");
		}

		str = gson.toJson(responseObj);
		return RestUtils.getReponse(Response.Status.CREATED, str, "application/json");
	}

	@POST
	@Consumes({ "application/json" })
	@Path("/setRSP/")
	@RequestLogger
	public Response setRSP(@Context HttpHeaders headers, @Context UriInfo ui, @Context ServletContext context,
			String reqJsonString) {

		String str = null;
		String token = context.getInitParameter("authKey");
		String htoken = null;
		Gson gson = new Gson();
		boolean dataSet = false;
		ResponseBO responseObj = new ResponseBO();
		try {

			if (headers.getRequestHeader("token") != null && headers.getRequestHeader("token").size() > 0)
				htoken = headers.getRequestHeader("token").get(0);

			if (htoken != null) {
				if (htoken.equalsIgnoreCase(token)) {
					String userId = null;
					String roleId = null;
					String productName = null;
					String siteID = null;
					List<String> siteIDList = null;
					float newPrice = 0;
					String effectiveFrom = null;

					TSMBO tsmBO = gson.fromJson(reqJsonString, TSMBO.class);
					userId = tsmBO.getUserId();
					roleId = tsmBO.getRoleId();
					siteIDList = tsmBO.getSiteIDList();
					productName = tsmBO.getProductName();
					newPrice = tsmBO.getNewPrice();
					effectiveFrom = tsmBO.getEffectiveFrom();
					siteID = tsmBO.getSiteID();

					if (siteIDList != null) {
						dataSet = new PriceDAO().setPriceDetails(userId, roleId, siteIDList, productName, newPrice,
								effectiveFrom,siteID);
						if (dataSet) {
							responseObj.setError(null);
							responseObj.setErrorCode("0000");
							responseObj.setStatus("Success");
						} else {
							responseObj.setError("Unable to insert the price.");
							responseObj.setErrorCode("0002");
							responseObj.setStatus("ERROR");
						}
					} else {
						responseObj.setError("No Data!.");
						responseObj.setErrorCode("0001");
						responseObj.setStatus("Success");
					}
				} else {
					responseObj.setError("Wrong Token @getRSP");
					responseObj.setErrorCode("9997");
					responseObj.setStatus("ERROR");
				}
			} else {
				responseObj.setError("Required header detail missing @getRSP");
				responseObj.setErrorCode("9998");
				responseObj.setStatus("ERROR");
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			responseObj.setError("Unexpected Error @getRSP" + e.getMessage());
			responseObj.setErrorCode("9999");
			responseObj.setStatus("ERROR");
		}

		str = gson.toJson(responseObj);
		return RestUtils.getReponse(Response.Status.CREATED, str, "application/json");
	}
	
	@POST
	@Consumes({ "application/json" })
	@Path("/setBulkRSP/")
	@RequestLogger
	public Response setBulkRSP(@Context HttpHeaders headers, @Context UriInfo ui,
        		@Context ServletContext context, String reqJsonString) {
		
		String str = null;
		String token = context.getInitParameter("authKey");
		String htoken = null;
		Gson gson = new Gson();
		boolean dataSet = false;
		ResponseBO responseObj = new ResponseBO();
		try {

			if (headers.getRequestHeader("token") != null && headers.getRequestHeader("token").size() > 0)
				htoken = headers.getRequestHeader("token").get(0);

			if (htoken != null) {
				if (htoken.equalsIgnoreCase(token)) {
					String userId = null;
					String roleId = null;
					List<PriceBO> bulkPrice= null;

					//System.out.println("bulkPrice ::" + reqJsonString);
					
					
					TSMBO tsmBO = gson.fromJson(reqJsonString, TSMBO.class);
					userId = tsmBO.getUserId();
					roleId = tsmBO.getRoleId();
					bulkPrice = tsmBO.getBulkPrice();
					
					//System.out.println("bulkPrice ::" + bulkPrice);
					
					if (bulkPrice != null) {
						dataSet = new PriceDAO().setBulkPriceDetails(userId,roleId,bulkPrice);
						if(dataSet) {
							responseObj.setError(null);
							responseObj.setErrorCode("0000");
							responseObj.setStatus("Success");
						} else {
							responseObj.setError("Unable to insert the price.");
							responseObj.setErrorCode("0002");
							responseObj.setStatus("ERROR");
						}
					} else {
						responseObj.setError("No Data!.");
						responseObj.setErrorCode("0001");
						responseObj.setStatus("Success");
					}
				} else {
					responseObj.setError("Wrong Token @getRSP");
					responseObj.setErrorCode("9997");
					responseObj.setStatus("ERROR");
				}
			} else {
				responseObj.setError("Required header detail missing @getRSP");
				responseObj.setErrorCode("9998");
				responseObj.setStatus("ERROR");
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			responseObj.setError("Unexpected Error @getRSP"+e.getMessage());
			responseObj.setErrorCode("9999");
			responseObj.setStatus("ERROR");
		}

		str = gson.toJson(responseObj);
		return RestUtils.getReponse(Response.Status.CREATED, str, "application/json");
	}

	@POST
	@Consumes({ "application/json" })
	@Path("/getPriceROList/")
	@RequestLogger
	public Response getPriceROList(@Context HttpHeaders headers, @Context UriInfo ui, @Context ServletContext context,
			String reqJsonString) {
		String str = null;
		String token = context.getInitParameter("authKey");
		String htoken = null;
		Gson gson = new Gson();
		JSONArray dataSet = new JSONArray();
		ResponseBO responseObj = new ResponseBO();
		try {

			if (headers.getRequestHeader("token") != null && headers.getRequestHeader("token").size() > 0)
				htoken = headers.getRequestHeader("token").get(0);

			if (htoken != null) {
				if (htoken.equalsIgnoreCase(token)) {
					String userId = null;
					String roleId = null;
					String country = null;
					String siteIDs = null;
					String state = null;
					String city = null;
					String region = null;
					String division = null;
					String district = null;
					String subDistrict = null;
					String productName = null;

					TSMBO tsmBO = gson.fromJson(reqJsonString, TSMBO.class);
					userId = tsmBO.getUserId();
					roleId = tsmBO.getRoleId();
					siteIDs = tsmBO.getSiteID();
					country = tsmBO.getCountry();
					state = tsmBO.getState();
					city = tsmBO.getCity();
					region = tsmBO.getRegion();
					division = tsmBO.getDivision();
					subDistrict = tsmBO.getSubDistrict();
					district = tsmBO.getDistrict();
					productName = tsmBO.getProductName();

					if (siteIDs == null)
						siteIDs = new HierarchyFT().getUserHierarchyFT(userId, roleId, null);
					//System.out.println("siteIDs:" + siteIDs);
					if (siteIDs != null) {
						dataSet = new PriceDAO().getPriceROList(siteIDs, country, state, city, division, district,
								subDistrict, region, productName);
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
					responseObj.setError("Wrong Token @getROList");
					responseObj.setErrorCode("9997");
					responseObj.setStatus("ERROR");
				}
			} else {
				responseObj.setError("Required header detail missing @getROList");
				responseObj.setErrorCode("9998");
				responseObj.setStatus("ERROR");
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			responseObj.setError("Unexpected Error @getROList");
			responseObj.setErrorCode("9999");
			responseObj.setStatus("ERROR");
		}

		str = gson.toJson(responseObj);
		return RestUtils.getReponse(Response.Status.CREATED, str, "application/json");
	}

	

	@POST
	@Path("/bulkUpload")
	@Consumes("multipart/form-data")
	@RequestLogger
	public Response bulkUpload(@Context HttpRequest request, MultipartFormDataInput input) {
		String str = null;
		Gson gson = new Gson();
		
		JSONArray dataSet = new JSONArray();
		ResponseBO responseObj = new ResponseBO();
		String fileName = "";

		HOSConfig conf = new HOSConfig();
		String UPLOADED_FILE_PATH = conf.getValue("FW_Path_Local");

		Map<String, List<InputPart>> uploadForm = input.getFormDataMap();
		List<InputPart> inputParts = uploadForm.get("uploadedFile");

		for (InputPart inputPart : inputParts) {
			try {

				MultivaluedMap<String, String> header = inputPart.getHeaders();
				fileName = getFileName(header);

				// convert the uploaded file to inputstream
				InputStream inputStream = inputPart.getBody(InputStream.class, null);

				byte[] bytes = IOUtils.toByteArray(inputStream);
				// constructs upload file path
				fileName = UPLOADED_FILE_PATH + fileName;

				boolean uploadSts = writeFile(bytes, fileName);
				if (uploadSts) {
					dataSet = new Excel2JSON().convert(fileName);
					responseObj.setDataSet(dataSet);
					responseObj.setError(null);
					responseObj.setErrorCode("0000");
					responseObj.setStatus("Success");
				}
				//System.out.println("** FILE STORED IN LOCAL *** " + fileName);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		str = gson.toJson(responseObj);
		return RestUtils.getReponse(Response.Status.CREATED, str, "application/json");
	}

	/**
	 * header sample { Content-Type=[image/png], Content-Disposition=[form-data;
	 * name="file"; filename="filename.extension"] }
	 **/
	// get uploaded filename, is there a easy way in RESTEasy?
	private String getFileName(MultivaluedMap<String, String> header) {

		String[] contentDisposition = header.getFirst("Content-Disposition").split(";");

		for (String filename : contentDisposition) {
			if ((filename.trim().startsWith("filename"))) {

				String[] name = filename.split("=");

				String finalFileName = name[1].trim().replaceAll("\"", "");
				return finalFileName;
			}
		}
		return "unknown";
	}

	/**
	 * Save to somewhere
	 * 
	 * @param content
	 * @param filename
	 * @return
	 * @throws IOException
	 */
	private boolean writeFile(byte[] content, String filename) throws IOException {

		try {
			File file = new File(filename);
			if (!file.exists()) {
				file.createNewFile();
			}
			FileOutputStream fop = new FileOutputStream(file);
			fop.write(content);
			fop.flush();
			fop.close();
			return true;
		} catch (Exception e) {
			// TODO: handle exception
		}

		return false;

	}
	
	
	@POST
	@Consumes({ "application/json" })
	@Path("/getROCurrentPriceList/")
	@RequestLogger
	public Response getROCurrentPriceList(@Context HttpHeaders headers, @Context UriInfo ui, @Context ServletContext context,
			String reqJsonString) {
		String str = null;
		String token = context.getInitParameter("authKey");
		String htoken = null;
		Gson gson = new Gson();
		JSONArray dataSet = new JSONArray();
		ResponseBO responseObj = new ResponseBO();
		try {

			if (headers.getRequestHeader("token") != null && headers.getRequestHeader("token").size() > 0)
				htoken = headers.getRequestHeader("token").get(0);

			if (htoken != null) {
				if (htoken.equalsIgnoreCase(token)) {
					String country = null;
					String siteIDs = null;
					String productName = null;

					TSMBO tsmBO = gson.fromJson(reqJsonString, TSMBO.class);
					siteIDs = tsmBO.getSiteID();
					country = tsmBO.getCountry();
					productName = tsmBO.getProductName();
					
					dataSet = new PriceDAO().getROCurrentPriceList(siteIDs, country, productName);
					responseObj.setDataSet(dataSet);
					responseObj.setError(null);
					responseObj.setErrorCode("0000");
					responseObj.setStatus("Success");
					
				} else {
					responseObj.setError("Wrong Token @getROCurrentPriceList");
					responseObj.setErrorCode("9997");
					responseObj.setStatus("ERROR");
				}
			} else {
				responseObj.setError("Required header detail missing @getROCurrentPriceList");
				responseObj.setErrorCode("9998");
				responseObj.setStatus("ERROR");
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			responseObj.setError("Unexpected Error @getROCurrentPriceList");
			responseObj.setErrorCode("9999");
			responseObj.setStatus("ERROR");
		}

		str = gson.toJson(responseObj);
		return RestUtils.getReponse(Response.Status.CREATED, str, "application/json");
	}
	

}
