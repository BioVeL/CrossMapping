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

import org.openbio.xmap.common.dao.util.exception.DAOWriteException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Class that facilitate the execution of jdbc sql commands in SQLite databases
 * <p>
 * OpenBio XMap
 * <p>
 * Date: 15/05/12
 * @author scmjpg
 */
public class SQLiteHelperImpl extends JDBCHelperImpl {

	/**
	 * Method responsible to enable the constraint to check foreign keys
	 * @param conn sql connection
	 * @throws SQLException
	 */	
    public void enableForeignKeyChecks(Connection conn) throws SQLException {
        Statement st = conn.createStatement();
        try {
            st.execute("pragma foreign_keys = on");
        }
        finally {
            st.close();
        }
    }

	/**
	 * Method responsible to disable the constraint to check foreign keys
	 * @param conn sql connection
	 * @throws SQLException
	 */    
    public void disableForeignKeyChecks(Connection conn) throws SQLException {
        Statement st = conn.createStatement();
        try {
            st.execute("pragma foreign_keys = off");
        }
        finally {
            st.close();
        }
    }

    /**
     * Method responsible to delete all the tables in the database which connection is received as parameter
     * @param conn sql connection to the database
     * @throws SQLException
     */    
    public void deleteAll(Connection conn) throws SQLException {
        disableForeignKeyChecks(conn);
        try {
            Statement st = conn.createStatement();
            try {
                DatabaseMetaData metadata = conn.getMetaData();
                ResultSet rsTables = metadata.getTables(null, null, "%", null);
                List<String> tables = new ArrayList<String>();
                List<String> views = new ArrayList<String>();
                while (rsTables.next()) {
                    String tableName = rsTables.getString(3);
                    String tableType =  rsTables.getString(4);
                    if (tableType.equalsIgnoreCase("table"))
                        tables.add(tableName);
                    else if (tableType.equalsIgnoreCase("view"))
                        views.add(tableName);
                    else if (tableType.equalsIgnoreCase("index"))
                        ; // ignore
                    else
                        throw new RuntimeException("table " + tableName + " has unknown type " + tableType);
                }
                for (String viewName : views) {
                    st.execute("drop view " + escapeIdentifier(viewName));
                }
                for (String tableName : tables) {
                    st.execute("drop table " + escapeIdentifier(tableName));
                }
            }
            finally {
                st.close();
            }
        }
        finally {
            enableForeignKeyChecks(conn);
        }
    }

    /**
     * Method responsible to get the sql command for insert a new register in the table received as parameter 
     * using the defaults values of the fields in this table
     * @param tableName name of the table
     * @return string with the sql command to do the insert
     */    
    @Override
    public String createDefaultSQL(String tableName) {
        return "insert into `" + tableName + "` default values";
    }

    /**
     * Method responsible to obtain the prepare statement for returning the automatic generated keys
     * @param conn connection in which get the prepare statement
     * @param sql string with sql command to be executed 
     * @return prepare statement 
     * @throws SQLException
     */      
    @Override
    public PreparedStatement createGeneratedKeysPreparedStatement(Connection conn, String sql) throws SQLException {
        return conn.prepareStatement(sql);
    }

    /**
     * Method responsible to return the generated id after inserting a new register in the 
     * table received as parameter with all its fields in their default values
     * @param conn sql connection
     * @param tableName name of the table in which create a new register with default values
     * @return integer with the value of the id generated for the register inserted
     * @throws SQLException
     * @throws DAOWriteException
     */       
    @Override
    public int generateId(Connection conn, String tableName) throws SQLException, DAOWriteException {
        String sqlTaxon = createDefaultSQL(tableName);
        Statement stTaxon = conn.createStatement();
        stTaxon.executeUpdate(sqlTaxon);
        ResultSet rsTaxon = stTaxon.getGeneratedKeys();
        if ( rsTaxon.next() )
            return rsTaxon.getInt(1);
        else
            throw new DAOWriteException(tableName);
    }

}
