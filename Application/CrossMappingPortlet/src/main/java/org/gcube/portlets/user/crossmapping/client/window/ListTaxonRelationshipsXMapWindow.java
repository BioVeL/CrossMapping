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
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.gcube.portlets.user.crossmapping.client.CrossMappingPortlet;
import org.gcube.portlets.user.crossmapping.client.model.RelationshipPairTaxaProperties;

import org.gcube.portlets.user.crossmapping.client.resources.Resources;
import org.gcube.portlets.user.crossmapping.client.util.ErrorMessage;
import org.gcube.portlets.user.crossmapping.client.util.TooltipCell;
import org.gcube.portlets.user.crossmapping.shared.RelationshipDetailGWT;
import org.gcube.portlets.user.crossmapping.shared.RelationshipPairTaxaGWT;
import org.gcube.portlets.user.crossmapping.shared.TaxonGWT;
import org.gcube.portlets.user.crossmapping.shared.XMapGWT;
import org.gcube.portlets.user.crossmapping.shared.XMapRelPathGWT;

import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.sencha.gxt.cell.core.client.TextButtonCell;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.core.client.dom.ScrollSupport.ScrollMode;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.widget.core.client.FramedPanel;
import com.sencha.gxt.widget.core.client.Window;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.sencha.gxt.widget.core.client.grid.GridViewConfig;


public class ListTaxonRelationshipsXMapWindow  extends Window {

	protected static final RelationshipPairTaxaProperties properties = GWT.create(RelationshipPairTaxaProperties.class);

	private Logger logger = Logger.getLogger(ListTaxonRelationshipsXMapWindow.class.getName());
	
	public ListTaxonRelationshipsXMapWindow(final TaxonGWT taxon, final XMapGWT xMap, boolean isLeft) {			
		setPixelSize(900, 350);
		setModal(true);
		setResizable(false);		
		setHeadingText("List of relationships for the " + (isLeft?"left":"right") + " taxon " + taxon.getAcceptedName() + " in cross-map" + xMap.getShortName());
		getHeader().setIcon(Resources.INSTANCE.show());
		
	
		final ListStore<RelationshipPairTaxaGWT> store = new ListStore<RelationshipPairTaxaGWT>(properties.key());
		store.addAll(getStoreElements(taxon,isLeft));	    
		
		ColumnConfig<RelationshipPairTaxaGWT, String> leftTaxonNameCol = new ColumnConfig<RelationshipPairTaxaGWT, String>(properties.leftTaxonName(), 250, "Left taxon");
		leftTaxonNameCol.setCell(new TooltipCell<String>());
		ColumnConfig<RelationshipPairTaxaGWT, String> leftTaxonRankCol = new ColumnConfig<RelationshipPairTaxaGWT, String>(properties.leftTaxonRank(), 80, "Left rank");
		leftTaxonRankCol.setCell(new TooltipCell<String>());		
	    ColumnConfig<RelationshipPairTaxaGWT, String> relationshipCol = new ColumnConfig<RelationshipPairTaxaGWT, String>(properties.relationship(), 100, "Relationship");
	    relationshipCol.setCell(new TooltipCell<String>());
	    ColumnConfig<RelationshipPairTaxaGWT, String> rightTaxonNameCol = new ColumnConfig<RelationshipPairTaxaGWT, String>(properties.rightTaxonName(), 250, "Right taxon");
	    rightTaxonNameCol.setCell(new TooltipCell<String>());
	    ColumnConfig<RelationshipPairTaxaGWT, String> rightTaxonRankCol = new ColumnConfig<RelationshipPairTaxaGWT, String>(properties.rightTaxonRank(), 80, "Right rank");
		rightTaxonRankCol.setCell(new TooltipCell<String>());	
	    ColumnConfig<RelationshipPairTaxaGWT, String> viewCol = new ColumnConfig<RelationshipPairTaxaGWT, String>(properties.leftTaxonId(), 100, "View");	    
	    TextButtonCell button = new TextButtonCell();
	    button.setText("View rel.");
	    button.addSelectHandler(new SelectHandler() {
	      @Override
	      public void onSelect(SelectEvent event) {
	        Context c = event.getContext();
	        int row = c.getIndex();
	        RelationshipPairTaxaGWT relPairTaxa = store.get(row);
	        doViewRelationshipDetail(xMap,relPairTaxa.getLeftTaxonId(),relPairTaxa.getRightTaxonId());
	      }
	    });
	    viewCol.setCell(button);
	    
	   				
	    List<ColumnConfig<RelationshipPairTaxaGWT, ?>> l = new ArrayList<ColumnConfig<RelationshipPairTaxaGWT, ?>>();
	    l.add(leftTaxonNameCol);
	    l.add(leftTaxonRankCol);
	    l.add(relationshipCol);
	    l.add(rightTaxonNameCol);
	    l.add(rightTaxonRankCol);
	    l.add(viewCol);
	    ColumnModel<RelationshipPairTaxaGWT> cm = new ColumnModel<RelationshipPairTaxaGWT>(l);
	 	    
	    final Grid<RelationshipPairTaxaGWT> grid = new Grid<RelationshipPairTaxaGWT>(store, cm);
	    grid.getView().setStripeRows(true);
		grid.getView().setColumnLines(true);
		grid.getView().setEmptyText("No entries found.");
		grid.setBorders(false);
		grid.setColumnReordering(true);		
	    
	    
	    grid.getView().setViewConfig(new GridViewConfig<RelationshipPairTaxaGWT>() {			
			@Override
			public String getRowStyle(RelationshipPairTaxaGWT model, int rowIndex) {
				String relationship = model.getRelationship();
				String style = "";
				
				if (relationship.equalsIgnoreCase("not_found_in")){
					style = "taxaRelNotFound";
				}
				else if (relationship.equalsIgnoreCase("poss_name_match")){
					style = "taxaRelPossNameMatch";
				}
				else if (relationship.equalsIgnoreCase("poss_gen_trnfr")) {
					style = "taxaRelPossGenTransfer";
				}
				else if (relationship.equalsIgnoreCase("corresponds")) {
					style = "taxaRelCorrespond";
				}
				else if (relationship.equalsIgnoreCase("includes")) {
					style = "taxaRelIncludes";
				}
				else if (relationship.equalsIgnoreCase("included_by")) {
					style = "taxaRelIncludedBy";
				}
				else if (relationship.equalsIgnoreCase("overlaps")) {
					style = "taxaRelOverlaps";
				}
				
				return style;
			}
			
			@Override
			public String getColStyle(RelationshipPairTaxaGWT model, ValueProvider<? super RelationshipPairTaxaGWT, ?> valueProvider, int rowIndex, int colIndex) {
				// TODO Auto-generated method stub
				return null;
			}		
		});
	 
	    
	    FramedPanel panel = new FramedPanel();
	    panel.setHeaderVisible(false);
	   		
		VerticalLayoutContainer container = new VerticalLayoutContainer();
		container.setScrollMode(ScrollMode.AUTO);
		container.add(grid, new VerticalLayoutData(1, 1));		
		panel.add(container);		
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
	

	private List<RelationshipPairTaxaGWT> getStoreElements(TaxonGWT taxon, boolean isLeft){
		List<RelationshipPairTaxaGWT> listRelPairTaxa = new ArrayList<RelationshipPairTaxaGWT>();
		for (XMapRelPathGWT rel: taxon.getXmapRels()){
			RelationshipPairTaxaGWT relPairTaxa = new RelationshipPairTaxaGWT();
			relPairTaxa.setRelationship(rel.getRelationship());
			if (isLeft){
				relPairTaxa.setLeftTaxonId(taxon.getTaxonId());
				relPairTaxa.setLeftTaxonName(taxon.getAcceptedName());
				relPairTaxa.setLeftTaxonRank(taxon.getRank());
				relPairTaxa.setRightTaxonId(rel.getOtherTaxon().getTaxonId());
				relPairTaxa.setRightTaxonName(rel.getOtherTaxon().getAcceptedName());
				relPairTaxa.setRightTaxonRank(rel.getOtherTaxon().getRank());
			}
			else{
				relPairTaxa.setLeftTaxonId(rel.getOtherTaxon().getTaxonId());
				relPairTaxa.setLeftTaxonName(rel.getOtherTaxon().getAcceptedName());
				relPairTaxa.setLeftTaxonRank(rel.getOtherTaxon().getRank());
				relPairTaxa.setRightTaxonId(taxon.getTaxonId());
				relPairTaxa.setRightTaxonName(taxon.getAcceptedName());
				relPairTaxa.setRightTaxonRank(taxon.getRank());
			}
			listRelPairTaxa.add(relPairTaxa);			
		}
		return listRelPairTaxa;
	}
	
	private void doViewRelationshipDetail(final XMapGWT xMap, String leftTaxonId, String rightTaxonId) {
		CrossMappingPortlet.tabPanel.mask("Loading relationship between taxon " + leftTaxonId + " and taxon " + rightTaxonId + " ...");
		
		CrossMappingPortlet.service.getXMapRelationshipDetail(xMap.getId(), leftTaxonId, rightTaxonId, new AsyncCallback<RelationshipDetailGWT>() {

			@Override
			public void onSuccess(RelationshipDetailGWT xMapRelationShipDetail) {
				XMapRelationshipDetailsWindow taxonDetailsWindow = new XMapRelationshipDetailsWindow(xMap,xMapRelationShipDetail, false);
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
	
}
