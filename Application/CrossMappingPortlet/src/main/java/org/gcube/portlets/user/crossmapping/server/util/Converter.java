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
package org.gcube.portlets.user.crossmapping.server.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Stack;

import org.gcube.portlets.user.crossmapping.shared.ChecklistGWT;
import org.gcube.portlets.user.crossmapping.shared.DataRetrievalFilterGWT;
import org.gcube.portlets.user.crossmapping.shared.DataRetrievalSortGWT;
import org.gcube.portlets.user.crossmapping.shared.EntryInUserKnowledgeGWT;
import org.gcube.portlets.user.crossmapping.shared.EntryInXMapGWT;
import org.gcube.portlets.user.crossmapping.shared.EntryInChecklistGWT;
import org.gcube.portlets.user.crossmapping.shared.ExportationResultGWT;
import org.gcube.portlets.user.crossmapping.shared.IdentifyExtraTaxaTypeGWT;
import org.gcube.portlets.user.crossmapping.shared.NamesRelationshipTypeGWT;
import org.gcube.portlets.user.crossmapping.shared.RawDataFieldGWT;
import org.gcube.portlets.user.crossmapping.shared.RawDataGWT;
import org.gcube.portlets.user.crossmapping.shared.RawDataRegisterGWT;
import org.gcube.portlets.user.crossmapping.shared.RelationshipDetailGWT;
import org.gcube.portlets.user.crossmapping.shared.RelationshipPairTaxaEntryGWT;
import org.gcube.portlets.user.crossmapping.shared.RelationshipPairTaxaGWT;
import org.gcube.portlets.user.crossmapping.shared.TaskProgressGWT;
import org.gcube.portlets.user.crossmapping.shared.TaskStatusGWT;
import org.gcube.portlets.user.crossmapping.shared.TaxonDetailGWT;
import org.gcube.portlets.user.crossmapping.shared.TaxonGWT;
import org.gcube.portlets.user.crossmapping.shared.TaxonomicRankGWT;
import org.gcube.portlets.user.crossmapping.shared.UserKnowledgeLevelForRefinementGWT;
import org.gcube.portlets.user.crossmapping.shared.XMapGWT;
import org.gcube.portlets.user.crossmapping.shared.XMapRelPathGWT;
import org.openbio.xmap.common.serviceinterfaces.types.xmap.Checklist;
import org.openbio.xmap.common.serviceinterfaces.types.xmap.DataRetrievalFilter;
import org.openbio.xmap.common.serviceinterfaces.types.xmap.DataRetrievalFilterType;
import org.openbio.xmap.common.serviceinterfaces.types.xmap.DataRetrievalSort;
import org.openbio.xmap.common.serviceinterfaces.types.xmap.DataRetrievalSortingDirection;
import org.openbio.xmap.common.serviceinterfaces.types.xmap.EntryInChecklist;
import org.openbio.xmap.common.serviceinterfaces.types.xmap.EntryInUserKnowledge;
import org.openbio.xmap.common.serviceinterfaces.types.xmap.EntryInXMap;
import org.openbio.xmap.common.serviceinterfaces.types.xmap.RawData;
import org.openbio.xmap.common.serviceinterfaces.types.xmap.RawDataRegister;
import org.openbio.xmap.common.serviceinterfaces.types.xmap.RelationshipDetail;
import org.openbio.xmap.common.serviceinterfaces.types.xmap.RelationshipPairTaxa;
import org.openbio.xmap.common.serviceinterfaces.types.xmap.RelationshipPairTaxaEntry;
import org.openbio.xmap.common.serviceinterfaces.types.xmap.ResultExportXmap;
import org.openbio.xmap.common.serviceinterfaces.types.xmap.TaskProgress;
import org.openbio.xmap.common.serviceinterfaces.types.xmap.TaskStatus;
import org.openbio.xmap.common.serviceinterfaces.types.xmap.Taxon;
import org.openbio.xmap.common.serviceinterfaces.types.xmap.TaxonDetail;
import org.openbio.xmap.common.serviceinterfaces.types.xmap.TaxonomicRank;
import org.openbio.xmap.common.serviceinterfaces.types.xmap.UserKnowledgeLevelForRefinement;
import org.openbio.xmap.common.serviceinterfaces.types.xmap.XMap;
import org.openbio.xmap.common.serviceinterfaces.types.xmap.RawDataRegister.RawDataField;
import org.openbio.xmap.common.serviceinterfaces.types.xmap.XMapRelPath;


/**
 * @author "Federico De Faveri defaveri@isti.cnr.it"
 * Modification: 10/10/12 F. Quevedo. Changes
 *		 - TaskStatus by TaskStatusGWT
 */
public class Converter {

	protected static final String CHECKLIST_TABLE_NAME_SUFFIX = "_NameUse_vw";
	protected static final String MAP_TABLE_NAME_SUFFIX = "_Flat_vw";

	public static ChecklistGWT convertChecklistObj(Checklist serverChecklist) {   
		if (serverChecklist!=null){
			ChecklistGWT checklist = new ChecklistGWT();
			checklist.setId(serverChecklist.getId());
			checklist.setName(serverChecklist.getName());
			checklist.setTaskStatus(convertStatus(serverChecklist.getStatus()));
			 
			String tableName = serverChecklist.getPrefixTableDB() + CHECKLIST_TABLE_NAME_SUFFIX;
			checklist.setTableName(tableName);
	
			if (serverChecklist.getTaskId()!=null) checklist.setTaskId(serverChecklist.getTaskId().getValue());
			return checklist;
		}
		else{
			return null;
		}				
	}   

	protected static TaskStatusGWT convertStatus(TaskStatus status) {
		return TaskStatusGWT.fromValue(status.value());
		/*
		switch (status) {
			case PENDING: return TaskStatusGWT.PENDING;
			case ACTIVE: return TaskStatusGWT.ACTIVE;
			case COMPLETED: return TaskStatusGWT.COMPLETED;
			case FAILED: return TaskStatusGWT.FAILED;
			case CANCELLING: return TaskStatusGWT.CANCELLING;
		}		
		return null;
		*/
	}

	public static TaskStatus convertStatus(TaskStatusGWT status) {
		if (status!=null){
			return TaskStatus.fromValue(status.value());
		}
		else{
			return null;
		}
		/*
		switch (status) {
			case PENDING: return TaskStatus.PENDING;
			case ACTIVE: return TaskStatus.ACTIVE;
			case COMPLETED: return TaskStatus.COMPLETED;
			case FAILED: return TaskStatus.FAILED;
			case CANCELLING: return TaskStatus.CANCELLING;
		}
		return null;
		*/
	}

	public static List<ChecklistGWT> convertListChecklist(List<Checklist> list){
		List<ChecklistGWT> listGWT = new ArrayList<ChecklistGWT>();
		for (Checklist checklist:list){
			listGWT.add(convertChecklistObj(checklist));
		}
		return listGWT;
	}

	public static TaskProgressGWT convertTaskProgressObj(TaskProgress serverObj){
		if (serverObj!=null){
			TaskProgressGWT taskProgressGWT = new TaskProgressGWT();
			taskProgressGWT.setTaskName(serverObj.getTaskName());
			//TODO taskProgressGWT.setType(serverObj.getType());
			taskProgressGWT.setStatus(convertStatus(serverObj.getStatus()));
			taskProgressGWT.setDetails(serverObj.getDetails());
			taskProgressGWT.setPercentage(serverObj.getPercentage());
			taskProgressGWT.setStartDate(serverObj.getStartDate());
			taskProgressGWT.setFinishDate(serverObj.getFinishDate());
	
			/*if (serverObj.getTaskResponse()!=null){
	    		taskProgressGWT.setTaskResponse(convertTaskResponse(serverObj.getTaskResponse()));
	    	}*/
	
			/*if (serverObj.getSubTasksProgress()!=null){
		    	for (TaskProgress subTaskProgressServer: serverObj.getSubTasksProgress()){
		    		taskProgressGWT.getSubTasksProgress().add(convertTaskProgressObj(subTaskProgressServer));
		        }    		
	    	}*/
			return taskProgressGWT;
		}
		else{
			return null;
		}			
	}

	public static XMapGWT convertXMapObj(XMap serverXMap) {
		if (serverXMap!=null){
			XMapGWT xmap = new XMapGWT();
			xmap.setId(serverXMap.getId());
			xmap.setShortName(serverXMap.getShortName());
			xmap.setLongName(serverXMap.getLongName());
			xmap.setLeftChecklistName(serverXMap.getLeftChecklistName());
			xmap.setLeftChecklistId(serverXMap.getLeftChecklistId());
			xmap.setRightChecklistName(serverXMap.getRightChecklistName());
			xmap.setRightChecklistId(serverXMap.getRightChecklistId());
			xmap.setStrict(serverXMap.isStrict());
			if (serverXMap.getIdentifyExtraTaxa()!=null){
				xmap.setIdentifyExtraTaxa(IdentifyExtraTaxaTypeGWT.valueOf(serverXMap.getIdentifyExtraTaxa().toString()));
			}		
			xmap.setCompareHigherTaxa(serverXMap.isCompareHigherTaxa());
			if (serverXMap.getHigherRankToCompare()!=null){
				xmap.setHighestRankToCompare(convertTaxonomicRankObj(serverXMap.getHigherRankToCompare()));
			}
			
			
			xmap.setStatus(convertStatus(serverXMap.getStatus()));
			
			String tableName = serverXMap.getPrefixTableDB() + MAP_TABLE_NAME_SUFFIX;
			xmap.setTableName(tableName);
	
			if (serverXMap.getTaskId()!=null) xmap.setTaskId(serverXMap.getTaskId().getValue());
	
			return xmap;
		}
		else{
			return null;
		}			
	} 

	public static List<XMapGWT> convertListXMap(List<XMap> list){
		List<XMapGWT> listGWT = new ArrayList<XMapGWT>();
		for (XMap xmap:list){
			listGWT.add(convertXMapObj(xmap));
		}
		return listGWT;
	}


	public static ExportationResultGWT convertResultExportXMapObj(ResultExportXmap serverResult) {    	
		if (serverResult!=null){
			ExportationResultGWT exportationResult = new ExportationResultGWT();
			exportationResult.setId(serverResult.getId());
			exportationResult.setName(serverResult.getName());
			exportationResult.setxMapId(serverResult.getXMapId());
			exportationResult.setIncludeAcceptedName(serverResult.isIncludeAcceptedName()); 
			exportationResult.setStatus(convertStatus(serverResult.getStatus()));    	
			exportationResult.setResultFileURL(serverResult.getResultFileURL());
			exportationResult.setExportDate(new Date(serverResult.getExportDate()));
	
			if (serverResult.getTaskId()!=null) exportationResult.setTaskId(serverResult.getTaskId().getValue());
	
			return exportationResult;
		}
		else{
			return null;
		}			
	}       




	public static List<ExportationResultGWT> convertListResultExportXMap(List<ResultExportXmap> list) {
		List<ExportationResultGWT> listGWT = new ArrayList<ExportationResultGWT>();
		for (ResultExportXmap resultExportXMap:list){
			listGWT.add(convertResultExportXMapObj(resultExportXMap));
		}
		return listGWT;		
	}    

	/* public static TaskResponseGWT convertTaskResponse(TaskResponse serverObj){
    	if (serverObj instanceof ImportChecklistTaskResponse){
    		ImportChecklistTaskResponseGWT taskResponse = new ImportChecklistTaskResponseGWT();
    		taskResponse.setImportedChecklistId(((ImportChecklistTaskResponse) serverObj).getImportedChecklistId());    		
    		return taskResponse;
    	}
    	else if (serverObj instanceof DoXmapTaskResponse){
    		DoXmapTaskResponseGWT taskResponse = new DoXmapTaskResponseGWT();
    		taskResponse.setCompletedCrossmapId(((DoXmapTaskResponse) serverObj).getCompletedCrossmapId());    		
    		return taskResponse;

    	}
    	else if(serverObj instanceof ExportXmapTaskResponse){
    		ExportXmapTaskResponseGWT taskResponse = new ExportXmapTaskResponseGWT();
    		taskResponse.setXmapResultFile(((ExportXmapTaskResponse) serverObj).getXmapResultFile());
    		return taskResponse;
    	}
    	else{
    		throw new RuntimeException("Task response type not supported");
    	}
    }*/
	
	
	public static List<TaxonomicRankGWT> convertListTaxonomicRanks(List<TaxonomicRank> list) {
		List<TaxonomicRankGWT> listGWT = new ArrayList<TaxonomicRankGWT>();
		for (TaxonomicRank taxonomicRank:list){
			listGWT.add(convertTaxonomicRankObj(taxonomicRank));
		}
		return listGWT;		
	}   
	
	public static TaxonomicRankGWT convertTaxonomicRankObj(TaxonomicRank rank){
		if (rank!=null){
			TaxonomicRankGWT rankGWT = new TaxonomicRankGWT();
			rankGWT.setId(rank.getId());
			rankGWT.setName(rank.getName());		
			
			if (rank.getParentId()!=null) rankGWT.setParentId(rank.getParentId());
			
			rankGWT.setIsHigherRank(rank.isIsHigherRank());
	
			return rankGWT;
		}
		else{
			return null;
		}
	}
	
	public static TaxonomicRank convertTaxonomicRankObj(TaxonomicRankGWT rankGWT){
		if (rankGWT!=null){
			TaxonomicRank rank = new TaxonomicRank();
			rank.setId(rankGWT.getId());
			rank.setName(rankGWT.getName());		
			
			if (rankGWT.getParentId()!=null) rank.setParentId(rankGWT.getParentId());
			
			rank.setIsHigherRank(rankGWT.getIsHigherRank());
	
			return rank;
		}
		else{
			return null;
		}
	}

	
	public static TaxonDetailGWT convertTaxonDetailObj(TaxonDetail taxonDetail){
		if (taxonDetail!=null){
			TaxonDetailGWT taxonDetailGWT = new TaxonDetailGWT();		
			taxonDetailGWT.setAcceptedName(Converter.convertEntryInChecklistObj(taxonDetail.getAcceptedName()));
			taxonDetailGWT.setSynonyms(Converter.convertListEntryInChecklist(taxonDetail.getSynonym()));		
			taxonDetailGWT.setParent(Converter.convertEntryInChecklistObj(taxonDetail.getParent()));
			taxonDetailGWT.setChildren(Converter.convertListEntryInChecklist(taxonDetail.getChild()));
			taxonDetailGWT.setRawData(Converter.convertRawDataObj(taxonDetail.getRawData()));				
			return taxonDetailGWT;
		}
		else{
			return null;
		}
	}
	
	public static RelationshipDetailGWT convertRelationshipDetailObj (RelationshipDetail relationshipDetail){
		if (relationshipDetail!=null){
			RelationshipDetailGWT relationshipDetailGWT = new RelationshipDetailGWT();
			relationshipDetailGWT.setDirectRelationship(Converter.convertRelationshipPairTaxaObj(relationshipDetail.getDirectRelationship()));
			relationshipDetailGWT.setIndirectRelationshipsLeft2Right(Converter.convertListRelationshipPairTaxa(relationshipDetail.getIndirectRelationshipLeft2Right()));
			relationshipDetailGWT.setIndirectRelationshipsRight2Left(Converter.convertListRelationshipPairTaxa(relationshipDetail.getIndirectRelationshipRight2Left()));		
			return relationshipDetailGWT;
		}
		else{
			return null;
		}
	}
	
	
	private static RelationshipPairTaxaGWT convertRelationshipPairTaxaObj (RelationshipPairTaxa relationshipPairTaxa){
		if (relationshipPairTaxa!=null){
			RelationshipPairTaxaGWT relationshipPairTaxaGWT = new RelationshipPairTaxaGWT();
			relationshipPairTaxaGWT.setLeftTaxonId(relationshipPairTaxa.getLeftTaxonId());
			relationshipPairTaxaGWT.setLeftTaxonName(relationshipPairTaxa.getLeftTaxonName());
			relationshipPairTaxaGWT.setLeftTaxonRank(relationshipPairTaxa.getLeftTaxonRank());
			relationshipPairTaxaGWT.setRightTaxonId(relationshipPairTaxa.getRightTaxonId());
			relationshipPairTaxaGWT.setRightTaxonName(relationshipPairTaxa.getRightTaxonName());
			relationshipPairTaxaGWT.setRightTaxonRank(relationshipPairTaxa.getRightTaxonRank());
			relationshipPairTaxaGWT.setRelationship(relationshipPairTaxa.getRelationship());
			
			List<RelationshipPairTaxaEntryGWT> entriesGWT = new ArrayList<RelationshipPairTaxaEntryGWT>();
			for (RelationshipPairTaxaEntry entry: relationshipPairTaxa.getElementRel()){
				RelationshipPairTaxaEntryGWT entryGWT = new RelationshipPairTaxaEntryGWT();
				entryGWT.setTaxId1(entry.getTaxId1());
				entryGWT.setElement1(entry.getElement1());
				entryGWT.setExtra1(entry.getExtra1());
				entryGWT.setRelType(entry.getRelType());
				entryGWT.setTaxId2(entry.getTaxId2());
				entryGWT.setElement2(entry.getElement2());
				entryGWT.setExtra2(entry.getExtra2());
				entryGWT.setInOthers(entry.isInOthers());
				entryGWT.setNameLevel(entry.isNameLevel());
				
				entriesGWT.add(entryGWT);
			}
			relationshipPairTaxaGWT.setElementsRel(entriesGWT);
			return relationshipPairTaxaGWT;
		}
		else{
			return null;
		}
	}
	
	private static List<RelationshipPairTaxaGWT> convertListRelationshipPairTaxa (List<RelationshipPairTaxa> relationshipsPairTaxa){
		List<RelationshipPairTaxaGWT> relsPairTaxaGWT = new ArrayList<RelationshipPairTaxaGWT>();
		for (RelationshipPairTaxa relPairTaxa: relationshipsPairTaxa){
			relsPairTaxaGWT.add(Converter.convertRelationshipPairTaxaObj(relPairTaxa));
		}
		return relsPairTaxaGWT;		
	}	
	
	private static EntryInChecklistGWT convertEntryInChecklistObj (EntryInChecklist entryInCheckList){
		if (entryInCheckList!=null){
			EntryInChecklistGWT entryInChecklistGWT = new EntryInChecklistGWT();		
			entryInChecklistGWT.setTaxonId(entryInCheckList.getTaxonId());
			entryInChecklistGWT.setTidyName(entryInCheckList.getTidyName());
			entryInChecklistGWT.setStatus(entryInCheckList.getStatus());
			entryInChecklistGWT.setHigher(entryInCheckList.getHigher());
			entryInChecklistGWT.setSpecies(entryInCheckList.getSpecies());
			entryInChecklistGWT.setInfraspecies(entryInCheckList.getInfraspecies());
			entryInChecklistGWT.setAuthority(entryInCheckList.getAuthority());
			entryInChecklistGWT.setRank(entryInCheckList.getRank());
			entryInChecklistGWT.setParentId(entryInCheckList.getParentTaxonId());
			return entryInChecklistGWT;
		}
		else{
			return null;
		}
	}
	
	public static List<EntryInChecklistGWT> convertListEntryInChecklist (List<EntryInChecklist> entriesInCheckList){
		List<EntryInChecklistGWT> entriesGWT = new ArrayList<EntryInChecklistGWT>();
		for (EntryInChecklist entry: entriesInCheckList){
			entriesGWT.add(Converter.convertEntryInChecklistObj(entry));
		}
		return entriesGWT;
	}
	
	private static RawDataGWT convertRawDataObj(RawData rawData){
		if (rawData!=null){
			RawDataGWT rawDataGWT = new RawDataGWT();
			List<RawDataRegisterGWT> registersGWT = new ArrayList<RawDataRegisterGWT>();
			for (RawDataRegister register: rawData.getRegisters().getRegister()){
				RawDataRegisterGWT registerGWT = new RawDataRegisterGWT();
				List<RawDataFieldGWT> dataFieldsGWT = new ArrayList<RawDataFieldGWT>();
				for (RawDataField dataField: register.getRawDataField()){
					RawDataFieldGWT dataFieldGWT = new RawDataFieldGWT();
					dataFieldGWT.setName(dataField.getName());
					dataFieldGWT.setValue(dataField.getValue());
					dataFieldsGWT.add(dataFieldGWT);
				}
				registerGWT.setRawDataFields(dataFieldsGWT);
				registersGWT.add(registerGWT);
			}
			rawDataGWT.setRegisters(registersGWT);
			return rawDataGWT;
		}
		else{
			return null;
		}
	}
	
	
	private static EntryInXMapGWT convertEntryInXMapObj (EntryInXMap entryInXMap){
		if (entryInXMap!=null){
			EntryInXMapGWT entryInXMapGWT = new EntryInXMapGWT();	
			entryInXMapGWT.setTaxonIdLeft(entryInXMap.getTaxonIdLeft());
			entryInXMapGWT.setRankLeft(entryInXMap.getRankLeft());
			entryInXMapGWT.setAcceptedNameLeft(entryInXMap.getAcceptedNameLeft());	
			entryInXMapGWT.setUuidLeft(entryInXMap.getUuidLeft());
			entryInXMapGWT.setChecklistNameLeft(entryInXMap.getChecklistNameLeft());		
			entryInXMapGWT.setRelationship(entryInXMap.getRelationship());
			entryInXMapGWT.setTaxonIdRight(entryInXMap.getTaxonIdRight());
			entryInXMapGWT.setRankRight(entryInXMap.getRankRight());
			entryInXMapGWT.setAcceptedNameRight(entryInXMap.getAcceptedNameRight());
			entryInXMapGWT.setUuidRight(entryInXMap.getUuidRight());
			entryInXMapGWT.setChecklistNameRight(entryInXMap.getChecklistNameRight());
						
			return entryInXMapGWT;
		}
		else{
			return null;
		}
	}
	
	public static List<EntryInXMapGWT> convertListEntryInXMap (List<EntryInXMap> entriesInXMap){
		List<EntryInXMapGWT> entriesGWT = new ArrayList<EntryInXMapGWT>();
		for (EntryInXMap entry: entriesInXMap){
			entriesGWT.add(Converter.convertEntryInXMapObj(entry));
		}
		return entriesGWT;
	}			
	
	private static DataRetrievalFilter convertDataRetrivalFilterGWT (DataRetrievalFilterGWT filterGWT){
		if (filterGWT!=null){
			DataRetrievalFilter filter = new DataRetrievalFilter();
			filter.setColumn(filterGWT.getColumn());
			filter.setType(DataRetrievalFilterType.fromValue(filterGWT.getType().value()));
			filter.setValue(filterGWT.getValue());
			return filter;
		}
		else{
			return null;
		}
	}
	
	public static List<DataRetrievalFilter> convertListDataRetrivalFilterGWT (List<DataRetrievalFilterGWT> filtersGWT){
		List<DataRetrievalFilter> filters = new ArrayList<DataRetrievalFilter>();
		for (DataRetrievalFilterGWT filterGWT: filtersGWT){
			filters.add(Converter.convertDataRetrivalFilterGWT(filterGWT));
		}
		return filters;
	}		
	
	
	private static DataRetrievalSort convertDataRetrivalFilterGWT (DataRetrievalSortGWT sortGWT){
		if (sortGWT!=null){
			DataRetrievalSort sort = new DataRetrievalSort();		
			sort.setColumn(sortGWT.getColumn());
			sort.setDirection(DataRetrievalSortingDirection.fromValue(sortGWT.getDirection().value()));		
			return sort;
		}
		else{
			return null;
		}			
	}	
	
	
	public static List<DataRetrievalSort> convertListDataRetrivalSortGWT (List<DataRetrievalSortGWT> sortsGWT){
		List<DataRetrievalSort> sorts = new ArrayList<DataRetrievalSort>();
		for (DataRetrievalSortGWT sortGWT: sortsGWT){
			sorts.add(Converter.convertDataRetrivalFilterGWT(sortGWT));
		}
		return sorts;
	}	
	
	
	private static TaxonGWT convertTaxonObj (Taxon taxon){
		if (taxon!=null){
			TaxonGWT taxonGWT = new TaxonGWT();
			taxonGWT.setChecklistId(taxon.getChecklistId());
			taxonGWT.setTaxonId(taxon.getTaxonId());
			taxonGWT.setRank(taxon.getRank());
			taxonGWT.setStatus(taxon.getStatus());
			taxonGWT.setAcceptedName(taxon.getAcceptedName());
			taxonGWT.setSynonyms(taxon.getSynonyms());
			taxonGWT.setParentId(taxon.getParentId());
			taxonGWT.setChildrenId(taxon.getChildrenId());		
			taxonGWT.setXmapRels(convertListXMapRelPath(taxon.getXmapRels()));
			return taxonGWT;
		}
		else{
			return null;
		}			
	}
	
	public static List<TaxonGWT> convertListTaxon (List<Taxon> taxa){
		List<TaxonGWT> taxaGWT = new ArrayList<TaxonGWT>();
		for (Taxon taxon: taxa){
			taxaGWT.add(Converter.convertTaxonObj(taxon));
		}
		return taxaGWT;
	}	
	
	
	private static XMapRelPathGWT convertXMapRelPathObj (XMapRelPath rel){
		if (rel!=null){
			XMapRelPathGWT relGWT = new XMapRelPathGWT();
			relGWT.setRelationship(rel.getRelationship());	
			relGWT.setOtherTaxon(convertTaxonObj(rel.getOtherTaxon()));
			List<String> path = rel.getPath();
			if (path.size()>0){
				path.remove(path.size()-1);
			}
			Stack<String> pathGWT = new Stack<String>();
			pathGWT.addAll(path);
			relGWT.setPath(pathGWT);
			return relGWT;
		}
		else{
			return null;
		}			
	}
	
	
	private static List<XMapRelPathGWT> convertListXMapRelPath (List<XMapRelPath> rels){
		List<XMapRelPathGWT> relsGWT = new ArrayList<XMapRelPathGWT>();
		for (XMapRelPath rel: rels){
			relsGWT.add(Converter.convertXMapRelPathObj(rel));
		}
		return relsGWT;
	}	
	
	
	protected static UserKnowledgeLevelForRefinementGWT convertUserRefinement(UserKnowledgeLevelForRefinement userRefinement) {
		return UserKnowledgeLevelForRefinementGWT.fromValue(userRefinement.value());		
	}
	
	public static UserKnowledgeLevelForRefinement convertUserRefinement(UserKnowledgeLevelForRefinementGWT userRefinement) {
		if (userRefinement!=null){
			return UserKnowledgeLevelForRefinement.fromValue(userRefinement.value());
		}
		else{
			return null;
		}		
	}	
	
	
	private static EntryInUserKnowledgeGWT convertEntryInUserKnowledgeObj (EntryInUserKnowledge serverObj){
		if (serverObj!=null){
			EntryInUserKnowledgeGWT clientObj = new EntryInUserKnowledgeGWT();	
			clientObj.setId(serverObj.getId());
			clientObj.setName1(serverObj.getName1());
			clientObj.setRelationship(NamesRelationshipTypeGWT.fromValue(serverObj.getRelationship().value()));
			clientObj.setName2(serverObj.getName2());
			clientObj.setxMapId(serverObj.getXMapId());
			clientObj.setxMapName(serverObj.getXMapName());
			clientObj.setUser(serverObj.getUser());
			clientObj.setScope(serverObj.getScope());
			clientObj.setComment(serverObj.getComment());
			return clientObj;
		}
		else{
			return null;
		}
	}
	
	public static List<EntryInUserKnowledgeGWT> convertListEntryInUserKnowledge (List<EntryInUserKnowledge> entriesInUserKnowledge){
		List<EntryInUserKnowledgeGWT> entriesGWT = new ArrayList<EntryInUserKnowledgeGWT>();
		for (EntryInUserKnowledge entry: entriesInUserKnowledge){
			entriesGWT.add(Converter.convertEntryInUserKnowledgeObj(entry));
		}
		return entriesGWT;
	}			
	
	
}
