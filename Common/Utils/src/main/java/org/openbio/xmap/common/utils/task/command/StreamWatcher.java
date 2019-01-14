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
package org.openbio.xmap.common.utils.task.command;

/**
 * Interface that define the method of stream watcher
 * <p>
 * OpenBio XMap
 * <p>
 * Date: 15/05/12
 * @author scmjpg
 */
public interface StreamWatcher {

	/**
	 * Method responsible to add a line read by the StreamGobbler
	 * @param gobbler
	 * @param line
	 */
    void addStreamLine(StreamGobbler gobbler, String line);
}
