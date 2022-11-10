package com.bct.HOS.Fleet;

import java.util.ArrayList;

public class BPMSIn_Msg {

	String Pre_Tranx_ID = null;
	String Tranx_ID = null;
	String Tranx_Time = null;
	String Tranx_Qty = null;
	String Tranx_Value = null;
	String Tranx_Price = null;
	String Req_Time = null;
	String Site_Code = null;
	String Prd_Code = null;
	String Fleet_Auth_Type = null;
	String Fleet_Auth_No = null;
	String Drv_Auth_Type = null;
	String Drv_Auth_No = null;
	String Req_Qty = null;
	String Req_Value = null;
	String Market_Price = null;
	String Subsidery_Price = null;
	ArrayList<SitesBO> sites = null;

	public ArrayList<SitesBO> getSites() {
		return sites;
	}

	public void setSites(ArrayList<SitesBO> sites) {
		this.sites = sites;
	}

	public String getPre_Tranx_ID() {
		return Pre_Tranx_ID;
	}

	public void setPre_Tranx_ID(String pre_Tranx_ID) {
		Pre_Tranx_ID = pre_Tranx_ID;
	}

	public String getReq_Time() {
		return Req_Time;
	}

	public void setReq_Time(String req_Time) {
		Req_Time = req_Time;
	}

	public String getSite_Code() {
		return Site_Code;
	}

	public void setSite_Code(String site_Code) {
		Site_Code = site_Code;
	}

	public String getPrd_Code() {
		return Prd_Code;
	}

	public void setPrd_Code(String prd_Code) {
		Prd_Code = prd_Code;
	}

	public String getFleet_Auth_Type() {
		return Fleet_Auth_Type;
	}

	public void setFleet_Auth_Type(String fleet_Auth_Type) {
		Fleet_Auth_Type = fleet_Auth_Type;
	}

	public String getFleet_Auth_No() {
		return Fleet_Auth_No;
	}

	public void setFleet_Auth_No(String fleet_Auth_No) {
		Fleet_Auth_No = fleet_Auth_No;
	}

	public String getDrv_Auth_Type() {
		return Drv_Auth_Type;
	}

	public void setDrv_Auth_Type(String drv_Auth_Type) {
		Drv_Auth_Type = drv_Auth_Type;
	}

	public String getDrv_Auth_No() {
		return Drv_Auth_No;
	}

	public void setDrv_Auth_No(String drv_Auth_No) {
		Drv_Auth_No = drv_Auth_No;
	}

	public String getReq_Qty() {
		return Req_Qty;
	}

	public void setReq_Qty(String req_Qty) {
		Req_Qty = req_Qty;
	}

	public String getReq_Value() {
		return Req_Value;
	}

	public void setReq_Value(String req_Value) {
		Req_Value = req_Value;
	}

	public String getMarket_Price() {
		return Market_Price;
	}

	public void setMarket_Price(String market_Price) {
		Market_Price = market_Price;
	}

	public String getSubsidery_Price() {
		return Subsidery_Price;
	}

	public void setSubsidery_Price(String subsidery_Price) {
		Subsidery_Price = subsidery_Price;
	}

	public String getTranx_ID() {
		return Tranx_ID;
	}

	public void setTranx_ID(String tranx_ID) {
		Tranx_ID = tranx_ID;
	}

	public String getTranx_Time() {
		return Tranx_Time;
	}

	public void setTranx_Time(String tranx_Time) {
		Tranx_Time = tranx_Time;
	}

	public String getTranx_Qty() {
		return Tranx_Qty;
	}

	public void setTranx_Qty(String tranx_Qty) {
		Tranx_Qty = tranx_Qty;
	}

	public String getTranx_Value() {
		return Tranx_Value;
	}

	public void setTranx_Value(String tranx_Value) {
		Tranx_Value = tranx_Value;
	}

	public String getTranx_Price() {
		return Tranx_Price;
	}

	public void setTranx_Price(String tranx_Price) {
		Tranx_Price = tranx_Price;
	}

}
