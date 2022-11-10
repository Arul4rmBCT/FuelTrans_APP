package com.bct.HOS.Fleet;

import java.util.ArrayList;

public class SitesBO {

	String site_code = null;
	ArrayList<CardDetails> allowed_cards = null;
	ArrayList<Rules> rules_Set = null;

	public String getSite_code() {
		return site_code;
	}

	public void setSite_code(String site_code) {
		this.site_code = site_code;
	}

	public ArrayList<CardDetails> getAllowed_cards() {
		return allowed_cards;
	}

	public void setAllowed_cards(ArrayList<CardDetails> allowed_cards) {
		this.allowed_cards = allowed_cards;
	}

	public ArrayList<Rules> getRules_Set() {
		return rules_Set;
	}

	public void setRules_Set(ArrayList<Rules> rules_Set) {
		this.rules_Set = rules_Set;
	}

}
