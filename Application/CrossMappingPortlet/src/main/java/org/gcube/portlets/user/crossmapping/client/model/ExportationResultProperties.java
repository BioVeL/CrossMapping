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
package org.gcube.portlets.user.crossmapping.client.model;

import java.util.Date;

import org.gcube.portlets.user.crossmapping.shared.ExportationResultGWT;
import org.gcube.portlets.user.crossmapping.shared.TaskStatusGWT;

import com.google.gwt.editor.client.Editor.Path;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;

/**
 * @author "Federico De Faveri defaveri@isti.cnr.it"
 * Modification: 10/10/12 F. Quevedo. Change TaskStatus by TaskStatusGWT
 */
public interface ExportationResultProperties extends PropertyAccess<ExportationResultGWT> {

	@Path("id")
	ModelKeyProvider<ExportationResultGWT> key();

	@Path("name")
	LabelProvider<ExportationResultGWT> nameLabel();

	ValueProvider<ExportationResultGWT, String> name();

	ValueProvider<ExportationResultGWT, TaskStatusGWT> status();
	
	ValueProvider<ExportationResultGWT, Boolean> includeAcceptedName();

	ValueProvider<ExportationResultGWT, Date> exportDate();
}
