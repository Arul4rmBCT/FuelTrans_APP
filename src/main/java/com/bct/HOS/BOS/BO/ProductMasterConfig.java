package com.bct.HOS.BOS.BO;

import javax.xml.bind.annotation.XmlElement;

public class ProductMasterConfig {

	private String Product_Unit;

	private String Product_Color;

	private String Product_Name;

	private String Product_Reorder;

	private String Product_Type;

	private String Product_No;

	private String SAP_Code;

	public String getProduct_Unit() {
		return Product_Unit;
	}

	@XmlElement(name = "Product_Unit")
	public void setProduct_Unit(String Product_Unit) {
		this.Product_Unit = Product_Unit;
	}

	public String getProduct_Color() {
		return Product_Color;
	}

	@XmlElement(name = "Product_Color")
	public void setProduct_Color(String Product_Color) {
		this.Product_Color = Product_Color;
	}

	public String getProduct_Name() {
		return Product_Name;
	}

	@XmlElement(name = "Product_Name")
	public void setProduct_Name(String Product_Name) {
		this.Product_Name = Product_Name;
	}

	public String getProduct_Reorder() {
		return Product_Reorder;
	}

	@XmlElement(name = "Product_Reorder")
	public void setProduct_Reorder(String Product_Reorder) {
		this.Product_Reorder = Product_Reorder;
	}

	public String getProduct_Type() {
		return Product_Type;
	}

	@XmlElement(name = "Product_Type")
	public void setProduct_Type(String Product_Type) {
		this.Product_Type = Product_Type;
	}

	public String getProduct_No() {
		return Product_No;
	}

	@XmlElement(name = "Product_No")
	public void setProduct_No(String Product_No) {
		this.Product_No = Product_No;
	}

	public String getSAP_Code() {
		return SAP_Code;
	}

	public void setSAP_Code(String SAP_Code) {
		this.SAP_Code = SAP_Code;
	}

}
