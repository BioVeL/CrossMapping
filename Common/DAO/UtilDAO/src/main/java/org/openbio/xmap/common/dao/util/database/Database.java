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

import java.io.IOException;
import java.util.List;

import java.sql.SQLException;

import org.openbio.xmap.common.utils.task.Task;

/**
 * Interface that defines the methods that a database implementation should offer 
 * <p>
 * OpenBio XMap 
 * <p>
 * Date: 15/05/12
 * @author scmjpg 
 */
public interface Database {

	/**
	 * Method responsible to return the product name of the server database (e.g. MySQL, SQLite, etc.)
	 * @return string with the product name of the database (e.g. MySQL, SQLite, etc.)
	 * @throws DatabaseException
	 */
	String getProductName() throws DatabaseException;
	
	/**
	 * Method responsible to return the host name of the server database
	 * @return string with the host name of the server database
	 * @throws DatabaseException
	 * @throws SQLException
	 */
    String getHost() throws DatabaseException, SQLException;

    /**
     * Method responsible to return the port number of the server database
     * @return integer with the port number of the server database
     * @throws DatabaseException
     * @throws SQLException
     */
    Integer getPort() throws DatabaseException, SQLException;

    /**
     * Method responsible to return the user name used in order to connect to the database
     * @return string the user name used in order to connect to the database
     * @throws DatabaseException
     * @throws SQLException
     */
    String getUsername() throws DatabaseException, SQLException;

    /**
     * Method to return if the database object knows the password used to connect to it 
     * @return boolean with the value true if we know the password to connect to the database and false in the other case 
     */
    boolean knowsPassword();

    /***
     * Method to get the password of the user to connect to the database
     * @return string with the password of the user to connect to the database
     * @throws DatabaseException
     */
    String getPassword() throws DatabaseException;
    
    /**
     * Method to set the password of the user to connect to the database
     * @param password string with password of the user to connect to the database
     * @throws DatabaseException
     */
    void setPassword(String password) throws DatabaseException;

    /**
     * Method to set the prefix of the databases that will be created automatically in the server database
     * @param prefix string with the prefix of the databases that will be created automatically in the server database
     */
    void setCatalogPrefix(String prefix);

    /**
     * Method to return the database connection object to connect to this database
     * @return the database connection object to connect to this database
     * @throws DatabaseException
     * @throws SQLException
     */
    DatabaseConnection getConnection() throws DatabaseException, SQLException;

    /**
     * Method to return the database connection object to connect to this database specifying its catalog
     * @param catalogName name of the catalog to connect to
     * @return the database connection object to connect to this database
     * @throws DatabaseException
     * @throws SQLException
     */
    DatabaseConnection getConnection(String catalogName) throws DatabaseException, SQLException;

    /**
     * Method responsible to create a new database (catalog) in the server database
     * @return a string with the name of the new catalog created
     * @throws SQLException
     */
    String createCatalog() throws SQLException;
    
    /**
     * Method to grant access for the given user to the given catalog database 
     * @param catalogName name of the database in which we will grant access to the user userName
     * @param userName name of the user to granted access
     * @throws SQLException
     */
    void grantAccessToCatalog(String catalogName, String userName) throws SQLException;

    /**
     * Method responsible to delete the database received a parameter from the server database
     * @param catalogName name of the database to delete
     * @throws SQLException
     */
    void deleteCatalog(String catalogName) throws SQLException;

    /**
     * Method responsible to delete the database received a parameter from the server database without
     * raising exception
     * @param catalogName name of the database to delete
     */
    void deleteCatalogQuietly(String catalogName);

    /**
     * Method to create a transfer task to transfer the data present in the tables received as third parameter 
     * from the source database to the target database received as first and second parameter respectively
     * @param sourceCatalog name of the source database
     * @param targetCatalog name of the target database
     * @param tableNames list with the name of the tables that we want to transfer its content from the source db to the target db
     * @return task to realize the transfer
     * @throws IOException
     * @throws DatabaseException
     */
    Task createTransferTask(String sourceCatalog, String targetCatalog, List<String> tableNames) throws IOException, DatabaseException;
}
