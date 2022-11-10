package com.bct.HOS.BOS.BO;

import javax.xml.bind.annotation.XmlElement;

public class ObjSiteConfig {

	private String Site_ID;
	private String Country;
	private String Region;
	private String State;
	private String Zone_Code;
	private String Site_Name;
	private String Site_Type;
	private String Site_Group;
	private String Dealer_Name;
	private String Address1;
	private String Address2;
	private String Address3;
	private String City;
	private String Pin_Code;
	private String Contact;
	private String Mobile_No;
	private String Email;
	private String SAP_Code;
	private String EOD_Type;
	private String EOD_Time;
	private String DO_Name;
	private String DO_Code;

	// Getter Methods

	public String getSite_ID() {
		return Site_ID;
	}

	public String getCountry() {
		return Country;
	}

	public String getRegion() {
		return Region;
	}

	public String getState() {
		return State;
	}

	public String getZone_Code() {
		return Zone_Code;
	}

	public String getSite_Name() {
		return Site_Name;
	}

	public String getSite_Type() {
		return Site_Type;
	}

	public String getSite_Group() {
		return Site_Group;
	}

	public String getDealer_Name() {
		return Dealer_Name;
	}

	public String getAddress1() {
		return Address1;
	}

	public String getAddress2() {
		return Address2;
	}

	public String getAddress3() {
		return Address3;
	}

	public String getCity() {
		return City;
	}

	public String getPin_Code() {
		return Pin_Code;
	}

	public String getContact() {
		return Contact;
	}

	public String getMobile_No() {
		return Mobile_No;
	}

	public String getEmail() {
		return Email;
	}

	public String getSAP_Code() {
		return SAP_Code;
	}

	public String getEOD_Type() {
		return EOD_Type;
	}

	public String getEOD_Time() {
		return EOD_Time;
	}

	public String getDO_Name() {
		return DO_Name;
	}

	public String getDO_Code() {
		return DO_Code;
	}

	// Setter Methods

	@XmlElement(name = "Site_ID")
	public void setSite_ID(String Site_ID) {
		this.Site_ID = Site_ID;
	}

	@XmlElement(name = "Country")
	public void setCountry(String Country) {
		this.Country = Country;
	}

	@XmlElement(name = "Region")
	public void setRegion(String Region) {
		this.Region = Region;
	}

	@XmlElement(name = "State")
	public void setState(String State) {
		this.State = State;
	}

	@XmlElement(name = "Zone_Code")
	public void setZone_Code(String Zone_Code) {
		this.Zone_Code = Zone_Code;
	}

	@XmlElement(name = "Site_Name")
	public void setSite_Name(String Site_Name) {
		this.Site_Name = Site_Name;
	}

	@XmlElement(name = "Site_Type")
	public void setSite_Type(String Site_Type) {
		this.Site_Type = Site_Type;
	}

	@XmlElement(name = "Site_Group")
	public void setSite_Group(String Site_Group) {
		this.Site_Group = Site_Group;
	}

	@XmlElement(name = "Dealer_Name")
	public void setDealer_Name(String Dealer_Name) {
		this.Dealer_Name = Dealer_Name;
	}

	@XmlElement(name = "Address1")
	public void setAddress1(String Address1) {
		this.Address1 = Address1;
	}

	@XmlElement(name = "Address2")
	public void setAddress2(String Address2) {
		this.Address2 = Address2;
	}

	@XmlElement(name = "Address3")
	public void setAddress3(String Address3) {
		this.Address3 = Address3;
	}

	@XmlElement(name = "City")
	public void setCity(String City) {
		this.City = City;
	}

	@XmlElement(name = "Pin_Code")
	public void setPin_Code(String Pin_Code) {
		this.Pin_Code = Pin_Code;
	}

	@XmlElement(name = "Contact")
	public void setContact(String Contact) {
		this.Contact = Contact;
	}

	@XmlElement(name = "Mobile_No")
	public void setMobile_No(String Mobile_No) {
		this.Mobile_No = Mobile_No;
	}

	@XmlElement(name = "Email")
	public void setEmail(String Email) {
		this.Email = Email;
	}

	@XmlElement(name = "SAP_Code")
	public void setSAP_Code(String SAP_Code) {
		this.SAP_Code = SAP_Code;
	}

	@XmlElement(name = "EOD_Type")
	public void setEOD_Type(String EOD_Type) {
		this.EOD_Type = EOD_Type;
	}

	@XmlElement(name = "EOD_Time")
	public void setEOD_Time(String EOD_Time) {
		this.EOD_Time = EOD_Time;
	}

	@XmlElement(name = "DO_Name")
	public void setDO_Name(String DO_Name) {
		this.DO_Name = DO_Name;
	}

	@XmlElement(name = "DO_Code")
	public void setDO_Code(String DO_Code) {
		this.DO_Code = DO_Code;
	}

}
