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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


import org.openbio.xmap.common.dao.crossmap.XMapDAO;
import org.openbio.xmap.common.dao.crossmap.XMapDAOFactory;
import org.openbio.xmap.common.dao.util.exception.DAODataSourceException;
import org.openbio.xmap.common.dao.util.exception.DAOException;
import org.openbio.xmap.common.serviceinterfaces.types.xmap.ResultExportXmap;
import org.openbio.xmap.common.serviceinterfaces.types.xmap.RunXmapTaskResponse;
import org.openbio.xmap.common.serviceinterfaces.types.xmap.IdentifyExtraTaxaType;
import org.openbio.xmap.common.serviceinterfaces.types.xmap.TaskStatus;
import org.openbio.xmap.common.serviceinterfaces.types.xmap.TaxonomicRank;
import org.openbio.xmap.common.serviceinterfaces.types.xmap.UserKnowledgeLevelForRefinement;
import org.openbio.xmap.common.utils.misc.Misc;
import org.openbio.xmap.common.utils.task.Task;
import org.openbio.xmap.common.utils.task.TaskException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class to implement the export task which extends Task indicating the return type of an export data operation
 * <p>
 * 4D4Life WP7  
 * <p>
 * Date: 07/12/11
 * @author scmjpg
 */
public class XMapTask extends Task<RunXmapTaskResponse> {


	/****************************************************************************************/
	/* ATTRIBUTES																			*/														
	/****************************************************************************************/	
	/**
	 * slf4j logger for this class
	 */		
	private static Logger logger = LoggerFactory.getLogger(XMapTask.class);		
	
	private String xMapName;
	private String description;
	private String leftChecklistId;
	private String rightChecklistId;
	private boolean strict; 
	private IdentifyExtraTaxaType identifyExtraTaxa;
	private boolean compareHigherTaxa;
	private TaxonomicRank highestTaxonomicRankToXmap;
	private UserKnowledgeLevelForRefinement userRefinementLevel;
	private String xMapIdToReRun;
	private boolean overwritePreviousXMap;
	
		
	private XMapDAO xMapDAO;
	
	private RunXmapTaskResponse taskResult;
		
	/****************************************************************************************/
	/* GETTERS & SETTERS																	*/														
	/****************************************************************************************/		
	
	public String getxMapName() {
		return xMapName;
	}

	public void setxMapName(String xMapName) {
		this.xMapName = xMapName;
	}
	
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getLeftChecklistId() {
		return leftChecklistId;
	}

	public void setLeftChecklistId(String leftChecklistId) {
		this.leftChecklistId = leftChecklistId;
	}

	public String getRightChecklistId() {
		return rightChecklistId;
	}

	public void setRightChecklistId(String rightChecklistId) {
		this.rightChecklistId = rightChecklistId;
	}

	public boolean getStrict() {
		return strict;
	}

	public void setStrict(boolean strict) {
		this.strict = strict;
	}

	public IdentifyExtraTaxaType getIdentifyExtraTaxa() {
		return identifyExtraTaxa;
	}

	public void setIdentifyExtraTaxa(IdentifyExtraTaxaType identifyExtraTaxa) {
		this.identifyExtraTaxa = identifyExtraTaxa;
	}
	
	public boolean getCompareHigherTaxa() {
		return compareHigherTaxa;
	}

	public void setCompareHigherTaxa(boolean compareHigherTaxa) {
		this.compareHigherTaxa = compareHigherTaxa;
	}

	public TaxonomicRank getHighestTaxonomicRankToXmap() {
		return highestTaxonomicRankToXmap;
	}

	public void setHighestTaxonomicRankToXmap(TaxonomicRank highestTaxonomicRankToXmap) {
		this.highestTaxonomicRankToXmap = highestTaxonomicRankToXmap;
	}	
	
	
	public UserKnowledgeLevelForRefinement getUserRefinementLevel() {
		return userRefinementLevel;
	}

	public void setUserRefinementLevel(
			UserKnowledgeLevelForRefinement userRefinementLevel) {
		this.userRefinementLevel = userRefinementLevel;
	}
		
	public String getxMapIdToReRun() {
		return xMapIdToReRun;
	}

	public void setxMapIdToReRun(String xMapIdToReRun) {
		this.xMapIdToReRun = xMapIdToReRun;
	}

	public boolean isOverwritePreviousXMap() {
		return overwritePreviousXMap;
	}

	public void setOverwritePreviousXMap(boolean overwritePreviousXMap) {
		this.overwritePreviousXMap = overwritePreviousXMap;
	}	
	

		
	/****************************************************************************************/
	/* CONSTRUCTORS																			*/		
	/****************************************************************************************/    

	/**
	 * Constructor of XMap Task
	 * @param xmapJndiDataSource jndi to connect to the database
	 * @param xMapName name of the xmap 
	 * @param leftChecklistId id of left checkList
	 * @param rightChecklistId id of right checkList
	 * @param strict boolean that indicates if the comparison between taxa is strict (true) or without using author's names (false)
	 * @param identifyExtraTaxa parameter that indicates if xmap should identify extra taxa, and if so what type.
	 * @param compareHigherTaxa
	 * @param highestTaxonomicRankToXmap
	 * @param userRefinementLevel
	 * @param xMapIdToReRun
	 * @param overwritePreviousXMap
	 * @throws DAODataSourceException 
	 */
	public XMapTask(String xmapJndiDataSource, String xMapName, String description, String leftChecklistId, String rightChecklistId, 
			boolean strict, IdentifyExtraTaxaType identifyExtraTaxa, boolean compareHigherTaxa, TaxonomicRank highestTaxonomicRankToXmap,
			UserKnowledgeLevelForRefinement userRefinementLevel, String user, String scope, String xMapIdToReRun, boolean overwritePreviousXMap) throws DAODataSourceException {
		
		this.taskName = "XMap " +  xMapName + " checklists " + leftChecklistId + " - " + rightChecklistId;
		
		this.xMapDAO = XMapDAOFactory.getXMapDAO(xmapJndiDataSource);
		this.xMapName = xMapName;
		this.description = description;
		this.leftChecklistId = leftChecklistId;
		this.rightChecklistId = rightChecklistId;
		this.strict = strict;
		this.identifyExtraTaxa = identifyExtraTaxa;
		this.user = user;
		this.compareHigherTaxa = compareHigherTaxa;
		this.highestTaxonomicRankToXmap = highestTaxonomicRankToXmap;
		this.userRefinementLevel = userRefinementLevel;
		this.scope = scope;
		this.xMapIdToReRun = xMapIdToReRun;
		this.overwritePreviousXMap = overwritePreviousXMap;
		this.taskType = "xmap";
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
     * Method responsible to perform this task by executing different stages to do the xmap
     * @throws Exception
     */    	
	@Override
	public void perform() throws TaskException {	
    	try{
    		int totalStages = 12;
			int stageNumber;
    		
			String xMapId="";
			
			
			//Stage 0: Verify cross-map name 
			stageNumber = 0;
			startActivePhase("Stage 0: Verifying cross-map name",stageNumber,totalStages);			
			if (xMapIdToReRun==null){ //Not re-run				
	    		if (existXMapNameInRepository()){
	    			setFail("There is already a crossmap with this name");
	    			return;
	    		}
	    		else{	    		    			    			
					xMapId=addXMapNameToRepository();
	    		}		
			}
			else{ //Re-run a previous cross-map
				if (overwritePreviousXMap){
					xMapId=xMapIdToReRun;		
	    			deleteContentPreviousXMap(xMapId);	    			
	    			updateXMapInRepository(xMapId);					
				}
				else{
					DateFormat dateFormat = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
					Date date = new Date();
					this.xMapName= this.xMapName + dateFormat.format(date);
					xMapId=addXMapNameToRepository();
				}									
			}
				    		
				
			//TODO: JOB submission. 
			// Determine when the task should be send depending of the combine size of checklists
				
			//If submit job
				//Preapare the dumps or get the ids if already generated
			
				//Call the job
			
				//Track its status
			
				//If finish correctly
					//Get return dump and put it in db
			
    		stablishTransaction();
	    	try{   		
	    		throwIfCancelled();		    		
	    			    	    		
    			//Stage 1: Create tables required for running the xmap and save its results 
	    		stageNumber = 1;
	    		startActivePhase("Stage 1: Creating tables for XMap",stageNumber,totalStages);
				createXMapTables(xMapId);
				
				throwIfCancelled();
									
				/************************************************
				 *		 Cross-map lower taxa					*	 
				 *************************************************/
				TaxonomicRank rank = this.xMapDAO.getTaxonomicRankByName("species");
				
				//Stage 2: Fill table elements in common with names in common
				stageNumber = 2;
				startActivePhase("Stage 2: Fill table names in common",stageNumber,totalStages);
				fillTableElementsInCommonByNames(xMapId);
				
				throwIfCancelled();
									
				//Stage 3: Fill tables ToMany and ManyToMany
				stageNumber = 3;
				startActivePhase("Stage 3: Fill tables toMany",stageNumber,totalStages);
				fillTablesToMany(xMapId);
				
				throwIfCancelled();					
				
				//Stage 4: Obtain the not_found_in relationships
				stageNumber = 4;
				startActivePhase("Stage 4: Obtaining not_found_in relationships",stageNumber,totalStages);
				obtainNotFoundInRelationships(xMapId,rank);
						
				throwIfCancelled();					
				
				//Stage 5: Obtain the correspond relationships
				stageNumber = 5;
				startActivePhase("Stage 5: Obtaining correspond relationships",stageNumber,totalStages);
				obtainCorrespondRelationships(xMapId,rank);
						
				throwIfCancelled();
				
				//Stage 6: Obtain the includes relationship
				stageNumber = 6;
				startActivePhase("Stage 6: Obtaining includes relationships",stageNumber,totalStages);
				obtainIncludesRelationships(xMapId,rank);
				
				throwIfCancelled();
									
				//Stage 7: Obtain the included_by relationship
				stageNumber = 7;
				startActivePhase("Stage 7: Obtaining included_by relationships",stageNumber,totalStages);
				obtainIncludedByRelationships(xMapId,rank);
				
				throwIfCancelled();
									
				//Stage 8: Obtain the overlaps relationships
				stageNumber = 8;
				startActivePhase("Stage 8: Obtaining overlaps relationships",stageNumber,totalStages);
				obtainOverlapsRelationships(xMapId,rank);
				
				throwIfCancelled();
				
				//Stage 9: Reevaluate the entries in the flat table with the 'notFoundIn' relationships
				stageNumber = 9;
				startActivePhase("Stage 9: Reevaluating not found relationships",stageNumber,totalStages);
				reevaluateLowerTaxaNotFoundRelationships(xMapId);
						
				throwIfCancelled();

				
				
				/************************************************
				 *		 Cross-map higher taxa					*	 
				 *************************************************/			
				
				//Stage 10: Crossmap higher taxa
				stageNumber = 10;
				startActivePhase("Stage 10: Crossmapping higher taxa",stageNumber,totalStages);
				crossmapHigherTaxa(xMapId);
				
				throwIfCancelled();
				
				//Stage 11: Clean the temporary results
				stageNumber = 11;
				startActivePhase("Stage 11: Cleaning temporary tables",stageNumber,totalStages);
				cleanUp(xMapId);
			
				throwIfCancelled();
				
				//Commit transaction
				commitTransaction();
								
				taskResult = new RunXmapTaskResponse(xMapId);
			}
	    	catch (Exception ex){    		
	    		rollBackTransaction(xMapId);	    
	    		throw ex;    	    		
	    	}  
		}
		catch (Exception ex){
			throw new TaskException(ex, "error in xmap");    	    		
		}		    
	}
	
	    
    @Override
    public void setException(TaskException ex) {    	
    	try{
    		String pseudoId = Misc.generatePseudoId();
    		this.setExceptionId(pseudoId);
    		
    		List<String> parameters = new ArrayList<String>();
    		parameters.add(xMapName);
    		parameters.add(leftChecklistId);
    		parameters.add(rightChecklistId);
    		parameters.add(strict?"1":"0");
    		parameters.add((identifyExtraTaxa!=null?identifyExtraTaxa.toString():"null"));
    		parameters.add(compareHigherTaxa?"1":"0");
    		parameters.add(highestTaxonomicRankToXmap.getName());
    		parameters.add(userRefinementLevel.value());
    		ex.setParameters(parameters);
    		
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
    	this.xMapDAO.startTransaction();
    }
    
    private void commitTransaction() throws DAOException{
    	this.xMapDAO.commitTransaction();
    }
    
    private void rollBackTransaction(String xMapId) throws DAOException{
    	this.xMapDAO.dropTemporaryTables(xMapId);
    	this.xMapDAO.rollbackTransaction();
    }    
    
    
    private boolean existXMapNameInRepository() throws DAOException{
    	return this.xMapDAO.existXMapNameInRepository(xMapName,user,scope);
    }
    
    private String addXMapNameToRepository() throws DAOException{
    	return this.xMapDAO.addXMapNameToRepository(xMapName,description,leftChecklistId,rightChecklistId,strict,identifyExtraTaxa,compareHigherTaxa,highestTaxonomicRankToXmap,userRefinementLevel,user,scope,this.taskId);
    }    
    

	/**
	 * Method responsible to create the tables required for running the xmap and save its results 
	 * @throws DAOException 
	 */
	private void createXMapTables(String xMapId) throws DAOException{
		this.xMapDAO.createXMapTables(xMapId);		
	}
	
	private void fillTableElementsInCommonByNames(String xMapId) throws DAOException{
		this.xMapDAO.fillTableElementsInCommonByNames(xMapId,leftChecklistId,rightChecklistId,strict,userRefinementLevel);
	}	
	
	private void fillTablesToMany(String xMapId) throws DAOException{
		this.xMapDAO.fillTablesToMany(xMapId);
	}	
			
	private void obtainNotFoundInRelationships(String xMapId, TaxonomicRank rank) throws DAOException{
		this.xMapDAO.obtainNotFoundInRelationships(xMapId,leftChecklistId,rightChecklistId,rank);
	}			
	
	private void obtainCorrespondRelationships(String xMapId, TaxonomicRank rank) throws DAOException{
		this.xMapDAO.obtainCorrespondRelationships(xMapId,leftChecklistId,rightChecklistId,rank);
	}		
		
	private void obtainIncludesRelationships(String xMapId, TaxonomicRank rank) throws DAOException{
		this.xMapDAO.obtainIncludesRelationships(xMapId,leftChecklistId,rightChecklistId,rank);
	}
	
	private void obtainIncludedByRelationships(String xMapId, TaxonomicRank rank) throws DAOException{
		this.xMapDAO.obtainIncludedByRelationships(xMapId,leftChecklistId,rightChecklistId,rank);
	}		
	
	private void obtainOverlapsRelationships(String xMapId, TaxonomicRank rank) throws DAOException{
		this.xMapDAO.obtainOverlapsRelationships(xMapId,leftChecklistId,rightChecklistId,rank);
	}	
	

	private void reevaluateLowerTaxaNotFoundRelationships(String xMapId) throws DAOException{
		if(this.strict && this.identifyExtraTaxa!=IdentifyExtraTaxaType.NONE){
			//Create new extra working table 
			//this.xMapDAO.createExtraWorkingTable(xMapId);
					
			//for each 'not_found_in' in flat table, alter the relationship if one of the names
			// for the not-found-in taxon id has the same nameNoAuthorId as one of the names in the other list
			this.xMapDAO.reevaluateLowerTaxaNotFoundWithPossNameMatch(xMapId,leftChecklistId,rightChecklistId,userRefinementLevel);
			
			if (identifyExtraTaxa==IdentifyExtraTaxaType.GENERIC_TRANSFER){
				//Delete the previous content in extra xmap working table
				//this.xMapDAO.deleteContentInExtraWorkingTable(xMapId);
				
				// For each remaining 'not_found_in', updatate the relationship if one of the names 
				// for the not-found-in taxon id has the same epithet as one of the names in the other list
				// and the authority of one of the names identified is contained in the other (implying generic transfer)
				// (NOT allowing check for whether an author string of '' is a substring of the other one 
				this.xMapDAO.reevaluateLowerTaxaNotFoundWithPossGenTrnfr(xMapId,leftChecklistId,rightChecklistId,userRefinementLevel);				
			}					
		}
	}	
	
	private void crossmapHigherTaxa (String xMapId) throws DAOException{		
		if (this.compareHigherTaxa){			
			//Create the tables: higherTaxaInCommon
			this.xMapDAO.createTablesForXMapHigherTaxa(xMapId);
						
			// Get all different ranks in left and right checklist			
			List<TaxonomicRank> ranksInLeftChecklist = getTaxonomicRanksInChecklist(leftChecklistId);
			List<TaxonomicRank> ranksInRighthecklist = getTaxonomicRanksInChecklist(rightChecklistId);
			
			TaxonomicRank currentRank = this.xMapDAO.getTaxonomicRankByName("subgenus");
			
			//Run the xmap for the higher taxa from the rank subgenus until the highest rank selected by the user 
			while (currentRank!=null && compareTaxonomicRanks(currentRank,highestTaxonomicRankToXmap)<=0){				
				if ((ranksInLeftChecklist.contains(currentRank) && !ranksInRighthecklist.contains(currentRank)) ||
						(ranksInRighthecklist.contains(currentRank) && !ranksInLeftChecklist.contains(currentRank))){
					this.xMapDAO.obtainNotFoundInRelationships(xMapId,leftChecklistId,rightChecklistId,currentRank);					
				}
				else if (ranksInLeftChecklist.contains(currentRank) && ranksInRighthecklist.contains(currentRank)) {							
				
					//Delete previous content of table higherTaxaInCommon and fill it with the registers in table elementInCommon,
					//that in this moment should contain relationship between the taxa of the the previous rank, plus the ancestors for each 
					//register that has the rank are going to process in this iteration.
					this.xMapDAO.reFillTableHigherTaxaInCommon(xMapId,currentRank);
					
					//Delete the previous content of tables elemenmtsInCommon,toManys1, toManys2  
					this.xMapDAO.cleanPreviousXMapCalculationTables(xMapId);
					
					//Fill table elementsInCommon with ancestor fields for the entries in table higherTaxaInCommon 
					//where both ancestor fields are not null and its relationship is confirmed (aka certain)
					this.xMapDAO.fillTableElementsInCommonByCurrentLevel(xMapId);
					
					/* Do the same steps to the ones we did for species:
					 		- Fill tables toManys
					 		- Get not found and add them to flat table
					 		- Get corresponds and add them to flat table
					 		- Get includes and add them to flat table
					 		- Get included_by and add them to flat table
					 		- Get overlaps and add them to flat table  */
					this.xMapDAO.fillTablesToMany(xMapId);
					this.xMapDAO.obtainNotFoundInRelationships(xMapId,leftChecklistId,rightChecklistId,currentRank);
					this.xMapDAO.obtainCorrespondRelationships(xMapId,leftChecklistId,rightChecklistId,currentRank);
					this.xMapDAO.obtainIncludesRelationships(xMapId,leftChecklistId,rightChecklistId,currentRank);
					this.xMapDAO.obtainIncludedByRelationships(xMapId,leftChecklistId,rightChecklistId,currentRank);
					this.xMapDAO.obtainOverlapsRelationships(xMapId,leftChecklistId,rightChecklistId,currentRank);						
									
					//If cross-map has strict=true with identify extra taxa, we should take into consideration the possible matches in the previous level 
					if(this.strict && this.identifyExtraTaxa!=IdentifyExtraTaxaType.NONE){
						this.xMapDAO.reevaluateHigherTaxaNotFound(xMapId,leftChecklistId,rightChecklistId,currentRank);
					}					
					
					//Add to the table elementsInCommon the entries in table higherTaxaInCommon where any of the ancestor were null
					this.xMapDAO.addToTableElementsInCommonEntriesWithoutAncestorForCurrentLevel(xMapId);										
				}
				currentRank = getNextTaxonomicRank(currentRank,true);
			}
		}
	}
	
	
	/**
	 * Function responsible to return the following taxonomic rank (the parent) for the taxonomic rank passed as parameter
	 * @param rank id of the taxonomic rank to obtain its parent
	 * @param up direction of the next rank. When up is true we will get the parent of the given rank, when up is false we get the child of the given rank
	 * @return parentRank or null if rank is the top rank
	 */
	private TaxonomicRank getNextTaxonomicRank(TaxonomicRank rank, boolean up) throws DAOException{
		return this.xMapDAO.getNextTaxonomicRank(rank,up);
	}
	
	/**
	 * 
	 * @param rankA
	 * @param rankB
	 * @return a negative number if rankA is inferior to rankB, 0 if they are equals, and a positive number if rankA is superior to rankB
	 */
	private int compareTaxonomicRanks(TaxonomicRank rankA, TaxonomicRank rankB) throws DAOException{
		return this.xMapDAO.compareTaxonomicRanks(rankA, rankB);
	}
	
	
	private List<TaxonomicRank> getTaxonomicRanksInChecklist(String checklistId) throws DAOException{
		return this.xMapDAO.getTaxonomicRanksInChecklist(checklistId).getRank();
	}
	
	
	private void cleanUp(String xMapId) throws DAOException{		
		//Drop temporary table create during the xmap
		this.xMapDAO.dropTemporaryTables(xMapId);			
	}
	
	
    private void updateXMapInRepository(String xMapId) throws DAOException{    	
    	this.xMapDAO.updateXMapInRepository(xMapId,xMapName,description,leftChecklistId,rightChecklistId,strict,identifyExtraTaxa,compareHigherTaxa,highestTaxonomicRankToXmap,userRefinementLevel,user,scope,this.taskId);
    }      
    
	private void deleteContentPreviousXMap(String xMapId) throws DAOException{		
		List<ResultExportXmap> results = this.xMapDAO.getCrossMapsExportResultsByXmap(xMapId).getExportResultXmap();
		for (ResultExportXmap result: results){
			if (result.getStatus()!=TaskStatus.COMPLETED){
				throw new RuntimeException("The crossmap "+ xMapId + " can not be re-run overwriting the previous one because the previous one" +
						" has exportation of its results in progrress. You should first wait for their completion or abort them.");
			}
			else{
				this.xMapDAO.deleteCrossMapResult(result.getId());
			}
		}			
		
		this.xMapDAO.deleteCrossMap(xMapId,false);				
	}
    	
	
}
