package com.bct.UserMangement.bo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import com.google.gson.JsonObject;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class ResponseBO {

	String status = null;
	String errorCode = null;
	String error = null;
	String fileName = null;
	ArrayList data = null;
	JSONArray dataArr = null;
	JSONArray dataSet = null;
	JsonObject dataObject = null;
	JSONObject dataObj = null;
	JSONArray GRID_COLUMN = null;
	JSONArray alarms = null;
	JSONArray notifications = null;
	HashMap dataHash = null;
	String message = null;

	public JSONArray getDataArr() {
		return dataArr;
	}

	public void setDataArr(JSONArray dataArr) {
		this.dataArr = dataArr;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public HashMap getDataHash() {
		return dataHash;
	}

	public void setDataHash(HashMap dataHash) {
		this.dataHash = dataHash;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public ArrayList getData() {
		return data;
	}

	public void setData(ArrayList data) {
		this.data = data;
	}

	public JSONArray getDataSet() {
		return dataSet;
	}

	public void setDataSet(JSONArray dataSet) {
		this.dataSet = dataSet;
	}

	public JSONArray getAlarms() {
		return alarms;
	}

	public void setAlarms(JSONArray alarms) {
		this.alarms = alarms;
	}

	public JSONArray getNotifications() {
		return notifications;
	}

	public void setNotifications(JSONArray notifications) {
		this.notifications = notifications;
	}

	public JSONArray getGRID_COLUMN() {
		return GRID_COLUMN;
	}

	public void setGRID_COLUMN(JSONArray gRID_COLUMN) {
		GRID_COLUMN = new JSONArray();
		try {
			JSONObject jobj = null;

			if (gRID_COLUMN != null) {
				if (gRID_COLUMN.size() > 0) {
					JSONObject jsonObj = gRID_COLUMN.getJSONObject(0);
					Iterator<String> keys = jsonObj.keys();
					int i = 0;
					while (keys.hasNext()) {
						i++;
						String field = keys.next();
						String headerName = field.replaceAll("_", " ");
						headerName = headerName.toUpperCase();
						jobj = new JSONObject();
						jobj.put("field", field);
						jobj.put("headerName", headerName);
						jobj.put("SNO", i);
						GRID_COLUMN.add(jobj);
					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public JsonObject getDataObject() {
		return dataObject;
	}

	public void setDataObject(JsonObject dataObject) {
		this.dataObject = dataObject;
	}

	public JSONObject getDataObj() {
		return dataObj;
	}

	public void setDataObj(JSONObject dataObj) {
		this.dataObj = dataObj;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
	
	

}

