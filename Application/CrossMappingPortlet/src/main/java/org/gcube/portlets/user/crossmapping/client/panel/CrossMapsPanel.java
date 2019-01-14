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
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;


import org.gcube.portlets.user.crossmapping.client.CrossMappingPortlet;
import org.gcube.portlets.user.crossmapping.client.model.EntryInXMapProperties;
import org.gcube.portlets.user.crossmapping.client.model.TaxonProperties;
import org.gcube.portlets.user.crossmapping.client.model.XMapProperties;
import org.gcube.portlets.user.crossmapping.client.resources.Resources;
import org.gcube.portlets.user.crossmapping.client.resources.TreeBundle;
import org.gcube.portlets.user.crossmapping.client.util.CrossMapTaskStatusCell;
import org.gcube.portlets.user.crossmapping.client.util.ErrorMessage;
import org.gcube.portlets.user.crossmapping.client.util.TooltipCell;
import org.gcube.portlets.user.crossmapping.client.util.TreeNodeXMapRelationsCell;
import org.gcube.portlets.user.crossmapping.client.window.CrossMapInformationWindow;
import org.gcube.portlets.user.crossmapping.client.window.ListTaxonRelationshipsXMapWindow;
import org.gcube.portlets.user.crossmapping.client.window.SearchTreeWindow;
import org.gcube.portlets.user.crossmapping.client.window.TaskProgressWindow;
import org.gcube.portlets.user.crossmapping.client.window.TaxonDetailsWindow;
import org.gcube.portlets.user.crossmapping.client.window.UserKnowledgeWindow;
import org.gcube.portlets.user.crossmapping.client.window.XMapConfigurationWindow;
import org.gcube.portlets.user.crossmapping.client.window.XMapRelationshipDetailsWindow;
import org.gcube.portlets.user.crossmapping.shared.EntryInXMapGWT;
import org.gcube.portlets.user.crossmapping.shared.RelationshipDetailGWT;
import org.gcube.portlets.user.crossmapping.shared.TaskStatusGWT;
import org.gcube.portlets.user.crossmapping.shared.TaxonDetailGWT;
import org.gcube.portlets.user.crossmapping.shared.TaxonGWT;
import org.gcube.portlets.user.crossmapping.shared.XMapGWT;
import org.gcube.portlets.user.crossmapping.shared.XMapRelPathGWT;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.sencha.gxt.cell.core.client.ButtonCell.IconAlign;
import com.sencha.gxt.core.client.Style.SelectionMode;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.core.client.resources.ThemeStyles;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.core.client.util.ToggleGroup;
import com.sencha.gxt.data.client.loader.RpcProxy;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.TreeStore;
import com.sencha.gxt.data.shared.loader.BeforeLoadEvent;
import com.sencha.gxt.data.shared.loader.BeforeLoadEvent.BeforeLoadHandler;
import com.sencha.gxt.data.shared.loader.ChildTreeStoreBinding;
import com.sencha.gxt.data.shared.loader.FilterPagingLoadConfig;
import com.sencha.gxt.data.shared.loader.FilterPagingLoadConfigBean;
import com.sencha.gxt.data.shared.loader.LoadEvent;
import com.sencha.gxt.data.shared.loader.LoadExceptionEvent;
import com.sencha.gxt.data.shared.loader.LoadHandler;
import com.sencha.gxt.data.shared.loader.LoadExceptionEvent.LoadExceptionHandler;
import com.sencha.gxt.data.shared.loader.PagingLoadResult;
import com.sencha.gxt.data.shared.loader.PagingLoader;
import com.sencha.gxt.data.shared.loader.TreeLoader;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.Dialog.PredefinedButton;
import com.sencha.gxt.widget.core.client.TabItemConfig;
import com.sencha.gxt.widget.core.client.box.ConfirmMessageBox;
import com.sencha.gxt.widget.core.client.box.MessageBox;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.button.ToggleButton;
import com.sencha.gxt.widget.core.client.button.ToolButton;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer.BorderLayoutData;
import com.sencha.gxt.widget.core.client.container.MarginData;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.event.BeforeCollapseItemEvent;
import com.sencha.gxt.widget.core.client.event.BeforeCollapseItemEvent.BeforeCollapseItemHandler;
import com.sencha.gxt.widget.core.client.event.BeforeShowContextMenuEvent;
import com.sencha.gxt.widget.core.client.event.CellClickEvent;
import com.sencha.gxt.widget.core.client.event.CellClickEvent.CellClickHandler;
import com.sencha.gxt.widget.core.client.event.ExpandItemEvent;
import com.sencha.gxt.widget.core.client.event.ExpandItemEvent.ExpandItemHandler;
import com.sencha.gxt.widget.core.client.event.HideEvent;
import com.sencha.gxt.widget.core.client.event.HideEvent.HideHandler;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.BeforeShowContextMenuEvent.BeforeShowContextMenuHandler;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.sencha.gxt.widget.core.client.grid.GridSelectionModel;
import com.sencha.gxt.widget.core.client.grid.GridViewConfig;
import com.sencha.gxt.widget.core.client.grid.LiveGridView;
import com.sencha.gxt.widget.core.client.grid.LiveToolItem;
import com.sencha.gxt.widget.core.client.info.Info;
import com.sencha.gxt.widget.core.client.menu.Item;
import com.sencha.gxt.widget.core.client.menu.Menu;
import com.sencha.gxt.widget.core.client.menu.MenuItem;
import com.sencha.gxt.widget.core.client.toolbar.SeparatorToolItem;
import com.sencha.gxt.widget.core.client.toolbar.ToolBar;
import com.sencha.gxt.widget.core.client.treegrid.TreeGrid;
import com.sencha.gxt.widget.core.client.treegrid.TreeGridView;

/**
 * @author "Federico De Faveri defaveri@isti.cnr.it"
 *  Modification: 10/10/12 F. Quevedo. Changes:
 *		 - TaskStatus by TaskStatusGWT
 *		 - Write log messages using logging framework
 *		 - How errors are shown to the users
 *       - Only start the timer to refresh the list when panel is expanded
 *		 - Bug in visible options in context menu when the item in panel was selected previoulsy
 */
public class CrossMapsPanel extends XMapObjectsContentPanel {
	
	protected enum CrossMapViewType { GridView, TreeView}
	
	private static final String REFRESH_TOOLTIP = "Refresh the checklist grid";
	private static final String REFRESH_TOOLTIP_ERROR = "Auto-refresh disabled.";

	protected static final int REFRESH_INTERVAL = 5 * 1000; //ms
	

	protected static final XMapProperties properties = GWT.create(XMapProperties.class);
	protected ListStore<XMapGWT> store;
	protected Menu contextMenu;
	protected MenuItem cancel;
	protected MenuItem view;
	protected MenuItem viewAsGrid;
	protected MenuItem viewAsTree;	
	protected MenuItem export;
	protected MenuItem delete;
	protected MenuItem details;
	protected MenuItem info;
	protected GridSelectionModel<XMapGWT> selectionModel;
		
	
	protected TextButton refreshButton;
	protected Timer refreshTimer;	

	private Logger logger = Logger.getLogger(CrossMapsPanel.class.getName());
	
	public CrossMapsPanel(ContentPanelAppearance appearance){
		super(appearance);
		setAnimCollapse(false);
		setHeadingText("Crossmaps");

		VerticalLayoutContainer container = new VerticalLayoutContainer();
		setWidget(container);
		container.add(createToolBarLeftPanel(), new VerticalLayoutData(1, -1));
		container.add(createGridLeftPanel(), new VerticalLayoutData(1, 1));		
	
		
		//Set timer to refresh list		
		refreshTimer = new Timer() {
			@Override
			public void run() {
				refreshList(true);
			}
		};				
	}
	
	private ToolBar createToolBarLeftPanel(){
		ToolBar toolBar = new ToolBar();
		refreshButton = new TextButton();
		refreshButton.setIcon(Resources.INSTANCE.refresh());
		refreshButton.setToolTip("Refresh the cross-map grid");
		refreshButton.addSelectHandler(new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
				refreshList(false);
			}
		});
		toolBar.add(refreshButton);
	
		TextButton xmapButton = new TextButton();
		xmapButton.setIcon(Resources.INSTANCE.crossmap());
		xmapButton.setToolTip("Start a cross-map operation");
		xmapButton.addSelectHandler(new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
				onCrossMapping();
			}
		});
		toolBar.add(xmapButton);
		
		TextButton userRefinementButton = new TextButton();
		userRefinementButton.setIcon(Resources.INSTANCE.edit2());
		userRefinementButton.setToolTip("User refinement");
		userRefinementButton.addSelectHandler(new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
				UserKnowledgeWindow userKnowledgeWindow = new UserKnowledgeWindow();
				userKnowledgeWindow.show();				
			}
		});
		toolBar.add(userRefinementButton);		
		
		return toolBar;
	}

	private Grid<XMapGWT> createGridLeftPanel(){
		ColumnConfig<XMapGWT, String> nameColumn = new ColumnConfig<XMapGWT, String>(properties.shortName(), 50, "Name");			
		nameColumn.setCell(new TooltipCell<String>());
		
		ColumnConfig<XMapGWT, String> descriptionColumn = new ColumnConfig<XMapGWT, String>(properties.longName(), 50, "Description");
		descriptionColumn.setCell(new TooltipCell<String>());
		
		ColumnConfig<XMapGWT, TaskStatusGWT> statusColumn = new ColumnConfig<XMapGWT, TaskStatusGWT>(properties.status(), 100, "Status");
		statusColumn.setCell(new CrossMapTaskStatusCell());

		
		List<ColumnConfig<XMapGWT, ?>> columns = new ArrayList<ColumnConfig<XMapGWT, ?>>();
		columns.add(nameColumn);
		columns.add(descriptionColumn);
		columns.add(statusColumn);

		ColumnModel<XMapGWT> cm = new ColumnModel<XMapGWT>(columns);

		store = new ListStore<XMapGWT>(properties.key());

		final Grid<XMapGWT> grid = new Grid<XMapGWT>(store, cm);
		grid.getView().setAutoExpandColumn(nameColumn);
		grid.getView().setStripeRows(true);
		grid.getView().setColumnLines(true);
		grid.getView().setEmptyText("No entries found.");
		grid.setBorders(false);
		grid.setColumnReordering(true);
		
		
		selectionModel = new GridSelectionModel<XMapGWT>();
		selectionModel.setSelectionMode(SelectionMode.MULTI);
		grid.setSelectionModel(selectionModel);

		buildContextMenuLeftPanel();
		grid.setContextMenu(contextMenu);

		grid.addBeforeShowContextMenuHandler(new BeforeShowContextMenuHandler() {
			@Override
			public void onBeforeShowContextMenu(BeforeShowContextMenuEvent event) {
				for (XMapGWT checklist:grid.getSelectionModel().getSelectedItems()){
					logger.log(Level.FINER, "Item: "+checklist.getId());	
				}
				setupContextMenuOnLeftPanel();
			}
		});	
				
		return grid;		
	}


	protected void onExpand(){				
		logger.log(Level.FINE, "Expand xmap panel");
		super.onExpand();

		refreshList(true);		
		refreshTimer.scheduleRepeating(REFRESH_INTERVAL);		
	}
	
	protected void onCollapse(){
		logger.log(Level.FINE, "Collapse xmap panel");
		super.onCollapse();
		
		refreshTimer.cancel();
	}	

	/**
	 * TODO move to event bus
	 */
	@Override
	public void refreshList(final boolean silent) {
		if (!silent) mask("Loading cross-maps...");
		CrossMappingPortlet.service.getCrossMaps(new AsyncCallback<List<XMapGWT>>() {

			@Override
			public void onSuccess(List<XMapGWT> result) {
				setCrossMaps(result);	
				unmask();
				refreshButton.setIcon(Resources.INSTANCE.refresh());
				refreshButton.setToolTip(REFRESH_TOOLTIP);
				refreshTimer.scheduleRepeating(REFRESH_INTERVAL);				
			}

			@Override
			public void onFailure(Throwable caught) {
				if (!silent) {
					logger.log(Level.SEVERE, "Error loading cross-maps",caught);
					ErrorMessage.showError("Error loading the cross-maps",caught);						
					unmask();
				} else {
					logger.log(Level.SEVERE, "Network problems",caught);
					Info.display("Network problems", "Cross-maps grid auto-refresh disabled. Refresh it manually in order to re-enable the auto-refresh.");
					refreshTimer.cancel();
				}
				
				refreshButton.setIcon(Resources.INSTANCE.refreshwarning());
				refreshButton.setToolTip(REFRESH_TOOLTIP_ERROR);				
			}
		});
	}

	protected void onCrossMapping() {
		XMapConfigurationWindow xMapConfigurationWindow = new XMapConfigurationWindow();
		xMapConfigurationWindow.addHideHandler(new HideHandler() {

			@Override
			public void onHide(HideEvent event) {
				Timer timer = new Timer() {
					@Override
					public void run() {
						refreshList(true);
					}
				};		
				timer.schedule(1 * 1000);
			}
		});
		xMapConfigurationWindow.show();
	}	
	

	protected void buildContextMenuLeftPanel()	{
		contextMenu = new Menu();

		SelectionHandler<Item> selectionHandler = new SelectionHandler<Item>() {
			@Override
			public void onSelection(SelectionEvent<Item> event) {
				List<XMapGWT> selectedItems = selectionModel.getSelectedItems();				
				if (viewAsTree.equals(event.getSelectedItem())) doView(selectedItems,CrossMapViewType.TreeView);
				if (viewAsGrid.equals(event.getSelectedItem())) doView(selectedItems,CrossMapViewType.GridView);
				if (export.equals(event.getSelectedItem())) askParametersExport(selectedItems);
				if (info.equals(event.getSelectedItem())) doInfo(selectedItems.get(0));
				if (cancel.equals(event.getSelectedItem())) requestConfirmCancel(selectedItems);
				if (details.equals(event.getSelectedItem())) showCheckListImportDetails(selectedItems.get(0));
				if (delete.equals(event.getSelectedItem())) requestConfirmDelete(selectedItems);
			}
		};

		view = new MenuItem();
		view.setText("View");
		view.setIcon(Resources.INSTANCE.show());
		//view.addSelectionHandler(selectionHandler);
		contextMenu.add(view);
		
		Menu radioMenu = new Menu();
	    radioMenu.addSelectionHandler(selectionHandler);	 
	   
	    viewAsTree = new MenuItem("In a tree");
	    viewAsTree.setIcon(Resources.INSTANCE.tree());	    
	    radioMenu.add(viewAsTree);
	 
	    viewAsGrid = new MenuItem("In a grid");	    
	    viewAsGrid.setIcon(Resources.INSTANCE.grid());	    
	    radioMenu.add(viewAsGrid);
	    view.setSubMenu(radioMenu);			

		export = new MenuItem();
		export.setText("Export");
		export.setIcon(Resources.INSTANCE.export());
		export.addSelectionHandler(selectionHandler);
		contextMenu.add(export);

		details = new MenuItem();
		details.setText("Details");
		details.setIcon(Resources.INSTANCE.information());
		details.addSelectionHandler(selectionHandler);
		contextMenu.add(details);

		info = new MenuItem();
		info.setText("Info");
		info.setIcon(Resources.INSTANCE.information());
		info.addSelectionHandler(selectionHandler);
		contextMenu.add(info);

		cancel = new MenuItem();
		cancel.setText("Cancel");
		cancel.setIcon(Resources.INSTANCE.cancel());
		cancel.addSelectionHandler(selectionHandler);
		contextMenu.add(cancel);

		delete = new MenuItem();
		delete.setText("Delete");
		delete.setIcon(Resources.INSTANCE.delete());
		delete.addSelectionHandler(selectionHandler);
		contextMenu.add(delete);

	}

	protected void setupContextMenuOnLeftPanel() {
		List<XMapGWT> selectedItems = selectionModel.getSelectedItems();
		disableAllMenuLeftPanel();
		
		if (selectedItems.size()==1) {	
			TaskStatusGWT status = selectedItems.get(0).getStatus();
			switch (status) {
				case COMPLETED: {
					view.setVisible(true);
					info.setVisible(true);					
					delete.setVisible(true);
					export.setVisible(true);
				} break;
				case FAILED: {
					details.setVisible(true);
					delete.setVisible(true);
				} break;
				case PENDING:
				case ACTIVE:{
					details.setVisible(true);
					cancel.setVisible(true);
				} break;
				case CANCELLING: {
					details.setVisible(true);
				} break;
			}
		}	
		else if (selectedItems.size()>1){				
			int i = 1;
			boolean equivalentStatus = true;			
			TaskStatusGWT status = selectedItems.get(0).getStatus();
			boolean allCompleted = (status==TaskStatusGWT.COMPLETED);
			while (i<selectedItems.size() && equivalentStatus){
				if ((selectedItems.get(i-1).getStatus() == TaskStatusGWT.COMPLETED || selectedItems.get(i-1).getStatus() == TaskStatusGWT.FAILED) &&
						(selectedItems.get(i).getStatus() == TaskStatusGWT.COMPLETED || selectedItems.get(i).getStatus() == TaskStatusGWT.FAILED)){
					equivalentStatus=true;
					allCompleted = allCompleted && (selectedItems.get(i).getStatus() == TaskStatusGWT.COMPLETED);
					i++;					
				}					
				else if ((selectedItems.get(i-1).getStatus() == TaskStatusGWT.PENDING || selectedItems.get(i-1).getStatus() == TaskStatusGWT.ACTIVE) &&
							(selectedItems.get(i).getStatus() == TaskStatusGWT.PENDING || selectedItems.get(i).getStatus() == TaskStatusGWT.ACTIVE)){
						equivalentStatus=true;
						i++;		
				}
				else{
					equivalentStatus=false;
				}
			}
			if (equivalentStatus){
				switch (status) {
					case COMPLETED:
					case FAILED:{						
						delete.setVisible(true);
					} break;
					case PENDING:
					case ACTIVE:{							
						cancel.setVisible(true);
					} break;
				}
				if (allCompleted){
					view.setVisible(true);
					export.setVisible(true);
				}
			}
		}
	}

	protected void disableAllMenuLeftPanel() {
		int numItems = contextMenu.getWidgetCount();
		for (int i=0;i<numItems;i++){
			contextMenu.getWidget(i).setVisible(false);
		}
	}	
	
	protected void requestConfirmCancel(final List<XMapGWT> selectedItems)
	{
		StringBuilder msg = new StringBuilder("Are you sure you want to cancel the crossing map operation(s):");		
		for (XMapGWT selectedItem:selectedItems){
			msg.append(" " + selectedItem.getShortName() + ",");
		}
		msg.deleteCharAt(msg.length()-1);
		msg.append("?");
		
		final ConfirmMessageBox box = new ConfirmMessageBox("Confirm", msg.toString());			
		box.addHideHandler(new HideHandler() {
			public void onHide(HideEvent event) {
				if (box.getHideButton() == box.getButtonById(PredefinedButton.YES.name())) {
					for (XMapGWT selectedItem:selectedItems){
						doCancel(selectedItem.getTaskId(), "CrossingMap operation "+selectedItem.getShortName()+" canceled correctly");
					}
				} 
			}
		});
		box.show();
	}

	protected void doCancel(String taskId, final String description)
	{
		CrossMappingPortlet.service.cancelTask(taskId, new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {
				logger.log(Level.SEVERE, "Error cancelling crossmap",caught);
				ErrorMessage.showError("Error cancelling crossmap ",caught);	
			}

			@Override
			public void onSuccess(Void result) {
				Info.display("Task cancelled correctly", description);
				refreshList(true);
			}
		});
	}

	protected void showCheckListImportDetails(XMapGWT selectedItem)
	{
		if (selectedItem.getTaskId()==null) return;
		TaskProgressWindow progressWindow = new TaskProgressWindow("CrossMap "+selectedItem.getShortName(), selectedItem.getTaskId());
		progressWindow.addHideHandler(new HideHandler() {

			@Override
			public void onHide(HideEvent event) {
				refreshList(true);
			}
		});
		progressWindow.show();
	}

	protected void requestConfirmDelete(final List<XMapGWT> selectedItems)
	{
		StringBuilder msg = new StringBuilder("Are you sure you want to delete the crossmap(s):");		
		for (XMapGWT selectedItem:selectedItems){
			msg.append(" " + selectedItem.getShortName() + ",");
		}
		msg.deleteCharAt(msg.length()-1);
		msg.append("?");	
		
		final ConfirmMessageBox box = new ConfirmMessageBox("Confirm", msg.toString());
		
		box.addHideHandler(new HideHandler() {
			@Override
			public void onHide(HideEvent event) {
				if (box.getHideButton() == box.getButtonById(PredefinedButton.YES.name())) {
					for (XMapGWT selectedItem:selectedItems){
						doDelete(selectedItem.getId(), "CrossMap "+selectedItem.getShortName()+" deleted correctly");
					}
				} 
			}
		});
		box.show();
	}

	protected void doDelete(final String crossMapId, final String description)
	{
		CrossMappingPortlet.service.deleteCompletedCrossMap(crossMapId, new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {
				logger.log(Level.SEVERE, "Error deleting crossmap",caught);
				ErrorMessage.showError("Error deleting crossmap",caught);	
			}

			@Override
			public void onSuccess(Void result) {
				Info.display("CrossMap delete", description);
				
				//If the xmap was opened in a tabular data, we close the tab
				int tabPos = getTabPositionXMap(crossMapId);								
				if (tabPos!=-1){
					CrossMappingPortlet.tabPanel.remove(tabPos);
				}
				
				refreshList(true);
			}
		});
	}


	public void setCrossMaps(List<XMapGWT> crossMaps)
	{
		logger.log(Level.FINE, "crossMaps: "+crossMaps.size());
		List<XMapGWT> selectedItems = selectionModel.getSelectedItems();
		selectionModel.deselectAll();
		store.clear();

		store.addAll(crossMaps);
				
		//Fix bug: The status of the items that were selected could have changed since 
		//they were selected so the options in the context menu will be different.				
		if (selectedItems.size()>0){
			for (XMapGWT selectedItem: selectedItems){
				boolean found = false;
				int i = 0;
				while (i<store.size() && !found){
					XMapGWT item = store.get(i);
					if (item.getId().equals(selectedItem.getId())){
						selectionModel.select(item, true);
						found=true;
					}
					i++;
				}	
			}
			
			if (contextMenu.isVisible()){
				setupContextMenuOnLeftPanel();
			}
		}	
	}
	

	protected void doInfo(XMapGWT crossMap)	{
		CrossMapInformationWindow informationWindow = new CrossMapInformationWindow(crossMap);
		informationWindow.show();
	}

	protected void askParametersExport(final List<XMapGWT> selectedItems) {
		final MessageBox box = new MessageBox("Include accepted names", "");
		box.setPredefinedButtons(PredefinedButton.YES, PredefinedButton.NO, PredefinedButton.CANCEL);
		box.setIcon(MessageBox.ICONS.question());
		box.setMessage("Do you want to include the accepted names in the exported result(s)?");
		box.addHideHandler(new HideHandler() {

			@Override
			public void onHide(HideEvent event) {
				if (box.getHideButton() == box.getButtonById(PredefinedButton.CANCEL.name())) return;
					for (XMapGWT selectedItem:selectedItems){
							doExport(selectedItem, box.getHideButton() == box.getButtonById(PredefinedButton.YES.name()));
					}
			}
		});
		box.show();

	}

	protected void doExport(final XMapGWT crossMap, boolean includeAcceptedName) {
		CrossMappingPortlet.service.exportXmapResult(crossMap.getId(), includeAcceptedName, new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {
				logger.log(Level.SEVERE, "Error exporting xmap",caught);
				ErrorMessage.showError("Error exporting xmap",caught);				
			}

			@Override
			public void onSuccess(Void result) {
				Info.display("CrossMap export", "Export of "+crossMap.getShortName()+" started...");
			}
		});
	}
	
	
	protected void doView(final List<XMapGWT> selectedItems, CrossMapViewType viewType) {						
		for (XMapGWT crossMap:selectedItems){
			int tabPos = getTabPositionXMap(crossMap.getId());
			if (tabPos==-1){
				final VerticalLayoutContainer container = new VerticalLayoutContainer();
				container.setData("xmapId",crossMap.getId());	
				container.setData("viewType",viewType);
				container.add(createToolbarInTabPanel(crossMap,viewType), new VerticalLayoutData(1, 28));
				if (viewType==CrossMapViewType.TreeView){
					container.add(createTreeViewInTabPanel(crossMap), new VerticalLayoutData(1, 1));
				}
				else if (viewType==CrossMapViewType.GridView) {
					container.add(createGridViewInTabPanel(crossMap), new VerticalLayoutData(1, 1));
				}				
								
				CrossMappingPortlet.tabPanel.add(container, new TabItemConfig(crossMap.getShortName(), true));
				CrossMappingPortlet.tabPanel.setActiveWidget(container);					
			}	
			else { //The xmap is displayed in one tab
				VerticalLayoutContainer container = (VerticalLayoutContainer) CrossMappingPortlet.tabPanel.getWidget(tabPos);	
				CrossMapViewType oldViewType = container.getData("viewType");
				if (oldViewType!= viewType){ //the xmap was displayed as different view type
					CrossMappingPortlet.tabPanel.mask("Changing view");			
					container.setData("viewType",viewType);
					container.remove(0);
					container.remove(0);
					container.add(createToolbarInTabPanel(crossMap,viewType), new VerticalLayoutData(1, 28));
					if (viewType==CrossMapViewType.TreeView){
						container.add(createTreeViewInTabPanel(crossMap), new VerticalLayoutData(1, 1));
					}
					else if (viewType==CrossMapViewType.GridView) {
						container.add(createGridViewInTabPanel(crossMap), new VerticalLayoutData(1, 1));
					}															
					container.forceLayout();
					CrossMappingPortlet.tabPanel.unmask();	
				}
				CrossMappingPortlet.tabPanel.setActiveWidget(CrossMappingPortlet.tabPanel.getWidget(tabPos));			
			}
		}
	}
		
	
	private int getTabPositionXMap(String xmapId){
		boolean found =false;
		int i = 0;
		while (i<CrossMappingPortlet.tabPanel.getWidgetCount() && !found){
			VerticalLayoutContainer container = (VerticalLayoutContainer) CrossMappingPortlet.tabPanel.getWidget(i);			
			String xmapIdInTab = container.getData("xmapId");
			if (xmapIdInTab!=null && xmapIdInTab.equalsIgnoreCase(xmapId)){
				found = true;				
			}
			else{
				i++;
			}
		}	
		return (found?i:-1);
	}
	
	private BorderLayoutContainer createTreeViewInTabPanel(final XMapGWT xmap){				
	    final TreeGrid<TaxonGWT> leftTree = createTreeViewChecklist(xmap.getLeftChecklistId(),xmap.getId(),true) ; 
	    final TreeGrid<TaxonGWT> rightTree = createTreeViewChecklist(xmap.getRightChecklistId(),xmap.getId(),false);
	
	    //Set context menu for the trees
	    leftTree.setContextMenu(createContextMenuXMapTree(xmap, leftTree, rightTree, true));	
	    rightTree.setContextMenu(createContextMenuXMapTree(xmap, rightTree, leftTree, false));	
	    
	    //Create the content panels for each tree view
	    ContentPanel leftTreePanel = createContentPanelForTreeView(leftTree,xmap.getLeftChecklistName());
	    ContentPanel rightTreePanel = createContentPanelForTreeView(rightTree,xmap.getRightChecklistName());
	    	    
	    //Define the borderlayout container
	    BorderLayoutData eastData = new BorderLayoutData(Window.getClientWidth()/2.5);
	    eastData.setMargins(new Margins(0, 5, 0, 5));
	    eastData.setSplit(true);	    
	    eastData.setMaxSize(1000);
	    
	    BorderLayoutContainer con = new BorderLayoutContainer();	    	 
	    con.setBorders(true);
	    con.setCenterWidget(leftTreePanel, new MarginData());
	    con.setEastWidget(rightTreePanel, eastData);
	    		       
	    //Add event handlers to the trees        
	    leftTree.addCellClickHandler(new CellClickHandler() {			
			@Override
			public void onCellClick(CellClickEvent event) {				
				if (event.getSource().getColumnModel().getColumn(event.getCellIndex()).getHeader().asString().equalsIgnoreCase("Relations")){
					markRelationshipNodeSelected(leftTree,rightTree);		
				}				
			}
		});
	      	    
	    
	    rightTree.addBeforeCollapseHandler(new BeforeCollapseItemHandler<TaxonGWT>() {
			@Override
			public void onBeforeCollapse(BeforeCollapseItemEvent<TaxonGWT> event) {
				TaxonGWT taxonNode = event.getItem();
				TreeGridView<TaxonGWT> treeView =  rightTree.getTreeView();				
				if (treeView.getRow(taxonNode).getClassName().contains("xMapRelExpandedNode")){
					event.setCancelled(true);	
				}				
			}	    	
		});	    
	    
	    
	    rightTree.addCellClickHandler(new CellClickHandler() {			
			@Override
			public void onCellClick(CellClickEvent event) {
				if (event.getSource().getColumnModel().getColumn(event.getCellIndex()).getHeader().asString().equalsIgnoreCase("Relations")){		
					markRelationshipNodeSelected(rightTree,leftTree);						
				}
				
			}
	    });
	    
	    
	    leftTree.addBeforeCollapseHandler(new BeforeCollapseItemHandler<TaxonGWT>() {
			@Override
			public void onBeforeCollapse(BeforeCollapseItemEvent<TaxonGWT> event) {
				TaxonGWT taxonNode = event.getItem();
				TreeGridView<TaxonGWT> treeView =  leftTree.getTreeView();				
				if (treeView.getRow(taxonNode).getClassName().contains("xMapRelExpandedNode")){
					event.setCancelled(true);	
				}				
			}	    	
		});		    
	    	   
	    return con;
	}	
	
	private ContentPanel createContentPanelForTreeView(TreeGrid<TaxonGWT> tree, String title){
		ContentPanel cp = new ContentPanel();	   	
		cp.setHeadingText(title);
		cp.add(tree);	

		ToolButton searchTreenBtn = new ToolButton(ToolButton.SEARCH);
		searchTreenBtn.setToolTip("Search for taxa");
		searchTreenBtn.addSelectHandler(new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
				//TODO
				SearchTreeWindow searchWindow = new SearchTreeWindow(); 
				searchWindow.show();
			}
		});

		ToolButton statisticTreenBtn = new ToolButton(ToolButton.QUESTION);
		statisticTreenBtn.setToolTip("View statistics");
		statisticTreenBtn.addSelectHandler(new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
				//TODO
				Window.alert("Function not implemented yet");
			}
		});		
		
		cp.addTool(searchTreenBtn);
		cp.addTool(statisticTreenBtn);
		cp.setCollapsible(true);     

		return cp;
	}
	
	private void markRelationshipNodeSelected(TreeGrid<TaxonGWT> sourceTree, TreeGrid<TaxonGWT> targetTree){
		if (targetTree.isRendered()){ //Source tree is loaded
			//Remove style of nodes in the source tree related to the previous selected node in the target tree
			TaxonGWT prevTaxonSelTargetTree = targetTree.getData("prevTaxonSel");		
			if (prevTaxonSelTargetTree!=null){
				cleanStylesNodeRelsInOppositeTree(prevTaxonSelTargetTree,sourceTree);	
				targetTree.getView().getRow(prevTaxonSelTargetTree).removeClassName("xMapRelSelHighlightedNode");	
				targetTree.setData("prevTaxonSel",null);
				targetTree.getSelectionModel().deselectAll();
			}
			
			//Remove style of nodes in the target tree related to the previous selected node in the source tree
			TaxonGWT prevTaxonSelSourceTree = sourceTree.getData("prevTaxonSel");		
			if (prevTaxonSelSourceTree!=null){
				cleanStylesNodeRelsInOppositeTree(prevTaxonSelSourceTree,targetTree);
				sourceTree.getView().getRow(prevTaxonSelSourceTree).removeClassName("xMapRelSelHighlightedNode");				
			}
			
			//Set style of nodes in target tree related to the selected node in the source tree	
			TaxonGWT taxonSelSourceTree = sourceTree.getSelectionModel().getSelectedItem();
			sourceTree.getView().getRow(taxonSelSourceTree).removeClassName(sourceTree.getView().getAppearance().styles().rowSelected());			
			sourceTree.getView().getRow(taxonSelSourceTree).addClassName("xMapRelSelHighlightedNode");
			sourceTree.setData("prevTaxonSel", taxonSelSourceTree);
			expandNodeRelsInOppositeTree(taxonSelSourceTree,targetTree);	
			Info.display("Nodes related", taxonSelSourceTree.getXmapRels().size() + " node(s) has been marked in the other tree");			
		}
		else{
			Info.display("Marking nodes relationships","The target tree hasn't been render yet");
		}			
	}
	
	private void cleanStylesNodeRelsInOppositeTree(TaxonGWT taxon, TreeGrid<TaxonGWT> tree){
		String styleIntermediateNode = "xMapRelExpandedNode"; 
		if (taxon!=null){
			TaxonGWT auxTaxon = taxon.deepClone();
			for (XMapRelPathGWT rel: auxTaxon.getXmapRels()){
				String cssClassName = getClassNameForRelationship(rel.getRelationship());
				Stack<String> path = rel.getPath();
				while (!path.empty()){
					TreeGridView<TaxonGWT> treeView =  tree.getTreeView();			
					TaxonGWT searchTaxon = new TaxonGWT();
					searchTaxon.setTaxonId(path.pop());	    
					TaxonGWT taxonNode = tree.getStore().findModel(searchTaxon);
					if (taxonNode!=null){	 
						treeView.getRow(taxonNode).removeClassName(styleIntermediateNode);
						treeView.getRow(taxonNode).removeClassName(cssClassName);
					}
				}
			}
		}
	}
	
	
	
	private void expandNodeRelsInOppositeTree(TaxonGWT taxon,TreeGrid<TaxonGWT> tree){
		TaxonGWT auxTaxon = taxon.deepClone();

		for (XMapRelPathGWT rel: auxTaxon.getXmapRels()){
			expandPath(tree,rel.getPath(),rel.getRelationship());
		}		
	}
	
	private void expandPath(final TreeGrid<TaxonGWT> tree, final Stack<String> path, final String relationship){					
		String styleIntermediateNode = "xMapRelExpandedNode"; 
		if (!path.empty()){	
			TreeGridView<TaxonGWT> treeView =  tree.getTreeView();			
			TaxonGWT searchTaxon = new TaxonGWT();
			searchTaxon.setTaxonId(path.peek());	    
			TaxonGWT taxonNode = tree.getStore().findModel(searchTaxon);
			if (taxonNode!=null){
				path.pop();
				if (path.empty()){
					int index = treeView.findRowIndex(treeView.getRow(taxonNode));
					treeView.focusRow(index);
					treeView.getRow(taxonNode).addClassName(getClassNameForRelationship(relationship));					
				}
				else{			
					treeView.getRow(taxonNode).addClassName(styleIntermediateNode);
					if (!tree.isExpanded(taxonNode)){							
						ExpandItemHandler<TaxonGWT> expandHandler = new ExpandItemHandler<TaxonGWT>() {						 			
							@Override
							public void onExpand(ExpandItemEvent<TaxonGWT> event) {			
								expandPath(tree,path,relationship);		
							}
						};	
						
						HandlerRegistration handlerReg =  tree.addExpandHandler(expandHandler);													
						tree.setExpanded(taxonNode, true);
						//handlerReg.removeHandler();
					}
					else{												
						expandPath(tree,path,relationship);
					}						
				}	
			}
			else{
				Timer timer = new Timer() {
					@Override
					public void run() {
						expandPath(tree,path,relationship);
					}
				};		
				timer.schedule(1 * 1000);				
			}			
		}
				
	}
	
	private String getClassNameForRelationship(String relationship){
		String cssClassName = "";
		if (relationship.equalsIgnoreCase("not_found_in")){
			cssClassName= "xMapRelNotFound";;
		}
		else if (relationship.equalsIgnoreCase("corresponds")){
			cssClassName = "xMapRelCorrespond";
		}
		else if (relationship.equalsIgnoreCase("includes")){
			cssClassName = "xMapRelIncludes";
		}
		else if (relationship.equalsIgnoreCase("included_by")){
			cssClassName = "xMapRelIncludedBy";
		}
		else if (relationship.equalsIgnoreCase("overlaps")){
			cssClassName = "xMapRelOverlaps";
		}
		else if (relationship.equalsIgnoreCase("poss_name_match")){
			cssClassName = "xMapRelPossNameMatch";
		}
		else if (relationship.equalsIgnoreCase("poss_gen_trnfr")){
			cssClassName = "xMapRelPossGenTransfer";
		}	
		else{
			cssClassName = "xMapRelPossHigherMatch";
		}
		return cssClassName;
	}
	
	
	private TreeGrid<TaxonGWT> createTreeViewChecklist(final String checklistId, final String xMapId, final boolean isLeftTree){
		//Set RPC proxy to call the service to get the data to show in treeview
		RpcProxy<TaxonGWT, List<TaxonGWT>> proxy = new RpcProxy<TaxonGWT, List<TaxonGWT>>() {			 
			@Override
			public void load(TaxonGWT taxon, AsyncCallback<List<TaxonGWT>> callback) {
				String taxonId = (taxon!=null?taxon.getTaxonId():"-1");
				CrossMappingPortlet.service.getTaxonChildrenPlusRelationships(checklistId,taxonId,xMapId,isLeftTree,callback);
			}
		};
		
		//Set columns
	    TaxonProperties taxonProperties = GWT.create(TaxonProperties.class);
	    	    
		ColumnConfig<TaxonGWT, String> accNameColumn = new ColumnConfig<TaxonGWT, String>(taxonProperties.acceptedName(), 350, "Name");
		accNameColumn.setCell(new TooltipCell<String>());		
				
		ColumnConfig<TaxonGWT, String> rankColumn = new ColumnConfig<TaxonGWT, String>(taxonProperties.rank(), 100, "Rank");
		rankColumn.setCell(new TooltipCell<String>());

		ColumnConfig<TaxonGWT, String> taxonIdColumn = new ColumnConfig<TaxonGWT, String>(taxonProperties.taxonId(), 160, "TaxonId");
		taxonIdColumn.setCell(new TooltipCell<String>());		
		
		ColumnConfig<TaxonGWT, List<XMapRelPathGWT>> taxonRelationsColumn = new ColumnConfig<TaxonGWT, List<XMapRelPathGWT>>(taxonProperties.xmapRels(), 120, "Relations");
		taxonRelationsColumn.setCell(new TreeNodeXMapRelationsCell());	
			

		List<ColumnConfig<TaxonGWT, ?>> columns = new ArrayList<ColumnConfig<TaxonGWT, ?>>();
		columns.add(accNameColumn);
		columns.add(rankColumn);
		columns.add(taxonIdColumn);		
		columns.add(taxonRelationsColumn);		
		ColumnModel<TaxonGWT> cm = new ColumnModel<TaxonGWT>(columns);
		
		//Set store
		TreeStore<TaxonGWT> store = new TreeStore<TaxonGWT>(taxonProperties.key());

		//Set tree loader
		TreeLoader<TaxonGWT> loader = new TreeLoader<TaxonGWT>(proxy) {
			@Override
			public boolean hasChildren(TaxonGWT parent) {
				return (parent.getChildrenId().size()>0);
			}
		};
		loader.addLoadHandler(new ChildTreeStoreBinding<TaxonGWT>(store));		
		loader.addLoadExceptionHandler(new LoadExceptionHandler<TaxonGWT>() {
			@Override
			public void onLoadException(LoadExceptionEvent<TaxonGWT> event){
				logger.log(Level.SEVERE, "Error loading tree nodes for taxon "+ event.getLoadConfig().getTaxonId(), event.getException());
				ErrorMessage.showError("Error loading tree nodes for taxon "+ event.getLoadConfig().getTaxonId(), event.getException());	
			}	    	
		});			
			    
		
	    //Set tree grid
	    final TreeGrid<TaxonGWT> tree = new TreeGrid<TaxonGWT>(store, cm, accNameColumn);
	    tree.setTreeLoader(loader);	    
	    tree.getTreeView().setEmptyText("No entries found.");
	    tree.setLoadMask(true);
	    tree.setColumnReordering(true);
	 
	    	    
	   	TreeBundle bundle = GWT.create(TreeBundle.class);    
	    tree.getStyle().setJointCloseIcon(bundle.plus());
	    tree.getStyle().setJointOpenIcon(bundle.minus());
	    tree.getStyle().setNodeCloseIcon(bundle.folder());
	    tree.getStyle().setNodeOpenIcon(bundle.folderOpen());
	    
	    
	    //Mask the tree while loading
		loader.addBeforeLoadHandler(new BeforeLoadHandler<TaxonGWT>() {
			@Override
			public void onBeforeLoad(BeforeLoadEvent<TaxonGWT> event) {
				tree.mask("Loading children");
				
			}	    	
		}); 
	    
	    //Unmask the tree after load and expand node if only has one child 
		loader.addLoadHandler(new LoadHandler<TaxonGWT, List<TaxonGWT>>() {			
			@Override
			public void onLoad(LoadEvent<TaxonGWT, List<TaxonGWT>> event) {
				tree.unmask();
				if (event.getLoadResult().size()==1){					
					tree.setExpanded(event.getLoadResult().get(0), true);
				}
			}
		});		    
	    
	    return tree;
	}
	
		
	
	private VerticalLayoutContainer createGridViewInTabPanel(final XMapGWT crossMap){		
		final int numRegsPerLoad = 200;
		
		//Set RPC proxy to call the service to get the data to show in the grid
		RpcProxy<FilterPagingLoadConfig, PagingLoadResult<EntryInXMapGWT>> proxy = new RpcProxy<FilterPagingLoadConfig, PagingLoadResult<EntryInXMapGWT>>() {			 
		      @Override
		      public void load(FilterPagingLoadConfig loadConfig, AsyncCallback<PagingLoadResult<EntryInXMapGWT>> callback) {
				  String xmapId = crossMap.getId();	
				  loadConfig.setLimit(numRegsPerLoad);
		    	  CrossMappingPortlet.service.getEntriesInXMap(xmapId,loadConfig,callback);
		      }
		};
					 
	    		 
		//Set columns config for grid
		EntryInXMapProperties entryProperties = GWT.create(EntryInXMapProperties.class);	
		List<ColumnConfig<EntryInXMapGWT, ?>> columns = new ArrayList<ColumnConfig<EntryInXMapGWT, ?>>();
		
		ColumnConfig<EntryInXMapGWT, String> cc1 = new ColumnConfig<EntryInXMapGWT, String>(entryProperties.taxonIdLeft(), 165, "taxonIdLeft");
		cc1.setCell(new TooltipCell<String>());
		cc1.setHideable(false);
		columns.add(cc1);
		
		ColumnConfig<EntryInXMapGWT, String> cc2 = new ColumnConfig<EntryInXMapGWT, String>(entryProperties.checklistNameLeft(), 200, "checklistNameLeft");
		cc2.setCell(new TooltipCell<String>());		
		columns.add(cc2);
		
		ColumnConfig<EntryInXMapGWT, String> cc3 = new ColumnConfig<EntryInXMapGWT, String>(entryProperties.rankLeft(), 100,"rankLeft");
		cc3.setCell(new TooltipCell<String>());
		columns.add(cc3);
			
		ColumnConfig<EntryInXMapGWT, String> cc4 = new ColumnConfig<EntryInXMapGWT, String>(entryProperties.acceptedNameLeft(), 350, "acceptedNameLeft");
		cc4.setCell(new TooltipCell<String>());
		columns.add(cc4);
		
		ColumnConfig<EntryInXMapGWT, String> cc5 = new ColumnConfig<EntryInXMapGWT, String>(entryProperties.uuidLeft(), 150, "uuidLeft");
		cc5.setCell(new TooltipCell<String>());
		cc5.setHidden(true);
		columns.add(cc5);

		ColumnConfig<EntryInXMapGWT, String> cc6 = new ColumnConfig<EntryInXMapGWT, String>(entryProperties.relationship(), 200, "relationship");
		cc6.setCell(new TooltipCell<String>());
		cc6.setHideable(false);
		columns.add(cc6);
		
		ColumnConfig<EntryInXMapGWT, String> cc7 = new ColumnConfig<EntryInXMapGWT, String>(entryProperties.taxonIdRight(), 125, "taxonIdRight");
		cc7.setCell(new TooltipCell<String>());
		cc7.setHideable(false);
		columns.add(cc7);
		
		ColumnConfig<EntryInXMapGWT, String> cc8 = new ColumnConfig<EntryInXMapGWT, String>(entryProperties.checklistNameRight(), 225, "checklistNameRight");
		cc8.setCell(new TooltipCell<String>());
		columns.add(cc8);
		
		ColumnConfig<EntryInXMapGWT, String> cc9 = new ColumnConfig<EntryInXMapGWT, String>(entryProperties.rankRight(), 100, "rankRight");
		cc9.setCell(new TooltipCell<String>());
		columns.add(cc9);
		
		ColumnConfig<EntryInXMapGWT, String> cc10 = new ColumnConfig<EntryInXMapGWT, String>(entryProperties.acceptedNameRight(), 350, "acceptedNameRight");
		cc10.setCell(new TooltipCell<String>());
		columns.add(cc10);
		
		ColumnConfig<EntryInXMapGWT, String> cc11 = new ColumnConfig<EntryInXMapGWT, String>(entryProperties.uuidRight(), 150, "uuidRight");
		cc11.setCell(new TooltipCell<String>());
		cc11.setHidden(true);
		columns.add(cc11);
		
		ColumnModel<EntryInXMapGWT> cm = new ColumnModel<EntryInXMapGWT>(columns);
		
		//Set store
		ListStore<EntryInXMapGWT> store = new ListStore<EntryInXMapGWT>(entryProperties.key());
		
		//Set loader
		final PagingLoader<FilterPagingLoadConfig, PagingLoadResult<EntryInXMapGWT>> gridLoader = new PagingLoader<FilterPagingLoadConfig, PagingLoadResult<EntryInXMapGWT>>(proxy) {
	        @Override
	        protected FilterPagingLoadConfig newLoadConfig() {	        	
	        	return new FilterPagingLoadConfigBean();
	        }
	     };
		gridLoader.setRemoteSort(true);
		
		gridLoader.addLoadExceptionHandler(new LoadExceptionHandler<FilterPagingLoadConfig>() {
			@Override
			public void onLoadException(LoadExceptionEvent<FilterPagingLoadConfig> event) {
				logger.log(Level.SEVERE, "Error loading entries in this cross-map "+ crossMap.getId(), event.getException());
				ErrorMessage.showError("Error loading entries in this cross-map "+ crossMap.getId(), event.getException());	
				
			}	    	
		});	
		
		
		final LiveGridView<EntryInXMapGWT> liveGridView = new LiveGridView<EntryInXMapGWT>();
		liveGridView.setForceFit(true);
		liveGridView.setCacheSize(numRegsPerLoad);		
		liveGridView.setEmptyText("No entries found.");


		final Grid<EntryInXMapGWT> grid = new Grid<EntryInXMapGWT>(store, cm) {
			@Override
			protected void onAfterFirstAttach() {
				super.onAfterFirstAttach();
				Scheduler.get().scheduleDeferred(new ScheduledCommand() {
					@Override
					public void execute() {
						gridLoader.load();
					}
				});
			}
		};
		grid.setLoadMask(true);
		grid.setLoader(gridLoader);
		grid.setView(liveGridView);
		grid.setColumnReordering(true);
		
		liveGridView.setViewConfig(new GridViewConfig<EntryInXMapGWT>() {			
			@Override
			public String getRowStyle(EntryInXMapGWT model, int rowIndex) {
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
				else if (relationship.equalsIgnoreCase("poss_match")) {
					style = "taxaRelPossHigherMatch";
				}				
				
				return style;
			}
			
			@Override
			public String getColStyle(EntryInXMapGWT model, ValueProvider<? super EntryInXMapGWT, ?> valueProvider, int rowIndex,	int colIndex) {
				// TODO Auto-generated method stub
				return null;
			}
		});
	    
	    
	    //Set context menu
	    Menu contextMenu = createContextMenuXMapGrid(crossMap, grid);
	    grid.setContextMenu(contextMenu);	    
	    	    
	    VerticalLayoutContainer vp = new VerticalLayoutContainer();
	    vp.add(grid, new VerticalLayoutData(1, 1));

	    ToolBar toolBar = new ToolBar();
	    toolBar.add(new LiveToolItem(grid));
	    toolBar.addStyleName(ThemeStyles.getStyle().borderTop());
	    toolBar.getElement().getStyle().setProperty("borderBottom", "none");

	    vp.add(toolBar, new VerticalLayoutData(1, 25));	    

	    return vp;
	}
	
	
	private ToolBar createToolbarInTabPanel(final XMapGWT crossMap, CrossMapViewType viewType){	
		SelectHandler selectionViewHandler = new SelectHandler() { 			 
		      @Override
		      public void onSelect(SelectEvent event) {
		    	  List<XMapGWT> xmaps = new ArrayList<XMapGWT>();
		    	  xmaps.add(crossMap);
		    	  if (((ToggleButton)event.getSource()).getText().equalsIgnoreCase("grid")){
		    		  doView(xmaps,CrossMapViewType.GridView);
		    	  }
		    	  else if (((ToggleButton)event.getSource()).getText().equalsIgnoreCase("tree")){
		    		  doView(xmaps,CrossMapViewType.TreeView);
		    	  }
		      }			
		 };	
				  					
		ToggleButton btnTree = new ToggleButton("tree");
		btnTree.setIcon(Resources.INSTANCE.tree());
		btnTree.setIconAlign(IconAlign.LEFT);
		btnTree.addSelectHandler(selectionViewHandler);				
		
		ToggleButton btnGrid = new ToggleButton("grid");
		btnGrid.setIcon(Resources.INSTANCE.grid());
		btnGrid.setIconAlign(IconAlign.LEFT);
		btnGrid.addSelectHandler(selectionViewHandler);
		
		ToggleGroup btnGroup = new ToggleGroup();
		btnGroup.add(btnTree);
		btnGroup.add(btnGrid);
					
		btnGroup.setValue(viewType==CrossMapViewType.TreeView?btnTree:btnGrid);
	
		TextButton btnConfig = new TextButton();
		btnConfig.setIcon(Resources.INSTANCE.edit());
		btnConfig.setToolTip("User refinement for this crossmap");
		btnConfig.addSelectHandler(new SelectHandler() { 			 
		      @Override
		      public void onSelect(SelectEvent event) {
		    	  //TODO
		    	  UserKnowledgeWindow userRefinementWindow = new UserKnowledgeWindow();
		    	  userRefinementWindow.show();
		      }			
		 });					
		
		TextButton btnReRun = new TextButton();
		btnReRun.setIcon(Resources.INSTANCE.config());
		btnReRun.setToolTip("Re-run crossmap");
		btnReRun.addSelectHandler(new SelectHandler() { 			 
		      @Override
		      public void onSelect(SelectEvent event) {
		    	  //TODO
		    	  XMapConfigurationWindow xMapConfigurationWindow = new XMapConfigurationWindow();
		    	  xMapConfigurationWindow.show();
		      }			
		 });						
		
		ToolBar toolBar = new ToolBar();	
		toolBar.add(btnTree);
		toolBar.add(btnGrid);
		toolBar.add(new SeparatorToolItem());
		toolBar.add(btnConfig);
		toolBar.add(btnReRun);								
		return toolBar;	
	}		
	
	private Menu createContextMenuXMapGrid(final XMapGWT crossMap, final Grid<EntryInXMapGWT> grid ){
		Menu contextMenu = new Menu();	    
	    
		MenuItem viewRelationshipDetails = new MenuItem();
	    viewRelationshipDetails.setText("View relationship details");
	    viewRelationshipDetails.setIcon(Resources.INSTANCE.show());
	    viewRelationshipDetails.addSelectionHandler(new SelectionHandler<Item>() {	 
	      @Override
	      public void onSelection(SelectionEvent<Item> event) {
	    	EntryInXMapGWT selEntry = grid.getSelectionModel().getSelectedItem();
	        if (selEntry != null) {	          		        	
				String leftTaxonId = selEntry.getTaxonIdLeft();
				String rightTaxonId = selEntry.getTaxonIdRight();				
				doViewRelationshipDetail(crossMap,leftTaxonId,rightTaxonId);
	        }
	      }
	    });	
	    contextMenu.add(viewRelationshipDetails);
	    
	    MenuItem viewLeftTaxonDetails = new MenuItem();
	    viewLeftTaxonDetails.setText("View left taxon details");
	    viewLeftTaxonDetails.setIcon(Resources.INSTANCE.leftDetail());
	    viewLeftTaxonDetails.addSelectionHandler(new SelectionHandler<Item>() {	 
	      @Override
	      public void onSelection(SelectionEvent<Item> event) {
	    	EntryInXMapGWT selEntry = grid.getSelectionModel().getSelectedItem();
	        if (selEntry != null) {	          	
	        	String checklistId = crossMap.getLeftChecklistId();	        	
				String taxonId = selEntry.getTaxonIdLeft();								
				if (taxonId.equals("-1")){
					Window.alert("No taxon to show details");
				}
				else{
					doViewTaxonDetail(checklistId,crossMap.getLeftChecklistName(), taxonId);								
				}
	        }
	      }
	    });	
	    contextMenu.add(viewLeftTaxonDetails);		    
	    
	    MenuItem viewRightTaxonDetails = new MenuItem();
	    viewRightTaxonDetails.setText("View right taxon details");
	    viewRightTaxonDetails.setIcon(Resources.INSTANCE.rightDetail());
	    viewRightTaxonDetails.addSelectionHandler(new SelectionHandler<Item>() {	 
	      @Override
	      public void onSelection(SelectionEvent<Item> event) {
	    	EntryInXMapGWT selEntry = grid.getSelectionModel().getSelectedItem();
	        if (selEntry != null) {	          	
	        	String checklistId = crossMap.getRightChecklistId();	        	
				String taxonId = selEntry.getTaxonIdRight();	
				if (taxonId.equals("-1")){
					Window.alert("No taxon to show details");
				}
				else{				
					doViewTaxonDetail(checklistId,crossMap.getRightChecklistName(), taxonId);
				}
	        }
	      }
	    });	
	    contextMenu.add(viewRightTaxonDetails);	  
	    
	    return contextMenu;
	}
	
	
	private Menu createContextMenuXMapTree(final XMapGWT crossMap, final TreeGrid<TaxonGWT> sourceTree, final TreeGrid<TaxonGWT> targetTree, final boolean isLeftTree ){
		Menu contextMenu = new Menu();	    
	    				    		
		MenuItem viewRelationshipDetails = new MenuItem();
		viewRelationshipDetails.setText("View relationships details");
		viewRelationshipDetails.setIcon(Resources.INSTANCE.view());		
		viewRelationshipDetails.addSelectionHandler(new SelectionHandler<Item>() {	 
			@Override
			public void onSelection(SelectionEvent<Item> event) {
				TaxonGWT selTaxon = sourceTree.getSelectionModel().getSelectedItem();
				if (selTaxon != null) {
					markRelationshipNodeSelected(sourceTree,targetTree);					
					if (selTaxon.getXmapRels().size()>1){
						ListTaxonRelationshipsXMapWindow listRelationshipsWindow = new ListTaxonRelationshipsXMapWindow(selTaxon,crossMap,isLeftTree);
						listRelationshipsWindow.show();							
					}
					else if (selTaxon.getXmapRels().size()==1){
						String otherTaxonId = "-1";
						if (selTaxon.getXmapRels().get(0).getOtherTaxon()!=null){
							otherTaxonId = selTaxon.getXmapRels().get(0).getOtherTaxon().getTaxonId();
						}
						
						if (isLeftTree){
							String leftTaxonId = selTaxon.getTaxonId();
							String rightTaxonId = otherTaxonId;
							doViewRelationshipDetail(crossMap,leftTaxonId,rightTaxonId);
						}
						else{
							String leftTaxonId = otherTaxonId;
							String rightTaxonId = selTaxon.getTaxonId();		
							doViewRelationshipDetail(crossMap,leftTaxonId,rightTaxonId);
						}						
					}
					else{ //Taxon not process in xmap
						Info.display("View relationship details", "Taxon not consider in cross-map");
					}														
				}
			}
		});	
		contextMenu.add(viewRelationshipDetails);	  	
		
		
		MenuItem viewTaxonDetails = new MenuItem();
		viewTaxonDetails.setText("View taxon details");		
		viewTaxonDetails.setIcon(isLeftTree?Resources.INSTANCE.leftDetail():Resources.INSTANCE.rightDetail());		
		viewTaxonDetails.addSelectionHandler(new SelectionHandler<Item>() {	 
			@Override
			public void onSelection(SelectionEvent<Item> event) {
				TaxonGWT selTaxon = sourceTree.getSelectionModel().getSelectedItem();
				if (selTaxon != null) {	    
					String checklistName = (isLeftTree?crossMap.getLeftChecklistName():crossMap.getRightChecklistName()); 					
					doViewTaxonDetail(selTaxon.getChecklistId(),checklistName,selTaxon.getTaxonId());
				}
			}
		});	
		contextMenu.add(viewTaxonDetails);	  		

		return contextMenu;
	}	
	
	
	private void doViewRelationshipDetail(final XMapGWT xMap, String taxonId1, String taxonId2) {
		CrossMappingPortlet.tabPanel.mask("Loading relationship between taxon " + taxonId1 + " and taxon " + taxonId2 + " ...");
		
		CrossMappingPortlet.service.getXMapRelationshipDetail(xMap.getId(), taxonId1, taxonId2, new AsyncCallback<RelationshipDetailGWT>() {

			@Override
			public void onSuccess(RelationshipDetailGWT xMapRelationShipDetail) {
				XMapRelationshipDetailsWindow taxonDetailsWindow = new XMapRelationshipDetailsWindow(xMap,xMapRelationShipDetail,true);
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
				TaxonDetailsWindow taxonDetailsWindow = new TaxonDetailsWindow(checklistId,checklistName,taxonDetail,true);
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
