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
package org.gcube.portlets.user.crossmapping.shared;

/**
 * @author "Federico De Faveri defaveri@isti.cnr.it"
 * Modification: 10/10/12 F. Quevedo. Changes:
 *		 - How errors are shown to the users
 */
public class XMapGUIException extends Exception {

	private static final long serialVersionUID = -5366502025812865947L;

	private String exceptionId;
	private String userData;
		
	
	public String getExceptionId() {
		return exceptionId;
	}

	public void setExceptionId(String exceptionId) {
		this.exceptionId = exceptionId;
	}

	public String getUserData() {
		return userData;
	}

	public void setUserData(String userData) {
		this.userData = userData;
	}

	
	
	public XMapGUIException(){
		//Not delete this constructor because it's needed for the gwt
	}
	
	/**
	 * Constructor of XMapGUIException
	 * @param userData data of the exception to be shown to the user
	 * @param exceptionId id of the exception register in the database
	 */
	public XMapGUIException(String userData, String exceptionId) {
		super(userData);
		this.userData = userData;
		this.exceptionId = exceptionId;		
	}

	/**
	 * Constructor of XMapGUIException
	 * @param userData data of the exception to be shown to the user
	 */
	public XMapGUIException(String userData) {
		super(userData);
		this.userData = userData;	
	}
	
}
