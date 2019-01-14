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

public enum UserKnowledgeLevelForRefinementGWT {


    /**
     * Don't apply user knowledge
     * 
     */
    NONE("None"),

    /**
     * Only use the knowledge indicate for this xmap
     * 
     */
    X_MAP("XMap"),

    /**
     * Use all the knowlegde set by this user which is not conflicting amongst it
     * 
     */   
    USER("User"),

    /**
     * Use all the knowlegde set by any user which is not conflictingg amongst it
     * 
     */
    GLOBAL("Global");
    private final String value;

    UserKnowledgeLevelForRefinementGWT(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static UserKnowledgeLevelForRefinementGWT fromValue(String v) {
        for (UserKnowledgeLevelForRefinementGWT c: UserKnowledgeLevelForRefinementGWT.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }
    
}
