package com.bct.UserMangement.bo;

import java.sql.Date;

public class User {
	
	private int roleid;
	private String firstname;
	private String middlename;
	private String lastname;
	private String username;
	private String mobile;
	private String email;
	private String passwordhash;
	private String webaccess;
	private String mobileaccess;
	private Date lastlogin;
	private String status;
	private String created_by;
	private Date created_date;
	private String modified_by;
	private Date modified_date;
	private String flag;
	
	public int getRoleid() {
		return roleid;
	}
	public void setRoleid(int roleid) {
		this.roleid = roleid;
	}
	public String getFirstname() {
		return firstname;
	}
	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}
	public String getMiddlename() {
		return middlename;
	}
	public void setMiddlename(String middlename) {
		this.middlename = middlename;
	}
	public String getLastname() {
		return lastname;
	}
	public void setLastname(String lastname) {
		this.lastname = lastname;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPasswordhash() {
		return passwordhash;
	}
	public void setPasswordhash(String passwordhash) {
		this.passwordhash = passwordhash;
	}
	public String getWebaccess() {
		return webaccess;
	}
	public void setWebaccess(String webaccess) {
		this.webaccess = webaccess;
	}
	public String getMobileaccess() {
		return mobileaccess;
	}
	public void setMobileaccess(String mobileaccess) {
		this.mobileaccess = mobileaccess;
	}
	public Date getLastlogin() {
		return lastlogin;
	}
	public void setLastlogin(Date lastlogin) {
		this.lastlogin = lastlogin;
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
	public String getFlag() {
		return flag;
	}
	public void setFlag(String flag) {
		this.flag = flag;
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
       	
	
	
}