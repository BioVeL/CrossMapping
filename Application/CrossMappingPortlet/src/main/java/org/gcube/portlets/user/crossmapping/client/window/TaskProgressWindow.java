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
import org.gcube.portlets.user.crossmapping.shared.TaskProgressGWT;

import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.sencha.gxt.widget.core.client.ProgressBar;
import com.sencha.gxt.widget.core.client.Window;
import com.sencha.gxt.widget.core.client.FramedPanel;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.event.HideEvent;
import com.sencha.gxt.widget.core.client.event.HideEvent.HideHandler;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.ShowEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.event.ShowEvent.ShowHandler;
import com.sencha.gxt.widget.core.client.form.FieldLabel;
import com.sencha.gxt.widget.core.client.form.TextArea;
import com.sencha.gxt.widget.core.client.form.TextField;

/**
 * @author "Federico De Faveri defaveri@isti.cnr.it"
 * Modification: 10/10/12 F. Quevedo. Changes:
 *		 - Write log messages using logging framework
 *
 */
public class TaskProgressWindow extends Window {
	
	protected static final int REFRESH_INTERVAL_TASK_PROGRESS = 2 * 1000; //ms

	protected TextField taskName;
	protected TextField taskStatus;
	protected ProgressBar progressBar;
	protected TextArea taskDetails;

	private Logger logger = Logger.getLogger(TaskProgressWindow.class.getName());
	
	public TaskProgressWindow(String title, final String taskId)
	{
		setPixelSize(400, 350);
		setModal(true);
		setBlinkModal(true);
		setHeadingText(title);
		getHeader().setIcon(Resources.INSTANCE.information());

		FramedPanel panel = new FramedPanel();
		panel.setWidth(350);
		panel.setHeaderVisible(false);
		panel.setBodyStyle("background: none; padding: 5px");

		VerticalLayoutContainer p = new VerticalLayoutContainer();
		panel.add(p);

		taskName = new TextField();
		taskName.setReadOnly(true);
		p.add(new FieldLabel(taskName, "Task name"), new VerticalLayoutData(1, -1));
		
		taskStatus = new TextField();
		taskStatus.setReadOnly(true);
		p.add(new FieldLabel(taskStatus, "Task status"), new VerticalLayoutData(1, -1));
		
		progressBar = new ProgressBar();
		p.add(new FieldLabel(progressBar, "Progress"), new VerticalLayoutData(1, -1));

		taskDetails = new TextArea();
		taskDetails.setReadOnly(true);
		taskDetails.setHeight(170);
		p.add(new FieldLabel(taskDetails, "Details"), new VerticalLayoutData(1, -1));
		
		add(panel);
		
		final Timer timer = new Timer() {			
			@Override
			public void run() {
				loadTaskProgress(taskId, true);
			}
		};

		TextButton closeButton = new TextButton("Close");
		closeButton.addSelectHandler(new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
				hide();
			}
		});
		addButton(closeButton);

		addShowHandler(new ShowHandler() {			
			@Override
			public void onShow(ShowEvent event) {
				loadTaskProgress(taskId, false);
				timer.scheduleRepeating(REFRESH_INTERVAL_TASK_PROGRESS);
			}
		});
		
		addHideHandler(new HideHandler() {			
			@Override
			public void onHide(HideEvent event) {
				timer.cancel();
			}
		});

		setFocusWidget(getButtonBar().getWidget(0));
	}
	
	protected void loadTaskProgress(String id, final boolean background) {
		if (!background) mask("Loading task information...");
		CrossMappingPortlet.service.getTaskProgress(id, new AsyncCallback<TaskProgressGWT>() {
			
			@Override
			public void onSuccess(TaskProgressGWT result) {
				setTaskProgress(result);
				unmask();
			}
			
			@Override
			public void onFailure(Throwable caught) {
				logger.log(Level.SEVERE, "Error loading the task information", caught);	
				ErrorMessage.showError("Error loading the task information",caught);
				if (!background) unmask();
			}
		});
	}
	
	protected void setTaskProgress(TaskProgressGWT taskProgress)
	{
		taskName.setText(taskProgress.getTaskName());
		taskStatus.setText(taskProgress.getStatus().value());
		double percentage = (taskProgress.getPercentage()>0)?(double)taskProgress.getPercentage()/100.00:0;
		progressBar.updateProgress(percentage, taskProgress.getPercentage()+"%");
		taskDetails.setText(taskProgress.getDetails());
	}

}
