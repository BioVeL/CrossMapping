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
package org.openbio.xmap.common.dao.util.database;

import java.sql.SQLException;

/**
 * Interface that define the method of a transaction in the database
 * <p>
 * OpenBio XMap
 * <p>
 * Date: 15/05/12
 * @author scmjpg
 */
public interface Transaction {

	/**
	 * Method responsible to commit the transaction
	 * @throws SQLException
	 */
    void commit() throws SQLException;

    /**
     * Method responsible to close the transaction doing a rollback
     * @throws SQLException
     */
    void close() throws SQLException;
}
