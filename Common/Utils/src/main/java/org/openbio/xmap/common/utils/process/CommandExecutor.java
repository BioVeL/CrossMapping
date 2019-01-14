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

import java.io.ByteArrayOutputStream;

/**
 * Class to execute commands controlling its standard output and error stream
 * <p>
 * OpenBio XMap
 * <p>
 * Date: 15/05/12
 * @author Fran
 */
public class CommandExecutor {

	/****************************************************************************************/
	/* PUBLIC METHODS																		*/														
	/****************************************************************************************/	
	
	/**
	 * Method responsible to execute the the command with its arguments that are received as parameter 
	 * @param cmd array of string with the command to exceute with its arguments
	 * @param exitOutputStream output stream in which will have the standard output when the command
	 * finishes correctly or the error stream when it finishes incorrectly
	 * @return the exit value of the command. By convention, the value 0 indicates normal termination
	 * @throws Exception
	 */
	public static int execCommand(String[] cmd, ByteArrayOutputStream exitOutputStream) throws Exception {	
		ByteArrayOutputStream outOS = new ByteArrayOutputStream();
		ByteArrayOutputStream errOS = new ByteArrayOutputStream();

		try {			
			//Exec command
			Process proc = Runtime.getRuntime().exec(cmd);
							
			//Read output and err stream of process
			//Note: the method used to obtain a process's output stream is called getInputStream(). 
			//The thing to remember is that the API sees things from the perspective of the Java program 
			//and not the external process. Therefore, the external program's output is the Java program's input. 
			//And that logic carries over to the external program's input stream, which is an output stream to the Java program. 						
			StreamGobbler outputGobbler = new StreamGobbler(proc.getInputStream(), outOS);
			StreamGobbler errorGobbler = new StreamGobbler(proc.getErrorStream(), errOS);
			
			// kick them off
            errorGobbler.start();
            outputGobbler.start();
						
			//Wait until finish and get exit value
			int exitVal = proc.waitFor();
					
			if (exitVal!=0) {
				exitOutputStream.write(errOS.toByteArray());;
			}
			else{
				exitOutputStream.write(outOS.toByteArray());
			}
			
			return exitVal;
		}
		catch (Exception ex) {
			throw ex;
		}
		finally{
			outOS.close();
			errOS.close();
		}
	} 
	
}
