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

import java.io.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import org.openbio.xmap.common.dao.util.database.Database;
import org.openbio.xmap.common.dao.util.database.DatabaseException;
import org.openbio.xmap.common.dao.util.database.impl.DatabaseConnectionImpl;
import org.openbio.xmap.common.utils.task.Task;
import org.openbio.xmap.common.utils.task.command.CommandTask;

import static org.openbio.xmap.common.utils.misc.FileUtil.ensureFilePrivate;

/**
 * Class that implement the interface DatabaseConnection for MySQL databases 
 * <p>
 * OpenBio XMap 
 * <p>
 * Date: 15/05/12
 * @author scmjpg
 */
public class MySQLConnectionImpl extends DatabaseConnectionImpl {

	/****************************************************************************************/
	/* CONSTRUCTORS																			*/		
	/****************************************************************************************/
	/**
	 * Constructor of MySQLConnectionImpl that receives the mysql database and sql connection
	 * @param database
	 * @param conn
	 */
    MySQLConnectionImpl(MySQLDatabaseImpl database, Connection conn) {
        super(database, conn);
    }

	/****************************************************************************************/
	/* PUBLIC METHODS																		*/														
	/****************************************************************************************/    
    
    /**
     * Method to execute the sql commands present in the input stream received as a parameter
     * @param in input stream with the sql command to be executed in the database
     * @throws Exception
     * @throws SQLException
     * @throws DatabaseException
     */    
    @Override
    public void executeStream(InputStream in) throws Exception {
        // both solutions take 5-6 seconds to load the Base Schema.
        if (true) {
            executeStatements(in, 0);
        }
        else {
            File scriptFile = File.createTempFile("tmp", null);
            try {
                String configFilePath = scriptFile.getAbsolutePath();
                ensureFilePrivate(configFilePath);
                OutputStream out = new FileOutputStream(scriptFile);
                try {
                    byte[] buffer = new byte[1024];
                    int n;
                    while ((n = in.read(buffer)) > 0) {
                        out.write(buffer, 0, n);
                    }
                }
                finally {
                    out.close();
                }
                executeFile(scriptFile);
            }
            finally {
                scriptFile.delete();
            }
        }
    }

    /**
     * Method responsible to execute the sql commands present in the the file recieved as parameter
     * @param file file with the sql commands to be executed
     * @throws Exception
     */
    public void executeFile(File file) throws Exception {
        File configFile = File.createTempFile("tmp", null);
        try {
            String configFilePath = configFile.getAbsolutePath();
            ensureFilePrivate(configFilePath);
            writeConfigFile(configFile, database);
            StringBuilder command = new StringBuilder("mysql --defaults-file=");
            command.append(configFilePath);
            command.append(' ');
            command.append(getCatalog());
            command.append(" < ");
            command.append(file.getAbsolutePath());
            Task task = new CommandTask(command.toString());
            task.run();
            task.waitForResult();
           }
        finally {
            configFile.delete();
        }
    }

    /**
     * Method responsible to delete all the tables presented in the database
     * @throws SQLException
     */    
    @Override
    public void deleteAll() throws SQLException {
        Statement st = conn.createStatement();
        try {
            disableForeignKeyChecks(st);
            try {
                deleteAll(st);
            }
            finally {
                enableForeignKeyChecks(st);
            }
        }
        finally {
            st.close();
        }
    }

    
	/****************************************************************************************/
	/* PROTECTED METHODS																	*/														
	/****************************************************************************************/	    
    
    /**
     * Method responsible to enable the check of foreign keys
     * @param st
     * @throws SQLException
     */
    protected static void enableForeignKeyChecks(Statement st) throws SQLException {
        st.execute("set foreign_key_checks = 1");
    }

    /**
     * Method responsible to disable the check of foreign keys
     * @param st
     * @throws SQLException
     */    
    protected static void disableForeignKeyChecks(Statement st) throws SQLException {
        st.execute("set foreign_key_checks = 0");
    }

    
    
	/****************************************************************************************/
	/* PRIVATE METHODS																		*/														
	/****************************************************************************************/	    
    
    /**
     * Method responsible to write in the config file received as parameter the connection properties
     * of the database received as second parameter 
     * @param configFile config file in which write the connection properties of the database
     * @param database database to write its connection properties in the config file
     * @throws IOException
     * @throws SQLException
     * @throws DatabaseException
     */
    private void writeConfigFile(File configFile, Database database)
            throws IOException, SQLException, DatabaseException {
        PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(configFile)));
        try {
            out.println("[client]");
            out.println("host=" + database.getHost());
            Integer port = database.getPort();
            if (port != null) {
                out.println("port=" + port);
            }
            out.println("user=" + database.getUsername());
            String password = database.getPassword();
            if (password != null) {
                out.println("password=" + password);
            }
        }
        finally {
            out.close();
        }
    }

    /**
     * Method responsible to execute the sql commands that come in the input stream
     * @param in input stream with the sql commands
     * @param lineNo number of line read
     * @throws IOException
     * @throws SQLException
     */
    private void executeStatements(InputStream in, int lineNo) throws IOException, SQLException {
        Statement st = createStatement();
        try {
            executeStatements(st, in, lineNo);
        }
        finally {
            st.close();
        }
    }

    /**
     * Method responsible to execute the sql commands that come in the input stream
     * @param st sql statement to use for executing the sql commands
     * @param in input stream with the sql commands
     * @param lineNo number of line read
     * @throws IOException
     * @throws SQLException
     */
    private static void executeStatements(Statement st, InputStream in, int lineNo) throws IOException, SQLException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
        StringBuilder sb = new StringBuilder();
        String buf = reader.readLine();
        while (buf != null) {
            lineNo += 1;
            String trimmed = buf.trim();
            sb.append(trimmed);
            int length = sb.length();
            if (length > 0 && sb.charAt(length - 1) == ';') {
                st.execute(sb.toString());
                sb = new StringBuilder();
            }
            else {
                sb.append('\n');
            }
            buf = reader.readLine();
        }
    }    

}
