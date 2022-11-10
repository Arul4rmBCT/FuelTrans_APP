package com.bct.HOS.App.BO;

import java.util.ArrayList;

public class Node {

	String NodeName;
	ArrayList<Node> child;

	public String getNodeName() {
		return NodeName;
	}

	public void setNodeName(String nodeName) {
		NodeName = nodeName;
	}

	public ArrayList<Node> getChild() {
		return child;
	}

	public void setChild(ArrayList<Node> child) {
		this.child = child;
	}

}
