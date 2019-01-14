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
import java.util.List;

import java.sql.SQLException;

import org.openbio.xmap.common.dao.util.database.Database;
import org.openbio.xmap.common.dao.util.database.DatabaseException;
import org.openbio.xmap.common.utils.misc.ExceptionHelper;
import org.openbio.xmap.common.utils.task.TaskException;
import org.openbio.xmap.common.utils.task.command.CommandTask;

import static org.openbio.xmap.common.utils.misc.FileUtil.ensureFilePrivate;

/**
 * Class that implement the interface CommandTask to execute the transfer of data between 2 MySQL databases 
 * <p>
 * OpenBio XMap
 * <p>
 * Date: 15/05/12
 * @author scmjpg
 */
public class MySQLTransferTask extends CommandTask {

	/****************************************************************************************/
	/* ATTRIBUTES																			*/														
	/****************************************************************************************/
	
	/**
	 * Config file in which we will write the instruction for the mysql dump
	 */
    private File configFile;
    
    /**
     * Number of tables that have been created 
     */
    private int createdTables;
    
    /**
     * Number of tables to be created (= number of tables in the source database)
     */
    private int maxTables;

        
    
	/****************************************************************************************/
	/* CONSTRUCTORS																			*/		
	/****************************************************************************************/    
    
    /**
     * Constructor of MySQLTransferTask that receives the mysql database and its catalog name that 
     * will act as a source database. It also receives a second mysql database and its catalog name
     * that will act as a target database.
     * <p>
     * Here we will set the command to be executed in order to perform the task. In this case the command
     * is a mysqldump that received a config file as parameter.
     * 
     * @param sourceDatabase mysql database that will act as a source database
     * @param sourceCatalog name of the catalog to be used as source
     * @param targetDatabase mysql database that will act as a target database
     * @param targetCatalog name of the catalog to be used as target
     * @param tableNames list with the name of the tables to transfer (copy) its contentent between source and target
     * @throws IOException
     * @throws DatabaseException
     */
    public MySQLTransferTask(MySQLDatabaseImpl sourceDatabase, String sourceCatalog,
                             MySQLDatabaseImpl targetDatabase, String targetCatalog,
                             List<String> tableNames) throws IOException, DatabaseException {
        setCommand(createCommand(sourceDatabase, sourceCatalog, targetDatabase, targetCatalog, tableNames));
        createdTables = 0;
        // add one to maxTables because the CREATE TABLE commands have work performed before and
        // after, so they are intervals rather than endpoints. For example, after the last table
        // is created, we still need to populate it with the transferred data rows.
        maxTables = tableNames.size() + 1;
    }

    

	/****************************************************************************************/
	/* PUBLIC METHODS																		*/														
	/****************************************************************************************/
    
    /**
     * Method responsible when the transfer task has finished to do a clean up, deleting the config
     * file with the mysql dump instructions   
     */
    @Override
    public void cleanup() {
        while (!isFinished()) {
            try {
                waitForFinished();
            }
            catch (InterruptedException e) {
                ExceptionHelper.ignore(e);
            }
        }
        deleteConfigFileQuietly();
    }

    /**
     * Method responsible to perform the action of the transfer task
     * It will call its super class (CommandTask) to execute the mysqldump command
     * that we have define in the constructor of this class
     * @throws Exception
     */
    @Override
    public void perform() throws TaskException {
        setActive(0, maxTables);
        super.perform();
    }

    /**
     * Method to add a new line to the output
     */
    @Override
    public void addOutputLine(String line) {
        super.addOutputLine(line);
        if (line.startsWith("CREATE TABLE ")) {
            ++createdTables;
            if (createdTables < maxTables) {
                updateActive(createdTables);
            }
        }
    }
    
    
	/****************************************************************************************/
	/* PRIVATE METHODS																		*/														
	/****************************************************************************************/	    

    /**
     * Method responsible to write in the config file received as parameter the instruction
     * to execute mysqldump in order to transfer the data from the source daabase to the target database
     * @param configFile config file with the instruction of the mysqldump
     * @param sourceDatabase source database from which copy its data
     * @param targetDatabase target database to which copy the data
     * @throws IOException
     * @throws SQLException
     * @throws DatabaseException
     */
    private static void writeConfigFile(File configFile, Database sourceDatabase,
                                        Database targetDatabase)
            throws IOException, SQLException, DatabaseException {
        PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(configFile)));
        try {
            out.println("[mysqldump]");
            out.println("host=" + sourceDatabase.getHost());
            Integer port;
            port = sourceDatabase.getPort();
            if (port != null) {
                out.println("port=" + sourceDatabase.getPort());
            }
            out.println("user=" + sourceDatabase.getUsername());
            String password;
            password = sourceDatabase.getPassword();
            if (password != null) {
                out.println("password=" + password);
            }
            out.println("single-transaction");
            out.println("quick");
            out.println();
            out.println("[mysql]");
            out.println("host=" + targetDatabase.getHost());
            port = targetDatabase.getPort();
            if (port != null) {
                out.println("port=" + port);
            }
            out.println("user=" + targetDatabase.getUsername());
            password = targetDatabase.getPassword();
            if (password != null) {
                out.println("password=" + password);
            }
        }
        finally {
            out.close();
        }
    }

    /**
     * Method responsible to detelte the config file used for the mysqldump commands
     */
    private void deleteConfigFileQuietly() {
        if (configFile != null) {
            try {
                configFile.delete();
            }
            catch (Exception e) {
                ExceptionHelper.ignore(e);
            }
        }
        configFile = null;
    }

    /**
     * Method responsible create the command to execute the mysqldump
     * @param sourceDatabase source database 
     * @param sourceCatalog name of the source catalog
     * @param targetDatabase target database 
     * @param targetCatalog name of the target catalog
     * @param tableNames list with the name of the tables to trasfer its content
     * @return string with the command to execute the mysqldump
     * @throws DatabaseException
     * @throws IOException
     */
    private String createCommand(Database sourceDatabase, String sourceCatalog,
                                        Database targetDatabase, String targetCatalog,
                                        List<String> tableNames) throws DatabaseException, IOException {
        configFile = File.createTempFile("tmp", null);
        try {
            String configFilePath = configFile.getAbsolutePath();
            ensureFilePrivate(configFilePath);
            writeConfigFile(configFile, sourceDatabase, targetDatabase);
            StringBuilder command = new StringBuilder("mysqldump --defaults-file=");
            command.append(configFilePath);
            command.append(' ');
            command.append(sourceCatalog);
            for (String tableName : tableNames) {
                command.append(' ');
                command.append(tableName);
            }
            command.append(" | mysql --defaults-file=");
            command.append(configFilePath);
            command.append(" --verbose ");
            command.append(targetCatalog);
            return command.toString();
        }
        catch (DatabaseException e) {
            deleteConfigFileQuietly();
            throw e;
        }
        catch (Exception e) {
            deleteConfigFileQuietly();
            throw new DatabaseException(e);
        }
    }    
}
