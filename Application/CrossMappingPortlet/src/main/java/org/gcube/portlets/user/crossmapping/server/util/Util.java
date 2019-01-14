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
package org.gcube.portlets.user.crossmapping.server.util;

import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpSession;

import org.gcube.application.framework.core.session.ASLSession;
import org.gcube.application.framework.core.session.SessionManager;
import org.gcube.common.core.contexts.GHNContext;
import org.gcube.common.core.informationsystem.client.AtomicCondition;
import org.gcube.common.core.informationsystem.client.ISClient;
import org.gcube.common.core.informationsystem.client.queries.GCUBERuntimeResourceQuery;
import org.gcube.common.core.resources.GCUBERuntimeResource;
import org.gcube.common.core.resources.runtime.AccessPoint;
import org.gcube.common.core.scope.GCUBEScope;

public class Util {
	
	private static Logger logger =  Logger.getLogger(Util.class.getName());
	
	/**
	 * Method responsible to generate a pseudoId 
	 * @return pseudoId generated
	 */
    public static String generatePseudoId(){
    	return UUID.randomUUID().toString();
    }
    
    /**
     * Method responsible to return position of the "n"th apparition of the character "needle" in the string "text"
     * @param text string with the text in which to obtain the position  of the "n"th apparition of the character "needle"
     * @param needle character to find its "n"th apparition
     * @param n number of the apparition
     * @return position of the "n"th apparition of the character "needle" in the string "text"
     */
	public static int nthIndexOf(String text, char needle, int n){
	    for (int i = 0; i < text.length(); i++){
	        if (text.charAt(i) == needle){
	            n--;
	            if (n == 0){
	                return i;
	            }
	        }
	    }
	    return -1;
	}	    
	
	
	public static GCUBERuntimeResource getRuntimeResource(String resourceName, GCUBEScope scope) throws Exception {
		ISClient client = (ISClient)GHNContext.getImplementation(ISClient.class);
		GCUBERuntimeResourceQuery rtrQuery = (GCUBERuntimeResourceQuery)client.getQuery(GCUBERuntimeResourceQuery.class);
		rtrQuery.addAtomicConditions(new AtomicCondition[] { new AtomicCondition("/Profile/Name", resourceName) });

		List<GCUBERuntimeResource> runtimeResources = client.execute(rtrQuery, scope);

		if (runtimeResources.size() == 0) throw new Exception("Resource " + resourceName + " not found");

		GCUBERuntimeResource runtimeResource = (GCUBERuntimeResource)runtimeResources.get(0);

		return runtimeResource;
	}

	public static AccessPoint getRuntimeAccessPoint(String resourceName, String entryName, GCUBEScope scope) throws Exception {
		GCUBERuntimeResource runtimeResource = getRuntimeResource(resourceName, scope);

		for (AccessPoint accessPoint : runtimeResource.getAccessPoints()) {
			if (entryName.equals(accessPoint.getEntryname())) {
				return accessPoint;
			}
		}

		throw new Exception("No AccessPoint found with name " + entryName);
	}
	
	public static ASLSession getCurrentSession(HttpSession httpSession) throws Exception  {
		try {

			String sessionID = httpSession.getId();
			String user = (String)httpSession.getAttribute("username");

			if (user == null) {
				logger.log(Level.FINE, "XMapGUI PORTLET STARTING IN TEST MODE - NO USER FOUND");

				user = "francisco.quevedo";
				httpSession.setAttribute("username", user);
				ASLSession session = SessionManager.getInstance().getASLSession(sessionID, user);
				session.setScope("/gcube/devsec/devVRE");
				return session;
			}
			ASLSession session = SessionManager.getInstance().getASLSession(sessionID, user);

			session.getScope();
			return session;
		} 
		catch (Exception e) {
			logger.log(Level.SEVERE,"Error on getCurrentSession", e);
			throw new Exception("User session expired", e);
		}
	}	
	
}
