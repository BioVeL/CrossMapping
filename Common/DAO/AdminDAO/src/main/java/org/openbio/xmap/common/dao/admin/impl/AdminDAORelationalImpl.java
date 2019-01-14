/*
 * #%L
 * XMap Admin DAO
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
package org.openbio.xmap.common.dao.admin.impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.openbio.xmap.common.dao.admin.AdminDAO;
import org.openbio.xmap.common.dao.util.exception.DAOConnectionException;
import org.openbio.xmap.common.dao.util.exception.DAOException;
import org.openbio.xmap.common.dao.util.exception.DAOReadException;
import org.openbio.xmap.common.dao.util.exception.DAOWriteException;
import org.openbio.xmap.common.dao.util.helper.RelationalHelper;
import org.openbio.xmap.common.serviceinterfaces.types.xmap.TaskId;
import org.openbio.xmap.common.serviceinterfaces.types.xmap.TaskProgress;
import org.openbio.xmap.common.serviceinterfaces.types.xmap.TaskResponse;
import org.openbio.xmap.common.serviceinterfaces.types.xmap.TaskStatus;
import org.openbio.xmap.common.utils.task.Task;


public abstract class AdminDAORelationalImpl implements AdminDAO {	
	
	/****************************************************************************************/
	/* ATTRIBUTES																			*/														
	/****************************************************************************************/	
	
	/**
	 * Datasource to connect to the database
	*/	
	protected DataSource ds;
	
	/**
	 * Relational helper to help with the sql commands
	 */		
	protected RelationalHelper helper;
	
	
	private static boolean dbCreated = false;
	private static final Object lock = new Object();

	
	/****************************************************************************************/
	/* CONSTRUCTORS																			*/		
	/****************************************************************************************/	
	
	/**
	 * Constructor of AdminDAORelationalImpl that receives the datasource to connect with the database and an
	 * object of a class that implements the interface RelationalHelper. 
	 * @param ds datasource to connect to the database
	 * @param helper implementation of the interface RelationalHelper to help with the sql commands
	 * @throws DAOException
	 */		
	public AdminDAORelationalImpl(DataSource ds,RelationalHelper helper) throws DAOException{
		this.ds = ds;
		this.helper = helper;
		
		synchronized (lock){ 	
			if (!dbCreated){
				startUpDatabase();
				dbCreated=true;
			}
		}
	}	
		
	
	
	/****************************************************************************************/
	/* PUBLIC METHODS																		*/														
	/****************************************************************************************/			
	@Override
	public void verifyUserExistence(String user, String scope) throws DAOException{
		Connection conn = getConnection();
		PreparedStatement st=null;
		try{			
			//If the user/scope doen't exist create a new entry with password=changeme
			String sql = "INSERT IGNORE INTO User (name,scope,password,role) VALUES (?,?,?,?)";
			st = helper.createGeneratedKeysPreparedStatement(conn,sql);
			st.setString(1, user);
			st.setString(2, scope);
			
			String unecryptedPassword = "changeme";
			String salt = user.substring(0, 2);       
			MessageDigest messageDigest=null;
			messageDigest = MessageDigest.getInstance("MD5");
			messageDigest.update((salt+unecryptedPassword).getBytes());
			String encryptedPassword = (new BigInteger(messageDigest.digest())).toString(16);			
			st.setString(3, encryptedPassword);
			
			st.setString(4, "WS");				
			st.executeUpdate();			
		}
		catch (Exception ex){
			throw new DAOWriteException(ex);	
		}	
		finally{
			closeStatementWithoutException(st);
			closeConnectionWithoutException(conn);
		}
	}
	
	@Override
	public List<String> getUsers() throws DAOException  { 
		List<String> users = new ArrayList<String>();
		Connection conn = getConnection();	
		PreparedStatement st = null;
		try{
			String sql = "SELECT name,password,role FROM User";
			st = getPreparedStatement(sql,conn,true);

			ResultSet rs = st.executeQuery();
            while (rs.next()){
    			users.add(rs.getString("name"));		            	
            }    
            rs.close();
            
            return users;
		}
		catch (SQLException ex){
			throw new DAOReadException(ex);	
		}	
		finally{
			closeStatementWithoutException(st);
			closeConnectionWithoutException(conn);
		}
	}

	/** 
	 * Method to register the exception that its information is received as parameter 
	 * @param exceptionId pseudoId of the exception. This id will be show to the user to then later can be utilized
	 * to track down the exception recovering it from the Admin database 
	 * @param msg Message of the exception
	 * @param stackTrace stack trace of the exception. Including the stack trace of the initial exception of that was 
	 * wrapper in other exception
	 * @param className full class name of the class where the exception was captured and processed
	 * @param parameters List of the parameters (names and its values) with which the method that provoked the exception 
	 * was invoked
	 * @throws DAOException if there was a problem executing this method
	 */
	public void registerException(String exceptionId, String msg, String stackTrace, String className, 
			List<String> parameters, String user, String scope) throws DAOException{
		Connection conn = getConnection();
		PreparedStatement st=null;
		try{			
			String sql = "INSERT INTO Exception (id,description,className,detail,parameters,date,user,scope)" +
					" VALUES (?,?,?,?,?,?,?,?)";
			st = helper.createGeneratedKeysPreparedStatement(conn,sql);
			st.setString(1, exceptionId);
			st.setString(2, (msg!=null?msg:""));
			st.setString(3, className);
			st.setString(4, stackTrace);	
			st.setString(5, parameters.toString());
			st.setTimestamp(6, new java.sql.Timestamp(new java.util.Date().getTime()));		
			st.setString(7, user);	
			st.setString(8, scope);	
			st.executeUpdate();				
		}
		catch (SQLException ex){
			throw new DAOReadException(ex);	
		}	
		finally{
			closeStatementWithoutException(st);
			closeConnectionWithoutException(conn);			
		}		
	}
	
	
	/**
	 * Method responsible to log the execution time of the method received as parameter
	 * @param methodName name of the method
	 * @param className class in which this method is
	 * @param parameters List of the parameters (names and its values) with which the method was invoked
	 * @param elapsedTime number of milliseconds that the method has taken to execute
	 * @param logLevel level of log of this message. If this level is great or equals to the one
	 * define in the database, this message will be register.
	 * @throws DAOException
	 */		
	public void registerMethodExecutionTime(String methodName, String className, List<String> parameters, 
			long elapsedTime, byte logLevel, String user, String scope) throws DAOException{
		
		Connection conn = getConnection();
		PreparedStatement stMinTimeLog=null;
		PreparedStatement stRegisterMethodTime=null;
		try{										
			//Get the current application log level
			String sqlMinTimeToLog = "SELECT minTimeToLog from LogLevel";
			stMinTimeLog = helper.createGeneratedKeysPreparedStatement(conn,sqlMinTimeToLog);
			ResultSet rsMinTimeLog = stMinTimeLog.executeQuery();
            
			if (rsMinTimeLog.next()) {
            	long minTimeLog = rsMinTimeLog.getLong("minTimeToLog");
            
            	//If the minTime is non negative and the method execution time is bigger that it we must register the time
            	if (minTimeLog >= 0 && elapsedTime>=minTimeLog){					
					String sqlRegisterMethodTime = "INSERT INTO LogMethodExecutionTime" +
							" (methodName,className,parameters,elapsedTime,date,user,scope) VALUES (?,?,?,?,?,?,?)";
					stRegisterMethodTime = helper.createGeneratedKeysPreparedStatement(conn,sqlRegisterMethodTime);
					stRegisterMethodTime.setString(1, methodName);	
					stRegisterMethodTime.setString(2, className);
					stRegisterMethodTime.setString(3, parameters.toString());
					stRegisterMethodTime.setFloat(4, elapsedTime);			
					stRegisterMethodTime.setTimestamp(5, new java.sql.Timestamp(new java.util.Date().getTime()));
					stRegisterMethodTime.setString(6, user);	
					stRegisterMethodTime.setString(7, scope);	
					stRegisterMethodTime.executeUpdate();		
            	}
            }
			rsMinTimeLog.close();
		}
		catch (SQLException ex){
			throw new DAOWriteException(ex);	
		}	
		finally{			
			closeStatementWithoutException(stRegisterMethodTime);
			closeStatementWithoutException(stMinTimeLog);
			closeConnectionWithoutException(conn);
		}	
	}			
	
	
	@Override
	public List<TaskId> getActiveTasks(String user, String scope) throws DAOException { 
		List<TaskId> activeTasks = new ArrayList<TaskId>();
		Connection conn = getConnection();		
		PreparedStatement st = null;
		try{
			String sql = "SELECT id FROM Task where finishDate=-1 and user=? and scope=?";
			st = getPreparedStatement(sql,conn,true);
			st.setString(1, user);	
			st.setString(2, scope);	

			ResultSet rs = st.executeQuery();
            while (rs.next()){ 
            	activeTasks.add(new TaskId(rs.getString("id")));		            	
            }   
            rs.close();
            
            return activeTasks;
		}
		catch (SQLException ex){
			throw new DAOReadException(ex);	
		}	
		finally{
			closeStatementWithoutException(st);
			closeConnectionWithoutException(conn);
		}
	}	
	
	@Override
	public void updateTaskProgress(Task task) throws DAOException{
		Connection conn = getConnection();		
		try{	
			String details = "";		
	        switch (task.getProgress().getState()) {
	            case PENDING:
	            case ACTIVE:
	            case CANCELLING:
	            case COMPLETED:
	                details = task.getProgress().getPhase();
	                break;
	            case FAILED:
					if (task.getProgress().getException() != null){
						details = "Error executing task.";
						if (task.getExceptionId()!=null){
							details += " ExceptionId:" + task.getExceptionId();
						}
						details += " Description: " + task.getProgress().getException().getUserData();
		            }
		            else{
		            	details = "fail";
		            }
	                break;
	        }						
			
			String sqlExistsTask = "SELECT id FROM Task where id=?";			
			PreparedStatement stExistsTask = getPreparedStatement(sqlExistsTask,conn,false);
			stExistsTask.setString(1, task.getTaskId().getValue());				
			ResultSet rsExistsTask = stExistsTask.executeQuery();
			if (!rsExistsTask.next()){
            	//Non exists
            	String sqlInsert = "INSERT INTO Task (id,name,type,status,details,percentage,startDate,finishDate,taskResponse,user,scope) VALUES (?,?,?,?,?,?,?,?,?,?,?)";
            	PreparedStatement st = helper.createGeneratedKeysPreparedStatement(conn,sqlInsert);
    			st.setString(1, task.getTaskId().getValue());
    			st.setString(2, task.getTaskName());
    			st.setString(3, task.getTaskType());
    			st.setString(4, task.getProgress().getState().value());
    			st.setString(5, details);
    			st.setFloat(6, task.getProgress().getPercentProgress());	
    			st.setTimestamp(7,new Timestamp(task.getProgress().getStartDate()));
    			if(task.getProgress().getFinishDate()!=-1){
    				st.setTimestamp(8,new Timestamp(task.getProgress().getFinishDate()));
    			}
    			else{
    				st.setTimestamp(8,null);
    			}
    			
    			if (task.getProgress().getResult()!=null){
    				st.setObject(9,task.getProgress().getResult());
    			}
    			else{
    				st.setObject(9,null);
    			}					
    			st.setString(10, task.getUser());
    			st.setString(11, task.getScope());
    			st.executeUpdate();
    			st.close();					
            }
            else{
            	//Exists
				String sqlUpdate = "update Task set status=?,details=?,percentage=?,finishDate=?,taskResponse=? where id = ?";
				PreparedStatement st = helper.createGeneratedKeysPreparedStatement(conn,sqlUpdate);    			
    			st.setString(1, task.getProgress().getState().value());
    			st.setString(2, details);
    			st.setFloat(3, task.getProgress().getPercentProgress());	
    			if(task.getProgress().getFinishDate()!=-1){
    				st.setTimestamp(4,new Timestamp(task.getProgress().getFinishDate()));
    			}
    			else{
    				st.setTimestamp(4,null);
    			}
    			
    			if (task.getProgress().getResult()!=null){
    				st.setObject(5,task.getProgress().getResult());
    			}
    			else{
    				st.setObject(5,null);
    			}					
    			st.setString(6, task.getTaskId().getValue());
    			st.executeUpdate();	
    			st.close();
            }		
			rsExistsTask.close();
			stExistsTask.close();        				
		}
		catch (SQLException ex){
			throw new DAOReadException(ex);	
		}	
		finally{			
			closeConnectionWithoutException(conn);			
		}		
	}	
	
	@Override
	public void cancelTask(TaskId taskId) throws DAOException{
		Connection conn = getConnection();
		PreparedStatement st=null;		
		try{
			String sql = "update Task set status = ?, finishDate=? where id = ?";
			st = helper.createGeneratedKeysPreparedStatement(conn,sql);
			st.setString(1, TaskStatus.FAILED.value());
			st.setTimestamp(2, new java.sql.Timestamp(new java.util.Date().getTime()));
			st.setString(3, taskId.getValue());
			st.executeUpdate();							
		}
		catch (Exception ex){
			throw new DAOReadException(ex);	
		}	
		finally{
			closeStatementWithoutException(st);
			closeConnectionWithoutException(conn);
		}	
	}
	
	
	@Override
	public TaskProgress getTaskProgress(TaskId taskId) throws DAOException{		
		Connection conn = getConnection();
		PreparedStatement st=null;
		TaskProgress taskProgresss = null;
		try{
			String sql = "SELECT id,name,type,status,details,percentage,startDate,finishDate,taskResponse,user,scope FROM Task where id=?";
			
			st = helper.createGeneratedKeysPreparedStatement(conn,sql);
			st.setString(1, taskId.getValue());
			ResultSet rs = st.executeQuery();
            if (!rs.next()){
            	rs.close();
            	throw new RuntimeException("Task not found in the database");            	   	
            }          
            else{
            	taskProgresss = new TaskProgress();
            	taskProgresss.setTaskName(rs.getString("name"));
            	taskProgresss.setType(rs.getString("type"));
            	taskProgresss.setStatus(TaskStatus.fromValue(rs.getString("status")));
            	taskProgresss.setDetails(rs.getString("details"));
            	taskProgresss.setPercentage((int)rs.getFloat("percentage"));
            	taskProgresss.setStartDate(rs.getTimestamp("startDate").getTime());
            	
            	Timestamp finishDate = rs.getTimestamp("finishDate");
            	if (!rs.wasNull()){
            		taskProgresss.setFinishDate(finishDate.getTime());
                	
            	}
            	else{
            		taskProgresss.setFinishDate(-1);
            	}
            	
            	//TaskResponse taskResponse = (rs.getObject("taskResponse")!=null?(TaskResponse)rs.getObject("taskResponse"):null);
            	byte[] buf = rs.getBytes("taskResponse");
                ObjectInputStream objectIn = null;
                if (buf != null){
                  objectIn = new ObjectInputStream(new ByteArrayInputStream(buf));
             
                  Object deSerializedObject = objectIn.readObject();            	
                  TaskResponse taskResponse = (TaskResponse) deSerializedObject;
                  taskProgresss.setTaskResponse(taskResponse);
                }            	                       	
            	
            	taskProgresss.setUser(rs.getString("user"));
            	taskProgresss.setScope(rs.getString("scope"));
            	
            	rs.close();
            }
            
            return taskProgresss;
		}
		catch (Exception ex){
			throw new DAOReadException(ex);	
		}	
		finally{
			closeStatementWithoutException(st);
			closeConnectionWithoutException(conn);
		}		
	}
	
	
	/****************************************************************************************/
	/* PROTECTED METHODS																	*/														
	/****************************************************************************************/		
		
	/**
	 * Abstract method that must be implemented in the children classes and it will return the specific prepare statement
	 * with the values passed as parameters, depending on the type of database.
	 * @param sql sql command for which we create the prepare statement 
	 * @param conn sql connection
	 * @param returnKey boolean that indicate if the prepare statement should or not return the auto generated key after execute
	 * the sql command
	 * @return a PreparedStatement object
	 * @throws SQLException
	 */		
	protected abstract PreparedStatement getPreparedStatement(String sql, Connection conn, boolean returnKey) throws SQLException;	
	
	/**
	 * Abstract method  to must be implemented in the children classes and it will read the schema db from a file and 
	 * return an object inputStream to read it 
	 * @return input stream with the sql commands to create the tables needed
	 */		
	protected abstract InputStream openSchemaStream();	
	
	
	/**
	 * Abstract method that must be implemented in the children classes and it will read the sql insert commands from a file and 
	 * return an object inputStream to read it 
	 * @return input stream with the sql commands to insert the initial data for the log tables
	 */	
	protected abstract InputStream openInitialDataStream();	
	
	/**
	 * Method to obtain the sql connection from the datasource
	 * @return the sql connection object
	 * @throws DAOException if it couldn't get the connection to the db
	 */	
	protected Connection getConnection() throws DAOException {
	    try{
	        return ds.getConnection();
	    }
	    catch (SQLException e) {
	        throw new DAOConnectionException(e);
	    }
	}
	
	/**
	 * Method to close the sql connection received as parameter 
	 * @param conn sql connection
	 * @throws DAOException if it couldn't close the connection
	 */    
	protected void closeConnection(Connection conn) throws DAOException {
	    try {
	        if (conn != null) {
	            conn.close();
	        }
	    }
	    catch (SQLException e)
	    {
	        throw new DAOConnectionException(e);
	    }
	}
	
	/**
	 * Method to close silently the sql connection received as parameter.
	 * Note: This method don't rise an exception if the connection couldn't be closed 
	 * @param conn sql connection.
	 */    
	protected void closeConnectionWithoutException(Connection conn) {
	    try {
	        closeConnection(conn);
	    }
	    catch (DAOException e)
	    {
	    	e.printStackTrace();
	        System.err.println("[IGNORED]");
	    }
	}
	
	protected void closeStatementWithoutException(PreparedStatement st) {
	    try {
	        if (st!=null && !st.isClosed()){
	        	st.close();
	        }
	    }
	    catch (SQLException e) {
	    	e.printStackTrace();
	        System.err.println("[IGNORED]");
	    }
	}		
	
	
	/**
	 * Method that will start up the databases, detecting if the required tables for admin the xmap  
	 * already exists and if don't, read the schema db from a file and create its tables.
	 * @throws DAOException if there is a problem executing this method
	 */      
	protected synchronized void startUpDatabase() throws DAOException {
		Connection conn = getConnection();		
		try{		
			DatabaseMetaData metadata = conn.getMetaData();
	        ResultSet rsTables = metadata.getTables(null, null, "User", null);
			if (!rsTables.next()){
				InputStream isSchema = openSchemaStream();
		        if (isSchema == null)
		            throw new DAOReadException("schema not present");	
				InputStream isData = openInitialDataStream();
		        if (isData == null)
		            throw new DAOReadException("initial data not present");	
				try{ 			
					helper.executeStatements(conn, isSchema);
					helper.executeStatements(conn, isData);							
				}
				finally{
		            try {
		            	isSchema.close();
		            	isData.close();	
		            } 
		            catch (IOException e) {
		            	e.printStackTrace();
		                System.err.println("[IGNORED]");
		            }									
				}	
			}			
		}
	    catch (SQLException e) {
	        throw new DAOWriteException(e);
	    }			
		finally{
			closeConnection(conn);
		}
	}		
	
}
