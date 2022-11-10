package com.bct.HOS.BOS.BO;

import javax.xml.bind.annotation.XmlElement;

public class TankListConfig {

	private String Water;

	private String Temp_High;

	private String Tank_No;

	private String Probe_Type;

	private String Temp_Low;

	private String Probe_Offset;

	private String Max_Capacity;

	private String Temp_Factor;

	private String Delivery_Delay;

	private String Probe_Address;

	private String Overflow_Capacity;

	private String High_Water;

	private String Capacity;

	private String IsWater_Float;

	private String Height;

	private String Mini_Capacity;

	private String Product_No;

	@XmlElement(name = "Water")
	public void setWater(String water) {
		Water = water;
	}

	@XmlElement(name = "Temp_High")
	public void setTemp_High(String temp_High) {
		Temp_High = temp_High;
	}

	@XmlElement(name = "Tank_No")
	public void setTank_No(String tank_No) {
		Tank_No = tank_No;
	}

	@XmlElement(name = "Probe_Type")
	public void setProbe_Type(String probe_Type) {
		Probe_Type = probe_Type;
	}

	@XmlElement(name = "Temp_Low")
	public void setTemp_Low(String temp_Low) {
		Temp_Low = temp_Low;
	}

	@XmlElement(name = "Probe_Offset")
	public void setProbe_Offset(String probe_Offset) {
		Probe_Offset = probe_Offset;
	}

	@XmlElement(name = "Max_Capacity")
	public void setMax_Capacity(String max_Capacity) {
		Max_Capacity = max_Capacity;
	}

	@XmlElement(name = "Temp_Factor")
	public void setTemp_Factor(String temp_Factor) {
		Temp_Factor = temp_Factor;
	}

	@XmlElement(name = "Delivery_Delay")
	public void setDelivery_Delay(String delivery_Delay) {
		Delivery_Delay = delivery_Delay;
	}

	@XmlElement(name = "Probe_Address")
	public void setProbe_Address(String probe_Address) {
		Probe_Address = probe_Address;
	}

	@XmlElement(name = "Overflow_Capacity")
	public void setOverflow_Capacity(String overflow_Capacity) {
		Overflow_Capacity = overflow_Capacity;
	}

	@XmlElement(name = "High_Water")
	public void setHigh_Water(String high_Water) {
		High_Water = high_Water;
	}

	@XmlElement(name = "Capacity")
	public void setCapacity(String capacity) {
		Capacity = capacity;
	}

	@XmlElement(name = "IsWater_Float")
	public void setIsWater_Float(String isWater_Float) {
		IsWater_Float = isWater_Float;
	}

	@XmlElement(name = "Height")
	public void setHeight(String height) {
		Height = height;
	}

	@XmlElement(name = "Mini_Capacity")
	public void setMini_Capacity(String mini_Capacity) {
		Mini_Capacity = mini_Capacity;
	}

	@XmlElement(name = "Product_No")
	public void setProduct_No(String product_No) {
		Product_No = product_No;
	}

	public String getWater() {
		return Water;
	}

	public String getTemp_High() {
		return Temp_High;
	}

	public String getTank_No() {
		return Tank_No;
	}

	public String getProbe_Type() {
		return Probe_Type;
	}

	public String getTemp_Low() {
		return Temp_Low;
	}

	public String getProbe_Offset() {
		return Probe_Offset;
	}

	public String getMax_Capacity() {
		return Max_Capacity;
	}

	public String getTemp_Factor() {
		return Temp_Factor;
	}

	public String getDelivery_Delay() {
		return Delivery_Delay;
	}

	public String getProbe_Address() {
		return Probe_Address;
	}

	public String getOverflow_Capacity() {
		return Overflow_Capacity;
	}

	public String getHigh_Water() {
		return High_Water;
	}

	public String getCapacity() {
		return Capacity;
	}

	public String getIsWater_Float() {
		return IsWater_Float;
	}

	public String getHeight() {
		return Height;
	}

	public String getMini_Capacity() {
		return Mini_Capacity;
	}

	public String getProduct_No() {
		return Product_No;
	}

}
