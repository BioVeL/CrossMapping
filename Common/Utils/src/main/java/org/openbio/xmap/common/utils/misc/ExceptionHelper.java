/*
 * #%L
 * Xmap Utils
 * %%
 * Copyright (C) 2012 - 2013 Cardiff University
 * %%
 * Use of this software is governed by the attached licence file. If no licence 
 * file is present the software must not be used.
 * 
 * The use of this software, including reverse engineering, for any other purpose 
 * is prohibited without the express written permission of the software owner, 
 * Cardiff University.
 * #L%
 */
package org.openbio.xmap.common.utils.misc;

import org.slf4j.Logger;

/**
 * Class that provides some methods to makes more easy to control exceptions
 * <p>
 * OpenBio XMap
 * <p>
 * Date: 15/05/12
 * @author scmjpg
 */
public class ExceptionHelper {
	    
	/****************************************************************************************/
	/* PUBLIC METHODS																		*/														
	/****************************************************************************************/
	
	/**
	 * Method that will print in the looger that the exception received as parameter has been ignored
	 * @param logger logger in which write that the exception has been ignored
	 * @param e exception that has been ignored
	 */
    public static void ignore(Logger logger, Exception e) {
    	logger.error("[IGNORED]", e);
    }

    /**
     * Method that will print in the system error output stream that the exception received as parameter has been ignored 
     * @param e  exception that has been ignored
     */
    @Deprecated
    public static void ignore(Exception e) {
        e.printStackTrace();
        System.err.println("[IGNORED]");
    }

    /**
     * Method to check if the exception received as first parameter belongs to any of the classes received as a list in the second parameter
     * @param thrown exception if belong to some class
     * @param causeClasses classes of exception
     * @return true if the exception is one of these classes and false otherwise
     */
    public static boolean causedBy(Throwable thrown, Class<? extends Throwable>... causeClasses) {
        while (thrown != null) {
            for (Class<? extends Throwable> causeClass : causeClasses) {
                if (causeClass.isInstance(thrown)) {
                    return true;
                }
            }
            thrown = thrown.getCause();
        }
        return false;
    }
}
