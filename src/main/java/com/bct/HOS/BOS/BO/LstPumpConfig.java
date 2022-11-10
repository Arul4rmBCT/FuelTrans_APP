package com.bct.HOS.BOS.BO;

import javax.xml.bind.annotation.XmlElement;

public class LstPumpConfig {

	PumpListConfig PumpListConfigObject;


	 // Getter Methods 

	 public PumpListConfig getPumpListConfig() {
	  return PumpListConfigObject;
	 }

	 // Setter Methods 
	 @XmlElement(name = "PumpListConfig")
	 public void setPumpListConfig(PumpListConfig PumpListConfigObject) {
	  this.PumpListConfigObject = PumpListConfigObject;
	 }

}
