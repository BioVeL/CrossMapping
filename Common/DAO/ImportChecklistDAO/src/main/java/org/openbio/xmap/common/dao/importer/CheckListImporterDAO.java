/*
 * #%L
 * XMap Checklist importer DAO
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
package org.openbio.xmap.common.dao.importer;

import java.io.File;
import java.util.List;

import org.openbio.xmap.common.dao.util.exception.DAOException;
import org.openbio.xmap.common.serviceinterfaces.types.xmap.TaskId;


public interface CheckListImporterDAO {
	
	/****************************************************************************************/
	/* Public methods																		*/														
	/****************************************************************************************/

	public boolean existChecklistNameInRepository(String checklistName,String user, String scope) throws DAOException;
	
	public String addChecklistToRepositoy (String checklistName, String fileName, String user, String scope, TaskId taskId) throws DAOException;
	
	public void importChecklistIntoTableTaxa(String checklistId, File importFile) throws DAOException;
	
	public void fillNameUse(String checklistId) throws DAOException;
	
	public void buildAdjacentTree(String checklistId) throws DAOException;
	
	public void preProccessDataForXMap(String checklistId) throws DAOException;		
	
	public void deleteImportedChecklist(String checklistId) throws DAOException;
	
	public void startTransaction() throws DAOException;
	
	public void commitTransaction() throws DAOException;
	
	public void rollbackTransaction() throws DAOException;
	
}
