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
package org.gcube.portlets.user.crossmapping.client;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.gcube.portlets.user.crossmapping.client.panel.ChecklistsPanel;
import org.gcube.portlets.user.crossmapping.client.panel.CrossMapsPanel;
import org.gcube.portlets.user.crossmapping.client.panel.XMapObjectsContentPanel;
import org.gcube.portlets.user.crossmapping.client.panel.ResultsPanel;
import org.gcube.portlets.user.crossmapping.client.rpc.XMapGUIService;
import org.gcube.portlets.user.crossmapping.client.rpc.XMapGUIServiceAsync;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.TabItemConfig;
import com.sencha.gxt.widget.core.client.TabPanel;
import com.sencha.gxt.widget.core.client.button.ToolButton;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer.BorderLayoutData;
import com.sencha.gxt.widget.core.client.container.MarginData;
import com.sencha.gxt.widget.core.client.container.SimpleContainer;
import com.sencha.gxt.widget.core.client.container.Viewport;
import com.sencha.gxt.core.client.util.Margins;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.sencha.gxt.widget.core.client.container.AccordionLayoutContainer;
import com.sencha.gxt.widget.core.client.container.AccordionLayoutContainer.AccordionLayoutAppearance;
import com.sencha.gxt.widget.core.client.container.AccordionLayoutContainer.ExpandMode;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 * @author "Federico De Faveri defaveri@isti.cnr.it"
 * Modification: 10/10/12 F. Quevedo. Changes
 *		 - Write log messages using logging framework
 *		 - How errors are shown to the users
 *       - Only refresh the list of panel visible
 */
public class CrossMappingPortlet implements EntryPoint {
	
	public static final String MAIN_PANEL_DIV = "mainPanel";

	public static final XMapGUIServiceAsync service = GWT.create(XMapGUIService.class);
	
	private SimpleContainer appContainerPanel;
	
	protected AccordionLayoutContainer accordion;
	public static TabPanel tabPanel;

				
	private Logger logger = Logger.getLogger(CrossMappingPortlet.class.getName());
	

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		final BorderLayoutContainer con = new BorderLayoutContainer();
		con.setBorders(true);

		//WEST PANEL
		ContentPanel west = createStackPanel();		
		BorderLayoutData westData = new BorderLayoutData(300);
		westData.setCollapsible(true);
		westData.setSplit(true);
		westData.setCollapseMini(true);
		westData.setMinSize(150);
		westData.setMargins(new Margins(0, 5, 0, 5));
		con.setWestWidget(west, westData);
		

		//CENTRAL PANEL
	   tabPanel = new TabPanel();
	   tabPanel.setStyleName("tabPanel");
	   tabPanel.setTabScroll(true);
	   tabPanel.setAnimScroll(true);	  
	   tabPanel.setCloseContextMenu(true);
	   con.setCenterWidget(tabPanel);
	   
	   
	   tabPanel.addSelectionHandler(new SelectionHandler<Widget>() {		
		   @Override
		   public void onSelection(SelectionEvent<Widget> event) {
			   TabPanel panel = (TabPanel) event.getSource();
			   Widget w = event.getSelectedItem();
			   TabItemConfig config = panel.getConfig(w);
			   panel.forceLayout();			
		   }
	   });
	   

	   appContainerPanel = new SimpleContainer();
	   appContainerPanel.add(con, new MarginData(5));
		
		Window.addResizeHandler(new ResizeHandler() {
			@Override
			public void onResize(ResizeEvent event) {
				updateSize();
			}
		});
		
		RootPanel root = RootPanel.get(MAIN_PANEL_DIV);
		if (root!=null){
			logger.log(Level.FINE, "CROSS MAPPING div found, we are on portal");
			updateSize();
			root.add(appContainerPanel);

		} else{
			logger.log(Level.FINE, "CROSS MAPPING  div not found, we are out of the portal");
			Viewport viewport = new Viewport();
			viewport.add(appContainerPanel);
			RootPanel.get().add(viewport);
		}
		
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {			
			@Override
			public void execute() {
				refreshContentLeftPanel();
			}
		});							
	}
	
	public void updateSize() {
		logger.log(Level.FINE, "Resizing");
		RootPanel discovery = RootPanel.get(MAIN_PANEL_DIV);

		int topBorder = discovery.getAbsoluteTop();
		int leftBorder = discovery.getAbsoluteLeft();

		int rightScrollBar = 17;
		int rootHeight = Window.getClientHeight() - topBorder - 36;
		int rootWidth = Window.getClientWidth() - 2* leftBorder - rightScrollBar;

		logger.log(Level.FINE, "new size "+rootWidth+"x"+rootHeight);
		appContainerPanel.setPixelSize(rootWidth, rootHeight);
	}

	protected ContentPanel createStackPanel() {
		ContentPanel west = new ContentPanel();

		accordion = new AccordionLayoutContainer();
		accordion.setExpandMode(ExpandMode.SINGLE_FILL);
		west.add(accordion);

		AccordionLayoutAppearance appearance = GWT.<AccordionLayoutAppearance> create(AccordionLayoutAppearance.class);

		ChecklistsPanel checklistsPanel = new ChecklistsPanel(appearance);
		accordion.add(checklistsPanel);
		
		CrossMapsPanel crossMapsPanel = new CrossMapsPanel(appearance);
		accordion.add(crossMapsPanel);

		ResultsPanel resultsPanel = new ResultsPanel(appearance);
		accordion.add(resultsPanel);
		
		accordion.setActiveWidget(checklistsPanel);			
		
		ToolButton adminBtn = new ToolButton(ToolButton.GEAR);
		adminBtn.setToolTip("Administration");
		adminBtn.addSelectHandler(new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
				// TODO Auto-generated method stub
				Window.alert("Funtion not implemented yet");			
			}
		});		
		west.addTool(adminBtn);
		
		return west;
	}

	
	public void refreshContentLeftPanel() {
		XMapObjectsContentPanel objPanel = (XMapObjectsContentPanel) accordion.getActiveWidget();
		objPanel.refreshList(true);
	}

}

