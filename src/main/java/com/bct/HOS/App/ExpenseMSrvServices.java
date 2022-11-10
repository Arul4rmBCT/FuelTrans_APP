package com.bct.HOS.App;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.apache.http.HttpRequest;

import com.bct.HOS.App.BO.ExpenseHeaderBO;
import com.bct.HOS.App.BO.ResponseBO;
import com.bct.HOS.App.DAO.ExpenseDAO;
import com.bct.HOS.App.utils.FileZippingUtil;
import com.bct.HOS.App.utils.HOSConfig;
import com.bct.HOS.App.utils.RequestLogger;
import com.bct.HOS.App.utils.RestUtils;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@Path("/v1")
public class ExpenseMSrvServices {

	@POST
	@Consumes({ "application/json" })
	@Path("/storeExpenseDetails/")
	@RequestLogger
	public Response storeExpenseDetails(@Context HttpHeaders headers, @Context UriInfo ui, @Context ServletContext context,
			String reqJsonString) {
		String str = null;
		String token = context.getInitParameter("authKey");
		String htoken = null;
		Gson gson = new Gson();
		ResponseBO responseObj = new ResponseBO();
		ExpenseHeaderBO reqObj = null;
		JsonObject result =  new JsonObject();
		
		try {

			if (headers.getRequestHeader("token") != null && headers.getRequestHeader("token").size() > 0)
				htoken = headers.getRequestHeader("token").get(0);

			if (htoken != null) {
				if (htoken.equalsIgnoreCase(token)) {
					
					String siteId = null;
					
					reqObj = gson.fromJson(reqJsonString, ExpenseHeaderBO.class);
		
					siteId = reqObj.getRo_id();
					if (siteId != null) {
						if(reqObj.getTransaction_no() == null || reqObj.getTransaction_no().isEmpty()) 
							result = new ExpenseDAO().storeExpenseDetails(siteId, reqObj);
						 else 
							result = new ExpenseDAO().updateExpenseDetails(reqObj);
						
						if (result !=null) {
							responseObj.setDataObject(result);
							responseObj.setError(null);
							responseObj.setErrorCode("0000");
							responseObj.setStatus("Success");
						} else {
							responseObj.setError("Unable to insert/update the expense details.");
							responseObj.setErrorCode("0002");
							responseObj.setStatus("ERROR");
						}
						
							
						
					} else {
						responseObj.setErrorCode("0");
						responseObj.setError("Error Site Id NULL");
						responseObj.setStatus("Failed");
					}
				} else {
					responseObj.setError("Wrong Token @storeExpenseDetails");
					responseObj.setErrorCode("9997");
					responseObj.setStatus("ERROR");
				}
			} else {
				responseObj.setError("Required header detail missing @storeExpenseDetails");
				responseObj.setErrorCode("9998");
				responseObj.setStatus("ERROR");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			responseObj.setError("Unexpected Error @storeExpenseDetails" + e.getMessage());
			responseObj.setErrorCode("9999");
			responseObj.setStatus("ERROR");
		}

		str = gson.toJson(responseObj);
		return RestUtils.getReponse(Response.Status.CREATED, str, "application/json");
	}
	
	@POST
	@Consumes({ "application/json" })
	@Path("/updateExpenseDetails/")
	@RequestLogger
	public Response updateExpenseDetailsStatus(@Context HttpHeaders headers, @Context UriInfo ui, @Context ServletContext context,
			String reqJsonString) {
		String str = null;
		String token = context.getInitParameter("authKey");
		String htoken = null;
		Gson gson = new Gson();
		ResponseBO responseObj = new ResponseBO();
		ExpenseHeaderBO reqObj = null;
		JsonObject result = null;
		
		try {

			if (headers.getRequestHeader("token") != null && headers.getRequestHeader("token").size() > 0)
				htoken = headers.getRequestHeader("token").get(0);

			if (htoken != null) {
				if (htoken.equalsIgnoreCase(token)) {
					
					String siteId = null;
					
					reqObj = gson.fromJson(reqJsonString, ExpenseHeaderBO.class);
		
					siteId = reqObj.getRo_id();
					if (siteId != null) {
						
						result = new ExpenseDAO().updateExpenseDetails(reqObj);
						if (result !=null) {
							responseObj.setDataObject(result);
							responseObj.setError(null);
							responseObj.setErrorCode("0000");
							responseObj.setStatus("Success");
						} else {
							responseObj.setError("Unable to insert the expense details.");
							responseObj.setErrorCode("0002");
							responseObj.setStatus("ERROR");
						}
					} else {
						responseObj.setErrorCode("0");
						responseObj.setError("Error Site Id NULL");
						responseObj.setStatus("Failed");
					}
				} else {
					responseObj.setError("Wrong Token @updateExpenseDetailsStatus");
					responseObj.setErrorCode("9997");
					responseObj.setStatus("ERROR");
				}
			} else {
				responseObj.setError("Required header detail missing @updateExpenseDetailsStatus");
				responseObj.setErrorCode("9998");
				responseObj.setStatus("ERROR");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			responseObj.setError("Unexpected Error @updateExpenseDetailsStatus" + e.getMessage());
			responseObj.setErrorCode("9999");
			responseObj.setStatus("ERROR");
		}

		str = gson.toJson(responseObj);
		return RestUtils.getReponse(Response.Status.CREATED, str, "application/json");
	}

	@POST
	@Consumes({ "application/json" })
	@Path("/deleteExpenseDetails/")
	@RequestLogger
	public Response deleteExpenseDetails(@Context HttpHeaders headers, @Context UriInfo ui, @Context ServletContext context,
			String reqJsonString) {
		String str = null;
		String token = context.getInitParameter("authKey");
		String htoken = null;
		Gson gson = new Gson();
		ResponseBO responseObj = new ResponseBO();
		ExpenseHeaderBO reqObj = null;
		boolean result = false;
		
		try {

			if (headers.getRequestHeader("token") != null && headers.getRequestHeader("token").size() > 0)
				htoken = headers.getRequestHeader("token").get(0);

			if (htoken != null) {
				if (htoken.equalsIgnoreCase(token)) {
					
					String siteId = null;
					
					reqObj = gson.fromJson(reqJsonString, ExpenseHeaderBO.class);
		
					siteId = reqObj.getRo_id();
					if (siteId != null) {
						
						result = new ExpenseDAO().deleteExpenseDetailsByRO(siteId);
						if (result) {
							responseObj.setError(null);
							responseObj.setErrorCode("0000");
							responseObj.setStatus("Success");
						} else {
							responseObj.setError("Unable to delete the expense details.");
							responseObj.setErrorCode("0002");
							responseObj.setStatus("ERROR");
						}
					} else {
						responseObj.setErrorCode("0");
						responseObj.setError("Error Site Id NULL");
						responseObj.setStatus("Failed");
					}
				} else {
					responseObj.setError("Wrong Token @deleteExpenseDetails");
					responseObj.setErrorCode("9997");
					responseObj.setStatus("ERROR");
				}
			} else {
				responseObj.setError("Required header detail missing @deleteExpenseDetails");
				responseObj.setErrorCode("9998");
				responseObj.setStatus("ERROR");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			responseObj.setError("Unexpected Error @deleteExpenseDetails" + e.getMessage());
			responseObj.setErrorCode("9999");
			responseObj.setStatus("ERROR");
		}

		str = gson.toJson(responseObj);
		return RestUtils.getReponse(Response.Status.CREATED, str, "application/json");
	}
	
	@POST
	@Consumes({ "application/json" })
	@Path("/getExpenseDetails/")
	@RequestLogger
	public Response getExpenseDetails(@Context HttpHeaders headers, @Context UriInfo ui, @Context ServletContext context,
			String reqJsonString) {
		String str = null;
		String token = context.getInitParameter("authKey");
		String htoken = null;
		Gson gson = new Gson();
		ResponseBO responseObj = new ResponseBO();
		ExpenseHeaderBO reqObj = null;
		JSONArray result =  new JSONArray();
		
		try {

			if (headers.getRequestHeader("token") != null && headers.getRequestHeader("token").size() > 0)
				htoken = headers.getRequestHeader("token").get(0);

			if (htoken != null) {
				if (htoken.equalsIgnoreCase(token)) {
					
					String siteId = null;
					String fromDate = null;
					String mode = null;
					reqObj = gson.fromJson(reqJsonString, ExpenseHeaderBO.class);
		
					siteId = reqObj.getRo_id();
					fromDate = reqObj.getFromDate();
					mode = reqObj.getMode();
					
					if (fromDate != null) {
						
						result = new ExpenseDAO().getExpenseDetailsByFromdate(siteId,fromDate);
						if (result.size() > 0) {
							responseObj.setDataSet(result);
							responseObj.setError(null);
							responseObj.setErrorCode("0000");
							responseObj.setStatus("Success");
						} else {
							responseObj.setError("Unable to fetch the expense details.");
							responseObj.setErrorCode("0002");
							responseObj.setStatus("ERROR");
						}
					} else if ("ByRO".equalsIgnoreCase(mode)) {
						
						result = new ExpenseDAO().getExpenseDetailsByRO(siteId);
						if (result.size() > 0) {
							responseObj.setDataSet(result);
							responseObj.setError(null);
							responseObj.setErrorCode("0000");
							responseObj.setStatus("Success");
						} else {
							responseObj.setError("Unable to fetch the expense details.");
							responseObj.setErrorCode("0002");
							responseObj.setStatus("ERROR");
						}
					} else if("ByTransNo".equalsIgnoreCase(mode)) {
						JSONObject obj = new ExpenseDAO().getExpenseDetailsByTransactionNo(siteId,reqObj.getTransaction_no());
						if (obj !=null) {
							responseObj.setDataObj(obj);
							responseObj.setError(null);
							responseObj.setErrorCode("0000");
							responseObj.setStatus("Success");
						} else {
							responseObj.setError("Unable to fetch the expense details.");
							responseObj.setErrorCode("0002");
							responseObj.setStatus("ERROR");
						}
						
					} else {
						responseObj.setErrorCode("0");
						responseObj.setError("Error mode is not matching");
						responseObj.setStatus("Failed");
					}
				} else {
					responseObj.setError("Wrong Token @getExpenseDetails");
					responseObj.setErrorCode("9997");
					responseObj.setStatus("ERROR");
				}
			} else {
				responseObj.setError("Required header detail missing @getExpenseDetails");
				responseObj.setErrorCode("9998");
				responseObj.setStatus("ERROR");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			responseObj.setError("Unexpected Error @getExpenseDetails" + e.getMessage());
			responseObj.setErrorCode("9999");
			responseObj.setStatus("ERROR");
		}

		str = gson.toJson(responseObj);
		return RestUtils.getReponse(Response.Status.CREATED, str, "application/json");
	}
	
	@POST
	@Path("/uploadExpenseAttachement")
	@Consumes(MediaType.APPLICATION_JSON)
	@RequestLogger
	public Response uploadExpAttachement(@Context HttpRequest request, String reqJsonString) {
		
		Gson gson = new Gson();
		ResponseBO responseObj = new ResponseBO();
		boolean uploadSts = false;

		@SuppressWarnings(value = { "unchecked" })
		Map<String, Object> map = (Map<String, Object>) gson.fromJson(
				reqJsonString, Object.class);
		String fileContent = (String) map.get("fileContent");
		String fileName = (String) map.get("fileName");
		String previousFile = (String) map.get("previous_fileName");
		fileName = fileName + "_"+ System.nanoTime();
		byte[] fileBinary = Base64.getDecoder().decode(fileContent);
		HOSConfig conf = new HOSConfig();
		String UPLOADED_FILE_PATH = conf.getValue("EXP_ATTACH_FILE_LOCATION");
		if(previousFile != null && previousFile.length() > 0) {
			String fileTobeDeleted = UPLOADED_FILE_PATH + previousFile;
			File removingFile = new File(fileTobeDeleted);
			if(removingFile.exists()) {
				 boolean deleteFlag = removingFile.delete();
				 if(deleteFlag) {
					 System.out.println("Expenses >> uploadAttachement >> The old files has been removed");
				 } else {
					 System.out.println("Expenses >> uploadAttachement >> Not able to remove old files");
				 }
			} else {
				System.out.println("Expenses>> uploadAttachement >> The previous file is not available");
			}
		}
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
		String formattedDate = simpleDateFormat.format(new Date());
		String uploadDir = UPLOADED_FILE_PATH +  formattedDate;
		File uploadDirFile = new File(uploadDir);
		if(!uploadDirFile.exists()) {
			uploadDirFile.mkdir();
		}
		String fullFilePath = uploadDir + "/" + fileName;
		try {
			uploadSts = writeFile(fileBinary, fullFilePath);
		} catch (Exception e) {
			System.out.println("Expenses >> uploadAttachement >> Exception occurred "+e.getMessage());
			e.printStackTrace();
		}
		if (uploadSts) {
			responseObj.setFileName(formattedDate+"/"+fileName);
		    responseObj.setErrorCode("1");
			responseObj.setError("");
			responseObj.setStatus("Success");
			
		} else {
			responseObj.setErrorCode("0");
			responseObj.setError("Error could not upload");
			responseObj.setStatus("Failed");
		}

		return Response.status(200).entity(gson.toJson(responseObj)).build();
	}

	@POST
	@Path("/downloadExpenseAttachement")
	@Consumes(MediaType.APPLICATION_JSON)
	@RequestLogger
	public Response downloadExpAttachement(@Context HttpRequest request, String reqJsonString) {
		
		Map responseMap = new HashMap();
		Gson gson = new Gson();
		ResponseBO responseObj = new ResponseBO();
		
		@SuppressWarnings(value = { "unchecked" })
		Map<String, Object> map = (Map<String, Object>) gson.fromJson(
				reqJsonString, Object.class);
		String fileName = (String) map.get("attachmentId");
		HOSConfig conf = new HOSConfig();
		String UPLOADED_FILE_PATH = conf.getValue("EXP_ATTACH_FILE_LOCATION");
		
		byte[] fileBytes = null;
		try {
			File filePath = new File(UPLOADED_FILE_PATH + fileName);
			if(filePath.exists()) {
				fileBytes = Files.readAllBytes(filePath.toPath());
				String fileString = Base64.getEncoder().encodeToString(fileBytes);
				responseMap.put("status", "Success");
				responseMap.put("errorCode", "0");
				responseMap.put("fileContent", fileString);
				responseMap.put("errorMessage", null);
			} else {
				responseMap.put("status", "Failed");
				responseMap.put("errorCode", "1");
				responseMap.put("fileContent", null);
				responseMap.put("errorMessage", "File does not exists");
			}
		} catch(Exception ex) {
			System.out.println("Expenses >> downloadAttachement >>Exception occurred in file read: "+ex.getMessage());
			responseMap.put("status", "Failed");
			responseMap.put("errorCode", "1");
			responseMap.put("fileContent", null);
			responseMap.put("errorMessage", ex.getMessage());
		}
		return Response.status(200).entity(gson.toJson(responseMap)).build();
	}

	@POST
	@Path("/downloadExpenseZipFile")
	@Consumes(MediaType.APPLICATION_JSON)
	@RequestLogger
	public Response downloadExpenseZipFile(@Context HttpRequest request, String reqJsonString) {
		
		Map responseMap = new HashMap();
		Gson gson = new Gson();
		
		@SuppressWarnings(value = { "unchecked" })
		Map<String, Object> map = (Map<String, Object>) gson.fromJson(
				reqJsonString, Object.class);
		String stDate = (String) map.get("st_date");
		String endDate = (String) map.get("end_date");
		String stationId = (String) map.get("st_name");
		HOSConfig conf = new HOSConfig();
		String dumpImageLoc = conf.getValue("EXP_ATTACH_FILE_LOCATION");
		String downloadTempExtractLoc = conf.getValue("DOWNLOAD_EXTRACT_LOCATION");
		String currTme = String.valueOf(System.currentTimeMillis());
		
		String newZipFileText = stationId+"_ExpSlip_"+currTme;
		String subTempPath = downloadTempExtractLoc + "/" + newZipFileText;
		File mainDir = new File(subTempPath);
		mainDir.mkdirs();	
		new ExpenseDAO().getExpenseDetailCSVByRO(stationId,stDate,endDate,dumpImageLoc,subTempPath);
		String newZipFilePath = downloadTempExtractLoc+"/"+newZipFileText+".zip";
		byte fileBytes[] = null; 
		try {
			File exportZipFile = FileZippingUtil.createZipFile(subTempPath,newZipFilePath);
			if(exportZipFile.exists()) {
				fileBytes = Files.readAllBytes(exportZipFile.toPath());
				String fileString = Base64.getEncoder().encodeToString(fileBytes);
				responseMap.put("status", "Success");
				responseMap.put("errorCode", "0");
				responseMap.put("fileContent", fileString);
				responseMap.put("fileName", newZipFileText+"zip");
				responseMap.put("errorMessage", null);
			} else {
				responseMap.put("status", "Failed");
				responseMap.put("errorCode", "1");
				responseMap.put("fileContent", null);
				responseMap.put("errorMessage", "File does not exists");
			}
		} catch(Exception ex) {
			System.out.println("Bank Deposit >> downloadAttachement >>Exception occurred in file read: "+ex.getMessage());
			responseMap.put("status", "Failed");
			responseMap.put("errorCode", "1");
			responseMap.put("fileContent", null);
			responseMap.put("errorMessage", ex.getMessage());
		}
		return Response.status(200).entity(gson.toJson(responseMap)).build();
	}

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
			e.printStackTrace();
		}

		return false;

	}
}
