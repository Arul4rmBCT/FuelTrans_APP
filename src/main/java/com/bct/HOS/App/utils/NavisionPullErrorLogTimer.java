package com.bct.HOS.App.utils;

import java.io.IOException;
import java.io.StringReader;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Date;
import java.util.TimerTask;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.http.HttpEntity;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.NTCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class NavisionPullErrorLogTimer extends TimerTask {
	
	
	private String urlString = null;
	private String messageStr = null;
	private final String soap11NS = "http://schemas.xmlsoap.org/soap/envelope/";
	private final String soap12NS = "http://www.w3.org/2003/05/soap-envelope";
		
	 public NavisionPullErrorLogTimer(String urlString, String messageStr) {
		this.urlString = urlString;
		this.messageStr = messageStr;
	}
	
	 @SuppressWarnings("resource")
	@Override
	 public void run() {
	        System.out.println("NavisionPullErrorLogTimer task started at:"+new Date());
	        Date dateObj = null;
	        String xmlStr = null;
	        String todayDt = null;
	        Connection conn = null;
	        Statement st = null;
	        DBConnector dbc = null;
	        ResultSet rs = null;
	        URLConnection urlConn = null;
	        URL url = null;
	        dbc = new DBConnector();
        	conn = dbc.getConnection();
    		String schema = dbc.getSchema();
			try {
				/*dateObj = getPreviousDt();
				todayDt = getTodayDt();
	    		String sql = "insert into "+schema+".navision_job_details (job_type,data_date,generate_status,send_status) values (?,?,?,?) returning job_id";
	    		String sql = "insert into "+schema+".navision_job_details (job_type,data_date,generate_status,send_status,local_path,remote_path,file_name,status_text) values (?,?,?,?,?,?,?,?) returning job_id";
				PreparedStatement statement = conn.prepareStatement(sql);
				statement.setString(1,transType);
				java.sql.Date sqlPackageDate
	            = new java.sql.Date(dateObj.getTime());
				statement.setDate(2,sqlPackageDate);
				statement.setString(3,"F");
				statement.setString(4,"F");
				rs = statement.executeQuery();
				String jobId = null;
				statement.close();*/
				/*String res = null;
				StringBuffer sb = new StringBuffer();
				url = new URL(urlString);
				urlConn = (HttpURLConnection) url.openConnection();
				((HttpURLConnection) urlConn).setRequestMethod("POST");
				urlConn.setConnectTimeout(60);
				urlConn.setReadTimeout(60);
				urlConn.setDoOutput(true);
				urlConn.setDoInput(true);
				urlConn.setAllowUserInteraction(true);
				urlConn.setUseCaches(false);
				urlConn.setRequestProperty("Content-Type", "application/xml");
				//Authenticator.setDefault(new BasicAuthenticator());
				String userpass = "MAHA\\FSAS" + ":" + "FS*1650$As";
				String basicAuth = "Basic " + new String(Base64.getEncoder().encode(userpass.getBytes()));
				urlConn.setRequestProperty ("Authorization", basicAuth);
				
				BufferedWriter out = new BufferedWriter(new OutputStreamWriter(
						urlConn.getOutputStream()));
				out.write("");
				out.flush();
				out.close();
				BufferedReader in = new BufferedReader(new InputStreamReader(
						urlConn.getInputStream()));

				while ((res = in.readLine()) != null) {
					sb.append(res);
				}
				System.out.println("TEST LOG | The http request is: "+sb.toString());
				in.close();*/
			} catch (Exception e) {
				e.printStackTrace();
			}
			String finalStr = null;
		try {
			String inputEnvelop = getInputMessage();
			System.out.println("The input SOAP envelop is:.............. "+inputEnvelop);
			finalStr = invokeWebService();
			System.out.println("The output SOAP envelop is ........: "+finalStr);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Exception occurred at calling Error log webservice ............."+e.getMessage());
		}
		
		try {
			if(finalStr != null) {
				Node rootNode = getBodyObject(finalStr);
				String rootName = rootNode.getNodeName();
				if("ReadMultiple_Result".equals(rootName)) {
					NodeList nodeList1 = rootNode.getChildNodes();
					for(int nodeCt1 = 0; nodeCt1 < nodeList1.getLength(); nodeCt1++) {
						Node chldNode1 = nodeList1.item(nodeCt1);
						String chldNdName1 = chldNode1.getNodeName();
						System.out.println(chldNdName1);
						if("ReadMultiple_Result".equals(chldNdName1)) {
							NodeList nodeList2 = chldNode1.getChildNodes();
							for(int nodeCt2 = 0; nodeCt2 < nodeList2.getLength(); nodeCt2++) {
								Node chldNode2 = nodeList2.item(nodeCt2);
								String chldNdName2 = chldNode2.getNodeName();
								System.out.println(chldNdName2);
								if("FSErrorLOG".equals(chldNdName2)) {
									NodeList chldnodeList3 = chldNode2.getChildNodes();
									String entNo = null;
									String fileName = null;
									String modiFileName = null;
									String errorMsg = null;
									for(int chlnodeCt3 = 0; chlnodeCt3 < chldnodeList3.getLength(); chlnodeCt3++) {
										Node grChldNode = chldnodeList3.item(chlnodeCt3);
										if("Entry_No".equals(grChldNode.getNodeName())) {
												if(grChldNode.getFirstChild() != null) {
														entNo = grChldNode.getFirstChild().getNodeValue();
												}
												System.out.println("entNo: "+entNo);
											} else if("File_Name".equals(grChldNode.getNodeName())) {
												if(grChldNode.getFirstChild() != null) {
													fileName = grChldNode.getFirstChild().getNodeValue();
													modiFileName = fileName.replace("\\", "/");
													int lastIndex = modiFileName.lastIndexOf("/");
													if(lastIndex > 0) {
														modiFileName = modiFileName.substring(lastIndex+1);
													}
												}
											} else if("Message".equals(grChldNode.getNodeName())) {
												if(grChldNode.getFirstChild() != null) {
													errorMsg = grChldNode.getFirstChild().getNodeValue();
												}
											}
										}
										System.out.println("TEST LOG | entNo ..............."+entNo);
										System.out.println("TEST LOG | fileName ..............."+fileName);
										System.out.println("TEST LOG | modified fileName ..............."+modiFileName);
										System.out.println("TEST LOG | errorMsg ..............."+errorMsg);
										String sqlQuery = "update "+schema+".navision_job_details set remote_ref=?, remote_error=?, ack_receive_status='T', ack_send_status='F' where file_name=?";
										PreparedStatement statement = conn.prepareStatement(sqlQuery);
										statement.setString(1,entNo);
										statement.setString(2,errorMsg);
										statement.setString(3,modiFileName);
										int upCount = statement.executeUpdate();
										System.out.println("TEST LOG | Navision update count ..............."+upCount);
										statement.close();
									}
								}
							}
						}
					}
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Exception occurred while updating Error details ............."+e.getMessage());
		} finally {
			try {
				if(rs != null) {
					rs.close();
				}
				if(st != null) {
					st.close();
				}
				dbc.closeConnection(conn);
			} catch(Exception ex) {
				
			}
		}
	 }
	  
	
	 public static void main(String abc[]) {
		 String url = "http://srv22.almaha.com.om:1396/ALM_2021_NOV_DEV/WS/Al%20Maha%20Petroleum%20Products%20Co./Page/FSErrorLOG";
		String payLoad = "<fser:Read xmlns:fser=\"urn:microsoft-dynamics-schemas/page/fserrorlog\"><fser:Entry_No></fser:Entry_No></fser:Read>";
		 NavisionPullErrorLogTimer rv = new NavisionPullErrorLogTimer(url,payLoad);
		 rv.run();
	 }
	 
	 private String getInputMessage() {
		 
		 String inputMsg = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:fser=\"urn:microsoft-dynamics-schemas/page/fserrorlog\"> " +
				   "<soapenv:Header/>" +
				   "<soapenv:Body>" +
				      "<fser:ReadMultiple>" +
				         "<fser:filter>" +
				            "<fser:Field></fser:Field>" +
				            "<fser:Criteria></fser:Criteria>" +
				         "</fser:filter>" +
				         "<fser:bookmarkKey></fser:bookmarkKey>" +
				         "<fser:setSize></fser:setSize>" +
				      "</fser:ReadMultiple>" +
				   "</soapenv:Body>" +
				"</soapenv:Envelope>";
		 return inputMsg;
	 }
	 
	 private Node getBodyObject(String resposeString) throws ParserConfigurationException, SAXException, IOException {
		 DocumentBuilderFactory dbf=DocumentBuilderFactory.newInstance();
		  dbf.setNamespaceAware(true);
		  DocumentBuilder db=dbf.newDocumentBuilder();
		  Document document=db.parse(new InputSource(new StringReader(resposeString)));
		  Node soapBody=document.getElementsByTagNameNS(soap11NS,"Body").item(0);
		  return soapBody.getFirstChild();
		}
	 
	 public String invokeWebService() {
		   String bodyAsString = getInputMessage(); //Provide Input SOAP Message

		   CredentialsProvider credsProvider = new BasicCredentialsProvider();
		   credsProvider.setCredentials(AuthScope.ANY,
		      new NTCredentials("FSAS", "FS*1650$As", "srv22.almaha.com.om", "MAHA"));

		   HttpClient client = HttpClientBuilder.create().setDefaultCredentialsProvider(credsProvider).build();
		   HttpPost post = new HttpPost(urlString); //Provide Request URL
		   try {

		      StringEntity input = new StringEntity(bodyAsString);
		      input.setContentType("text/xml; charset=utf-8");
		      post.setEntity(input);
		      post.setHeader("Content-type", "text/xml; charset=utf-8");
		      post.setHeader("SOAPAction", "urn:microsoft-dynamics-schemas/page/fserrorlog:ReadMultiple"); //Provide Soap action
		      org.apache.http.HttpResponse response = client.execute(post);
		      HttpEntity responseEntity = response.getEntity();
		      if (responseEntity != null) {
		         return EntityUtils.toString(responseEntity);
		      }
		   } catch (IOException ex) {
			   System.out.println("Exception occurred while invoking Navision webservice: "+ex.getMessage());
		      ex.printStackTrace();
		   }
		   return null;
		}
	 
	 /*
	  * 
	  CREATE TABLE "ALMAHA".navision_job_details
(
    seq_id SERIAL,
    job_id character varying(10) NOT NULL DEFAULT LPAD(nextval('"ALMAHA".Navision_Job_id_seq'::regclass)::text, 5, '0'),
    job_type character varying(20) NOT NULL,
    data_date date,
    job_date timestamp DEFAULT CURRENT_TIMESTAMP,
	remote_ref character varying(20),
    generate_status character(1),
    send_status character(1),
    local_path character varying(100),
    remote_path character varying(100),
    file_name character varying(50),
    ack_receive_status character(1),
    ack_send_status character(1),
    status_text character varying(200),
	remote_error text,
    CONSTRAINT navision_job_pkey PRIMARY KEY (job_id)
)
	  */
}
