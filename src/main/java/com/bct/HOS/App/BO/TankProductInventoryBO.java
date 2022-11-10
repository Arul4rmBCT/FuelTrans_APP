package com.bct.HOS.App.BO;

public class TankProductInventoryBO {

	String tank = null;
	String product = null;
	String volume = null;
	
	public String getTank() {
		return tank;
	}
	public void setTank(String tank) {
		this.tank = tank;
	}
	public String getProduct() {
		return product;
	}
	public void setProduct(String product) {
		this.product = product;
	}
	public double getVolume() {
		return Double.parseDouble(volume);
	}
	public void setVolume(String volume) {
		this.volume = volume;
	}
	
}
