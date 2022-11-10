package com.bct.HOS.BOS.BO;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "GeneralConfiguration")
public class GeneralConfiguration {

	ObjSiteConfig ObjSiteConfigObject;
	lstProdctConfig lstProdctConfig;
	LstGradeConfig LstGradeConfigObject;
	LstTankConfig LstTankConfigObject;
	LstTankChartConfig LstTankChartConfigObject;
	LstDUConfig LstDUConfigObject;
	LstPumpConfig LstPumpConfigObject;
	LstNozzleConfig LstNozzleConfigObject;
	private String lstPrinterConfig;
	private String lstUPSConfig;
	private String lstSupplyConfig;
	LstFTPEmailConfig LstFTPEmailConfigObject;
	LstPriceMstrConfig LstPriceMstrConfigObject;
	LstVersionMstrConfig LstVersionMstrConfigObject;
	private String lstRemoteConfig;
	private String lstRemoteUpdateDevice;
	private String lstCOPTConfig;

	private String xsi = null;
	private String xsd = null;

	// Getter Methods
	@XmlAttribute(name = "xmlns:xsi")
	public String getXsi() {
		return xsi;
	}

	public void setXsi(String xsi) {
		this.xsi = xsi;
	}

	@XmlAttribute(name = "xmlns:xsd")
	public String getXsd() {
		return xsd;
	}

	public void setXsd(String xsd) {
		this.xsd = xsd;
	}

	public ObjSiteConfig getObjSiteConfig() {
		return ObjSiteConfigObject;
	}

	public lstProdctConfig getLstProdctConfig() {
		return lstProdctConfig;
	}

	public LstGradeConfig getLstGradeConfig() {
		return LstGradeConfigObject;
	}

	public LstTankConfig getLstTankConfig() {
		return LstTankConfigObject;
	}

	public LstTankChartConfig getLstTankChartConfig() {
		return LstTankChartConfigObject;
	}

	public LstDUConfig getLstDUConfig() {
		return LstDUConfigObject;
	}

	public LstPumpConfig getLstPumpConfig() {
		return LstPumpConfigObject;
	}

	public LstNozzleConfig getLstNozzleConfig() {
		return LstNozzleConfigObject;
	}

	public String getLstPrinterConfig() {
		return lstPrinterConfig;
	}

	public String getLstUPSConfig() {
		return lstUPSConfig;
	}

	public String getLstSupplyConfig() {
		return lstSupplyConfig;
	}

	public LstFTPEmailConfig getLstFTPEmailConfig() {
		return LstFTPEmailConfigObject;
	}

	public LstPriceMstrConfig getLstPriceMstrConfig() {
		return LstPriceMstrConfigObject;
	}

	public LstVersionMstrConfig getLstVersionMstrConfig() {
		return LstVersionMstrConfigObject;
	}

	public String getLstRemoteConfig() {
		return lstRemoteConfig;
	}

	public String getLstRemoteUpdateDevice() {
		return lstRemoteUpdateDevice;
	}

	public String getLstCOPTConfig() {
		return lstCOPTConfig;
	}

	// Setter Methods

	public void setObjSiteConfig(ObjSiteConfig objSiteConfigObject) {
		this.ObjSiteConfigObject = objSiteConfigObject;
	}

	public void setLstProdctConfig(lstProdctConfig lstProdctConfig) {
		this.lstProdctConfig = lstProdctConfig;
	}

	public void setLstGradeConfig(LstGradeConfig lstGradeConfigObject) {
		this.LstGradeConfigObject = lstGradeConfigObject;
	}

	public void setLstTankConfig(LstTankConfig lstTankConfigObject) {
		this.LstTankConfigObject = lstTankConfigObject;
	}

	public void setLstTankChartConfig(LstTankChartConfig lstTankChartConfigObject) {
		this.LstTankChartConfigObject = lstTankChartConfigObject;
	}

	public void setLstDUConfig(LstDUConfig lstDUConfigObject) {
		this.LstDUConfigObject = lstDUConfigObject;
	}

	public void setLstPumpConfig(LstPumpConfig lstPumpConfigObject) {
		this.LstPumpConfigObject = lstPumpConfigObject;
	}

	public void setLstNozzleConfig(LstNozzleConfig lstNozzleConfigObject) {
		this.LstNozzleConfigObject = lstNozzleConfigObject;
	}

	public void setLstPrinterConfig(String lstPrinterConfig) {
		this.lstPrinterConfig = lstPrinterConfig;
	}

	@XmlElement(name="lstUPSConfig")
	public void setLstUPSConfig(String lstUPSConfig) {
		this.lstUPSConfig = lstUPSConfig;
	}

	public void setLstSupplyConfig(String lstSupplyConfig) {
		this.lstSupplyConfig = lstSupplyConfig;
	}

	public void setLstFTPEmailConfig(LstFTPEmailConfig lstFTPEmailConfigObject) {
		this.LstFTPEmailConfigObject = lstFTPEmailConfigObject;
	}

	public void setLstPriceMstrConfig(LstPriceMstrConfig lstPriceMstrConfigObject) {
		this.LstPriceMstrConfigObject = lstPriceMstrConfigObject;
	}

	public void setLstVersionMstrConfig(LstVersionMstrConfig lstVersionMstrConfigObject) {
		this.LstVersionMstrConfigObject = lstVersionMstrConfigObject;
	}

	public void setLstRemoteConfig(String lstRemoteConfig) {
		this.lstRemoteConfig = lstRemoteConfig;
	}

	public void setLstRemoteUpdateDevice(String lstRemoteUpdateDevice) {
		this.lstRemoteUpdateDevice = lstRemoteUpdateDevice;
	}

	public void setLstCOPTConfig(String lstCOPTConfig) {
		this.lstCOPTConfig = lstCOPTConfig;
	}

}
