package com.bct.HOS.App.utils;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;

@WebServlet(urlPatterns= "/SchedulerServelet",
			loadOnStartup = 1) 
public class SchedulerServlet extends HttpServlet {
    
	private static final long serialVersionUID = 1L;

	public void init(ServletConfig config) throws ServletException {
		System.out.println("Entered into Scheduler Servelet .................... ");
		new Thread(new Runnable() {
		    @Override
		    public void run() {
		
				TimerTask timerTask = new InventoryReportTimer();
				Calendar today = Calendar.getInstance();
				today.set(Calendar.HOUR_OF_DAY, 23);
				today.set(Calendar.MINUTE, 59);
				today.set(Calendar.SECOND,59);
				today.add(Calendar.HOUR, 2);
		        Timer timer = new Timer(true);
		        timer.schedule(timerTask, today.getTime(), TimeUnit.MILLISECONDS.convert(1, TimeUnit.DAYS));
		        //timer.schedule(timerTask, 0, TimeUnit.MILLISECONDS.convert(1, TimeUnit.DAYS));
		        System.out.println("The Inventory report is scheduled .................... ");
			}
		}).start();
		
		 new Thread(new Runnable() {
		    @Override
		    public void run() {
		    	String transType = "FUEL_SLAES";
				TimerTask timerTask = new HosToNavisionPushTimer(transType);
				Calendar today = Calendar.getInstance();
				today.set(Calendar.HOUR_OF_DAY, 23);
				today.set(Calendar.MINUTE, 59);
				today.set(Calendar.SECOND,59);
				today.add(Calendar.HOUR, 3);
		        Timer timer = new Timer(true);
		        timer.schedule(timerTask, today.getTime(), TimeUnit.MILLISECONDS.convert(1, TimeUnit.DAYS));
		        //timer.schedule(timerTask, 0, TimeUnit.MILLISECONDS.convert(1, TimeUnit.MINUTES));
		        System.out.println("The Navision Push Timer is scheduled .................... ");
			}
		}).start();
		 
		 new Thread(new Runnable() {
			    @Override
			    public void run() {
			    	String url = "http://srv22.almaha.com.om:1396/ALM_2021_NOV_DEV/WS/Al%20Maha%20Petroleum%20Products%20Co./Page/FSErrorLOG";
					String payLoad = "<fser:Read xmlns:fser=\"urn:microsoft-dynamics-schemas/page/fserrorlog\"><fser:Entry_No></fser:Entry_No></fser:Read>";
			    	TimerTask timerTask = new NavisionPullErrorLogTimer(url,payLoad);
					Calendar today = Calendar.getInstance();
					today.set(Calendar.HOUR_OF_DAY, 23);
					today.set(Calendar.MINUTE, 59);
					today.set(Calendar.SECOND,59);
					today.add(Calendar.HOUR, 4);
			        Timer timer = new Timer(true);
			        timer.schedule(timerTask, today.getTime(), TimeUnit.MILLISECONDS.convert(1, TimeUnit.DAYS));
			        System.out.println("The Navision Pull Error log Timer is scheduled .................... ");
				}
			}).start();
		 
		 new Thread(new Runnable() {
			    @Override
			    public void run() {
			    	TimerTask timerTask = new NavisionPullLocationMstTimer();
					Calendar today = Calendar.getInstance();
					today.set(Calendar.HOUR_OF_DAY, 23);
					today.set(Calendar.MINUTE, 59);
					today.set(Calendar.SECOND,59);
					today.add(Calendar.MINUTE, 30);
			        Timer timer = new Timer(true);
			        timer.schedule(timerTask, today.getTime(), TimeUnit.MILLISECONDS.convert(1, TimeUnit.DAYS));
			        //timer.schedule(timerTask, 0, TimeUnit.MILLISECONDS.convert(5, TimeUnit.HOURS));
			        System.out.println("The Navision Pull Location Master Timer is scheduled .................... ");
				}
			}).start();
		 
		 new Thread(new Runnable() {
			    @Override
			    public void run() {
			    	TimerTask timerTask = new NavisionPullProdMasterTimer();
					Calendar today = Calendar.getInstance();
					today.set(Calendar.HOUR_OF_DAY, 23);
					today.set(Calendar.MINUTE, 59);
					today.set(Calendar.SECOND,59);
					today.add(Calendar.MINUTE, 40);
			        Timer timer = new Timer(true);
			        timer.schedule(timerTask, today.getTime(), TimeUnit.MILLISECONDS.convert(1, TimeUnit.DAYS));
			        //timer.schedule(timerTask, 0, TimeUnit.MILLISECONDS.convert(5, TimeUnit.HOURS));
			        System.out.println("The Navision Pull Location Master Timer is scheduled .................... ");
				}
			}).start();
		 
		 new Thread(new Runnable() {
			    @Override
			    public void run() {
			    	TimerTask timerTask = new NavisionPullPumpRateMstTimer();
					Calendar today = Calendar.getInstance();
					today.set(Calendar.HOUR_OF_DAY, 23);
					today.set(Calendar.MINUTE, 59);
					today.set(Calendar.SECOND,59);
					today.add(Calendar.MINUTE, 50);
			        Timer timer = new Timer(true);
			        timer.schedule(timerTask, today.getTime(), TimeUnit.MILLISECONDS.convert(1, TimeUnit.DAYS));
			        //timer.schedule(timerTask, 0, TimeUnit.MILLISECONDS.convert(5, TimeUnit.HOURS));
			        System.out.println("The Navision Pull Pump Rate Master Timer is scheduled .................... ");
				}
			}).start();
		 
    }
}