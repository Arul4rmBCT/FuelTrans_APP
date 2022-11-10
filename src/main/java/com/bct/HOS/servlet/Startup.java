package com.bct.HOS.servlet;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.http.HttpServlet;

import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

import com.bct.HOS.App.BO.ReportTemplates;
import com.bct.HOS.App.BO.TemplateDef;
import com.bct.HOS.App.BO.Templates;
import com.bct.HOS.App.utils.HOSConfig;
import com.bct.HOS.App.utils.HOSReportSyncScheduler;
import com.bct.HOS.App.utils.HOScheduler;
import com.bct.HOS.App.utils.InMem;
import com.google.gson.Gson;

/**
 * Servlet implementation class Startup
 */
public class Startup extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public static String readFileAsString(String file) throws Exception {
		return new String(Files.readAllBytes(Paths.get(file)));
	}

	public void init(){
		// TODO Auto-generated method stub
		HOSConfig config = new HOSConfig();
		//System.out.println("********** HOS SERVER STARTUP - Initiated ************");
		InMem mem = InMem.getInstance();
		try {
			String templateName = null;
			String json = readFileAsString("reporttemplate.json");
			HashMap<String, TemplateDef> map = new HashMap<String, TemplateDef>();
			ReportTemplates repTemplate = new Gson().fromJson(json, ReportTemplates.class);
			TemplateDef tmpDef = null;
			
			ArrayList<Templates> tmps = repTemplate.getReportTemplates();
			for (Templates tmpDefs : tmps) {
				templateName = tmpDefs.getTemplatename();
				tmpDef = tmpDefs.getTemplatedef();
				map.put(templateName, tmpDef);
			}

			mem.put("REPORT_TEMPLATES", map);
			//System.out.println("********** HOS SERVER STARTUP - Started ************");
			
			// Scheduler processing
			JobDetail hosDBJob = JobBuilder.newJob(HOScheduler.class)
                    .withIdentity("HOSDBJob", "HOSDBGroup").build(); 
            Trigger hosDBTrg = TriggerBuilder.newTrigger()
                    .withIdentity("HOSDBScheduler", "HOSDBGroup")
                    .withSchedule(CronScheduleBuilder.cronSchedule(config.getValue("HOS_SCHD_DB")))
                    .build();
            
            
            JobDetail hosReportSyncJob = JobBuilder.newJob(HOSReportSyncScheduler.class)
                    .withIdentity("HOSReportSyncJob", "HOSReportSyncGroup").build();            
            Trigger hosReportSyncTrg = TriggerBuilder.newTrigger()
                    .withIdentity("HOSReportSyncScheduler", "HOSDBGroup")
                    .withSchedule(CronScheduleBuilder.cronSchedule(config.getValue("HOS_REPORT_SYNC")))
                    .build();
            
            
             
            Scheduler hosDBSch = new StdSchedulerFactory().getScheduler();
            hosDBSch.start();
            hosDBSch.scheduleJob(hosDBJob, hosDBTrg);
            hosDBSch.scheduleJob(hosReportSyncJob, hosReportSyncTrg);
            
            //Thread.sleep(100000);
            //hosDBSch.isShutdown();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
