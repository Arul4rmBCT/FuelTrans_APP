package com.bct.HOS.Fleet;

import java.util.ArrayList;

public class CardDetails {

	String identification_type = null;
	String identification_no = null;
	String identification_status = null;
	String valid_from_date = null;
	String valid_to_date = null;
	String mapped_fleet_code = null;
	String mapped_fleet_reg_no = null;
	String fleet_status = null;
	String allowed_primary_fuel = null;
	String mapped_driver_code = null;
	String driver_status = null;
	ArrayList<Rules> rules = null;

	public String getIdentification_type() {
		return identification_type;
	}

	public void setIdentification_type(String identification_type) {
		this.identification_type = identification_type;
	}

	public String getIdentification_no() {
		return identification_no;
	}

	public void setIdentification_no(String identification_no) {
		this.identification_no = identification_no;
	}

	public String getIdentification_status() {
		return identification_status;
	}

	public void setIdentification_status(String identification_status) {
		this.identification_status = identification_status;
	}

	public String getValid_from_date() {
		return valid_from_date;
	}

	public void setValid_from_date(String valid_from_date) {
		this.valid_from_date = valid_from_date;
	}

	public String getValid_to_date() {
		return valid_to_date;
	}

	public void setValid_to_date(String valid_to_date) {
		this.valid_to_date = valid_to_date;
	}

	public String getMapped_fleet_code() {
		return mapped_fleet_code;
	}

	public void setMapped_fleet_code(String mapped_fleet_code) {
		this.mapped_fleet_code = mapped_fleet_code;
	}

	public String getMapped_fleet_reg_no() {
		return mapped_fleet_reg_no;
	}

	public void setMapped_fleet_reg_no(String mapped_fleet_reg_no) {
		this.mapped_fleet_reg_no = mapped_fleet_reg_no;
	}

	public String getFleet_status() {
		return fleet_status;
	}

	public void setFleet_status(String fleet_status) {
		this.fleet_status = fleet_status;
	}

	public String getAllowed_primary_fuel() {
		return allowed_primary_fuel;
	}

	public void setAllowed_primary_fuel(String allowed_primary_fuel) {
		this.allowed_primary_fuel = allowed_primary_fuel;
	}

	public String getMapped_driver_code() {
		return mapped_driver_code;
	}

	public void setMapped_driver_code(String mapped_driver_code) {
		this.mapped_driver_code = mapped_driver_code;
	}

	public String getDriver_status() {
		return driver_status;
	}

	public void setDriver_status(String driver_status) {
		this.driver_status = driver_status;
	}

	public ArrayList<Rules> getRules() {
		return rules;
	}

	public void setRules(ArrayList<Rules> rules) {
		this.rules = rules;
	}

}
