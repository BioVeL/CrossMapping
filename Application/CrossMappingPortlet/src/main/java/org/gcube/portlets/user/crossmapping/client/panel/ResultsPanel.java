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
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.gcube.portlets.user.crossmapping.client.CrossMappingPortlet;
import org.gcube.portlets.user.crossmapping.client.model.ExportationResultProperties;
import org.gcube.portlets.user.crossmapping.client.resources.Resources;
import org.gcube.portlets.user.crossmapping.client.util.ErrorMessage;
import org.gcube.portlets.user.crossmapping.client.util.ResultTaskStatusCell;
import org.gcube.portlets.user.crossmapping.client.util.TooltipCell;
import org.gcube.portlets.user.crossmapping.client.window.TaskProgressWindow;
import org.gcube.portlets.user.crossmapping.shared.ExportationResultGWT;
import org.gcube.portlets.user.crossmapping.shared.TaskStatusGWT;
import org.gcube.portlets.user.workspace.lighttree.client.event.PopupEvent;
import org.gcube.portlets.user.workspace.lighttree.client.event.PopupHandler;
import org.gcube.portlets.user.workspace.lighttree.client.save.WorkspaceLightTreeSavePopup;


import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.sencha.gxt.core.client.Style.SelectionMode;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.widget.core.client.Dialog.PredefinedButton;
import com.sencha.gxt.widget.core.client.box.ConfirmMessageBox;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.event.BeforeShowContextMenuEvent;
import com.sencha.gxt.widget.core.client.event.HideEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.BeforeShowContextMenuEvent.BeforeShowContextMenuHandler;
import com.sencha.gxt.widget.core.client.event.HideEvent.HideHandler;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.sencha.gxt.widget.core.client.grid.GridSelectionModel;
import com.sencha.gxt.widget.core.client.info.Info;
import com.sencha.gxt.widget.core.client.menu.Item;
import com.sencha.gxt.widget.core.client.menu.Menu;
import com.sencha.gxt.widget.core.client.menu.MenuItem;
import com.sencha.gxt.widget.core.client.toolbar.ToolBar;

/**
 * @author "Federico De Faveri defaveri@isti.cnr.it"
 * Modification: 10/10/12 F. Quevedo. Changes:
 *		 - TaskStatus by TaskStatusGWT
 *		 - Write log messages using logging framework
 *		 - How errors are shown to the users
 *       - Only start the timer to refresh the list when panel is expanded
 *		 - Bug in visible options in context menu when the item in panel was selected previoulsy
 */
public class ResultsPanel extends XMapObjectsContentPanel {

	private static final String REFRESH_TOOLTIP = "Refresh the checklist grid";
	private static final String REFRESH_TOOLTIP_ERROR = "Auto-refresh disabled.";

	protected static final int REFRESH_INTERVAL = 5 * 1000; //ms


	protected static final ExportationResultProperties properties = GWT.create(ExportationResultProperties.class);
	protected ListStore<ExportationResultGWT> store;
	protected Menu contextMenu;
	protected MenuItem cancel;
	protected MenuItem save;
	protected MenuItem delete;
	protected MenuItem details;
	protected MenuItem sendToPippingTool;
	protected GridSelectionModel<ExportationResultGWT> selectionModel;

	protected TextButton refreshButton;
	protected Timer refreshTimer;	
	
	private Logger logger = Logger.getLogger(ResultsPanel.class.getName());
	
	public ResultsPanel(ContentPanelAppearance appearance)
	{
		super(appearance);
		setAnimCollapse(false);
		setHeadingText("Results");

		ToolBar toolBar = new ToolBar();
		refreshButton = new TextButton();
		refreshButton.setIcon(Resources.INSTANCE.refresh());
		refreshButton.setToolTip("Refresh the results grid");
		refreshButton.addSelectHandler(new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
				refreshList(false);
			}
		});
		toolBar.add(refreshButton);

		ColumnConfig<ExportationResultGWT, String> nameColumn = new ColumnConfig<ExportationResultGWT, String>(properties.name(), 50, "Name");
		nameColumn.setCell(new TooltipCell<String>());
		
		ColumnConfig<ExportationResultGWT, Boolean> acceptedNameColumn = new ColumnConfig<ExportationResultGWT, Boolean>(properties.includeAcceptedName(), 50, "Accepted Name");
		acceptedNameColumn.setCell(new TooltipCell<Boolean>());
		
		ColumnConfig<ExportationResultGWT, Date> exportDateColumn = new ColumnConfig<ExportationResultGWT, Date>(properties.exportDate(), 50, "Export Date");
		exportDateColumn.setCell(new TooltipCell<Date>());
		
		ColumnConfig<ExportationResultGWT, TaskStatusGWT> statusColumn = new ColumnConfig<ExportationResultGWT, TaskStatusGWT>(properties.status(), 50, "Status");
		statusColumn.setCell(new ResultTaskStatusCell());

		List<ColumnConfig<ExportationResultGWT, ?>> columns = new ArrayList<ColumnConfig<ExportationResultGWT, ?>>();
		columns.add(nameColumn);
		columns.add(acceptedNameColumn);
		columns.add(exportDateColumn);
		columns.add(statusColumn);

		ColumnModel<ExportationResultGWT> cm = new ColumnModel<ExportationResultGWT>(columns);

		store = new ListStore<ExportationResultGWT>(properties.key());

		final Grid<ExportationResultGWT> grid = new Grid<ExportationResultGWT>(store, cm);
		grid.getView().setAutoExpandColumn(nameColumn);
		grid.getView().setStripeRows(true);
		grid.getView().setColumnLines(true);
		grid.getView().setEmptyText("No entries found.");
		grid.setBorders(false);
		grid.setColumnReordering(true);

		selectionModel = new GridSelectionModel<ExportationResultGWT>();
		selectionModel.setSelectionMode(SelectionMode.MULTI);
		grid.setSelectionModel(selectionModel);

		buildContextMenu();
		grid.setContextMenu(contextMenu);

		grid.addBeforeShowContextMenuHandler(new BeforeShowContextMenuHandler() {

			@Override
			public void onBeforeShowContextMenu(BeforeShowContextMenuEvent event) {
				for (ExportationResultGWT result:grid.getSelectionModel().getSelectedItems()) {
					logger.log(Level.FINER, "Item: "+result.getId());	
				}

				setupContextMenu();
			}
		});

		VerticalLayoutContainer container = new VerticalLayoutContainer();
		setWidget(container);

		container.add(toolBar, new VerticalLayoutData(1, -1));
		container.add(grid, new VerticalLayoutData(1, 1));

		refreshTimer = new Timer() {
			@Override
			public void run() {
				refreshList(true);
			}
		};				
	}
	
	
	protected void onExpand(){
		logger.log(Level.FINE, "Expand results panel");
		super.onExpand();
		
		refreshList(true);		
		refreshTimer.scheduleRepeating(REFRESH_INTERVAL);	
	}
	
	protected void onCollapse(){
		logger.log(Level.FINE, "Collapse results panel");
		super.onCollapse();
		
		refreshTimer.cancel();
	}

	protected void buildContextMenu()
	{
		contextMenu = new Menu();

		SelectionHandler<Item> selectionHandler = new SelectionHandler<Item>() {
			@Override
			public void onSelection(SelectionEvent<Item> event) {
				onContextMenuSelection(event);
			}
		};

		save = new MenuItem();
		save.setText("Save");
		save.setIcon(Resources.INSTANCE.save());
		save.addSelectionHandler(selectionHandler);
		contextMenu.add(save);

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
		
		sendToPippingTool = new MenuItem();
		sendToPippingTool.setText("Send to pipping tool");
		sendToPippingTool.setIcon(Resources.INSTANCE.send());
		sendToPippingTool.addSelectionHandler(selectionHandler);
		contextMenu.add(sendToPippingTool);
				

	}

	protected void setupContextMenu()
	{
		List<ExportationResultGWT> selectedItems = selectionModel.getSelectedItems();
		disableAllMenu();
		
		if (selectedItems.size()==1) {	
			TaskStatusGWT status = selectedItems.get(0).getStatus();
			switch (status) {
				case COMPLETED: {
					save.setVisible(true);
					delete.setVisible(true);
					sendToPippingTool.setVisible(true);
				} break;
				case FAILED: {
					details.setVisible(true);
					delete.setVisible(true);
				} break;
				case PENDING:
				case ACTIVE: {
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
					//save.setVisible(true);
					sendToPippingTool.setVisible(true);
				}
			}
		}
	}

	protected void disableAllMenu()	{
		int numItems = contextMenu.getWidgetCount();
		for (int i=0;i<numItems;i++){
			contextMenu.getWidget(i).setVisible(false);
		}
	}

	protected void onContextMenuSelection(SelectionEvent<Item> event) {
		List<ExportationResultGWT> selectedItems = selectionModel.getSelectedItems();
		if (cancel.equals(event.getSelectedItem())) requestConfirmCancel(selectedItems);
		if (details.equals(event.getSelectedItem())) showCheckListImportDetails(selectedItems.get(0));
		if (delete.equals(event.getSelectedItem())) requestConfirmDelete(selectedItems);
		if (save.equals(event.getSelectedItem())) doSave(selectedItems.get(0));
		if (sendToPippingTool.equals(event.getSelectedItem())) requestConfirmSendResultsToPippingTool(selectedItems);
	}

	protected void requestConfirmCancel(final List<ExportationResultGWT> selectedItems)
	{
		StringBuilder msg = new StringBuilder("Are you sure you want to cancel the result export operation(s) for:");		
		for (ExportationResultGWT selectedItem:selectedItems){
			msg.append(" " + selectedItem.getName() + ",");
		}
		msg.deleteCharAt(msg.length()-1);
		msg.append("?");
		
		final ConfirmMessageBox box = new ConfirmMessageBox("Confirm", msg.toString());			
		box.addHideHandler(new HideHandler() {
			public void onHide(HideEvent event) {
				if (box.getHideButton() == box.getButtonById(PredefinedButton.YES.name())) {
					for (ExportationResultGWT selectedItem:selectedItems){
						doCancel(selectedItem.getTaskId(), "Export operation "+selectedItem.getName()+" canceled correctly");
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
				logger.log(Level.SEVERE, "Error cancelling exportation",caught);
				ErrorMessage.showError("Error cancelling exportation",caught);
			}

			@Override
			public void onSuccess(Void result) {
				Info.display("Export cancelled correctly", description);
				refreshList(true);
			}
		});
	}

	protected void showCheckListImportDetails(ExportationResultGWT selectedItem)
	{
		if (selectedItem.getTaskId()==null) return;
		TaskProgressWindow progressWindow = new TaskProgressWindow("Export "+selectedItem.getName(), selectedItem.getTaskId());
		progressWindow.addHideHandler(new HideHandler() {

			@Override
			public void onHide(HideEvent event) {
				refreshList(true);
			}
		});
		progressWindow.show();
	}

	protected void requestConfirmDelete(final List<ExportationResultGWT> selectedItems)
	{
		StringBuilder msg = new StringBuilder("Are you sure you want to delete the export operation(s) for:");		
		for (ExportationResultGWT selectedItem:selectedItems){
			msg.append(" " + selectedItem.getName() + ",");
		}
		msg.deleteCharAt(msg.length()-1);
		msg.append("?");		
		
		final ConfirmMessageBox box = new ConfirmMessageBox("Confirm", msg.toString());			
		box.addHideHandler(new HideHandler() {
			public void onHide(HideEvent event) {
				if (box.getHideButton() == box.getButtonById(PredefinedButton.YES.name())) {
					for (ExportationResultGWT selectedItem:selectedItems){
						doDelete(selectedItem.getId(), "Exported result "+selectedItem.getName()+" deleted correctly");
					}
				} 
			}
		});
		box.show();
	}

	protected void doDelete(String exportId, final String description)
	{
		CrossMappingPortlet.service.deleteCrossMapResultsExported(exportId, new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {
				logger.log(Level.SEVERE, "Error deleting exported results",caught);
				ErrorMessage.showError("Error deleting exported results",caught);	
			}

			@Override
			public void onSuccess(Void result) {
				Info.display("Exported result delete", description);
				refreshList(true);
			}
		});
	}

	
	protected void requestConfirmSendResultsToPippingTool(final List<ExportationResultGWT> selectedItems)
	{
		StringBuilder msg = new StringBuilder("Are you sure you want to send the result of the cross-map(s):");		
		for (ExportationResultGWT selectedItem:selectedItems){
			msg.append(" " + selectedItem.getName() + ",");
		}
		msg.deleteCharAt(msg.length()-1);
		msg.append(" to the pipping tool?");
		
		final ConfirmMessageBox box = new ConfirmMessageBox("Confirm", msg.toString());			
		box.addHideHandler(new HideHandler() {
			public void onHide(HideEvent event) {
				if (box.getHideButton() == box.getButtonById(PredefinedButton.YES.name())) {
					for (ExportationResultGWT selectedItem:selectedItems){
						sendResultsToPippingTool(selectedItem.getId());
					}
				} 
			}
		});
		box.show();
	}

	protected void sendResultsToPippingTool(String exportId) {
		//TODO:
		Window.alert("Function not yet implemented");
	}	
	
	

	public void setResults(List<ExportationResultGWT> results)
	{
		logger.log(Level.FINE,"results: "+results.size());
		List<ExportationResultGWT> selectedItems = selectionModel.getSelectedItems();
		selectionModel.deselectAll();
		store.clear();

		store.addAll(results);
				
		//Fix bug: The status of the items that were selected could have changed since 
		//they were selected so the options in the context menu will be different.				
		if (selectedItems.size()>0){
			for (ExportationResultGWT selectedItem: selectedItems){
				boolean found = false;
				int i = 0;
				while (i<store.size() && !found){
					ExportationResultGWT item = store.get(i);
					if (item.getId().equals(selectedItem.getId())){
						selectionModel.select(item, true);
						found=true;
					}
					i++;
				}	
			}
			
			if (contextMenu.isVisible()){
				setupContextMenu();
			}
		}
	}

	/**
	 * TODO move to event bus
	 */
	@Override
	public void refreshList(final boolean silent)
	{
		if (!silent) mask("Loading...");
		CrossMappingPortlet.service.getExportationResults(new AsyncCallback<List<ExportationResultGWT>>() {

			@Override
			public void onSuccess(List<ExportationResultGWT> results) {
				setResults(results);	
				unmask();
				refreshButton.setIcon(Resources.INSTANCE.refresh());
				refreshButton.setToolTip(REFRESH_TOOLTIP);
				refreshTimer.scheduleRepeating(REFRESH_INTERVAL);				
			}

			@Override
			public void onFailure(Throwable caught) {
				if (!silent) {
					logger.log(Level.SEVERE, "Error loading  xmaps results",caught);
					ErrorMessage.showError("Error loading the  xmaps results",caught);						
					unmask();
				} else {
					logger.log(Level.SEVERE, "Network problems",caught);
					Info.display("Network problems", "Xmaps results grid auto-refresh disabled. Refresh it manually in order to re-enable the auto-refresh.");
					refreshTimer.cancel();
				}
				
				refreshButton.setIcon(Resources.INSTANCE.refreshwarning());
				refreshButton.setToolTip(REFRESH_TOOLTIP_ERROR);													
			}
		});
	}

	protected void doSave(final ExportationResultGWT result)
	{
		WorkspaceLightTreeSavePopup popup = new WorkspaceLightTreeSavePopup("Save crossmapping results in the workspace", true, result.getName() + ".zip");

		popup.addPopupHandler(new PopupHandler() {

			@Override
			public void onPopup(PopupEvent event) {
				if (!event.isCanceled()) {
					Info.display("Result export", "Saving the result on the workspace");
					String wsFolderItemId = event.getSelectedItem().getId();
					String wsFfileName = event.getName();
					CrossMappingPortlet.service.saveExportResultsCrossMapInWorkspace(result.getName(), result.getResultFileURL(), wsFolderItemId, wsFfileName, new AsyncCallback<Void>() {
						public void onSuccess(Void result) {
							Info.display("Result export", "The file with the results of the crossmapping has been saved successfully into the workspace");
						}
						public void onFailure(Throwable caught) {
							logger.log(Level.SEVERE, "Result export error",caught);
							ErrorMessage.showError("Result export error", caught);
						}
					});
				}
			}
		});

		popup.center();
		popup.setWidth("500px");
		popup.show();
	}
}
