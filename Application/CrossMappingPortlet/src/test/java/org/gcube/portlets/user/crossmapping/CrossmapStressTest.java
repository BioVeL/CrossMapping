/*
 * #%L
 * XMap GUI CrossMapping Portlet
 * %%
 * Copyright (C) 2012 - 2013 Cardiff University
 * %%
 * Use of this software is governed by the attached licence file. If no licence 
 * file is present the software must not be used.
 * 
 * The use of this software, including reverse engineering, for any other purpose 
 * is prohibited without the express written permission of the software owners, 
 * Cardiff University and Italy National Research Council.
 * #L%
 */
package org.gcube.portlets.user.crossmapping;

import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.activation.DataHandler;
import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.WebServiceFeature;
import javax.xml.ws.soap.MTOMFeature;


import org.gcube.portlets.user.crossmapping.server.util.InputStreamDataSource;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.openbio.xmap.common.serviceinterfaces.services.xmap.XMapService;
import org.openbio.xmap.common.serviceinterfaces.services.xmap.XMapServiceWS;
import org.openbio.xmap.common.serviceinterfaces.types.xmap.RunXmapTaskResponse;
import org.openbio.xmap.common.serviceinterfaces.types.xmap.IdentifyExtraTaxaType;
import org.openbio.xmap.common.serviceinterfaces.types.xmap.ImportChecklistTaskResponse;
import org.openbio.xmap.common.serviceinterfaces.types.xmap.TaskHandler;
import org.openbio.xmap.common.serviceinterfaces.types.xmap.TaskId;
import org.openbio.xmap.common.serviceinterfaces.types.xmap.TaskProgress;
import org.openbio.xmap.common.serviceinterfaces.types.xmap.TaskResponse;
import org.openbio.xmap.common.serviceinterfaces.types.xmap.TaskStatus;
import org.openbio.xmap.common.serviceinterfaces.types.xmap.TaxonomicRank;
import org.openbio.xmap.common.serviceinterfaces.types.xmap.UserKnowledgeLevelForRefinement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CrossmapStressTest {

	
	/****************************************************************************************/
	/* ATTRIBUTES																			*/														
	/****************************************************************************************

	/**
	 * slf4j logger for the given class.
	 */
	private static Logger logger = LoggerFactory.getLogger(CrossmapStressTest.class);
	
	
	
	/**
	 * Number of seconds we wait between checkProgress operations  
	 */
	private final int checkTimeInterval = 500;
	
	/**
	 * ThreadPool to obtain threads to run the tasks for the tests
	 */
	private static ExecutorService myThreadPool;	
	
	
	
	private static List<XMapService> xMapServices = new ArrayList<XMapService>();
	
	
	
	/****************************************************************************************/
	/* BEFORE AND AFER TEST METHODS															*/	
	/****************************************************************************************/	
	
	/**
	 * Method used to set up all the things needed to be configured before to run the tests
	 * <p>
 	 * This method uses the jUnit annotation "BeforeClass", being only run once before any 
	 * of the tests in this class are executed.
	 * @throws Exception 
	 */
    @BeforeClass
    public static void setUpClass() throws Exception {
    	xMapServices.add(createService("http://localhost:8085/CrossMapping/XMapService?wsdl"));    	
    	//xMapServices.add(createService("http://litchi1.cs.cf.ac.uk:8080/CrossMapping2/XMapService?wsdl"));
    	//xMapServices.add(createService("http://litchi5.cs.cf.ac.uk:8080/CrossMapping2/XMapService?wsdl"));

    	myThreadPool = Executors.newCachedThreadPool();
    }

	/**
	 * Method used to stop the sca Node
	 * The annotation AfterClass causes that this method is only run once 
	 * and after all the tests in the class have been completed 
	 * @throws Exception
	 */	
    @AfterClass 
    public static void destroy() {
    	myThreadPool.shutdownNow();
    }        
    
    
	/****************************************************************************************/
	/* TEST	METHODS																			*/	
	/****************************************************************************************/    
    
    /**
     * Test
     * @throws Exception
     */
    @Test 
    @Ignore
    public void testExperiment() throws Exception {
    	logger.info("Stress test");    	    
    	        	
    	String user = "francisco.quevedo";
		String scope = "/gcube/devsec/devVRE";		
		DateFormat df = new SimpleDateFormat("yyyyddMMHHmmss");
		List<String> dwcaFiles = new ArrayList<String>();
		
		/*
    	dwcaFiles.add("C:\\Projects\\OpenBio\\Demo_OpenBio\\Checklists\\CoL_Download_Service_25th_October_12\\2012-10-25-archive-complete.zip");
    	dwcaFiles.add("C:\\Projects\\OpenBio\\Demo_OpenBio\\Checklists\\CoL_Download_Service_17th_August_12\\2012-08-17-archive-complete.zip");
    	*/

		/*
    	dwcaFiles.add("C:\\Projects\\OpenBio\\Demo_OpenBio\\Checklists\\CoL_Download_Service_20th_November_12\\archive-family-asteraceae-bl3.zip");
    	dwcaFiles.add("C:\\Projects\\OpenBio\\Demo_OpenBio\\Checklists\\CoL_Download_Service_17th_August_12\\archive-family-asteraceae-bl3.zip");

    	dwcaFiles.add("C:\\Projects\\OpenBio\\Demo_OpenBio\\Checklists\\CoL_Download_Service_20th_November_12\\archive-family-fabaceae-bl3.zip");    	
    	dwcaFiles.add("C:\\Projects\\OpenBio\\Demo_OpenBio\\Checklists\\CoL_Download_Service_17th_August_12\\archive-family-fabaceae-bl3.zip");
    	
    	dwcaFiles.add("C:\\Projects\\OpenBio\\Demo_OpenBio\\Checklists\\CoL_Download_Service_20th_November_12\\archive-family-orchidaceae-bl3.zip");
    	dwcaFiles.add("C:\\Projects\\OpenBio\\Demo_OpenBio\\Checklists\\CoL_Download_Service_17th_August_12\\archive-family-orchidaceae-bl3.zip");
    	
    	dwcaFiles.add("C:\\Projects\\OpenBio\\Demo_OpenBio\\Checklists\\CoL_Download_Service_20th_November_12\\archive-family-poaceae-bl3.zip");
    	dwcaFiles.add("C:\\Projects\\OpenBio\\Demo_OpenBio\\Checklists\\CoL_Download_Service_17th_August_12\\archive-family-poaceae-bl3.zip");
    	
    	dwcaFiles.add("C:\\Projects\\OpenBio\\Demo_OpenBio\\Checklists\\CoL_Download_Service_20th_November_12\\archive-family-rosaceae-bl3.zip");
    	dwcaFiles.add("C:\\Projects\\OpenBio\\Demo_OpenBio\\Checklists\\CoL_Download_Service_17th_August_12\\archive-family-rosaceae-bl3.zip");
    	
    	dwcaFiles.add("C:\\Projects\\OpenBio\\Demo_OpenBio\\Checklists\\CoL_Download_Service_20th_November_12\\archive-family-melastomataceae-bl3.zip");
    	dwcaFiles.add("C:\\Projects\\OpenBio\\Demo_OpenBio\\Checklists\\CoL_Download_Service_17th_August_12\\archive-family-melastomataceae-bl3.zip");
    	
    	dwcaFiles.add("C:\\Projects\\OpenBio\\Demo_OpenBio\\Checklists\\CoL_Download_Service_20th_November_12\\archive-family-lamiaceae-bl3.zip");
    	dwcaFiles.add("C:\\Projects\\OpenBio\\Demo_OpenBio\\Checklists\\CoL_Download_Service_17th_August_12\\archive-family-lamiaceae-bl3.zip");
    	
    	dwcaFiles.add("C:\\Projects\\OpenBio\\Demo_OpenBio\\Checklists\\CoL_Download_Service_20th_November_12\\archive-family-malvaceae-bl3.zip");
    	dwcaFiles.add("C:\\Projects\\OpenBio\\Demo_OpenBio\\Checklists\\CoL_Download_Service_17th_August_12\\archive-family-malvaceae-bl3.zip");

    	dwcaFiles.add("C:\\Projects\\OpenBio\\Demo_OpenBio\\Checklists\\CoL_Download_Service_20th_November_12\\archive-family-verbenaceae-bl3.zip");
    	dwcaFiles.add("C:\\Projects\\OpenBio\\Demo_OpenBio\\Checklists\\CoL_Download_Service_17th_August_12\\archive-family-verbenaceae-bl3.zip");

    	dwcaFiles.add("C:\\Projects\\OpenBio\\Demo_OpenBio\\Checklists\\CoL_Download_Service_20th_November_12\\archive-family-apocynaceae-bl3.zip");
    	dwcaFiles.add("C:\\Projects\\OpenBio\\Demo_OpenBio\\Checklists\\CoL_Download_Service_17th_August_12\\archive-family-apocynaceae-bl3.zip");
		*/
		
		
    	dwcaFiles.add("C:\\Projects\\OpenBio\\Demo_OpenBio\\Checklists\\Bignoniaceae_experiment\\Family-Bignoniaceae-from-BrazilianFlora.zip");
		dwcaFiles.add("C:\\Projects\\OpenBio\\Demo_OpenBio\\Checklists\\Bignoniaceae_experiment\\Family-Bignoniaceae-from-CatalogueOfLife-Jan-13.zip");

		
		
    	int numRepetitionsForServer = 1;		
		if (dwcaFiles.size()%2!=0){
			throw new RuntimeException("Number of dwc-a files is not an even number");
		}		
		
		//***************************
		// Importation of checklists
		//***************************
		logger.info("Importation tasks"); 
		List<Callable<TaskHandlerExt>> importTaskList = new ArrayList<Callable<TaskHandlerExt>>();    	
		for (int i=0;i<numRepetitionsForServer;i++){
    		for (int j=0;j<dwcaFiles.size();j++){
    			Date today = Calendar.getInstance().getTime();        		
        		String reportDate = df.format(today);		
        		String chkName = "test_chk_" + (i+1) + "_" + (j+1) + "_" + reportDate; //Util.generatePseudoId()         		
        		String file = dwcaFiles.get(j);  
        		for (int k=0;k<xMapServices.size();k++){
        			importTaskList.add(new ImportChecklistTask (chkName,file,user,scope, k));
        		}
    		}
		}
		
		//Lunch tasks
		List<TaskHandlerExt> importTaskHandlerList = lunchTasks(importTaskList,150);					
			
		//Simulate user checking task status
		List<TaskResult> importTaskResults = checkTasksStatusLoop(importTaskHandlerList,checkTimeInterval);
						
		//Print report:
		printReport(importTaskResults);		    	
		
		//Get the ids of the checklists that haven been imported correctly.
		List<String> checklistImported = new ArrayList<String>();
		for (TaskResult importTaskResult: importTaskResults){
			if (importTaskResult.getTaskInfo().getStatus()!=TaskStatus.COMPLETED){
				throw new RuntimeException("At least on of the importations has failed");
			}
			else{
				checklistImported.add(((ImportChecklistTaskResponse)(importTaskResult.getTaskInfo().getTaskResponse())).getImportedChecklistId());
			}
		}
		
		
		//***************************
		// Run xmaps
		//***************************	
		logger.info("Xmap tasks");
		TaxonomicRank highestRankToCompare = new TaxonomicRank("1", "kingdom", "-1", true);
		//XMap pairs of checklists		
		List<Callable<TaskHandlerExt>> xmapTaskList = new ArrayList<Callable<TaskHandlerExt>>();    
		for (int i=0;i<checklistImported.size();i=i+(xMapServices.size()*2)){			
			for (int j=0; j<xMapServices.size();j++){
				Date today = Calendar.getInstance().getTime();        		
	    		String reportDate = df.format(today);		
	    		String leftChecklistId = checklistImported.get(i+j);
	    		String rightChecklistId = checklistImported.get(i+j+xMapServices.size());
	    		String id = "test_xmap_" + leftChecklistId + "_vs_" + rightChecklistId + "_" + reportDate; //Util.generatePseudoId()  	    		 				    		
	    		
	    		xmapTaskList.add(new XMapTask(id, id, leftChecklistId, rightChecklistId, true, IdentifyExtraTaxaType.GENERIC_TRANSFER, true, highestRankToCompare, UserKnowledgeLevelForRefinement.GLOBAL, user, scope, j));								
			}
				
		}
		
		//Lunch tasks
		List<TaskHandlerExt> xmapTaskHandlerList = lunchTasks(xmapTaskList,150);					
					
		//Simulate user checking task status
		List<TaskResult> xmapTaskResults = checkTasksStatusLoop(xmapTaskHandlerList,checkTimeInterval);		
				
		//Print report:
		printReport(xmapTaskResults);	
		
		
		//Get the ids of the xmaps that haven been executed correctly.
		List<String> xmapExecuted = new ArrayList<String>();
		for (TaskResult xmapTaskResult: xmapTaskResults){
			if (xmapTaskResult.getTaskInfo().getStatus()!=TaskStatus.COMPLETED){
				throw new RuntimeException("At least on of the xmaps has failed");
			}
			else{
				xmapExecuted.add(((RunXmapTaskResponse)(xmapTaskResult.getTaskInfo().getTaskResponse())).getXMapId());
			}
		}		
				
		//***************************
		// Export results
		//***************************	
		logger.info("Export tasks"); 
		//Export results xmap
		List<Callable<TaskHandlerExt>> exportTaskList = new ArrayList<Callable<TaskHandlerExt>>();    
		for (int i=0;i<xmapExecuted.size();i++){
			Date today = Calendar.getInstance().getTime();        		
    		String reportDate = df.format(today);		
    		String xmapId = xmapExecuted.get(i);  
    		
    		int xMapServIndex = i%xMapServices.size();   
    		
    		exportTaskList.add(new ExportResultsTask(xmapId, true, user, scope, xMapServIndex));
		}
		
		//Lunch tasks
		List<TaskHandlerExt> exportTaskHandlerList = lunchTasks(exportTaskList,150);					
					
		//Simulate user checking task status
		List<TaskResult> exportTaskResults = checkTasksStatusLoop(exportTaskHandlerList,checkTimeInterval);		
				
		//Print report:
		printReport(exportTaskResults);					 
    }
   
    @Test
    @Ignore
    public void test2() throws Exception{
    	String user = "francisco.quevedo";
		String scope = "/gcube/devsec/devVRE";		
		DateFormat df = new SimpleDateFormat("yyyyddMMHHmmss");    	
    	
    	List<String> checklistImported = new ArrayList<String>();
    	checklistImported.addAll(Arrays.asList("115","116"));
    	    	
				
    	
		//***************************
		// Run xmaps
		//***************************	
		logger.info("Xmap tasks");
		TaxonomicRank highestRankToCompare = new TaxonomicRank("1", "kingdom", "-1", true);
		//XMap pairs of checklists		
		List<Callable<TaskHandlerExt>> xmapTaskList = new ArrayList<Callable<TaskHandlerExt>>();    
		for (int i=0;i<checklistImported.size();i=i+(xMapServices.size()*2)){			
			for (int j=0; j<xMapServices.size();j++){
				Date today = Calendar.getInstance().getTime();        		
	    		String reportDate = df.format(today);		
	    		String leftChecklistId = checklistImported.get(i+j);
	    		String rightChecklistId = checklistImported.get(i+j+xMapServices.size());
	    		String id = "test_xmap_" + leftChecklistId + "_vs_" + rightChecklistId + "_" + reportDate; //Util.generatePseudoId()  	    		 				    		
	    		
	    		xmapTaskList.add(new XMapTask(id, id, leftChecklistId, rightChecklistId, true, IdentifyExtraTaxaType.GENERIC_TRANSFER, true, highestRankToCompare, UserKnowledgeLevelForRefinement.GLOBAL, user, scope, j));								
			}
				
		}
		
		//Lunch tasks
		List<TaskHandlerExt> xmapTaskHandlerList = lunchTasks(xmapTaskList,150);					
					
		//Simulate user checking task status
		List<TaskResult> xmapTaskResults = checkTasksStatusLoop(xmapTaskHandlerList,checkTimeInterval);		
				
		//Print report:
		printReport(xmapTaskResults);	
		
		
		//Get the ids of the xmaps that haven been executed correctly.
		List<String> xmapExecuted = new ArrayList<String>();
		for (TaskResult xmapTaskResult: xmapTaskResults){
			if (xmapTaskResult.getTaskInfo().getStatus()!=TaskStatus.COMPLETED){
				throw new RuntimeException("At least on of the xmaps has failed");
			}
			else{
				xmapExecuted.add(((RunXmapTaskResponse)(xmapTaskResult.getTaskInfo().getTaskResponse())).getXMapId());
			}
		}		
		
		//***************************
		// Export results
		//***************************	
		logger.info("Export tasks"); 
		//Export results xmap
		List<Callable<TaskHandlerExt>> exportTaskList = new ArrayList<Callable<TaskHandlerExt>>();    
		for (int i=0;i<xmapExecuted.size();i++){
			Date today = Calendar.getInstance().getTime();        		
    		String reportDate = df.format(today);		
    		String xmapId = xmapExecuted.get(i);  
    		
    		int xMapServIndex = i%xMapServices.size();   
    		
    		exportTaskList.add(new ExportResultsTask(xmapId, true, user, scope, xMapServIndex));
		}
		
		//Lunch tasks
		List<TaskHandlerExt> exportTaskHandlerList = lunchTasks(exportTaskList);					
					
		//Simulate user checking task status
		List<TaskResult> exportTaskResults = checkTasksStatusLoop(exportTaskHandlerList,checkTimeInterval);		
				
		//Print report:
		printReport(exportTaskResults);		    	
    }
    
    
	/****************************************************************************************/
	/* PRIVATE METHODS																		*/	
	/****************************************************************************************/	
	
	private static XMapService createService(String endPointWSDL) throws Exception {
		XMapServiceWS xMapWSFactory = new XMapServiceWS(new URL(endPointWSDL), new QName("http://xmap.openbio.org/common/serviceinterfaces/services/xmap", "XMap_XMapService"));
		MTOMFeature mtom = new MTOMFeature();
		XMapService xMapWS = (XMapService)xMapWSFactory.getPort(new QName("http://xmap.openbio.org/common/serviceinterfaces/services/xmap", "xmapWSSOAP11Port"), XMapService.class, new WebServiceFeature[] { mtom });

		BindingProvider bp = (BindingProvider)xMapWS;
		bp.getRequestContext().put("javax.xml.ws.service.endpoint.address", endPointWSDL);
		return xMapWS;
	}	
	
	
	/**
	 * Method responsible to run in different threads the list of tasks it receives as parameter simultaneously
	 * @param taskList list of tasks to be lunched
	 * @return List of taskHandler returned after lunch the task. These taskHandlers will be use to control
	 * the state of the tasks
	 * @throws Exception
	 */	
	private List<TaskHandlerExt> lunchTasks(List<Callable<TaskHandlerExt>> taskList) throws Exception{
		return lunchTasks(taskList,0);
	}
	
	/**
	 * Method responsible to run in different threads the list of tasks it receives as parameter but waiting x number of mili second between
	 * lunching each task
	 * @param taskList list of tasks to be lunched
	 * @return List of taskHandler returned after lunch the task. These taskHandlers will be use to control
	 * the state of the tasks
	 * @throws Exception
	 */		
	private List<TaskHandlerExt> lunchTasks(List<Callable<TaskHandlerExt>> taskList, int numMiliSecondBetweenLunches) throws Exception{
		List<TaskHandlerExt> taskHandlerList = new ArrayList<TaskHandlerExt>();

		for (int i=0; i<taskList.size(); i++){																	 			
			TaskHandlerExt taskHandler = lunchTask(taskList.get(i));
			taskHandlerList.add(taskHandler);
			
			if (numMiliSecondBetweenLunches>0){
				Thread.sleep(numMiliSecondBetweenLunches);
			}										
		}	
		
		return taskHandlerList;
	}
	
	/**
	 * Method responsible to run in one thread the task it receives as parameter
	 * @param task task to be lunched
	 * @return TaskHandler to control the state of the tasks
	 * @throws Exception
	 */		
	private TaskHandlerExt lunchTask (Callable<TaskHandlerExt> task) throws Exception{
		Future<TaskHandlerExt> futureResult  = myThreadPool.submit(task);	
		TaskHandlerExt taskHandlerExt = futureResult.get();
		
		logger.debug("reqId obtained=" + taskHandlerExt.getTaskHandler().getTaskId().getValue());
		
		Assert.assertNotNull(taskHandlerExt);
		
		return taskHandlerExt;
	}		
		
	/**
	 * Method responsible to lunch the cancellation of a certain number of task from the tasks it receives as parameter, waiting x number
	 * of second between cancellations
	 * @param taskHandlerList list with the taskHandler of tasks to cancel 
	 * @param numTaskToCancel number of tasks from the list to cancel 
	 * @param numMiliSecondBetweenCancels number of mili second to wait between cancellations
	 * @throws Exception
	 */	
	private void cancelTasks(List<TaskHandlerExt> taskHandlerExtList, int numTaskToCancel, int numMiliSecondBetweenCancels) throws Exception{		
		for (int i=0; i<numTaskToCancel; i++){
			Thread.sleep(numMiliSecondBetweenCancels);
			cancelTask(taskHandlerExtList.get(i));
			logger.debug("canceling request reqId= " + taskHandlerExtList.get(0).getTaskHandler().getTaskId().getValue());			
		}		
	}
	
	/**
	 * Method to lunch the cancellation of the task which taskHander is received as parameter
	 * @param taskHandler taskHandler of the task to cancel
	 * @throws Exception
	 */	
	private void cancelTask (TaskHandlerExt taskHandlerExt) throws Exception{
		myThreadPool.submit(new CancelTask(taskHandlerExt));
	}		
	
	
	/**
	 * Method responsible to check the status of the task which taskHandlers receives as parameter waiting x number of second
	 * between checks
	 * @param taskHandlerList list of task handlers of the task to check its status
	 * @param numMiliSecondsWaitBetweenCheck number of seconds to wait between checks
	 * @return List with the r
	 * @throws Exception
	 */	
	private List<TaskResult> checkTasksStatusLoop(List<TaskHandlerExt> taskHandlerExtList, int numMiliSecondsWaitBetweenCheck) throws Exception{
		return checkTasksStatusLoop(taskHandlerExtList,-1,numMiliSecondsWaitBetweenCheck);
	}	
	
	/**
	 * Method responsible to check the status of every task in taskHandlerList every numMiliSecondsWaitBetweenCheck miliseconds 
	 * in a loop until all the tasks have been completed (successfully or with error) or the time-out has expired
	 * Note: If numSecondsExecutionTimeOut is negative we will carry on checking the progress of every task until all
	 * of the finish (independently of the state completed or aborted) 
	 * @param taskHandlerList list with taskHandlers of the tasks that we want to check its status
	 * @param numSecondsExecutionTimeFrame time-out (number of seconds waiting for the tasks to complete) 
	 * @param numMiliSecondBetweenChecks number of second between checks
	 * @return List with the status of each task
	 * @throws Exception 
	 */
	private List<TaskResult> checkTasksStatusLoop(List<TaskHandlerExt> taskHandlerExtList, int numSecondsExecutionTimeOut, int numMiliSecondBetweenChecks) throws Exception{
		long currentTime = System.currentTimeMillis();
		long limitEndTime;
		
		if (numSecondsExecutionTimeOut>0){
			limitEndTime = currentTime + (numSecondsExecutionTimeOut * 1000);
		}
		else{
			limitEndTime = Long.MAX_VALUE;
		}	
		
		List<TaskResult> listTaskResults = new ArrayList<TaskResult>();
		for (TaskHandlerExt taskHandlerExt: taskHandlerExtList){			
			listTaskResults.add(new TaskResult(taskHandlerExt.getTaskHandler().getTaskId()));
		}
							
		//Simulate users checking progress of their operations
		boolean done = false;
		while (!done && currentTime<limitEndTime) {				
			Iterator<TaskHandlerExt> iterator = taskHandlerExtList.iterator();
			while (iterator.hasNext()) {
				TaskResult taskResult = checkTaskStatus(iterator.next());															
				updateListTaskResult(listTaskResults,taskResult);
				
				if (taskResult.getTaskInfo().getStatus()==TaskStatus.COMPLETED || taskResult.getTaskInfo().getStatus()==TaskStatus.FAILED){
					iterator.remove();
				}
			}	
			
			if (taskHandlerExtList.size()>0){
				Thread.sleep(numMiliSecondBetweenChecks);
				currentTime = System.currentTimeMillis();	
			}
			else{
				done = true;
			}							
		}		
		
		if (taskHandlerExtList.size()==0){
			logger.info("all operations completed");
		}
		else{
			logger.info("some operations haven't finished in the time definied");
		}		
		
		return listTaskResults;
	}
		
	/**
	 * Method responsible to check the status of the task which taskHandler is received as parameter 
	 * in a loop waiting till it's completed
	 * @param taskHandler taskHandler of the task to check its status
	 * @param numMiliSecondsWaitBetweenCheck number of milisecond between checks
	 * @return status of the tasks
	 * @throws Exception
	 */	
	private TaskResult checkTaskStatusLoop(TaskHandlerExt taskHandlerExt, int numMiliSecondsWaitBetweenCheck) throws Exception{
		return checkTaskStatusLoop(taskHandlerExt,-1,numMiliSecondsWaitBetweenCheck);
	}
	
	/**
	 * Method responsible to check the status of the task which taskHandler is received as parameter 
	 * in a loop waiting till it's completed or its time-out expired
	 * @param taskHandler  taskHandler of the task to check its status
	 * @param numSecondsExecutionTimeOut number of seconds between checks
	 * @param numMiliSecondBetweenChecks  number of seconds waiting for the task to complete
	 * @return result of the task
	 * @throws Exception
	 */	
	private TaskResult checkTaskStatusLoop(TaskHandlerExt taskHandlerExt, int numSecondsExecutionTimeOut, int numMiliSecondBetweenChecks) throws Exception{
		long currentTime = System.currentTimeMillis();
		long limitEndTime;
		
		if (numSecondsExecutionTimeOut>0){
			limitEndTime = currentTime + (numSecondsExecutionTimeOut * 1000);
		}
		else{
			limitEndTime = Long.MAX_VALUE;
		}	
		
		TaskResult taskResult = new TaskResult(taskHandlerExt.getTaskHandler().getTaskId());
							
		//Simulate user checking progress of his/her operation
		boolean done=false;
		while (!done && currentTime<limitEndTime) {				
			taskResult = checkTaskStatus(taskHandlerExt);			
			if (taskResult.getTaskInfo().getStatus()==TaskStatus.COMPLETED || taskResult.getTaskInfo().getStatus()==TaskStatus.FAILED){
				done = true;
			}
			else{
				Thread.sleep(numMiliSecondBetweenChecks);
				currentTime = System.currentTimeMillis();				
			}			
		}		
		
		if (done){
			logger.info("operation completed");
		}
		else{
			logger.info("operations hasn't finished in the time definied");
		}		
		
		return taskResult;		
	}
	
	/**
	 * Method to check the status of the task which taskHandler is received as a parameter
	 * @param taskHandler
	 * @return status of the task
	 * @throws Exception
	 */	
	private TaskResult checkTaskStatus(TaskHandlerExt taskHandlerExt) throws Exception{
		TaskId taskId = taskHandlerExt.getTaskHandler().getTaskId();
		Future<TaskProgress> futureResult  = myThreadPool.submit(new CheckTaskProgress(taskHandlerExt));
		TaskProgress taskProgress = futureResult.get();
				
		logger.trace("checking progress reqId= " + taskId.getValue() 
				+  " info" + getPrintableProgressInfo(taskProgress,1));
		
		return new TaskResult(taskId,taskProgress);		
	}	
	
	/**
	 * Method to update the task result inside the liste with its new value
	 * @param listTaskResults list of task results
	 * @param newTaskResult new result of the task
	 */	
	private void updateListTaskResult(List<TaskResult> listTaskResults, TaskResult newTaskResult){
		boolean found = false;
		ListIterator <TaskResult> listIterator = listTaskResults.listIterator();
		while (listIterator.hasNext() && !found) {
			TaskResult taskResult = listIterator.next();
			if (taskResult.taskId.getValue().equals(newTaskResult.getTaskId().getValue())){	
				taskResult.setTaskInfo(newTaskResult.getTaskInfo());
				found = true;
			}
		}
	}	
	
	/**
	 * Method responsible to print a report with list of taskResult received as a parameter
	 * @param taskResults list of task results to print its report
	 */
	private void printReport(List<TaskResult> taskResults){
		logger.info("");
		logger.info("****************************************************");
		logger.info("Report task  execution:");
		logger.info("****************************************************");
		for (TaskResult taskResult: taskResults){
			TaskId reqId = taskResult.getTaskId();
			TaskStatus finalStatus = taskResult.getTaskInfo().getStatus();
			TaskResponse taskResponse = taskResult.getTaskInfo().getTaskResponse();
			logger.info("-->ReqId: " + reqId.getValue()
					+ " Final status: " + finalStatus
					+ " Task response: " + taskResponse);	
		}
	}
		
	/**
	 * Method that return the string with the information to print of taskProgress
	 * @param info
	 * @param level
	 * @return string with the progress information 
	 */	
	private String getPrintableProgressInfo(TaskProgress info, int level){
		StringBuilder sb = new StringBuilder();
		sb.append("\n");
		sb.append(getIndentation(level) + "Task name: " + (info.getTaskName()!=null? info.getTaskName() : ""));
		sb.append("\n");
		sb.append(getIndentation(level) + "Task status: " + (info.getStatus()!=null? info.getStatus() : ""));
		sb.append("\n");
		sb.append(getIndentation(level) + "Task details: " + (info.getDetails()!=null? info.getDetails() : ""));
		sb.append("\n");
		sb.append(getIndentation(level) + "Task percentage: " + info.getPercentage());
		sb.append("\n");
		sb.append(getIndentation(level) + "Task response: " + (info.getTaskResponse()!=null? info.getTaskResponse() : ""));
		sb.append("\n");
		
		if (info.getSubTasksProgress() != null){
			for (int i=0;i<info.getSubTasksProgress().size();i++){
				sb.append(getIndentation(level) + getPrintableProgressInfo(info.getSubTasksProgress().get(i),level + 1));
				sb.append("\n");
			}
		}	
		
		return sb.toString();
	}
	
	/**
	 * Method that return a string to create indentation in the report 
	 * @param level
	 * @return string with the inditation for the level received
	 */	
	private String getIndentation(int level){
		StringBuilder sb = new StringBuilder();
		String indentString = "--->";
		for (int i=0; i<level; i++){
			sb.append(indentString);
		}
		return sb.toString();
	}
	
	
	
	/********************************************************************************************************************/
	/*	PRIVATE CLASSES																									*/
	/********************************************************************************************************************/
	
	/**
	 * Class to execute in a thread the ImportChecklistTask and return its taskHandler 
	 * @author Fran
	 */	
	private class ImportChecklistTask implements Callable<TaskHandlerExt> {
		private String checklistName; 
		private String file; 
		private String user; 
		private String scope;
		
		private int xMapServIndex;
	
		/**
		 * Constructor of ImportChecklistTask
		 */			
		public ImportChecklistTask(String checklistName, String file, String user, String scope, int xMapServIndex){
			this.checklistName = checklistName;
			this.file = file;
			this.user = user;
			this.scope = scope;
			
			this.xMapServIndex = xMapServIndex;
		}
		
		/**
		 * Method to execute in the thread and will call upload the file and start the import returning its taskHandler
		 * @return taskHandler of the task lunched
		 */		
		public TaskHandlerExt call() throws Exception{		
			try{
				InputStream is = new FileInputStream(file);
				String fileNameInServer = checklistName + ".zip";
					
				long startTime = System.currentTimeMillis();
				DataHandler binaryData = new DataHandler(new InputStreamDataSource(is));
				xMapServices.get(xMapServIndex).uploadChecklistFile(fileNameInServer,binaryData,user,scope);
				is.close();
				
				long elapsedTime = System.currentTimeMillis() - startTime;	
				System.out.println("Upload file " + file + " elapsedTime " + elapsedTime);
	
				TaskHandler taskHandler = xMapServices.get(xMapServIndex).importChecklist(fileNameInServer,"DwC-CoL",checklistName,user,scope);    											
				return new TaskHandlerExt(xMapServIndex, taskHandler);
			}
			catch(Exception ex){
				System.err.println(ex.getMessage());
				ex.printStackTrace();
				throw ex;
			}
		}
		
	}	
	
	
	/**
	 * Class to execute in a thread the XMapTask and return its taskHandler 
	 * @author Fran
	 */	
	private class XMapTask implements Callable<TaskHandlerExt> {
		private String xmapName; 
		private String description; 
		private String leftChecklistId; 
		private String rightChecklistId; 
		private boolean strict; 
		private IdentifyExtraTaxaType identifyExtraTaxa; 
		private boolean compareHigherTaxa; 
		private TaxonomicRank highestTaxonomicRankToCompare;
		private UserKnowledgeLevelForRefinement userRefinementLevel;
		private String user; 
		private String scope;
	
		private int xMapServIndex;
		
		/**
		 * Constructor of ImportChecklistTask
		 */			
		public XMapTask(String xmapName, String description, String leftChecklistId, String rightChecklistId,
				boolean strict, IdentifyExtraTaxaType identifyExtraTaxa, boolean compareHigherTaxa, 
				TaxonomicRank highestTaxonomicRankToCompare, UserKnowledgeLevelForRefinement userRefinementLevel, 
				String user, String scope, int xMapServIndex) {
			super();
			this.xmapName = xmapName;
			this.description = description;
			this.leftChecklistId = leftChecklistId;
			this.rightChecklistId = rightChecklistId;
			this.strict = strict;
			this.identifyExtraTaxa = identifyExtraTaxa;
			this.compareHigherTaxa = compareHigherTaxa;
			this.highestTaxonomicRankToCompare = highestTaxonomicRankToCompare;
			this.userRefinementLevel = userRefinementLevel;
			this.user = user;
			this.scope = scope;
			
			this.xMapServIndex = xMapServIndex;
		}		
		
		/**
		 * Method to execute in the thread and will call upload the file and start the import returning its taskHandler
		 * @return taskHandler of the task lunched
		 */		
		public TaskHandlerExt call() throws Exception{									
			TaskHandler taskHandler = xMapServices.get(xMapServIndex).runXmap(xmapName, description, leftChecklistId, rightChecklistId, strict, identifyExtraTaxa, compareHigherTaxa, highestTaxonomicRankToCompare, userRefinementLevel, user, scope);    			
									
			return new TaskHandlerExt(xMapServIndex, taskHandler);
		}
		
	}
	
	
	/**
	 * Class to execute in a thread the ImportChecklistTask and return its taskHandler 
	 * @author Fran
	 */	
	private class ExportResultsTask implements Callable<TaskHandlerExt> {
		private String xMapId; 
		private boolean includeAcceptedName; 
		private String user; 
		private String scope;
	
		private int xMapServIndex;
		
		/**
		 * Constructor of ImportChecklistTask
		 */			
		public ExportResultsTask(String xMapId, boolean includeAcceptedName, String user, String scope, int xMapServIndex){
			this.xMapId = xMapId;
			this.includeAcceptedName = includeAcceptedName;
			this.user = user;
			this.scope = scope;
			this.xMapServIndex = xMapServIndex;
		}
		
		/**
		 * Method to execute in the thread and will call upload the file and start the import returning its taskHandler
		 * @return taskHandler of the task lunched
		 */		
		public TaskHandlerExt call() throws Exception{	
			TaskHandler taskHandler = xMapServices.get(xMapServIndex).exportResultXmap(xMapId, includeAcceptedName, user, scope); 			
									
			return new TaskHandlerExt(xMapServIndex, taskHandler);
		}		
	}		
		
	
	
	/**
	 * Class to execute in a thread the CheckTaskProgress and it will return the TaskProgress
	 * @author Fran
	 */	
	private class CheckTaskProgress implements Callable<TaskProgress> {
		private TaskHandlerExt taskHandlerExt;
		
		/**
		 * Constructor of CheckTaskProgress
		 * @param taskId
		 */		
		public CheckTaskProgress(TaskHandlerExt taskHandlerExt){
			this.taskHandlerExt = taskHandlerExt;		
		}
		
		/**
		 * Method to obtain the taskProgress of the task 
		 */		
		public TaskProgress call() throws Exception{	
			TaskProgress taskProgress = null;
												
			taskProgress = xMapServices.get(taskHandlerExt.getxMapServIndex()).getTaskProgress(taskHandlerExt.getTaskHandler().getTaskId(), taskHandlerExt.getTaskHandler().getUser(), taskHandlerExt.getTaskHandler().getScope());
						
			return taskProgress;
		}		
	}		
	
	
	/**
	 * Class to execute in a thread the CancelTask
	 * @author Fran
	 */	
	private class CancelTask implements Runnable{
		private TaskHandlerExt taskHandlerExt;
		
		/**
		 * Constructor of CancelTask
		 * @param taskId
		 */		
		public CancelTask(TaskHandlerExt taskHandlerExt){
			this.taskHandlerExt = taskHandlerExt;
		}
		
		/**
		 * Method to execute the cancellation of the task
		 */		
		public void run(){	
			try{
				xMapServices.get(taskHandlerExt.getxMapServIndex()).cancelTask(taskHandlerExt.getTaskHandler().getTaskId(), taskHandlerExt.getTaskHandler().getUser(), taskHandlerExt.getTaskHandler().getScope());						
			}
			catch(Exception ex){
				System.err.println(ex.getMessage());
				ex.printStackTrace();
			}						
		}		
	}		
	
	/**
	 * Class to have the structure with the attributes of a taskResult
	 * @author Fran
	 */	
	private class TaskResult{
		private TaskId taskId;
		private TaskProgress taskInfo;
		
		public TaskId getTaskId() {
			return taskId;
		}
		public void setTaskId(TaskId taskId) {
			this.taskId = taskId;
		}
		public TaskProgress getTaskInfo() {
			return taskInfo;
		}
		public void setTaskInfo(TaskProgress taskInfo) {
			this.taskInfo = taskInfo;
		}
		
		public TaskResult(TaskId taskId, TaskProgress taskInfo) {
			super();
			this.taskId = taskId;
			this.taskInfo = taskInfo;
		}
		
		public TaskResult(TaskId taskId) {
			super();
			this.taskId = taskId;
			
			this.taskInfo = new TaskProgress();
			this.taskInfo.setStatus(TaskStatus.ACTIVE);
			this.taskInfo.setDetails("Executing");
			this.taskInfo.setPercentage(0);				
		}		
	}	
    	
	
	private class TaskHandlerExt {
		private int xMapServIndex;
		private TaskHandler taskHandler;

		public int getxMapServIndex() {
			return xMapServIndex;
		}

		public void setxMapServIndex(int xMapServIndex) {
			this.xMapServIndex = xMapServIndex;
		}
		
		public TaskHandler getTaskHandler() {
			return taskHandler;
		}

		public void setTaskHandler(TaskHandler taskHandler) {
			this.taskHandler = taskHandler;
		}

		public TaskHandlerExt(int xMapServIndex, TaskHandler taskHandler) {
			super();
			this.xMapServIndex = xMapServIndex;
			this.taskHandler = taskHandler;
		}		
		
		
	}
	
}
