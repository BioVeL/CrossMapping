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
package org.gcube.portlets.user.crossmapping.client.util;

import java.util.List;

import org.gcube.portlets.user.crossmapping.client.resources.Resources;
import org.gcube.portlets.user.crossmapping.shared.XMapRelPathGWT;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;

/**
 * @author "Federico De Faveri defaveri@isti.cnr.it"
 * Modification: 10/10/12 F. Quevedo. Changes
 *		 - TaskStatus by TaskStatusGWT
 */
public class TreeNodeXMapRelationsCell extends AbstractCell<List<XMapRelPathGWT>>{


	@Override
	public void render(com.google.gwt.cell.client.Cell.Context context, List<XMapRelPathGWT> rels, SafeHtmlBuilder sb) {
		String text = "";
		String tooltipHTML = "";
		if (rels.size()==0){
			//Not process
			text="not processed";
			tooltipHTML+="This taxon has not been considered in the crossmap";
			sb.appendHtmlConstant("<span title='"+ tooltipHTML +"'>" + text + " </span>");
		}
		else if (rels.size()==1){
			XMapRelPathGWT rel = rels.get(0);
			text=rel.getRelationship();	
			if (rel.getPath().size()>0){
				tooltipHTML+="A `"  + rel.getRelationship() + "` relationship has been found between this taxon and the taxon " + rel.getOtherTaxon().getAcceptedName() +" (taxonId " + rel.getOtherTaxon().getTaxonId() + ") in the other checklist" ;
				sb.appendHtmlConstant("<span title='"+ tooltipHTML +"'>" + text + " </span><img title='Mark its relationships in the other tree' onmouseover='this.style.cursor=&apos;pointer&apos;' onmouseover='this.style.cursor=&apos;default&apos;' src='" + Resources.INSTANCE.marker().getSafeUri().asString() + "' height='12'/>");
			}
			else{
				tooltipHTML+="This taxon has not been found in the other checklist";
				sb.appendHtmlConstant("<span title='"+ tooltipHTML +"'>" + text + " </span>");				
			}			
		}
		else{
			//Multiple rel
			text = "multiple rels";
			tooltipHTML="Multiple relationships found:"; 
			for (XMapRelPathGWT rel: rels){
				if (rel.getPath().size()>0){
					tooltipHTML+="&#13;&#09;- A `"  + rel.getRelationship() + "` relationship has been found between this taxon and the taxon " + rel.getOtherTaxon().getAcceptedName() +" (taxonId " + rel.getOtherTaxon().getTaxonId() + ") in the other checklist" ;
				}
			}
			sb.appendHtmlConstant("<span title='"+ tooltipHTML +"'>" + text + " </span><img title='Mark its relationships in the other tree' onmouseover='this.style.cursor=&apos;pointer&apos;' onmouseover='this.style.cursor=&apos;default&apos;' src='" + Resources.INSTANCE.marker().getSafeUri().asString() + "' height='12'/>");
		}					
	}


}
