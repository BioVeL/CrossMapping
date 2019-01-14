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

import org.openbio.xmap.common.dao.util.exception.DAOException;
import org.openbio.xmap.common.dao.util.exception.DAOWriteException;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Interface that define the method that any helper for relation databases should provided
 * <p>
 * OpenBio XMap
 * <p>
 * Date: 15/05/12
 * User: scmjpg
 */
public interface RelationalHelper {

	/**
	 * Method responsible to check if the database which connection is received as parameter
	 * is empty. In other words, it hasn't got any table.
	 * @param conn sql connection to the database
	 * @return boolean indicating if the database is empty or not
	 * @throws SQLException
	 */	
    boolean databaseIsEmpty(Connection conn) throws SQLException;

    /**
     * Method responsible to escape quotes characters
     * @param unescaped string with its characters unescaped 
     * @return string with its character escaped
     */
    String escapeIdentifier(String unescaped);
    
	/**
	 * Method responsible to enable the constraint to check foreign keys
	 * @param conn sql connection
	 * @throws SQLException
	 */    
    void enableForeignKeyChecks(Connection conn)
            throws SQLException;

    /**
     * Method responsible to disable the constraint to check foreign keys
	 * @param conn sql connection
	 * @throws SQLException
     */
    void disableForeignKeyChecks(Connection conn)
            throws SQLException;
   
    /**
     * Method responsible to delete all the tables in the database which connection is received as parameter
     * @param conn sql connection to the database
     * @throws SQLException
     */
    void deleteAll(Connection conn) throws SQLException;

    /**
     * Method responsible to execute the sql commands that are prensent in the input stream received as second parameter
     * in the database which connection is received as first parameter
     * @param conn sql connection to the database
     * @param is input stream with the sql commands to be executed
     */    
    void executeStatements(Connection conn, InputStream is) 
            throws SQLException, DAOException;
    
    
    /**
     * Method responsible to execute the sql command that receives as parameter 
     * @param conn sql connection to the database
     * @param sql string with the sql command to execute
     * @return either (1) the row count for SQL Data Manipulation Language (DML) statements or (2) 0 for SQL statements that return nothing
     * @throws SQLException
     */
    int executeUpdate(Connection conn, String sql) throws SQLException;
             

    /**
     * Method responsible to create the sql index for the fields in the table received as parameter
     * @param conn sql connection to the database
     * @param indexName name of the index
     * @param tableName name of the table in which create the index
     * @param fields array of string with the name of the fields that forms the index
     * @param unique boolean that indicate if the index should be defiend as unique or not
     */    
    void createIndex(Connection conn, String indexName, String tableName, String[] fields, boolean unique)
            throws SQLException;

    /**
     * Method responsible to get the sql command for insert a new register in the table received as parameter 
     * using the defaults values of the fields in this table
     * @param tableName name of the table
     * @return string with the sql comamnd to do the insert
     */
    String createDefaultSQL(String tableName);

    /**
     * Method responsible to obtain the prepare statement for returning the automatic generated keys
     * @param conn connection in which get the prepare statement
     * @param sql string with sql command to be executed 
     * @return prepare statement 
     * @throws SQLException
     */    
    PreparedStatement createGeneratedKeysPreparedStatement(Connection conn, String sql) throws SQLException;

    /**
     * Method responsible to return the generated id after inserting a new register in the 
     * table received as parameter with all its fields in their default values
     * @param conn sql connection
     * @param tableName name of the table in which create a new register with default values
     * @return integer with the value of the id generated for the register inserted
     * @throws SQLException
     * @throws DAOWriteException
     */    
    int generateId(Connection conn, String tableName) throws SQLException, DAOWriteException;
    			   
}
