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
package org.gcube.portlets.user.crossmapping.client.window;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.gcube.portlets.user.crossmapping.client.CrossMappingPortlet;
import org.gcube.portlets.user.crossmapping.client.model.EntryInChecklistProperties;
import org.gcube.portlets.user.crossmapping.client.model.TaxonRawDataRegisterProperties;
import org.gcube.portlets.user.crossmapping.client.resources.Resources;
import org.gcube.portlets.user.crossmapping.client.util.ErrorMessage;
import org.gcube.portlets.user.crossmapping.client.util.TooltipCell;
import org.gcube.portlets.user.crossmapping.shared.EntryInChecklistGWT;
import org.gcube.portlets.user.crossmapping.shared.RawDataFieldGWT;
import org.gcube.portlets.user.crossmapping.shared.RawDataRegisterGWT;
import org.gcube.portlets.user.crossmapping.shared.TaxonDetailGWT;

import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Label;
import com.sencha.gxt.cell.core.client.TextButtonCell;
import com.sencha.gxt.core.client.dom.ScrollSupport.ScrollMode;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.widget.core.client.Window;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.FlowLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.event.CellClickEvent;
import com.sencha.gxt.widget.core.client.event.CellClickEvent.CellClickHandler;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.sencha.gxt.widget.core.client.info.Info;


public class TaxonDetailsWindow  extends Window {

	protected static final EntryInChecklistProperties propertiesEntryInChecklist = GWT.create(EntryInChecklistProperties.class);
	protected static final TaxonRawDataRegisterProperties propertiesRawData = GWT.create(TaxonRawDataRegisterProperties.class);
	
	
	private Logger logger = Logger.getLogger(TaxonDetailsWindow.class.getName());

	public TaxonDetailsWindow(final String checklistId, final String checklistName, final TaxonDetailGWT taxonDetail, Boolean isModal) {
		setPixelSize(1100, 650);
		setModal(true);
		setResizable(false);		
		setHeadingText("Details of the taxon `" + taxonDetail.getAcceptedName().getTidyName() + "` with rank " + taxonDetail.getAcceptedName().getRank() + " in checklist " + checklistName);
		getHeader().setIcon(Resources.INSTANCE.show());
			
		FlowLayoutContainer panel = new FlowLayoutContainer();
		panel.getScrollSupport().setScrollMode(ScrollMode.AUTOY);		
		//panel.setWidth(1090);

		VerticalLayoutContainer p = new VerticalLayoutContainer();
		panel.add(p);						
												
		//Get column model for EntryInChecklist register 
		ColumnModel<EntryInChecklistGWT> cmEntries = getColumModelEntryInChecklist();
		ColumnModel<RawDataRegisterGWT> cmRawData = getColumModelRawData(taxonDetail.getRawData().getRegisters().get(0));
		
		//Define a cell click handler for the grid to show succesive taxon details
		CellClickHandler cellClickHandler = new CellClickHandler() {			
			@Override
			public void onCellClick(CellClickEvent event) {				
				EntryInChecklistGWT selEntry = (EntryInChecklistGWT) event.getSource().getSelectionModel().getSelectedItem();				
				if (event.getSource().getColumnModel().getColumn(event.getCellIndex()).getHeader().asString().equalsIgnoreCase("TaxonId")){												
					if (!(taxonDetail.getAcceptedName().getTaxonId().equalsIgnoreCase(selEntry.getTaxonId()))){
						doViewTaxonDetail(checklistId,checklistName,selEntry.getTaxonId());
					}
					else{
						Info.display("View details", "Taxon details already displayed");
					}																	
				}
			}
		};		
		
				
		//Get the grid for the parent 
		Label lblAcceptedName = new Label("Accepted name:");	
		lblAcceptedName.setStyleName("label");
		p.add(lblAcceptedName, new VerticalLayoutData(1, -1, new Margins(10,0,0,0)));			
		Grid<EntryInChecklistGWT>  gridAcceptedName = getGridEntryInChecklist(Arrays.asList(taxonDetail.getAcceptedName()),cmEntries); 		
		p.add(gridAcceptedName, new VerticalLayoutData(0.98, 100, new Margins(10)));			
		
		//Get the grid for the synonyms
		Label lblSynonyms = new Label("Synonyms:");		
		lblSynonyms.setStyleName("label");
		p.add(lblSynonyms, new VerticalLayoutData(1, -1, new Margins(10,0,0,0)));		
		Grid<EntryInChecklistGWT>  gridSynonyms = getGridEntryInChecklist(taxonDetail.getSynonyms(),cmEntries); 
		p.add(gridSynonyms, new VerticalLayoutData(0.98, Math.min(85 + (taxonDetail.getSynonyms().size()*15),500), new Margins(10)));
		
		//Get the grid for the parent 
		Label lblParent = new Label("Parent:");
		lblParent.setStyleName("label");
		p.add(lblParent, new VerticalLayoutData(1, -1, new Margins(10,0,0,0)));			
		Grid<EntryInChecklistGWT>  gridParent = getGridEntryInChecklist(Arrays.asList(taxonDetail.getParent()),cmEntries); 
		p.add(gridParent, new VerticalLayoutData(0.98, 100, new Margins(10)));	
		gridParent.addCellClickHandler(cellClickHandler);
		
		
		//Get the grid for the children 
		Label lblChildren = new Label("Children:");		
		lblChildren.setStyleName("label");
		p.add(lblChildren, new VerticalLayoutData(1, -1, new Margins(10,0,0,0)));		
		final Grid<EntryInChecklistGWT>  gridChildren = getGridEntryInChecklist(taxonDetail.getChildren(),cmEntries); 
		p.add(gridChildren, new VerticalLayoutData(0.98, Math.min(85 + (taxonDetail.getChildren().size()*15),500), new Margins(10)));		
		gridChildren.addCellClickHandler(cellClickHandler);		
		
		
		//Get the grid for the raw data 
		Label lblRawData = new Label("Raw data:");		
		lblRawData.setStyleName("label");
		p.add(lblRawData, new VerticalLayoutData(1, -1, new Margins(10,0,0,0)));		
		Grid<RawDataRegisterGWT>  gridRawData = getGridRawData(taxonDetail.getRawData().getRegisters(),cmRawData); 
		p.add(gridRawData, new VerticalLayoutData(0.98, Math.min(85 + (taxonDetail.getRawData().getRegisters().size()*15),500), new Margins(10)));					
		
		add(panel);
		
		TextButton closeButton = new TextButton("Close");
		closeButton.addSelectHandler(new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
				hide();
			}
		});
		addButton(closeButton);

		setFocusWidget(getButtonBar().getWidget(0));
	}
	
	
	private Grid<EntryInChecklistGWT> getGridEntryInChecklist(List<EntryInChecklistGWT> entries, ColumnModel<EntryInChecklistGWT> cm){
		//Create the store
		ListStore<EntryInChecklistGWT> store = new ListStore<EntryInChecklistGWT>(propertiesEntryInChecklist.key());
		
		//Add to the store the entries to be shown in the grid 
		store.addAll(entries);
		
		//Define the grid
		Grid<EntryInChecklistGWT> grid = new Grid<EntryInChecklistGWT>(store, cm);	
		grid.getView().setStripeRows(true);
		grid.getView().setColumnLines(true);
		grid.setColumnReordering(true);
		grid.getView().setEmptyText("No entries found.");
		grid.setBorders(false);
						
		grid.setHeight(20 + (entries.size()*10));
		grid.setWidth(1000);
		
		
		return grid;
	}
	
	
	private ColumnModel<EntryInChecklistGWT> getColumModelEntryInChecklist(){
		//Define the columns
		ColumnConfig<EntryInChecklistGWT, String> taxonIdColumn = new ColumnConfig<EntryInChecklistGWT, String>(propertiesEntryInChecklist.taxonId(), 100, "TaxonId");			
		taxonIdColumn.setCell(new TextButtonCell());
		ColumnConfig<EntryInChecklistGWT, String> tidyNameColumn = new ColumnConfig<EntryInChecklistGWT, String>(propertiesEntryInChecklist.tidyName(), 200, "TidyName");
		tidyNameColumn.setCell(new TooltipCell<String>());
		ColumnConfig<EntryInChecklistGWT, String> statusColumn = new ColumnConfig<EntryInChecklistGWT, String>(propertiesEntryInChecklist.status(), 100, "Status");
		statusColumn.setCell(new TooltipCell<String>());
		ColumnConfig<EntryInChecklistGWT, String> higherColumn = new ColumnConfig<EntryInChecklistGWT, String>(propertiesEntryInChecklist.higher(), 100, "Higher");
		higherColumn.setCell(new TooltipCell<String>());
		ColumnConfig<EntryInChecklistGWT, String> speciesColumn = new ColumnConfig<EntryInChecklistGWT, String>(propertiesEntryInChecklist.species(), 100, "Species");
		speciesColumn.setCell(new TooltipCell<String>());
		ColumnConfig<EntryInChecklistGWT, String> infraspeciesColumn = new ColumnConfig<EntryInChecklistGWT, String>(propertiesEntryInChecklist.infraspecies(), 100, "Infraspecies");
		infraspeciesColumn.setCell(new TooltipCell<String>());
		ColumnConfig<EntryInChecklistGWT, String> authorityColumn = new ColumnConfig<EntryInChecklistGWT, String>(propertiesEntryInChecklist.authority(), 100, "Authority");
		authorityColumn.setCell(new TooltipCell<String>());
		ColumnConfig<EntryInChecklistGWT, String> rankColumn = new ColumnConfig<EntryInChecklistGWT, String>(propertiesEntryInChecklist.rank(), 100, "Rank");
		rankColumn.setCell(new TooltipCell<String>());
		ColumnConfig<EntryInChecklistGWT, String> parentTaxonIdColumn = new ColumnConfig<EntryInChecklistGWT, String>(propertiesEntryInChecklist.parentId(), 100, "ParentId");
		parentTaxonIdColumn.setCell(new TooltipCell<String>());
		
		//Add the colums to the list
		List<ColumnConfig<EntryInChecklistGWT, ?>> columns = new ArrayList<ColumnConfig<EntryInChecklistGWT, ?>>();
		columns.add(taxonIdColumn);
		columns.add(tidyNameColumn);
		columns.add(statusColumn);
		columns.add(higherColumn);
		columns.add(speciesColumn);
		columns.add(infraspeciesColumn);
		columns.add(authorityColumn);
		columns.add(rankColumn);
		columns.add(parentTaxonIdColumn);

		//Set the columns model with the columns list
		ColumnModel<EntryInChecklistGWT> cm = new ColumnModel<EntryInChecklistGWT>(columns);	
		
		return cm;
	}
	
	
	private ColumnModel<RawDataRegisterGWT> getColumModelRawData(RawDataRegisterGWT register){		
		//Define the columns
		List<ColumnConfig<RawDataRegisterGWT, ?>> columns = new ArrayList<ColumnConfig<RawDataRegisterGWT, ?>>();
		for (RawDataFieldGWT field : register.getRawDataFields()){
			ColumnConfig<RawDataRegisterGWT, String> column = new ColumnConfig<RawDataRegisterGWT, String>(propertiesRawData.fieldValue(), 100, field.getName());		
			columns.add(column);
		}
				
		//Set the columns model with the columns list
		ColumnModel<RawDataRegisterGWT> cm = new ColumnModel<RawDataRegisterGWT>(columns);	
		
		return cm;
	}	
	
	
	private Grid<RawDataRegisterGWT> getGridRawData(List<RawDataRegisterGWT> registers, ColumnModel<RawDataRegisterGWT> cm){
		//Create the store
		ListStore<RawDataRegisterGWT> store = new ListStore<RawDataRegisterGWT>(propertiesRawData.key());
		
		//Add to the store the registers to be shown in the grid 
		store.addAll(registers);
		
		//Define the grid
		Grid<RawDataRegisterGWT> grid = new Grid<RawDataRegisterGWT>(store, cm);	
		grid.getView().setStripeRows(true);
		grid.getView().setColumnLines(true);
		grid.setColumnReordering(true);		
		grid.getView().setEmptyText("No entries found.");
		grid.setBorders(false);
						
		return grid;
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
