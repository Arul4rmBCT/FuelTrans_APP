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

public class NavisionPullProdMasterTimer extends TimerTask {
	
	
	private String urlString = null;
	private String messageStr = null;
	
	private final String SOAP_URL = "http://SRV22.almaha.com.om:1396/ALM_2021_NOV_DEV/WS/Al%20Maha%20Petroleum%20Products%20Co./Page/FSAS_Product_Master";
	private final String SOAP_ACTION = "http://www.w3.org/2003/05/soap-envelope";
	private final String soap11NS = "http://schemas.xmlsoap.org/soap/envelope/";
	private final String soap12NS = "http://www.w3.org/2003/05/soap-envelope";
		
	
	 @SuppressWarnings("resource")
	@Override
	 public void run() {
	        System.out.println("NavisionPullProdMasterTimer task started at:"+new Date());
	        Connection conn = null;
	        Statement st = null;
	        DBConnector dbc = null;
	        ResultSet rs = null;
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
			System.out.println("The input SOAP envelop for product master is:.............. "+inputEnvelop);
			WSInvocationNTService wsInvoke = new WSInvocationNTService();
			Map<String, String> parameterMap = new HashMap<>();
			parameterMap.put("userName","FSAS");
		    parameterMap.put("password","FS*1650$As");
		    parameterMap.put("domain","MAHA");
		    parameterMap.put("workStation","srv22.almaha.com.om");
		    parameterMap.put("soapAction","urn:microsoft-dynamics-schemas/page/fsas_product_master:ReadMultiple");
			resultMap = wsInvoke.invokeWebService(SOAP_URL, inputEnvelop, parameterMap);
			System.out.println("The output SOAP envelop is ........: "+resultMap);
			finalStr = (String) resultMap.get("responseStr");
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Exception occurred at invoking product master webservice ............."+e.getMessage());
		}
		//finalStr = "";
		try {
			//finalStr = getOutputMessage();
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
								if("FSAS_Product_Master".equals(chldNdName2)) {
									NodeList chldnodeList3 = chldNode2.getChildNodes();
									String no = null;
									String Description = null;
									String uom = null;
									String group = null;
									for(int chlnodeCt3 = 0; chlnodeCt3 < chldnodeList3.getLength(); chlnodeCt3++) {
										Node grChldNode = chldnodeList3.item(chlnodeCt3);
										if("No".equals(grChldNode.getNodeName())) {
												if(grChldNode.getFirstChild() != null) {
														no = grChldNode.getFirstChild().getNodeValue();
												}
												System.out.println("No: "+no);
											} else if("Description".equals(grChldNode.getNodeName())) {
												if(grChldNode.getFirstChild() != null) {
													Description = grChldNode.getFirstChild().getNodeValue();
												}
											} else if("Base_Unit_of_Measure".equals(grChldNode.getNodeName())) {
												if(grChldNode.getFirstChild() != null) {
													uom = grChldNode.getFirstChild().getNodeValue();
												}
											} else if("Inventory_Posting_Group".equals(grChldNode.getNodeName())) {
												if(grChldNode.getFirstChild() != null) {
													group = grChldNode.getFirstChild().getNodeValue();
												}
											}
										}
										String sqlQuery = null;
										if("FUEL".equalsIgnoreCase(group)) {
											sqlQuery = "insert into "+schema+".ms_fuel_products (number,name,unit,type,created_by,created_date) "
												+ " values (?,?,?,?,'NAV-PULL',now()) on conflict (number) DO NOTHING";
										} else {
											sqlQuery = "insert into "+schema+".ms_non_fuel_products (number,name,unit,type,created_by,created_date) "
													+ " values (?,?,?,?,'NAV-PULL',now()) on conflict (number) DO NOTHING";
										}
										PreparedStatement statement = conn.prepareStatement(sqlQuery);
										statement.setString(1,no);
										statement.setString(2,Description);
										statement.setString(3,uom);
										statement.setString(4,group);
										int upCount = statement.executeUpdate();
										System.out.println("TEST LOG | Navision product/non-product update count ..............."+upCount);
										statement.close();
									}
								}
							}
						}
					}
			} else {
				System.out.println("Navision API - Product Master returned null response..........");
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
		 NavisionPullProdMasterTimer rv = new NavisionPullProdMasterTimer();
		 rv.run();
	 }
	 
	 private String getInputMessage() {
		 
		 String inputMsg = "<Envelope xmlns=\"http://schemas.xmlsoap.org/soap/envelope/\">"
				 	+ "<Body>"
				 	+ "<ReadMultiple xmlns=\"urn:microsoft-dynamics-schemas/page/fsas_product_master\">"
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
		 String out = "<Soap:Envelope xmlns:Soap=\"http://schemas.xmlsoap.org/soap/envelope/\"><Soap:Body><ReadMultiple_Result xmlns=\"urn:microsoft-dynamics-schemas/page/fsas_product_master\"><ReadMultiple_Result><FSAS_Product_Master><Key>24;GwAAAAJ7/1AAUgAwADAAMAAx10;13300116860;</Key><No>PR0001</No><Description>MOGAS-95</Description><Base_Unit_of_Measure>LTR</Base_Unit_of_Measure><Inventory_Posting_Group>FUEL</Inventory_Posting_Group></FSAS_Product_Master><FSAS_Product_Master><Key>24;GwAAAAJ7/1AAUgAwADAAMAAy10;13300116880;</Key><No>PR0002</No><Description>MOGAS-91</Description><Base_Unit_of_Measure>LTR</Base_Unit_of_Measure><Inventory_Posting_Group>FUEL</Inventory_Posting_Group></FSAS_Product_Master><FSAS_Product_Master><Key>24;GwAAAAJ7/1AAUgAwADAAMAAz10;13300116940;</Key><No>PR0003</No><Description>DIESEL- GO</Description><Base_Unit_of_Measure>LTR</Base_Unit_of_Measure><Inventory_Posting_Group>FUEL</Inventory_Posting_Group></FSAS_Product_Master><FSAS_Product_Master><Key>24;GwAAAAJ7/1AAUgAwADEAMgAy10;13300203390;</Key><No>PR0122</No><Description>AM Supreme 20w50 SL 6x4L</Description><Base_Unit_of_Measure>PCS</Base_Unit_of_Measure><Inventory_Posting_Group>LUBE</Inventory_Posting_Group></FSAS_Product_Master><FSAS_Product_Master><Key>24;GwAAAAJ7/1AAUgAwADEAMgAz10;13300203410;</Key><No>PR0123</No><Description>AM Supreme 20w50 SL 24x1L</Description><Base_Unit_of_Measure>PCS</Base_Unit_of_Measure><Inventory_Posting_Group>LUBE</Inventory_Posting_Group></FSAS_Product_Master><FSAS_Product_Master><Key>24;GwAAAAJ7/1AAUgAwADEAMgA110;13300203430;</Key><No>PR0125</No><Description>AM SD Engine Oil SAE 30 208L</Description><Base_Unit_of_Measure>DRUM</Base_Unit_of_Measure><Inventory_Posting_Group>LUBE</Inventory_Posting_Group></FSAS_Product_Master></ReadMultiple_Result></ReadMultiple_Result></Soap:Body></Soap:Envelope>";
		 return out;
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
