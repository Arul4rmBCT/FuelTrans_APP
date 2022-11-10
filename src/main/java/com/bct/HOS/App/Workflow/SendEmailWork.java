package com.bct.HOS.App.Workflow;

import org.jeasy.flows.work.DefaultWorkReport;
import org.jeasy.flows.work.Work;
import org.jeasy.flows.work.WorkContext;
import org.jeasy.flows.work.WorkReport;
import org.jeasy.flows.work.WorkStatus;

public class SendEmailWork implements Work {

	private String message;

	public SendEmailWork(String message) {
		this.message = message;
	}

	public String getName() {
		return "print message work";
	}

	public WorkReport execute(WorkContext workContext) {
		//System.out.println(message);
		return new DefaultWorkReport(WorkStatus.COMPLETED, workContext);

	}

}
