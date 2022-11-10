package com.bct.HOS.App.utils;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;


import com.bct.HOS.App.DAO.UtilDAO;

public class HOSReportSyncScheduler implements Job {

	public void execute(JobExecutionContext context) throws JobExecutionException {

		try {
			new UtilDAO().runReportSync();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
