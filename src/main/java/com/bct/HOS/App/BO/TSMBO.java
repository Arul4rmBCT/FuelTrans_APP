package com.bct.HOS.App.BO;

import java.util.List;

public class TSMBO {

	String userId = null;
	String roleId = null;
	String userName = null;
	String roleName = null;
	String loginId = null;
	String fromDate = null;
	String toDate = null;
	String fromTime = null;
	String toTime = null;
	String productName = null;
	String count = null;
	String date = null;
	String timePeriodType = null;
	String recordLimit = null;
	String productCode = null;
	String dayDiff = null;
	String siteID = null;
	String country = null;
	String state = null;
	String division = null;
	String city = null;
	String subDistrict = null;
	String region = null;
	String district = null;
	String pump = null;
	String tank = null;
	String nozzle = null;
	String du = null;
	String mode = null;
	List<String> siteIDList = null;
	float newPrice = 0;
	String effectiveFrom = null;
	List<PriceBO> bulkPrice = null;
	// Firmware
	boolean grouping = false;
	String fccVersion = null;
	String versionName = null;
	String releaseNote = null;
	String comment = null;
	String status = null;
	String clientName = null;

	String tagId = null;
	String attendeeName = null;

	
	public String getFromTime() {
		return fromTime;
	}

	public void setFromTime(String fromTime) {
		this.fromTime = fromTime;
	}

	public String getToTime() {
		return toTime;
	}

	public void setToTime(String toTime) {
		this.toTime = toTime;
	}

	public String getTagId() {
		return tagId;
	}

	public void setTagId(String tagId) {
		this.tagId = tagId;
	}

	public String getAttendeeName() {
		return attendeeName;
	}

	public void setAttendeeName(String attendeeName) {
		this.attendeeName = attendeeName;
	}

	boolean latestRecord = false;

	public boolean isLatestRecord() {
		return latestRecord;
	}

	public void setLatestRecord(boolean latestRecord) {
		this.latestRecord = latestRecord;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getFccVersion() {
		return fccVersion;
	}

	public void setFccVersion(String fccVersion) {
		this.fccVersion = fccVersion;
	}

	public String getVersionName() {
		return versionName;
	}

	public void setVersionName(String versionName) {
		this.versionName = versionName;
	}

	public String getReleaseNote() {
		return releaseNote;
	}

	public void setReleaseNote(String releaseNote) {
		this.releaseNote = releaseNote;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public float getNewPrice() {
		return newPrice;
	}

	public void setNewPrice(float newPrice) {
		this.newPrice = newPrice;
	}

	public List<String> getSiteIDList() {
		return siteIDList;
	}

	public void setSiteIDList(List<String> siteIDList) {
		this.siteIDList = siteIDList;
	}

	public String getEffectiveFrom() {
		return effectiveFrom;
	}

	public void setEffectiveFrom(String effectiveFrom) {
		this.effectiveFrom = effectiveFrom;
	}

	public String getPump() {
		return pump;
	}

	public void setPump(String pump) {
		this.pump = pump;
	}

	public String getTank() {
		return tank;
	}

	public void setTank(String tank) {
		this.tank = tank;
	}

	public String getNozzle() {
		return nozzle;
	}

	public void setNozzle(String nozzle) {
		this.nozzle = nozzle;
	}

	public String getDu() {
		return du;
	}

	public void setDu(String du) {
		this.du = du;
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public String getDistrict() {
		return district;
	}

	public void setDistrict(String district) {
		this.district = district;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getSubDistrict() {
		return subDistrict;
	}

	public void setSubDistrict(String subDistrict) {
		this.subDistrict = subDistrict;
	}

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getDivision() {
		return division;
	}

	public void setDivision(String division) {
		this.division = division;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	boolean paymentMode = false;

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getRoleId() {
		return roleId;
	}

	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public String getLoginId() {
		return loginId;
	}

	public void setLoginId(String loginId) {
		this.loginId = loginId;
	}

	public String getFromDate() {
		return fromDate;
	}

	public void setFromDate(String fromDate) {
		this.fromDate = fromDate;
	}

	public String getToDate() {
		return toDate;
	}

	public void setToDate(String toDate) {
		this.toDate = toDate;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getCount() {
		return count;
	}

	public void setCount(String count) {
		this.count = count;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getTimePeriodType() {
		return timePeriodType;
	}

	public void setTimePeriodType(String timePeriodType) {
		this.timePeriodType = timePeriodType;
	}

	public String getRecordLimit() {
		return recordLimit;
	}

	public void setRecordLimit(String recordLimit) {
		this.recordLimit = recordLimit;
	}

	public String getProductCode() {
		return productCode;
	}

	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}

	public String getDayDiff() {
		return dayDiff;
	}

	public void setDayDiff(String dayDiff) {
		this.dayDiff = dayDiff;
	}

	public boolean isPaymentMode() {
		return paymentMode;
	}

	public void setPaymentMode(boolean paymentMode) {
		this.paymentMode = paymentMode;
	}

	public String getSiteID() {

		if (isNullOrEmpty(siteID)) {
			return null;
		} else {
			if (!siteID.equalsIgnoreCase("null"))
				if (siteID.startsWith("'"))
					return siteID;
				else
					return "'" + siteID + "'";
			else
				return null;
		}
	}

	public void setSiteID(String siteID) {
		this.siteID = siteID;
	}

	public static boolean isNullOrEmpty(String str) {
		if (str != null && !str.isEmpty())
			return false;
		return true;
	}

	public List<PriceBO> getBulkPrice() {
		return bulkPrice;
	}

	public void setBulkPrice(List<PriceBO> bulkPrice) {
		this.bulkPrice = bulkPrice;
	}

	public boolean isGrouping() {
		return grouping;
	}

	public void setGrouping(boolean grouping) {
		this.grouping = grouping;
	}

	public String getClientName() {
		return clientName;
	}

	public void setClientName(String clientName) {
		this.clientName = clientName;
	}

}
