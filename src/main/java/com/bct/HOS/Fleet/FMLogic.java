package com.bct.HOS.Fleet;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import com.bct.HOS.App.utils.HOSConfig;
import com.google.gson.Gson;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class FMLogic {

	HOSConfig config = null;

	public FMLogic() {
		// TODO Auto-generated constructor stub
		config = new HOSConfig();
	}

	/**
	 * 
	 * @param reqObj
	 * @return
	 */
	public PreTransResBO preTrans(PreTransReqBO reqObj) {
		PreTransResBO dataset = new PreTransResBO();
		try {
			// System.out.println("**** Pre Transaction Online *****");
			URL url = new URL(config.getValue("PreOnlineTransaction"));
			// System.out.println(url.getHost()+"/"+url.getPort()+"/"+url.getPath());
			Gson gson = new Gson();
			String postData = null;
			byte[] postDataBytes = null;

			BPMSResponse response = new BPMSResponse();
			BPMSRequest request = new BPMSRequest();
			BPMSInputVariables inputVariables = new BPMSInputVariables();
			BPMSIn_Msg in_msg = new BPMSIn_Msg();

			in_msg.setPre_Tranx_ID(reqObj.getPre_Tranx_ID());
			in_msg.setReq_Time(reqObj.getReq_Time());
			in_msg.setSite_Code(reqObj.getSite_Code());
			in_msg.setPrd_Code(reqObj.getPrd_Code());
			in_msg.setFleet_Auth_Type(reqObj.getFleet_Auth_Type());
			in_msg.setFleet_Auth_No(reqObj.getFleet_Auth_No());
			in_msg.setDrv_Auth_Type(reqObj.getDrv_Auth_Type());
			in_msg.setDrv_Auth_No(reqObj.getDrv_Auth_No());
			in_msg.setReq_Qty(reqObj.getReq_Qty());
			in_msg.setReq_Value(reqObj.getReq_Value());
			in_msg.setMarket_Price(reqObj.getMarket_Price());
			in_msg.setSubsidery_Price(reqObj.getSubsidery_Price());
			inputVariables.setIn_msg(in_msg);
			request.setInputVariables(inputVariables);

			postData = gson.toJson(request);
			// System.out.println("postData="+postData);
			postDataBytes = postData.toString().getBytes("UTF-8");

			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/json");
			// conn.setRequestProperty("Content-Length",
			// String.valueOf(postDataBytes.length));
			conn.setDoOutput(true);
			conn.getOutputStream().write(postDataBytes);

			int status = conn.getResponseCode();
			StringBuilder sb = new StringBuilder();
			if (status == 200 || status == 201) {
				BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
				String line;
				while ((line = br.readLine()) != null) {
					sb.append(line + "\n");
				}
				br.close();
			}
			// System.out.println("BPMS status::"+status);
			String responseJson = sb.toString();
			// System.out.println("RESPONSE FROM FLEET>>"+responseJson);
			if (responseJson != null) {
				response = gson.fromJson(responseJson, BPMSResponse.class);

				BPMSOut_Msg outObj = response.getOut_msg();
				dataset.setPre_Tranx_ID(outObj.getPre_Tranx_ID());
				dataset.setRes_Code(outObj.getRes_Code());
				dataset.setRes_Message(outObj.getRes_Message());
				dataset.setRes_Time(outObj.getRes_Time());
				dataset.setApproved_Qty(outObj.getApproved_Qty());
				dataset.setApproved_Value(outObj.getApproved_Value());
				dataset.setApproved_Price(outObj.getApproved_Price());
				dataset.setCard_Type(outObj.getCard_Type());
				dataset.setFleet_Code(outObj.getFleet_Code());
				dataset.setAcc_Type(outObj.getAcc_Type());
				dataset.setVehicle_No(outObj.getVehicle_No());
				dataset.setResponseDescription(outObj.getResponseDescription());
				dataset.setError_Code(outObj.getError_Code());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dataset;
	}

	/**
	 * 
	 * @param reqObj
	 * @return
	 */
	public PosTransResBO postTrans(PosTransReqBO reqObj) {
		PosTransResBO dataset = new PosTransResBO();
		try {
			// System.out.println("**** Post Transaction Online *****");
			URL url = new URL(config.getValue("PostOnlineTransaction"));
			// System.out.println(url.getHost()+"/"+url.getPort()+"/"+url.getPath());
			Gson gson = new Gson();
			String postData = null;
			byte[] postDataBytes = null;

			BPMSResponse response = new BPMSResponse();
			BPMSRequest request = new BPMSRequest();
			BPMSInputVariables inputVariables = new BPMSInputVariables();
			BPMSIn_Msg in_msg = new BPMSIn_Msg();

			in_msg.setPre_Tranx_ID(reqObj.getPre_Tranx_ID());
			in_msg.setTranx_ID(reqObj.getTranx_ID());
			in_msg.setTranx_Time(reqObj.getTranx_Time());
			in_msg.setTranx_Qty(reqObj.getTranx_Qty());
			in_msg.setTranx_Value(reqObj.getTranx_Value());
			in_msg.setTranx_Price(reqObj.getTranx_Price());

			inputVariables.setIn_msg(in_msg);
			request.setInputVariables(inputVariables);

			postData = gson.toJson(request);
			// System.out.println("postData="+postData);
			postDataBytes = postData.toString().getBytes("UTF-8");

			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/json");
			// conn.setRequestProperty("Content-Length",
			// String.valueOf(postDataBytes.length));
			conn.setDoOutput(true);
			conn.getOutputStream().write(postDataBytes);

			int status = conn.getResponseCode();
			StringBuilder sb = new StringBuilder();
			if (status == 200 || status == 201) {
				BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
				String line;
				while ((line = br.readLine()) != null) {
					sb.append(line + "\n");
				}
				br.close();
			}
			// System.out.println("BPMS status::"+status);
			String responseJson = sb.toString();
			// System.out.println("RESPONSE FROM FLEET>>"+responseJson);
			if (responseJson != null) {
				response = gson.fromJson(responseJson, BPMSResponse.class);

				BPMSOut_Msg outObj = response.getOut_msg();
				dataset.setPre_Tranx_ID(outObj.getPre_Tranx_ID());
				dataset.setTranx_ID(outObj.getTranx_ID());
				dataset.setTranx_Qty(outObj.getTranx_Qty());
				dataset.setTranx_Price(outObj.getTranx_Price());
				dataset.setTranx_Value(outObj.getTranx_Value());
				dataset.setResponseMessage(outObj.getResponseMessage());
				dataset.setTransStatus(outObj.getTransStatus());

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dataset;
	}

	/**
	 * 
	 * @param reqList
	 * @return
	 */
	public ArrayList<PosTransResBO> postOfflineTrans(ArrayList<BPMSIn_Msg> reqList) {
		ArrayList<PosTransResBO> dataset = new ArrayList<PosTransResBO>();
		try {
			// System.out.println("**** Post Offline Transaction Online *****");
			URL url = new URL(config.getValue("PostOffTransaction"));
			// System.out.println(url.getHost()+"/"+url.getPort()+"/"+url.getPath());
			Gson gson = new Gson();
			String postData = null;
			byte[] postDataBytes = null;

			BPMSArrayResponse response = new BPMSArrayResponse();
			BPMSArrayRequest request = new BPMSArrayRequest();
			BPMSInputArray inputVariables = new BPMSInputArray();
			inputVariables.setIn_msg(reqList);
			request.setInputVariables(inputVariables);

			postData = gson.toJson(request);
			// System.out.println("postData="+postData);
			postDataBytes = postData.toString().getBytes("UTF-8");

			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/json");
			// conn.setRequestProperty("Content-Length",
			// String.valueOf(postDataBytes.length));
			conn.setDoOutput(true);
			conn.getOutputStream().write(postDataBytes);

			int status = conn.getResponseCode();
			StringBuilder sb = new StringBuilder();
			if (status == 200 || status == 201) {
				BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
				String line;
				while ((line = br.readLine()) != null) {
					sb.append(line + "\n");
				}
				br.close();
			}
			// System.out.println("BPMS status::"+status);
			String responseJson = sb.toString();
			// System.out.println("RESPONSE FROM FLEET>>"+responseJson);
			if (responseJson != null) {
				response = gson.fromJson(responseJson, BPMSArrayResponse.class);
				ArrayList<BPMSTransactionList> trans = response.getOut_msg().getTransactionList();
				if (trans != null) {
					PosTransResBO resInnerObj = null;
					for (BPMSTransactionList transObj : trans) {
						resInnerObj = new PosTransResBO();
						resInnerObj.setPre_Tranx_ID(transObj.getPre_Tranx_ID());
						resInnerObj.setTranx_ID(transObj.getTranx_ID());
						resInnerObj.setTranx_Qty(transObj.getTranx_Qty());
						resInnerObj.setTranx_Value(transObj.getTranx_Value());
						resInnerObj.setRes_Code(transObj.getRes_Code());
						resInnerObj.setRes_Message(transObj.getRes_Message());
						resInnerObj.setTranx_Price(transObj.getTranx_Price());
						dataset.add(resInnerObj);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dataset;
	}

	/**
	 * 
	 * @param reqObj
	 * @return
	 */
	public String offLineDataSet(OfflineDataReqBO reqObj) {
		String responseJson = null;
		try {
			// System.out.println("**** Offline Dataset *****");
			URL url = new URL(config.getValue("GetOfflineRuleDataSet"));
			// System.out.println(url.getHost()+"/"+url.getPort()+"/"+url.getPath());
			Gson gson = new Gson();
			String postData = null;
			byte[] postDataBytes = null;

			BPMSResponse response = new BPMSResponse();
			BPMSRequest request = new BPMSRequest();
			BPMSInputVariables inputVariables = new BPMSInputVariables();
			BPMSIn_Msg in_msg = new BPMSIn_Msg();

			in_msg.setSites(reqObj.getSites());

			inputVariables.setIn_msg(in_msg);
			request.setInputVariables(inputVariables);

			postData = gson.toJson(request);
			// System.out.println("postData="+postData);
			postDataBytes = postData.toString().getBytes("UTF-8");

			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/json");
			// conn.setRequestProperty("Content-Length",
			// String.valueOf(postDataBytes.length));
			conn.setDoOutput(true);
			conn.getOutputStream().write(postDataBytes);

			int status = conn.getResponseCode();
			StringBuilder sb = new StringBuilder();
			if (status == 200 || status == 201) {
				BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
				String line;
				while ((line = br.readLine()) != null) {
					sb.append(line + "\n");
				}
				br.close();
			}
			// System.out.println("BPMS status::"+status);
			responseJson = sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return responseJson;
	}

}
