/*
 * #%L
 * XMap Business Logic
 * %%
 * Copyright (C) 2012 - 2013 Cardiff University
 * %%
 * Use of this software is governed by the attached licence file. If no licence 
 * file is present the software must not be used.
 * 
 * The use of this software, including reverse engineering, for any other purpose 
 * is prohibited without the express written permission of the software owner, 
 * Cardiff University.
 * #L%
 */
package org.openbio.xmap.core.xmap;

import static org.junit.Assert.*;
import static org.junit.Assume.assumeNoException;
import static org.junit.Assume.assumeNotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;

import org.apache.commons.dbcp.BasicDataSource;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.openbio.xmap.common.serviceinterfaces.services.xmap.XMapService;
import org.openbio.xmap.common.serviceinterfaces.types.xmap.RunXmapTaskResponse;
import org.openbio.xmap.common.serviceinterfaces.types.xmap.ExportXmapTaskResponse;
import org.openbio.xmap.common.serviceinterfaces.types.xmap.IdentifyExtraTaxaType;
import org.openbio.xmap.common.serviceinterfaces.types.xmap.ImportChecklistTaskResponse;
import org.openbio.xmap.common.serviceinterfaces.types.xmap.TaskHandler;
import org.openbio.xmap.common.serviceinterfaces.types.xmap.TaskId;
import org.openbio.xmap.common.serviceinterfaces.types.xmap.TaskProgress;
import org.openbio.xmap.common.serviceinterfaces.types.xmap.TaskResponse;
import org.openbio.xmap.common.serviceinterfaces.types.xmap.TaskStatus;
import org.openbio.xmap.common.serviceinterfaces.types.xmap.TaxonomicRank;
import org.openbio.xmap.common.serviceinterfaces.types.xmap.UserKnowledgeLevelForRefinement;
import org.openbio.xmap.common.utils.misc.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class to test the import and export data functionality provided by the TreeStore works as expected 
 * <p>
 * OpenBio XMap  
 * <p>
 * Date: 12/05/12
 * @author Fran
 */
public class XMapImplTest {
	
	private static XMapService xMap;
	
	 /** number of seconds for the time-out of waiting for the completion of long running tasks  
	 */
	private final int maxElapsedTime = 120;
	
	/**
	 * number of seconds to check the state of a long running task
	 */
	private final int checkTimeInterval = 5;
	
	/**
	 * slf4j logger for the given class
	 */	
	private static Logger logger = LoggerFactory.getLogger(XMapImplTest.class);	
			 
	
	/**
	 * Method used to set up all the things needed to be configured before to run the tests
	 * <p>
 	 * This method uses the jUnit annotation "BeforeClass", being only run once before any 
	 * of the tests in this class are executed.
	 */
    @BeforeClass
    public static void setUpClass() {
		//Create the DataSource and bind it to the initial context
        try { 
        	//Obtain the xmap test properties to be used during jUnit tests        	
        	Properties prop = getXmapTestProperties();
        	
        	//Obtain the jdbcUrl of the xmap database to use during the tests
            String jdbcUrl = prop.getProperty("test.database.xmap.jdbc");
            assumeNotNull(jdbcUrl);
        	
        	//Create jndi datasource 
            String jndiName = "jdbc/XMapDB";
            InitialContext ic = null;
        	if (ic==null){
    			// Create initial context
    			System.setProperty(Context.INITIAL_CONTEXT_FACTORY, "org.apache.naming.java.javaURLContextFactory");
    			System.setProperty(Context.URL_PKG_PREFIXES, "org.apache.naming");
    			ic = new InitialContext();
    			ic.createSubcontext("java:");
    			ic.createSubcontext("java:/comp");
    			ic.createSubcontext("java:/comp/env");
    			ic.createSubcontext("java:/comp/env/jdbc");
    			
    			BasicDataSource ds = new BasicDataSource();
    			ds.setDriverClassName("com.mysql.jdbc.Driver");
    			ds.setUrl(jdbcUrl);			
    			ic.bind("java:/comp/env/" + jndiName, ds);    			
        	}            
        	
        	String exportFolder = createTempFolder("uploadChecklists");    	            	
        	String uploadFolder = createTempFolder("xmapResults");
        	
        	xMap = new XMapImpl(jndiName, exportFolder,uploadFolder);        	        	       	        	        	       
        }
        catch (Exception e) {
            assumeNoException(e);
        }
    }
    
    
    /**
     * Test the xMap
     * @throws Exception
     */
    @Test
    @Ignore
    public void testXMap() throws Exception {   	
    	String user = "francisco.quevedo";
    	String scope = "devsec";
    	
    	//Import checklistA
    	String pathDwcaChecklistA = this.getClass().getResource("/dwca/col-order-primates-bl3.zip").getFile();
    	String checklistA = "Primates";
    	TaskHandler taskHandlerImportChecklistA = xMap.importChecklist(pathDwcaChecklistA, "DwC-CoL", checklistA,user,scope);
    	
    
    	//Simulate user checking import task status until it finishes
    	TaskResult taskResultImporChecklistA = checkTaskStatusLoop(taskHandlerImportChecklistA,checkTimeInterval);	
    	
		//Process task result
		if (taskResultImporChecklistA.getTaskInfo().getStatus() != TaskStatus.COMPLETED) {
			logger.info("Importation of checklistA has finished incorrectly");
			fail("Task has finished incorrectly");
		}
		else{				
			//Import checklistA	
	    	String pathDwcaChecklistB = this.getClass().getResource("/dwca/col-family-hominidae-bl3.zip").getFile();
	    	String checklistB = "Hominidae";
	    	TaskHandler taskHandlerImportChecklistB = xMap.importChecklist(pathDwcaChecklistB, "DwC-CoL", checklistB, user,scope);
	    	
	    	//Simulate user checking import task status until it finishes
	    	TaskResult taskResultImporChecklistB = checkTaskStatusLoop(taskHandlerImportChecklistB,checkTimeInterval);		   
	    	
			//Process task result
			if (taskResultImporChecklistB.getTaskInfo().getStatus() != TaskStatus.COMPLETED) {
				logger.info("Importation of checklistB has finished incorrectly");
				fail("Task has finished incorrectly");
			}
			else{		    		    	
				//Do the xMap between the 2 imported checklist 
				String xMapName = "PrimatesvsHominidae";
				ImportChecklistTaskResponse importChecklistATaskResponse = (ImportChecklistTaskResponse) taskResultImporChecklistA.getTaskInfo().getTaskResponse();
				ImportChecklistTaskResponse importChecklistBTaskResponse = (ImportChecklistTaskResponse) taskResultImporChecklistB.getTaskInfo().getTaskResponse();
				
				TaskHandler taskHandlerDoXmap = xMap.runXmap(xMapName,"PrimatesvsHominidae", importChecklistATaskResponse.getImportedChecklistId(), 
						importChecklistBTaskResponse.getImportedChecklistId(), true, IdentifyExtraTaxaType.NONE, true, new TaxonomicRank("1","kingdom",null,true),
						UserKnowledgeLevelForRefinement.NONE,user,scope);
	    	
		    	//Simulate user checking doXMap task status until it finishes
		    	TaskResult taskResultDoXmap = checkTaskStatusLoop(taskHandlerDoXmap,checkTimeInterval);	
		    
				//Process task result
				if (taskResultDoXmap.getTaskInfo().getStatus() != TaskStatus.COMPLETED) {
					logger.info("Crossmap has finished incorrectly");
					fail("Task has finished incorrectly");
				}
				else{			
					//Export xMap result
					RunXmapTaskResponse runXMapTaskResponse = (RunXmapTaskResponse)(taskResultDoXmap.getTaskInfo().getTaskResponse());
					TaskHandler taskHandlerExportXmapResult = xMap.exportResultXmap(runXMapTaskResponse.getXMapId(),true,user,scope);
			    	
			    	//Simulate user checking export task status until it finishes
			    	TaskResult taskResultExportXmapResult = checkTaskStatusLoop(taskHandlerExportXmapResult,checkTimeInterval);	
			    
					//Process task result
					if (taskResultExportXmapResult.getTaskInfo().getStatus() != TaskStatus.COMPLETED) {
						logger.info("Exportation of crossmaping results has finished incorrectly");
						fail("Task has finished incorrectly");
					}
					else{	
						ExportXmapTaskResponse exportXmapTaskResponse = (ExportXmapTaskResponse)taskResultExportXmapResult.getTaskInfo().getTaskResponse();
						String resultFile = exportXmapTaskResponse.getXmapResultFile();
						assertNull(resultFile);												
					}
				}		    	
			}				
		}    	    	
    }        
        
    
	/****************************************************************************************/
	/* PRIVATE METHODS																		*/	
	/****************************************************************************************/
      
    /**
	 * Method responsible to check the status of the task which taskHandlers receives as parameter waiting x number of second
	 * between checks
	 * @param taskHandlerList list of task handlers of the task to check its status
	 * @param numSecondsWaitBetweenCheck number of seconds to wait between checks
	 * @return List with the result of the tasks once executed
	 * @throws Exception
	 */
	private List<TaskResult> checkTasksStatusLoop(List<TaskHandler> taskHandlerList, int numSecondsWaitBetweenCheck) throws Exception{
		return checkTasksStatusLoop(taskHandlerList,-1,numSecondsWaitBetweenCheck);
	}	
	
	/**
	 * Method responsible to check the status of every task in taskHandlerList every numSecondsWaitBetweenCheck seconds 
	 * in a loop until all the tasks have been completed (successfully or with error) or the time-out has expired
	 * Note: If numSecondsExecutionTimeOut is negative we will carry on cheching the progress of every task until all
	 * of the finish (independetly of the state completed or aborted) 
	 * @param taskHandlerList list with taskHandlers of the tasks that we want to check its status
	 * @param numSecondsExecutionTimeFrame time-out (number of seconds waiting for the tasks to complete) 
	 * @param numSecondsWaitBetweenCheck number of second between checks
	 * @return List with the result of the tasks once executed
	 * @throws Exception
	 */
	private List<TaskResult> checkTasksStatusLoop(List<TaskHandler> taskHandlerList, int numSecondsExecutionTimeOut, int numSecondsWaitBetweenCheck) throws Exception{
		long currentTime = System.currentTimeMillis();
		long limitEndTime;
		
		if (numSecondsExecutionTimeOut>0){
			limitEndTime = currentTime + (numSecondsExecutionTimeOut * 1000);
		}
		else{
			limitEndTime = Long.MAX_VALUE;
		}	
		
		List<TaskResult> listTaskResults = new ArrayList<TaskResult>();
		for (TaskHandler taskHandler: taskHandlerList){			
			listTaskResults.add(new TaskResult(taskHandler.getTaskId()));
		}
							
		//Simulate users checking progress of their operations
		boolean done = false;
		while (!done && currentTime<limitEndTime) {				
			Iterator<TaskHandler> iterator = taskHandlerList.iterator();
			while (iterator.hasNext()) {
				TaskResult taskResult = checkTaskStatus(iterator.next());															
				updateListTaskResult(listTaskResults,taskResult);
				
				if (taskResult.getTaskInfo().getStatus()==TaskStatus.COMPLETED || taskResult.getTaskInfo().getStatus()==TaskStatus.FAILED){
					iterator.remove();
				}
			}	
			
			if (taskHandlerList.size()>0){
				Thread.sleep(numSecondsWaitBetweenCheck * 1000);
				currentTime = System.currentTimeMillis();	
			}
			else{
				done = true;
			}							
		}		
		
		if (taskHandlerList.size()==0){
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
	 * @param numSecondsWaitBetweenCheck number of seconds between checks
	 * @return result of the task received as parameter
	 * @throws Exception
	 */
	private TaskResult checkTaskStatusLoop(TaskHandler taskHandler, int numSecondsWaitBetweenCheck) throws Exception{
		return checkTaskStatusLoop(taskHandler,-1,numSecondsWaitBetweenCheck);
	}
	
	/**
	 * Method responsible to check the status of the task which taskHandler is received as parameter 
	 * in a loop waiting till it's completed or its time-out expired
	 * @param taskHandler taskHandler of the task to check its status
	 * @param numSecondsExecutionTimeOut number of seconds between checks
	 * @param numSecondsWaitBetweenCheck  number of seconds waiting for the task to complete
	 * @return result of the task
	 * @throws Exception
	 */
	private TaskResult checkTaskStatusLoop(TaskHandler taskHandler, int numSecondsExecutionTimeOut, int numSecondsWaitBetweenCheck) throws Exception{
		long currentTime = System.currentTimeMillis();
		long limitEndTime;
		
		if (numSecondsExecutionTimeOut>0){
			limitEndTime = currentTime + (numSecondsExecutionTimeOut * 1000);
		}
		else{
			limitEndTime = Long.MAX_VALUE;
		}	
		
		TaskResult taskResult = new TaskResult(taskHandler.getTaskId());
							
		//Simulate user checking progress of his/her operation
		boolean done=false;
		while (!done && currentTime<limitEndTime) {				
			taskResult = checkTaskStatus(taskHandler);			
			if (taskResult.getTaskInfo().getStatus()==TaskStatus.COMPLETED || taskResult.getTaskInfo().getStatus()==TaskStatus.FAILED){
				done = true;
			}
			else{
				Thread.sleep(numSecondsWaitBetweenCheck * 1000);
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
	 * @param taskHandler taskHandler of the task to check its status
	 * @return result of the task received as parameter
	 * @throws Exception
	 */
	private TaskResult checkTaskStatus(TaskHandler taskHandler) throws Exception{
		String user = "francisco.quevedo";
    	String scope = "devsec";
		
		TaskId taskId = taskHandler.getTaskId();	
		TaskProgress taskProgress = xMap.getTaskProgress(taskId,user,scope);
				
		logger.debug("checking progress reqId= " + taskId.getValue() 
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
		StringBuilder sb = new StringBuilder();
		sb.append("\n");		
		sb.append("****************************************************\n");
		sb.append("Report task  execution:\n");
		sb.append("****************************************************\n");
		for (TaskResult taskResult: taskResults){
			TaskId reqId = taskResult.getTaskId();
			TaskStatus finalStatus = taskResult.getTaskInfo().getStatus();
			TaskResponse taskResponse = taskResult.getTaskInfo().getTaskResponse();
			sb.append("-->ReqId: " + reqId.getValue()
					+ " Final status: " + finalStatus
					+ " Task response: " + taskResponse
					+ "\n");	
		}
		logger.info(sb.toString());		
	}
				
	/**
	 * Method that return the string with the information to print of taskProgress
	 * @param info taskProgress to print
	 * @param level integer that indicate the level of the indentation of the text to print
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
	 * @param level integer that indicate the level of the indentation of the text to print
	 * @return string with the indentation for the level received
	 */
	private String getIndentation(int level){
		StringBuilder sb = new StringBuilder();
		String indentString = "--->";
		for (int i=0; i<level; i++){
			sb.append(indentString);
		}
		return sb.toString();
	}    
     

    /**
     * Method responsible to return the properties object for the xmap test
     * @return  properties object for the xmap test
     * @throws IOException 
     */
    private static Properties getXmapTestProperties() throws IOException{
        String home = System.getProperty("user.home");
        File config = new File(home, "xmap-test.properties");
        Properties properties = new Properties();

        FileInputStream is = new FileInputStream(config);
        properties.load(is);
        return properties;
    }    
    
	
	private static String createTempFolder(String folderName) throws IOException{
    	File sysTempDir = new File(System.getProperty("java.io.tmpdir"));    
    	File newDir = new File(sysTempDir,folderName);
    	if (!newDir.exists()){
    		newDir.mkdirs();
    	}
    	else{
    		FileUtil.deleteDirectoryContent(newDir.getCanonicalPath());
    	}
    	
    	return FileUtil.getOSIndependentPath(newDir.getCanonicalPath());
	}

	
	
	/****************************************************************************************/
	/* PRIVATE CLASSES																		*/														
	/****************************************************************************************/		
	
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
			this.setTaskId(taskId);
			
			this.taskInfo = new TaskProgress();
			this.taskInfo.setStatus(TaskStatus.ACTIVE);
			this.taskInfo.setDetails("Executing");
			this.taskInfo.setPercentage(0);					
		}		
	}	
	
}
