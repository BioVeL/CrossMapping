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
import org.gcube.portlets.user.crossmapping.client.model.ChecklistProperties;
import org.gcube.portlets.user.crossmapping.client.model.EntryInChecklistProperties;
import org.gcube.portlets.user.crossmapping.client.model.TaxonProperties;
import org.gcube.portlets.user.crossmapping.client.resources.Resources;
import org.gcube.portlets.user.crossmapping.client.resources.TreeBundle;
import org.gcube.portlets.user.crossmapping.client.util.ErrorMessage;
import org.gcube.portlets.user.crossmapping.client.util.TaskStatusCell;
import org.gcube.portlets.user.crossmapping.client.util.TooltipCell;
import org.gcube.portlets.user.crossmapping.client.window.ImportChecklistWindow;
import org.gcube.portlets.user.crossmapping.client.window.TaskProgressWindow;
import org.gcube.portlets.user.crossmapping.client.window.TaxonDetailsWindow;
import org.gcube.portlets.user.crossmapping.client.window.XMapConfigurationWindow;
import org.gcube.portlets.user.crossmapping.shared.ChecklistGWT;
import org.gcube.portlets.user.crossmapping.shared.EntryInChecklistGWT;
import org.gcube.portlets.user.crossmapping.shared.TaskStatusGWT;
import org.gcube.portlets.user.crossmapping.shared.TaxonDetailGWT;
import org.gcube.portlets.user.crossmapping.shared.TaxonGWT;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.sencha.gxt.cell.core.client.ButtonCell.IconAlign;
import com.sencha.gxt.cell.core.client.form.ComboBoxCell.TriggerAction;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.core.client.Style.SelectionMode;
import com.sencha.gxt.core.client.resources.ThemeStyles;
import com.sencha.gxt.core.client.util.ToggleGroup;
import com.sencha.gxt.data.client.loader.RpcProxy;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.TreeStore;
import com.sencha.gxt.data.shared.loader.BeforeLoadEvent;
import com.sencha.gxt.data.shared.loader.ChildTreeStoreBinding;
import com.sencha.gxt.data.shared.loader.FilterPagingLoadConfig;
import com.sencha.gxt.data.shared.loader.FilterPagingLoadConfigBean;
import com.sencha.gxt.data.shared.loader.LoadEvent;
import com.sencha.gxt.data.shared.loader.LoadExceptionEvent;
import com.sencha.gxt.data.shared.loader.LoadHandler;
import com.sencha.gxt.data.shared.loader.PagingLoadResult;
import com.sencha.gxt.data.shared.loader.PagingLoader;
import com.sencha.gxt.data.shared.loader.TreeLoader;
import com.sencha.gxt.data.shared.loader.BeforeLoadEvent.BeforeLoadHandler;
import com.sencha.gxt.data.shared.loader.LoadExceptionEvent.LoadExceptionHandler;
import com.sencha.gxt.widget.core.client.Dialog.PredefinedButton;
import com.sencha.gxt.widget.core.client.TabItemConfig;
import com.sencha.gxt.widget.core.client.box.ConfirmMessageBox;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.button.ToggleButton;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.event.BeforeShowContextMenuEvent;
import com.sencha.gxt.widget.core.client.event.HideEvent;
import com.sencha.gxt.widget.core.client.event.HideEvent.HideHandler;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.BeforeShowContextMenuEvent.BeforeShowContextMenuHandler;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.ComboBox;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.sencha.gxt.widget.core.client.grid.GridSelectionModel;
import com.sencha.gxt.widget.core.client.grid.GridViewConfig;
import com.sencha.gxt.widget.core.client.grid.LiveGridView;
import com.sencha.gxt.widget.core.client.grid.LiveToolItem;
import com.sencha.gxt.widget.core.client.grid.filters.GridFilters;
import com.sencha.gxt.widget.core.client.grid.filters.StringFilter;
import com.sencha.gxt.widget.core.client.info.Info;
import com.sencha.gxt.widget.core.client.menu.Item;
import com.sencha.gxt.widget.core.client.menu.Menu;
import com.sencha.gxt.widget.core.client.menu.MenuItem;
import com.sencha.gxt.widget.core.client.toolbar.SeparatorToolItem;
import com.sencha.gxt.widget.core.client.toolbar.ToolBar;
import com.sencha.gxt.widget.core.client.treegrid.TreeGrid;

/**
 * @author "Federico De Faveri defaveri@isti.cnr.it"
 * Modification: 10/10/12 F. Quevedo. Changes:
 *		 - TaskStatus by TaskStatusGWT
 *		 - Write log messages using logging framework
 *		 - How errors are shown to the users
 *       - Only start the timer to refresh the list when panel is expanded
 *		 - Bug in visible options in context menu when the item in panel was selected previoulsy
 */
public class ChecklistsPanel extends XMapObjectsContentPanel {

	protected enum ChecklistViewType { GridView, TreeView}
	
	private static final String REFRESH_TOOLTIP = "Refresh the checklist grid";
	private static final String REFRESH_TOOLTIP_ERROR = "Auto-refresh disabled.";

	protected static final int REFRESH_INTERVAL = 5 * 1000; //ms
	
	protected ListStore<ChecklistGWT> checklistsStore;
	protected Menu contextMenu;
	protected MenuItem cancel;
	protected MenuItem view;
	protected MenuItem viewAsGrid;
	protected MenuItem viewAsTree;
	protected MenuItem delete;
	protected MenuItem details;
	protected MenuItem runXmap;
	protected GridSelectionModel<ChecklistGWT> selectionModel;
	
	protected TextButton refreshButton;
	protected Timer refreshTimer;
	
	private Logger logger = Logger.getLogger(ChecklistsPanel.class.getName());

	public ChecklistsPanel(ContentPanelAppearance appearance) {
		super(appearance);
		setAnimCollapse(false);
		setHeadingText("Available checklists");

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
		
	
	protected void onExpand(){				
		logger.log(Level.FINE,"Expand checklist panel");
		super.onExpand();

		refreshList(true);		
		refreshTimer.scheduleRepeating(REFRESH_INTERVAL);		
	}
	
	protected void onCollapse(){
		logger.log(Level.FINE,"Collapse checklist panel");
		super.onCollapse();
		
		refreshTimer.cancel();
	}	
	
	
	private ToolBar createToolBarLeftPanel(){
		ToolBar toolBar = new ToolBar();
		refreshButton = new TextButton();
		refreshButton.setIcon(Resources.INSTANCE.refresh());
		refreshButton.setToolTip(REFRESH_TOOLTIP);
		refreshButton.addSelectHandler(new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
				refreshList(false);
			}
		});
		toolBar.add(refreshButton);

		TextButton importButton = new TextButton();
		importButton.setIcon(Resources.INSTANCE.getImport());
		importButton.setToolTip("Import a new checklist");
		importButton.addSelectHandler(new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
				onImport();
			}
		});
		toolBar.add(importButton);
		return toolBar;
	}
	
	private Grid<ChecklistGWT> createGridLeftPanel(){
		ChecklistProperties checklistsProperties = GWT.create(ChecklistProperties.class);
		checklistsStore = new ListStore<ChecklistGWT>(checklistsProperties.key());
		
		ColumnConfig<ChecklistGWT, String> nameColumn = new ColumnConfig<ChecklistGWT, String>(checklistsProperties.name(), 50, "Name");
		nameColumn.setCell(new TooltipCell<String>());
		
		ColumnConfig<ChecklistGWT, TaskStatusGWT> statusColumn = new ColumnConfig<ChecklistGWT, TaskStatusGWT>(checklistsProperties.taskStatus(), 100, "Status");
		statusColumn.setCell(new TaskStatusCell());

		List<ColumnConfig<ChecklistGWT, ?>> columns = new ArrayList<ColumnConfig<ChecklistGWT, ?>>();
		columns.add(nameColumn);
		columns.add(statusColumn);

		ColumnModel<ChecklistGWT> cm = new ColumnModel<ChecklistGWT>(columns);


		final Grid<ChecklistGWT> grid = new Grid<ChecklistGWT>(checklistsStore, cm);
		grid.getView().setAutoExpandColumn(nameColumn);
		grid.getView().setStripeRows(true);
		grid.getView().setColumnLines(true);
		grid.getView().setEmptyText("No entries found.");
		grid.setBorders(false);
		grid.setColumnReordering(true);		

		selectionModel = new GridSelectionModel<ChecklistGWT>();
		selectionModel.setSelectionMode(SelectionMode.MULTI);
		grid.setSelectionModel(selectionModel);

		StringFilter<ChecklistGWT> nameFilter = new StringFilter<ChecklistGWT>(checklistsProperties.name());
		GridFilters<ChecklistGWT> filters = new GridFilters<ChecklistGWT>();
		filters.initPlugin(grid);
		filters.setLocal(true);
		filters.addFilter(nameFilter);
	
		buildContextMenuLeftPanel();
		grid.setContextMenu(contextMenu);

		grid.addBeforeShowContextMenuHandler(new BeforeShowContextMenuHandler() {
			@Override
			public void onBeforeShowContextMenu(BeforeShowContextMenuEvent event) {
				for (ChecklistGWT checklist:grid.getSelectionModel().getSelectedItems()){
					logger.log(Level.FINER, "Item: "+checklist.getId());	
				}
				setupContextMenuLeftPanel();
			}
		});		
		
		return grid;
	}
	
	
	/**
	 * TODO move to event bus
	 */
	@Override
	public void refreshList(final boolean silent) {
		if (!silent) mask("Loading checklists...");
		CrossMappingPortlet.service.getChecklists(null, new AsyncCallback<List<ChecklistGWT>>() {

			@Override
			public void onSuccess(List<ChecklistGWT> result) {
				setChecklists(result);
				unmask();
				refreshButton.setIcon(Resources.INSTANCE.refresh());
				refreshButton.setToolTip(REFRESH_TOOLTIP);
				refreshTimer.scheduleRepeating(REFRESH_INTERVAL);
			}

			@Override
			public void onFailure(Throwable caught) {				
				if (!silent) {
					logger.log(Level.SEVERE,"Error loading checklists",caught);
					ErrorMessage.showError("Error loading the checklists",caught);						
					unmask();
				} else {
					logger.log(Level.SEVERE,"Network problems",caught);
					Info.display("Network problems", "Checklist grid auto-refresh disabled. Refresh it manually in order to re-enable the auto-refresh.");
					refreshTimer.cancel();
				}
				
				refreshButton.setIcon(Resources.INSTANCE.refreshwarning());
				refreshButton.setToolTip(REFRESH_TOOLTIP_ERROR);				
			}
		});
	}
	
	private void setChecklists(List<ChecklistGWT> checklists) 	{
		logger.log(Level.FINE, "checklists: "+checklists.size());
		List<ChecklistGWT> selectedItems = selectionModel.getSelectedItems();
		selectionModel.deselectAll();
		checklistsStore.clear();

		checklistsStore.addAll(checklists);
				
		//Fix bug: The status of the items that were selected could have changed since 
		//they were selected so the options in the context menu will be different.				
		if (selectedItems.size()>0){
			for (ChecklistGWT selectedItem: selectedItems){
				boolean found = false;
				int i = 0;
				while (i<checklistsStore.size() && !found){
					ChecklistGWT item = checklistsStore.get(i);
					if (item.getId().equals(selectedItem.getId())){
						selectionModel.select(item, true);
						found=true;
					}
					i++;
				}	
			}
			
			if (contextMenu.isVisible()){
				setupContextMenuLeftPanel();
			}
		}
	}	

	protected void onImport() {
		ImportChecklistWindow checklistWindow = new ImportChecklistWindow();
		checklistWindow.addHideHandler(new HideHandler() {
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
		checklistWindow.show();
	}	
	
	protected void buildContextMenuLeftPanel() {
		contextMenu = new Menu();

		SelectionHandler<Item> selectionHandler = new SelectionHandler<Item>() {
			@Override
			public void onSelection(SelectionEvent<Item> event) {
				List<ChecklistGWT> selectedItems = selectionModel.getSelectedItems();
				if (viewAsTree.equals(event.getSelectedItem())) doView(selectedItems,ChecklistViewType.TreeView);
				if (viewAsGrid.equals(event.getSelectedItem())) doView(selectedItems,ChecklistViewType.GridView);
				if (cancel.equals(event.getSelectedItem())) requestConfirmCancel(selectedItems);
				if (details.equals(event.getSelectedItem())) showCheckListImportDetails(selectedItems.get(0));
				if (delete.equals(event.getSelectedItem())) requestConfirmDelete(selectedItems);	
				if (runXmap.equals(event.getSelectedItem())) requestConfirmRunXMap(selectedItems);	
			}
		};
		
		view = new MenuItem("View");		
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

		details = new MenuItem();
		details.setText("Details");
		details.setIcon(Resources.INSTANCE.information());
		details.addSelectionHandler(selectionHandler);
		contextMenu.add(details);

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
		
		runXmap = new MenuItem();
		runXmap.setText("Run cross-map");
		runXmap.setIcon(Resources.INSTANCE.crossmap());
		runXmap.addSelectionHandler(selectionHandler);
		contextMenu.add(runXmap);				
			
	}

	protected void setupContextMenuLeftPanel() {
		List<ChecklistGWT> selectedItems = selectionModel.getSelectedItems();		
		disableAllMenuLeftPanel();
				
		if (selectedItems.size()==1) {			
			TaskStatusGWT status = selectedItems.get(0).getTaskStatus();
			switch (status) {
				case COMPLETED: {
					view.setVisible(true);
					delete.setVisible(true);
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
			TaskStatusGWT status = selectedItems.get(0).getTaskStatus();
			boolean allCompleted = (status==TaskStatusGWT.COMPLETED);
			while (i<selectedItems.size() && equivalentStatus){
				if ((selectedItems.get(i-1).getTaskStatus() == TaskStatusGWT.COMPLETED || selectedItems.get(i-1).getTaskStatus() == TaskStatusGWT.FAILED) &&
						(selectedItems.get(i).getTaskStatus() == TaskStatusGWT.COMPLETED || selectedItems.get(i).getTaskStatus() == TaskStatusGWT.FAILED)){
					equivalentStatus=true;
					allCompleted = allCompleted && (selectedItems.get(i).getTaskStatus() == TaskStatusGWT.COMPLETED);
					i++;
				}					
				else if ((selectedItems.get(i-1).getTaskStatus() == TaskStatusGWT.PENDING || selectedItems.get(i-1).getTaskStatus() == TaskStatusGWT.ACTIVE) &&
							(selectedItems.get(i).getTaskStatus() == TaskStatusGWT.PENDING || selectedItems.get(i).getTaskStatus() == TaskStatusGWT.ACTIVE)){
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
					if (selectedItems.size()==2){
						runXmap.setVisible(true);
					}					
				}
			}
		}
	}

	protected void disableAllMenuLeftPanel()	{
		int numItems = contextMenu.getWidgetCount();
		for (int i=0;i<numItems;i++){
			contextMenu.getWidget(i).setVisible(false);
		}
	}


	protected void showCheckListImportDetails(ChecklistGWT selectedItem) {
		if (selectedItem.getTaskId()==null) return;
		TaskProgressWindow progressWindow = new TaskProgressWindow("Import of checklist "+selectedItem.getName(), selectedItem.getTaskId());
		progressWindow.show();
	}

	protected void requestConfirmCancel(final List<ChecklistGWT> selectedItems) {
		StringBuilder msg = new StringBuilder("Are you sure you want to cancel the import of checklist(s):");		
		for (ChecklistGWT selectedItem:selectedItems){
			msg.append(" " + selectedItem.getName() + ",");
		}
		msg.deleteCharAt(msg.length()-1);
		msg.append("?");
		
		final ConfirmMessageBox box = new ConfirmMessageBox("Confirm", msg.toString());
		
		box.addHideHandler(new HideHandler() {
			public void onHide(HideEvent event) {
				if (box.getHideButton() == box.getButtonById(PredefinedButton.YES.name())) {
					for (ChecklistGWT selectedItem:selectedItems){
						doCancel(selectedItem.getTaskId(), "Import of checklist "+selectedItem.getName()+" canceled correctly");
					}
				} 
			}
		});
		box.show();
	}

	protected void doCancel(String taskId, final String description) {
		CrossMappingPortlet.service.cancelTask(taskId, new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {
				logger.log(Level.SEVERE, "Error cancelling importation of checklist",caught);
				ErrorMessage.showError("Error cancelling importation of checklist",caught);
			}

			@Override
			public void onSuccess(Void result) {
				Info.display("Task cancelled correctly", description);				
				refreshList(true);
			}
		});
	}

	protected void requestConfirmDelete(final List<ChecklistGWT> selectedItems) {
		StringBuilder msg = new StringBuilder("Are you sure you want to delete the checklist(s):");		
		for (ChecklistGWT selectedItem:selectedItems){
			msg.append(" " + selectedItem.getName() + ",");
		}
		msg.deleteCharAt(msg.length()-1);
		msg.append("?");	
		
		final ConfirmMessageBox box = new ConfirmMessageBox("Confirm", msg.toString());
		box.addHideHandler(new HideHandler() {
			@Override
			public void onHide(HideEvent event) {
				if (box.getHideButton() == box.getButtonById(PredefinedButton.YES.name())) {
					for (ChecklistGWT selectedItem:selectedItems){
						doDelete(selectedItem.getId(), "Checklist "+selectedItem.getName()+" deleted correctly");	
					}
				} 
			}
		});
		box.show();
	}

	protected void doDelete(final String checklistId, final String description) {
		CrossMappingPortlet.service.deleteChecklistImported(checklistId, new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {
				logger.log(Level.SEVERE,"Error deleting checklist",caught);
				ErrorMessage.showError("Error deleting checklist",caught);
			}

			@Override
			public void onSuccess(Void result) {
				Info.display("Checklist deleted", description);
				
				//If the checklist was opened in a tabular data, we close the tab
				int tabPos = getTabPositionChecklist(checklistId);
				if (tabPos!=-1){
										
					CrossMappingPortlet.tabPanel.remove(tabPos);
				}				
				
				refreshList(true);
			}
		});
	}
	
	protected void requestConfirmRunXMap(final List<ChecklistGWT> selectedItems) {
		String leftChecklistId = selectedItems.get(0).getId();
		String leftChecklistName = selectedItems.get(0).getName();
		String rightChecklistId = selectedItems.get(1).getId();
		String rightChecklistName = selectedItems.get(1).getName();
		XMapConfigurationWindow xMapWindow = new XMapConfigurationWindow(leftChecklistId,leftChecklistName,rightChecklistId,rightChecklistName,false);
		xMapWindow.addHideHandler(new HideHandler() {
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
		xMapWindow.show();
	}
		

	protected void doView(final List<ChecklistGWT> selectedItems, ChecklistViewType viewType) {			
		for (ChecklistGWT checklist:selectedItems){
			int tabPos = getTabPositionChecklist(checklist.getId());
			if (tabPos==-1){
				VerticalLayoutContainer container = new VerticalLayoutContainer();
				container.setData("checklistId",checklist.getId());	
				container.setData("viewType",viewType);
				container.add(createToolbarInTabPanel(checklist,viewType), new VerticalLayoutData(1, 28));
				if (viewType==ChecklistViewType.TreeView){
					container.add(createTreeViewInTabPanel(checklist), new VerticalLayoutData(1, 1));
				}
				else if (viewType==ChecklistViewType.GridView) {
					container.add(createGridViewInTabPanel(checklist), new VerticalLayoutData(1, 1));
				}
				
				CrossMappingPortlet.tabPanel.add(container, new TabItemConfig(checklist.getName(), true));
				CrossMappingPortlet.tabPanel.setActiveWidget(container);								
			}	
			else { //The checkilst is displayed in one tab
				VerticalLayoutContainer container = (VerticalLayoutContainer) CrossMappingPortlet.tabPanel.getWidget(tabPos);	
				ChecklistViewType oldViewType = container.getData("viewType");
				if (oldViewType!= viewType){ //the checklist was displayed as different view type
					CrossMappingPortlet.tabPanel.mask("Changing view");			
					container.setData("viewType",viewType);
					container.remove(0);
					container.remove(0);
					container.add(createToolbarInTabPanel(checklist,viewType), new VerticalLayoutData(1, 28));
					if (viewType==ChecklistViewType.TreeView){
						container.add(createTreeViewInTabPanel(checklist), new VerticalLayoutData(1, 1));
					}
					else if (viewType==ChecklistViewType.GridView) {
						container.add(createGridViewInTabPanel(checklist), new VerticalLayoutData(1, 1));
					}															
					container.forceLayout();
					CrossMappingPortlet.tabPanel.unmask();	
				}
				CrossMappingPortlet.tabPanel.setActiveWidget(CrossMappingPortlet.tabPanel.getWidget(tabPos));		
			}			
		}			
	}

	
	private int getTabPositionChecklist(String checklistId){
		boolean found =false;
		int i = 0;
		while (i<CrossMappingPortlet.tabPanel.getWidgetCount() && !found){
			VerticalLayoutContainer container = (VerticalLayoutContainer) CrossMappingPortlet.tabPanel.getWidget(i);
			String checklistIdInTab = container.getData("checklistId");
			if (checklistIdInTab!=null && checklistIdInTab.equalsIgnoreCase(checklistId)){
				found = true;				
			}
			else{
				i++;
			}
		}	
		return (found?i:-1);
	}	
		
	
	private VerticalLayoutContainer createTreeViewInTabPanel(final ChecklistGWT checklist){				
		//Set RPC proxy to call the service to get the data to show in treeview
		RpcProxy<TaxonGWT, List<TaxonGWT>> proxy = new RpcProxy<TaxonGWT, List<TaxonGWT>>() {			 
		      @Override
		      public void load(TaxonGWT taxon, AsyncCallback<List<TaxonGWT>> callback) {
				  String checklistId = checklist.getId();
				  String taxonId = (taxon!=null?taxon.getTaxonId():"-1");
		    	  CrossMappingPortlet.service.getTaxonChildren(checklistId,taxonId,callback);
		      }
		};
		 		 
	    //Set columns
	    TaxonProperties taxonProperties = GWT.create(TaxonProperties.class);	
	    
		ColumnConfig<TaxonGWT, String> accNameColumn = new ColumnConfig<TaxonGWT, String>(taxonProperties.acceptedName(), 350, "Name");
		accNameColumn.setCell(new TooltipCell<String>());		
		
		ColumnConfig<TaxonGWT, String> taxonIdColumn = new ColumnConfig<TaxonGWT, String>(taxonProperties.taxonId(), 150, "TaxonId");
		taxonIdColumn.setCell(new TooltipCell<String>());
		
		ColumnConfig<TaxonGWT, String> rankColumn = new ColumnConfig<TaxonGWT, String>(taxonProperties.rank(), 150, "Rank");
		rankColumn.setCell(new TooltipCell<String>());

		
		List<ColumnConfig<TaxonGWT, ?>> columns = new ArrayList<ColumnConfig<TaxonGWT, ?>>();
		columns.add(accNameColumn);
		columns.add(rankColumn);
		columns.add(taxonIdColumn);
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
		
			    
	    //Set context menu
	    Menu contextMenu = createContextMenuChecklistTree(checklist,tree); 	   	 	 
	    tree.setContextMenu(contextMenu);	    
	       
	    
	    VerticalLayoutContainer vp = new VerticalLayoutContainer();
	    vp.add(tree, new VerticalLayoutData(1, 1));	    	      
	    return vp;
	}
	
	
	private VerticalLayoutContainer createGridViewInTabPanel(final ChecklistGWT checklist){
		final int numRegsPerLoad = 200;
						 	    		
		//Set columns config for grid
		EntryInChecklistProperties entryProperties = GWT.create(EntryInChecklistProperties.class);	
		List<ColumnConfig<EntryInChecklistGWT, ?>> columns = new ArrayList<ColumnConfig<EntryInChecklistGWT, ?>>();
		
		ColumnConfig<EntryInChecklistGWT, String> cc1 = new ColumnConfig<EntryInChecklistGWT, String>(entryProperties.taxonId(), 180, "taxonId");
		cc1.setCell(new TooltipCell<String>());
		cc1.setHideable(false);
		columns.add(cc1);
		
		ColumnConfig<EntryInChecklistGWT, String> cc2 = new ColumnConfig<EntryInChecklistGWT, String>(entryProperties.tidyName(), 350, "tidyName");
		cc2.setCell(new TooltipCell<String>());
		cc2.setHideable(false);
		columns.add(cc2);
			
		ColumnConfig<EntryInChecklistGWT, String> cc3 = new ColumnConfig<EntryInChecklistGWT, String>(entryProperties.status(), 110, "status");
		cc3.setCell(new TooltipCell<String>());
		cc3.setHideable(false);
		columns.add(cc3);
		
		ColumnConfig<EntryInChecklistGWT, String> cc4 = new ColumnConfig<EntryInChecklistGWT, String>(entryProperties.higher(), 150, "higher");
		cc4.setCell(new TooltipCell<String>());
		columns.add(cc4);

		ColumnConfig<EntryInChecklistGWT, String> cc5 = new ColumnConfig<EntryInChecklistGWT, String>(entryProperties.species(), 125, "species");
		cc5.setCell(new TooltipCell<String>());
		columns.add(cc5);
		
		ColumnConfig<EntryInChecklistGWT, String> cc6 = new ColumnConfig<EntryInChecklistGWT, String>(entryProperties.infraspecies(), 125, "infraspecies");
		cc6.setCell(new TooltipCell<String>());		
		columns.add(cc6);
		
		ColumnConfig<EntryInChecklistGWT, String> cc7 = new ColumnConfig<EntryInChecklistGWT, String>(entryProperties.authority(), 225, "authority");
		cc7.setCell(new TooltipCell<String>());		
		columns.add(cc7);
		
		ColumnConfig<EntryInChecklistGWT, String> cc8 = new ColumnConfig<EntryInChecklistGWT, String>(entryProperties.rank(), 100, "rank");
		cc8.setCell(new TooltipCell<String>());
		columns.add(cc8);
		
		ColumnConfig<EntryInChecklistGWT, String> cc9 = new ColumnConfig<EntryInChecklistGWT, String>(entryProperties.parentId(), 180, "parentId");
		cc9.setCell(new TooltipCell<String>());
		columns.add(cc9);
		
		ColumnModel<EntryInChecklistGWT> cm = new ColumnModel<EntryInChecklistGWT>(columns);
		
		//Set store
		ListStore<EntryInChecklistGWT> store = new ListStore<EntryInChecklistGWT>(entryProperties.key());
				
		//Set RPC proxy to call the service to get the data to show in the grid
		RpcProxy<FilterPagingLoadConfig, PagingLoadResult<EntryInChecklistGWT>> proxy = new RpcProxy<FilterPagingLoadConfig, PagingLoadResult<EntryInChecklistGWT>>() {			 
		      @Override
		      public void load(FilterPagingLoadConfig loadConfig, AsyncCallback<PagingLoadResult<EntryInChecklistGWT>> callback) {
				  String checklistId = checklist.getId();	
				  loadConfig.setLimit(numRegsPerLoad);
		    	  CrossMappingPortlet.service.getEntriesInChecklist(checklistId,loadConfig,callback);
		      }
		};		
		
		//Set loader
		final PagingLoader<FilterPagingLoadConfig, PagingLoadResult<EntryInChecklistGWT>> gridLoader = new PagingLoader<FilterPagingLoadConfig, PagingLoadResult<EntryInChecklistGWT>>(proxy) {
	        @Override
	        protected FilterPagingLoadConfig newLoadConfig() {	        	
	        	return new FilterPagingLoadConfigBean();
	        }
	     };
		gridLoader.setRemoteSort(true);
		
								
		//gridLoader.addLoadHandler(new LoadResultListStoreBinding<FilterPagingLoadConfig, TaxonGWT, PagingLoadResult<TaxonGWT>>(store));
		gridLoader.addLoadExceptionHandler(new LoadExceptionHandler<FilterPagingLoadConfig>() {
			@Override
			public void onLoadException(LoadExceptionEvent<FilterPagingLoadConfig> event) {
				logger.log(Level.SEVERE, "Error loading entries in this checklist "+ checklist.getId(), event.getException());
				ErrorMessage.showError("Error loading entries in this checklist "+ checklist.getId(), event.getException());					
			}	    	
		});	
			
		
		final Grid<EntryInChecklistGWT> grid = new Grid<EntryInChecklistGWT>(store, cm) {
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
		grid.setColumnReordering(true);
		
		
		final LiveGridView<EntryInChecklistGWT> liveGridView = new LiveGridView<EntryInChecklistGWT>();
		liveGridView.setForceFit(true);
		liveGridView.setCacheSize(numRegsPerLoad);		
		liveGridView.setEmptyText("No entries found.");		
						
		liveGridView.setViewConfig(new GridViewConfig<EntryInChecklistGWT>() {			
			@Override
			public String getRowStyle(EntryInChecklistGWT model, int rowIndex) {
				String status = model.getStatus();
				String style = "";
				
				if (status.equalsIgnoreCase("accepted")){
					style = "scientificName_accepted";
				}
				else if (status.equalsIgnoreCase("synonym")){
					style = "scientificName_synonym";
				}
				else if (status.equalsIgnoreCase("misapplied")){
					style = "scientificName_misapplied";
				}		
				else {
					style = "scientificName_otherStatus";
				}
				return style;
			}
			
			@Override
			public String getColStyle(EntryInChecklistGWT model, ValueProvider<? super EntryInChecklistGWT, ?> valueProvider, int rowIndex,	int colIndex) {
				// TODO Auto-generated method stub
				return null;
			}
		});		
		grid.setView(liveGridView);
				    
	    
	    //Set context menu
	    Menu contextMenu = createContextMenuChecklistGrid(checklist, grid);
	    grid.setContextMenu(contextMenu);	    
	    	    
	    VerticalLayoutContainer vp = new VerticalLayoutContainer();
	    vp.add(grid, new VerticalLayoutData(1, 1));
	    
	    ToolBar gridFooter = new ToolBar();
	    gridFooter.add(new LiveToolItem(grid));
	    gridFooter.addStyleName(ThemeStyles.getStyle().borderTop());
	    gridFooter.getElement().getStyle().setProperty("borderBottom", "none");
	    vp.add(gridFooter, new VerticalLayoutData(1, 25));	    
	    
	    return vp;
	}
	
	private Menu createContextMenuChecklistTree(final ChecklistGWT checklist,final TreeGrid<TaxonGWT> tree){
		Menu contextMenu = new Menu();	    
	    				    
		MenuItem viewTaxonDetails = new MenuItem();
		viewTaxonDetails.setText("View taxon details");
		viewTaxonDetails.setIcon(Resources.INSTANCE.view());		
		viewTaxonDetails.addSelectionHandler(new SelectionHandler<Item>() {	 
			@Override
			public void onSelection(SelectionEvent<Item> event) {
				TaxonGWT selTaxon = tree.getSelectionModel().getSelectedItem();
				if (selTaxon != null) {	          	          
					doViewTaxonDetail(selTaxon.getChecklistId(),checklist.getName(),selTaxon.getTaxonId());
				}
			}
		});	
		contextMenu.add(viewTaxonDetails);	  

		return contextMenu;
	}	
	
	private Menu createContextMenuChecklistGrid(final ChecklistGWT checklist, final Grid<EntryInChecklistGWT> grid ){
		Menu contextMenu = new Menu();	    	    	   
	    MenuItem viewTaxonDetails = new MenuItem();
	    viewTaxonDetails.setText("View taxon details");
	    viewTaxonDetails.setIcon(Resources.INSTANCE.view());
	    viewTaxonDetails.addSelectionHandler(new SelectionHandler<Item>() {	 
		      @Override
		      public void onSelection(SelectionEvent<Item> event) {
		    	EntryInChecklistGWT selEntry = grid.getSelectionModel().getSelectedItem();
		        if (selEntry != null) {	          	          
		        	doViewTaxonDetail(checklist.getId(), checklist.getName(),selEntry.getTaxonId());
		        }
		      }
	    });	
	    contextMenu.add(viewTaxonDetails);		    
	    	   
	    return contextMenu;
	}		
	
	
	private ToolBar createToolbarInTabPanel(final ChecklistGWT checklist, ChecklistViewType viewType){	
		SelectHandler selectionViewHandler = new SelectHandler() { 			 
		      @Override
		      public void onSelect(SelectEvent event) {
		    	  List<ChecklistGWT> checklists = new ArrayList<ChecklistGWT>();
		    	  checklists.add(checklist);
		    	  if (((ToggleButton)event.getSource()).getText().equalsIgnoreCase("grid")){
		    		  doView(checklists,ChecklistViewType.GridView);
		    	  }
		    	  else if (((ToggleButton)event.getSource()).getText().equalsIgnoreCase("tree")){
		    		  doView(checklists,ChecklistViewType.TreeView);
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
		
		btnGroup.setValue(viewType==ChecklistViewType.TreeView?btnTree:btnGrid);
		
		final TextButton btnSearch = new TextButton();
		btnSearch.setIcon(Resources.INSTANCE.search());
		btnSearch.setToolTip("Search for taxa");
		/*btnSearch.addSelectHandler(new SelectHandler() { 			 
		      @Override
		      public void onSelect(SelectEvent event) {
		    	  //TODO
		    	 SearchTreeWindow searchWindow = new SearchTreeWindow();
		    	 searchWindow.show();
		      }			
		 });					
		*/
		
		EntryInChecklistProperties props = GWT.create(EntryInChecklistProperties.class);
	    ListStore<EntryInChecklistGWT> store = new ListStore<EntryInChecklistGWT>(props.key());
	    
	 
	    final ComboBox<EntryInChecklistGWT> combo = new ComboBox<EntryInChecklistGWT>(store, props.scientificName());
	    combo.setName("name");
	    combo.setForceSelection(true);
	    combo.setStore(store);
	    combo.setTriggerAction(TriggerAction.ALL);
	 
	    combo.addKeyDownHandler(new KeyDownHandler() {			
			@Override
			public void onKeyDown(KeyDownEvent event) {
				// TODO Auto-generated method stub
				if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
					VerticalLayoutContainer container = (VerticalLayoutContainer) CrossMappingPortlet.tabPanel.getActiveWidget();
					
		            Window.alert("TODO: Search");
		            btnSearch.hideMenu();
		        }
			}
		});    

	    
	    
	    Menu menu = new Menu();
	    menu.add(combo);
	    btnSearch.setMenu(menu);		
		
		
		
		TextButton btnStatistics = new TextButton();
		btnStatistics.setIcon(Resources.INSTANCE.information());
		btnStatistics.setToolTip("View statistics");
		btnStatistics.addSelectHandler(new SelectHandler() { 			 
		      @Override
		      public void onSelect(SelectEvent event) {
		    	  //TODO
		    	  Window.alert("Funtion not implemented yet");
		      }			
		 });					
		
		ToolBar toolBar = new ToolBar();		
		toolBar.add(btnTree);
		toolBar.add(btnGrid);
		toolBar.add(new SeparatorToolItem());
		toolBar.add(btnSearch);
		toolBar.add(btnStatistics);		
		return toolBar;	
	}	
	
	
	
	private void doViewTaxonDetail(final String checklistId,final String checklistName, String taxonId){							
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
