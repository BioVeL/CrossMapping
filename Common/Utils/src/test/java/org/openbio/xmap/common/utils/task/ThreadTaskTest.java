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


import org.junit.Test;
import org.junit.Ignore;

import org.openbio.xmap.common.serviceinterfaces.types.xmap.TaskStatus;
import org.openbio.xmap.common.utils.task.Progress;
import org.openbio.xmap.common.utils.task.Task;
import org.openbio.xmap.common.utils.task.observer.PrintProgressObserver;

import static org.junit.Assert.assertEquals;

/**
 * Class to verify the execution of a sample task in a thread and follow its progress
 * <p>
 * OpenBio XMap
 * <p>
 * Date: 15/05/12
 * @author scmjpg
 */
public class ThreadTaskTest {

	/****************************************************************************************/
	/* TEST	METHODS																			*/	
	/****************************************************************************************/		
	
	/**
	 * Test that executes the sample task in a thread and wait till its finished.
	 * @throws Exception
	 */
    @Test
	@Ignore
    public void testTaskExecution() throws Exception {
        Task task = new SampleTask(2);
        task.addObserver(new PrintProgressObserver(task));
        new Thread(task).start();
        task.waitForFinished();
        assertEquals(TaskStatus.COMPLETED, task.getProgress().getState());
    }

    /**
     * Test that executes the sample task in a thread, checking its progress until it reaches certain level, 
     * the moment in which the test will send an event to task cancel it.
     * @throws Exception
     */
    @Test
	@Ignore
    public void testTaskCancel() throws Exception {
        Task task = new SampleTask(4);
        task.addObserver(new PrintProgressObserver(task));
        new Thread(task).start();
        try {
            boolean done = false;
            while (!done) {
                Progress progress = task.getProgress();
                if (progress.getCurProgress() == 2) {
                    task.cancel();
                }
                switch (progress.getState()) {
                    case PENDING:
                    case ACTIVE:
                    case CANCELLING:
                        break;
                    case COMPLETED:
                    case FAILED:
                        done = true;
                        break;
                }
                Thread.sleep(1);
            }
        }
        finally {
            task.waitForFinished();
        }
        assertEquals(TaskStatus.FAILED, task.getProgress().getState());
    }
}
