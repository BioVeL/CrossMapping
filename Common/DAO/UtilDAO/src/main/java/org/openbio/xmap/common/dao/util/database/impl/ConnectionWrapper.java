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

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;

import java.sql.*;

/**
 * Class that wraps sql connection hidden the fact if we are using the version 1.6 or 1.7 of sql connection 
 * <p>
 * OpenBio XMap
 * <p>
 * Date: 15/05/12
 * @author scmjpg
 * @see java.sql.Connection
 */
public class ConnectionWrapper implements Connection {

	/****************************************************************************************/
	/* ATTRIBUTES																			*/														
	/****************************************************************************************/
	
	/**
	 * SQl connection
	 */
    protected final Connection conn;

    
	/****************************************************************************************/
	/* CONSTRUCTORS																			*/		
	/****************************************************************************************/    
    
    /**
     * Constructor of ConnectionWrapper receiving the sql connection to wrap
     * @param conn
     */
    protected ConnectionWrapper(Connection conn) {
        this.conn = conn;
    }

    
	/****************************************************************************************/
	/* PUBLIC METHODS																		*/														
	/****************************************************************************************/
    
    @Override
    public Statement createStatement() throws SQLException {
        return conn.createStatement();
    }

    @Override
    public PreparedStatement prepareStatement(String s) throws SQLException {
        return conn.prepareStatement(s);
    }

    @Override
    public CallableStatement prepareCall(String s) throws SQLException {
        return conn.prepareCall(s);
    }

    @Override
    public String nativeSQL(String s) throws SQLException {
        return conn.nativeSQL(s);
    }

    @Override
    public void setAutoCommit(boolean b) throws SQLException {
        conn.setAutoCommit(b);
    }

    @Override
    public boolean getAutoCommit() throws SQLException {
        return conn.getAutoCommit();
    }

    @Override
    public void commit() throws SQLException {
        conn.commit();
    }

    @Override
    public void rollback() throws SQLException {
        conn.rollback();
    }

    @Override
    public void close() throws SQLException {
        conn.close();
    }

    @Override
    public boolean isClosed() throws SQLException {
        return conn.isClosed();
    }

    @Override
    public DatabaseMetaData getMetaData() throws SQLException {
        return conn.getMetaData();
    }

    @Override
    public void setReadOnly(boolean b) throws SQLException {
        conn.setReadOnly(b);
    }

    @Override
    public boolean isReadOnly() throws SQLException {
        return conn.isReadOnly();
    }

    @Override
    public void setCatalog(String s) throws SQLException {
        conn.setCatalog(s);
    }

    @Override
    public String getCatalog() throws SQLException {
        return conn.getCatalog();
    }

    @Override
    public void setTransactionIsolation(int i) throws SQLException {
        conn.setTransactionIsolation(i);
    }

    @Override
    public int getTransactionIsolation() throws SQLException {
        return conn.getTransactionIsolation();
    }

    @Override
    public SQLWarning getWarnings() throws SQLException {
        return conn.getWarnings();
    }

    @Override
    public void clearWarnings() throws SQLException {
        conn.clearWarnings();
    }

    @Override
    public Statement createStatement(int i, int i1) throws SQLException {
        return conn.createStatement();
    }

    @Override
    public PreparedStatement prepareStatement(String s, int i, int i1) throws SQLException {
        return conn.prepareStatement(s, i, i1);
    }

    @Override
    public CallableStatement prepareCall(String s, int i, int i1) throws SQLException {
        return conn.prepareCall(s, i, i1);
    }

    @Override
    public Map<String, Class<?>> getTypeMap() throws SQLException {
        return conn.getTypeMap();
    }

    @Override
    public void setTypeMap(Map<String, Class<?>> stringClassMap) throws SQLException {
        conn.setTypeMap(stringClassMap);
    }

    @Override
    public void setHoldability(int i) throws SQLException {
        conn.setHoldability(i);
    }

    @Override
    public int getHoldability() throws SQLException {
        return conn.getHoldability();
    }

    @Override
    public Savepoint setSavepoint() throws SQLException {
        return conn.setSavepoint();
    }

    @Override
    public Savepoint setSavepoint(String s) throws SQLException {
        return conn.setSavepoint();
    }

    @Override
    public void rollback(Savepoint savepoint) throws SQLException {
        conn.rollback();
    }

    @Override
    public void releaseSavepoint(Savepoint savepoint) throws SQLException {
        conn.releaseSavepoint(savepoint);
    }

    @Override
    public Statement createStatement(int i, int i1, int i2) throws SQLException {
        return conn.createStatement();
    }

    @Override
    public PreparedStatement prepareStatement(String s, int i, int i1, int i2) throws SQLException {
        return conn.prepareStatement(s, i, i1, i2);
    }

    @Override
    public CallableStatement prepareCall(String s, int i, int i1, int i2) throws SQLException {
        return conn.prepareCall(s, i, i1, i2);
    }

    @Override
    public PreparedStatement prepareStatement(String s, int i) throws SQLException {
        return conn.prepareStatement(s, i);
    }

    @Override
    public PreparedStatement prepareStatement(String s, int[] intArray) throws SQLException {
        return conn.prepareStatement(s, intArray);
    }

    @Override
    public PreparedStatement prepareStatement(String s, String[] strings) throws SQLException {
        return conn.prepareStatement(s, strings);
    }

    @Override
    public Clob createClob() throws SQLException {
        return conn.createClob();
    }

    @Override
    public Blob createBlob() throws SQLException {
        return conn.createBlob();
    }

    @Override
    public NClob createNClob() throws SQLException {
        return conn.createNClob();
    }

    @Override
    public SQLXML createSQLXML() throws SQLException {
        return conn.createSQLXML();
    }

    @Override
    public boolean isValid(int i) throws SQLException {
        return conn.isValid(i);
    }

    @Override
    public void setClientInfo(String s, String s1) throws SQLClientInfoException {
        conn.setClientInfo(s, s1);
    }

    @Override
    public void setClientInfo(Properties properties) throws SQLClientInfoException {
        conn.setClientInfo(properties);
    }

    @Override
    public String getClientInfo(String s) throws SQLException {
        return conn.getClientInfo(s);
    }

    @Override
    public Properties getClientInfo() throws SQLException {
        return conn.getClientInfo();
    }

    @Override
    public Array createArrayOf(String s, Object[] objects) throws SQLException {
        return conn.createArrayOf(s, objects);
    }

    @Override
    public Struct createStruct(String s, Object[] objects) throws SQLException {
        return conn.createStruct(s, objects);
    }

    @Override
    public <T> T unwrap(Class<T> tClass) throws SQLException {
        return conn.unwrap(tClass);
    }

    @Override
    public boolean isWrapperFor(Class<?> aClass) throws SQLException {
        return conn.isWrapperFor(aClass);
    }

    // Following added to support JDK 1.7 - use reflection to enable it to be compiled
    // for 1.6 and 1.7
    public void setSchema(String schema) throws SQLException {
        try {
            conn.getClass().getMethod("setSchema", String.class).invoke(conn, schema);
        }
        catch (InvocationTargetException e) {
            Throwable cause = e.getCause();
            if (SQLException.class.isInstance(cause)) {
                throw (SQLException) cause;
            }
            else {
                throw new SQLException(cause);
            }
        }
        catch (NoSuchMethodException e) {
            throw new SQLFeatureNotSupportedException(e);
        }
        catch (IllegalAccessException e) {
            throw new SQLException(e);
        }
    }

    public String getSchema() throws SQLException {
        try {
            return (String) conn.getClass().getMethod("getSchema").invoke(conn);
        }
        catch (InvocationTargetException e) {
            Throwable cause = e.getCause();
            if (SQLException.class.isInstance(cause)) {
                throw (SQLException) cause;
            }
            else {
                throw new SQLException(cause);
            }
        }
        catch (NoSuchMethodException e) {
            throw new SQLFeatureNotSupportedException(e);
        }
        catch (IllegalAccessException e) {
            throw new SQLException(e);
        }
    }

    public void abort(Executor executor) throws SQLException {
        try {
            conn.getClass().getMethod("abort", Executor.class).invoke(conn, executor);
        }
        catch (InvocationTargetException e) {
            Throwable cause = e.getCause();
            if (SQLException.class.isInstance(cause)) {
                throw (SQLException) cause;
            }
            else {
                throw new SQLException(cause);
            }
        }
        catch (NoSuchMethodException e) {
            throw new SQLFeatureNotSupportedException(e);
        }
        catch (IllegalAccessException e) {
            throw new SQLException(e);
        }
    }

    public void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException {
        try {
            conn.getClass().getMethod("setNetworkTimeout", Executor.class, Integer.class).
                    invoke(conn, executor, milliseconds);
        }
        catch (InvocationTargetException e) {
            Throwable cause = e.getCause();
            if (SQLException.class.isInstance(cause)) {
                throw (SQLException) cause;
            }
            else {
                throw new SQLException(cause);
            }
        }
        catch (NoSuchMethodException e) {
            throw new SQLFeatureNotSupportedException(e);
        }
        catch (IllegalAccessException e) {
            throw new SQLException(e);
        }
    }

    public int getNetworkTimeout() throws SQLException {
        try {
            return (Integer) conn.getClass().getMethod("getNetworkTimeout").invoke(conn);
        }
        catch (InvocationTargetException e) {
            Throwable cause = e.getCause();
            if (SQLException.class.isInstance(cause)) {
                throw (SQLException) cause;
            }
            else {
                throw new SQLException(cause);
            }
        }
        catch (NoSuchMethodException e) {
            throw new SQLFeatureNotSupportedException(e);
        }
        catch (IllegalAccessException e) {
            throw new SQLException(e);
        }
    }

}
