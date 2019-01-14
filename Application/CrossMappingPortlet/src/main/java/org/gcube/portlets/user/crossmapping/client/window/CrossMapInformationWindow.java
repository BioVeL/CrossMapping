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
import org.gcube.portlets.user.crossmapping.shared.XMapGWT;

import com.sencha.gxt.widget.core.client.Window;
import com.sencha.gxt.widget.core.client.FramedPanel;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.CheckBox;
import com.sencha.gxt.widget.core.client.form.FieldLabel;
import com.sencha.gxt.widget.core.client.form.TextArea;
import com.sencha.gxt.widget.core.client.form.TextField;

/**
 * @author "Federico De Faveri defaveri@isti.cnr.it"
 * Modification: 10/10/12 F. Quevedo. Changes:
 *		 - Write log messages using logging framework
 */
public class CrossMapInformationWindow extends Window {

	protected TextField crossmappingName;
	protected TextArea crossmappingDescription;
	protected TextField leftChecklist;
	protected TextField rightChecklist;
	protected CheckBox strictCheckBox;
	protected CheckBox compareHigherTaxaCheckBox;
	
	protected String leftChecklistId;
	protected String rightChecklistId;
	
	private Logger logger = Logger.getLogger(CrossMapInformationWindow.class.getName());

	public CrossMapInformationWindow(XMapGWT crossMap)
	{
		setPixelSize(450, 400);
		setModal(true);
		setBlinkModal(true);
		setHeadingText("Cross map information");
		getHeader().setIcon(Resources.INSTANCE.information());

		FramedPanel panel = new FramedPanel();
		panel.setWidth(400);
		panel.setHeaderVisible(false);
		panel.setBodyStyle("background: none; padding: 5px");

		VerticalLayoutContainer p = new VerticalLayoutContainer();
		panel.add(p);

		crossmappingName = new TextField();
		crossmappingName.setReadOnly(true);
		crossmappingName.setValue(crossMap.getShortName());
		p.add(new FieldLabel(crossmappingName, "Name"), new VerticalLayoutData(1, -1));
		
		crossmappingDescription = new TextArea();
		crossmappingDescription.setReadOnly(true);
		crossmappingDescription.setValue(crossMap.getLongName());
		p.add(new FieldLabel(crossmappingDescription, "Description"), new VerticalLayoutData(1, -1));
		
		leftChecklist = new TextField();
		leftChecklist.setReadOnly(true);
		leftChecklist.setWidth(210);
		leftChecklist.setValue(crossMap.getLeftChecklistName());
		p.add(new FieldLabel(leftChecklist, "Left checklist"), new VerticalLayoutData(1, -1));

		rightChecklist = new TextField();
		rightChecklist.setReadOnly(true);
		rightChecklist.setWidth(210);
		rightChecklist.setValue(crossMap.getRightChecklistName());
		p.add(new FieldLabel(rightChecklist, "Right checklist"), new VerticalLayoutData(1, -1));
	
		
	    strictCheckBox = new CheckBox();
	    strictCheckBox.setReadOnly(true);
	    strictCheckBox.setValue(crossMap.isStrict());
	    p.add(new FieldLabel(strictCheckBox, "Strict matching"), new VerticalLayoutData(1, -1));
		
	    if (crossMap.isStrict()){
			TextField extra = new TextField();
			extra.setReadOnly(true);
			extra.setValue(crossMap.getIdentifyExtraTaxa().toString());
			p.add(new FieldLabel(extra, "Identify extra possible names"), new VerticalLayoutData(1, -1));
	    }
			
	    
	    compareHigherTaxaCheckBox = new CheckBox();
	    compareHigherTaxaCheckBox.setReadOnly(true);
	    compareHigherTaxaCheckBox.setValue(crossMap.isCompareHigherTaxa());
	    p.add(new FieldLabel(compareHigherTaxaCheckBox, "Match higher taxa"), new VerticalLayoutData(1, -1));
			    
	    
	    if (crossMap.isCompareHigherTaxa()){
			TextField highestRank = new TextField();
			highestRank.setReadOnly(true);
			highestRank.setValue(crossMap.getHighestRankToCompare().getName());
			p.add(new FieldLabel(highestRank, "Top rank to compare"), new VerticalLayoutData(1, -1));
	    }	    	    
	    
	    
		add(panel);

		TextButton closeButton = new TextButton("Close");
		closeButton.addSelectHandler(new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
				hide();
			}
		});
		addButton(closeButton);


		setFocusWidget(getButtonBar().getWidget(0));
	}

}
