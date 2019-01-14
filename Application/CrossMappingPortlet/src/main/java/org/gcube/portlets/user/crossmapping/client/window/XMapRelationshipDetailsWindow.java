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

import java.util.Arrays;

import java.util.logging.Logger;

import org.gcube.portlets.user.crossmapping.client.panel.ChecklistsPanel;
import org.gcube.portlets.user.crossmapping.client.panel.CrossMapsPanel;
import org.gcube.portlets.user.crossmapping.client.panel.DetailsRelationshipPairTaxaPanel;
import org.gcube.portlets.user.crossmapping.client.panel.ResultsPanel;
import org.gcube.portlets.user.crossmapping.client.resources.Resources;
import org.gcube.portlets.user.crossmapping.shared.RelationshipDetailGWT;
import org.gcube.portlets.user.crossmapping.shared.XMapGWT;

import com.google.gwt.core.client.GWT;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.Window;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.AccordionLayoutContainer;
import com.sencha.gxt.widget.core.client.container.AccordionLayoutContainer.AccordionLayoutAppearance;
import com.sencha.gxt.widget.core.client.container.AccordionLayoutContainer.ExpandMode;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;


public class XMapRelationshipDetailsWindow  extends Window {

	
	protected AccordionLayoutContainer accordion;
	
	
	private Logger logger = Logger.getLogger(XMapRelationshipDetailsWindow.class.getName());

	public XMapRelationshipDetailsWindow(XMapGWT xMap,RelationshipDetailGWT xMapRelDetail, Boolean isModal) {
		setPixelSize(1000, 650);
		setModal(true);
		setResizable(false);		
		setHeadingText("Details of the relationship: "+ xMapRelDetail.getDirectRelationship().getLeftTaxonName() 
				+ " '" + xMapRelDetail.getDirectRelationship().getRelationship() + "' "+ xMapRelDetail.getDirectRelationship().getRightTaxonName());
		getHeader().setIcon(Resources.INSTANCE.show());

				
		accordion = new AccordionLayoutContainer();
		accordion.setExpandMode(ExpandMode.SINGLE_FILL);
		//accordion.setHeight(650);
		
		AccordionLayoutAppearance appearance = GWT.<AccordionLayoutAppearance> create(AccordionLayoutAppearance.class);

		DetailsRelationshipPairTaxaPanel directRelPanel = new DetailsRelationshipPairTaxaPanel(xMap,Arrays.asList(xMapRelDetail.getDirectRelationship()),"Direct relationship",appearance);
		accordion.add(directRelPanel);
		
		DetailsRelationshipPairTaxaPanel inderectL2RRelPanel = new DetailsRelationshipPairTaxaPanel(xMap,xMapRelDetail.getIndirectRelationshipsLeft2Right(),"Inderect relationship left to right",appearance);
		accordion.add(inderectL2RRelPanel);

		DetailsRelationshipPairTaxaPanel inderectR2LRelPanel = new DetailsRelationshipPairTaxaPanel(xMap,xMapRelDetail.getIndirectRelationshipsRight2Left(),"Inderect relationship right to left",appearance);
		accordion.add(inderectR2LRelPanel);
		
		accordion.setActiveWidget(directRelPanel);				
		
				
		add(accordion);
		
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
	

	
	protected ContentPanel createStackPanel()
	{
		ContentPanel west = new ContentPanel();

		accordion = new AccordionLayoutContainer();
		accordion.setExpandMode(ExpandMode.MULTI);
		west.add(accordion);

		AccordionLayoutAppearance appearance = GWT.<AccordionLayoutAppearance> create(AccordionLayoutAppearance.class);

		ChecklistsPanel checklistsPanel = new ChecklistsPanel(appearance);
		accordion.add(checklistsPanel);
		
		CrossMapsPanel crossMapsPanel = new CrossMapsPanel(appearance);
		accordion.add(crossMapsPanel);

		ResultsPanel resultsPanel = new ResultsPanel(appearance);
		accordion.add(resultsPanel);
		
		accordion.setActiveWidget(checklistsPanel);			
		
		return west;
	}		
	
}
