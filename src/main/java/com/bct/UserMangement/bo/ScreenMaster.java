package com.bct.UserMangement.bo;

public class ScreenMaster {
	 
	private int seq_no;
	private String application_id = "default";
    private String module_id = "default";
	private String screen_id;
	private String screen_desc;
	private String language_id = "default";
	private String entry_code = "default";
	private String screen_type = "default";
	private String display_flag = "default";
	private String flag;
	
	public int getSeq_no() {
		return seq_no;
	}
	public void setSeq_no(int seq_no) {
		this.seq_no = seq_no;
	}
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
	public String getScreen_id() {
		return screen_id;
	}
	public void setScreen_id(String screen_id) {
		this.screen_id = screen_id;
	}
	public String getScreen_desc() {
		return screen_desc;
	}
	public void setScreen_desc(String screen_desc) {
		this.screen_desc = screen_desc;
	}
	public String getLanguage_id() {
		return language_id;
	}
	public void setLanguage_id(String language_id) {
		this.language_id = language_id;
	}
	public String getEntry_code() {
		return entry_code;
	}
	public void setEntry_code(String entry_code) {
		this.entry_code = entry_code;
	}
	public String getScreen_type() {
		return screen_type;
	}
	public void setScreen_type(String screen_type) {
		this.screen_type = screen_type;
	}
	public String getDisplay_flag() {
		return display_flag;
	}
	public void setDisplay_flag(String display_flag) {
		this.display_flag = display_flag;
	}
	public String getFlag() {
		return flag;
	}
	public void setFlag(String flag) {
		this.flag = flag;
	}
		   
}
