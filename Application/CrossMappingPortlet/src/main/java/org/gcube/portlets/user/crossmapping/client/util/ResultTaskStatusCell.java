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
package org.gcube.portlets.user.crossmapping.client.util;

import org.gcube.portlets.user.crossmapping.shared.TaskStatusGWT;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;

/**
 * @author "Federico De Faveri defaveri@isti.cnr.it"
 * Modification: 10/10/12 F. Quevedo. Changes
 *		 - TaskStatus by TaskStatusGWT
 */
public class ResultTaskStatusCell extends AbstractCell<TaskStatusGWT>{


	@Override
	public void render(com.google.gwt.cell.client.Cell.Context context, TaskStatusGWT value, SafeHtmlBuilder sb) {
		String color = "black";
		String text = "n/a";
		switch (value) {
			case PENDING:
			case ACTIVE: {
				color = "#FF6633";
				text = "In progress";
			} break;
			case COMPLETED: {
				color = "green";
				text = "Ready";
			} break;
			case FAILED: {
				color = "red";
				text = "Failed";
			} break;
			case CANCELLING: {
				color = "red";
				text = "Cancelling";
			} break;				
		}

		sb.appendHtmlConstant("<span style='color: "+color+"' title='"+text+"'>" + text + "</span>");
	}


}
