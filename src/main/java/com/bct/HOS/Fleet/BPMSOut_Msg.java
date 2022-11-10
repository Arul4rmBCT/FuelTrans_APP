package com.bct.HOS.Fleet;

import java.util.ArrayList;

public class BPMSOut_Msg {

	String Pre_Tranx_ID = null;
	String Res_Time = null;
	String Res_Code = null;
	String Res_Message = null;
	String Card_Type = null;
	String Approved_Qty = null;
	String Approved_Value = null;
	String Approved_Price = null;
	String Tranx_ID = null;
	String Tranx_Price = null;
	String Tranx_Qty = null;
	String responseMessage = null;
	String ResponseDescription= null;
	String transStatus = null;
	String Tranx_Value = null;
	ArrayList<BPMSTransactionList> transactionList = null;
	ArrayList<SiteList> siteList = null;
	String Fleet_Code = null;
	String Acc_Type = null;
	String Vehicle_No = null;
	String Error_Code = null;
	

	public String getVehicle_No() {
		return Vehicle_No;
	}

	public void setVehicle_No(String vehicle_No) {
		Vehicle_No = vehicle_No;
	}

	public String getAcc_Type() {
		return Acc_Type;
	}

	public void setAcc_Type(String acc_Type) {
		Acc_Type = acc_Type;
	}

	public String getFleet_Code() {
		return Fleet_Code;
	}

	public void setFleet_Code(String fleet_Code) {
		Fleet_Code = fleet_Code;
	}

	public ArrayList<SiteList> getSiteList() {
		return siteList;
	}

	public void setSiteList(ArrayList<SiteList> siteList) {
		this.siteList = siteList;
	}

	public ArrayList<BPMSTransactionList> getTransactionList() {
		return transactionList;
	}

	public void setTransactionList(ArrayList<BPMSTransactionList> transactionList) {
		this.transactionList = transactionList;
	}

	public String getPre_Tranx_ID() {
		return Pre_Tranx_ID;
	}

	public void setPre_Tranx_ID(String pre_Tranx_ID) {
		Pre_Tranx_ID = pre_Tranx_ID;
	}

	public String getRes_Time() {
		return Res_Time;
	}

	public void setRes_Time(String res_Time) {
		Res_Time = res_Time;
	}

	public String getRes_Code() {
		return Res_Code;
	}

	public void setRes_Code(String res_Code) {
		Res_Code = res_Code;
	}

	public String getRes_Message() {
		return Res_Message;
	}

	public void setRes_Message(String res_Message) {
		Res_Message = res_Message;
	}

	public String getCard_Type() {
		return Card_Type;
	}

	public void setCard_Type(String card_Type) {
		Card_Type = card_Type;
	}

	public String getApproved_Qty() {
		return Approved_Qty;
	}

	public void setApproved_Qty(String approved_Qty) {
		Approved_Qty = approved_Qty;
	}

	public String getApproved_Value() {
		return Approved_Value;
	}

	public void setApproved_Value(String approved_Value) {
		Approved_Value = approved_Value;
	}

	public String getApproved_Price() {
		return Approved_Price;
	}

	public void setApproved_Price(String approved_Price) {
		Approved_Price = approved_Price;
	}

	public String getTranx_ID() {
		return Tranx_ID;
	}

	public void setTranx_ID(String tranx_ID) {
		Tranx_ID = tranx_ID;
	}

	public String getTranx_Price() {
		return Tranx_Price;
	}

	public void setTranx_Price(String tranx_Price) {
		Tranx_Price = tranx_Price;
	}

	public String getTranx_Qty() {
		return Tranx_Qty;
	}

	public void setTranx_Qty(String tranx_Qty) {
		Tranx_Qty = tranx_Qty;
	}

	public String getResponseMessage() {
		return responseMessage;
	}

	public void setResponseMessage(String responseMessage) {
		this.responseMessage = responseMessage;
	}

	public String getTransStatus() {
		return transStatus;
	}

	public void setTransStatus(String transStatus) {
		this.transStatus = transStatus;
	}

	public String getTranx_Value() {
		return Tranx_Value;
	}

	public void setTranx_Value(String tranx_Value) {
		Tranx_Value = tranx_Value;
	}

	public String getResponseDescription() {
		return ResponseDescription;
	}

	public void setResponseDescription(String responseDescription) {
		ResponseDescription = responseDescription;
	}

	public String getError_Code() {
		return Error_Code;
	}

	public void setError_Code(String error_Code) {
		Error_Code = error_Code;
	}
}
