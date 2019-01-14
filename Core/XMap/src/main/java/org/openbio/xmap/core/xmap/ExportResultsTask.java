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

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.openbio.xmap.common.dao.admin.AdminDAO;
import org.openbio.xmap.common.dao.admin.AdminDAOFactory;
import org.openbio.xmap.common.dao.crossmap.XMapDAO;
import org.openbio.xmap.common.dao.crossmap.XMapDAOFactory;
import org.openbio.xmap.common.dao.util.exception.DAODataSourceException;
import org.openbio.xmap.common.dao.util.exception.DAOException;
import org.openbio.xmap.common.serviceinterfaces.types.xmap.ExportXmapTaskResponse;
import org.openbio.xmap.common.serviceinterfaces.types.xmap.ResultExportXmap;
import org.openbio.xmap.common.serviceinterfaces.types.xmap.XMap;
import org.openbio.xmap.common.utils.misc.FileUtil;
import org.openbio.xmap.common.utils.misc.Misc;
import org.openbio.xmap.common.utils.misc.StackTraceUtil;
import org.openbio.xmap.common.utils.net.NetClient;
import org.openbio.xmap.common.utils.task.Task;
import org.openbio.xmap.common.utils.task.TaskException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.com.bytecode.opencsv.CSVWriter;

public class ExportResultsTask extends Task<ExportXmapTaskResponse> {

	/****************************************************************************************/
	/* ATTRIBUTES																			*/														
	/****************************************************************************************/	
	/**
	 * slf4j logger for this class
	 */		
	private static Logger logger = LoggerFactory.getLogger(XMapTask.class);		
	
	private String xMapId;
	private Boolean includeAcceptedNames;
	private String exportFolder;

	private String exportDate;
	
	private XMapDAO xMapDAO;
	private AdminDAO adminDAO;
	
	private ExportXmapTaskResponse taskResult;
	
	
	/****************************************************************************************/
	/* GETTERS & SETTERS																	*/														
	/****************************************************************************************/		
	
	public String getxMapId() {
		return xMapId;
	}

	public void setxMapId(String xMapId) {
		this.xMapId = xMapId;
	}	
	
	
	/****************************************************************************************/
	/* CONSTRUCTORS																			*/		
	/****************************************************************************************/
        	
	/**
	 * Constructor of Download Task
	 * @param xmapJndiDataSource
	 * @param xMapId
	 * @param includeAcceptedNames
	 * @param exportFolder
	 * @param user
	 * @param scope
	 * @throws DAODataSourceException 
	 */
	public ExportResultsTask(String xmapJndiDataSource, String xMapId, Boolean includeAcceptedNames,
			String exportFolder, String user, String scope) throws DAODataSourceException{
		
		this.taskName = "Export results xmap " + xMapId;
		
		this.xMapDAO = XMapDAOFactory.getXMapDAO(xmapJndiDataSource);
		this.adminDAO = AdminDAOFactory.getAdminDAO(xmapJndiDataSource);
		this.xMapId = xMapId;
		this.includeAcceptedNames = includeAcceptedNames;
		this.exportFolder = exportFolder;
		this.user = user;
		this.scope = scope;
		this.taskType = "export";
		this.exportDate = Misc.convertTime(new Date().getTime(),"dd_MM_yyyy_HH_mm_ss");
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
            //throwIfCancelled();
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
     * Method responsible to perform this task by 
     * @throws Exception
     */    	
	@Override
	public void perform() throws TaskException {				
		try{
    		int totalStages = 7;
			int stageNumber;
    		   		    		
			//Stage 0: Add xMapResult to repository
			stageNumber = 0;
			startActivePhase("Stage 0: Add xMap result to repository",stageNumber,totalStages);
			String xMapResultId=addXMapResultToRepository();			
					
			stablishTransaction();
			try{
				List<File> files = new ArrayList<File>();
				
				throwIfCancelled();	
				
				//Stage 1: Create addit taxa files for left and right checklist and populate with not found in relationship 
				stageNumber = 1;
    			startActivePhase("Stage 1: Creating addit taxa files",stageNumber,totalStages);
				List<File> additTaxaFiles = createAdditTaxaFiles(xMapResultId);
				files.addAll(additTaxaFiles);
				
				throwIfCancelled();	
				
				//Stage 2: Preparatory steps for other cross mapping types  (includes, corresponds, overlaps)
				stageNumber = 2;
    			startActivePhase("Stage 2: Creating xmap file",stageNumber,totalStages);
				File xmapFile = createXMapFile(xMapResultId,includeAcceptedNames);
				files.add(xmapFile);
				
				throwIfCancelled();	
				
				//Stage 3: Obtain the correspond relationships
				stageNumber = 3;
    			startActivePhase("Stage 3: Creating all names file",stageNumber,totalStages);
				File allNamesFile = createAllNamesFile(xMapResultId);
				files.add(allNamesFile);
						
				throwIfCancelled();	
				
				//Stage 4: Obtain the include relationship
				stageNumber = 4;
    			startActivePhase("Stage 4: Generating zip file",stageNumber,totalStages);
				File zipFile = createZipFile(xMapResultId,files);
				files.add(zipFile);			
				
				throwIfCancelled();	
				
		    	//Stage 5: Publish zip file into export folder 
				stageNumber = 5;
    			startActivePhase("Stage 5: publishing zip file into export folder",stageNumber,totalStages);
		    	String resultFileUrl = publishResultsFile(zipFile,exportFolder);		    	
		    	this.xMapDAO.updateExportResultCrossmap(xMapResultId,resultFileUrl);
		    			    	
		    	throwIfCancelled();	
		    	                                  		
				//Stage 6: Clean up
		    	stageNumber = 6;
    			startActivePhase("Stage 6: Clenning up",stageNumber,totalStages);
				cleanUp(xMapResultId,files);
								
								
				throwIfCancelled();	
				
				//Commit transaction
				commitTransaction();
									
				taskResult = new ExportXmapTaskResponse(resultFileUrl);
			}
			catch (Exception ex){
				rollBackTransaction();
				throw ex;    	    		
			}
		}
		catch (Exception ex){
			throw new TaskException(ex, "error in exportation");    	    		
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
    		parameters.add(xMapId);
    		parameters.add(exportFolder);
    		parameters.add(user);
    		parameters.add(scope);
    		
    		this.adminDAO.registerException(pseudoId, msg, stackTrace, className, parameters,this.user, this.scope);
    		
    		super.setException(ex);    	
    	} 
    	catch (Exception e){ 
			logger.error(e.getMessage(),e);
		}    	
    }	
	
	
	/****************************************************************************************/
	/* PRIVATE METHODS 																		*/														
	/****************************************************************************************/
	
    
    private void stablishTransaction() throws DAOException{
    	xMapDAO.startTransaction();
    }
    
    private void commitTransaction() throws DAOException{
    	xMapDAO.commitTransaction();
    }
    
    private void rollBackTransaction() throws DAOException{
    	xMapDAO.rollbackTransaction();
    }        
    
    private boolean existXMapResultInRepository() throws DAOException{
    	return this.xMapDAO.existXMapResultInRepository(xMapId,user,scope);
    }
    
    private String addXMapResultToRepository() throws DAOException{
    	XMap xMap = this.xMapDAO.getCrossMap(xMapId);
    	return this.xMapDAO.addXMapResultToRepository(xMap.getShortName(),xMapId,includeAcceptedNames,user,scope,this.taskId);
    }    
    
	private List<File> createAdditTaxaFiles(String xMapResultId) throws IOException, DAOException{
		ResultExportXmap xMapResult = this.xMapDAO.getCrossMapResult(xMapResultId);
		File csvLeftFile = createTempFile(xMapResult.getName() + "_left_addit_Taxa.csv");				
		File csvRightFile = createTempFile(xMapResult.getName() + "_right_addit_Taxa.csv");
		
		CSVWriter csvLeftWriter = new CSVWriter(new FileWriter(csvLeftFile), '\t','\"','\\');
		CSVWriter csvRightWriter = new CSVWriter(new FileWriter(csvRightFile), '\t','\"','\\');
		try{
			this.xMapDAO.exportFlatTableAdditTaxa(xMapResult.getXMapId(), csvLeftWriter, true);
			this.xMapDAO.exportFlatTableAdditTaxa(xMapResult.getXMapId(), csvRightWriter, false);
		}
		finally{
			csvLeftWriter.close();
			csvRightWriter.close();
		}
		
		List<File> additTaxaFiles = new ArrayList<File>();
		additTaxaFiles.add(csvLeftFile);
		additTaxaFiles.add(csvRightFile);
		
		return additTaxaFiles;	
	}
	
	private File createXMapFile(String xMapResultId, Boolean includeAcceptedNames) throws IOException, DAOException{
		ResultExportXmap xMapResult = this.xMapDAO.getCrossMapResult(xMapResultId);
		File csvFile = createTempFile(xMapResult.getName() + "_xmap.csv");		
		CSVWriter csvWriter = new CSVWriter(new FileWriter(csvFile), '\t','\"','\\');
		try{
			this.xMapDAO.exportFlatTable(xMapResult.getXMapId(), includeAcceptedNames, csvWriter);
		}
		finally{
			csvWriter.close();
		}		
		return csvFile;	
	}
	
	private File createAllNamesFile(String xMapResultId) throws IOException, DAOException{
		ResultExportXmap xMapResult = this.xMapDAO.getCrossMapResult(xMapResultId);
		File csvFile = createTempFile(xMapResult.getName() + "_all_names.csv");
		
		CSVWriter csvWriter =  new CSVWriter(new FileWriter(csvFile), '\t','\"','\\');	
		try{
			this.xMapDAO.exportFlatTableNameUses(xMapResult.getXMapId(), csvWriter);
		}
		finally{
			csvWriter.close();
		}			
		
		return csvFile;	
	}	
	
	private File createZipFile(String xMapResultId,List<File> files) throws IOException,DAOException{
		ResultExportXmap xMapResult = this.xMapDAO.getCrossMapResult(xMapResultId);
		String fileName = "xMapResults_" + xMapResultId + ".zip";
		File zipFile = createTempFile(fileName);
		
		ZipOutputStream zipOutputStream = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(zipFile)));
		try{
			for (File file:files){
				addFileToZip(file,zipOutputStream);
			}
		}
		finally{
			zipOutputStream.close();
		}
		return zipFile;
	}	
	
	private void cleanUp(String xMapResultId, List<File> files) throws DAOException{
		for (File file:files){
			file.delete();
		}
	}	
	
	private File createTempFile(String fileName) throws IOException{		
		String tmpXMapFolder = FileUtil.getOSIndependentPath(System.getProperty("java.io.tmpdir") + "/xmap/" + this.user + "/" + this.scope + "/" + this.exportDate);
					
		File dir = new File(tmpXMapFolder);
		if (!dir.exists()){
			dir.mkdirs();
		}
		
		File file = new File(tmpXMapFolder,fileName);
		if (file.exists()){
			file.delete();
		}
		
		file.createNewFile();	
		return file;
	}
	
	
	private void addFileToZip(File file, ZipOutputStream zip) throws IOException{
        // Create a buffer for reading the files
        byte[] buf = new byte[1024];

        FileInputStream in = new FileInputStream(file);
        try {
            zip.putNextEntry(new ZipEntry(file.getName()));

            // Transfer bytes from the file to the ZIP file
            int len;
            while ((len = in.read(buf)) > 0) {
                zip.write(buf, 0, len);
            }

            // Complete the entry
            zip.closeEntry();
        }
        finally {
            in.close();
        }		
	}
	
	private String publishResultsFile(File file,String folderEndPoint) throws Exception{
        NetClient netClient = new NetClient();     		
    	netClient.uploadFile(file,folderEndPoint,file.getName());
    	return (folderEndPoint +"/" + file.getName());
	}

}
