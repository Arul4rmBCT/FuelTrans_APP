package com.bct.HOS.App.Workflow;

import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.jeasy.flows.engine.WorkFlowEngine;
import org.jeasy.flows.engine.WorkFlowEngineBuilder;
import org.jeasy.flows.work.WorkContext;
import org.jeasy.flows.work.WorkReport;
import org.jeasy.flows.work.WorkStatus;
import org.jeasy.flows.workflow.SequentialFlow;
import org.jeasy.flows.workflow.SequentialFlow.Builder;
import org.jeasy.flows.workflow.WorkFlow;

import com.fasterxml.uuid.Generators;

public class Workflow {

	public Workflow() {
		// TODO Auto-generated constructor stub
	}
	
	private static String getUUID() {
		UUID uuid = Generators.timeBasedGenerator().generate();
		return uuid.toString();
	}
	
	public static void main(String args[]) {
		String uuid=getUUID();
		
		SendEmailWork emailWork = new SendEmailWork("Send Email!..");
		
		//Builder workflowBuilder = executeSequence(inst);
		//Builder wfBuilder = SequentialFlow.Builder.aNewSequentialFlow();
		//wfBuilder.execute(new Start()).named(nodeName).build();
		//wfBuilder.execute(new End()).named(nodeName).build();
		//ExecutorService executorService = Executors.newFixedThreadPool(2);
		//WorkFlow workflow = workflowBuilder.build();
		
		ExecutorService executorService = Executors.newFixedThreadPool(2);
		WorkFlow workflow = SequentialFlow.Builder.aNewSequentialFlow()
		                    .named("Email workflow")
		                    .execute(emailWork)
		                    .build();

		WorkFlowEngine workFlowEngine = WorkFlowEngineBuilder.aNewWorkFlowEngine().build();
		WorkContext workContext = new WorkContext();
		WorkReport workReport = workFlowEngine.run(workflow, workContext);
		WorkStatus ws = workReport.getStatus();
		//System.out.println(ws.toString());
		executorService.shutdown();

	}

}
