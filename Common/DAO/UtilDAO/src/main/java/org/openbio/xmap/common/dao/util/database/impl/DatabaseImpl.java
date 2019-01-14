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
package org.openbio.xmap.common.dao.util.database.impl;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.openbio.xmap.common.dao.util.database.Database;
import org.openbio.xmap.common.dao.util.database.DatabaseException;

/**
 * Abstract class that implement the interface Database 
 * <p>
 * OpenBio XMap
 * <p>
 * Date: 15/05/12
 * @author scmjpg
 */
public abstract class DatabaseImpl implements Database {

	/****************************************************************************************/
	/* ATTRIBUTES																			*/														
	/****************************************************************************************/
	
	/**
	 * Default prefix to be used for new databases
	 */
    private static String defaultCatalogPrefix = "auto_";

    /**
     * Datasouce of the database
     */
    protected final DataSource ds;
    
    /**
     * Attribute with the current value of prefix to be used for name of the tree databases created automatically
     */
    private String catalogPrefix = defaultCatalogPrefix;

    
    
	/****************************************************************************************/
	/* CONSTRUCTORS																			*/		
	/****************************************************************************************/
    
    /**
     * Constructor of the DatabaseImpl that receives the datasource to connect to 
     * @param dataSource
     */
    protected DatabaseImpl(DataSource dataSource) {
        ds = dataSource;
    }
    
    
	
	/****************************************************************************************/
	/* PUBLIC METHODS																		*/														
	/****************************************************************************************/
	
    /**
	 * Method responsible to return the product name of the server database (e.g. MySQL, SQLite, etc.)
	 * @return string with the product name of the database (e.g. MySQL, SQLite, etc.)
	 * @throws DatabaseException
	 */    
    @Override
    public String getProductName() throws DatabaseException{
    	String dbProductName;
		try{
	        Connection conn = ds.getConnection();
	        try {
			    DatabaseMetaData metadata = conn.getMetaData();
			    dbProductName = metadata.getDatabaseProductName();			    			 
	        }
	        finally {
			    conn.close();
	        }
	        return dbProductName;
		}
    	catch (SQLException ex){
    		throw new DatabaseException("Unable to read metadata from datasource");
    	}	
	}    

    /**
     * Method responsible to set the prefix of the databases that will be created automatically in the server database
     * @param catalogPrefix string with the prefix of the databases that will be created automatically in the server database
     */    
    @Override
    public void setCatalogPrefix(String catalogPrefix) {
        if (catalogPrefix == null) {
            catalogPrefix = defaultCatalogPrefix;
        }
        this.catalogPrefix = catalogPrefix;
    }

    /**
     * Method responsible to get the prefix for automatic trees databases
     * @return the prefix for automatic trees databases
     */
    public String getCatalogPrefix() {
        return catalogPrefix;
    }

    /**
     * Method responsible to delete the database received a parameter from the server database without
     * raising exception
     * @param catalogName name of the database to delete
     */    
    @Override
    public void deleteCatalogQuietly(String catalogName) {
        try {
            deleteCatalog(catalogName);
        }
        catch (Exception e) {
            // ignore
        }
    }
}
