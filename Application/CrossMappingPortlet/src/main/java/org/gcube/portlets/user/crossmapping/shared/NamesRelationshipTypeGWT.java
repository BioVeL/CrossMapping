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

public enum NamesRelationshipTypeGWT {


    /**
     * name1 equals to name2
     * 
     */
    EQUAL,

    /**
     * name1 not equals to name2
     * 
     */
    NOT_EQUAL;

    public String value() {
        return name();
    }

    public static NamesRelationshipTypeGWT fromValue(String v) {
        return valueOf(v);
    }

}	
