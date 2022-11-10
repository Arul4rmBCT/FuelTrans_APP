package com.bct.HOS.Fleet;

public class Rules {

	String ruleid = null;
	RuleParams woking_dys_timelimit = null;
	RuleParams holiday_exception = null;
	RuleParams offline_rules = null;

	public String getRuleid() {
		return ruleid;
	}

	public void setRuleid(String ruleid) {
		this.ruleid = ruleid;
	}

	public RuleParams getWoking_dys_timelimit() {
		return woking_dys_timelimit;
	}

	public void setWoking_dys_timelimit(RuleParams woking_dys_timelimit) {
		this.woking_dys_timelimit = woking_dys_timelimit;
	}

	public RuleParams getHoliday_exception() {
		return holiday_exception;
	}

	public void setHoliday_exception(RuleParams holiday_exception) {
		this.holiday_exception = holiday_exception;
	}

	public RuleParams getOffline_rules() {
		return offline_rules;
	}

	public void setOffline_rules(RuleParams offline_rules) {
		this.offline_rules = offline_rules;
	}

}
