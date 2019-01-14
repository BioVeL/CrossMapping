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

import java.util.List;

import org.gcube.portlets.user.crossmapping.shared.RelationshipPairTaxaEntryGWT;
import org.gcube.portlets.user.crossmapping.shared.RelationshipPairTaxaGWT;

import com.google.gwt.editor.client.Editor.Path;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;

public interface RelationshipPairTaxaProperties extends PropertyAccess<RelationshipPairTaxaGWT> {
	
	@Path("leftTaxonId")
	ModelKeyProvider<RelationshipPairTaxaGWT> key();

	@Path("relationship")
	LabelProvider<RelationshipPairTaxaGWT> nameLabel();

	ValueProvider<RelationshipPairTaxaGWT, String> leftTaxonId();
	
	ValueProvider<RelationshipPairTaxaGWT, String> leftTaxonName();

	ValueProvider<RelationshipPairTaxaGWT, String> leftTaxonRank();
	
	ValueProvider<RelationshipPairTaxaGWT, String> relationship();
	
	ValueProvider<RelationshipPairTaxaGWT, String> rightTaxonId();
	
	ValueProvider<RelationshipPairTaxaGWT, String> rightTaxonName();
	
	ValueProvider<RelationshipPairTaxaGWT, String> rightTaxonRank();
	
	ValueProvider<RelationshipPairTaxaGWT, List<RelationshipPairTaxaEntryGWT>> elementsRel();

}
