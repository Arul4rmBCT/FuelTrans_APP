package com.bct.HOS.BOS.BO;


import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "lstTankConfig")
public class LstTankConfig {

	List<TankListConfig> TankListConfigObject=null;


	 // Getter Methods 

	 public List<TankListConfig> getTankListConfig() {
	  return TankListConfigObject;
	 }

	 // Setter Methods 
	 @XmlElement(name = "TankListConfig")
	 public void setTankListConfig(List<TankListConfig> TankListConfigObject) {
	  this.TankListConfigObject = TankListConfigObject;
	 }

}
