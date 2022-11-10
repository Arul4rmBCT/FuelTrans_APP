package com.bct.UserMangement.bo;

public class RoleScreenMapping {

	private int role_id;
	private String component_id ="default"; 
	private String screen_id = "default";
	private String permission = "default";
	private String role_desc = "default";
	private int seq_no;
	private int send_conf_flag = 0;
	private String flag;
	
	
	public int getRole_id() {
		return role_id;
	}
	public void setRole_id(int role_id) {
		this.role_id = role_id;
	}
	public String getComponent_id() {
		return component_id;
	}
	public void setComponent_id(String component_id) {
		this.component_id = component_id;
	}
	public String getScreen_id() {
		return screen_id;
	}
	public void setScreen_id(String screen_id) {
		this.screen_id = screen_id;
	}
	public String getPermission() {
		return permission;
	}
	public void setPermission(String permission) {
		this.permission = permission;
	}
	public String getRole_desc() {
		return role_desc;
	}
	public void setRole_desc(String role_desc) {
		this.role_desc = role_desc;
	}
	public int getSeq_no() {
		return seq_no;
	}
	public void setSeq_no(int seq_no) {
		this.seq_no = seq_no;
	}
	public int getSend_conf_flag() {
		return send_conf_flag;
	}
	public void setSend_conf_flag(int send_conf_flag) {
		this.send_conf_flag = send_conf_flag;
	}
	public String getFlag() {
		return flag;
	}
	public void setFlag(String flag) {
		this.flag = flag;
	}
	
    
}
