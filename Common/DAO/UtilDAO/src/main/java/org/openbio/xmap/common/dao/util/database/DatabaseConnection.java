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

import java.io.InputStream;
import java.util.List;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Interface that extends from sql conenection adding new methods
 * in order to start transactions, delete and create schemas, etc. 
 * <p>
 * OpenBio XMap
 * <p>
 * Date: 15/05/12
 * @author scmjpg
 */
public interface DatabaseConnection extends Connection {

	/**
	 * Method that return the Databases object for the DatabaseConnection 
	 * @return Databases object for this DatabaseConnection
	 * @throws SQLException
	 * @throws DatabaseException
	 */
    Database getDatabase() throws SQLException, DatabaseException;

    /**
     * Method that start a transaction returning it in order to the client do the commit or rollback
     * @return The transaction started allowing the client later to do the commit or rollback
     * @throws SQLException
     */
    Transaction startTransaction() throws SQLException;

    /**
     * Method to execute the sql commands present in the input stream received as a parameter
     * @param in input stream with the sql command to be executed in the database
     * @throws Exception
     * @throws SQLException
     * @throws DatabaseException
     */
    void executeStream(InputStream in) throws Exception, SQLException, DatabaseException;

    /**
     * Method responsible to return the list with name of the tables present in the database
     * @return list of strings with name of the tables present in the database
     * @throws SQLException
     */
    List<String> getTableNames() throws SQLException;

    /**
     * Method responsible to delete all the tables presented in the database
     * @throws SQLException
     */
    void deleteAll() throws SQLException;

    /**
     * Method responsible to return the number of the tables present in the database
     * @return number of tables present in the database
     * @throws SQLException
     */
    int countTables() throws SQLException;

    /**
     * Method to escape the special characters presented in the string received as a parameter 
     * @param unescaped string to be escaped
     * @return string with the special character escaped 
     */
    String escapeIdentifier(String unescaped);
}
