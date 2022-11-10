package com.bct.HOS.App.BO;

public class PriceBO {
	
		String siteID= null;
		String productName = null;
		float newPrice = 0;
		String effectiveFrom = null;
		String country = null;
		
		public String getSiteID() {
			return siteID;
		}
		public void setSiteID(String siteID) {
			this.siteID = siteID;
		}
		public String getProductName() {
			return productName;
		}
		public void setProductName(String productName) {
			this.productName = productName;
		}
		public float getNewPrice() {
			return newPrice;
		}
		public void setNewPrice(float newPrice) {
			this.newPrice = newPrice;
		}
		public String getEffectiveFrom() {
			return effectiveFrom;
		}
		public void setEffectiveFrom(String effectiveFrom) {
			this.effectiveFrom = effectiveFrom;
		}
		public String getCountry() {
			return country;
		}
		public void setCountry(String country) {
			this.country = country;
		}
		
		 
	
}
