package com.bct.HOS.App.utils;

import java.io.IOException;
import java.io.StringReader;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
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

public class NavisionPullPumpRateMstTimer extends TimerTask {
	
	
	private String urlString = null;
	private String messageStr = null;
	
	private final String SOAP_URL = "http://SRV22.almaha.com.om:1396/ALM_2021_NOV_DEV/WS/Al%20Maha%20Petroleum%20Products%20Co./Page/FSAS_Pump_Rates";
	private final String SOAP_ACTION = "http://www.w3.org/2003/05/soap-envelope";
	private final String soap11NS = "http://schemas.xmlsoap.org/soap/envelope/";
	private final String soap12NS = "http://www.w3.org/2003/05/soap-envelope";
		
	
	 @SuppressWarnings("resource")
	@Override
	 public void run() {
	        System.out.println("NavisionPullPumpRateMstTimer task started at:"+new Date());
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
			System.out.println("The input SOAP envelop for pump rate is:.............. "+inputEnvelop);
			WSInvocationNTService wsInvoke = new WSInvocationNTService();
			Map<String, String> parameterMap = new HashMap<>();
			parameterMap.put("userName","FSAS");
		    parameterMap.put("password","FS*1650$As");
		    parameterMap.put("domain","MAHA");
		    parameterMap.put("workStation","srv22.almaha.com.om");
		    parameterMap.put("soapAction","urn:microsoft-dynamics-schemas/page/fsas_pump_rates:ReadMultiple");
			resultMap = wsInvoke.invokeWebService(SOAP_URL, inputEnvelop, parameterMap);
			System.out.println("The output SOAP envelop is for pump rate ........: "+resultMap);
			finalStr = (String) resultMap.get("responseStr");
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Exception occurred at invoking pump rate webservice ............."+e.getMessage());
		}
		//finalStr = "";
		try {
			//finalStr = getOutputMessage();
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			String	sqlQuery = "update "+schema+".ms_fuel_products set pump_rate=?,rate_withtax=? "
					+ " where number=? ";
			if(finalStr != null) {
				Node rootNode = getBodyObject(finalStr);
				String rootName = rootNode.getNodeName();
				if("ReadMultiple_Result".equals(rootName)) {
					NodeList nodeList1 = rootNode.getChildNodes();
					for(int nodeCt1 = 0; nodeCt1 < nodeList1.getLength(); nodeCt1++) {
						Node chldNode1 = nodeList1.item(nodeCt1);
						String chldNdName1 = chldNode1.getNodeName();
						if("ReadMultiple_Result".equals(chldNdName1)) {
							NodeList nodeList2 = chldNode1.getChildNodes();
							for(int nodeCt2 = 0; nodeCt2 < nodeList2.getLength(); nodeCt2++) {
								Node chldNode2 = nodeList2.item(nodeCt2);
								String chldNdName2 = chldNode2.getNodeName();
								if("FSAS_Pump_Rates".equals(chldNdName2)) {
									NodeList chldnodeList3 = chldNode2.getChildNodes();
									String productNo = null;
									String startDate = null;
									String endDate = null;
									String rate = null;
									String rateTax = null;
									for(int chlnodeCt3 = 0; chlnodeCt3 < chldnodeList3.getLength(); chlnodeCt3++) {
										Node grChldNode = chldnodeList3.item(chlnodeCt3);
										if("Item_No".equals(grChldNode.getNodeName())) {
											if(grChldNode.getFirstChild() != null) {
													productNo = grChldNode.getFirstChild().getNodeValue();
											}
										} else if("Starting_Date".equals(grChldNode.getNodeName())) {
											if(grChldNode.getFirstChild() != null) {
												startDate = grChldNode.getFirstChild().getNodeValue();
											}
										} else if("FSAS_End_Date".equals(grChldNode.getNodeName())) {
											if(grChldNode.getFirstChild() != null) {
												endDate = grChldNode.getFirstChild().getNodeValue();
											}
										} else if("Pump_Rate".equals(grChldNode.getNodeName())) {
											if(grChldNode.getFirstChild() != null) {
												rate = grChldNode.getFirstChild().getNodeValue();
											}
										} else if("Pump_Rate_Inc_Tax".equals(grChldNode.getNodeName())) {
											if(grChldNode.getFirstChild() != null) {
												rateTax = grChldNode.getFirstChild().getNodeValue();
											}
										}
									}
									Date dateStart = null;
									Date dateEnd = null;
									try {
										dateStart = formatter.parse(startDate);
										dateEnd = formatter.parse(endDate);
									} catch(Exception ex) {
										System.out.println("Exception occurred while parsing start date / end date"+ex.getMessage());
										continue;
									}
									dateEnd.setHours(23);
									dateEnd.setMinutes(59);
									dateEnd.setSeconds(59);
									Date currentDt = new Date();
									if(currentDt.after(dateStart) && currentDt.before(dateEnd)) {
										PreparedStatement statement = conn.prepareStatement(sqlQuery);
										statement.setDouble(1,Double.valueOf(rate));
										statement.setDouble(2,Double.valueOf(rateTax));
										statement.setString(3,productNo);
										int upCount = statement.executeUpdate();
										System.out.println("TEST LOG | Navision Pump Rate update count ..............."+upCount);
										statement.close();
									}
								}
							}
							}
						}
					}
			} else {
				System.out.println("Navision API - Pump Rate returned null response..........");
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
		 NavisionPullPumpRateMstTimer rv = new NavisionPullPumpRateMstTimer();
		 rv.run();
	 }
	 
	 private String getInputMessage() {
		 
		 String inputMsg = "<Envelope xmlns=\"http://schemas.xmlsoap.org/soap/envelope/\">"
				 	+ "<Body>"
				 	+ "<ReadMultiple xmlns=\"urn:microsoft-dynamics-schemas/page/fsas_pump_rates\">"
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
	
	/* private String getOutputMessage() {
		 String out = "<Soap:Envelope xmlns:Soap=\"http://schemas.xmlsoap.org/soap/envelope/\"><Soap:Body><ReadMultiple_Result xmlns=\"urn:microsoft-dynamics-schemas/page/fsas_pump_rates\"><ReadMultiple_Result><FSAS_Pump_Rates><Key>64;+8MAAAJ7/1AAUgAwADAAMAAxAAAAAnv/VwBIADAAMAA2AAAAAC7lhRYAAC4VhxY=10;13300201890;</Key><Item_No>PR0001</Item_No><Starting_Date>2020-09-01</Starting_Date><FSAS_End_Date>2020-09-30</FSAS_End_Date><Pump_Rate>0.194</Pump_Rate><Pump_Rate_Inc_Tax>0</Pump_Rate_Inc_Tax></FSAS_Pump_Rates><FSAS_Pump_Rates><Key>64;+8MAAAJ7/1AAUgAwADAAMAAxAAAAAnv/VwBIADAAMAA2AAAAAC4XhxYAAC5NhxY=10;13300201900;</Key><Item_No>PR0001</Item_No><Starting_Date>2021-02-01</Starting_Date><FSAS_End_Date>2021-02-28</FSAS_End_Date><Pump_Rate>0.202</Pump_Rate><Pump_Rate_Inc_Tax>0</Pump_Rate_Inc_Tax></FSAS_Pump_Rates><FSAS_Pump_Rates><Key>64;+8MAAAJ7/1AAUgAwADAAMAAxAAAAAnv/VwBIADAAMAA2AAAAAC5PhxYAAC6LhxY=10;13300201910;</Key><Item_No>PR0001</Item_No><Starting_Date>2022-04-05</Starting_Date><FSAS_End_Date>2022-04-05</FSAS_End_Date><Pump_Rate>0.214</Pump_Rate><Pump_Rate_Inc_Tax>0</Pump_Rate_Inc_Tax></FSAS_Pump_Rates><FSAS_Pump_Rates><Key>64;+8MAAAJ7/1AAUgAwADAAMAAyAAAAAnv/VwBIADAAMAA2AAAAAC7lhRYAAC6ZhhY=10;13300202250;</Key><Item_No>PR0002</Item_No><Starting_Date>2020-09-01</Starting_Date><FSAS_End_Date>2020-09-30</FSAS_End_Date><Pump_Rate>0.183</Pump_Rate><Pump_Rate_Inc_Tax>0</Pump_Rate_Inc_Tax></FSAS_Pump_Rates><FSAS_Pump_Rates><Key>64;+8MAAAJ7/1AAUgAwADAAMAAyAAAAAnv/VwBIADAAMAA2AAAAAC6bhhYAAC4VhxY=10;13300202260;</Key><Item_No>PR0002</Item_No><Starting_Date>2020-12-01</Starting_Date><FSAS_End_Date>2020-12-31</FSAS_End_Date><Pump_Rate>0.18</Pump_Rate><Pump_Rate_Inc_Tax>0</Pump_Rate_Inc_Tax></FSAS_Pump_Rates><FSAS_Pump_Rates><Key>64;+8MAAAJ7/1AAUgAwADAAMAAyAAAAAnv/VwBIADAAMAA2AAAAAC4XhxYAAC5NhxY=10;13300202270;</Key><Item_No>PR0002</Item_No><Starting_Date>2022-02-01</Starting_Date><FSAS_End_Date>2022-04-05</FSAS_End_Date><Pump_Rate>0.188</Pump_Rate><Pump_Rate_Inc_Tax>0</Pump_Rate_Inc_Tax></FSAS_Pump_Rates><FSAS_Pump_Rates><Key>64;+8MAAAJ7/1AAUgAwADAAMAAzAAAAAnv/VwBIADAAMAA2AAAAAC7lhRYAAC4fhhY=10;13300202570;</Key><Item_No>PR0003</Item_No><Starting_Date>2020-09-01</Starting_Date><FSAS_End_Date>2020-09-30</FSAS_End_Date><Pump_Rate>0.214</Pump_Rate><Pump_Rate_Inc_Tax>0</Pump_Rate_Inc_Tax></FSAS_Pump_Rates></ReadMultiple_Result></ReadMultiple_Result></Soap:Body></Soap:Envelope>";
		 return out;
	 }*/
}
