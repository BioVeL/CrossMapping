/*
 * #%L
 * XMap Checklist importer DAO
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
package org.openbio.xmap.common.dao.importer.impl;

import java.io.IOException;
import java.io.InputStream;

import javax.sql.DataSource;

import org.openbio.xmap.common.dao.util.exception.DAOException;
import org.openbio.xmap.common.dao.util.helper.SQLiteHelperImpl;

public class CheckListImporterDAOSQLiteImpl extends CheckListImporterDAORelationalImpl {

	/****************************************************************************************/
	/* CONSTRUCTORS																			*/		
	/****************************************************************************************/	
	
	/**
	 * Constructor of CheckListImporterDAO for SQLite. It will call the super class (CheckListImporterDAORelationalImpl)
	 * with the datasource and with the specific implementation of relationHelper for SQLite databases 
	 * @param ds datasource to connect to the xmap database
	 * @throws DAOException
	 * @throws IOException 
	 */		
	public CheckListImporterDAOSQLiteImpl(DataSource ds) throws DAOException, IOException{
		super(ds, new SQLiteHelperImpl());	
	}
	
	
	/****************************************************************************************/
	/* PROTECTED METHODS																	*/														
	/****************************************************************************************/		
	
	/**
	 * Method that reads the CheckListImporter tables schema for SQLite database from a resource file and 
	 * return an object inputStream to process it 
	 * @return input stream with the sql commands to create the tables needed
	 */			
    protected InputStream openSchemaStream() {
        return this.getClass().getResourceAsStream("/sql/importer-schema-SQLite.sql");
    }	
    
	/**
	 * Method that reads the CheckListImporter insert data sql commands for SQLite database from a resource file and 
	 * return an object inputStream to process it 
	 * @return inputStream of the sql file with the command to insert the data needed for the administration 
	 * of the xmap application
	 */	       
    protected InputStream openInitialDataStream() {
        return this.getClass().getResourceAsStream("/sql/importer-data.sql");
    }     
	
}
