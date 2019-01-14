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

public enum TaskStatusGWT {


    /**
     * Task is pending to execute
     * 
     */
    
    PENDING("Pending"),

    /**
     * Task is being executed
     * 
     */
   
    ACTIVE("Active"),

    /**
     * Task is being cancelled
     * 
     */
    
    CANCELLING("Cancelling"),

    /**
     * Task has been executed successfully
     * 
     */
    
    COMPLETED("Completed"),

    /**
     * Task has failed
     * 
     */
    
    FAILED("Failed");
    private final String value;

    TaskStatusGWT(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static TaskStatusGWT fromValue(String v) {
        for (TaskStatusGWT c: TaskStatusGWT.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }
    
}
