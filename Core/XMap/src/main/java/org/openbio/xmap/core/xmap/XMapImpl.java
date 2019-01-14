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

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.activation.DataHandler;

import org.openbio.xmap.common.cheklistimporter.CheckListImporterTask;
import org.openbio.xmap.common.dao.admin.AdminDAO;
import org.openbio.xmap.common.dao.admin.AdminDAOFactory;
import org.openbio.xmap.common.dao.crossmap.XMapDAO;
import org.openbio.xmap.common.dao.crossmap.XMapDAOFactory;
import org.openbio.xmap.common.dao.importer.CheckListImporterDAO;
import org.openbio.xmap.common.dao.importer.CheckListImporterDAOFactory;
import org.openbio.xmap.common.dao.util.exception.DAOException;
import org.openbio.xmap.common.serviceinterfaces.services.xmap.XMapService;
import org.openbio.xmap.common.serviceinterfaces.services.xmap.XMapServiceFault;
import org.openbio.xmap.common.serviceinterfaces.services.xmap.XMapServiceFault_Exception;
import org.openbio.xmap.common.serviceinterfaces.types.xmap.Checklist;
import org.openbio.xmap.common.serviceinterfaces.types.xmap.ChecklistSeq;
import org.openbio.xmap.common.serviceinterfaces.types.xmap.DataRetrievalFilterSeq;
import org.openbio.xmap.common.serviceinterfaces.types.xmap.DataRetrievalSortSeq;
import org.openbio.xmap.common.serviceinterfaces.types.xmap.EntriesInChecklistSeq;
import org.openbio.xmap.common.serviceinterfaces.types.xmap.EntriesInUserKnowledgeSeq;
import org.openbio.xmap.common.serviceinterfaces.types.xmap.EntriesInXMapSeq;
import org.openbio.xmap.common.serviceinterfaces.types.xmap.EntryInChecklist;
import org.openbio.xmap.common.serviceinterfaces.types.xmap.EntryInUserKnowledge;
import org.openbio.xmap.common.serviceinterfaces.types.xmap.EntryInXMap;
import org.openbio.xmap.common.serviceinterfaces.types.xmap.IdentifyExtraTaxaType;
import org.openbio.xmap.common.serviceinterfaces.types.xmap.NamesRelationshipType;
import org.openbio.xmap.common.serviceinterfaces.types.xmap.RawData;
import org.openbio.xmap.common.serviceinterfaces.types.xmap.RelationshipDetail;
import org.openbio.xmap.common.serviceinterfaces.types.xmap.ResultExportXmap;
import org.openbio.xmap.common.serviceinterfaces.types.xmap.ResultExportXmapSeq;
import org.openbio.xmap.common.serviceinterfaces.types.xmap.TaskHandlerRunXmap;
import org.openbio.xmap.common.serviceinterfaces.types.xmap.TaskHandlerExportXmap;
import org.openbio.xmap.common.serviceinterfaces.types.xmap.TaskHandlerImportChecklist;
import org.openbio.xmap.common.serviceinterfaces.types.xmap.TaskHandlerRunXmap;
import org.openbio.xmap.common.serviceinterfaces.types.xmap.TaskId;
import org.openbio.xmap.common.serviceinterfaces.types.xmap.TaskProgress;
import org.openbio.xmap.common.serviceinterfaces.types.xmap.TaskStatus;
import org.openbio.xmap.common.serviceinterfaces.types.xmap.Taxon;
import org.openbio.xmap.common.serviceinterfaces.types.xmap.TaxonDetail;
import org.openbio.xmap.common.serviceinterfaces.types.xmap.TaxonSeq;
import org.openbio.xmap.common.serviceinterfaces.types.xmap.TaxonomicRank;
import org.openbio.xmap.common.serviceinterfaces.types.xmap.TaxonomicRankSeq;
import org.openbio.xmap.common.serviceinterfaces.types.xmap.UserKnowledgeLevelForRefinement;
import org.openbio.xmap.common.serviceinterfaces.types.xmap.XMap;
import org.openbio.xmap.common.serviceinterfaces.types.xmap.XMapRelPath;
import org.openbio.xmap.common.serviceinterfaces.types.xmap.XMapSeq;

import org.openbio.xmap.common.utils.misc.FileUtil;
import org.openbio.xmap.common.utils.misc.StackTraceUtil;
import org.openbio.xmap.common.utils.net.NetClient;
import org.openbio.xmap.common.utils.task.Task;
import org.openbio.xmap.common.utils.task.TaskException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class implements the business logic of the xmap service
 * <p>
 * <p>
 * OpenBio XMap  
 * <p>
 * Date: 12/05/12
 * @author Fran
 */
public class XMapImpl implements XMapService {
	
	/****************************************************************************************/
	/* ATTRIBUTES																			*/														
	/****************************************************************************************/

	/**
	 * slf4j logger for this class
	 */		
	private static Logger logger = LoggerFactory.getLogger(XMapImpl.class);	
	    
    private String xmapJndiDataSource; 
    private XMapDAO xMapDAO;
    private CheckListImporterDAO importDAO;
    private AdminDAO adminDAO;

    private String exportFolder;   
    private String uploadFolder;
        
    /**
     * Static hash map that will keep in memory the active tasks in this stroe  
     */
    private static HashMap<String,Task> taskCache = new HashMap<String, Task>();   
    
    
	/****************************************************************************************/
	/* GETTERS & SETTERS																	*/														
	/****************************************************************************************/    

    protected AdminDAO getAdminDAO() {
		return adminDAO;
	}	
    

	/****************************************************************************************/
	/* CONSTRUCTORS																			*/														
	/****************************************************************************************/	
    
    /**
     * Constructor of the XMapImpl
     * @param xmapJndiDs jdni datasource to the database to be use for the xmap
     * @param exportFolder path of the folder in which the xmap will create the files with the result of the crossmappings
     * @param userId
     */
    public XMapImpl(String xmapJndiDs, String exportFolder, String uploadFolder) throws XMapServiceFault_Exception{	
    	try{   		
	    	this.xmapJndiDataSource = xmapJndiDs;
	    	this.adminDAO = AdminDAOFactory.getAdminDAO(xmapJndiDs);
	    	this.importDAO = CheckListImporterDAOFactory.getImporterDAO(xmapJndiDs);
			this.xMapDAO = XMapDAOFactory.getXMapDAO(xmapJndiDs);
			this.exportFolder = exportFolder;
			this.uploadFolder = uploadFolder;
        }
        catch (Exception e) {
            throw raiseXMapServiceException("Error initializing xmap class. " + e.getMessage(), e);
        }				
    }
    
    
	/****************************************************************************************/
	/* PUBLIC METHODS																		*/														
	/****************************************************************************************/	
    
	@Override
	public String uploadChecklistFile(String fileName, DataHandler binaryData, String user, String scope) throws XMapServiceFault_Exception {    	
		try{
			verifyUserExistence(user,scope);
			
			NetClient netClient = new NetClient();
			String userFolder = this.uploadFolder + "/" + user+ "/" + scope;
			netClient.saveDataHandlerFileToDirectory(binaryData, fileName, userFolder);
				   	
		    return "File uploaded sucessfully";
        }
        catch (Exception e) {
            throw raiseXMapServiceException("Error uploading checklist file. " + e.getMessage(), e);
        }	        
	}
	

	@Override
	public List<String> getChecklistFilesUploaded(String user, String scope) throws XMapServiceFault_Exception {		
		try{			
			NetClient netClient = new NetClient();
			String userFolder = this.uploadFolder + "/" + user + "/" + scope;
			File userLocalDir = new File (netClient.getLocalPathFolderForHTTPEndPoint(userFolder));
						
	    	return FileUtil.getListFilesInDirectory(userLocalDir);
	    }
	    catch (Exception e) {
	        throw raiseXMapServiceException("Error getting uploaded checkklist files. " + e.getMessage(), e);
	    }	    	
	}
	
	@Override
	public String deleteChecklistFileUploaded(String fileName, String user, String scope) throws XMapServiceFault_Exception {		
		try{				
			NetClient netClient = new NetClient();
			String userUploadFolder = this.uploadFolder + "/" + user+ "/" + scope;
			File userUploadLocalDir = new File (netClient.getLocalPathFolderForHTTPEndPoint(userUploadFolder));
			
			FileUtil.deleteFile(userUploadLocalDir.getCanonicalPath() + "/" + fileName);
			
			return "Checklist file deleted correctly from the server";
	    }
	    catch (Exception e) {
	        throw raiseXMapServiceException("Error deleting uploaded checkklist file. " + e.getMessage(), e);
	    }	    	
	}	
	
	@Override
	public TaskHandlerImportChecklist importChecklist(String importFileName, String provider, String checklistname,
			String user, String scope) throws XMapServiceFault_Exception {
		try{
			verifyUserExistence(user,scope);
			
			if (this.importDAO.existChecklistNameInRepository(checklistname, user, scope)){
				throw new RuntimeException("Checklist name " + checklistname + " already in use");
			}
			else{								
				NetClient netClient = new NetClient();		
				String userFolder = this.uploadFolder + "/" + user+ "/" + scope;
				File uploadLocalDir = new File (netClient.getLocalPathFolderForHTTPEndPoint(userFolder));
				String localImportFilePath = uploadLocalDir + "/" + importFileName;	
							
				//Create a new task to do the importation of the checklist
				Task importChecklistTask = new CheckListImporterTask(this.xmapJndiDataSource,localImportFilePath,provider,checklistname,user,scope);
				importChecklistTask.addObserver(new TaskObserver(adminDAO));
				
				//Put the task inside the static hashmap in orde to be able later to cancel it by killing the thread
				//in which it's running
		        taskCache.put(importChecklistTask.getTaskId().getValue(),importChecklistTask);	        			
				
		        //Start the task in a new thread
		        new Thread(importChecklistTask).start();
				       
		        //Return the task handler of the task
		        return new TaskHandlerImportChecklist(importChecklistTask.getTaskId(),user,scope,null);
			}
        }
        catch (Exception e) {
            throw raiseXMapServiceException("Error importing checklist. " + e.getMessage(), e);
        }		        
	}

	@Override
	public ChecklistSeq getChecklistsImported(String user, String scope, TaskStatus status) throws XMapServiceFault_Exception {
		try{			
			return this.xMapDAO.getChecklists(user, scope,status);        	      
        }
        catch (Exception e) {
            throw raiseXMapServiceException("Error getting the list of imported checklists. " + e.getMessage(), e);
        }		
	}
	
	
	@Override
	public String deleteChecklistImported(String checklistId, String user, String scope) throws XMapServiceFault_Exception {
		try{			
			if (getCrossMapsByChecklist(checklistId).getXMap().size()>0){
				throw new RuntimeException("This checklist has been used in some xmap. Please delete all the xmap in " +
						"which this checklist has been used before to delete this checklist.");
			}
			else{
				Checklist checklist = this.xMapDAO.getChecklist(checklistId);
				if (checklist==null){
					throw new RuntimeException("The checklist "+ checklistId + " doesn't exist"); 
				}
				else if (checklist.getUser().compareTo(user)!=0 || checklist.getScope().compareTo(scope)!=0){
					throw new RuntimeException ("The checklist to be deleted doesn't belong to the user-scope received ");
				}
				else if (checklist.getStatus()!=TaskStatus.COMPLETED && checklist.getStatus()!=TaskStatus.FAILED){
					throw new RuntimeException("The checklist "+ checklistId + " can not be deleted because it hasn't been completed or failed. You should first stop its task.");
				}
				else{						
					//Delete database info for the checklist
					this.xMapDAO.deleteImportedChecklist(checklistId);
					
					//Delete file without raise exception if it doesn't exist
					try{
						deleteChecklistFileUploaded(checklist.getImportedFileName(),checklist.getUser(),checklist.getScope());
					}
					catch (Exception ex){
						//If the server is redeployed the previous files are deleted in the undeployment but entries in database point to those files
						//So the function of delete checklist should not failed if it is the case.
				    	ex.printStackTrace();
				        System.err.println("[IGNORED]");
					}
					
					return "Checklist delete correctly";
				}
			}						   	      
        }
        catch (Exception e) {
            throw raiseXMapServiceException("Error deleting imported checklist. " + e.getMessage(), e);
        }		
	}	
	   
	
	@Override
	public TaskHandlerRunXmap runXmap(String xmapName, String description, String leftChecklistId, String rightChecklistId, boolean strict,
			IdentifyExtraTaxaType identifyExtraTaxa, boolean compareHigherTaxa, TaxonomicRank highestTaxonomicRankToCompare, 
			UserKnowledgeLevelForRefinement userRefinementLevel, String user, String scope) throws XMapServiceFault_Exception {
		try{			
			verifyUserExistence(user,scope);
			
			if (this.xMapDAO.existXMapNameInRepository(xmapName,user,scope)){
				throw new RuntimeException("There is already a crossmap with this name");
			}			
			else{		
				Checklist lefChecklist = this.xMapDAO.getChecklist(leftChecklistId);
				Checklist rightChecklist = this.xMapDAO.getChecklist(rightChecklistId);
				
				if (lefChecklist==null || rightChecklist==null || lefChecklist.getStatus()!=TaskStatus.COMPLETED || rightChecklist.getStatus()!=TaskStatus.COMPLETED
						|| lefChecklist.getUser().compareTo(user)!=0 || lefChecklist.getScope().compareTo(scope)!=0 || rightChecklist.getUser().compareTo(user)!=0 || rightChecklist.getScope().compareTo(scope)!=0){
					throw new RuntimeException("Please make sure that the left and right checklists exist, they have been imported correctly and belong to the user-scope");
				}
				else{					
					//Create a new task to run the xmap
					Task runXmapTask = new XMapTask(this.xmapJndiDataSource,xmapName,description,leftChecklistId,rightChecklistId,strict,identifyExtraTaxa,
							compareHigherTaxa,highestTaxonomicRankToCompare,userRefinementLevel,user,scope,null,false);
			        
					//Add an observer to the task that will use the adminDAO to write the status or error of the task every time that notifies a change
					runXmapTask.addObserver(new TaskObserver(adminDAO));   
			        
					//Put the task inside the static hashmap in orde to be able later to cancel it by killing the thread in which it's running
			        taskCache.put(runXmapTask.getTaskId().getValue(),runXmapTask);
			        
			        //Start the task in a new thread
			        new Thread(runXmapTask).start();	 
			        
			        //Return the task handler of the task
			        return new TaskHandlerRunXmap(runXmapTask.getTaskId(),user,scope,null);	 
				}
			}    
      	      
        }
        catch (Exception e) {
            throw raiseXMapServiceException("Error initiating the cross mapping. " + e.getMessage(), e);
        }			        
	}
	
	@Override
	public TaskHandlerRunXmap reRunXmap(String xMapId, String description, boolean strict, IdentifyExtraTaxaType identifyExtraTaxa, boolean compareHigherTaxa, 
			TaxonomicRank highestTaxonomicRankToCompare, UserKnowledgeLevelForRefinement userRefinementLevel, boolean overWritePreviousXMap, String user, String scope) throws XMapServiceFault_Exception {
		try{
			XMap xMap = this.xMapDAO.getCrossMap(xMapId);				
			if (xMap==null){
				throw new RuntimeException("The crossmap "+ xMapId + " doesn't exist"); 
			}
			else if (xMap.getUser().compareTo(user)!=0 || xMap.getScope().compareTo(scope)!=0){
				throw new RuntimeException ("The crosmap to re-run doesn't belong to the user-scope received");
			}
			else if (xMap.getStatus()!=TaskStatus.COMPLETED){
				throw new RuntimeException("The crossmap "+ xMapId + " can not be re-run because it hasn't been completed. You should first wait for its completion.");
			}
			else{		
				if (overWritePreviousXMap){
					List<ResultExportXmap> results = getCrossMapsExportResultsByXmap(xMapId).getExportResultXmap();
					
					for (ResultExportXmap result: results){
						if (result.getStatus()!=TaskStatus.COMPLETED){
							throw new RuntimeException("The crossmap "+ xMapId + " can not be re-run overwriting the previous one because the previous one" +
									" has exportation of its results in progrress. You should first wait for their completion or abort them.");
						}
					}
				}
				
				//Create a new task to re-run the xmap
				Task reRunXmapTask = new XMapTask(this.xmapJndiDataSource,xMap.getShortName(),description,xMap.getLeftChecklistId(),xMap.getRightChecklistId(),strict,identifyExtraTaxa,
						compareHigherTaxa,highestTaxonomicRankToCompare,userRefinementLevel,user,scope, xMap.getId(), overWritePreviousXMap);
		        
				//Add an observer to the task that will use the adminDAO to write the status or error of the task every time that notifies a change
				reRunXmapTask.addObserver(new TaskObserver(adminDAO));   
		        
				//Put the task inside the static hashmap in orde to be able later to cancel it by killing the thread in which it's running
		        taskCache.put(reRunXmapTask.getTaskId().getValue(),reRunXmapTask);
		        
		        //Start the task in a new thread
		        new Thread(reRunXmapTask).start();	 
		        
		        //Return the task handler of the task
		        return new TaskHandlerRunXmap(reRunXmapTask.getTaskId(),user,scope,null);	 		
			}
        }
        catch (Exception e) {
            throw raiseXMapServiceException("Error initiating the re-run of the cross mapping. " + e.getMessage(), e);
        }				
	}


	@Override
	public XMapSeq getCrossMaps(String user, String scope, TaskStatus status) throws XMapServiceFault_Exception {
		try{			
			return this.xMapDAO.getCrossMaps(user,scope,status);        	      
        }
        catch (Exception e) {
            throw raiseXMapServiceException("Error getting crossmaps. " + e.getMessage(), e);
        }	
	}	
	
	@Override
	public String deleteCrossMap(String xMapId, String user, String scope) throws XMapServiceFault_Exception {
		try{			
			if (getCrossMapsExportResultsByXmap(xMapId).getExportResultXmap().size()>0){
				throw new RuntimeException("This crossmap has export results. Please delete them first before delete this xmap");
			}
			else{
				XMap xMap = this.xMapDAO.getCrossMap(xMapId);				
				if (xMap==null){
					throw new RuntimeException("The crossmap "+ xMapId + " doesn't exist"); 
				}
				else if (xMap.getUser().compareTo(user)!=0 || xMap.getScope().compareTo(scope)!=0){
					throw new RuntimeException ("The crosmap to be deleted doesn't belong to the user-scope received");
				}
				else if (xMap.getStatus()!=TaskStatus.COMPLETED && xMap.getStatus()!=TaskStatus.FAILED){
					throw new RuntimeException("The crossmap "+ xMapId + " can not be deleted because it hasn't been completed or failed. You should first stop its task.");
				}
				else{
					this.xMapDAO.deleteCrossMap(xMapId,true);
					return "Crossmap deleted correctly";
				}
			}
        }
        catch (Exception e) {
            throw raiseXMapServiceException("Error deleting crossmap. " + e.getMessage(), e);
        }		
	}		

	@Override
	public TaskHandlerExportXmap exportResultXmap(String xMapId, boolean includeAcceptedNames, String user, String scope) throws XMapServiceFault_Exception {
		try{
			verifyUserExistence(user,scope);
			
			XMap xMap = this.xMapDAO.getCrossMap(xMapId);			
			if (xMap==null || xMap.getStatus()!=TaskStatus.COMPLETED || xMap.getUser().compareTo(user)!=0 || xMap.getScope().compareTo(scope)!=0){
				throw new RuntimeException("Please make sure that the crossmap exists, it has been completed correctly and belongs to the user-scope"); 
			}		
			else{						
				//Create a new task for export the results of the xmap
				String userExportFolder = this.exportFolder + "/" + user+ "/" + scope;
		        Task exportTask = new ExportResultsTask(this.xmapJndiDataSource,xMapId,includeAcceptedNames,userExportFolder,user,scope);	        
		        
				//Add an observer to the task that will use the adminDAO to write the status of the task everytime 
				//that notifies a change
		        exportTask.addObserver(new TaskObserver(adminDAO));
		        
				//Put the task inside the static hashmap in orde to be able later to cancel it by killing the thread
				//in which it's running
		        taskCache.put(exportTask.getTaskId().getValue(),exportTask);	        
		        
		        //Start the task in a new thread
		        new Thread(exportTask).start();	    
		        
		        //Return the task handler of the task
		        return new TaskHandlerExportXmap(exportTask.getTaskId(),user,scope,null);
			}
        }
        catch (Exception e) {
            throw raiseXMapServiceException("Error exporting the results of the cross mapping. " + e.getMessage(), e);
        }			        
	}
	
	@Override
	public ResultExportXmapSeq getCrossMapResultsExported(String user, String scope, TaskStatus status) throws XMapServiceFault_Exception {
		try{		
			return this.xMapDAO.getCrossMapResultsExported(user,scope,status);     	
        }
        catch (Exception e) {
            throw raiseXMapServiceException("Error getting crossmap results exported. " + e.getMessage(), e);
        }				
	}	
	
	@Override
	public String deleteCrossMapResultsExported(String xMapResultId, String user, String scope) throws XMapServiceFault_Exception {
		try{		
			ResultExportXmap xMapResult = this.xMapDAO.getCrossMapResult(xMapResultId);
	        
			if (xMapResult==null){
				throw new RuntimeException("The crossmap result "+ xMapResultId + " doesn't exist"); 
			}
			else if (xMapResult.getUser().compareTo(user)!=0 || xMapResult.getScope().compareTo(scope)!=0){
				throw new RuntimeException ("The crossmap result to be deleted doesn't belong to the user-scope received");
			}
			else if (xMapResult.getStatus()!=TaskStatus.COMPLETED && xMapResult.getStatus()!=TaskStatus.FAILED){
				throw new RuntimeException("The xMapResult "+ xMapResultId + " can not be deleted because it hasn't been completed or failed. You should first stop its task.");
			}					
			else{						
				//Delete entries in db
				this.xMapDAO.deleteCrossMapResult(xMapResultId);   
		        				
				//Delete file without raise exception if it doesn't exist
				if (xMapResult.getResultFileURL()!=null){
					try{
						NetClient netClient = new NetClient();		
						FileUtil.deleteFile(netClient.getLocalPathFileForHTTPEndPoint(xMapResult.getResultFileURL()));
					}
					catch (Exception ex){
						//If the server is redeployed the previous files are deleted in the undeployment but entries in database point to those files
						//So the function of delete xmap results should not failed if it is the case.
				    	ex.printStackTrace();
				        System.err.println("[IGNORED]");
					}				
				}
								
				return "Crossmap result deleted correctly";
			}
        }
        catch (Exception e) {
            throw raiseXMapServiceException("Error deleting file with the results of the crossmap. " + e.getMessage(), e);
        }		
	}
		
	@Override
	public EntriesInChecklistSeq getEntriesInChecklist(String checklistId, DataRetrievalFilterSeq filters, DataRetrievalSortSeq sorts, int start, int end, String user, String scope) throws XMapServiceFault_Exception {
		try{		
			Checklist checklist = this.xMapDAO.getChecklist(checklistId);
			if (checklist==null){
				throw new RuntimeException("The checklist "+ checklistId + " doesn't exist"); 
			}
			else if (checklist.getUser().compareTo(user)!=0 || checklist.getScope().compareTo(scope)!=0){
				throw new RuntimeException ("The checklist to be inspected doesn't belong to the user-scope received");
			}
			else if (checklist.getStatus()!=TaskStatus.COMPLETED){
				throw new RuntimeException("The checklist "+ checklistId + " can not be insepected because it hasn't been completed. Please wait until the importation is completed correctly.");
			}				
			else{
				return this.xMapDAO.getEntriesInChecklist(checklistId, filters, sorts, start, end);
			}
        }
        catch (Exception e) {
            throw raiseXMapServiceException("Error getting entries for the given checklist. " + e.getMessage(), e);
        }
	}	


	@Override
	public EntriesInXMapSeq getEntriesInXMap(String xMapId, DataRetrievalFilterSeq filters, DataRetrievalSortSeq sorts, int start, int end, String user, String scope) throws XMapServiceFault_Exception {
		try{		
			XMap xMap = this.xMapDAO.getCrossMap(xMapId);			
			if (xMap==null){
				throw new RuntimeException("The crossmap "+ xMapId + " doesn't exist"); 
			}	
			else if (xMap.getUser().compareTo(user)!=0 || xMap.getScope().compareTo(scope)!=0){
				throw new RuntimeException ("The xmap to be inspected doesn't belong to the user-scope received");
			}
			else if (xMap.getStatus()!=TaskStatus.COMPLETED){
				throw new RuntimeException("The crossmap "+ xMapId + " can not be insepected because it hasn't been completed. Please wait until the xmap is completed correctly.");
			}			
			else{			
				return this.xMapDAO.getEntriesInXMap(xMapId, filters, sorts, start, end);
			}
        }
        catch (Exception e) {
            throw raiseXMapServiceException("Error getting entries for the given crossmap. " + e.getMessage(), e);
        }
	}		

	@Override
	public TaxonomicRankSeq getTaxonomicRanks() throws XMapServiceFault_Exception {
		try{
			return this.xMapDAO.getTaxonomicRanks();
        }
        catch (Exception e) {
            throw raiseXMapServiceException("Error getting the taxonomic ranks. " + e.getMessage(), e);
        }		
	}
	
	
	@Override
	public TaxonDetail getTaxonDetail(String checklistId, String taxonId, String user, String scope) throws XMapServiceFault_Exception {
		try{
			Checklist checklist = this.xMapDAO.getChecklist(checklistId);
			if (checklist==null){
				throw new RuntimeException("The checklist "+ checklistId + " doesn't exist"); 
			}
			else if (checklist.getUser().compareTo(user)!=0 || checklist.getScope().compareTo(scope)!=0){
				throw new RuntimeException ("The checklist to be inspected doesn't belong to the user-scope received");
			}
			else if (checklist.getStatus()!=TaskStatus.COMPLETED){
				throw new RuntimeException("The checklist "+ checklistId + " can not be insepected because it hasn't been completed. Please wait until the importation is completed correctly.");
			}				
			else{		
				TaxonDetail taxonDetail = new TaxonDetail();
				//One taxon should have at least one accepted name
				List<EntryInChecklist> entriesAcceptedNames =  this.xMapDAO.getEntriesInChecklistForTaxon(checklistId,taxonId,"accepted").getEntry();
				if (entriesAcceptedNames.size()==0) throw new RuntimeException("TaxonId not found in this checklist");				
				EntryInChecklist entryAcceptedName = entriesAcceptedNames.get(0);
				taxonDetail.setAcceptedName(entryAcceptedName);
				
				List<EntryInChecklist> entriesSynonyms = this.xMapDAO.getEntriesInChecklistForTaxon(checklistId,taxonId,"synonym").getEntry();
				taxonDetail.getSynonym().addAll(entriesSynonyms);
				
				String parentId = entryAcceptedName.getParentTaxonId();				
				if (parentId!=null && !parentId.equals("") && !parentId.equalsIgnoreCase(entryAcceptedName.getTaxonId()) && !parentId.equalsIgnoreCase("0")){
					EntryInChecklist acceptedNameParentTaxon = this.xMapDAO.getEntriesInChecklistForTaxon(checklistId,parentId,"accepted").getEntry().get(0);
					taxonDetail.setParent(acceptedNameParentTaxon);
				}
				
				List<String> childrenId = this.xMapDAO.getTaxonChildrenId(checklistId,taxonId); 
				for (String childId: childrenId){
					List<EntryInChecklist> childAcceptedNamesEntries = this.xMapDAO.getEntriesInChecklistForTaxon(checklistId,childId,"accepted").getEntry();
					if (childAcceptedNamesEntries.size()>0){
						taxonDetail.getChild().add(childAcceptedNamesEntries.get(0));
					}					
				}
				
				RawData taxonRawData = this.xMapDAO.getTaxonRawData(checklistId,taxonId);
				taxonDetail.setRawData(taxonRawData);
				
				return taxonDetail;
			}
        }
        catch (Exception e) {
            throw raiseXMapServiceException("Error getting the taxon detail. " + e.getMessage(), e);
        }			
	}
	
	@Override
	public RelationshipDetail getXMapRelationshipDetail(String xMapId, String leftTaxonId, String rightTaxonId, String user, String scope) throws XMapServiceFault_Exception {
		try{		
			XMap xMap = this.xMapDAO.getCrossMap(xMapId);			
			if (xMap==null){
				throw new RuntimeException("The crossmap "+ xMapId + " doesn't exist"); 
			}	
			else if (xMap.getUser().compareTo(user)!=0 || xMap.getScope().compareTo(scope)!=0){
				throw new RuntimeException ("The xmap to be inspected doesn't belong to the user-scope received");
			}
			else if (xMap.getStatus()!=TaskStatus.COMPLETED){
				throw new RuntimeException("The crossmap "+ xMapId + " can not be insepected because it hasn't been completed. Please wait until the xmap is completed correctly.");
			}			
			else{			
				return this.xMapDAO.getXMapRelationshipDetail(xMapId,leftTaxonId,rightTaxonId);
			}
        }
        catch (Exception e) {
            throw raiseXMapServiceException("Error getting detail relationship between taxa in crossmap. " + e.getMessage(), e);
        }
	}

	@Override
	public TaxonSeq getTaxonChildren(String checklistId, String taxonId, String user, String scope) throws XMapServiceFault_Exception {
		try{		
			Checklist checklist = this.xMapDAO.getChecklist(checklistId);
			if (checklist==null){
				throw new RuntimeException("The checklist "+ checklistId + " doesn't exist"); 
			}
			else if (checklist.getUser().compareTo(user)!=0 || checklist.getScope().compareTo(scope)!=0){
				throw new RuntimeException ("The checklist to be inspected doesn't belong to the user-scope received");
			}
			else if (checklist.getStatus()!=TaskStatus.COMPLETED){
				throw new RuntimeException("The checklist "+ checklistId + " can not be insepected because it hasn't been completed. Please wait until the importation is completed correctly.");
			}				
			else{
				List<String> childrenId;
				if (taxonId==null || taxonId.equalsIgnoreCase("-1") || taxonId.equalsIgnoreCase("")){
					childrenId = this.xMapDAO.getTaxonChildrenId(checklistId,"0");
				}
				else{
					childrenId = this.xMapDAO.getTaxonChildrenId(checklistId,taxonId); 
				}				
				
				TaxonSeq children = new TaxonSeq();				
				for (String childId: childrenId){
					Taxon child = getTaxon(checklistId,childId);
					children.getChild().add(child);
				}
																
				return children;
			}
        }
        catch (Exception e) {
            throw raiseXMapServiceException("Error getting children. " + e.getMessage(), e);
        }
	}
	
	@Override
	public TaxonSeq getTaxonChildrenPlusRelationships(String checklistId, String taxonId, String xMapId, boolean isLeftTaxon, String user, String scope) throws XMapServiceFault_Exception {
		try{		
			Checklist checklist = this.xMapDAO.getChecklist(checklistId);
			XMap xMap = this.xMapDAO.getCrossMap(xMapId);							
			if (xMap==null){
				throw new RuntimeException("The crossmap "+ xMapId + " doesn't exist"); 
			}	
			else if (xMap.getUser().compareTo(user)!=0 || xMap.getScope().compareTo(scope)!=0){
				throw new RuntimeException ("The xmap to be inspected doesn't belong to the user-scope received");
			}
			else if (xMap.getStatus()!=TaskStatus.COMPLETED){
				throw new RuntimeException("The crossmap "+ xMapId + " can not be insepected because it hasn't been completed. Please wait until the xmap is completed correctly.");
			}			
			else{	
				TaxonSeq children = getTaxonChildren(checklistId,taxonId,user,scope);
								
				for (Taxon child: children.getChild()){
					List<EntryInXMap> rels = this.xMapDAO.getEntriesInXMapForTaxon(xMapId, child.getTaxonId(), isLeftTaxon).getEntry();
					for (EntryInXMap rel : rels){
						XMapRelPath relPath = new XMapRelPath();
						relPath.setRelationship(rel.getRelationship());						
						List<String> path;
						Taxon otherTaxon = null;
						if (isLeftTaxon){
							otherTaxon = getTaxon(xMap.getRightChecklistId(), rel.getTaxonIdRight());
							path = this.xMapDAO.getPathTaxonInChecklist(xMap.getRightChecklistId(),rel.getTaxonIdRight());
						}
						else{
							otherTaxon = getTaxon(xMap.getLeftChecklistId(), rel.getTaxonIdLeft());
							path = this.xMapDAO.getPathTaxonInChecklist(xMap.getLeftChecklistId(),rel.getTaxonIdLeft());
						}	
						relPath.setOtherTaxon(otherTaxon);
						relPath.getPath().addAll(path);
						child.getXmapRels().add(relPath);
					}
				}
				
				return children;
			}
        }
        catch (Exception e) {
            throw raiseXMapServiceException("Error getting children. " + e.getMessage(), e);
        }
	}	
	


	@Override
	public EntriesInUserKnowledgeSeq getEntriesInUserKnowledge(DataRetrievalFilterSeq filters, DataRetrievalSortSeq sorts, int start, int end, String user, String scope) throws XMapServiceFault_Exception {
		try{		
			return this.xMapDAO.getEntriesInUserKnowledge(filters, sorts, start, end);
        }
        catch (Exception e) {
            throw raiseXMapServiceException("Error getting entries in user knowledge. " + e.getMessage(), e);
        }		
	}


	@Override
	public String addUserKnowledge(EntryInUserKnowledge entry, boolean forceIncrementalXMap) throws XMapServiceFault_Exception {
		try{						
			String userKnowledgeId = this.xMapDAO.addEntryInUserKnowledge(entry);
			
			if (forceIncrementalXMap){
				//TODO: re run xmap incrementally
			}
			
			return userKnowledgeId;
        }
        catch (Exception e) {
            throw raiseXMapServiceException("Error adding user knowledge. " + e.getMessage(), e);
        }		
	}	


	@Override
	public void deleteUserKnowledge(String userKnowledgeId, String user, String scope) throws XMapServiceFault_Exception {
		try{
			EntryInUserKnowledge entry = this.xMapDAO.getEntryInUserKnowledge(userKnowledgeId);					
			if (entry==null){
				throw new RuntimeException("Entry "+ userKnowledgeId + " doesn't exist"); 
			}	
			else if (entry.getUser().compareTo(user)!=0 || entry.getScope().compareTo(scope)!=0){
				throw new RuntimeException ("The entry to be deleted doesn't belong to the user-scope received");
			}			
			else{
				this.xMapDAO.deleteEntryInUserKnowledge(userKnowledgeId);
			}
        }
        catch (Exception e) {
            throw raiseXMapServiceException("Error delete entry in user knowledge. " + e.getMessage(), e);
        }			
	}


	@Override
	public TaskProgress getTaskProgress(TaskId taskId, String user, String scope) throws XMapServiceFault_Exception {
        try{
        	return this.adminDAO.getTaskProgress(taskId);	
        }
        catch (Exception e) {
            throw raiseXMapServiceException("Error getting task progress. " + e.getMessage(), e);
        }		        
	}


	@Override
	public List<TaskId> getActiveTasks(String user, String scope) throws XMapServiceFault_Exception {
		try{
			return this.adminDAO.getActiveTasks(user,scope);			
        }
        catch (Exception e) {
            throw raiseXMapServiceException("Error getting active tasks. " + e.getMessage(), e);
        }			
	}


	@Override
	public String cancelTask(TaskId taskId, String user, String scope) throws XMapServiceFault_Exception {
        try{
        	Task task = taskCache.get(taskId.getValue());                	
        	if (task!=null){
            	if (task.getUser().compareTo(user)!=0 ||task.getScope().compareTo(scope)!=0){
            		throw new RuntimeException ("The task to be cancelled doesn't belong to the user-scope received ");
            	}
    			else if (task.getTaskProgress().getStatus()!=TaskStatus.PENDING && task.getTaskProgress().getStatus()!=TaskStatus.ACTIVE){
    				throw new RuntimeException("Only task in status pending or active can be cancelled");
    			}	
            	else{
			        task.cancel();
			        taskCache.remove(taskId.getValue());
            	}
        	}
        	else{
        		//Force to set task to cancel
        		this.adminDAO.cancelTask(taskId);
        	}
        	return "Cancel signal sent correctly. Please wait till task is cancelled completely";
        }
        catch (Exception e) {
            throw raiseXMapServiceException("Error cancelling tasks. " + e.getMessage(), e);
        }		        
	}	
	
	
	/****************************************************************************************/
	/* PRIVATE METHODS																		*/														
	/****************************************************************************************/	    
    
    /**
     * Method that returns a XMapServiceFault_Exception with a descriptive msg to show to the user
     * and the cause of the original exception.
     * @param msg Message to show to the user
     * @param ex Original exception
     * @return XMapServiceFault_Exception
     */
    private XMapServiceFault_Exception raiseXMapServiceException(String msg, Exception ex){
        //Wrap exception into XMapService Exception
        XMapServiceFault xMapServiceFault = new XMapServiceFault();
        xMapServiceFault.setUserData(msg);

        //Raise exception wrapped
        return (XMapServiceFault_Exception) new XMapServiceFault_Exception(msg,xMapServiceFault,ex);
    }	
    
    /**
     * Method in charge of verify if the user/scope exists and if it doesn't create it
     * @param user
     * @param scope
     * @throws DAOException
     */
    private void verifyUserExistence(String user, String scope) throws DAOException{
    	this.adminDAO.verifyUserExistence(user,scope);
    }

    
    private XMapSeq getCrossMapsByChecklist(String checklistId) throws DAOException{
    	return this.xMapDAO.getCrossMapsByChecklist(checklistId);
    }    
    
    private ResultExportXmapSeq getCrossMapsExportResultsByXmap(String xMapId) throws DAOException{
    	return this.xMapDAO.getCrossMapsExportResultsByXmap(xMapId);
    }
    
    
    private Taxon getTaxon(String checklistId, String taxonId) throws DAOException{
    	Taxon taxon = null;
    	List<EntryInChecklist> taxonAcceptedNamesEntries = this.xMapDAO.getEntriesInChecklistForTaxon(checklistId,taxonId,"accepted").getEntry();	
		if (taxonAcceptedNamesEntries.size()>0){
			EntryInChecklist taxonAcceptedNameEntry = taxonAcceptedNamesEntries.get(0);
			taxon = new Taxon();
			taxon.setChecklistId(checklistId);
			taxon.setParentId(taxonAcceptedNameEntry.getParentTaxonId());
			taxon.setTaxonId(taxonAcceptedNameEntry.getTaxonId());
			taxon.setRank(taxonAcceptedNameEntry.getRank());
			taxon.setStatus(taxonAcceptedNameEntry.getStatus());
			taxon.setAcceptedName(taxonAcceptedNameEntry.getTidyName());
			
			List<EntryInChecklist> taxonSynonymEntries = this.xMapDAO.getEntriesInChecklistForTaxon(checklistId,taxonId,"synonym").getEntry();
			for (EntryInChecklist taxonSynonymEntry: taxonSynonymEntries){
				taxon.getSynonyms().add(taxonSynonymEntry.getTidyName());
			}
			taxon.getChildrenId().addAll(this.xMapDAO.getTaxonChildrenId(checklistId,taxonId)); 								
		}	
		return taxon;
    }
    
    
    
	/****************************************************************************************/
	/* PRIVATE CLASSES																		*/														
	/****************************************************************************************/
    
    private class TaskObserver implements Observer {    	        
        private AdminDAO adminDAO;

        public TaskObserver(AdminDAO adminDAO) {                        
            this.adminDAO = adminDAO;
        }

        /**
         * Method responsible print information of the progress when its state has been updated
         * Note: This method is called whenever the observed object is changed.
         * @param observable the observable object.
         * @param arg an argument passed to the notifyObservers method.
         */
        @Override
        public void update(Observable observable, Object arg) {
        	try{
	        	Task task = (Task) observable;
	        	
	        	//Update the task progress
	    		adminDAO.updateTaskProgress(task);
	    		
	    		//Register exception if has happened
	        	if (task.getExceptionId()!=null || !task.getExceptionId().isEmpty()){
	        		TaskException ex = task.getProgress().getException();
	        		String msg = ex.getUserData();
	        		String stackTrace = StackTraceUtil.getStackTrace(ex);	
	        		String className = task.getClass().getName();	        
	        		adminDAO.registerException(task.getExceptionId(), msg, stackTrace, className, ex.getParameters(),task.getUser(),task.getScope());
	        	}
	    		
	    		//When a task has finished independently because it was successful or it failed
	    		//we remove from the hashmap
	    		if (task.getTaskProgress().getFinishDate()!=-1){
	    			 taskCache.remove(task.getTaskId().getValue());
	    		}	    		
        	}
	    	catch(Exception e){
	    		logger.error("Error updatating task progress", e);
	    	}
        } 
    }
}
