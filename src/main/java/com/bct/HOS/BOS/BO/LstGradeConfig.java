package com.bct.HOS.BOS.BO;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "lstGradeConfig")
public class LstGradeConfig {

	
	List<GradeMasterConfig> GradeMasterConfigObject=null;


	 // Getter Methods 

	 public List<GradeMasterConfig> getGradeMasterConfig() {
	  return GradeMasterConfigObject;
	 }

	 // Setter Methods 
	 @XmlElement(name = "GradeMasterConfig")
	 public void setGradeMasterConfig(List<GradeMasterConfig> GradeMasterConfigObject) {
	  this.GradeMasterConfigObject = GradeMasterConfigObject;
	 }

}
