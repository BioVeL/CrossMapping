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

import org.gcube.portlets.user.crossmapping.shared.EntryInXMapGWT;

import com.google.gwt.editor.client.Editor.Path;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;

public interface EntryInXMapProperties extends PropertyAccess<EntryInXMapGWT> {
	
	@Path("taxonIdLeft")
	ModelKeyProvider<EntryInXMapGWT> key();

	@Path("acceptedNameLeft")
	LabelProvider<EntryInXMapGWT> nameLabel();

	ValueProvider<EntryInXMapGWT, String> taxonIdLeft();

	ValueProvider<EntryInXMapGWT, String> checklistNameLeft();
	
	ValueProvider<EntryInXMapGWT, String> rankLeft();
	
	ValueProvider<EntryInXMapGWT, String> acceptedNameLeft();

	ValueProvider<EntryInXMapGWT, String> uuidLeft();
	
	ValueProvider<EntryInXMapGWT, String> relationship();
	
	ValueProvider<EntryInXMapGWT, String> taxonIdRight();
	
	ValueProvider<EntryInXMapGWT, String> checklistNameRight();
	
	ValueProvider<EntryInXMapGWT, String> rankRight();
	
	ValueProvider<EntryInXMapGWT, String> acceptedNameRight();
	
	ValueProvider<EntryInXMapGWT, String> uuidRight();
	
}
