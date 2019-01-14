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
package org.openbio.xmap.common.utils.task;

import org.openbio.xmap.common.utils.task.Task;
import org.openbio.xmap.common.utils.task.TaskCancelledException;

/**
 * Class to implements a simple task that will be executing doing nothing during x seconds
 * <p>
 * OpenBio XMap
 * <p>
 * Date: 15/05/12
 * @author scmjpg 
 */
class SampleTask extends Task {

	/****************************************************************************************/
	/* ATTRIBUTES																			*/														
	/****************************************************************************************/
	
	/**
	 * Number of second this task will be executing
	 */
    private final int count;

    
    
	/****************************************************************************************/
	/* CONSTRUCTORS																			*/		
	/****************************************************************************************/
    
    /**
     * Constructor of the SampleTask that receives the number of seconds it will be executed
     * @param seconds number of seconds that this task will be executed
     */
    SampleTask(int seconds) {
        super();
        count = seconds;
    }

    
    
	/****************************************************************************************/
	/* PUBLIC METHODS																		*/														
	/****************************************************************************************/    
    
    /**
     * Method responsible to perform the execution of this task.
     * Note: In this task will do nothing for x seconds and passed this time we'll finilise its execution
     */
    @Override
    public void perform() throws TaskCancelledException {
        setActive(0, count);
        int i = 0;
        while (i < count) {
            throwIfCancelled();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                startCancellingPhase();
                throw new TaskCancelledException(e);
            }
            ++i;
            updateActive(i);
        }
    }
}
