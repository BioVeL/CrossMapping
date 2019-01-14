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
package org.gcube.portlets.user.crossmapping.client.util;

import org.gcube.portlets.user.crossmapping.shared.XMapGUIException;

import com.sencha.gxt.widget.core.client.box.AlertMessageBox;
import com.sencha.gxt.widget.core.client.event.HideEvent;
import com.sencha.gxt.widget.core.client.event.HideEvent.HideHandler;

public class ErrorMessage {

	public static void showError(String title, Throwable caught){
		String message;
		if (caught instanceof XMapGUIException){
			XMapGUIException ex = (XMapGUIException) caught;			
			message = ex.getUserData();
			if (ex.getExceptionId()!=null){
				message += " ExceptionId=" + ex.getExceptionId();
			}
		}
		else{
			message = caught.getMessage();
		}
		
		showError(title,message);		
	}
	
	public static void showError(String title, String message){
		AlertMessageBox errorPanel = new AlertMessageBox(title, message);
		errorPanel.addHideHandler( new HideHandler() {
            @Override
            public void onHide(HideEvent event) {
              //Nothing
            }
          });
		errorPanel.show();		
	}	
	
}
