package com.bct.UserMangement.bo;

public class ModuleMaster {

	private String application_id = "default";
	private String module_id = "default";
	private String module_desc = "default";
	private String language_id = "default";
	private String type = "default";
	private int visibility;
	private String flag;
	
	public String getApplication_id() {
		return application_id;
	}
	public void setApplication_id(String application_id) {
		this.application_id = application_id;
	}
	public String getModule_id() {
		return module_id;
	}
	public void setModule_id(String module_id) {
		this.module_id = module_id;
	}
	public String getModule_desc() {
		return module_desc;
	}
	public void setModule_desc(String module_desc) {
		this.module_desc = module_desc;
	}
	public String getLanguage_id() {
		return language_id;
	}
	public void setLanguage_id(String language_id) {
		this.language_id = language_id;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public int getVisibility() {
		return visibility;
	}
	public void setVisibility(int visibility) {
		this.visibility = visibility;
	}
	public String getFlag() {
		return flag;
	}
	public void setFlag(String flag) {
		this.flag = flag;
	}
	
}
