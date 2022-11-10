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
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.apache.http.HttpRequest;

import com.bct.HOS.App.BO.BankDetailsBO;
import com.bct.HOS.App.BO.ResponseBO;
import com.bct.HOS.App.BO.TSMBO;
import com.bct.HOS.App.DAO.BankDAO;
import com.bct.HOS.App.DAO.HierarchyFT;
import com.bct.HOS.App.utils.FileZippingUtil;
import com.bct.HOS.App.utils.HOSConfig;
import com.bct.HOS.App.utils.RequestLogger;
import com.bct.HOS.App.utils.RestUtils;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@Path("/v1")
public class BankMSrvServices {


	@POST
	@Path("/uploadAttachement")
	@Consumes(MediaType.APPLICATION_JSON)
	@RequestLogger
	public Response uploadAttachement(@Context HttpRequest request, String reqJsonString) {
		
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
		String UPLOADED_FILE_PATH = conf.getValue("ATTACH_FILE_LOCATION");
		if(previousFile != null && previousFile.length() > 0) {
			String fileTobeDeleted = UPLOADED_FILE_PATH + previousFile;
			File removingFile = new File(fileTobeDeleted);
			if(removingFile.exists()) {
				 boolean deleteFlag = removingFile.delete();
				 if(deleteFlag) {
					 System.out.println("Bank Deposit >> uploadAttachement >> The old files has been removed");
				 } else {
					 System.out.println("Bank Deposit >> uploadAttachement >> Not able to remove old files");
				 }
			} else {
				System.out.println("Bank Deposit >> uploadAttachement >> The previous file is not available");
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
			System.out.println("Bank Deposit >> uploadAttachement >> Exception occurred "+e.getMessage());
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
	@Path("/downloadAttachement")
	@Consumes(MediaType.APPLICATION_JSON)
	@RequestLogger
	public Response downloadAttachement(@Context HttpRequest request, String reqJsonString) {
		
		Map responseMap = new HashMap();
		Gson gson = new Gson();
		
		@SuppressWarnings(value = { "unchecked" })
		Map<String, Object> map = (Map<String, Object>) gson.fromJson(
				reqJsonString, Object.class);
		String fileName = (String) map.get("attachmentId");
		HOSConfig conf = new HOSConfig();
		String UPLOADED_FILE_PATH = conf.getValue("ATTACH_FILE_LOCATION");
		
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
			System.out.println("Bank Deposit >> downloadAttachement >>Exception occurred in file read: "+ex.getMessage());
			responseMap.put("status", "Failed");
			responseMap.put("errorCode", "1");
			responseMap.put("fileContent", null);
			responseMap.put("errorMessage", ex.getMessage());
		}
		return Response.status(200).entity(gson.toJson(responseMap)).build();
	}

	
	@POST
	@Path("/downloadBankZipFile")
	@Consumes(MediaType.APPLICATION_JSON)
	@RequestLogger
	public Response downloadBankZipFile(@Context HttpRequest request, String reqJsonString) {
		
		Map responseMap = new HashMap();
		Gson gson = new Gson();
		
		@SuppressWarnings(value = { "unchecked" })
		Map<String, Object> map = (Map<String, Object>) gson.fromJson(
				reqJsonString, Object.class);
		String stDate = (String) map.get("st_date");
		String endDate = (String) map.get("end_date");
		String stationId = (String) map.get("st_name");
		HOSConfig conf = new HOSConfig();
		String dumpImageLoc = conf.getValue("ATTACH_FILE_LOCATION");
		String downloadTempExtractLoc = conf.getValue("DOWNLOAD_EXTRACT_LOCATION");
		String currTme = String.valueOf(System.currentTimeMillis());
		
		String newZipFileText = stationId+"_BankSlip_"+currTme;
		String subTempPath = downloadTempExtractLoc + "/" + newZipFileText;
		File mainDir = new File(subTempPath);
		mainDir.mkdirs();	
		new BankDAO().getBankDepositCSVByRO(stationId,stDate,endDate,dumpImageLoc,subTempPath);
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
			e.printStackTrace();
		}

		return false;

	}
	
	@POST
	@Consumes({ "application/json" })
	@Path("/storeBankDetails/")
	@RequestLogger
	public Response storeBankDetails(@Context HttpHeaders headers, @Context UriInfo ui, @Context ServletContext context,
			String reqJsonString) {
		String str = null;
		String token = context.getInitParameter("authKey");
		String htoken = null;
		Gson gson = new Gson();
		ResponseBO responseObj = new ResponseBO();
		BankDetailsBO reqObj = null;
		JsonObject result =  null;
		
		try {

			if (headers.getRequestHeader("token") != null && headers.getRequestHeader("token").size() > 0)
				htoken = headers.getRequestHeader("token").get(0);

			if (htoken != null) {
				if (htoken.equalsIgnoreCase(token)) {
					
					String siteId = null;
					
					reqObj = gson.fromJson(reqJsonString, BankDetailsBO.class);
		
					siteId = reqObj.getRo_id();
					if (siteId != null) {
						if(reqObj.getTransaction_no() == null  || reqObj.getTransaction_no().isEmpty()) 
							result = new BankDAO().storeBankDepositDetails(siteId, reqObj);
						else 
							result = new BankDAO().updateBankDepositDetails(reqObj);
							if (result != null) {
								responseObj.setDataObject(result);
								responseObj.setError(null);
								responseObj.setErrorCode("0000");
								responseObj.setStatus("Success");
							} 
							else {
								responseObj.setError("Unable to insert the deposit details.");
								responseObj.setErrorCode("0002");
								responseObj.setStatus("ERROR");
							}
						
					} else {
						responseObj.setErrorCode("0");
						responseObj.setError("Error Site Id NULL");
						responseObj.setStatus("Failed");
					}
				} else {
					responseObj.setError("Wrong Token @storeBankDetails");
					responseObj.setErrorCode("9997");
					responseObj.setStatus("ERROR");
				}
			} else {
				responseObj.setError("Required header detail missing @storeBankDetails");
				responseObj.setErrorCode("9998");
				responseObj.setStatus("ERROR");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			responseObj.setError("Unexpected Error @storeBankDetails" + e.getMessage());
			responseObj.setErrorCode("9999");
			responseObj.setStatus("ERROR");
		}

		str = gson.toJson(responseObj);
		return RestUtils.getReponse(Response.Status.CREATED, str, "application/json");
	}
	
	@POST
	@Consumes({ "application/json" })
	@Path("/updateBankDetailsStatus/")
	@RequestLogger
	public Response updateBankDetailsStatus(@Context HttpHeaders headers, @Context UriInfo ui, @Context ServletContext context,
			String reqJsonString) {
		String str = null;
		String token = context.getInitParameter("authKey");
		String htoken = null;
		Gson gson = new Gson();
		ResponseBO responseObj = new ResponseBO();
		BankDetailsBO reqObj = null;
		JsonObject result = null;
		
		try {

			if (headers.getRequestHeader("token") != null && headers.getRequestHeader("token").size() > 0)
				htoken = headers.getRequestHeader("token").get(0);

			if (htoken != null) {
				if (htoken.equalsIgnoreCase(token)) {
					
					String siteId = null;
					
					reqObj = gson.fromJson(reqJsonString, BankDetailsBO.class);
		
					siteId = reqObj.getRo_id();
					if (siteId != null) {
						
						result = new BankDAO().updateBankDepositDetails(reqObj);
						if (result != null) {
							responseObj.setError(null);
							responseObj.setErrorCode("0000");
							responseObj.setStatus("Success");
						} else {
							responseObj.setError("Unable to insert the deposit details.");
							responseObj.setErrorCode("0002");
							responseObj.setStatus("ERROR");
						}
					} else {
						responseObj.setErrorCode("0");
						responseObj.setError("Error Site Id NULL");
						responseObj.setStatus("Failed");
					}
				} else {
					responseObj.setError("Wrong Token @storeBankDetails");
					responseObj.setErrorCode("9997");
					responseObj.setStatus("ERROR");
				}
			} else {
				responseObj.setError("Required header detail missing @storeBankDetails");
				responseObj.setErrorCode("9998");
				responseObj.setStatus("ERROR");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			responseObj.setError("Unexpected Error @storeBankDetails" + e.getMessage());
			responseObj.setErrorCode("9999");
			responseObj.setStatus("ERROR");
		}

		str = gson.toJson(responseObj);
		return RestUtils.getReponse(Response.Status.CREATED, str, "application/json");
	}

	@POST
	@Consumes({ "application/json" })
	@Path("/deleteBankDetails/")
	@RequestLogger
	public Response deleteBankDetails(@Context HttpHeaders headers, @Context UriInfo ui, @Context ServletContext context,
			String reqJsonString) {
		String str = null;
		String token = context.getInitParameter("authKey");
		String htoken = null;
		Gson gson = new Gson();
		ResponseBO responseObj = new ResponseBO();
		BankDetailsBO reqObj = null;
		boolean result = false;
		
		try {

			if (headers.getRequestHeader("token") != null && headers.getRequestHeader("token").size() > 0)
				htoken = headers.getRequestHeader("token").get(0);

			if (htoken != null) {
				if (htoken.equalsIgnoreCase(token)) {
					
					String siteId = null;
					
					reqObj = gson.fromJson(reqJsonString, BankDetailsBO.class);
		
					siteId = reqObj.getRo_id();
					if (siteId != null) {
						
						result = new BankDAO().deleteBankDepositDetailsByRO(siteId);
						if (result) {
							responseObj.setError(null);
							responseObj.setErrorCode("0000");
							responseObj.setStatus("Success");
						} else {
							responseObj.setError("Unable to delete the deposit details.");
							responseObj.setErrorCode("0002");
							responseObj.setStatus("ERROR");
						}
					} else {
						responseObj.setErrorCode("0");
						responseObj.setError("Error Site Id NULL");
						responseObj.setStatus("Failed");
					}
				} else {
					responseObj.setError("Wrong Token @deleteBankDetails");
					responseObj.setErrorCode("9997");
					responseObj.setStatus("ERROR");
				}
			} else {
				responseObj.setError("Required header detail missing @deleteBankDetails");
				responseObj.setErrorCode("9998");
				responseObj.setStatus("ERROR");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			responseObj.setError("Unexpected Error @deleteBankDetails" + e.getMessage());
			responseObj.setErrorCode("9999");
			responseObj.setStatus("ERROR");
		}

		str = gson.toJson(responseObj);
		return RestUtils.getReponse(Response.Status.CREATED, str, "application/json");
	}
	
	@POST
	@Consumes({ "application/json" })
	@Path("/getBankDetails/")
	@RequestLogger
	public Response getBankDetails(@Context HttpHeaders headers, @Context UriInfo ui, @Context ServletContext context,
			String reqJsonString) {
		String str = null;
		String token = context.getInitParameter("authKey");
		String htoken = null;
		Gson gson = new Gson();
		ResponseBO responseObj = new ResponseBO();
		BankDetailsBO reqObj = null;
		JSONArray result =  new JSONArray();
		
		try {

			if (headers.getRequestHeader("token") != null && headers.getRequestHeader("token").size() > 0)
				htoken = headers.getRequestHeader("token").get(0);

			if (htoken != null) {
				if (htoken.equalsIgnoreCase(token)) {
					
					String siteId = null;
					String fromDate = null;
					String mode = null;
					reqObj = gson.fromJson(reqJsonString, BankDetailsBO.class);
		
					siteId = reqObj.getRo_id();
					fromDate = reqObj.getFromDate();
					mode = reqObj.getMode();
					
					if(fromDate != null) {
						result = new BankDAO().getBankDepositDetailsByFromdate(siteId,fromDate);
						if (result.size() > 0) {
							responseObj.setDataSet(result);
							responseObj.setError(null);
							responseObj.setErrorCode("0000");
							responseObj.setStatus("Success");
						} else {
							responseObj.setError("Unable to fetch the deposit details.");
							responseObj.setErrorCode("0002");
							responseObj.setStatus("ERROR");
						}
						
					} else if ("ByRO".equalsIgnoreCase(mode)) {
						
						result = new BankDAO().getBankDepositDetailsByRO(siteId);
						if (result.size() > 0) {
							responseObj.setDataSet(result);
							responseObj.setError(null);
							responseObj.setErrorCode("0000");
							responseObj.setStatus("Success");
						} else {
							responseObj.setError("Unable to fetch the deposit details.");
							responseObj.setErrorCode("0002");
							responseObj.setStatus("ERROR");
						}
					}  else if("ByTransNo".equalsIgnoreCase(mode)) {
						JSONObject obj = new BankDAO().getBankDepositDetailsByTransactionNo(siteId,reqObj.getTransaction_no());
						if (obj != null)  {
							responseObj.setDataObj(obj);
							responseObj.setError(null);
							responseObj.setErrorCode("0000");
							responseObj.setStatus("Success");
						} else {
							responseObj.setError("Unable to fetch the deposit details.");
							responseObj.setErrorCode("0002");
							responseObj.setStatus("ERROR");
						}
						
					}else {
						responseObj.setErrorCode("0");
						responseObj.setError("Error No mode matching");
						responseObj.setStatus("Failed");
					}
				} else {
					responseObj.setError("Wrong Token @getBankDetails");
					responseObj.setErrorCode("9997");
					responseObj.setStatus("ERROR");
				}
			} else {
				responseObj.setError("Required header detail missing @getBankDetails");
				responseObj.setErrorCode("9998");
				responseObj.setStatus("ERROR");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			responseObj.setError("Unexpected Error @getBankDetails" + e.getMessage());
			responseObj.setErrorCode("9999");
			responseObj.setStatus("ERROR");
		}

		str = gson.toJson(responseObj);
		return RestUtils.getReponse(Response.Status.CREATED, str, "application/json");
	}
	
	@POST
	@Consumes({ "application/json" })
	@Path("/getAllBankDetails/")
	@RequestLogger
	public Response getAllBankDetails(@Context HttpHeaders headers, @Context UriInfo ui, @Context ServletContext context,
			String reqJsonString) {
		String str = null;
		String token = context.getInitParameter("authKey");
		String htoken = null;
		Gson gson = new Gson();
		ResponseBO responseObj = new ResponseBO();
		BankDetailsBO reqObj = null;
		JSONObject result =  null;
		
		try {

			if (headers.getRequestHeader("token") != null && headers.getRequestHeader("token").size() > 0) {
				htoken = headers.getRequestHeader("token").get(0);

			if (htoken != null) {
				if (htoken.equalsIgnoreCase(token)) {
					String userId = null;
					String roleId = null;
					String fromDate = null;
					String toDate = null;
					String siteIDs = null;

					TSMBO tsmBO = gson.fromJson(reqJsonString, TSMBO.class);
					userId = tsmBO.getUserId();
					roleId = tsmBO.getRoleId();
					fromDate = tsmBO.getFromDate();
					toDate = tsmBO.getToDate();
					siteIDs = tsmBO.getSiteID();
					
					if (siteIDs == null)
						siteIDs = new HierarchyFT().getUserHierarchyFT(userId, roleId, "OMAN");
					
					if (siteIDs != null) {
						
						result = new BankDAO().getBankDepositDetailsByRole(siteIDs,fromDate);
						if (result.size() > 0) {
							responseObj.setDataObj(result);
							responseObj.setError(null);
							responseObj.setErrorCode("0000");
							responseObj.setStatus("Success");
						} else {
							responseObj.setError("Unable to fetch the deposit details.");
							responseObj.setErrorCode("0002");
							responseObj.setStatus("ERROR");
						}
											
					}else {
						responseObj.setErrorCode("0");
						responseObj.setError("Error Site Id NULL");
						responseObj.setStatus("Failed");
					}
				}} else {
					responseObj.setError("Wrong Token @getBankDetails");
					responseObj.setErrorCode("9997");
					responseObj.setStatus("ERROR");
				}
			} else {
				responseObj.setError("Required header detail missing @getBankDetails");
				responseObj.setErrorCode("9998");
				responseObj.setStatus("ERROR");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			responseObj.setError("Unexpected Error @getBankDetails" + e.getMessage());
			responseObj.setErrorCode("9999");
			responseObj.setStatus("ERROR");
		}

		str = gson.toJson(responseObj);
		return RestUtils.getReponse(Response.Status.CREATED, str, "application/json");
	}
	
	
	@POST
	@Consumes({ "application/json" })
	@Path("/getMSDropdown/")
	@RequestLogger
	public Response getMSDropdown(@Context HttpHeaders headers, @Context UriInfo ui, @Context ServletContext context,
			String reqJsonString) {
		String str = null;
		String token = context.getInitParameter("authKey");
		String htoken = null;
		Gson gson = new Gson();
		ResponseBO responseObj = new ResponseBO();
	
		JSONArray result =  new JSONArray();
		
		try {

			if (headers.getRequestHeader("token") != null && headers.getRequestHeader("token").size() > 0)
				htoken = headers.getRequestHeader("token").get(0);

			if (htoken != null) {
				if (htoken.equalsIgnoreCase(token)) {
					
					String masterType = "";
					
					JsonElement jsonElement =  new JsonParser().parse(reqJsonString);
					JsonObject jsonObject = jsonElement.getAsJsonObject();
					
					
		
					masterType = jsonObject.get("mastertype").getAsString();
					if (masterType != null) {
						
						result = new BankDAO().getMasterTypeList(masterType);
						if (result.size() > 0) {
							responseObj.setDataSet(result);
							responseObj.setError(null);
							responseObj.setErrorCode("0000");
							responseObj.setStatus("Success");
						} else {
							responseObj.setError("Unable to fetch the master Type List.");
							responseObj.setErrorCode("0002");
							responseObj.setStatus("ERROR");
						}
					} else {
						responseObj.setErrorCode("0");
						responseObj.setError("Error Site Id NULL");
						responseObj.setStatus("Failed");
					}
				} else {
					responseObj.setError("Wrong Token @getBankDetails");
					responseObj.setErrorCode("9997");
					responseObj.setStatus("ERROR");
				}
			} else {
				responseObj.setError("Required header detail missing @getBankDetails");
				responseObj.setErrorCode("9998");
				responseObj.setStatus("ERROR");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			responseObj.setError("Unexpected Error @getBankDetails" + e.getMessage());
			responseObj.setErrorCode("9999");
			responseObj.setStatus("ERROR");
		}

		str = gson.toJson(responseObj);
		return RestUtils.getReponse(Response.Status.CREATED, str, "application/json");
	}
	
	
	@POST
	@Consumes({ "application/json" })
	@Path("/getBankAcountNumber/")
	@RequestLogger
	public Response getBankAcountNumber(@Context HttpHeaders headers, @Context UriInfo ui, @Context ServletContext context,
			String reqJsonString) {
		String str = null;
	//	System.out.println("Im in");
		String token = context.getInitParameter("authKey");
		String htoken = null;
		Gson gson = new Gson();
		ResponseBO responseObj = new ResponseBO();
	
		JSONArray result =  new JSONArray();
		
		try {

			if (headers.getRequestHeader("token") != null && headers.getRequestHeader("token").size() > 0)
				htoken = headers.getRequestHeader("token").get(0);

			if (htoken != null) {
				if (htoken.equalsIgnoreCase(token)) {
					
					String site_id = "";
					
					JsonElement jsonElement =  new JsonParser().parse(reqJsonString);
					JsonObject jsonObject = jsonElement.getAsJsonObject();
					
					if(reqJsonString.contains("site_id")) {
						site_id = jsonObject.get("site_id").getAsString();
						if (site_id != null) {
							result = new BankDAO().getBankAcountNo(site_id);
							
						} else {
							responseObj.setErrorCode("0");
							responseObj.setError("Error Site Id NULL");
							responseObj.setStatus("Failed");
						}
					} else {
						result = new BankDAO().getAllBankAcountNo();
					}
					if (result.size() > 0) {
						responseObj.setDataSet(result);
						responseObj.setError(null);
						responseObj.setErrorCode("0000");
						responseObj.setStatus("Success");
					} else {
						responseObj.setError("Unable to fetch the account details List.");
						responseObj.setErrorCode("0002");
						responseObj.setStatus("ERROR");
					}
				} else {
					responseObj.setError("Wrong Token @getBankDetails");
					responseObj.setErrorCode("9997");
					responseObj.setStatus("ERROR");
				}
			} else {
				responseObj.setError("Required header detail missing @getBankDetails");
				responseObj.setErrorCode("9998");
				responseObj.setStatus("ERROR");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			responseObj.setError("Unexpected Error @getBankDetails" + e.getMessage());
			responseObj.setErrorCode("9999");
			responseObj.setStatus("ERROR");
		}

		str = gson.toJson(responseObj);
		return RestUtils.getReponse(Response.Status.CREATED, str, "application/json");
	}
}
