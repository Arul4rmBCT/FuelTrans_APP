package com.bct.HOS.Fleet;

import java.util.ArrayList;

public class PosOfflineTransRes {

	ArrayList<PosTransResBO> posTrans = new ArrayList<PosTransResBO>();

	public ArrayList<PosTransResBO> getPosTransReqBO() {
		return posTrans;
	}

	public void setPosTransReqBO(ArrayList<PosTransResBO> posTrans) {
		this.posTrans = posTrans;
	}

}
