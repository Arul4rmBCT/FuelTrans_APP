package com.bct.HOS.BOS.BO;

import javax.xml.bind.annotation.XmlElement;

public class GradeMasterConfig {

	private String Grade_Color;

	private String Grade_Name;

	private String High_Product;

	private String Grade_Price;

	private String Low_Product;

	private String Grade_No;

	private String High_Percentage;

	private String Grade_Unit;

	private String Low_Percentage;

	private String SAP_Code;

	@XmlElement(name = "Grade_Color")
	public void setGrade_Color(String grade_Color) {
		Grade_Color = grade_Color;
	}

	@XmlElement(name = "Grade_Name")
	public void setGrade_Name(String grade_Name) {
		Grade_Name = grade_Name;
	}

	@XmlElement(name = "High_Product")
	public void setHigh_Product(String high_Product) {
		High_Product = high_Product;
	}

	@XmlElement(name = "Grade_Price")
	public void setGrade_Price(String grade_Price) {
		Grade_Price = grade_Price;
	}

	@XmlElement(name = "Low_Product")
	public void setLow_Product(String low_Product) {
		Low_Product = low_Product;
	}

	@XmlElement(name = "Grade_No")
	public void setGrade_No(String grade_No) {
		Grade_No = grade_No;
	}

	@XmlElement(name = "High_Percentage")
	public void setHigh_Percentage(String high_Percentage) {
		High_Percentage = high_Percentage;
	}

	@XmlElement(name = "Grade_Unit")
	public void setGrade_Unit(String grade_Unit) {
		Grade_Unit = grade_Unit;
	}

	@XmlElement(name = "Low_Percentage")
	public void setLow_Percentage(String low_Percentage) {
		Low_Percentage = low_Percentage;
	}

	@XmlElement(name = "SAP_Code")
	public void setSAP_Code(String sAP_Code) {
		SAP_Code = sAP_Code;
	}

	public String getGrade_Color() {
		return Grade_Color;
	}

	public String getGrade_Name() {
		return Grade_Name;
	}

	public String getHigh_Product() {
		return High_Product;
	}

	public String getGrade_Price() {
		return Grade_Price;
	}

	public String getLow_Product() {
		return Low_Product;
	}

	public String getGrade_No() {
		return Grade_No;
	}

	public String getHigh_Percentage() {
		return High_Percentage;
	}

	public String getGrade_Unit() {
		return Grade_Unit;
	}

	public String getLow_Percentage() {
		return Low_Percentage;
	}

	public String getSAP_Code() {
		return SAP_Code;
	}

	@Override
	public String toString() {
		return "ClassPojo [Grade_Color = " + Grade_Color + ", Grade_Name = " + Grade_Name + ", High_Product = "
				+ High_Product + ", Grade_Price = " + Grade_Price + ", Low_Product = " + Low_Product + ", Grade_No = "
				+ Grade_No + ", High_Percentage = " + High_Percentage + ", Grade_Unit = " + Grade_Unit
				+ ", Low_Percentage = " + Low_Percentage + ", SAP_Code = " + SAP_Code + "]";
	}

}
