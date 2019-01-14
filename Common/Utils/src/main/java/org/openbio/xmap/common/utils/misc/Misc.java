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

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.UUID;

/**
 * Class with some miscellaneous methods
 * <p>
 * OpenBio XMap 
 * <p>
 * Date: 15/05/12
 * @author Fran
 */
public abstract class Misc {

    
	/****************************************************************************************/
	/* PUBLIC METHODS																		*/														
	/****************************************************************************************/
	
	/**
	 * Method responsible to generate a pseudoId 
	 * @return pseudoId generated
	 */
    public static String generatePseudoId(){
    	String pseudoId = UUID.randomUUID().toString();
    	return pseudoId.replaceAll("-", "_");
    	
    	/*
    	Random randomGenerator = new Random();
    	int randomInt = randomGenerator.nextInt(1000);
    	Date currentDate = new Date();
    	long ts = currentDate.getTime();
    	
    	String pseudoId = Long.toHexString(ts*1000 + randomInt).toUpperCase(); // + "-" + Integer.toString(randomInt)
    	    	
    	return pseudoId;
    	*/
    }
    
    /**
     * Method responsible to return position of the "n"th apparition of the character "needle" in the string "text"
     * @param text string with the text in which to obtain the position  of the "n"th apparition of the character "needle"
     * @param needle character to find its "n"th apparition
     * @param n number of the apparition
     * @return position of the "n"th apparition of the character "needle" in the string "text"
     */
	public static int nthIndexOf(String text, char needle, int n){
	    for (int i = 0; i < text.length(); i++){
	        if (text.charAt(i) == needle){
	            n--;
	            if (n == 0){
	                return i;
	            }
	        }
	    }
	    return -1;
	}	    	
	
    public static String convertTime(long time, String dateFormat){
        Date date = new Date(time);
        Format format = new SimpleDateFormat(dateFormat);
        return format.format(date).toString();
    }
	
}
