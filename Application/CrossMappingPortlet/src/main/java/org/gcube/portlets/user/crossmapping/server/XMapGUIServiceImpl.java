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
package org.gcube.portlets.user.crossmapping.server;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.sencha.gxt.data.shared.SortDir;
import com.sencha.gxt.data.shared.SortInfo;
import com.sencha.gxt.data.shared.loader.FilterConfig;
import com.sencha.gxt.data.shared.loader.FilterPagingLoadConfig;
import com.sencha.gxt.data.shared.loader.PagingLoadResult;
import com.sencha.gxt.data.shared.loader.PagingLoadResultBean;

import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.activation.DataHandler;
import javax.servlet.http.HttpSession;
import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.WebServiceFeature;
import javax.xml.ws.soap.MTOMFeature;
import org.gcube.application.framework.core.session.ASLSession;
import org.gcube.common.core.resources.GCUBERuntimeResource;
import org.gcube.common.core.resources.runtime.AccessPoint;
import org.gcube.portlets.user.crossmapping.client.rpc.XMapGUIService;
import org.gcube.portlets.user.crossmapping.server.util.Converter;
import org.gcube.portlets.user.crossmapping.server.util.InputStreamDataSource;
import org.gcube.portlets.user.crossmapping.server.util.Util;
import org.gcube.portlets.user.crossmapping.shared.ChecklistGWT;
import org.gcube.portlets.user.crossmapping.shared.EntryInChecklistGWT;
import org.gcube.portlets.user.crossmapping.shared.EntryInXMapGWT;
import org.gcube.portlets.user.crossmapping.shared.ExportationResultGWT;
import org.gcube.portlets.user.crossmapping.shared.IdentifyExtraTaxaTypeGWT;
import org.gcube.portlets.user.crossmapping.shared.RelationshipDetailGWT;
import org.gcube.portlets.user.crossmapping.shared.TaskProgressGWT;
import org.gcube.portlets.user.crossmapping.shared.TaskStatusGWT;
import org.gcube.portlets.user.crossmapping.shared.TaxonDetailGWT;
import org.gcube.portlets.user.crossmapping.shared.TaxonGWT;
import org.gcube.portlets.user.crossmapping.shared.TaxonomicRankGWT;
import org.gcube.portlets.user.crossmapping.shared.UserKnowledgeLevelForRefinementGWT;
import org.gcube.portlets.user.crossmapping.shared.XMapGUIException;
import org.gcube.portlets.user.crossmapping.shared.XMapGWT;
import org.gcube.portlets.user.homelibrary.home.HomeLibrary;
import org.gcube.portlets.user.homelibrary.home.workspace.Workspace;
import org.gcube.portlets.user.homelibrary.home.workspace.WorkspaceFolder;
import org.gcube.portlets.user.homelibrary.home.workspace.WorkspaceItem;
import org.gcube.portlets.user.homelibrary.home.workspace.folder.items.ExternalFile;
import org.openbio.xmap.common.serviceinterfaces.services.xmap.XMapService;
import org.openbio.xmap.common.serviceinterfaces.services.xmap.XMapServiceFault_Exception;
import org.openbio.xmap.common.serviceinterfaces.services.xmap.XMapServiceWS;
import org.openbio.xmap.common.serviceinterfaces.types.xmap.Checklist;
import org.openbio.xmap.common.serviceinterfaces.types.xmap.DataRetrievalFilter;
import org.openbio.xmap.common.serviceinterfaces.types.xmap.DataRetrievalFilterSeq;
import org.openbio.xmap.common.serviceinterfaces.types.xmap.DataRetrievalFilterType;
import org.openbio.xmap.common.serviceinterfaces.types.xmap.DataRetrievalSort;
import org.openbio.xmap.common.serviceinterfaces.types.xmap.DataRetrievalSortSeq;
import org.openbio.xmap.common.serviceinterfaces.types.xmap.DataRetrievalSortingDirection;
import org.openbio.xmap.common.serviceinterfaces.types.xmap.EntriesInChecklistSeq;
import org.openbio.xmap.common.serviceinterfaces.types.xmap.EntriesInXMapSeq;
import org.openbio.xmap.common.serviceinterfaces.types.xmap.IdentifyExtraTaxaType;
import org.openbio.xmap.common.serviceinterfaces.types.xmap.RelationshipDetail;
import org.openbio.xmap.common.serviceinterfaces.types.xmap.ResultExportXmap;
import org.openbio.xmap.common.serviceinterfaces.types.xmap.TaskHandlerExportXmap;
import org.openbio.xmap.common.serviceinterfaces.types.xmap.TaskHandlerImportChecklist;
import org.openbio.xmap.common.serviceinterfaces.types.xmap.TaskHandlerRunXmap;
import org.openbio.xmap.common.serviceinterfaces.types.xmap.TaskId;
import org.openbio.xmap.common.serviceinterfaces.types.xmap.TaskProgress;
import org.openbio.xmap.common.serviceinterfaces.types.xmap.Taxon;
import org.openbio.xmap.common.serviceinterfaces.types.xmap.TaxonDetail;
import org.openbio.xmap.common.serviceinterfaces.types.xmap.TaxonomicRank;
import org.openbio.xmap.common.serviceinterfaces.types.xmap.UserKnowledgeLevelForRefinement;
import org.openbio.xmap.common.serviceinterfaces.types.xmap.XMap;

/**
 * @author "Federico De Faveri defaveri@isti.cnr.it"
 * Modification: 10/10/12 F. Quevedo. Changes
 *		 - Write log messages using logging framework
 *		 - How errors are shown to the users
 */
public class XMapGUIServiceImpl extends RemoteServiceServlet implements XMapGUIService {


	private static Logger logger =  Logger.getLogger(XMapGUIServiceImpl.class.getName());

	private static final long serialVersionUID = 1755085692669095249L;

	
	/****************************************************************************************/
	/* PUBLIC METHODS																		*/														
	/****************************************************************************************/	

	public List<ChecklistGWT> getChecklists(TaskStatusGWT filter) throws XMapGUIException {
		logger.log(Level.FINER, "getChecklists filter: " + filter);
		try {
			ASLSession session = getCurrentSession();

			long start = System.currentTimeMillis();
			List<Checklist> list = getXMapService().getChecklistsImported(session.getUsername(), session.getScope().toString(), Converter.convertStatus(filter)).getChecklist();
			List<ChecklistGWT> checklists = Converter.convertListChecklist(list);

			logger.log(Level.FINER, "checklists " + checklists.size() + " ready in " + (System.currentTimeMillis() - start));
			return checklists;
		}
		catch (XMapServiceFault_Exception e){
			logger.log(Level.SEVERE, "Error on getChecklists", e);
			throw new XMapGUIException(e.getFaultInfo().getUserData(),e.getFaultInfo().getIdException());
		}
		catch (Exception e) {
			logger.log(Level.SEVERE, "Error on getChecklists", e);
			throw new XMapGUIException(e.getMessage());
		}
	}

	public List<XMapGWT> getCrossMaps() throws XMapGUIException {
		try {
			ASLSession session = getCurrentSession();

			List<XMap> list = getXMapService().getCrossMaps(session.getUsername(), session.getScope().toString(),null).getXMap();
			return Converter.convertListXMap(list);
		} 
		catch (XMapServiceFault_Exception e){
			logger.log(Level.SEVERE, "Error on getCrossMaps", e);
			throw new XMapGUIException(e.getFaultInfo().getUserData(),e.getFaultInfo().getIdException());
		}		
		catch (Exception e) {
			logger.log(Level.SEVERE, "Error on getCrossMaps", e);
			throw new XMapGUIException(e.getMessage());
		}
	}

	public List<ExportationResultGWT> getExportationResults() throws XMapGUIException {
		try {
			ASLSession session = getCurrentSession();

			List<ResultExportXmap> list = getXMapService().getCrossMapResultsExported(session.getUsername(), session.getScope().toString(),null).getExportResultXmap();
			return Converter.convertListResultExportXMap(list);
		} 
		catch (XMapServiceFault_Exception e){
			logger.log(Level.SEVERE,"Error on getExportationResults", e);
			throw new XMapGUIException(e.getFaultInfo().getUserData(),e.getFaultInfo().getIdException());
		}		
		catch (Exception e) {
			logger.log(Level.SEVERE,"Error on getExportationResults", e);
			throw new XMapGUIException(e.getMessage());
		}
	}

	public TaskProgressGWT getTaskProgress(String taskId) throws XMapGUIException {
		logger.log(Level.FINE, "getTaskProgress taskId: " + taskId);
		try {
			ASLSession session = getCurrentSession();
			TaskProgress taskProgress = getXMapService().getTaskProgress(new TaskId(taskId),session.getUsername(), session.getScope().toString());
			return Converter.convertTaskProgressObj(taskProgress);
		} 
		catch (XMapServiceFault_Exception e){
			logger.log(Level.SEVERE,"Error on getChecklists", e);
			throw new XMapGUIException(e.getFaultInfo().getUserData(),e.getFaultInfo().getIdException());
		}		
		catch (Exception e) {
			logger.log(Level.SEVERE,"Error on getTaskProgress", e);
			throw new XMapGUIException(e.getMessage());
		}
	}

	public void cancelTask(String taskId) throws XMapGUIException {
		logger.log(Level.FINE, "cancelTask taskId: " + taskId);
		try {
			ASLSession session = getCurrentSession();
			getXMapService().cancelTask(new TaskId(taskId),session.getUsername(), session.getScope().toString());
		} 
		catch (XMapServiceFault_Exception e){
			logger.log(Level.SEVERE,"Error on cancelTask", e);
			throw new XMapGUIException(e.getFaultInfo().getUserData(),e.getFaultInfo().getIdException());
		}		
		catch (Exception e) {
			logger.log(Level.SEVERE,"Error on cancelTask", e);
			throw new XMapGUIException(e.getMessage());
		}
	}

	public void deleteChecklistImported(String checklistId) throws XMapGUIException {
		logger.log(Level.FINE, "deleteChecklistImported checklistId: " + checklistId);
		try {
			ASLSession session = getCurrentSession();
			getXMapService().deleteChecklistImported(checklistId,session.getUsername(), session.getScope().toString());
		}
		catch (XMapServiceFault_Exception e){
			logger.log(Level.SEVERE,"Error on deleteChecklistImported", e);
			throw new XMapGUIException(e.getFaultInfo().getUserData(),e.getFaultInfo().getIdException());
		}		
		catch (Exception e) {
			logger.log(Level.SEVERE,"Error on deleteChecklistImported", e);
			throw new XMapGUIException(e.getMessage());
		}
	}

	public void importChecklist(String workspaceItemId, String checklistName) throws XMapGUIException {
		logger.log(Level.FINE, "importChecklist workspaceItemId: " + workspaceItemId + " checklistName: " + checklistName);
		try {
			ASLSession session = getCurrentSession();

			Workspace userWorkspace = HomeLibrary.getUserWorkspace(session);
			WorkspaceItem workspaceItem = userWorkspace.getItem(workspaceItemId);

			ExternalFile checkListFile = (ExternalFile)workspaceItem;
			InputStream is = checkListFile.getData();

			String fileName = workspaceItem.getName();
			String fname = "";
			String ext = "";
			int mid = fileName.lastIndexOf(".");
			fname = fileName.substring(0, mid);
			ext = fileName.substring(mid, fileName.length());
			fileName = fname + Util.generatePseudoId() + ext;

			DataHandler binaryData = new DataHandler(new InputStreamDataSource(is));
			getXMapService().uploadChecklistFile(fileName, binaryData, session.getUsername(), session.getScope().toString());
			is.close();

			TaskHandlerImportChecklist taskHandler = getXMapService().importChecklist(fileName, "DwC-CoL", checklistName, session.getUsername(), session.getScope().toString());

			logger.log(Level.FINE, "Task id: " + taskHandler.getTaskId().getValue());
		}
		catch (XMapServiceFault_Exception e){
			logger.log(Level.SEVERE,"Error on importChecklist", e);
			throw new XMapGUIException(e.getFaultInfo().getUserData(),e.getFaultInfo().getIdException());
		}		
		catch (Exception e) {
			logger.log(Level.SEVERE,"Error on importChecklist", e);
			throw new XMapGUIException(e.getMessage());
		}
	}

	public void deleteCompletedCrossMap(String xMapId) throws XMapGUIException {
		logger.log(Level.FINE, "deleteCompletedCrossMap xMapId: " + xMapId);
		try {
			ASLSession session = getCurrentSession();
			getXMapService().deleteCrossMap(xMapId,session.getUsername(), session.getScope().toString());
		}
		catch (XMapServiceFault_Exception e){
			logger.log(Level.SEVERE,"Error on deleteCompletedCrossMap", e);
			throw new XMapGUIException(e.getFaultInfo().getUserData(),e.getFaultInfo().getIdException());
		}		
		catch (Exception e) {
			logger.log(Level.SEVERE,"Error on deleteCompletedCrossMap", e);
			throw new XMapGUIException(e.getMessage());
		}
	}


	public void runXmap(String xmapName, String description, String leftChecklistId, 
			String rightChecklistId, boolean strict, IdentifyExtraTaxaTypeGWT identifyExtraTaxa, 
			boolean compareHigherTaxa, TaxonomicRankGWT highestTaxonomicRankToCompare, UserKnowledgeLevelForRefinementGWT userRefinement) throws XMapGUIException {

		logger.log(Level.FINE, "runXmap xmapName: " + xmapName + " description: " + description + " leftChecklistId: " + leftChecklistId + " rightChecklistId: " + rightChecklistId + " strict: " + strict + " identifyExtraTaxa: " + identifyExtraTaxa);
		try {
			ASLSession session = getCurrentSession();

			TaskHandlerRunXmap taskHandler = getXMapService().runXmap(xmapName, description, leftChecklistId, rightChecklistId, 
					strict, IdentifyExtraTaxaType.fromValue(identifyExtraTaxa.toString()), compareHigherTaxa, 
					Converter.convertTaxonomicRankObj(highestTaxonomicRankToCompare), Converter.convertUserRefinement(userRefinement),
					session.getUsername(), session.getScope().toString());

			logger.log(Level.FINE, "task id: " + taskHandler.getTaskId().getValue());
		}
		catch (XMapServiceFault_Exception e){
			logger.log(Level.SEVERE,"Error on runXmap", e);
			throw new XMapGUIException(e.getFaultInfo().getUserData(),e.getFaultInfo().getIdException());
		}		
		catch (Exception e) {
			logger.log(Level.SEVERE,"Error on runXmap", e);
			throw new XMapGUIException(e.getMessage());
		}
	}

	public void deleteCrossMapResultsExported(String xMapResultId) throws XMapGUIException {
		logger.log(Level.FINE, "deleteCrossMapResultsExported xMapResultId: " + xMapResultId);
		try {
			ASLSession session = getCurrentSession();
			getXMapService().deleteCrossMapResultsExported(xMapResultId,session.getUsername(), session.getScope().toString());
		}
		catch (XMapServiceFault_Exception e){
			logger.log(Level.SEVERE,"Error on deleteCrossMapResultsExported", e);
			throw new XMapGUIException(e.getFaultInfo().getUserData(),e.getFaultInfo().getIdException());
		}		
		catch (Exception e) {
			logger.log(Level.SEVERE,"Error on deleteCrossMapResultsExported", e);
			throw new XMapGUIException(e.getMessage());
		}
	}

	public void exportXmapResult(String xmapId, boolean includeAcceptedName) throws XMapGUIException {
		logger.log(Level.FINE, "exportXmapResult xmapName:" + xmapId + " includeAcceptedName: " + includeAcceptedName);
		try {
			ASLSession session = getCurrentSession();

			TaskHandlerExportXmap taskHandler = getXMapService().exportResultXmap(xmapId, includeAcceptedName, session.getUsername(), session.getScope().toString());

			logger.log(Level.FINE, "Task id: " + taskHandler.getTaskId().getValue());
		} 
		catch (XMapServiceFault_Exception e){
			logger.log(Level.SEVERE,"Error on exportXmapResult", e);
			throw new XMapGUIException(e.getFaultInfo().getUserData(),e.getFaultInfo().getIdException());
		}		
		catch (Exception e) {
			logger.log(Level.SEVERE,"Error on exportXmapResult", e);
			throw new XMapGUIException(e.getMessage());
		}
	}

	public void saveExportResultsCrossMapInWorkspace(String resultName, String resultUrl, String workspaceFolderId, String fileName) throws XMapGUIException {
		logger.log(Level.FINE, "saveExportResultsCrossMapInWorkspace resultName:" + resultName + " resultUrl: " + resultUrl + " workspaceFolderId: " + workspaceFolderId + " fileName" + fileName);
		try {
			ASLSession session = getCurrentSession();
			Workspace userWorkspace = HomeLibrary.getUserWorkspace(session);

			WorkspaceFolder workspaceFolder = (WorkspaceFolder)userWorkspace.getItem(workspaceFolderId);

			InputStream fileData = new URL(resultUrl).openStream();
			String mimeType = "application/zip";

			workspaceFolder.createExternalFileItem(fileName, "XMap export results " + resultName, mimeType, fileData);
		}
		catch (XMapServiceFault_Exception e){
			logger.log(Level.SEVERE,"Error on saveExportResultsCrossMapInWorkspace", e);
			throw new XMapGUIException(e.getFaultInfo().getUserData(),e.getFaultInfo().getIdException());
		}
		catch (Exception e) {
			logger.log(Level.SEVERE,"Error on saveExportResultsCrossMapInWorkspace", e);
			throw new XMapGUIException(e.getMessage());
		}
	}
	
	public List<TaxonomicRankGWT> getTaxonomicRanks() throws XMapGUIException {
		logger.log(Level.FINER, "getTaxonomicRanks");
		try {			
			long start = System.currentTimeMillis();
			List<TaxonomicRank> list = getXMapService().getTaxonomicRanks().getRank();
			List<TaxonomicRankGWT> ranks = Converter.convertListTaxonomicRanks(list);

			logger.log(Level.FINER, "taxonomic ranks " + ranks.size() + " ready in " + (System.currentTimeMillis() - start));
			return ranks;
		}
		catch (XMapServiceFault_Exception e){
			logger.log(Level.SEVERE, "Error on getTaxonomicRanks", e);
			throw new XMapGUIException(e.getFaultInfo().getUserData(),e.getFaultInfo().getIdException());
		}
		catch (Exception e) {
			logger.log(Level.SEVERE, "Error on getTaxonomicRanks", e);
			throw new XMapGUIException(e.getMessage());
		}
	}		

	@Override
	public TaxonDetailGWT getTaxonDetail(String checklistId, String taxonId) throws XMapGUIException {
		logger.log(Level.FINER, "getTaxonDetail");
		try {
			ASLSession session = getCurrentSession();
			TaxonDetail taxonDetail = getXMapService().getTaxonDetail(checklistId,taxonId,session.getUsername(), session.getScope().toString());

			TaxonDetailGWT taxonDetailGWT = Converter.convertTaxonDetailObj(taxonDetail);			
			logger.log(Level.FINE, "Taxon detail id: " + taxonId);
			
			return taxonDetailGWT;
		}
		catch (XMapServiceFault_Exception e){
			logger.log(Level.SEVERE, "Error on getTaxonDetail", e);
			throw new XMapGUIException(e.getFaultInfo().getUserData(),e.getFaultInfo().getIdException());
		}
		catch (Exception e) {
			logger.log(Level.SEVERE, "Error on getTaxonDetail", e);
			throw new XMapGUIException(e.getMessage());
		}				
	}


	@Override
	public RelationshipDetailGWT getXMapRelationshipDetail(String xMapId, String leftTaxonId, String rightTaxonId) throws XMapGUIException {
		logger.log(Level.FINER, "getXMapRelationshipDetail");
		try {
			ASLSession session = getCurrentSession();
			RelationshipDetail relationshipDetail = getXMapService().getXMapRelationshipDetail(xMapId,leftTaxonId,rightTaxonId,session.getUsername(), session.getScope().toString());

			RelationshipDetailGWT relationshipDetailGWT = Converter.convertRelationshipDetailObj(relationshipDetail);			
			logger.log(Level.FINE, "Relationship detail leftTaxonId: " + leftTaxonId + " rightTaxonId: " + rightTaxonId);
			
			return relationshipDetailGWT;
		}
		catch (XMapServiceFault_Exception e){
			logger.log(Level.SEVERE, "Error on getXMapRelationshipDetail", e);
			throw new XMapGUIException(e.getFaultInfo().getUserData(),e.getFaultInfo().getIdException());
		}
		catch (Exception e) {
			logger.log(Level.SEVERE, "Error on getXMapRelationshipDetail", e);
			throw new XMapGUIException(e.getMessage());
		}	
	}
		
	
	@Override
	public PagingLoadResult<EntryInChecklistGWT> getEntriesInChecklist(String checklistId, FilterPagingLoadConfig config) throws XMapGUIException  {
		logger.log(Level.FINER, "getEntriesInChecklist");
		try {			
			long startTime = System.currentTimeMillis();
			
			ASLSession session = getCurrentSession();
			
			DataRetrievalSortSeq sortSeq = new DataRetrievalSortSeq();
			for (SortInfo sortInfo: config.getSortInfo()){
				DataRetrievalSort sort = new DataRetrievalSort();
				sort.setColumn(sortInfo.getSortField());
				if (sortInfo.getSortDir() == SortDir.ASC){
					sort.setDirection(DataRetrievalSortingDirection.ASC);
				}
				else {
					sort.setDirection(DataRetrievalSortingDirection.DESC);
				}
				sortSeq.getSort().add(sort);
			}
			
			
			DataRetrievalFilterSeq filterSeq = new DataRetrievalFilterSeq();
			for (FilterConfig filterConfig : config.getFilters()) {			
				DataRetrievalFilter filter = new DataRetrievalFilter();
				filter.setColumn(filterConfig.getField());
				filter.setType(DataRetrievalFilterType.CONTAINS); // f.getType() +  filterConfig.getComparison()
				filter.setValue(filterConfig.getValue());				
			}
			 
			int start = config.getOffset();
			int end = start + config.getLimit();
			
			EntriesInChecklistSeq  entriesSeq = getXMapService().getEntriesInChecklist(checklistId, filterSeq, sortSeq,  config.getOffset(), end, session.getUsername(), session.getScope().toString());			
			List<EntryInChecklistGWT> entries = Converter.convertListEntryInChecklist(entriesSeq.getEntry());
			
			
			logger.log(Level.FINER, "entries " + entries.size() + " ready in " + (System.currentTimeMillis() - startTime));
			return new PagingLoadResultBean<EntryInChecklistGWT>(entries, entriesSeq.getTotalEntries(), config.getOffset());	    		
		}
		catch (XMapServiceFault_Exception e){
			logger.log(Level.SEVERE, "Error on getEntriesInChecklist", e);
			throw new XMapGUIException(e.getFaultInfo().getUserData(),e.getFaultInfo().getIdException());
		}
		catch (Exception e) {
			logger.log(Level.SEVERE, "Error on getEntriesInChecklist", e);
			throw new XMapGUIException(e.getMessage());
		}				    
	}

	
	@Override
	public PagingLoadResult<EntryInXMapGWT> getEntriesInXMap(String xMapId, FilterPagingLoadConfig config) throws XMapGUIException  {
		logger.log(Level.FINER, "getEntriesInXMap");
		try {			
			long startTime = System.currentTimeMillis();
			
			ASLSession session = getCurrentSession();
			
			DataRetrievalSortSeq sortSeq = new DataRetrievalSortSeq();
			for (SortInfo sortInfo: config.getSortInfo()){
				DataRetrievalSort sort = new DataRetrievalSort();
				sort.setColumn(sortInfo.getSortField());
				if (sortInfo.getSortDir() == SortDir.ASC){
					sort.setDirection(DataRetrievalSortingDirection.ASC);
				}
				else {
					sort.setDirection(DataRetrievalSortingDirection.DESC);
				}
				sortSeq.getSort().add(sort);
			}
			
			
			DataRetrievalFilterSeq filterSeq = new DataRetrievalFilterSeq();
			for (FilterConfig filterConfig : config.getFilters()) {			
				DataRetrievalFilter filter = new DataRetrievalFilter();
				filter.setColumn(filterConfig.getField());
				filter.setType(DataRetrievalFilterType.CONTAINS); // f.getType() +  filterConfig.getComparison()
				filter.setValue(filterConfig.getValue());				
			}
			 
			int start = config.getOffset();
			int end = start + config.getLimit();
			
			EntriesInXMapSeq  entriesSeq = getXMapService().getEntriesInXMap(xMapId, filterSeq, sortSeq,  config.getOffset(), end, session.getUsername(), session.getScope().toString());			
			List<EntryInXMapGWT> entries = Converter.convertListEntryInXMap(entriesSeq.getEntry());
			
			
			logger.log(Level.FINER, "entries " + entries.size() + " ready in " + (System.currentTimeMillis() - startTime));
			return new PagingLoadResultBean<EntryInXMapGWT>(entries, entriesSeq.getTotalEntries(), config.getOffset());	    		
		}
		catch (XMapServiceFault_Exception e){
			logger.log(Level.SEVERE, "Error on getEntriesInXMap", e);
			throw new XMapGUIException(e.getFaultInfo().getUserData(),e.getFaultInfo().getIdException());
		}
		catch (Exception e) {
			logger.log(Level.SEVERE, "Error on getEntriesInXMap", e);
			throw new XMapGUIException(e.getMessage());
		}				    
	}
	
	

	@Override
	public List<TaxonGWT> getTaxonChildren(String checklistId, String taxonId) throws XMapGUIException {
		logger.log(Level.FINER, "getTaxonChildren");
		try {
			ASLSession session = getCurrentSession();
			List<Taxon> children = getXMapService().getTaxonChildren(checklistId, taxonId, session.getUsername(), session.getScope().toString()).getChild();

			List<TaxonGWT> childrenGWT = Converter.convertListTaxon(children);								
			return childrenGWT;
		}
		catch (XMapServiceFault_Exception e){
			logger.log(Level.SEVERE, "Error on getTaxonChildren", e);
			throw new XMapGUIException(e.getFaultInfo().getUserData(),e.getFaultInfo().getIdException());
		}
		catch (Exception e) {
			logger.log(Level.SEVERE, "Error on getTaxonChildren", e);
			throw new XMapGUIException(e.getMessage());
		}	
	}
	
	@Override
	public List<TaxonGWT> getTaxonChildrenPlusRelationships(String checklistId, String taxonId, String xMapId, boolean isLeftTaxon) throws XMapGUIException{
		logger.log(Level.FINER, "getTaxonChildrenPlusRelationships");
		try {
			ASLSession session = getCurrentSession();
			List<Taxon> children = getXMapService().getTaxonChildrenPlusRelationships(checklistId, taxonId, xMapId, isLeftTaxon, session.getUsername(), session.getScope().toString()).getChild();
	
			List<TaxonGWT> childrenGWT = Converter.convertListTaxon(children);								
			return childrenGWT;
		}
		catch (XMapServiceFault_Exception e){
			logger.log(Level.SEVERE, "Error on getTaxonChildrenPlusRelationships", e);
			throw new XMapGUIException(e.getFaultInfo().getUserData(),e.getFaultInfo().getIdException());
		}
		catch (Exception e) {
			logger.log(Level.SEVERE, "Error on getTaxonChildrenPlusRelationships", e);
			throw new XMapGUIException(e.getMessage());
		}	
	}	
	
	/****************************************************************************************/
	/* PROTECTED METHODS																	*/														
	/****************************************************************************************/	

	protected ASLSession getCurrentSession() throws Exception  {
		try {
			HttpSession httpSession = getThreadLocalRequest().getSession();
			return Util.getCurrentSession(httpSession);
		} 
		catch (Exception e) {
			logger.log(Level.SEVERE,"Error on getCurrentSession", e);
			throw new Exception("User session expired", e);
		}
	}

	protected XMapService getXMapService() throws Exception {
		ASLSession session = getCurrentSession();

		synchronized (session) {
			Object obj = session.getAttribute("xMapWS");
			if (obj != null) {
				return (XMapService)obj;
				
			}

									
			GCUBERuntimeResource runtimeResource = Util.getRuntimeResource("xMap", session.getScope());

			logger.log(Level.FINE, "Found RR " + runtimeResource.getID());

			List<AccessPoint> accessPoints = runtimeResource.getAccessPoints();
			for (AccessPoint accessPoint : accessPoints)
			{
				if (!accessPoint.getEntryname().startsWith("WSDL")) {
					logger.log(Level.FINE, "Skipping AC with name " + accessPoint.getEntryname());
				}
				else
				{
					String endPointWSDL = accessPoint.getEndpoint();

					//For debugging
					//endPointWSDL = "http://localhost:8085/CrossMapping/XMapService?wsdl";					
					//endPointWSDL = "http://litchi5.cs.cf.ac.uk:8080/CrossMapping/XMapService?wsdl";					

					logger.log(Level.FINE, "checking for " + endPointWSDL);
					try {												
						XMapService service = createService(endPointWSDL, session);
						session.setAttribute("xMapWS", service);
						return service;
					} catch (Exception e) {
						logger.log(Level.FINE, "Error using the service " + endPointWSDL + " skipping");
					}
				}
			}      
			logger.log(Level.FINE, "No running service found");
			throw new Exception("No running service found");	       		
		}		
	}

	protected XMapService createService(String endPointWSDL, ASLSession session) throws Exception {
		logger.log(Level.FINE, "creating the service stubs for " + endPointWSDL);
		XMapServiceWS xMapWSFactory = new XMapServiceWS(new URL(endPointWSDL), new QName("http://xmap.openbio.org/common/serviceinterfaces/services/xmap", "XMap_XMapService"));
		MTOMFeature mtom = new MTOMFeature();
		XMapService xMapWS = (XMapService)xMapWSFactory.getPort(new QName("http://xmap.openbio.org/common/serviceinterfaces/services/xmap", "xmapWSSOAP11Port"), XMapService.class, new WebServiceFeature[] { mtom });

		BindingProvider bp = (BindingProvider)xMapWS;
		bp.getRequestContext().put("javax.xml.ws.service.endpoint.address", endPointWSDL);
		return xMapWS;
	}



}
