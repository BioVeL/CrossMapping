/*
 * #%L
 * XMap Crossmapping DAO
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
package org.openbio.xmap.common.dao.crossmap;

import java.io.IOException;
import java.util.List;

import org.openbio.xmap.common.dao.util.exception.DAOException;
import org.openbio.xmap.common.serviceinterfaces.types.xmap.Checklist;
import org.openbio.xmap.common.serviceinterfaces.types.xmap.ChecklistSeq;
import org.openbio.xmap.common.serviceinterfaces.types.xmap.DataRetrievalFilterSeq;
import org.openbio.xmap.common.serviceinterfaces.types.xmap.DataRetrievalSortSeq;
import org.openbio.xmap.common.serviceinterfaces.types.xmap.EntriesInChecklistSeq;
import org.openbio.xmap.common.serviceinterfaces.types.xmap.EntriesInUserKnowledgeSeq;
import org.openbio.xmap.common.serviceinterfaces.types.xmap.EntriesInXMapSeq;
import org.openbio.xmap.common.serviceinterfaces.types.xmap.EntryInUserKnowledge;
import org.openbio.xmap.common.serviceinterfaces.types.xmap.IdentifyExtraTaxaType;
import org.openbio.xmap.common.serviceinterfaces.types.xmap.RawData;
import org.openbio.xmap.common.serviceinterfaces.types.xmap.RelationshipDetail;
import org.openbio.xmap.common.serviceinterfaces.types.xmap.ResultExportXmap;
import org.openbio.xmap.common.serviceinterfaces.types.xmap.ResultExportXmapSeq;
import org.openbio.xmap.common.serviceinterfaces.types.xmap.TaskId;
import org.openbio.xmap.common.serviceinterfaces.types.xmap.TaskStatus;
import org.openbio.xmap.common.serviceinterfaces.types.xmap.TaxonSeq;
import org.openbio.xmap.common.serviceinterfaces.types.xmap.TaxonomicRank;
import org.openbio.xmap.common.serviceinterfaces.types.xmap.TaxonomicRankSeq;
import org.openbio.xmap.common.serviceinterfaces.types.xmap.UserKnowledgeLevelForRefinement;
import org.openbio.xmap.common.serviceinterfaces.types.xmap.XMap;

import org.openbio.xmap.common.serviceinterfaces.types.xmap.XMapSeq;

import au.com.bytecode.opencsv.CSVWriter;


public interface XMapDAO {

	/****************************************************************************************/
	/* Public methods for XMap																*/														
	/****************************************************************************************/
		
	public void createXMapTables(String xMapId) throws DAOException;
		
	public void fillTableElementsInCommonByNames(String xMapId, String leftChecklistId, String rightChecklistId, boolean strict, UserKnowledgeLevelForRefinement userRefinementLevel) throws DAOException;
	
	public void fillTablesToMany(String xMapId) throws DAOException;
	
	public void obtainNotFoundInRelationships(String xMapId, String leftChecklistId, String rightChecklistId, TaxonomicRank rank) throws DAOException;
		
	public void obtainCorrespondRelationships(String xMapId, String leftChecklistId, String rightChecklistId, TaxonomicRank rank) throws DAOException;
	
	public void obtainIncludesRelationships(String xMapId, String leftChecklistId, String rightChecklistId, TaxonomicRank rank) throws DAOException;
	
	public void obtainIncludedByRelationships(String xMapId, String leftChecklistId, String rightChecklistId, TaxonomicRank rank) throws DAOException;
	
	public void obtainOverlapsRelationships(String xMapId, String leftChecklistId, String rightChecklistId, TaxonomicRank rank) throws DAOException;
		
	public void reevaluateLowerTaxaNotFoundWithPossNameMatch(String xMapId, String leftChecklistId, String rightChecklistId, UserKnowledgeLevelForRefinement userRefinementLevel) throws DAOException;
	
	public void reevaluateLowerTaxaNotFoundWithPossGenTrnfr(String xMapId, String leftChecklistId, String rightChecklistId, UserKnowledgeLevelForRefinement userRefinementLevel) throws DAOException;
		
	public void reevaluateHigherTaxaNotFound(String xMapId, String leftChecklistId, String rightChecklistId, TaxonomicRank rank) throws DAOException;
	
	public void dropTemporaryTables(String xMapId) throws DAOException;
	
	
	public void createTablesForXMapHigherTaxa(String xMapId) throws DAOException;
	public void reFillTableHigherTaxaInCommon(String xMapId, TaxonomicRank rank) throws DAOException;
	public void cleanPreviousXMapCalculationTables(String xMapId) throws DAOException;
	public void fillTableElementsInCommonByCurrentLevel(String xMapId) throws DAOException;
	public void addToTableElementsInCommonEntriesWithoutAncestorForCurrentLevel(String xMapId) throws DAOException;
	
	/****************************************************************************************/
	/* Public methods for Export resutls XMap												*/														
	/****************************************************************************************/	
	
	public void exportFlatTableAdditTaxa(String xMapId, CSVWriter csvWriter, boolean left) throws DAOException, IOException;
	
	public void exportFlatTable(String xMapId, boolean includeAcceptedNames, CSVWriter csvWriter) throws DAOException, IOException;
	
	public void exportFlatTableNameUses(String xMapId, CSVWriter csvWriter) throws DAOException, IOException;
	
	public boolean existXMapNameInRepository(String xMapName, String user, String scope) throws DAOException;
	
	public String addXMapNameToRepository(String xMapName, String description, String leftChecklistId, 
			String rightChecklistId, boolean strict, IdentifyExtraTaxaType identifyExtraTaxa, boolean compareHigherTaxa, 
			TaxonomicRank highestRankToCompare, UserKnowledgeLevelForRefinement userRefinementLevel, String user, String scope, TaskId taskId) throws DAOException;
	
	public void updateXMapInRepository(String xMapId, String xMapName, String description, String leftChecklistId, 
			String rightChecklistId, boolean strict, IdentifyExtraTaxaType identifyExtraTaxa, boolean compareHigherTaxa, 
			TaxonomicRank highestRankToCompare, UserKnowledgeLevelForRefinement userRefinementLevel, String user, String scope, TaskId taskId) throws DAOException;
		
	
	/****************************************************************************************/
	/* Other public methods 																*/														
	/****************************************************************************************/		
	
	public Checklist getChecklist(String checklistId) throws DAOException;	
	public XMap getCrossMap(String xMapId) throws DAOException;	
	public ResultExportXmap getCrossMapResult(String xMapResultId) throws DAOException;
	

	public ChecklistSeq getChecklists(String user, String scope, TaskStatus status) throws DAOException;	
	public XMapSeq getCrossMaps(String user, String scope, TaskStatus status) throws DAOException;	
	public ResultExportXmapSeq getCrossMapResultsExported(String user, String scope, TaskStatus status) throws DAOException;
	
	
	public XMapSeq getCrossMapsByChecklist(String checklistId) throws DAOException;	
	public ResultExportXmapSeq getCrossMapsExportResultsByXmap(String xMapId) throws DAOException;

	public List<String> getTaxonChildrenId (String checklistId,String taxonId) throws DAOException;
	public List<String> getPathTaxonInChecklist (String checklistId,String taxonId) throws DAOException;
	
	public EntriesInChecklistSeq getEntriesInChecklist(String checklistId, DataRetrievalFilterSeq filters, DataRetrievalSortSeq sorts, int start, int end) throws DAOException;		
	public EntriesInChecklistSeq getEntriesInChecklistForTaxon (String checklistId,String taxonId,String status) throws DAOException; 	
		
	public EntriesInXMapSeq getEntriesInXMap(String xMapId, DataRetrievalFilterSeq filters, DataRetrievalSortSeq sorts, int start, int end)  throws DAOException;
	public EntriesInXMapSeq getEntriesInXMapForTaxon (String xMapId,String taxonId,boolean left) throws DAOException; 
	
	public RawData getTaxonRawData (String checklistId,String taxonId)	throws DAOException;	
	public RelationshipDetail getXMapRelationshipDetail(String xMapId, String leftTaxonId, String rightTaxonId) throws DAOException; 	
	
	public TaxonomicRankSeq getTaxonomicRanks() throws DAOException;
	public TaxonomicRank getNextTaxonomicRank(TaxonomicRank rank, boolean up) throws DAOException;
	public TaxonomicRank getTaxonomicRankByName(String rankName) throws DAOException;
	public int compareTaxonomicRanks(TaxonomicRank rankA, TaxonomicRank rankB) throws DAOException;	
	public TaxonomicRankSeq getTaxonomicRanksInChecklist(String checklistId) throws DAOException;
	

	public void deleteImportedChecklist(String checklistId) throws DAOException;	
	public void deleteCrossMap(String xMapId, boolean includingEntryInRepository) throws DAOException;	
	public void deleteCrossMapResult(String xMapResultId) throws DAOException;
	
	
	public boolean existXMapResultInRepository(String xMapId, String user, String scope) throws DAOException;	
	public String addXMapResultToRepository(String name, String xMapId, boolean includeAcceptedNames, String user, String scope, TaskId taskId) throws DAOException;	
	public void updateExportResultCrossmap(String xMapResultId, String resultFileURL) throws DAOException;
	
	
	public EntryInUserKnowledge getEntryInUserKnowledge(String userKnowledgeId) throws DAOException;
	public EntriesInUserKnowledgeSeq getEntriesInUserKnowledge(DataRetrievalFilterSeq filters, DataRetrievalSortSeq sorts, int start, int end)  throws DAOException;	
	public String addEntryInUserKnowledge(EntryInUserKnowledge entry) throws DAOException;
	public void deleteEntryInUserKnowledge(String userKnowledgeId) throws DAOException;
	
					
	public void startTransaction() throws DAOException;
	
	public void commitTransaction() throws DAOException;
	
	public void rollbackTransaction() throws DAOException;
	
}
