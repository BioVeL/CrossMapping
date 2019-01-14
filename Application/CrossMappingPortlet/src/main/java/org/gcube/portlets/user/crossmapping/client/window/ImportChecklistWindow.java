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

import java.util.logging.Level;
import java.util.logging.Logger;

import org.gcube.portlets.user.crossmapping.client.CrossMappingPortlet;
import org.gcube.portlets.user.crossmapping.client.resources.Resources;
import org.gcube.portlets.user.crossmapping.client.util.ErrorMessage;
import org.gcube.portlets.user.workspace.lighttree.client.ItemType;
import org.gcube.portlets.user.workspace.lighttree.client.event.PopupEvent;
import org.gcube.portlets.user.workspace.lighttree.client.event.PopupHandler;
import org.gcube.portlets.user.workspace.lighttree.client.load.WorkspaceLightTreeLoadPopup;

import com.sencha.gxt.widget.core.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.sencha.gxt.widget.core.client.FramedPanel;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.event.InvalidEvent;
import com.sencha.gxt.widget.core.client.event.ValidEvent;
import com.sencha.gxt.widget.core.client.event.InvalidEvent.InvalidHandler;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.event.ValidEvent.ValidHandler;
import com.sencha.gxt.widget.core.client.form.FieldLabel;
import com.sencha.gxt.widget.core.client.form.TextField;
import com.sencha.gxt.widget.core.client.form.validator.MaxLengthValidator;
import com.sencha.gxt.widget.core.client.info.Info;

/**
 * @author "Federico De Faveri defaveri@isti.cnr.it"
 *
 */
public class ImportChecklistWindow extends Window {

	protected TextField checkListNameField;
	protected String workspaceItemId;
	protected TextButton importButton;
	
	private Logger logger = Logger.getLogger(ImportChecklistWindow.class.getName());

	public ImportChecklistWindow()
	{
		setPixelSize(400, 150);
		setModal(true);
		//setBlinkModal(true);
		setHeadingText("Import checklist from the workspace");
		getHeader().setIcon(Resources.INSTANCE.getImport());

		FramedPanel panel = new FramedPanel();
		panel.setWidth(350);
		panel.setHeaderVisible(false);
		panel.setBodyStyle("background: none; padding: 5px");

		VerticalLayoutContainer p = new VerticalLayoutContainer();
		panel.add(p);

		checkListNameField = new TextField();
		checkListNameField.setAllowBlank(false);
		checkListNameField.setEmptyText("Enter the checklist name...");
		checkListNameField.addValidator(new MaxLengthValidator(255));
		
		p.add(new FieldLabel(checkListNameField, "Name"), new VerticalLayoutData(1, -1));
		
		checkListNameField.addInvalidHandler(new InvalidHandler() {			
			@Override
			public void onInvalid(InvalidEvent event) {
				checkValid();
			}
		});
		checkListNameField.addValidHandler(new ValidHandler() {			
			@Override
			public void onValid(ValidEvent event) {
				checkValid();
			}
		});
		
		
		final TextField workspaceItem = new TextField();
		workspaceItem.setReadOnly(true);
		workspaceItem.setEmptyText("");
		
		TextButton browseButton = new TextButton("Browse");
		browseButton.addSelectHandler(new SelectHandler() {			
			@Override
			public void onSelect(SelectEvent event) {
				WorkspaceLightTreeLoadPopup loadPopup = new WorkspaceLightTreeLoadPopup("Select a checklist", false, true);
				loadPopup.setShowableTypes(ItemType.EXTERNAL_FILE);
				loadPopup.setSelectableTypes(ItemType.EXTERNAL_FILE);
				loadPopup.setAllowedMimeTypes("application/zip", "application/x-zip", "application/x-zip-compressed", "application/octet-stream", "application/x-compress", "application/x-compressed", "multipart/x-zip");
				loadPopup.setWidth("500px");
				
				loadPopup.addPopupHandler(new PopupHandler() {					
					@Override
					public void onPopup(PopupEvent event) {
						if (!event.isCanceled()) {
							workspaceItem.setText(event.getSelectedItem().getName());
							workspaceItemId = event.getSelectedItem().getId();
							checkValid();
						}
					}
				});
				
				loadPopup.center();
				loadPopup.show();
			}
		});
		
	    HorizontalPanel hp = new HorizontalPanel();
	    hp.add(workspaceItem);
	    hp.add(browseButton);
	    
		p.add(new FieldLabel(hp, "Checklist file"), new VerticalLayoutData(1, -1));

		add(panel);

		importButton = new TextButton("Import");
		importButton.addSelectHandler(new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
				doImport();				
			}
		});
		importButton.setEnabled(false);
		addButton(importButton);		

		TextButton cancelButton = new TextButton("Cancel");
		cancelButton.addSelectHandler(new SelectHandler() {

			@Override
			public void onSelect(SelectEvent event) {
				hide();
			}
		});
		addButton(cancelButton);


		setFocusWidget(getButtonBar().getWidget(0));
	}
	
	protected void checkValid()
	{
		boolean valid = checkListNameField.isValid(true) && workspaceItemId!=null;
		importButton.setEnabled(valid);
	}
	
	protected void doImport()
	{
		mask("Starting import...");
		final String checkListName = checkListNameField.getValue();
		CrossMappingPortlet.service.importChecklist(workspaceItemId, checkListName, new AsyncCallback<Void>() {
			
			@Override
			public void onSuccess(Void result) {
				unmask();
				hide();
				Info.display("Import started", "Import of checklist "+checkListName+" started");
			}
			
			@Override
			public void onFailure(Throwable caught) {
				unmask();
				hide();
				logger.log(Level.SEVERE,"Import failed", caught);
				ErrorMessage.showError("Import failed",caught);
			}
		});
	}

}
