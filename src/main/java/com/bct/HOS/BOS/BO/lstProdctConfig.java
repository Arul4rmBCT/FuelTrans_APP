package com.bct.HOS.BOS.BO;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "lstProdctConfig")
public class lstProdctConfig {

	List<ProductMasterConfig> ProductMasterConfig=null;


	 // Getter Methods 
	
	 public List<ProductMasterConfig> getProductMasterConfig() {
	  return ProductMasterConfig;
	 }

	 // Setter Methods 
	 @XmlElement(name = "ProductMasterConfig")
	 public void setProductMasterConfig(List<ProductMasterConfig> ProductMasterConfig) {
	  this.ProductMasterConfig = ProductMasterConfig;
	 }

}
