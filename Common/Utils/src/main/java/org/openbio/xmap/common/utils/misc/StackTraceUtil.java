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

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

/**
 * Class that implements some methods for the stack trace
 * <p>
 * OpenBio XMap
 * <p> 
 * Date: 15/05/12
 * @author Fran
 */
public abstract class StackTraceUtil {

	/**
	 * Method responsible to return the string that represent the stack trace of the exception received as parameter
	 * @param aThrowable exception to obtain its stack trace
	 * @return string that represent the stack trace of the exception received as parameter 
	 */
	public static String getStackTrace(Throwable aThrowable) {
		final Writer result = new StringWriter();
		final PrintWriter printWriter = new PrintWriter(result);
		aThrowable.printStackTrace(printWriter);
		return result.toString();
	}
	
}
