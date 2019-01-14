/*
 * #%L
 * XMap Test Utils
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
package org.openbio.xmap.common.test.net;

import java.io.IOException;
import java.net.Socket;

/**
 * Class that provides a static method to test if the network is available
 * <p>
 * OpenBio XMap
 * <p>
 * Date: 12/05/12
 * @author scmjpg  
 */
public class NetworkTest {

	/****************************************************************************************/
	/* ATTRIBUTES																			*/														
	/****************************************************************************************/
	
    private static final int HTTP_PORT = 80;

    
	/****************************************************************************************/
	/* PUBLIC METHODS																		*/														
	/****************************************************************************************/
    
    /**
     * Test whether remote network is available by connecting to a highly-available web site
     * @return true if external network access was found
     */
    public static boolean testNetworkAvailable() {
        Socket socket = null;
        try {
            socket = new Socket("www.google.com", HTTP_PORT);
            return true;
        }
        catch (IOException e) {
            return false;
        }
        finally {
            if (socket != null) {
                try {
                    socket.close();
                }
                catch (IOException e) {
                    //ignore
                }
            }
        }
    }
}
