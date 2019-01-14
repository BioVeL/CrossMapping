/*
 * #%L
 * XMap Util Data Access Objects
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
package org.openbio.xmap.common.dao.util.helper;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Class to facilitate the read and write of JAXB objects
 * <p>
 * OpenBio XMap
 * <p>
 * Date: 15/05/12
 * @author scmjpg
 */
public class JAXBHelper {
	
	/****************************************************************************************/
	/* PUBLIC METHODS																		*/														
	/****************************************************************************************/
	
	/**
	 * Method that write/marshall the jaxb object receive a third parameter into the output stream received as firs parameter 
	 * @param out output stream in which write/marshall the jaxb object
	 * @param jaxbClass class of the jaxb object
	 * @param jaxbObject jaxb object to be marshalled
	 * @throws JAXBException
	 */
    public static void write(OutputStream out, Class jaxbClass, Object jaxbObject) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(jaxbClass);
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        marshaller.marshal(jaxbObject, out);
    }
    
    /**
     * Method to read/unmarshall a jaxb object of the class indicated in the second parameter from the input stream
     * received in the first parameter
     * @param is input stream in which the jaxb object is marshalled  
     * @param jaxbClass class of the jaxb object to unmarshall
     * @return jaxb object
     * @throws JAXBException
     */
    public static Object read(InputStream is, Class jaxbClass) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(jaxbClass);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        return unmarshaller.unmarshal(is);
    }}
