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
package org.openbio.xmap.common.dao.importer;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.openbio.xmap.common.dao.importer.CheckListImporterDAO;
import org.openbio.xmap.common.dao.importer.impl.CheckListImporterDAOMySQLImpl;
import org.openbio.xmap.common.dao.importer.impl.CheckListImporterDAOSQLiteImpl;
import org.openbio.xmap.common.dao.util.exception.DAODataSourceException;

public class CheckListImporterDAOFactory {

	/****************************************************************************************/
	/* ENUM TYPES																			*/														
	/****************************************************************************************/
	
	/**
	 * Enum type with the different type of datasource supported.
	 */
	private enum DataSourceType {MySQL,SQLite}
	
	
	
	/****************************************************************************************/
	/* PUBLIC METHODS																		*/														
	/****************************************************************************************/	
		
	/**
	 * Method that returns the specific CheckListImporterDAO for the type of database of the jdni datasource
	 * @param jndiDatasourceName jndi datasource of the xmap admin db
	 * @return a CheckListImporterDAO object
	 * @throws DAODataSourceException if the type of database is not supported
	 */
	public static CheckListImporterDAO getImporterDAO(String jndiDatasourceName) throws DAODataSourceException {
		CheckListImporterDAO importerDAO = null;		
		try {			
			DataSource ds = getDataSource(jndiDatasourceName);
			DataSourceType dsType = getDataSourceType(ds);
		
			switch(dsType){
				case MySQL: 
					importerDAO = new CheckListImporterDAOMySQLImpl(ds);		
					break;
				case SQLite:
					importerDAO = new CheckListImporterDAOSQLiteImpl(ds);
					break;
				default:
					throw new RuntimeException("Type of checklist importer dao not found.");
			}				
		} 
		catch (Exception ex) {
			throw new DAODataSourceException(ex);
		}
		
		return importerDAO;
	}
	
	
	/****************************************************************************************/
	/* PRIVATE METHODS																		*/														
	/****************************************************************************************/
	
	/**
	 * Method that receiving a jndiDatasourceName returns its dataSource.
	 * @param jndiDatasourceName name of jndi resource from which obtain the datasource
	 * @return the datasource obtained from the jndi name received as parameter
	 * @throws NamingException
	 */
	private static DataSource getDataSource(String jndiDatasourceName) throws NamingException {
        Context initContext = new InitialContext();
        Context webContext = (Context) initContext.lookup("java:/comp/env");
        return (DataSource) webContext.lookup(jndiDatasourceName);
    }	
	
	
	/**
	 * Method that returns the type of database (e.g. MySQL, SQLite, ..) from the metadata of the datatasource 
	 * received as a parameter.
	 * @param dataSource datasource to obtain the type of database from its metadata
	 * @return DataSourceType indicating the type of databases (e.g. MySQL, SQLite, ..)
	 * @throws SQLException 
	 */
	private static DataSourceType getDataSourceType(DataSource dataSource) throws SQLException { 
		DataSourceType dsType = null;		
		String dbProductName;  
			
        Connection conn = dataSource.getConnection();
        try {
		    DatabaseMetaData metadata = conn.getMetaData();
		    dbProductName = metadata.getDatabaseProductName();
        }
        finally {
		    conn.close();
        }		
		
		if (dbProductName.equalsIgnoreCase("MySQL")) {
			dsType = DataSourceType.MySQL;
		}
		else if (dbProductName.equalsIgnoreCase("SQLite")){				
			dsType = DataSourceType.SQLite;
		}
		return dsType;
	}			
	
	
}
