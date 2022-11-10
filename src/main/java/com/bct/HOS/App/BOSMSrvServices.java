package com.bct.HOS.App;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

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
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

import com.bct.HOS.App.BO.ResponseBO;
import com.bct.HOS.App.BO.TSMBO;
import com.bct.HOS.App.DAO.BOSDAO;
import com.bct.HOS.App.DAO.HierarchyFT;
import com.bct.HOS.App.DAO.PriceDAO;
import com.bct.HOS.App.utils.HOSConfig;
import com.bct.HOS.App.utils.RequestLogger;
import com.bct.HOS.App.utils.RestUtils;
import com.bct.HOS.BOS.BO.BOSBookReqBO;
import com.bct.HOS.Fleet.PreTransReqBO;
import com.bct.HOS.Fleet.PreTransResBO;
import com.google.gson.Gson;

import net.sf.json.JSONArray;

@Path("/v1")
public class BOSMSrvServices {

	@POST
	@Path("/upload")
	@Consumes("multipart/form-data")
	@RequestLogger
	public Response uploadFile(@Context HttpRequest request, MultipartFormDataInput input) {
		String fileName = "";
		Gson gson = new Gson();
		ResponseBO responseObj = new ResponseBO();
		try {
			boolean uploadSts = false;
			String siteId = input.getFormDataPart("siteId", String.class, null);
			String date = input.getFormDataPart("date", String.class, null);
			String currencyCode = input.getFormDataPart("currencyCode", String.class, null);
			double deposit = input.getFormDataPart("deposit", Double.class, null);
			double expenses = input.getFormDataPart("expenses", Double.class, null);

			////System.out.println(siteId);
			////System.out.println(date);
			////System.out.println(currencyCode);
			////System.out.println(deposit);
			////System.out.println(expenses);
			
			HOSConfig conf = new HOSConfig();
			String UPLOADED_FILE_PATH = conf.getValue("TMP_LocalPath");

			Map<String, List<InputPart>> uploadForm = input.getFormDataMap();
			List<InputPart> inputParts = uploadForm.get("uploadedFile");
			////System.out.println("**************");
			for (InputPart inputPart : inputParts) {
				try {
					////System.out.println(inputParts.size());
					MultivaluedMap<String, String> header = inputPart.getHeaders();
					fileName = getFileName(header);

					// convert the uploaded file to inputstream
					InputStream inputStream = inputPart.getBody(InputStream.class, null);

					byte[] bytes = IOUtils.toByteArray(inputStream);
					fileName = UPLOADED_FILE_PATH + fileName;
					////System.out.println(fileName);
					////System.out.println(bytes.length);
					uploadSts = writeFile(bytes, fileName);
					////System.out.println(uploadSts);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			////System.out.println("**************END!..."+siteId+"/"+uploadSts);
			if (siteId != null && uploadSts) {
				boolean check = new BOSDAO().checkEntry(siteId, date);
				if (check) {
					responseObj.setErrorCode("0");
					responseObj.setError("Error book entry made for " + date);
					responseObj.setStatus("Failed");
				} else {
					boolean result = new BOSDAO().entryDailyBookDetails(siteId, date, currencyCode, deposit, expenses,fileName);
					////System.out.println("bookEntry=" + result);
					responseObj.setErrorCode("1");
					responseObj.setError("");
					responseObj.setStatus("Success");
				}
			} else {
				responseObj.setErrorCode("0");
				responseObj.setError("Error Site Id NULL");
				responseObj.setStatus("Failed");
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		return Response.status(200).entity(gson.toJson(responseObj)).build();
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
	@Path("/bookEntry/")
	@RequestLogger
	public Response bookEntry(@Context HttpHeaders headers, @Context UriInfo ui, @Context ServletContext context,
			String reqJsonString) {
		String str = null;
		Gson gson = new Gson();
		ResponseBO responseObj = new ResponseBO();
		BOSBookReqBO reqObj = null;

		String siteId = null;
		String date = null;
		String currencyCode = null;
		double deposit = 0;
		double expenses = 0;
		boolean result = false;
		try {

			reqObj = gson.fromJson(reqJsonString, BOSBookReqBO.class);

			siteId = reqObj.getSiteId();
			date = reqObj.getDate();
			currencyCode = reqObj.getCurrencyCode();
			deposit = reqObj.getDeposit();
			expenses = reqObj.getExpenses();
			if (siteId != null) {
				boolean check = new BOSDAO().checkEntry(siteId, date);
				if (check) {
					responseObj.setErrorCode("0");
					responseObj.setError("Error book entry made for " + date);
					responseObj.setStatus("Failed");
				} else {
					result = new BOSDAO().entryDailyBookDetails(siteId, date, currencyCode, deposit, expenses,null);
					////System.out.println("bookEntry=" + result);
					responseObj.setErrorCode("1");
					responseObj.setError("");
					responseObj.setStatus("Success");
				}
			} else {
				responseObj.setErrorCode("0");
				responseObj.setError("Error Site Id NULL");
				responseObj.setStatus("Failed");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			responseObj.setErrorCode("0");
			responseObj.setError("Error");
			responseObj.setStatus("Failed");
		}

		str = gson.toJson(responseObj);
		return RestUtils.getReponse(Response.Status.CREATED, str, "application/json");
	}

	@POST
	@Consumes({ "application/json" })
	@Path("/getBookEntry/")
	@RequestLogger
	public Response getBookEntry(@Context HttpHeaders headers, @Context UriInfo ui, @Context ServletContext context,
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
					String date = null;
					String siteIDs = null;
					String country = null;

					TSMBO tsmBO = gson.fromJson(reqJsonString, TSMBO.class);
					userId = tsmBO.getUserId();
					roleId = tsmBO.getRoleId();
					siteIDs = tsmBO.getSiteID();
					country = tsmBO.getCountry();
					date = tsmBO.getDate();

					if (siteIDs == null)
						siteIDs = new HierarchyFT().getUserHierarchyFT(userId, roleId, country);
					////System.out.println("siteIDs:" + siteIDs);
					if (siteIDs != null) {
						HOSConfig config = new HOSConfig();
						String fileLocation=config.getValue("BOS_FILE_LOCATION");
						String fileURL = config.getValue("BOS_FILE_URL");
						dataSet = new BOSDAO().getDailyBookDetails(siteIDs, date,fileLocation,fileURL);
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
					responseObj.setError("Wrong Token @getBookEntry");
					responseObj.setErrorCode("9997");
					responseObj.setStatus("ERROR");
				}
			} else {
				responseObj.setError("Required header detail missing @getBookEntry");
				responseObj.setErrorCode("9998");
				responseObj.setStatus("ERROR");
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			responseObj.setError("Unexpected Error @getBookEntry");
			responseObj.setErrorCode("9999");
			responseObj.setStatus("ERROR");
		}

		str = gson.toJson(responseObj);
		return RestUtils.getReponse(Response.Status.CREATED, str, "application/json");
	}

}
