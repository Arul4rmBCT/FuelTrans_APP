package com.bct.HOS.BOS;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import com.bct.HOS.App.utils.HOSConfig;
import com.bct.HOS.BOS.BO.GeneralConfiguration;
import com.bct.HOS.BOS.BO.GradeMasterConfig;
import com.bct.HOS.BOS.BO.LstGradeConfig;
import com.bct.HOS.BOS.BO.lstProdctConfig;
import com.google.gson.Gson;
import com.bct.HOS.BOS.BO.LstTankConfig;
import com.bct.HOS.BOS.BO.ObjSiteConfig;
import com.bct.HOS.BOS.BO.ProductMasterConfig;
import com.bct.HOS.BOS.BO.TankListConfig;

public class BOSXMLGenerator {

	public BOSXMLGenerator() {
		// TODO Auto-generated constructor stub
	}

	public boolean generateXML(String roCode, GeneralConfiguration pojo) {
		try {
			HOSConfig config = new HOSConfig();
			String filePAth = config.getValue("BOS_XML_Path")+roCode + ".xml";
			pojo.setXsi("http://www.w3.org/2001/XMLSchema-instance");
			pojo.setXsd("http://www.w3.org/2001/XMLSchema");
			
			JAXBContext bosContext = JAXBContext.newInstance(GeneralConfiguration.class);
			Marshaller bosMarshaller = bosContext.createMarshaller();
			bosMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

			bosMarshaller.marshal(pojo, new FileOutputStream(filePAth));
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public static void main(String args[]) {
		try {
			
			BOSXMLGenerator xmlObj = new BOSXMLGenerator();
			//GeneralConfiguration obj = new GeneralConfiguration();
			
			String json = " { " + 
					"	\"ObjSiteConfigObject\": { " + 
					"		\"Address1\": \"1\", " + 
					"		\"Address2\": \"1\", " + 
					"		\"Address3\": \"1\", " + 
					"		\"City\": \"1\", " + 
					"		\"Contact\": \"1\", " + 
					"		\"Country\": \"1\", " + 
					"		\"DO_Code\": \"1\", " + 
					"		\"DO_Name\": \"1\", " + 
					"		\"Dealer_Name\": \"1\", " + 
					"		\"EOD_Time\": \"1\", " + 
					"		\"EOD_Type\": \"1\", " + 
					"		\"Email\": \"1\", " + 
					"		\"Mobile_No\": \"1\", " + 
					"		\"Pin_Code\": \"1\", " + 
					"		\"Region\": \"1\", " + 
					"		\"SAP_Code\": \"1\", " + 
					"		\"Site_Group\": \"1\", " + 
					"		\"Site_ID\": \"1\", " + 
					"		\"Site_Name\": \"1\", " + 
					"		\"Site_Type\": \"1\", " + 
					"		\"State\": \"1\", " + 
					"		\"Zone_Code\": \"1\" " + 
					"	}, " + 
					"	\"LstGradeConfigObject\": { " + 
					"		\"GradeMasterConfigObject\": [ " + 
					"			{ " + 
					"				\"Grade_Color\": \"1\", " + 
					"				\"Grade_Name\": \"1\", " + 
					"				\"Grade_No\": \"1\", " + 
					"				\"Grade_Price\": \"1\", " + 
					"				\"Grade_Unit\": \"1\", " + 
					"				\"High_Percentage\": \"1\", " + 
					"				\"High_Product\": \"1\", " + 
					"				\"Low_Percentage\": \"1\", " + 
					"				\"Low_Product\": \"1\", " + 
					"				\"SAP_Code\": \"1\" " + 
					"			} " + 
					"		] " + 
					"	}, " + 
					"	\"lstProdctConfig\": { " + 
					"		\"ProductMasterConfig\": [ " + 
					"			{ " + 
					"				\"Product_Color\": \"1\", " + 
					"				\"Product_Name\": \"1\", " + 
					"				\"Product_No\": \"1\", " + 
					"				\"Product_Reorder\": \"1\", " + 
					"				\"Product_Type\": \"1\", " + 
					"				\"Product_Unit\": \"1\", " + 
					"				\"SAP_Code\": \"1\" " + 
					"			} " + 
					"		] " + 
					"	}, " + 
					"	\"LstTankConfigObject\": { " + 
					"		\"TankListConfigObject\": [ " + 
					"			{ " + 
					"				\"Capacity\": \"1\", " + 
					"				\"Delivery_Delay\": \"1\", " + 
					"				\"Height\": \"1\", " + 
					"				\"High_Water\": \"1\", " + 
					"				\"IsWater_Float\": \"1\", " + 
					"				\"Max_Capacity\": \"1\", " + 
					"				\"Mini_Capacity\": \"1\", " + 
					"				\"Overflow_Capacity\": \"1\", " + 
					"				\"Probe_Address\": \"1\", " + 
					"				\"Probe_Type\": \"1\", " + 
					"				\"Product_No\": \"1\", " + 
					"				\"Tank_No\": \"1\", " + 
					"				\"Temp_Factor\": \"1\", " + 
					"				\"Temp_High\": \"1\", " + 
					"				\"Temp_Low\": \"1\", " + 
					"				\"Water\": \"1\" " + 
					"			} " + 
					"		] " + 
					"	} " + 
					"}";
			
			Gson gson = new Gson();
			//System.out.println(json);
			GeneralConfiguration obj = gson.fromJson(json, GeneralConfiguration.class);
			//System.out.println(obj.getObjSiteConfig().getSite_ID());
			//xmlObj.dataLoader(obj);
			xmlObj.generateXML(obj.getObjSiteConfig().getSite_ID(), obj);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void dataLoader(GeneralConfiguration obj) {
		ObjSiteConfig objSite = new ObjSiteConfig();

		objSite.setSite_ID("BAHWAN");
		objSite.setCountry("OMAN");
		objSite.setRegion("MUSCAT");
		objSite.setState("NORTH GHOUBRA");
		objSite.setZone_Code("");
		objSite.setSite_Name("HORMUZ NORTH GHOUBRA");
		objSite.setSite_Group("NAYARA");
		objSite.setSite_Type("");
		objSite.setDealer_Name("Mr. HATIM");
		objSite.setAddress1("GHOUBRA NORTH");
		objSite.setAddress2("Address 2");
		objSite.setAddress3("Address 3");
		objSite.setCity("MUSCAT");
		objSite.setPin_Code("390024");
		objSite.setContact("92752307");
		objSite.setMobile_No("92752307");
		objSite.setEmail("mohammed.zahid@bahwancybertek.com");
		objSite.setSAP_Code("10101013");
		objSite.setEOD_Time("23:59");
		objSite.setEOD_Type("Auto");
		objSite.setDO_Code("01234567");
		objSite.setDO_Name("NAYARA");

		obj.setObjSiteConfig(objSite);

		List<ProductMasterConfig> ProductMaster = new ArrayList<ProductMasterConfig>();
		lstProdctConfig lstProdctConfig = new lstProdctConfig();
		ProductMasterConfig ProductMasterConfig = new ProductMasterConfig();
		ProductMasterConfig.setProduct_No("1");
		ProductMasterConfig.setSAP_Code("1");
		ProductMasterConfig.setProduct_Name("Premium");
		ProductMasterConfig.setProduct_Type("Fuel");
		ProductMasterConfig.setProduct_Unit("Ltr.");
		ProductMasterConfig.setProduct_Reorder("1000");
		ProductMasterConfig.setProduct_Color("#FFFFFF");
		ProductMaster.add(ProductMasterConfig);
		
		ProductMasterConfig ProductMasterConfig1 = new ProductMasterConfig();
		ProductMasterConfig1.setProduct_No("2");
		ProductMasterConfig1.setSAP_Code("2");
		ProductMasterConfig1.setProduct_Name("Premium");
		ProductMasterConfig1.setProduct_Type("Fuel");
		ProductMasterConfig1.setProduct_Unit("Ltr.");
		ProductMasterConfig1.setProduct_Reorder("1000");
		ProductMasterConfig1.setProduct_Color("#FFFFFF");
		ProductMaster.add(ProductMasterConfig1);
		
		lstProdctConfig.setProductMasterConfig(ProductMaster);
		obj.setLstProdctConfig(lstProdctConfig);

		List<GradeMasterConfig> GradeMasterConfig = new ArrayList<GradeMasterConfig>();
		LstGradeConfig lstGradeConfig = new LstGradeConfig();
		GradeMasterConfig gradeMasterConfig = new GradeMasterConfig();
		gradeMasterConfig.setGrade_No("1");
		gradeMasterConfig.setSAP_Code("1");
		gradeMasterConfig.setGrade_Name("Premium");
		gradeMasterConfig.setHigh_Product("1");
		gradeMasterConfig.setHigh_Percentage("100");
		gradeMasterConfig.setGrade_Price("80");
		gradeMasterConfig.setGrade_Unit("Ltr.");
		gradeMasterConfig.setGrade_Color("#FFFFFF");
		gradeMasterConfig.setLow_Percentage("0");
		gradeMasterConfig.setLow_Product("0");
		GradeMasterConfig.add(gradeMasterConfig);
		
		GradeMasterConfig gradeMasterConfig1 = new GradeMasterConfig();
		gradeMasterConfig1.setGrade_No("2");
		gradeMasterConfig1.setSAP_Code("2");
		gradeMasterConfig1.setGrade_Name("Premium");
		gradeMasterConfig1.setHigh_Product("1");
		gradeMasterConfig1.setHigh_Percentage("100");
		gradeMasterConfig1.setGrade_Price("80");
		gradeMasterConfig1.setGrade_Unit("Ltr.");
		gradeMasterConfig1.setGrade_Color("#FFFFFF");
		gradeMasterConfig1.setLow_Percentage("0");
		gradeMasterConfig1.setLow_Product("0");
		GradeMasterConfig.add(gradeMasterConfig1);
		lstGradeConfig.setGradeMasterConfig(GradeMasterConfig);

		obj.setLstGradeConfig(lstGradeConfig);
		
		List<TankListConfig> TankListConfigObject=new ArrayList<TankListConfig>();
		LstTankConfig lstTankConfig = new LstTankConfig();
		TankListConfig tankListConfig = new TankListConfig();
		tankListConfig.setTank_No("1");
		tankListConfig.setProduct_No("1");
		tankListConfig.setHeight("1000");
		tankListConfig.setCapacity("24000");
		tankListConfig.setWater("100");
		tankListConfig.setTemp_Factor("-17");
		tankListConfig.setDelivery_Delay("5");
		tankListConfig.setProbe_Type("18");
		tankListConfig.setProbe_Address("4");
		tankListConfig.setProbe_Type("25");
		tankListConfig.setIsWater_Float("1");
		tankListConfig.setOverflow_Capacity("12000");
		tankListConfig.setMax_Capacity("23500");
		tankListConfig.setMini_Capacity("1000");
		tankListConfig.setHigh_Water("500");
		tankListConfig.setTemp_High("2");
		tankListConfig.setTemp_Low("3");
		TankListConfigObject.add(tankListConfig);


		TankListConfig tankListConfig1 = new TankListConfig();
		tankListConfig1.setTank_No("2");
		tankListConfig1.setProduct_No("1");
		tankListConfig1.setHeight("1000");
		tankListConfig1.setCapacity("24000");
		tankListConfig1.setWater("100");
		tankListConfig1.setTemp_Factor("-17");
		tankListConfig1.setDelivery_Delay("5");
		tankListConfig1.setProbe_Type("18");
		tankListConfig1.setProbe_Address("4");
		tankListConfig1.setProbe_Type("25");
		tankListConfig1.setIsWater_Float("1");
		tankListConfig1.setOverflow_Capacity("12000");
		tankListConfig1.setMax_Capacity("23500");
		tankListConfig1.setMini_Capacity("1000");
		tankListConfig1.setHigh_Water("500");
		tankListConfig1.setTemp_High("2");
		tankListConfig1.setTemp_Low("3");
		TankListConfigObject.add(tankListConfig1);
		lstTankConfig.setTankListConfig(TankListConfigObject);
		
		obj.setLstTankConfig(lstTankConfig);
		

		obj.setLstPumpConfig(null);
		obj.setLstUPSConfig(null);

	}

}
