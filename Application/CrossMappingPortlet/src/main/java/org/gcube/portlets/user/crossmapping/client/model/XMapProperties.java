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

import org.gcube.portlets.user.crossmapping.shared.TaskStatusGWT;
import org.gcube.portlets.user.crossmapping.shared.XMapGWT;

import com.google.gwt.editor.client.Editor.Path;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;

/**
 * @author "Federico De Faveri defaveri@isti.cnr.it"
 * Modification: 10/10/12 F. Quevedo. Changes
 *		 - TaskStatus by TaskStatusGWT
 */
public interface XMapProperties extends PropertyAccess<XMapGWT> {
	
	@Path("id")
	ModelKeyProvider<XMapGWT> key();

	@Path("shortName")
	LabelProvider<XMapGWT> nameLabel();

	ValueProvider<XMapGWT, String> shortName();

	ValueProvider<XMapGWT, String> longName();

	ValueProvider<XMapGWT, TaskStatusGWT> status();

}
