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

import org.openbio.xmap.common.dao.util.database.Transaction;
import org.openbio.xmap.common.utils.misc.ExceptionHelper;

import java.sql.Connection;
import java.sql.SQLException;


/**
 * Class that implements the interface Transaction
 * <p>
 * This class supports nested transactions. In effect, each nested transaction does nothing. Only
 * the outermost transaction controls whether a set of work is committed or not.
 * <p>
 * This allows library methods that may require multiple statements, and hence may put the database
 * temporarily in an inconsistent state, to wrap the code in a transaction to ensure only a consistent
 * view is visible to other clients.
 * <p>
 * Normally, putting transactions in a library causes problems since the client may use a transaction
 * to perform several operations more efficiently, and nesting transactions is not supported very well.
 * <p>
 * The ugly solution is to provide library calls that require being called inside a transaction, but
 * this is error-prone. Providing this nesting transaction abstraction allows both parties to use
 * a consistent pattern.
 * <p>
 * Also, we get rid of the confusing issue of when rollback gets called. Generally, use the idiom
 * <pre>
 * {@code 
 * 	Transaction t = new TransactionImpl(conn);
 * 	try {
 *  	... multiple JDBC calls ...
 *     	t.commit();
 * 	}
 * 	finally {
 *     t.close();
 * 	}
 * }
 * </pre>
 * <p>
 * OpenBio XMap
 * <p>
 * Date: 15/05/12
 * @author scmjpg
 */
public class TransactionImpl implements Transaction {

	/****************************************************************************************/
	/* ATTRIBUTES																			*/														
	/****************************************************************************************/
	/**
	 * SQL connection
	 */
    private final Connection conn;
    
    /**
     * boolean to indicate if it is the most outer transaction
     */
    boolean isOutermostTransaction;

    
	/****************************************************************************************/
	/* CONSTRUCTORS																			*/		
	/****************************************************************************************/
    /**
     * Construction of TransactionImpl that receive the sql connection in which create the transaction
     * @param conn sql conneciton
     * @throws SQLException
     */
    TransactionImpl(Connection conn) throws SQLException {
        this.conn = conn;
        isOutermostTransaction = conn.getAutoCommit();
        if (isOutermostTransaction) {
            conn.setAutoCommit(false);
        }
    }

	/****************************************************************************************/
	/* PUBLIC METHODS																		*/														
	/****************************************************************************************/
    
	/**
	 * Method responsible to commit the transaction only if it is the most outer transaction
	 * @throws SQLException
	 */
    @Override
    public void commit() throws SQLException {
        if (isOutermostTransaction) {
            conn.commit();
        }
    }

	/**
	 * Method responsible to close the transaction only if it is the most outer transaction
	 * and if it that the case, rollback the transaction 
	 * @throws SQLException
	 */
    @Override
    public void close() throws SQLException {
        if (isOutermostTransaction) {
            try {
                conn.rollback();
            }
            catch (Exception e) {
                ExceptionHelper.ignore(e);
            }
            conn.setAutoCommit(true);
        }
    }
}
