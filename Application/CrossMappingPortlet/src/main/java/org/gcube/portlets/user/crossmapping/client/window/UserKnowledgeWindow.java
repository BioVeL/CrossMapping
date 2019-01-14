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

import java.util.logging.Logger;

import org.gcube.portlets.user.crossmapping.client.resources.Resources;

import com.sencha.gxt.widget.core.client.Window;
import com.google.gwt.user.client.ui.Label;
import com.sencha.gxt.widget.core.client.FramedPanel;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;


public class UserKnowledgeWindow extends Window {
	
	private Logger logger = Logger.getLogger(UserKnowledgeWindow.class.getName());

	public UserKnowledgeWindow() {
		setPixelSize(400, 150);
		setModal(true);
		//setBlinkModal(true);
		setHeadingText("User refinement");
		getHeader().setIcon(Resources.INSTANCE.search());

		FramedPanel panel = new FramedPanel();
		panel.setWidth(350);
		panel.setHeaderVisible(false);
		panel.setBodyStyle("background: none; padding: 5px");

		VerticalLayoutContainer p = new VerticalLayoutContainer();
		panel.add(p);
		
		//TODO
		p.add(new Label("Not implemented yet"));
		
		add(panel);
	}
	
	

}
