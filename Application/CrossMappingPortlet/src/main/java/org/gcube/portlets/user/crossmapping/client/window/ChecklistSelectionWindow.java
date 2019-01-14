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
package org.gcube.portlets.user.crossmapping.client.window;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.gcube.portlets.user.crossmapping.client.CrossMappingPortlet;
import org.gcube.portlets.user.crossmapping.client.model.ChecklistProperties;
import org.gcube.portlets.user.crossmapping.client.util.ErrorMessage;
import org.gcube.portlets.user.crossmapping.client.util.TaskStatusCell;
import org.gcube.portlets.user.crossmapping.client.util.TooltipCell;
import org.gcube.portlets.user.crossmapping.shared.ChecklistGWT;
import org.gcube.portlets.user.crossmapping.shared.TaskStatusGWT;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.sencha.gxt.core.client.Style.SelectionMode;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.Window;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.ShowEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.event.ShowEvent.ShowHandler;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.sencha.gxt.widget.core.client.grid.GridSelectionModel;
import com.sencha.gxt.widget.core.client.toolbar.ToolBar;

/**
 * @author "Federico De Faveri defaveri@isti.cnr.it"
 * Modification: 10/10/12 F. Quevedo. Changes:
 *		 - TaskStatus by TaskStatusGWT
 *		 - Write log messages using logging framework
 *		 - How errors are shown to the users
 *       - Alter position botton cancel and select
 */
public class ChecklistSelectionWindow extends Window {
	
	protected GridSelectionModel<ChecklistGWT> selectionModel;
	protected static final ChecklistProperties properties = GWT.create(ChecklistProperties.class);
	protected ListStore<ChecklistGWT> store;
	protected ChecklistGWT selectedItem;
	
	private Logger logger = Logger.getLogger(ChecklistSelectionWindow.class.getName());
	
	public ChecklistSelectionWindow()
	{
		setPixelSize(450, 400);
		setModal(true);
		setBlinkModal(true);
		setHeadingText("Checklist selection");
		
		ContentPanel content = new ContentPanel();
		content.setHeaderVisible(false);
		ToolBar toolBar = new ToolBar();

		TextButton refreshButton = new TextButton("Refresh");
		refreshButton.addSelectHandler(new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
				refreshList();
			}
		});
		toolBar.add(refreshButton);

		ColumnConfig<ChecklistGWT, String> nameColumn = new ColumnConfig<ChecklistGWT, String>(properties.name(), 50, "Name");
		nameColumn.setCell(new TooltipCell<String>());
		ColumnConfig<ChecklistGWT, TaskStatusGWT> statusColumn = new ColumnConfig<ChecklistGWT, TaskStatusGWT>(properties.taskStatus(), 100, "Status");
		statusColumn.setCell(new TaskStatusCell());

		List<ColumnConfig<ChecklistGWT, ?>> columns = new ArrayList<ColumnConfig<ChecklistGWT, ?>>();
		columns.add(nameColumn);
		columns.add(statusColumn);

		ColumnModel<ChecklistGWT> cm = new ColumnModel<ChecklistGWT>(columns);

		store = new ListStore<ChecklistGWT>(properties.key());

		final Grid<ChecklistGWT> grid = new Grid<ChecklistGWT>(store, cm);
		grid.getView().setAutoExpandColumn(nameColumn);
		grid.getView().setStripeRows(true);
		grid.getView().setColumnLines(true);
		grid.getView().setEmptyText("No entries found.");
		grid.setBorders(false);
		grid.setColumnReordering(true);

		selectionModel = new GridSelectionModel<ChecklistGWT>();
		selectionModel.setSelectionMode(SelectionMode.SINGLE);
		grid.setSelectionModel(selectionModel);

		VerticalLayoutContainer container = new VerticalLayoutContainer();
		container.add(toolBar, new VerticalLayoutData(1, -1));
		container.add(grid, new VerticalLayoutData(1, 1));
		
		content.add(container);
		
		add(content);
		
		addShowHandler(new ShowHandler() {			
			@Override
			public void onShow(ShowEvent event) {
				refreshList();
			}
		});
				
		final TextButton select = new TextButton("Select", new SelectHandler() {			
			@Override
			public void onSelect(SelectEvent event) {
				hide();
			}
		});
		select.setEnabled(false);
		
		selectedItem = null;
		
		selectionModel.addSelectionHandler(new SelectionHandler<ChecklistGWT>() {
			@Override
			public void onSelection(SelectionEvent<ChecklistGWT> event) {
				selectedItem = selectionModel.getSelectedItem();
				select.setEnabled(selectedItem!=null); 
			}
		});
		
		addButton(select);
		
		addButton(new TextButton("Cancel", new SelectHandler() {			
			@Override
			public void onSelect(SelectEvent event) {
				selectedItem = null;
				hide();
			}
		}));		
	}
	
	public void setChecklists(List<ChecklistGWT> checklists)
	{
		System.out.println("checklists: "+checklists.size());
		store.clear();
		store.addAll(checklists);
	}

	/**
	 * TODO move to event bus
	 */
	public void refreshList()
	{
		mask("Loading checklists...");
		CrossMappingPortlet.service.getChecklists(TaskStatusGWT.COMPLETED, new AsyncCallback<List<ChecklistGWT>>() {

			@Override
			public void onSuccess(List<ChecklistGWT> result) {
				setChecklists(result);	
				unmask();
			}

			@Override
			public void onFailure(Throwable caught) {
				unmask();
				logger.log(Level.SEVERE,"Error loading the checklists", caught);
				ErrorMessage.showError("Error loading the checklists",caught);				
			}
		});
	}

	/**
	 * @return the selectedItem
	 */
	public ChecklistGWT getSelectedItem() {
		return selectedItem;
	}	
}
