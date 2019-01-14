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

import org.gcube.portlets.user.crossmapping.shared.RelationshipPairTaxaEntryGWT;

import com.google.gwt.editor.client.Editor.Path;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;

public interface RelationshipPairTaxaEntryProperties extends PropertyAccess<RelationshipPairTaxaEntryGWT> {
		
	@Path("taxId1")
	ModelKeyProvider<RelationshipPairTaxaEntryGWT> key();

	@Path("element1")
	LabelProvider<RelationshipPairTaxaEntryGWT> nameLabel();

	ValueProvider<RelationshipPairTaxaEntryGWT, String> taxId1();

	ValueProvider<RelationshipPairTaxaEntryGWT, String> element1();

	ValueProvider<RelationshipPairTaxaEntryGWT, String> extra1();
		
	ValueProvider<RelationshipPairTaxaEntryGWT, String> relType();
	
	ValueProvider<RelationshipPairTaxaEntryGWT, String> taxId2();
	
	ValueProvider<RelationshipPairTaxaEntryGWT, String> element2();
	
	ValueProvider<RelationshipPairTaxaEntryGWT, String> extra2();
	
	ValueProvider<RelationshipPairTaxaEntryGWT, Boolean> inOthers();
	
	ValueProvider<RelationshipPairTaxaEntryGWT, Boolean> nameLevel();
	
}
