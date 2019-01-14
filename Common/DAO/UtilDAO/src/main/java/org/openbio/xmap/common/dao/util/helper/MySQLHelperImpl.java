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

/**
 * Class that facilitate the execution of jdbc sql commands in MySQL databases
 * <p>
 * OpenBio XMap
 * <p>
 * Date: 15/05/12
 * @author scmjpg
 */
public class MySQLHelperImpl extends JDBCHelperImpl {

	/**
	 * Method responsible to enable the constraint to check foreign keys
	 * @param conn sql connection
	 * @throws SQLException
	 */
    public void enableForeignKeyChecks(Connection conn) throws SQLException {
        Statement st = conn.createStatement();
        st.execute("set foreign_key_checks = 1");
    }

    /**
     * Method responsible to disable the constraint to check foreign keys
	 * @param conn sql connection
	 * @throws SQLException
     */
    public void disableForeignKeyChecks(Connection conn) throws SQLException {
        Statement st = conn.createStatement();
        st.execute("set foreign_key_checks = 0");
    }

    /**
     * Method responsible to get the sql command for insert a new register in the table received as parameter 
     * using the defaults values of the fields in this table
     * @param tableName name of the table
     * @return string with the sql command to do the insert
     */
    @Override
    public String createDefaultSQL(String tableName) {
        return "insert into `" + tableName + "` () values ()";
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
        return conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
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
        String sqlDefaultInsert = createDefaultSQL(tableName);
        Statement stDefaultInsert = conn.createStatement();
        stDefaultInsert.executeUpdate(sqlDefaultInsert, Statement.RETURN_GENERATED_KEYS);
        ResultSet rsDefaultInsert = stDefaultInsert.getGeneratedKeys();
        if ( rsDefaultInsert.next() )
            return rsDefaultInsert.getInt(1);
        else
            throw new DAOWriteException(tableName);
    }
    
 	    
}
