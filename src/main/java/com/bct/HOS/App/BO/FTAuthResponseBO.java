package com.bct.HOS.App.BO;

import java.util.ArrayList;

public class FTAuthResponseBO {

	String strFailureMsg = null;
	String strSuccessMsg = null;
	String SRESTIME = null;
	ArrayList<hdrcache> hdrcache = null;
	ArrayList<consoleMaster_array> consoleMaster_array;
	ArrayList<consoleReports_array> consoleReports_array;
	ArrayList<consoleTransaction_array> consoleTransaction_array;
	ArrayList<siteDetails_array> siteDetails_array;
	
	

	public ArrayList<siteDetails_array> getSiteDetails_array() {
		return siteDetails_array;
	}

	public void setSiteDetails_array(ArrayList<siteDetails_array> siteDetails_array) {
		this.siteDetails_array = siteDetails_array;
	}

	public String getStrSuccessMsg() {
		return strSuccessMsg;
	}

	public void setStrSuccessMsg(String strSuccessMsg) {
		this.strSuccessMsg = strSuccessMsg;
	}

	public ArrayList<consoleTransaction_array> getConsoleTransaction_array() {
		return consoleTransaction_array;
	}

	public void setConsoleTransaction_array(ArrayList<consoleTransaction_array> consoleTransaction_array) {
		this.consoleTransaction_array = consoleTransaction_array;
	}

	public ArrayList<consoleMaster_array> getConsoleMaster_array() {
		return consoleMaster_array;
	}

	public void setConsoleMaster_array(ArrayList<consoleMaster_array> consoleMaster_array) {
		this.consoleMaster_array = consoleMaster_array;
	}

	public ArrayList<consoleReports_array> getConsoleReports_array() {
		return consoleReports_array;
	}

	public void setConsoleReports_array(ArrayList<consoleReports_array> consoleReports_array) {
		this.consoleReports_array = consoleReports_array;
	}

	public String getStrFailureMsg() {
		return strFailureMsg;
	}

	public void setStrFailureMsg(String strFailureMsg) {
		this.strFailureMsg = strFailureMsg;
	}

	public String getSRESTIME() {
		return SRESTIME;
	}

	public void setSRESTIME(String sRESTIME) {
		SRESTIME = sRESTIME;
	}

	public ArrayList<hdrcache> getHdrcache() {
		return hdrcache;
	}

	public void setHdrcache(ArrayList<hdrcache> hdrcache) {
		this.hdrcache = hdrcache;
	}

}
