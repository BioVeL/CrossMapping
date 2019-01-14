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
package org.openbio.xmap.common.dao.util.database.mysql;

import java.io.IOException;
import java.net.ConnectException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import javax.sql.DataSource;
import org.apache.commons.dbcp.BasicDataSource;

import org.openbio.xmap.common.dao.util.database.DatabaseNetworkException;
import org.openbio.xmap.common.dao.util.database.DatabaseConnection;
import org.openbio.xmap.common.dao.util.database.DatabaseException;
import org.openbio.xmap.common.dao.util.database.impl.DatabaseImpl;
import org.openbio.xmap.common.utils.misc.ExceptionHelper;
import org.openbio.xmap.common.utils.task.Task;

/**
 * Class that implement the interface Database for MySQL databases 
 * <p>
 * OpenBio XMap
 * <p>
 * Date: 15/05/12
 * @author scmjpg
 */
public class MySQLDatabaseImpl extends DatabaseImpl {

	/****************************************************************************************/
	/* ATTRIBUTES																			*/														
	/****************************************************************************************/	
	/**
	 * Host name of the server database
	 */
    private String host;
    
    /**
     * Port number of the server database
     */
    private Integer port;
    
    /**
     * User name to connect to this database
     */
    private String username;
    
    /**
     * Password to connect to this database
     */
    private String password;
    
    /**
     * Boolean that indicates if we know the password used when connecting to this database
     */
    private boolean passwordKnown;

    
    
	/****************************************************************************************/
	/* CONSTRUCTORS																			*/		
	/****************************************************************************************/

    /**
     * Constructor of MySQLDatabaseImpl that receive the sql datasource
     * Note: With this constructor we don't know the password used to connect to the database
     * @param dataSource
     */
    public MySQLDatabaseImpl(DataSource dataSource) {
        super(dataSource);
        passwordKnown = false;
    }

    /**
     * Constructor of MySQLDatabaseImpl that receive the full jdbc url to conenct to the database
     * Note: With this constructor we don't know the password used to connect to the database
     * @param jdbcUrl 
     */
    public MySQLDatabaseImpl(String jdbcUrl) {
        super(createDataSourceFromJdbcUrl(jdbcUrl));
        passwordKnown = false;
    }

    /**
     * Constructor of MySQLDatabaseImpl that receive the full jdbc url to conenct to the database
     * Note: With this constructor we know the password used to connect to the database because
     * it comes inside the hash map parameters
     * @param host
     * @param port
     * @param catalog
     * @param parameters hashmap with other parameters like the user and password
     */
    public MySQLDatabaseImpl(String host, Integer port, String catalog, HashMap<String, String> parameters) {
        super(createDataSource(host, port, catalog, parameters));
        this.host = host;
        this.port = port;
        username = parameters.get("user");
        password = parameters.get("password");
        passwordKnown = true;
    }
    
    
	/****************************************************************************************/
	/* PUBLIC METHODS																		*/														
	/****************************************************************************************/
    
	/**
	 * Method responsible to return the host name of the server database
	 * @return string with the host name of the server database
	 * @throws DatabaseException
	 * @throws SQLException
	 */    
    @Override
    public String getHost() throws DatabaseException, SQLException {
        if (host == null) {
            setHostPort();
        }
        return host;
    }

    /**
     * Method responsible to return the port number of the server database
     * @return integer with the port number of the server database
     * @throws DatabaseException
     * @throws SQLException
     */    
    @Override
    public Integer getPort() throws DatabaseException, SQLException {
        if (host == null) {
            setHostPort();
        }
        return port;
    }

    /**
     * Method responsible to return the user name used in order to connect to the database
     * @return string the user name used in order to connect to the database
     * @throws DatabaseException
     * @throws SQLException
     */    
    @Override
    public String getUsername() throws DatabaseException, SQLException {
        if (username == null) {
            setHostPort();
        }
        return username;
    }

    /**
     * Method to return if the database object knows the password used to connect to it 
     * @return boolean with the value true if we know the password to connect to the database and false in the other case 
     */    
    @Override
    public boolean knowsPassword() {
        return passwordKnown;
    }

    /**
     * Method to set the password of the user to connect to the database
     * @param password string with password of the user to connect to the database
     * @throws DatabaseException
     */
    @Override
    public void setPassword(String password) {
        this.password = password;
        passwordKnown = true;
    }

    /***
     * Method to get the password of the user to connect to the database
     * @return string with the password of the user to connect to the database
     * @throws DatabaseException
     */    
    @Override
    public String getPassword() throws DatabaseException {
        if (!passwordKnown) {
            throw new DatabaseException("no password given for database");
        }
        return password;
    }

    /**
     * Method to return the database connection object to connect to this database
     * @return the database connection object to connect to this database
     * @throws DatabaseException
     * @throws SQLException
     */    
    @Override
    public DatabaseConnection getConnection() throws DatabaseException, SQLException {
        return getConnection(null);
    }

    /**
     * Method to return the database connection object to connect to this database specifying its catalog
     * @param catalogName name of the catalog to connect to
     * @return the database connection object to connect to this database
     * @throws DatabaseException
     * @throws SQLException
     */    
    @Override
    public DatabaseConnection getConnection(String catalogName) throws DatabaseException, SQLException {
        Connection conn;
        try {
            conn = ds.getConnection();
        }
        catch (SQLException e) {
            if (ExceptionHelper.causedBy(e, UnknownHostException.class, ConnectException.class)) {
                throw new DatabaseNetworkException(e);
            }
            else {
                throw e;
            }
        }
        if (host == null) {
            setHostPort(conn);
        }
        DatabaseConnection databaseConnection = new MySQLConnectionImpl(this, conn);
        if (catalogName != null) {
            databaseConnection.setCatalog(catalogName);
        }
        return databaseConnection;
    }

    /**
     * Method responsible to create a new database (catalog) in the server database
     * @return a string with the name of the new catalog created
     * @throws SQLException
     */    
    @Override
    public String createCatalog() throws SQLException {  
        Connection conn = ds.getConnection();
        
    	String catalogName = null;
        Statement st = conn.createStatement();
        try {
            int attempts = 3;
            boolean done = false;
            Random random = new Random();
            while (!done) {
                catalogName = getCatalogPrefix() + Integer.toHexString(random.nextInt());
                try {
                    createDatabase(st, catalogName);
                    done = true;
                }
                catch (SQLException e) {
                    if (--attempts <= 0) {
                        throw e;
                    }
                }
            }
        }
        finally {
            st.close();
            conn.close();
        }
        return catalogName;
    }
    
    /**
     * Method to grant access for the given user to the given catalog database 
     * @param catalogName name of the database in which we will grant access to the user userName
     * @param userName name of the user to granted access
     * @throws SQLException
     */    
    @Override
    public void grantAccessToCatalog(String catalogName, String userName) throws SQLException{
    	Connection conn = ds.getConnection();
    	Statement st = null;
    	try{
	    	st = conn.createStatement();
	    	st.execute("grant all privileges on " + catalogName + ".* to '" + userName + "'@'%'");
    	}
	    finally{	    	
    		st.close();
    		conn.close();
	    }
    }      

    /**
     * Method responsible to delete the database received a parameter from the server database
     * @param catalogName name of the database to delete
     * @throws SQLException
     */    
    @Override
    public void deleteCatalog(String catalogName) throws SQLException {
        Connection conn = ds.getConnection();
        try {
            Statement st = conn.createStatement();
            try {
                dropDatabase(st, catalogName);
            }
            finally {
                st.close();
            }
        }
        finally {
            conn.close();
        }
    }

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
    @Override
    public Task createTransferTask(String sourceCatalog, String targetCatalog, List<String> tableNames)
            throws IOException, DatabaseException {
        return new MySQLTransferTask(this, sourceCatalog, this, targetCatalog, tableNames);
    }
    
    
    
	/****************************************************************************************/
	/* PRIVATE METHODS																		*/														
	/****************************************************************************************/	
    
    /**
     * Method responsible to create a datasource from the jdbc url received as parameter 
     * @param jdbcUrl jdbc url to obtain its datasource
     * @return datasource for the jdbc url
     */
    private static DataSource createDataSourceFromJdbcUrl(String jdbcUrl) {
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setDriverClassName("com.mysql.jdbc.Driver");
        dataSource.setUrl(jdbcUrl);  
        return dataSource;
    }    

    /**
     * Method responsible to create a datasource from the values of the parameters received
     * @param host host name of the server database
     * @param port port number of the server database
     * @param catalog name of the catalog database
     * @param parameters hash map with other parameters like the user name and password
     * @return datasource
     */
    private static DataSource createDataSource(String host, Integer port, String catalog, HashMap<String,String> parameters) {
        BasicDataSource ds = new BasicDataSource();
        String driverClassName = "com.mysql.jdbc.Driver";
        ds.setDriverClassName(driverClassName);
        StringBuilder url = new StringBuilder("jdbc:mysql://");
        url.append(host);
        if (port != null) {
            url.append(':').append(port);
        }
        url.append('/');
        if (catalog != null) {
            url.append(catalog);
        }
        if (parameters != null) {
            boolean first = true;
            for (Map.Entry<String, String> entry : parameters.entrySet()) {
                url.append(first ? '?' : '&');
                first = false;
                url.append(entry.getKey()).append('=').append(entry.getValue());
            }
        }
        ds.setUrl(url.toString());
        return ds;
    }

    /**
     * Method responsible to set the host and port attributes of this class from the properties
     * of the datasource 
     * @throws DatabaseException
     * @throws SQLException
     */
    private void setHostPort() throws DatabaseException, SQLException {
        Connection conn = ds.getConnection();
        try {
            setHostPort(conn);
        }
        finally {
            conn.close();
        }
    }

    /**
     * Method responsible to set the host and port attributes of this class from the properties
     * of the sql connection received as parameter
     * @param conn sql connection
     * @throws DatabaseException
     * @throws SQLException
     */
    private void setHostPort(Connection conn) throws DatabaseException, SQLException {
        DatabaseMetaData metadata = conn.getMetaData();
        String url = metadata.getURL();
        URI uri;
        try {
            uri = new URI(url.substring(url.indexOf("mysql:")));
        } catch (URISyntaxException e) {
            throw new DatabaseException(e);
        }
        host = uri.getHost();
        port = uri.getPort();
        String username = metadata.getUserName();
        // username returned is in form: user@host
        int atSign = username.indexOf('@');
        if (atSign < 0) {
            this.username = username;
        }
        else {
            this.username = username.substring(0, atSign);
        }
    }      

    /**
     * Method responsible to execute the sql command to create a new catalog (database)
     * @param st sql statement in which we will execute the sql command
     * @param catalogName name of the catalog to create
     * @throws SQLException
     */
    private static void createDatabase(Statement st, String catalogName) throws SQLException {
        st.execute("create database " + catalogName);       
    }    
    
    /**
     * Method responsible to execute the sql command to delete the catalog received as parameter
     * @param st sql statement in which we will execute the sql command 
     * @param catalogName name of the catalog to be deleted
     * @throws SQLException
     */
    private static void dropDatabase(Statement st, String catalogName) throws SQLException {
        st.execute("delete from mysql.db where Db = '" + catalogName + "'");
        st.execute("flush privileges");
        st.execute("drop database if exists " + catalogName);
    }    
}
