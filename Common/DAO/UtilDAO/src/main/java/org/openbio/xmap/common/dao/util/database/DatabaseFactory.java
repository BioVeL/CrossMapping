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
package org.openbio.xmap.common.dao.util.database;

import org.openbio.xmap.common.dao.util.database.mysql.MySQLDatabaseImpl;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.HashMap;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

/**
 * Factory to obtain the specific Database objects
 * <p>
 * OpenBio XMap
 * <p> 
 * Date: 15/05/12
 * @author scmjpg
 */
public class DatabaseFactory {
   
	/****************************************************************************************/
	/* ATTRIBUTES																			*/														
	/****************************************************************************************/
	
	/**
     * Instance of the DatabaseFactory
     * Because we are using the singleton pattern we only create an instance of this factory 
     */	
	private static final DatabaseFactory ourInstance = new DatabaseFactory();


	/****************************************************************************************/
	/* CONSTRUCTORS																			*/		
	/****************************************************************************************/
    
	/**
	 * Private constructor of DatabaseFactory
	 * Note: This class will be only instantiated internally
	 */
    private DatabaseFactory() {
    }

    
	/****************************************************************************************/
	/* PUBLIC METHODS																		*/														
	/****************************************************************************************/
    
	/**
	 * Method to return the instance of the database factory
	 * @return instance of the database factory
	 */    
    public static DatabaseFactory getInstance() {
        return ourInstance;
    }
    
    /**
     * Method that return a database object build with the value of the parameters received
     * @param product product of the database (e.g. MySQL, SQLite, ...)
     * @param host url of the host of the server database
     * @param port port number of the server database
     * @param catalog name of the catalag (database)
     * @param parameters hasmap with the values of other parameters like user, password, ... 
     * @return database object
     * @throws DatabaseException
     */
    public Database getDatabase(String product, String host, Integer port, String catalog,
                                HashMap<String,String> parameters) throws DatabaseException {
        if (product.equalsIgnoreCase("mysql")) {
            return new MySQLDatabaseImpl(host, port, catalog, parameters);
        }
        throw new DatabaseException("database '" + product + "' not supported");
    }
    
    /**
     * Method responsible to return the database object from the jndi datasource name received as parameter
     * @param jndiDataSource jndi datasource name
     * @return database object
     * @throws DatabaseException
     */
    public Database getDatabase(String jndiDataSource) throws DatabaseException{
    	DataSource dataSource = getDataSource(jndiDataSource);    	
    	return getDatabase(dataSource);
    }
    
    /**
     * Method responsible to return the database object from the data source received as parameter
     * @param dataSource
     * @return database object
     * @throws DatabaseException
     */
    public Database getDatabase(DataSource dataSource) throws DatabaseException {
    	//Get information about database 
		String dbProductName = getDBProductName(dataSource);
		
    	if (dbProductName.equalsIgnoreCase("mysql")) {
    		return new MySQLDatabaseImpl(dataSource);
    	}
    	else{
    		throw new DatabaseException("database '" + dbProductName + "' not supported");
    	}
    }    
    
    
	/****************************************************************************************/
	/* PRIVATE METHODS																		*/														
	/****************************************************************************************/	
    
	/**
	 * Function that receiving a jndiDatasourceName returns its datasource
	 * @param jndiDatasourceName
	 * @return datasource datasource for the jndiDatasourceName received as parameter
	 * @throws NamingException
	 */
	private DataSource getDataSource(String jndiDatasourceName) throws DatabaseException {
		try{
			Context initContext = new InitialContext();
			Context envContext = (Context) initContext.lookup("java:/comp/env");
			return (DataSource) envContext.lookup(jndiDatasourceName);
		}
		catch (NamingException e) {
			throw new DatabaseException("Unable to get datasource from jndi datasource");
		}
    }   
	
	/**
	 * Method responsible to return the product name of the database 
	 * @param dataSource datasource to connect to the database that we want to obtain its product name
	 * @return string with the name of the product of the database (e.g. MySQL, SQLite)
	 * @throws DatabaseException
	 */
	private String getDBProductName(DataSource dataSource) throws DatabaseException{
    	String dbProductName;
		try{
	        Connection conn = dataSource.getConnection();
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
}
