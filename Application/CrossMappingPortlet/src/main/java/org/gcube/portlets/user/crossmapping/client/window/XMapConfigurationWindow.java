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

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.gcube.portlets.user.crossmapping.client.CrossMappingPortlet;
import org.gcube.portlets.user.crossmapping.client.resources.Resources;
import org.gcube.portlets.user.crossmapping.client.util.ErrorMessage;
import org.gcube.portlets.user.crossmapping.shared.ChecklistGWT;
import org.gcube.portlets.user.crossmapping.shared.IdentifyExtraTaxaTypeGWT;
import org.gcube.portlets.user.crossmapping.shared.TaxonomicRankGWT;
import org.gcube.portlets.user.crossmapping.shared.UserKnowledgeLevelForRefinementGWT;


import com.sencha.gxt.cell.core.client.form.ComboBoxCell.TriggerAction;
import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.widget.core.client.Window;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.sencha.gxt.widget.core.client.FramedPanel;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.event.HideEvent;
import com.sencha.gxt.widget.core.client.event.InvalidEvent;
import com.sencha.gxt.widget.core.client.event.ValidEvent;
import com.sencha.gxt.widget.core.client.event.HideEvent.HideHandler;
import com.sencha.gxt.widget.core.client.event.InvalidEvent.InvalidHandler;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.event.ValidEvent.ValidHandler;
import com.sencha.gxt.widget.core.client.form.CheckBox;
import com.sencha.gxt.widget.core.client.form.FieldLabel;
import com.sencha.gxt.widget.core.client.form.SimpleComboBox;
import com.sencha.gxt.widget.core.client.form.TextArea;
import com.sencha.gxt.widget.core.client.form.TextField;
import com.sencha.gxt.widget.core.client.form.validator.MaxLengthValidator;
import com.sencha.gxt.widget.core.client.info.Info;

/**
 * @author "Federico De Faveri defaveri@isti.cnr.it"
 * Modification: 10/10/12 F. Quevedo. Changes:
 *		 - Write log messages using logging framework
 *		 - How errors are shown to the users
 *
 */
public class XMapConfigurationWindow extends Window {

	protected TextField crossmappingName;
	protected TextArea crossmappingDescription;
	protected TextField leftChecklist;
	protected TextField rightChecklist;
	protected CheckBox strictCheckBox;
	protected SimpleComboBox<IdentifyExtraTaxaTypeGWT> extra;

	protected CheckBox higherTaxaCheckBox;
	protected SimpleComboBox<TaxonomicRankGWT> highestTaxonomicRank;
	protected SimpleComboBox<UserKnowledgeLevelForRefinementGWT> userRefinement;

	protected String leftChecklistId;
	protected String rightChecklistId;
	protected TextButton runButton;

	private Logger logger = Logger.getLogger(XMapConfigurationWindow.class.getName());

	public XMapConfigurationWindow(){
		setupWidget(false);
	}

	public XMapConfigurationWindow(String iniLeftChecklistId, String iniLeftChecklistName,
			String iniRightChecklistId, String iniRightChecklistName, boolean isReRun) {

		setupWidget(isReRun);

		//Set the initial left and right checklists selected	
		if (iniLeftChecklistId!=null && !iniLeftChecklistId.isEmpty()){
			leftChecklistId = iniLeftChecklistId;
			leftChecklist.setValue(iniLeftChecklistName);

			rightChecklistId = iniRightChecklistId;
			rightChecklist.setValue(iniRightChecklistName);

			crossmappingName.setValue(iniLeftChecklistName + " vs " + iniRightChecklistName);
			crossmappingDescription.setValue("Cross-map between " + iniLeftChecklistName + " and " + iniRightChecklistName);
		}

		checkValid();
	}	


	protected void setupWidget(boolean isReRun){
		setPixelSize(450, 400);
		setModal(true);
		//setBlinkModal(true);
		setHeadingText("Cross map configuration");
		getHeader().setIcon(Resources.INSTANCE.crossmap());

		FramedPanel panel = new FramedPanel();
		panel.setWidth(400);
		panel.setHeaderVisible(false);
		panel.setBodyStyle("background: none; padding: 5px");

		VerticalLayoutContainer p = new VerticalLayoutContainer();
		panel.add(p);

		//***********************************************************************************************************************	    
		//Fields to specify the name and description of the cross-map
		//***********************************************************************************************************************				
		crossmappingName = new TextField();
		crossmappingName.setAllowBlank(false);
		crossmappingName.setEmptyText("Enter a short name for your Cross-mapping...");
		crossmappingName.addValidator(new MaxLengthValidator(255));
		p.add(new FieldLabel(crossmappingName, "Name"), new VerticalLayoutData(1, -1));

		crossmappingName.addInvalidHandler(new InvalidHandler() {			
			@Override
			public void onInvalid(InvalidEvent event) {
				checkValid();
			}
		});
		crossmappingName.addValidHandler(new ValidHandler() {			
			@Override
			public void onValid(ValidEvent event) {
				checkValid();
			}
		});

		crossmappingDescription = new TextArea();
		crossmappingDescription.setAllowBlank(false);
		crossmappingDescription.setEmptyText("Enter a description for your Cross-mapping...");
		crossmappingDescription.addValidator(new MaxLengthValidator(512));
		p.add(new FieldLabel(crossmappingDescription, "Description"), new VerticalLayoutData(1, -1));

		crossmappingDescription.addInvalidHandler(new InvalidHandler() {			
			@Override
			public void onInvalid(InvalidEvent event) {
				checkValid();
			}
		});
		crossmappingDescription.addValidHandler(new ValidHandler() {			
			@Override
			public void onValid(ValidEvent event) {
				checkValid();
			}
		});
		//***********************************************************************************************************************		

		//***********************************************************************************************************************	    
		//Fields to specify the left checlist in the cross-map
		//***********************************************************************************************************************				
		leftChecklist = new TextField();
		leftChecklist.setReadOnly(true);
		leftChecklist.setEmptyText("");
		leftChecklist.setWidth(210);

		TextButton leftChecklistBrowseButton = new TextButton("Browse");
		leftChecklistBrowseButton.addSelectHandler(new SelectHandler() {			
			@Override
			public void onSelect(SelectEvent event) {
				final ChecklistSelectionWindow selectionWindow = new ChecklistSelectionWindow();
				selectionWindow.addHideHandler(new HideHandler() {

					@Override
					public void onHide(HideEvent event) {
						ChecklistGWT selectedItem = selectionWindow.getSelectedItem();
						if (selectedItem!=null){
							leftChecklistId = selectedItem.getId();
							leftChecklist.setValue(selectedItem.getName());
							checkValid();
						}
					}
				});
				selectionWindow.show();				
			}
		});

		HorizontalPanel leftChecklistPanel = new HorizontalPanel();	    	    
		leftChecklistPanel.add(leftChecklist);
		leftChecklistPanel.add(leftChecklistBrowseButton);
		p.add(new FieldLabel(leftChecklistPanel, "Left checklist"), new VerticalLayoutData(1, -1));
		//***********************************************************************************************************************

		//***********************************************************************************************************************	    
		//Fields to specify the right checlist in the cross-map
		//***********************************************************************************************************************			
		rightChecklist = new TextField();
		rightChecklist.setReadOnly(true);
		rightChecklist.setEmptyText("");
		rightChecklist.setWidth(210);

		TextButton rightChecklistBrowseButton = new TextButton("Browse");
		rightChecklistBrowseButton.addSelectHandler(new SelectHandler() {			
			@Override
			public void onSelect(SelectEvent event) {
				final ChecklistSelectionWindow selectionWindow = new ChecklistSelectionWindow();
				selectionWindow.addHideHandler(new HideHandler() {

					@Override
					public void onHide(HideEvent event) {
						ChecklistGWT selectedItem = selectionWindow.getSelectedItem();
						if (selectedItem!=null){
							rightChecklistId = selectedItem.getId();
							rightChecklist.setValue(selectedItem.getName());
							checkValid();
						}
					}
				});
				selectionWindow.show();

			}
		});

		HorizontalPanel rightChecklistPanel = new HorizontalPanel();
		rightChecklistPanel.add(rightChecklist);
		rightChecklistPanel.add(rightChecklistBrowseButton);
		p.add(new FieldLabel(rightChecklistPanel, "Right checklist"), new VerticalLayoutData(1, -1));		
		//***********************************************************************************************************************

		//***********************************************************************************************************************	    
		//Fields to specify if the cross-map should try to find extra relationships not using only strict comparision
		//***********************************************************************************************************************		
		strictCheckBox = new CheckBox();
		strictCheckBox.setToolTip("Strict matching between names");
		strictCheckBox.setValue(false);
		p.add(new FieldLabel(strictCheckBox, "Strict matching"), new VerticalLayoutData(1, -1));

		extra = new SimpleComboBox<IdentifyExtraTaxaTypeGWT>(new LabelProvider<IdentifyExtraTaxaTypeGWT>() {
			@Override
			public String getLabel(IdentifyExtraTaxaTypeGWT item) {
				return item.toString();
			}		
		});	    	
		extra.setAllowBlank(false);
		extra.setForceSelection(true);
		extra.setEditable(false);
		extra.setTriggerAction(TriggerAction.ALL);
		for (IdentifyExtraTaxaTypeGWT extraType:IdentifyExtraTaxaTypeGWT.values()) extra.add(extraType);
		extra.setValue(IdentifyExtraTaxaTypeGWT.NONE, true);

		final FieldLabel fieldExtra = new FieldLabel(extra, "Identify extra possible names");
		fieldExtra.setEnabled(strictCheckBox.getValue());
		p.add(fieldExtra, new VerticalLayoutData(1, -1));	    	    

		strictCheckBox.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				fieldExtra.setEnabled(event.getValue());
			}
		});
		//***********************************************************************************************************************

		//***********************************************************************************************************************	    
		//Fields to specify if the cross-map should find relationships for higher taxa
		//***********************************************************************************************************************
		higherTaxaCheckBox = new CheckBox();
		higherTaxaCheckBox.setToolTip("Match higher taxa");
		higherTaxaCheckBox.setValue(false);
		p.add(new FieldLabel(higherTaxaCheckBox, "Match higher taxa"), new VerticalLayoutData(1, -1));

		highestTaxonomicRank = new SimpleComboBox<TaxonomicRankGWT>(new LabelProvider<TaxonomicRankGWT>() {
			@Override
			public String getLabel(TaxonomicRankGWT item) {
				return item.getName();
			}
		});	    	    	    	    
		highestTaxonomicRank.setAllowBlank(false);
		highestTaxonomicRank.setForceSelection(true);
		highestTaxonomicRank.setEditable(false);	    	    
		highestTaxonomicRank.setTriggerAction(TriggerAction.ALL);	    
		getTaxonomicRanks();

		final FieldLabel fieldHighestTaxonomicRankToCompare = new FieldLabel(highestTaxonomicRank, "Top rank to compare");
		fieldHighestTaxonomicRankToCompare.setEnabled(higherTaxaCheckBox.getValue());
		p.add(fieldHighestTaxonomicRankToCompare, new VerticalLayoutData(1, -1));	    	    

		higherTaxaCheckBox.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				fieldHighestTaxonomicRankToCompare.setEnabled(event.getValue());
			}
		});	  
		//***********************************************************************************************************************


		//***********************************************************************************************************************
		//Fields to specify the level of user knowledge to be used in the cross-map for refine its results
		//***********************************************************************************************************************
		userRefinement = new SimpleComboBox<UserKnowledgeLevelForRefinementGWT>(new LabelProvider<UserKnowledgeLevelForRefinementGWT>() {
			@Override
			public String getLabel(UserKnowledgeLevelForRefinementGWT item) {
				return item.toString();
			}		
		});	  	   
		userRefinement.setAllowBlank(false);
		userRefinement.setForceSelection(true);
		userRefinement.setEditable(false);
		userRefinement.setTriggerAction(TriggerAction.ALL);
		for (UserKnowledgeLevelForRefinementGWT refinementLevel:UserKnowledgeLevelForRefinementGWT.values()) userRefinement.add(refinementLevel);
		userRefinement.setValue(UserKnowledgeLevelForRefinementGWT.NONE, true);	 

		final FieldLabel fieldUserRefinement = new FieldLabel(userRefinement, "Refinement level to consider");
		p.add(fieldUserRefinement, new VerticalLayoutData(1, -1));	    
		//fieldUserRefinement.setEnabled(enabled)
		//***********************************************************************************************************************	   

		add(panel);

		runButton = new TextButton("Run");
		runButton.addSelectHandler(new SelectHandler() {

			@Override
			public void onSelect(SelectEvent event) {
				runCrossMap();
			}
		});
		runButton.setEnabled(false);
		addButton(runButton);

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
		boolean valid = crossmappingName.isValid(true) && crossmappingDescription.isValid(true) && leftChecklistId!=null && rightChecklistId!=null;
		runButton.setEnabled(valid);
	}

	protected void runCrossMap()
	{
		mask("Starting crossmap");
		
		//Collect the parameters from which to run the cross-map
		final String xmapName = crossmappingName.getValue();
		String description = crossmappingDescription.getValue();
		boolean strict = strictCheckBox.getValue();
		IdentifyExtraTaxaTypeGWT identifyExtraTaxa = extra.getValue();
		boolean compareHigherTaxa = higherTaxaCheckBox.getValue();		
		TaxonomicRankGWT highestRankToCompare = highestTaxonomicRank.getValue();		
		UserKnowledgeLevelForRefinementGWT userRefinementLevel = userRefinement.getValue();		

		CrossMappingPortlet.service.runXmap(xmapName, description, leftChecklistId, rightChecklistId, strict, identifyExtraTaxa,compareHigherTaxa,highestRankToCompare,userRefinementLevel, new AsyncCallback<Void>() {
			@Override
			public void onFailure(Throwable caught) {
				unmask();
				logger.log(Level.SEVERE, "CrossMap failure", caught);		
				ErrorMessage.showError("CrossMap failure",caught);
			}

			@Override
			public void onSuccess(Void result) {
				hide();
				Info.display("Crossmap operation", "Crossmap operation "+xmapName+" started correctly");
			}
		});
	}

	public void getTaxonomicRanks()
	{
		mask("Loading taxonomic ranks...");
		CrossMappingPortlet.service.getTaxonomicRanks(new AsyncCallback<List<TaxonomicRankGWT>>() {

			@Override
			public void onSuccess(List<TaxonomicRankGWT> result) {
				boolean cmbEmpty = true;
				for (TaxonomicRankGWT rank: result){					
					if (rank.getIsHigherRank()){ //Only add the higher ranks
						highestTaxonomicRank.add(rank);
						if (cmbEmpty){
							highestTaxonomicRank.setValue(rank);
							cmbEmpty = false;
						}
					}
				}	
				unmask();
			}

			@Override
			public void onFailure(Throwable caught) {
				unmask();
				logger.log(Level.SEVERE,"Error loading taxonomic ranks", caught);
				ErrorMessage.showError("Error loading taxonomic ranks",caught);				
			}
		});
	}


}
