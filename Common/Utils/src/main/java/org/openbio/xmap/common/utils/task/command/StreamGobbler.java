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

import org.openbio.xmap.common.utils.misc.ExceptionHelper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Class to take an input stream and move its content to the stream watcher
 * <p>
 * This class is used in the class CommandTask for handling the standard error and standard output streams 
 * of the command tasks. 
 * <p>
 * OpenBio XMap
 * <p>
 * Date: 15/05/12
 * @author fran
 */
class StreamGobbler implements Runnable {
    
	/****************************************************************************************/
	/* ATTRIBUTES																			*/														
	/****************************************************************************************/	
	
	/**
	 * Input stream of the stream globbler that should be move its content to the the output stream  
	 * Note: This input stream could be the output or error stream of the exceution of a command
	 */	
	private final InputStream is;
	
	/**
	 * Stream watcher to move the inputstream
	 */
    private final StreamWatcher watcher;
       
	/****************************************************************************************/
	/* CONSTRUCTORS																			*/		
	/****************************************************************************************/    
    
    /**
     * Constructor of StreamGobbler that receives the input and the stram watcher 
     * @param is
     * @param watcher
     */
    public StreamGobbler(InputStream is, StreamWatcher watcher) {
        this.is = is;
        this.watcher = watcher;
    }
    
    
	/****************************************************************************************/
	/* PUBLIC METHODS																		*/														
	/****************************************************************************************/    
   
    /**
     * Method responsible to read the input stream and move its content to the stream watcher
     */
    @Override
    public void run() {
	    try {
	        BufferedReader br = new BufferedReader(new InputStreamReader(is));
            try {
                String line = br.readLine();
                while (line != null) {
                    watcher.addStreamLine(this, line);
                    line = br.readLine();
                }
            }
            finally {
                br.close();
            }
	    } 
	    catch (IOException e) {
            ExceptionHelper.ignore(e);
	    }
    }
}
