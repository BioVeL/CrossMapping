/*
 * #%L
 * XMap Checklist Importer
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
package org.openbio.xmap.common.cheklistimporter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


import org.openbio.xmap.common.dao.admin.*;
import org.openbio.xmap.common.dao.importer.*;
import org.openbio.xmap.common.dao.util.exception.DAOException;
import org.openbio.xmap.common.serviceinterfaces.types.xmap.ImportChecklistTaskResponse;
import org.openbio.xmap.common.utils.misc.*;
import org.openbio.xmap.common.utils.task.Task;
import org.openbio.xmap.common.utils.task.TaskException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CheckListImporterTask extends Task<ImportChecklistTaskResponse>{

	
	/****************************************************************************************/
	/* ATTRIBUTES																			*/														
	/****************************************************************************************/
	
	/**
	 * slf4j logger for this class
	 */		
	private static Logger logger = LoggerFactory.getLogger(CheckListImporterTask.class);		
	
	private CheckListImporterDAO importerDAO;
	private AdminDAO adminDAO;
	
    private File importFile;
    private String checklistName;
    private String provider;
	
	private ImportChecklistTaskResponse taskResult;
	
	private String checklistId;
    
    
	/****************************************************************************************/
	/* CONSTRUCTORS																			*/		
	/****************************************************************************************/    
	
	public CheckListImporterTask(String jdniDataSource, String pathImportFile, String provider, 
			String checklistName, String user, String scope) throws Exception{
		
		this.taskName = "Import checklist " + checklistName;		
		
		this.importerDAO = CheckListImporterDAOFactory.getImporterDAO(jdniDataSource);		
		this.adminDAO = AdminDAOFactory.getAdminDAO(jdniDataSource);
		
        this.importFile = new File(pathImportFile);
        
        if (!this.importFile.exists()){
        	throw new RuntimeException("import file not found");
        }
        
        this.checklistName = checklistName;
        this.provider = provider;  
        this.user = user;
        this.scope = scope;
        this.taskType = "import";
	}
	
	
	/****************************************************************************************/
	/* PUBLIC METHODS																		*/														
	/****************************************************************************************/
	
    /**
     * Method responsible to run the task
     */
    @Override
    public void run() {
        try {
            setRunThread(Thread.currentThread());
            perform();
            setDone(taskResult);
        }
        catch (TaskException e) {
            setException(e);
        }     
        finally {
            setRunThread(null);
        }
    }	
    

	/**
     * Method responsible to called the task, interrupting the executing of the thread in
     * which the task is running
     */
    @Override
    public synchronized void cancel() {
    	super.cancel();
    }
    
	
    /**
     * Method responsible to perform this task by executing different stages to do the xmap
     * @throws Exception
     */    	
	@Override
	public void perform() throws TaskException {
		try{
			int totalStages = 6;
			int stageNumber;
			
			//Stage 0: Verify that there isn't already a checklist imported with the same name 
			stageNumber = 0;
			startActivePhase("Stage " + stageNumber +" : Verifying there isn't already a checklist imported with this name",stageNumber,totalStages);
			if (existChecklistNameInRepository()){
				setFail("There is already a checklist imported with this name");
			}
			else{				
				//Stage 1: Add checklist name to list of imported checklists
				stageNumber = 1;
				startActivePhase("Stage " + stageNumber +": Adding checklist to repository",stageNumber,totalStages);
				checklistId = addChecklistToRepositoy();						
				
				stablishTransaction();
				try{
					throwIfCancelled();	
					
					//Stage 2: Load the content of the file into the table taxa
					stageNumber = 2;
					startActivePhase("Stage " + stageNumber +": Loading file content to table taxa",stageNumber,totalStages);
					loadFileContent(checklistId);

					throwIfCancelled();	
					
					//Stage 3: Fill table checklist name use from content in table taxa
					stageNumber = 3;
					startActivePhase("Stage " + stageNumber +": Filling table checklist NameUse",stageNumber,totalStages);
					fillNameUse(checklistId);
					
					throwIfCancelled();	
					
					//Stage 4: Build adjacent tree of the checklist
					stageNumber = 4;
					startActivePhase("Stage " + stageNumber +": Building adjacent tree",stageNumber,totalStages);
					buildAdjacentTree(checklistId);
					
					throwIfCancelled();	
							
					//Stage 5: Set the values of the columns in NameUse table that we are going to use in future xmap
					stageNumber = 5;
					startActivePhase("Stage " + stageNumber +": proccessing data for future cross mappings",stageNumber,totalStages);
					preProccessDataForXMap(checklistId);
					
					throwIfCancelled();	
					
					//Commit transaction
					commitTransaction();

					taskResult = new ImportChecklistTaskResponse(checklistId);
				}								
				catch (Exception ex){
					rollBackTransaction(checklistId);
					throw ex;    	    		
				} 
			}
		}
		catch (Exception ex){
			throw new TaskException(ex, "Error importing checklist " + ex.getMessage());    	    		
		}				
	}
	
	
    @Override
    public void setException(TaskException ex) {    	  	
    	try{
    		String pseudoId = Misc.generatePseudoId();
    		this.setExceptionId(pseudoId);
    		
    		String msg = ex.getUserData();
    		String stackTrace = StackTraceUtil.getStackTrace(ex);	
    		String className = this.getClass().getName();
    		List<String> parameters = new ArrayList<String>();
    		parameters.add(this.importFile.getAbsolutePath());
    		parameters.add(this.checklistName);
    		parameters.add(this.checklistName);
    		parameters.add(this.provider);     
    		
    		this.adminDAO.registerException(pseudoId, msg, stackTrace, className, parameters,this.user,this.scope);
    		
    		super.setException(ex);  
    	} 
    	catch (Exception e){ 
			logger.error(e.getMessage(),e);
		}    	
    }	
    
    
	/****************************************************************************************/
	/* PRIVATE METHODS																		*/														
	/****************************************************************************************/		

    private void stablishTransaction() throws DAOException{
    	this.importerDAO.startTransaction();
    }
    
    private void commitTransaction() throws DAOException{
    	this.importerDAO.commitTransaction();
    }
    
    private void rollBackTransaction(String checklistId) throws DAOException{
    	this.importerDAO.rollbackTransaction();    	
    }        
    
	private boolean existChecklistNameInRepository() throws DAOException{
		return this.importerDAO.existChecklistNameInRepository(checklistName,user,scope);
	}
        
	private void loadFileContent(String checklistId) throws IOException, DAOException{
		this.importerDAO.importChecklistIntoTableTaxa(checklistId, this.importFile);          
	}	
		
	private void fillNameUse(String checklistId) throws DAOException{
		this.importerDAO.fillNameUse(checklistId);
	}
	
	private void buildAdjacentTree(String checklistId) throws DAOException{
		this.importerDAO.buildAdjacentTree(checklistId);
	}
	
	private void preProccessDataForXMap(String checklistId) throws DAOException{
		this.importerDAO.preProccessDataForXMap(checklistId);
	}	
	
	private String addChecklistToRepositoy() throws DAOException{				
		String checklistid = this.importerDAO.addChecklistToRepositoy(checklistName, importFile.getName(), user, scope, this.taskId);	
		return checklistid;
	}

		
}
