package com.bct.HOS.Fleet;

import java.util.ArrayList;

public class SiteList {

	private ArrayList<AllowedCard> allowedCards = null;
	private ArrayList<RulesSet> rulesSet = null;
	private ArrayList<CardDetai> cardDetais = null;
	private String siteCode;

	public ArrayList<AllowedCard> getAllowedCards() {
		return allowedCards;
	}

	public void setAllowedCards(ArrayList<AllowedCard> allowedCards) {
		this.allowedCards = allowedCards;
	}

	public ArrayList<RulesSet> getRulesSet() {
		return rulesSet;
	}

	public void setRulesSet(ArrayList<RulesSet> rulesSet) {
		this.rulesSet = rulesSet;
	}

	public ArrayList<CardDetai> getCardDetais() {
		return cardDetais;
	}

	public void setCardDetais(ArrayList<CardDetai> cardDetais) {
		this.cardDetais = cardDetais;
	}

	public String getSiteCode() {
		return siteCode;
	}

	public void setSiteCode(String siteCode) {
		this.siteCode = siteCode;
	}

}
