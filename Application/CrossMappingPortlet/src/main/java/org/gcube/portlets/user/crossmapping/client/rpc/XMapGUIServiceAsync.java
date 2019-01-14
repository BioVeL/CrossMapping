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
import org.gcube.portlets.user.crossmapping.shared.XMapGWT;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.sencha.gxt.data.shared.loader.FilterPagingLoadConfig;
import com.sencha.gxt.data.shared.loader.PagingLoadResult;


/**
 * @author "Federico De Faveri defaveri@isti.cnr.it"
 * Modification: 10/10/12 F. Quevedo. Changes
 *		 - TaskStatus by TaskStatusGWT
 */
public interface XMapGUIServiceAsync {
 
    public void getChecklists(TaskStatusGWT filter, AsyncCallback<List<ChecklistGWT>> callback);
    
    public void getCrossMaps(AsyncCallback<List<XMapGWT>> callback);
    
    public void getExportationResults(AsyncCallback<List<ExportationResultGWT>> callback);  
    
    public void getTaskProgress(String taskId, AsyncCallback<TaskProgressGWT> callback);

    public void cancelTask(String taskId, AsyncCallback<Void> callback);

    public void deleteChecklistImported(String checklistId, AsyncCallback<Void> callback);

    public void importChecklist(String workspaceItemId, String checklistName, AsyncCallback<Void> callback);

    public void deleteCompletedCrossMap(String xMapId, AsyncCallback<Void> callback);

    public void runXmap(String xmapName, String description, String leftChecklistId, String rightChecklistId, boolean strict,
			IdentifyExtraTaxaTypeGWT identifyExtraTaxa, boolean compareHigherTaxa, 
			TaxonomicRankGWT highestTaxonomicRankToCompare, UserKnowledgeLevelForRefinementGWT refinementLevel, AsyncCallback<Void> callback);

	public void deleteCrossMapResultsExported(String xMapResultId, AsyncCallback<Void> callback);

	public void exportXmapResult(String xmapId, boolean includeAcceptedName, AsyncCallback<Void> callback);

	public void saveExportResultsCrossMapInWorkspace(String resultName, String resultUrl, String workspaceFolderId, String fileName, AsyncCallback<Void> callback);
	
	public void getTaxonomicRanks(AsyncCallback<List<TaxonomicRankGWT>> callback);
   	
	public void getTaxonDetail(String checklistId, String taxonId, AsyncCallback<TaxonDetailGWT> callback);
	
	public void getXMapRelationshipDetail(String xMapId, String taxonId1, String taxonId2, AsyncCallback<RelationshipDetailGWT> callback);
		
    public void getTaxonChildren(String checklistId, String taxonId, AsyncCallback<List<TaxonGWT>> callback);
	    
    public void getEntriesInChecklist(String checklistId, FilterPagingLoadConfig config, AsyncCallback<PagingLoadResult<EntryInChecklistGWT>> callback);
	     
    public void getEntriesInXMap(String xMapId, FilterPagingLoadConfig config, AsyncCallback<PagingLoadResult<EntryInXMapGWT>> callback);
    
    public void getTaxonChildrenPlusRelationships(String checklistId, String taxonId, String xMapId, boolean isLeftTaxon, AsyncCallback<List<TaxonGWT>> callback);
}
