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
/**
 * 
 */
package org.gcube.portlets.user.crossmapping.client.panel;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.gcube.portlets.user.crossmapping.client.CrossMappingPortlet;
import org.gcube.portlets.user.crossmapping.client.model.RelationshipPairTaxaEntryProperties;
import org.gcube.portlets.user.crossmapping.client.util.ErrorMessage;
import org.gcube.portlets.user.crossmapping.client.util.TooltipCell;
import org.gcube.portlets.user.crossmapping.client.window.TaxonDetailsWindow;
import org.gcube.portlets.user.crossmapping.client.window.XMapRelationshipDetailsWindow;
import org.gcube.portlets.user.crossmapping.shared.RelationshipDetailGWT;
import org.gcube.portlets.user.crossmapping.shared.RelationshipPairTaxaEntryGWT;
import org.gcube.portlets.user.crossmapping.shared.RelationshipPairTaxaGWT;
import org.gcube.portlets.user.crossmapping.shared.TaxonDetailGWT;
import org.gcube.portlets.user.crossmapping.shared.XMapGWT;


import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Label;
import com.sencha.gxt.cell.core.client.TextButtonCell;
import com.sencha.gxt.core.client.dom.ScrollSupport.ScrollMode;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.event.CellClickEvent;
import com.sencha.gxt.widget.core.client.event.CellClickEvent.CellClickHandler;

import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.sencha.gxt.widget.core.client.info.Info;


public class DetailsRelationshipPairTaxaPanel extends ContentPanel {

	protected static final RelationshipPairTaxaEntryProperties properties = GWT.create(RelationshipPairTaxaEntryProperties.class);
	
	private Logger logger = Logger.getLogger(DetailsRelationshipPairTaxaPanel.class.getName());

	public DetailsRelationshipPairTaxaPanel(final XMapGWT xMap, List<RelationshipPairTaxaGWT> rels, String heading, ContentPanelAppearance appearance)	{
		super(appearance);
		setAnimCollapse(false);
		setHeadingText(heading);
		
		VerticalLayoutContainer p = new VerticalLayoutContainer();
		
		if (rels.size()>0){		
			//Get column model for RelationshipPairTaxaEntry registers 
			ColumnModel<RelationshipPairTaxaEntryGWT> cmEntries = getColumModel();		
								
			//Add as many grid as different pair of taxa relationships are in array rels
			for (final RelationshipPairTaxaGWT rel: rels){
				String desc = "";
				if (!rel.getLeftTaxonName().equalsIgnoreCase("") && !rel.getRightTaxonName().equalsIgnoreCase("")){
					desc = "Details of '" + rel.getRelationship() + "' relationship between taxa with rank " + rel.getLeftTaxonRank() + ": `" + rel.getLeftTaxonName() + "` and `"+ rel.getRightTaxonName() + "`";
				}
				else if (rel.getRightTaxonName().equalsIgnoreCase("")){
					desc = "Details of '" + rel.getRelationship() + "' relationship of left taxon `" + rel.getLeftTaxonName() + "`";
				}
				else{
					desc = "Details of '" + rel.getRelationship() + "' relationship of right taxon `" + rel.getRightTaxonName() + "`";
				}
				
				Label lblDescription = new Label(desc);	
				lblDescription.setStyleName("label");
				p.add(lblDescription, new VerticalLayoutData(1, -1, new Margins(10,0,0,0)));			
				final Grid<RelationshipPairTaxaEntryGWT>  gridRel = getGrid(rel.getElementsRel(),cmEntries); 		
				p.add(gridRel, new VerticalLayoutData(1, 1, new Margins(15)));			
				
				gridRel.addCellClickHandler(new CellClickHandler() {			
					@Override
					public void onCellClick(CellClickEvent event) {
						RelationshipPairTaxaEntryGWT selEntry = gridRel.getSelectionModel().getSelectedItem();
						
						if (event.getSource().getColumnModel().getColumn(event.getCellIndex()).getHeader().asString().equalsIgnoreCase("Rel")){												
							if (!selEntry.getTaxId1().equalsIgnoreCase("") && !selEntry.getTaxId2().equalsIgnoreCase("")){
								if (!(rel.getLeftTaxonId().equalsIgnoreCase(selEntry.getTaxId1()))){
									doViewRelationshipDetail(xMap, selEntry.getTaxId1(), selEntry.getTaxId2());
								}
								else{
									Info.display("View relationship", "The maximum level of details has been reached");
								}
							}
							else{
								Info.display("View relationship", "Taxon only found in one checklist");
							}
						}
						else if (event.getSource().getColumnModel().getColumn(event.getCellIndex()).getHeader().asString().equalsIgnoreCase("TaxonId1")){
							if (!selEntry.getTaxId1().equalsIgnoreCase("")){
								doViewTaxonDetail(xMap.getLeftChecklistId(),xMap.getLeftChecklistName(), selEntry.getTaxId1());
							}
							else{
								Info.display("View taxon details", "No taxon");
							}
						}
						else if (event.getSource().getColumnModel().getColumn(event.getCellIndex()).getHeader().asString().equalsIgnoreCase("TaxonId2")){
							if (!selEntry.getTaxId2().equalsIgnoreCase("")){
								doViewTaxonDetail(xMap.getRightChecklistId(),xMap.getRightChecklistName(), selEntry.getTaxId2());
							}
							else{
								Info.display("View taxon details", "No taxon");
							}														
						}
					}
				});				
				
				
			}
		}
		else{
			Label lblNoElementsFound = new Label("No relationships found");		
			//lblNoElementsFound.setStyleName("label");
			p.add(lblNoElementsFound, new VerticalLayoutData(1, -1, new Margins(10,0,0,0)));		
		}
		
		 p.setScrollMode(ScrollMode.AUTOY);
		 		
		add(p);
	}
	
	protected void onExpand(){				
		logger.log(Level.FINE,"Expand panel");
		super.onExpand();
	}
	
	protected void onCollapse(){
		logger.log(Level.FINE,"Collapse panel");
		super.onCollapse();
	}	
	
	
	private ColumnModel<RelationshipPairTaxaEntryGWT> getColumModel(){
		//Define the columns
		ColumnConfig<RelationshipPairTaxaEntryGWT, String> taxonId1Column = new ColumnConfig<RelationshipPairTaxaEntryGWT, String>(properties.taxId1(), 150, "TaxonId1");
		taxonId1Column.setCell(new TextButtonCell());
		ColumnConfig<RelationshipPairTaxaEntryGWT, String> element1Column = new ColumnConfig<RelationshipPairTaxaEntryGWT, String>(properties.element1(), 250, "Element1");
		element1Column.setCell(new TooltipCell<String>());
		ColumnConfig<RelationshipPairTaxaEntryGWT, String> extra1Column = new ColumnConfig<RelationshipPairTaxaEntryGWT, String>(properties.extra1(), 100, "Comment1");
		extra1Column.setCell(new TooltipCell<String>());
		ColumnConfig<RelationshipPairTaxaEntryGWT, String> relTypeColumn = new ColumnConfig<RelationshipPairTaxaEntryGWT, String>(properties.relType(), 125, "Rel");
		relTypeColumn.setCell(new TextButtonCell());		
		ColumnConfig<RelationshipPairTaxaEntryGWT, String> taxonId2Column = new ColumnConfig<RelationshipPairTaxaEntryGWT, String>(properties.taxId2(), 150, "TaxonId2");		
		taxonId2Column.setCell(new TextButtonCell());
		ColumnConfig<RelationshipPairTaxaEntryGWT, String> element2Column = new ColumnConfig<RelationshipPairTaxaEntryGWT, String>(properties.element2(), 250, "Element2");
		element2Column.setCell(new TooltipCell<String>());
		ColumnConfig<RelationshipPairTaxaEntryGWT, String> extra2Column = new ColumnConfig<RelationshipPairTaxaEntryGWT, String>(properties.extra2(), 100, "Comment2");
		extra2Column.setCell(new TooltipCell<String>());		
		ColumnConfig<RelationshipPairTaxaEntryGWT, Boolean> inOthersColumn = new ColumnConfig<RelationshipPairTaxaEntryGWT, Boolean>(properties.inOthers(), 50, "InOthers");
		//inOthersColumn.setCell(new TooltipCell<Boolean>());
		ColumnConfig<RelationshipPairTaxaEntryGWT, Boolean> nameLevelColumn = new ColumnConfig<RelationshipPairTaxaEntryGWT, Boolean>(properties.nameLevel(), 50, "NameLevel");
		//nameLevelColumn.setCell(new TooltipCell<Boolean>());
		
		
		//Add the colums to the list
		List<ColumnConfig<RelationshipPairTaxaEntryGWT, ?>> columns = new ArrayList<ColumnConfig<RelationshipPairTaxaEntryGWT, ?>>();
		columns.add(taxonId1Column);
		columns.add(element1Column);
		columns.add(extra1Column);
		columns.add(relTypeColumn);		
		columns.add(taxonId2Column);
		columns.add(element2Column);
		columns.add(extra2Column);
		columns.add(inOthersColumn);
		columns.add(nameLevelColumn);
		
		//Set the columns model with the columns list
		ColumnModel<RelationshipPairTaxaEntryGWT> cm = new ColumnModel<RelationshipPairTaxaEntryGWT>(columns);	
		
		return cm;
	}	
	
	private Grid<RelationshipPairTaxaEntryGWT> getGrid(List<RelationshipPairTaxaEntryGWT> entries, ColumnModel<RelationshipPairTaxaEntryGWT> cm){
		//Create the store
		ListStore<RelationshipPairTaxaEntryGWT> store = new ListStore<RelationshipPairTaxaEntryGWT>(properties.key());
		
		//Add to the store the entries to be shown in the grid 
		store.addAll(entries);
		
		//Define the grid
		Grid<RelationshipPairTaxaEntryGWT> grid = new Grid<RelationshipPairTaxaEntryGWT>(store, cm);	
		grid.getView().setStripeRows(true);
		grid.getView().setColumnLines(true);
		grid.getView().setEmptyText("No entries found.");
		grid.setBorders(false);
		grid.setColumnReordering(true);		
		
		grid.setHeight(20 + (grid.getStore().size()*10));
		
		
		return grid;
	}

	
	private void doViewRelationshipDetail(final XMapGWT xMap, String leftTaxonId, String rightTaxonId) {
		CrossMappingPortlet.tabPanel.mask("Loading relationship between taxon " + leftTaxonId + " and taxon " + rightTaxonId + " ...");
		
		CrossMappingPortlet.service.getXMapRelationshipDetail(xMap.getId(), leftTaxonId, rightTaxonId, new AsyncCallback<RelationshipDetailGWT>() {

			@Override
			public void onSuccess(RelationshipDetailGWT xMapRelationShipDetail) {
				XMapRelationshipDetailsWindow taxonDetailsWindow = new XMapRelationshipDetailsWindow(xMap,xMapRelationShipDetail,false);
				taxonDetailsWindow.show();
				CrossMappingPortlet.tabPanel.unmask();
			}
	
			@Override
			public void onFailure(Throwable caught) {				
				logger.log(Level.SEVERE,"Error getting relationship details",caught);
				ErrorMessage.showError("Error getting relationship details",caught);						
				CrossMappingPortlet.tabPanel.unmask();
			}
		});										
	}		
	
	
	private void doViewTaxonDetail(final String checklistId, final String checklistName, String taxonId){							
		CrossMappingPortlet.tabPanel.mask("Loading taxon details " + taxonId + " ...");
		
		CrossMappingPortlet.service.getTaxonDetail(checklistId, taxonId, new AsyncCallback<TaxonDetailGWT>() {
			@Override
			public void onSuccess(TaxonDetailGWT taxonDetail) {
				TaxonDetailsWindow taxonDetailsWindow = new TaxonDetailsWindow(checklistId,checklistName,taxonDetail,false);
				taxonDetailsWindow.show();	
				CrossMappingPortlet.tabPanel.unmask();
			}
	
			@Override
			public void onFailure(Throwable caught) {				
				logger.log(Level.SEVERE,"Error getting taxon details",caught);
				ErrorMessage.showError("Error getting taxon details",caught);			
				
				CrossMappingPortlet.tabPanel.unmask();				
			}
		});		
	}		
	
}
