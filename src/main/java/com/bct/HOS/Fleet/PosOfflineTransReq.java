package com.bct.HOS.Fleet;

import java.util.ArrayList;

public class PosOfflineTransReq {

	ArrayList<PosTransReqBO> posTrans = new ArrayList<PosTransReqBO>();

	public ArrayList<PosTransReqBO> getPosTransReqBO() {
		return posTrans;
	}

	public void setPosTransReqBO(ArrayList<PosTransReqBO> posTrans) {
		this.posTrans = posTrans;
	}

}
