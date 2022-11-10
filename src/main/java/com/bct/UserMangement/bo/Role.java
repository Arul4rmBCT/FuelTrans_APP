package com.bct.UserMangement.bo;

import java.sql.Date;

public class Role {
	private int id;
	private String role_title;
	private String description;
	private int hierarchy;
	private String status;
	private String created_by;
	private Date created_date;
	private String modified_by;
	private Date modified_date;
	private String flag;
	private int org_id; 
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getOrg_id() {
		return org_id;
	}
	public void setOrg_id(int org_id) {
		this.org_id = org_id;
	}
	public String getRole_title() {
		return role_title;
	}
	public void setRole_title(String role_title) {
		this.role_title = role_title;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public int getHierarchy() {
		return hierarchy;
	}
	public void setHierarchy(int hierarchy) {
		this.hierarchy = hierarchy;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getCreated_by() {
		return created_by;
	}
	public void setCreated_by(String created_by) {
		this.created_by = created_by;
	}
	public String getModified_by() {
		return modified_by;
	}
	public void setModified_by(String modified_by) {
		this.modified_by = modified_by;
	}
	public Date getCreated_date() {
		return created_date;
	}
	public void setCreated_date(Date created_date) {
		this.created_date = created_date;
	}
	public Date getModified_date() {
		return modified_date;
	}
	public void setModified_date(Date modified_date) {
		this.modified_date = modified_date;
	}
	public String getFlag() {
		return flag;
	}
	public void setFlag(String flag) {
		this.flag = flag;
	}
	
    	
	
	
}