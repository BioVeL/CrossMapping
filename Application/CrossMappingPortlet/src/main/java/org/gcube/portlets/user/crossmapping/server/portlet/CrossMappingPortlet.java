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
package org.gcube.portlets.user.crossmapping.server.portlet;

import javax.portlet.GenericPortlet;
import javax.portlet.ActionRequest;
import javax.portlet.RenderRequest;
import javax.portlet.ActionResponse;
import javax.portlet.RenderResponse;
import javax.portlet.PortletException;
import java.io.IOException;
import javax.portlet.PortletRequestDispatcher;
import org.gcube.portal.custom.scopemanager.scopehelper.ScopeHelper;

/**
 * CrossMapping Portlet Class
 * @author fran
 */
public class CrossMappingPortlet extends GenericPortlet {
	
	public final void doView(final RenderRequest request, final RenderResponse response)
	throws PortletException, IOException {

		response.setContentType("text/html");
		try {
			ScopeHelper.setContext(request); // <-- Static method which sets the username in the session and the scope depending on the context automatically
		}
		catch (Exception e) {
			System.out.println("Could not initialize portlet context");
		}
		PortletRequestDispatcher dispatcher = getPortletContext().getRequestDispatcher("/WEB-INF/jsp/crossmapping.jsp");
		dispatcher.include(request, response);

	}


	public void processAction(final ActionRequest request, final ActionResponse response)
	throws PortletException, IOException {
		// nop - done by massi
	}

}
