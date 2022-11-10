package com.bct.HOS.App.utils;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.bct.HOS.App.DAO.SalesDAO;

public class HOScheduler implements Job {

	public void execute(JobExecutionContext context) throws JobExecutionException {

		try {
			new SalesDAO().runDailySales();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
