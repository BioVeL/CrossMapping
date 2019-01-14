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

import java.util.ArrayList;
import java.util.List;

import java.sql.*;

import org.openbio.xmap.common.dao.util.database.Database;
import org.openbio.xmap.common.dao.util.database.DatabaseConnection;
import org.openbio.xmap.common.dao.util.database.DatabaseException;
import org.openbio.xmap.common.dao.util.database.Transaction;

/**
 * Abstract class that implement the interface DatabaseConnection 
 * <p>
 * OpenBio XMap
 * <p>
 * Date: 15/05/12
 * @author scmjpg
 */
public abstract class DatabaseConnectionImpl extends ConnectionWrapper implements DatabaseConnection {

	/****************************************************************************************/
	/* ATTRIBUTES																			*/														
	/****************************************************************************************/	
	
	/**
	 * Database object for this database connection
	 */
    protected final Database database;

    
    
	/****************************************************************************************/
	/* CONSTRUCTORS																			*/		
	/****************************************************************************************/
    
    /**
     * Constructor of the database connection receiving the database and the sql connection
     * @param database
     * @param conn
     */
    protected DatabaseConnectionImpl(Database database, Connection conn) {
        super(conn);
        this.database = database;
    }


    
	/****************************************************************************************/
	/* PUBLIC METHODS																		*/														
	/****************************************************************************************/
    
	/**
	 * Method that return the Databases object for the DatabaseConnection 
	 * @return Databases object for this DatabaseConnection
	 * @throws SQLException
	 * @throws DatabaseException
	 */    
    @Override
    public Database getDatabase() throws SQLException, DatabaseException {
        return database;
    }

    /**
     * Method that start a transaction returning it in order to the client do the commit or rollback
     * @return The transaction started allowing the client later to do the commit or rollback
     * @throws SQLException
     */    
    @Override
    public Transaction startTransaction() throws SQLException {
        return new TransactionImpl(conn);
    }

    /**
     * Method responsible to return the list with name of the tables present in the database
     * @return list of strings with name of the tables present in the database
     * @throws SQLException
     */   
    @Override
    public List<String> getTableNames() throws SQLException {
        List<String> tableNames = new ArrayList<String>();
        DatabaseMetaData metadata = conn.getMetaData();
        ResultSet rs = metadata.getTables(null, null, "%", null);
        try {
            while ( rs.next() ) {
                String tableName = rs.getString(3);
                String tableType =  rs.getString(4);
                if (tableType.equalsIgnoreCase("table")) {
                    tableNames.add(tableName);
                }
            }
        }
        finally {
            rs.close();
        }
        return tableNames;
    }
    
    /**
     * Method responsible to delete all the tables presented in the database
     * @throws SQLException
     */    
    @Override
    public void deleteAll() throws SQLException {
        Statement st = conn.createStatement();
        try {
            deleteAll(st);
        }
        finally {
            st.close();
        }
    }

    /**
     * Method responsible to return the number of the tables present in the database
     * @return number of tables present in the database
     * @throws SQLException
     */    
    @Override
    public int countTables() throws SQLException {
        DatabaseMetaData metadata = conn.getMetaData();
        ResultSet rs = metadata.getTables(null, null, "%", null);
        try {
            return rs.last() ? rs.getRow() : 0;
        }
        finally {
            rs.close();
        }
    }

    /**
     * Method responsible to determine if the database is empty, it doesn't have any table
     * @return true if the database doesn't have any table, false otherwise
     * @throws SQLException
     */
    public boolean databaseIsEmpty() throws SQLException {
        DatabaseMetaData metadata = conn.getMetaData();
        ResultSet rs = metadata.getTables(null, null, "%", null);
        try {
            return !rs.next();
        }
        finally {
            rs.close();
        }
    }

    /**
     * Method to escape the special characters presented in the string received as a parameter 
     * @param unescaped string to be escaped
     * @return string with the special character escaped 
     */    
    @Override
    public String escapeIdentifier(String unescaped) {
         return "`" + unescaped + "`";
    }
    
    
	/****************************************************************************************/
	/* PROTECTED METHODS																	*/														
	/****************************************************************************************/	
    
    /**
     * Method responsible to delete all tables using the sql statement received as parameter    
     * @param st sql statement
     * @throws SQLException
     */
    protected void deleteAll(Statement st) throws SQLException {
        DatabaseMetaData metadata = conn.getMetaData();
        ResultSet rs = metadata.getTables(null, null, "%", null);
        try {
            while ( rs.next() ) {
                String tableName = rs.getString(3);
                String tableType =  rs.getString(4);
                if (tableType.equalsIgnoreCase("table")) {
                    st.execute("drop table " + escapeIdentifier(tableName));
                }
                else if (tableType.equalsIgnoreCase("view")) {
                    st.execute("drop view " + escapeIdentifier(tableName));
                }
                else {
                    throw new RuntimeException("table " + tableName + " has unknown type " + tableType);
                }
            }
        }
        finally {
            rs.close();
        }
    }    
}
