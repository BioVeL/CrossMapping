/*
 * #%L
 * XMap Admin DAO
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
package org.openbio.xmap.common.dao.admin.impl;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import org.openbio.xmap.common.dao.util.exception.DAOException;
import org.openbio.xmap.common.dao.util.helper.MySQLHelperImpl;

public class AdminDAOMySQLImpl extends AdminDAORelationalImpl {

	/****************************************************************************************/
	/* CONSTRUCTORS																			*/		
	/****************************************************************************************/
	
	/**
	 * Constructor of IndexStoreDAO for MySQL. It will call the super class (AdminDAORelationalImpl)
	 * with the datasource and with the specific implementation of relationHelper for MySQL databases 
	 * @param ds datasource to connect to the admin database
	 * @throws DAOException
	 */	
	public AdminDAOMySQLImpl(DataSource ds) throws DAOException {
		super(ds, new MySQLHelperImpl());		
	}
	
	/**
	 * Method that will return the specific prepare statement for MySQL databases, with the information passed as parameters
	 * @param sql sql command for which we create the prepare statement 
	 * @param conn sql connection
	 * @param returnKey boolean that indicate if the prepare statement should or not return the auto generated key after execute
	 * the sql command
	 * @return a PreparedStatement object
	 * @throws SQLException
	 */		
	protected PreparedStatement getPreparedStatement(String sql, Connection conn, boolean returnKey) throws SQLException{
		PreparedStatement st;
		if (returnKey){
			st = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
		}
		else{
			st = conn.prepareStatement(sql);
		}
		return st;
	}		
	
	/****************************************************************************************/
	/* PROTECTED METHODS																	*/														
	/****************************************************************************************/		
	
	/**
	 * Method that reads the Log tables schema for MySQL database from a resource file and 
	 * return an object inputStream to process it 
	 * @return inputStream of the sql file with the commands to create the tables needed for the administration 
	 * of the xmap application
	 */		
    protected InputStream openSchemaStream() {
        return this.getClass().getResourceAsStream("/sql/admin-schema-MySQL5.sql");
    }	
    
	/**
	 * Method that reads the Log insert data sql commands for MySQL database from a resource file and 
	 * return an object inputStream to process it 
	 * @return inputStream of the sql file with the command to insert the data needed for the administration 
	 * of the xmap application
	 */	       
    protected InputStream openInitialDataStream() {
        return this.getClass().getResourceAsStream("/sql/admin-data.sql");
    }    
}
