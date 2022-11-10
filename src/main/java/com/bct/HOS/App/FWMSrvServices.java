package com.bct.HOS.App;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.UriInfo;

import org.apache.commons.io.IOUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.http.HttpRequest;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

import com.bct.HOS.App.BO.ResponseBO;
import com.bct.HOS.App.BO.TSMBO;
import com.bct.HOS.App.BO.UserBO;
import com.bct.HOS.App.BO.WidgetData;
import com.bct.HOS.App.DAO.DeliveryDAO;
import com.bct.HOS.App.DAO.DeviceDAO;
import com.bct.HOS.App.DAO.HierarchyFT;
import com.bct.HOS.App.DAO.UserDAO;
import com.bct.HOS.App.DAO.UtilDAO;
import com.bct.HOS.App.utils.HOSConfig;
import com.bct.HOS.App.utils.InMem;
import com.bct.HOS.App.utils.RequestLogger;
import com.bct.HOS.App.utils.RestUtils;
import com.bct.HOS.BOS.BOSXMLGenerator;
import com.bct.HOS.BOS.BO.GeneralConfiguration;
import com.google.gson.Gson;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import com.jcraft.jsch.SftpProgressMonitor;

import net.sf.json.JSONArray;

@Path("/v1")
public class FWMSrvServices {

	@POST
	@Path("/upload")
	@Consumes("multipart/form-data")
	@RequestLogger
	public Response uploadFile(@Context HttpRequest request, MultipartFormDataInput input) {
		String fileName = "";
	    try {
			String dupFileName = "";
			boolean fileStatus = false;
			String country = input.getFormDataPart("country", String.class, null);
					    
			HOSConfig conf = new HOSConfig();
			String UPLOADED_FILE_PATH = conf.getValue(country+"_FW_Path_Local");
			String hostname = conf.getValue(country+"_FW_FTP");
			String username = conf.getValue(country+"_FW_UNAME");
			String password = conf.getValue(country+"_FW_PSW");
			String copyTo = conf.getValue(country+"_FW_COPYTO");
			int port = Integer.parseInt(conf.getValue(country+"_FW_PORT"));
			String protocol = conf.getValue(country+"_FW_PROTOCOL");
	
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
					dupFileName = fileName;
					fileName = UPLOADED_FILE_PATH + fileName;
	
					boolean uploadSts = writeFile(bytes, fileName);
					if (uploadSts) {
						fileStatus = putFile(hostname, username, password, fileName, copyTo, port, protocol, dupFileName);
					}
					if (!fileStatus)
						return Response.status(400).entity(" FTP Failed ").build();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
	    } catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		return Response.status(200).entity(" File (" + fileName + ") uploaded to FTP ").build();
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

	/**
	 * 
	 */
	final static SftpProgressMonitor monitor = new SftpProgressMonitor() {
		public void init(final int op, final String source, final String target, final long max) {
			//System.out.println("sftp start uploading file from:" + source + " to:" + target);
		}

		public boolean count(final long count) {
			//System.out.println("sftp sending bytes: " + count);
			return true;
		}

		public void end() {
			//System.out.println("sftp uploading is done.");
		}
	};

	/**
	 * 
	 * @param hostname
	 * @param username
	 * @param password
	 * @param copyFrom
	 * @param copyTo
	 * @throws JSchException
	 * @throws SftpException
	 */
	public boolean putFile(String hostname, String username, String password, String copyFrom, String copyTo, int port,
			String protocol, String fileName) throws JSchException, SftpException {
		//System.out.println("Initiate sending file to Server...");
		boolean moved = false;
		try {

			if (protocol.equalsIgnoreCase("SFTP")) {

				JSch jsch = new JSch();
				Session session = null;
				//System.out.println("Trying to connect.....");
				session = jsch.getSession(username, hostname, port);
				session.setConfig("StrictHostKeyChecking", "no");
				session.setPassword(password);
				session.connect();
				//System.out.println("is server connected? " + session.isConnected());

				Channel channel = session.openChannel(protocol);
				channel.connect();
				ChannelSftp sftpChannel = (ChannelSftp) channel;
				//System.out.println("Server's home directory: " + sftpChannel.getHome());
				try {
					sftpChannel.put(copyFrom, copyTo, monitor, ChannelSftp.OVERWRITE);
				} catch (SftpException e) {
					moved = false;
					//System.out.println("file was not found: " + copyFrom);
				}
				moved = true;
				sftpChannel.exit();
				session.disconnect();

			} else {

				FTPClient ftpClient = new FTPClient();
				ftpClient.connect(hostname, port);
				showServerReply(ftpClient);

				int replyCode = ftpClient.getReplyCode();
				if (!FTPReply.isPositiveCompletion(replyCode)) {
					//System.out.println("Connect failed");
					moved = false;
				}

				boolean success = ftpClient.login(username, password);
				showServerReply(ftpClient);

				if (!success) {
					//System.out.println("Could not login to the server");
					return false;
				}

				// Changes working directory
				success = ftpClient.changeWorkingDirectory("/");
				showServerReply(ftpClient);

				if (success) {
					//System.out.println("Successfully changed working directory.");
					FileInputStream fis = new FileInputStream(copyFrom);
					moved = ftpClient.storeFile(fileName, fis);

					//System.out.println("***********");
					//System.out.println(copyFrom);
					//System.out.println(copyTo);
					//System.out.println(fileName);
					//System.out.println("***********");
					//System.out.println(ftpClient.getReplyCode());
					showServerReply(ftpClient);
					//System.out.println("***********");

					//System.out.println(moved);
					fis.close();
				} else {
					//System.out.println("Failed to change working directory. See server's reply.");
					moved = false;
				}
				// logs out
				ftpClient.logout();
				ftpClient.disconnect();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return moved;
	}

	private static void showServerReply(FTPClient ftpClient) {
		String[] replies = ftpClient.getReplyStrings();
		if (replies != null && replies.length > 0) {
			for (String aReply : replies) {
				//System.out.println("SERVER: " + aReply);
			}
		}
	}

	@POST
	@Consumes({ "application/json" })
	@Path("/storeFW/")
	@RequestLogger
	public Response storeFW(@Context HttpHeaders headers, @Context UriInfo ui, @Context ServletContext context,
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

					String fccVersion = null;
					String versionName = null;
					String releaseNote = null;
					String comment = null;
					String country = null;

					TSMBO tsmBO = gson.fromJson(reqJsonString, TSMBO.class);
					fccVersion = tsmBO.getFccVersion();
					versionName = tsmBO.getVersionName();
					releaseNote = tsmBO.getReleaseNote();
					comment = tsmBO.getComment();
					country = tsmBO.getCountry(); 

					if (fccVersion != null && versionName != null && releaseNote != null) {
						new DeviceDAO().storeFW(fccVersion, versionName, releaseNote, comment,country);
						responseObj.setDataSet(null);
						responseObj.setError(null);
						responseObj.setErrorCode("0000");
						responseObj.setStatus("Success");
					} else {
						responseObj.setError("No Data!.");
						responseObj.setErrorCode("0001");
						responseObj.setStatus("Success");
					}
				} else {
					responseObj.setError("Wrong Token @getStoreFW");
					responseObj.setErrorCode("9997");
					responseObj.setStatus("ERROR");
				}
			} else {
				responseObj.setError("Required header detail missing @getStoreFW");
				responseObj.setErrorCode("9998");
				responseObj.setStatus("ERROR");
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			responseObj.setError("Unexpected Error @getStoreFW");
			responseObj.setErrorCode("9999");
			responseObj.setStatus("ERROR");
		}

		str = gson.toJson(responseObj);
		return RestUtils.getReponse(Response.Status.CREATED, str, "application/json");
	}

	@POST
	@Consumes({ "application/json" })
	@Produces("application/xml")
	@Path("/getBOSXML/")
	@RequestLogger
	public Response getBOSXML(@Context HttpHeaders headers, @Context UriInfo ui, @Context ServletContext context,
			String reqJsonString) {
		String str = null;
		String token = context.getInitParameter("authKey");
		String htoken = null;
		Gson gson = new Gson();
		ResponseBO responseObj = new ResponseBO();
		ResponseBuilder response = Response.ok();
		try {

			if (headers.getRequestHeader("token") != null && headers.getRequestHeader("token").size() > 0)
				htoken = headers.getRequestHeader("token").get(0);

			if (htoken != null) {
				if (htoken.equalsIgnoreCase(token)) {
					HOSConfig config = new HOSConfig();
					//System.out.println("getBOSXML===="+reqJsonString);
					GeneralConfiguration bosConf = gson.fromJson(reqJsonString, GeneralConfiguration.class);
					String roCode = bosConf.getObjSiteConfig().getSite_ID();
					if(new BOSXMLGenerator().generateXML(roCode, bosConf)) {
						String filePAth = config.getValue("BOS_XML_Path")+roCode + ".xml";
						File file = new File(filePAth);  					   
				        response = Response.ok((Object) file);  
				        response.header("Content-Disposition","attachment; filename=\"BAHWAN.xml\"");
					}
				} else {
					
				}
			} else {

			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		return response.build();
	}
	
	
	@POST
	@Consumes({ "application/json" })
	@Path("/validateRO/")
	@RequestLogger
	public Response ValidateRO(@Context HttpHeaders headers, @Context UriInfo ui, @Context ServletContext context,
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
					HOSConfig config = new HOSConfig();
					UserBO userBo = new UserBO();
					String userId = null;
					String siteId = null;
					
					userBo = gson.fromJson(reqJsonString, UserBO.class);
					siteId=userBo.getSiteID();
					userId = userBo.getUserId();
					
					dataSet=new UtilDAO().callFTROValidateService(siteId, userId, config.getValue("FT_URL"));
					responseObj.setDataSet(dataSet);
					responseObj.setError(null);
					responseObj.setErrorCode("0000");
					responseObj.setStatus("Success");
				} else {
					responseObj.setError("Wrong Token @ValidateRO");
					responseObj.setErrorCode("9997");
					responseObj.setStatus("ERROR");
				}
			} else {
				responseObj.setError("Required header detail missing @ValidateRO");
				responseObj.setErrorCode("9998");
				responseObj.setStatus("ERROR");
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			responseObj.setError("Unexpected Error @ValidateRO");
			responseObj.setErrorCode("9999");
			responseObj.setStatus("ERROR");
		}

		str = gson.toJson(responseObj);
		return RestUtils.getReponse(Response.Status.CREATED, str, "application/json");
	}

}
