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
package org.gcube.portlets.user.crossmapping.client.resources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

/**
 * @author "Federico De Faveri defaveri@isti.cnr.it"
 *
 */
public interface Resources extends ClientBundle {
	
	public Resources INSTANCE = GWT.create(Resources.class);

	@Source("cancel.png")
	ImageResource cancel();
	
	@Source("crossmap.png")
	ImageResource crossmap();
	
	@Source("delete.png")
	ImageResource delete();
	
	@Source("export.png")
	ImageResource export();
	
	@Source("import.png")
	ImageResource getImport();
	
	@Source("information.png")
	ImageResource information();
	
	@Source("refresh.png")
	ImageResource refresh();
	
	@Source("refreshwarning.png")
	ImageResource refreshwarning();
	
	@Source("show.png")
	ImageResource show();
	
	@Source("save.png")
	ImageResource save();
	
	@Source("tree.png")	
	ImageResource tree();
	
	@Source("grid.png")
	ImageResource grid();	
	
	@Source("view.gif")
	ImageResource view();		
	
	@Source("marker.png")
	ImageResource marker();		
		
	@Source("marker2.gif")
	ImageResource marker2();			
	
	@Source("leftDocument.png")
	ImageResource leftDetail();	
	
	@Source("rightDocument.png")
	ImageResource rightDetail();
	
	@Source("send.gif")
	ImageResource send();	

	@Source("config.png")
	ImageResource config();
	
	@Source("config2.png")
	ImageResource config2();		

	@Source("search.png")
	ImageResource search();
	
	@Source("edit.png")
	ImageResource edit();		
	
	@Source("edit2.png")
	ImageResource edit2();		
}
