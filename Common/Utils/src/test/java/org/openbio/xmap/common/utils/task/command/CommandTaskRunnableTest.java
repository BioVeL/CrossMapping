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


import org.junit.Ignore;
import org.junit.Test;
import org.openbio.xmap.common.serviceinterfaces.types.xmap.TaskStatus;
import org.openbio.xmap.common.utils.task.Progress;
import org.openbio.xmap.common.utils.task.command.CommandTask;
import org.openbio.xmap.common.utils.task.observer.PrintProgressObserver;

import static org.junit.Assert.assertEquals;

/**
 * Class to verify the methods for the CommandTask 
 * <p>
 * OpenBio XMap
 * <p>
 * Date: 15/05/12
 * @author scmjpg 
 */
public class CommandTaskRunnableTest {

	/****************************************************************************************/
	/* TEST	METHODS																			*/	
	/****************************************************************************************/		
	
	/**
	 * Test to lunch a command task and wait for it to finish
	 * @throws InterruptedException
	 */
    @Test
    @Ignore
    public void testTaskExecution() throws InterruptedException {
        CommandTask task = new CommandTask("/bin/sleep 2");
        task.addObserver(new PrintProgressObserver(task));
        new Thread(task).start();
        task.waitForFinished();
		if (!isWindows()){
			assertEquals(TaskStatus.COMPLETED, task.getProgress().getState());
		}
		else{
			assertEquals(TaskStatus.FAILED, task.getProgress().getState());
		}
    }

    /**
     * Test cancel a command task 
     * @throws InterruptedException
     */
    @Test
    public void testTaskCancel() throws InterruptedException {
        CommandTask task = new CommandTask("/bin/sleep 4");
        task.addObserver(new PrintProgressObserver(task));
        new Thread(task).start();
        try {
            Thread.sleep(2000);
            task.cancel();
            boolean done = false;
            while (!done) {
                Progress progress = task.getProgress();
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

    /**
     * Test a command task with a command that doesn't exists
     * @throws InterruptedException
     */
    @Test
    public void testNonExistentCommand() throws InterruptedException {
        CommandTask task = new CommandTask("does-not-exist 3");
        task.addObserver(new PrintProgressObserver(task));
        new Thread(task).start();
        task.waitForFinished();
        assertEquals(TaskStatus.FAILED, task.getProgress().getState());
    }

    /**
     * Test a command task with a command that exists but need more parameters
     * @throws InterruptedException
     */
    @Test
    public void testNonZeroExit() throws InterruptedException {
        CommandTask task = new CommandTask("mkdir"); // should be available on Unix and Windows
        task.addObserver(new PrintProgressObserver(task));
        new Thread(task).start();
        task.waitForFinished();
        assertEquals(TaskStatus.FAILED, task.getProgress().getState());
    }
	
    
	
	/****************************************************************************************/
	/* PRIVATE METHODS																		*/														
	/****************************************************************************************/	
    
    /**
     * Method responsible to detect if the java unit is executing in windows 
     * @return true if the java unit s executing in windows, false otherwise
     */
    private boolean isWindows() {
        return System.getProperty("os.name").startsWith("Windows");
    }	

}
