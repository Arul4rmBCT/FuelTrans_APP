package com.bct.HOS.App.BO;

public class NotificationConfig {

	private String notificationName;
	private String description;
	private String condition;
	private String actions;

	public String getNotificationName() {
		return notificationName;
	}

	public void setNotificationName(String notificationName) {
		this.notificationName = notificationName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getCondition() {
		return condition;
	}

	public void setCondition(String condition) {
		this.condition = condition;
	}

	public String getActions() {
		return actions;
	}

	public void setActions(String actions) {
		this.actions = actions;
	}

}
