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

import java.io.IOException;

import org.openbio.xmap.common.utils.misc.ExceptionHelper;
import org.openbio.xmap.common.utils.task.Task;
import org.openbio.xmap.common.utils.task.TaskCancelledException;
import org.openbio.xmap.common.utils.task.TaskException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A task that runs by calling an external program.
 * <p>
 * OpenBio XMap
 * <p>
 * Date: 15/05/12
 * @author scmjpg
 * @param <R> generic class that indicates the return value of the task.
 */
public class CommandTask<R> extends Task<R> implements StreamWatcher {

	/****************************************************************************************/
	/* ATTRIBUTES																			*/														
	/****************************************************************************************/
	
	/**
	 * slf4j logger for this class
	 */
	private static Logger logger = LoggerFactory.getLogger(CommandTask.class);
	
	/**
	 * Command to be executed
	 */
    private String command;
    
    /**
     * Streamgobbler to control the output stream of the comamnd
     */
    private StreamGobbler out;
    
    /**
     * Streamgobbler to control the error stream of the comamnd
     */
    private StreamGobbler err;
    
    /**
     * String with the first line of the error
     */
    private String firstErrLine;
    
    /**
     * String with the last line of the error
     */
    private String lastErrLine;

    
	/****************************************************************************************/
	/* CONSTRUCTORS																			*/		
	/****************************************************************************************/
    
    /**
     * Constructor of the CommandTask
     */
    public CommandTask() {
    }

    /**
     * Constructor of the CommandTask that receives the command to execute
     * @param command
     */
    public CommandTask(String command) {
        this.command = command;
    }

    
	/****************************************************************************************/
	/* PUBLIC METHODS																		*/														
	/****************************************************************************************/
    
    /**
     * Method responsible to set the command to be executed in this task
     * @param command string with the command to be executed in this task
     */
    public void setCommand(String command) {
        this.command = command;
    }

    /**
     * Method responsible to execute the the command
     */
    @Override
    public void perform() throws TaskException {      
        Process process = null;
        try {
	    	ProcessBuilder builder = isWindows() ?
	                new ProcessBuilder("cmd.exe", "/c", command) :
	                new ProcessBuilder("/bin/sh", "-c", command);
	        throwIfCancelled();
	        
	        process = builder.start();
              
            // The InputStreams do not indicate ready on EOF, making it impossible to
            // safely check here without hanging, so we start additional threads to
            // read the InputStreams.
            try {
                process.getOutputStream().close();
            } catch (IOException e) {
                ExceptionHelper.ignore(e);
            }
            out = new StreamGobbler(process.getInputStream(), this);
            Thread t1 = new Thread(out);
            t1.start();
            err = new StreamGobbler(process.getErrorStream(), this);
            Thread t2 = new Thread(err);
            t2.start();
            int exitCode;
            try {
                t1.join();
                t2.join();
                exitCode = process.waitFor();
            } catch (InterruptedException e) {
                startCancellingPhase();
                throw new TaskCancelledException(e);
            }
            if (exitCode == 0) {
                setDone();
            }
            else {
                /*
                    When a command fails, either the first or last line printed to stderr contains
                    the most useful information. If more than one line has been printed, provide
                    both the first and last lines, otherwise provide the single line printed,
                    otherwise if no output to stderr, just provide the exit code.
                 */
                if (lastErrLine != null) {
                    setFail(firstErrLine + " / " + lastErrLine);
                }
                else if (firstErrLine != null) {
                    setFail(firstErrLine);
                }
                else {
                    setFail("exited with code " + exitCode);
                }
            }
        }
        catch (Exception e){
        	throw new TaskException(e,e.getMessage());
        }
        finally {
            if (process!=null){
            	process.destroy();
            }
        }
    }

    /**
     * Method responsible to add a new line to the gobbler received as parameter
     * @param gobbler StreamGobbler that is controlling the output or error stream of the command
     * @param line string with the line to be added
     */
    @Override
    public void addStreamLine(StreamGobbler gobbler, String line) {
        if (gobbler == out) {
            addOutputLine(line);
        }
        else if (gobbler == err) {
            addErrorLine(line);
        }
    }
    
    
	/****************************************************************************************/
	/* PROTECTED METHODS																	*/														
	/****************************************************************************************/	    

    /**
     * Method to add a new line to the output stream
     * @param line string with the line to be added to the output stream
     */
    protected void addOutputLine(String line) {
    	logger.trace("CommandTask output stream of [" + this.command + "]: " + line.trim());
    }

    /**
     * Method responsible to add a new line to the error stream    
     * @param line  string with the line to be added to the output stream
     */
    protected void addErrorLine(String line) {
    	logger.trace("CommandTask error stream of [" + this.command + "]: " + line.trim());
    	
    	if (firstErrLine == null) {
            firstErrLine = line.trim();
        }
        else {
            lastErrLine = line.trim();
        }
    }
    
    
	/****************************************************************************************/
	/* PRIVATE METHODS																		*/														
	/****************************************************************************************/	    
  
    /**
     * Method responsible to detect if the operation system in which the jvm is running is windows
     * @return true if the O.S. is windows, false otherwise
     */
    private static boolean isWindows() {
        return System.getProperty("os.name").startsWith("Windows");
    }    
}
