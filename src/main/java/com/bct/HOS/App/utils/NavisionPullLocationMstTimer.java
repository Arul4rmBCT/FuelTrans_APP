package com.bct.HOS.App.utils;

import java.io.IOException;
import java.io.StringReader;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimerTask;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class NavisionPullLocationMstTimer extends TimerTask {
	
	
	private String urlString = null;
	private String messageStr = null;
	
	private final String SOAP_URL = "http://SRV22.almaha.com.om:1396/ALM_2021_NOV_DEV/WS/Al%20Maha%20Petroleum%20Products%20Co./Page/FSAS_Location_Master";
	private final String SOAP_ACTION = "http://www.w3.org/2003/05/soap-envelope";
	private final String soap11NS = "http://schemas.xmlsoap.org/soap/envelope/";
	private final String soap12NS = "http://www.w3.org/2003/05/soap-envelope";
		
	
	 @SuppressWarnings("resource")
	@Override
	 public void run() {
	        System.out.println("NavisionPullLocationMasterTimer task started at:"+new Date());
	        Connection conn = null;
	        DBConnector dbc = null;
	        //ResultSet rs = null;
	        URLConnection urlConn = null;
	        URL url = null;
	        dbc = new DBConnector();
        	conn = dbc.getConnection();
    		String schema = dbc.getSchema();
			Map resultMap = null;
			String finalStr = null;
		try {
			//SOAPEnvelope soapEnvelop = null;
			String inputEnvelop = getInputMessage();
			System.out.println("The input Location Master SOAP envelop is:.............. "+inputEnvelop);
			WSInvocationNTService wsInvoke = new WSInvocationNTService();
			Map<String, String> parameterMap = new HashMap<>();
			parameterMap.put("userName","FSAS");
		    parameterMap.put("password","FS*1650$As");
		    parameterMap.put("domain","MAHA");
		    parameterMap.put("workStation","srv22.almaha.com.om");
		    parameterMap.put("soapAction","urn:microsoft-dynamics-schemas/page/fsas_location_master:ReadMultiple");
			resultMap = wsInvoke.invokeWebService(SOAP_URL, inputEnvelop, parameterMap);
			System.out.println("The output Location Master SOAP envelop is ........: "+resultMap);
			finalStr = (String) resultMap.get("responseStr");
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Exception occurred at calling location master webservice ............."+e.getMessage());
		}
		try {
			//finalStr = getOutputMessage();
			if(finalStr != null) {
				Node rootNode = getBodyObject(finalStr);
				String rootName = rootNode.getNodeName();
				System.out.println("The root name is ............ "+rootName);
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
								if("FSAS_Location_Master".equals(chldNdName2)) {
									NodeList chldnodeList3 = chldNode2.getChildNodes();
									String accCode = null;
									String bankAccName = null;
									String bankAccNo = null;
									String siteName = null;
									String siteCode = null;
									PreparedStatement statement = null;
									for(int chlnodeCt3 = 0; chlnodeCt3 < chldnodeList3.getLength(); chlnodeCt3++) {
										Node grChldNode = chldnodeList3.item(chlnodeCt3);
										if("Code".equals(grChldNode.getNodeName())) {
											if(grChldNode.getFirstChild() != null) {
												siteCode = grChldNode.getFirstChild().getNodeValue();
											}
										} else if("Name".equals(grChldNode.getNodeName())) {
											if(grChldNode.getFirstChild() != null) {
												siteName = grChldNode.getFirstChild().getNodeValue();
											}
										} else if("Bank_Account_Code".equals(grChldNode.getNodeName())) {
											if(grChldNode.getFirstChild() != null) {
													accCode = grChldNode.getFirstChild().getNodeValue();
											}
										} else if("Bank_Account_Name".equals(grChldNode.getNodeName())) {
											if(grChldNode.getFirstChild() != null) {
												bankAccName = grChldNode.getFirstChild().getNodeValue();
											}
										} else if("Virtual_Account_No".equals(grChldNode.getNodeName())) {
											if(grChldNode.getFirstChild() != null) {
												bankAccNo = grChldNode.getFirstChild().getNodeValue();
											}
										}
									}
									if(bankAccNo != null && bankAccName != null) {
										String sqlQuery = "insert into "+schema+".bank_account_mst (site_id,account_no,bank_name,account_type,account_name,account_code,created_by,created_date) "
												+ " values (?,?,?,?,?,?,'NAV-PULL',now()) on conflict (account_no,bank_name) DO NOTHING";
										statement = conn.prepareStatement(sqlQuery);
										statement.setString(1,siteCode);
										statement.setString(2,bankAccNo);
										statement.setString(3,bankAccName);
										statement.setString(4,"Bank Account");
										statement.setString(5,bankAccName);
										statement.setString(6,accCode);
										int upCount = statement.executeUpdate();
										System.out.println("Navision bank account update count for account: "+bankAccNo+" ..............."+upCount);
										statement.close();
									}
									if(siteCode != null) {
										String sqlQuery = "insert into "+schema+".\"MS_SITE\" (\"SITE_ID\",\"SITE_NAME\",\"SITE_TYPE\",\"CLIENT_NAME\",\"DEALER_NAME\",\"CREATED_BY\",\"CREATION_TIME\") "
												+ " values (?,?,?,?,?,'NAV-PULL',now()) on conflict (\"SITE_ID\") DO NOTHING";
										statement = conn.prepareStatement(sqlQuery);
										statement.setString(1,siteCode);
										statement.setString(2,siteName);
										statement.setString(3,"COCO");
										statement.setString(4,"ALMAHA");
										statement.setString(5,"AL MAHA");
										int upCount = statement.executeUpdate();
										System.out.println("Navision Site update count for site: "+siteCode+" ..............."+upCount);
										statement.close();
									}
								}
							}
						}
					}
				}
			} else {
				System.out.println("Navision API did not recieve any location details on today ....");
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Exception occurred while updating Error details ............."+e.getMessage());
		} finally {
			try {
				dbc.closeConnection(conn);
			} catch(Exception ex) {
				
			}
		}
	 }
	  
	 public static void main(String abc[]) {
		 String url = "http://srv22.almaha.com.om:1396/ALM_2021_NOV_DEV/WS/Al%20Maha%20Petroleum%20Products%20Co./Page/FSErrorLOG";
		String payLoad = "<fser:Read xmlns:fser=\"urn:microsoft-dynamics-schemas/page/fserrorlog\"><fser:Entry_No></fser:Entry_No></fser:Read>";
		 NavisionPullLocationMstTimer rv = new NavisionPullLocationMstTimer();
		 rv.run();
	 }
	 
	 private String getInputMessage() {
		 
		 String inputMsg = "<Envelope xmlns=\"http://schemas.xmlsoap.org/soap/envelope/\">"
			+ "<Body>"
				+ " <ReadMultiple xmlns=\"urn:microsoft-dynamics-schemas/page/fsas_location_master\">"
				 	+ "<filter>"
				 		+ "<Field></Field>"
				 		+	"<Criteria></Criteria>"
				 	+ "</filter>"
                + "<bookmarkKey></bookmarkKey>"
                + "<setSize></setSize>"
                + "</ReadMultiple>"
              + "</Body>"
              + "</Envelope>";
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
	 
	 private String getOutputMessage() {
		 String out = "<Soap:Envelope xmlns:Soap=\"http://schemas.xmlsoap.org/soap/envelope/\"><Soap:Body><ReadMultiple_Result xmlns=\"urn:microsoft-dynamics-schemas/page/fsas_location_master\"><ReadMultiple_Result><FSAS_Location_Master><Key>32;DgAAAAJ7/0EATABNAEYAUwAwADAAMQ==10;13343107410;</Key><Code>ALMFS001</Code><Name>North Ghobra Filling Station</Name><Bank_Account_Code>BM-028-CALL</Bank_Account_Code><Bank_Account_Name>Bank Muscat - Call Account</Bank_Account_Name><Virtual_Account_No>0423005687760164</Virtual_Account_No></FSAS_Location_Master><FSAS_Location_Master><Key>32;DgAAAAJ7/0EATABNAEYAUwAwADAAMg==10;13343113080;</Key><Code>ALMFS002</Code><Name>Izz Filling Station</Name><Bank_Account_Code>BM-028-CALL</Bank_Account_Code><Bank_Account_Name>Bank Muscat - Call Account</Bank_Account_Name><Virtual_Account_No>0423005687761028</Virtual_Account_No></FSAS_Location_Master><FSAS_Location_Master><Key>32;DgAAAAJ7/0EATABNAEYAUwAwADAAMw==10;13343113150;</Key><Code>ALMFS003</Code><Name>Bander Rawdha</Name><Bank_Account_Code>BM-028-CALL</Bank_Account_Code><Bank_Account_Name>Bank Muscat - Call Account</Bank_Account_Name><Virtual_Account_No>0423005687761039</Virtual_Account_No></FSAS_Location_Master><FSAS_Location_Master><Key>32;DgAAAAJ7/0EATABNAEYAUwAyADIAMw==10;13343112530;</Key><Code>ALMFS223</Code><Name>Modern Al Amerat</Name><Bank_Account_Code>BM-028-CALL</Bank_Account_Code><Bank_Account_Name>Bank Muscat - Call Account</Bank_Account_Name><Virtual_Account_No>0423005687760946</Virtual_Account_No></FSAS_Location_Master><FSAS_Location_Master><Key>32;DgAAAAJ7/0EATABNAEYAUwAyADMAMQ==10;13343122460;</Key><Code>ALMFS231</Code><Name>Hail Farq 2 Nizwa(Al Hadhrami)</Name><Bank_Account_Code>BM-028-CALL</Bank_Account_Code><Bank_Account_Name>Bank Muscat - Call Account</Bank_Account_Name><Virtual_Account_No>0423005687762361</Virtual_Account_No></FSAS_Location_Master></ReadMultiple_Result></ReadMultiple_Result></Soap:Body></Soap:Envelope>";
		 return out;
	 }
}	 
	 /*
	  * 
	 CREATE SEQUENCE IF NOT EXISTS "ALMAHA".bank_account_mst_id_seq
    INCREMENT 1
    START 1
    MINVALUE 1
    MAXVALUE 2147483647
    CACHE 1
	
	
CREATE TABLE "ALMAHA".bank_account_mst
(
    id integer NOT NULL DEFAULT nextval('"ALMAHA".bank_account_mst_id_seq'::regclass),
    site_id character varying(50) NOT NULL,
    account_no character varying(50) NOT NULL,
    bank_name character varying(50),
    branch character varying(50),
    account_type character varying(50),
	account_name character varying(50),
    account_code character varying(50),
    account_desc character varying(500),
    created_by character varying(20),
    created_date timestamp without time zone,
    modified_by character varying(20),
    modified_date timestamp without time zone,
    CONSTRAINT bank_account_mst_pkey PRIMARY KEY (id),
    CONSTRAINT bank_account_mst_account_no_bank_name_key UNIQUE (account_no, bank_name)
)

 */
