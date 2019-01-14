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
package org.gcube.portlets.user.crossmapping.client.rpc;

import java.util.List;


import org.gcube.portlets.user.crossmapping.shared.ChecklistGWT;
import org.gcube.portlets.user.crossmapping.shared.EntryInXMapGWT;
import org.gcube.portlets.user.crossmapping.shared.EntryInChecklistGWT;
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

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.sencha.gxt.data.shared.loader.FilterPagingLoadConfig;
import com.sencha.gxt.data.shared.loader.PagingLoadResult;

/**
 * @author "Federico De Faveri defaveri@isti.cnr.it"
 * Modification: 10/10/12 F. Quevedo. Changes
 *		 - TaskStatus by TaskStatusGWT
 */
@RemoteServiceRelativePath("xMap")
public interface XMapGUIService extends RemoteService {

    public List<ChecklistGWT> getChecklists(TaskStatusGWT filter) throws XMapGUIException;
    
    public List<XMapGWT> getCrossMaps() throws XMapGUIException;
    
    public List<ExportationResultGWT> getExportationResults() throws XMapGUIException;
    
    public TaskProgressGWT getTaskProgress(String taskId) throws XMapGUIException;
    
    public void cancelTask(String taskId)throws XMapGUIException;
    
    public void deleteChecklistImported(String checklistId) throws XMapGUIException;
    
    public void importChecklist(String workspaceItemId, String checklistName) throws XMapGUIException;
    
    public void deleteCompletedCrossMap(String xMapId) throws XMapGUIException;
    
    public void runXmap(String xmapName, String description, String leftChecklistId, String rightChecklistId, 
    		boolean strict, IdentifyExtraTaxaTypeGWT identifyExtraTaxa, boolean compareHigherTaxa, 
    		TaxonomicRankGWT highestTaxonomicRankToCompare, UserKnowledgeLevelForRefinementGWT refinementLevel) throws XMapGUIException;
 
    public void deleteCrossMapResultsExported(String xMapResultId) throws XMapGUIException;
        
    public void exportXmapResult(String xmapId, boolean includeAcceptedName) throws XMapGUIException;
    
    public void saveExportResultsCrossMapInWorkspace(String resultName, String resultUrl, String workspaceFolderId, String fileName) throws XMapGUIException;
    
    public List<TaxonomicRankGWT> getTaxonomicRanks() throws XMapGUIException;
	    
    public TaxonDetailGWT getTaxonDetail(String checklistId, String taxonId) throws XMapGUIException;
    
    public RelationshipDetailGWT getXMapRelationshipDetail(String xMapId, String leftTaxonId, String rightTaxonId) throws XMapGUIException;
        
    public List<TaxonGWT> getTaxonChildren(String checklistId, String taxonId) throws XMapGUIException;
        
    public PagingLoadResult<EntryInChecklistGWT> getEntriesInChecklist(String checklistId, FilterPagingLoadConfig config) throws XMapGUIException;
    
    public PagingLoadResult<EntryInXMapGWT> getEntriesInXMap(String xMapId, FilterPagingLoadConfig config) throws XMapGUIException;
    
    public List<TaxonGWT> getTaxonChildrenPlusRelationships(String checklistId, String taxonId, String xMapId, boolean isLeftTaxon) throws XMapGUIException;
}
