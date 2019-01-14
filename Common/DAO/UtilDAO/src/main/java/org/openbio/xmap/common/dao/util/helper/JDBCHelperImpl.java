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
import org.openbio.xmap.common.dao.util.exception.DAOReadException;
import org.openbio.xmap.common.dao.util.exception.DAOValueException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.*;

/**
 * Abstract class to facilitate the execution of jdbc sql commands
 * <p>
 * OpenBio XMap
 * <p>
 * Date: 15/05/12
 * @author scmjpg
 */
public abstract class JDBCHelperImpl implements RelationalHelper {

	/**
	 * Method responsible to check if the database which connection is received as parameter
	 * is empty. In other words, it hasn't got any table.
	 * @param conn sql connection to the database
	 * @return boolean indicating if the database is empty or not
	 * @throws SQLException
	 */
    public boolean databaseIsEmpty(Connection conn) throws SQLException {
        DatabaseMetaData metadata = conn.getMetaData();
        ResultSet rsTables = metadata.getTables(null, null, "%", null);
        return !rsTables.next();
    }

    /**
     * Method responsible to escape quotes characters
     * @param unescaped string with its characters unescaped 
     * @return string with its character escaped
     */
    public String escapeIdentifier(String unescaped) {
         return "`" + unescaped + "`";
    }

    /**
     * Method responsible to delete all the tables in the database which connection is received as parameter
     * @param conn sql connection to the database
     * @throws SQLException
     */
    public void deleteAll(Connection conn) throws SQLException {
        Statement st = conn.createStatement();
        disableForeignKeyChecks(conn);
        try {
            DatabaseMetaData metadata = conn.getMetaData();
            ResultSet rsTables = metadata.getTables(null, null, "%", null);
            while (rsTables.next()) {
                String tableName = rsTables.getString(3);
                String tableType =  rsTables.getString(4);
                if (tableType.equalsIgnoreCase("table"))
                    st.execute("drop table " + escapeIdentifier(tableName));
                else if (tableType.equalsIgnoreCase("view"))
                    st.execute("drop view " + escapeIdentifier(tableName));
                else
                    throw new RuntimeException("table " + tableName + " has unknown type " + tableType);
            }
        }
        finally {
            enableForeignKeyChecks(conn);
        }
    }

    /**
     * Method responsible to execute the sql commands that are prensent in the input stream received as second parameter
     * in the database which connection is received as first parameter
     * @param conn sql connection to the database
     * @param is input stream with the sql commands to be executed
     */
    public void executeStatements(Connection conn, InputStream is) throws DAOException, SQLException {
        executeStatements(conn, is, 0);
    }
    
    
    /**
     * Method responsible to execute the sql command that receives as parameter 
     * @param conn sql connection to the database
     * @param sql string with the sql command to execute
     * @return either (1) the row count for SQL Data Manipulation Language (DML) statements or (2) 0 for SQL statements that return nothing
     * @throws SQLException
     */
    public int executeUpdate(Connection conn, String sql) throws SQLException{
		PreparedStatement st = createGeneratedKeysPreparedStatement(conn,sql);
		try{
			return st.executeUpdate();							
		}
		finally{
			st.close();
		}		    	
    }


    /**
     * Method responsible to execute the sql commands from the input stream into the database 
     * @param conn sql connection to the database
     * @param is input stream where the sql commands are
     * @param lineno number of line read
     * @throws DAOException
     * @throws SQLException
     */
    public void executeStatements(Connection conn, InputStream is, int lineno) throws DAOException, SQLException {
        Statement st = conn.createStatement();
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            StringBuilder sb = new StringBuilder();
            String buf = br.readLine();
            while (buf != null) {
                lineno += 1;
                sb.append(buf.trim());
                int length = sb.length();
                if (length > 0 && sb.charAt(length - 1) == ';') {
                    try {
                        st.execute(sb.toString());
                    }
                    catch (SQLException e) {
                        throw new DAOValueException("Line " + lineno, e);
                    }
                    sb = new StringBuilder();
                }
                else {
                    sb.append("\n");
                }
                buf = br.readLine();
            }
        }
        catch (IOException e) {
            throw new DAOReadException(e);
        }
        finally {
            st.close();
        }
    }

    /**
     * Method responsible to create the sql index for the fields in the table received as parameter
     * @param conn sql connection to the database
     * @param indexName name of the index
     * @param tableName name of the table in which create the index
     * @param fields array of string with the name of the fields that forms the index
     * @param unique boolean that indicate if the index should be defiend as unique or not
     */
    public void createIndex(Connection conn, String indexName, String tableName, String[] fields, boolean unique)
            throws SQLException {
        Statement st = conn.createStatement();
        try {
            StringBuilder sb = new StringBuilder("create");
            if (unique) sb.append(" unique");
            sb.append(" index " + escapeIdentifier(indexName) + " on " + escapeIdentifier(tableName) + " (");
            boolean first = true;
            for (String field : fields) {
                if (first) first = false; else sb.append(",");
                sb.append(escapeIdentifier(field));
            }
            sb.append(")");
            st.execute(sb.toString());
        }
        finally {
            st.close();
        }
    }
      
}
