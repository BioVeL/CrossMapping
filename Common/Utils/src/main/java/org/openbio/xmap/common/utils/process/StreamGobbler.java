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
package org.openbio.xmap.common.utils.process;

import java.io.*;

/**
 * Class to take an input stream and move it to an output streem
 * This class is used in the class CommandExceuter for handling the standard error and standard output streams 
 * of the command. Moving in a separate thread any stream passed into it (command's output or error) to a 
 * different output stream  
 * <p>
 * OpenBio XMap
 * <p>
 * Date: 15/05/12
 * @author Fran
 */
public class StreamGobbler extends Thread{
    
	/****************************************************************************************/
	/* ATTRIBUTES																			*/														
	/****************************************************************************************/
	
	/**
	 * Input stream of the stream globbler that should be move its content to the the output stream  
	 * Note: This input stream could be the output or error stream of the exceution of a command
	 */
	private InputStream is;
	
	/**
	 * Output stream of the stream globbler
	 */
    private OutputStream os;
           

	/****************************************************************************************/
	/* CONSTRUCTORS																			*/		
	/****************************************************************************************/
    
    /**
     * Constructor of StreamGobbler that receives the input and the output stream
     * @param is
     * @param os
     */
    public StreamGobbler(InputStream is, OutputStream os) {
        this.is = is;
        this.os = os;
    }
     
    
	/****************************************************************************************/
	/* PUBLIC METHODS																		*/														
	/****************************************************************************************/
    
    /**
     * Method responsible to read the input stream and move its content to the output stream
     */
    public void run(){
	    try {
	        PrintWriter pw = new PrintWriter(os);               
	        InputStreamReader isr = new InputStreamReader(is);
	        BufferedReader br = new BufferedReader(isr);
	        String line=null;
	        while ( (line = br.readLine()) != null){ 
	        	pw.println(line);    
	        }
	        pw.flush();
	    } 
	    catch (IOException ioe) {
	    	ioe.printStackTrace();  
	    }
    }
    
}
