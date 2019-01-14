/*
 * #%L
 * XMap Crossmapping DAO
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
package org.openbio.xmap.common.dao.crossmap.impl;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.lang.StringEscapeUtils;
import org.openbio.xmap.common.dao.crossmap.XMapDAO;
import org.openbio.xmap.common.dao.util.exception.DAOConnectionException;
import org.openbio.xmap.common.dao.util.exception.DAOException;
import org.openbio.xmap.common.dao.util.exception.DAOReadException;
import org.openbio.xmap.common.dao.util.exception.DAOWriteException;
import org.openbio.xmap.common.dao.util.helper.RelationalHelper;
import org.openbio.xmap.common.serviceinterfaces.types.xmap.Checklist;
import org.openbio.xmap.common.serviceinterfaces.types.xmap.ChecklistSeq;
import org.openbio.xmap.common.serviceinterfaces.types.xmap.DataRetrievalFilter;
import org.openbio.xmap.common.serviceinterfaces.types.xmap.DataRetrievalFilterSeq;
import org.openbio.xmap.common.serviceinterfaces.types.xmap.DataRetrievalSort;
import org.openbio.xmap.common.serviceinterfaces.types.xmap.DataRetrievalSortSeq;
import org.openbio.xmap.common.serviceinterfaces.types.xmap.EntriesInChecklistSeq;
import org.openbio.xmap.common.serviceinterfaces.types.xmap.EntriesInUserKnowledgeSeq;
import org.openbio.xmap.common.serviceinterfaces.types.xmap.EntriesInXMapSeq;
import org.openbio.xmap.common.serviceinterfaces.types.xmap.EntryInChecklist;
import org.openbio.xmap.common.serviceinterfaces.types.xmap.EntryInUserKnowledge;
import org.openbio.xmap.common.serviceinterfaces.types.xmap.EntryInXMap;
import org.openbio.xmap.common.serviceinterfaces.types.xmap.IdentifyExtraTaxaType;
import org.openbio.xmap.common.serviceinterfaces.types.xmap.NamesRelationshipType;
import org.openbio.xmap.common.serviceinterfaces.types.xmap.RawData;
import org.openbio.xmap.common.serviceinterfaces.types.xmap.RawDataRegister;
import org.openbio.xmap.common.serviceinterfaces.types.xmap.RelationshipDetail;
import org.openbio.xmap.common.serviceinterfaces.types.xmap.UserKnowledgeLevelForRefinement;
import org.openbio.xmap.common.serviceinterfaces.types.xmap.RawDataRegister.RawDataField;
import org.openbio.xmap.common.serviceinterfaces.types.xmap.RawDataRegisterSeq;
import org.openbio.xmap.common.serviceinterfaces.types.xmap.RelationshipPairTaxa;
import org.openbio.xmap.common.serviceinterfaces.types.xmap.RelationshipPairTaxaEntry;
import org.openbio.xmap.common.serviceinterfaces.types.xmap.ResultExportXmap;
import org.openbio.xmap.common.serviceinterfaces.types.xmap.ResultExportXmapSeq;
import org.openbio.xmap.common.serviceinterfaces.types.xmap.TaskId;
import org.openbio.xmap.common.serviceinterfaces.types.xmap.TaskStatus;
import org.openbio.xmap.common.serviceinterfaces.types.xmap.TaxonomicRank;
import org.openbio.xmap.common.serviceinterfaces.types.xmap.TaxonomicRankSeq;
import org.openbio.xmap.common.serviceinterfaces.types.xmap.XMap;
import org.openbio.xmap.common.serviceinterfaces.types.xmap.XMapSeq;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import au.com.bytecode.opencsv.CSVWriter;

public abstract class XMapDAORelationalImpl implements XMapDAO {
	
	/****************************************************************************************/
	/* ATTRIBUTES																			*/														
	/****************************************************************************************/	
	
	/**
	 * Datasource to connect to the database
	*/	
	protected DataSource ds;
	
	
	protected Connection conn;
	protected boolean isOuterMostTransaction;
	
	
	/**
	 * Relational helper to help with the sql commands
	 */		
	protected RelationalHelper helper;
	
	private static boolean dbCreated = false;
	private static final Object lock = new Object();
	
	/**
	 * slf4j logger for the given class.
	 */
	private static Logger logger = LoggerFactory.getLogger(XMapDAORelationalImpl.class);	
	
	
	/****************************************************************************************/
	/* CONSTRUCTORS																			*/		
	/****************************************************************************************/	
	
	/**
	 * Constructor of XMapDAORelationalImpl that receives the datasource to connect with the database and an
	 * object of a class that implements the interface RelationalHelper. 
	 * @param ds datasource to connect to the database
	 * @param helper implementation of the interface RelationalHelper to help with the sql commands
	 * @throws DAOException
	 */		
	public XMapDAORelationalImpl(DataSource ds,RelationalHelper helper) throws DAOException{
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
	public boolean existXMapNameInRepository(String xMapName,String user,String scope) throws DAOException{
		Connection conn = getConnection();
		PreparedStatement st = null;
		boolean existXMapName;
		
		try{						
			String sqlGetCrossMapByName = "select id from Crossmap where shortname=? and user=? and scope=?";			
			
			st = helper.createGeneratedKeysPreparedStatement(conn,sqlGetCrossMapByName);
			st.setString(1, xMapName);
			st.setString(2, user);
			st.setString(3, scope);
			
			ResultSet rs = st.executeQuery();
            
			if (rs.next()){
				existXMapName=true;
			}
			else{
				existXMapName=false;
			}						
            rs.close();
            
            return existXMapName;
			
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
	public String addXMapNameToRepository(String xMapName, String description, String leftChecklistId, 
			String rightChecklistId, boolean strict, IdentifyExtraTaxaType identifyExtraTaxa, 
			boolean compareHigherTaxa, TaxonomicRank highestRankToCompare, UserKnowledgeLevelForRefinement userRefinementLevel, 
			String user, String scope, TaskId taskId) throws DAOException{
		
		Connection conn = getConnection();	
		PreparedStatement stInsert = null;
		PreparedStatement stUpdate = null;
		try{				
			String sqlInsertXMap = "INSERT INTO Crossmap(shortname,user,scope,longname,leftChecklistId,rightChecklistId," +
					"strict,identifyExtraTaxa,compareHigherTaxa,highestRankIdToCompare,refinementLevel,taskId) VALUES (?,?,?,?,?,?,?,?,?,?,?,?);";
			
			stInsert = helper.createGeneratedKeysPreparedStatement(conn,sqlInsertXMap);
			stInsert.setString(1, xMapName);
			stInsert.setString(2,user);
			stInsert.setString(3,scope);
			stInsert.setString(4, description);
			stInsert.setString(5, leftChecklistId);
			stInsert.setString(6, rightChecklistId);
			if (strict){
				stInsert.setInt(7, 1);
				stInsert.setString(8, identifyExtraTaxa.value());
			}
			else{
				stInsert.setInt(7, 0);
				stInsert.setNull(8, java.sql.Types.VARCHAR);
			}			
			
			if (compareHigherTaxa){
				stInsert.setInt(9, 1);
				stInsert.setString(10, highestRankToCompare.getId());
			}
			else{
				stInsert.setInt(9, 0);
				stInsert.setNull(10, java.sql.Types.VARCHAR);
			}		
						
			stInsert.setString(11, userRefinementLevel.value());
								
			stInsert.setString(12, taskId.getValue());
			
			stInsert.executeUpdate();
			
			ResultSet rs = stInsert.getGeneratedKeys();
            if ( !rs.next() ) {
                throw new RuntimeException("cannot obtain id of xmap");
            }
            String xmapId = rs.getString(1);
          
            String sqlUpdate = "update Crossmap set prefixTableDB=? where id=?;";			
            stUpdate = helper.createGeneratedKeysPreparedStatement(conn,sqlUpdate);
            stUpdate.setString(1, "xmap_" + xmapId);
            stUpdate.setString(2, xmapId);            
            stUpdate.executeUpdate();
            
            return xmapId;
		}
		catch (SQLException ex){
			throw new DAOWriteException(ex);
		}	
		finally{
			closeStatementWithoutException(stInsert);
			closeStatementWithoutException(stUpdate);
			closeConnectionWithoutException(conn);
		}				
	}	
	
	
	@Override
	public void updateXMapInRepository(String xMapId, String xMapName, String description, String leftChecklistId, String rightChecklistId, boolean strict, 
			IdentifyExtraTaxaType identifyExtraTaxa, boolean compareHigherTaxa, TaxonomicRank highestRankToCompare, UserKnowledgeLevelForRefinement userRefinementLevel, 
			String user, String scope, TaskId taskId) throws DAOException {
	
		Connection conn = getConnection();	
		PreparedStatement stUpdate = null;
		try{				
			String sqlUpdateXMap = "update Crossmap set shortname=?,user=?,scope=?,longname=?,leftChecklistId=?,rightChecklistId=?," +
					"strict=?,identifyExtraTaxa=?,compareHigherTaxa=?,highestRankIdToCompare=?,refinementLevel=?,taskId=? where id=?;";
			
			stUpdate = helper.createGeneratedKeysPreparedStatement(conn,sqlUpdateXMap);
			stUpdate.setString(1, xMapName);
			stUpdate.setString(2,user);
			stUpdate.setString(3,scope);
			stUpdate.setString(4, description);
			stUpdate.setString(5, leftChecklistId);
			stUpdate.setString(6, rightChecklistId);
			if (strict){
				stUpdate.setInt(7, 1);
				stUpdate.setString(8, identifyExtraTaxa.value());
			}
			else{
				stUpdate.setInt(7, 0);
				stUpdate.setNull(8, java.sql.Types.VARCHAR);
			}			
			
			if (compareHigherTaxa){
				stUpdate.setInt(9, 1);
				stUpdate.setString(10, highestRankToCompare.getId());
			}
			else{
				stUpdate.setInt(9, 0);
				stUpdate.setNull(10, java.sql.Types.VARCHAR);
			}		
						
			stUpdate.setString(11, userRefinementLevel.value());
								
			stUpdate.setString(12, taskId.getValue());
			
			stUpdate.setString(13, xMapId);
			
			stUpdate.executeUpdate();
			
		}
		catch (SQLException ex){
			throw new DAOWriteException(ex);
		}	
		finally{
			closeStatementWithoutException(stUpdate);
			closeConnectionWithoutException(conn);
		}						
	}


	/**
	 * Method responsible to create the tables for the xmap 
	 * @param xMapId id of the xmap
	 */
	@Override
	public void createXMapTables(String xMapId) throws DAOException{
		Connection conn = getConnection();		
		List<String> sqlDDLsRollback = new ArrayList<String>();
		try{	
			XMap xMap = getXMap(xMapId,conn);
			
			String flatTable =  getTableNameXmapFlat(xMap.getPrefixTableDB());
			String viewFlatTable = getViewNameXmapFlatTable(xMap.getPrefixTableDB());
			String xmapRefineInfo = getTableNameXmapRefineInfo(xMap.getPrefixTableDB());
			String elementsInCommon = getTableNameElementsInCommon(xMap.getPrefixTableDB());			
			String list1tomanys = getTableNameListToManys(xMap.getPrefixTableDB(),1);
			String list2tomanys = getTableNameListToManys(xMap.getPrefixTableDB(),2);			
			String working = getTableNameWorking(xMap.getPrefixTableDB());

			
			String sqlCreateFlatTable = "create table " + flatTable + " ( " +
							" `id` int(11) NOT NULL AUTO_INCREMENT," +
							" `accName1` varchar(255) NOT NULL,"+
							" `uuid1` varchar(255) NOT NULL,"+
							" `taxId1` VARCHAR(60) NOT NULL,"+
							" `rank1` varchar(80) NOT NULL,"+
							" `ed1` int(10) NOT NULL,"+
							" `relationship` varchar(30) NOT NULL,"+
							" `relConfirmed` tinyint(1) NOT NULL DEFAULT 1,"+
							" `accName2` varchar(255) NOT NULL,"+
  							" `uuid2` varchar(255) NOT NULL,"+
  							" `taxId2` VARCHAR(60) NOT NULL,"+
  							" `rank2` varchar(80) NOT NULL,"+
  							" `ed2` int(10) NOT NULL,"+
  							" PRIMARY KEY (`id`),"+
  							" KEY `taxId1` (`taxId1`),"+
  							" KEY `taxId2` (`taxId2`),"+
  							" KEY `relationship` (`relationship`),"+
  							//" KEY `ed1` (`ed1`),"+
  							//" KEY `ed2` (`ed2`),"+
  							" KEY `abc` (`accName1`),"+
  							" KEY `def` (`accName2`),"+
  							" UNIQUE KEY `pairTaxa` (`taxId1`,`taxId2`)"+
  							");";

			String sqlCreateFlatView = "CREATE OR REPLACE VIEW " + viewFlatTable +
					" (taxonIdLeft,checklistNameLeft,rankLeft,acceptedNameLeft,uuidLeft,relationship,relConfirmed,taxonIdRight,checklistNameRight,rankRight,acceptedNameRight,uuidRight)" +
					" AS SELECT flat.taxId1,leftChk.checklistName,flat.rank1,flat.accName1,flat.uuid1,flat.relationship,flat.relConfirmed,flat.taxId2,rightChk.checklistName,flat.rank2,flat.accName2,flat.uuid2" +
					" FROM " + flatTable + " as flat " +
					" inner join Checklist as leftChk on leftChk.id = flat.ed1" +
					" inner join Checklist as rightChk on rightChk.id = flat.ed2;";										
		    
			
			//Create table to store the pairs of name to apply to the cross-map according to the userRefinementLevel
			String sqlCreateTableXmapRefineInfo = "CREATE TABLE " + xmapRefineInfo + " ("+
					" name1 varchar(255) not null," +
					" relationship varchar(30) not null," +
					" name2 varchar(255) not null," +
					" UNIQUE KEY `pairTaxa` (`name1`,`name2`)" +
					");";					
					
			
			String sqlCreateElementsInCommon="CREATE TABLE " + elementsInCommon + " (" +
					" `id` int(11) NOT NULL AUTO_INCREMENT, " +
					" `taxId1` VARCHAR(60) NOT NULL," +
					" `ed1` int(10) NOT NULL," +
					" `taxId2` VARCHAR(60) NOT NULL," +
					" `ed2` int(10) NOT NULL," +
					" `confirmed` tinyint(1) NOT NULL DEFAULT 1,"+
					" PRIMARY KEY (`id`), " +
					//" KEY `ed1` (`ed1`), KEY `ed2` (`ed2`)," +
					" KEY `taxId1` (`taxId1`),  KEY `taxId2` (`taxId2`)," +
					" UNIQUE KEY `pairTaxa` (`taxId1`,`taxId2`)" +
					");";																
		
			String sqlCreateListToManys1="CREATE TABLE "+list1tomanys+ " (" +
					" `taxonId` VARCHAR(60)," +
					" PRIMARY KEY(`taxonId`));";					
			
			String sqlCreateListToManys2="CREATE TABLE "+list2tomanys+ " (" +
					" `taxonId` VARCHAR(60)," +
					" PRIMARY KEY(`taxonId`));";		
						
			String sqlCreateWorking="CREATE TABLE "+ working + " ("+
					" `taxId1` VARCHAR(60) NOT NULL," +
					" `ed1` int(10) NOT NULL," +
					" `relationship` varchar(30) NOT NULL,"+
					" `taxId2` VARCHAR(60) NOT NULL," +
					" `ed2` int(10) NOT NULL," +
					" PRIMARY KEY(`taxId1`,`taxId2`));";		
					
			
			
		    //Add ddl instructions for the rollback
			sqlDDLsRollback.add("DROP VIEW IF EXISTS " + viewFlatTable + ";");
		    sqlDDLsRollback.add("DROP TABLE IF EXISTS " + flatTable + ";");
			sqlDDLsRollback.add("DROP TABLE IF EXISTS " + xmapRefineInfo + ";");
		    sqlDDLsRollback.add("DELETE TABLE IF EXISTS "+ elementsInCommon +";");
		    sqlDDLsRollback.add("DELETE TABLE IF EXISTS "+ list1tomanys +";");	
		    sqlDDLsRollback.add("DELETE TABLE IF EXISTS "+ list2tomanys +";");			
		    sqlDDLsRollback.add("DELETE TABLE IF EXISTS "+ working +";");	
		    
		        
		    
			startTransaction(conn);
			helper.executeUpdate(conn, sqlCreateFlatTable);						
			helper.executeUpdate(conn, sqlCreateFlatView);
			helper.executeUpdate(conn, sqlCreateTableXmapRefineInfo);
			helper.executeUpdate(conn, sqlCreateElementsInCommon);
			helper.executeUpdate(conn, sqlCreateListToManys1);
			helper.executeUpdate(conn, sqlCreateListToManys2);	
			helper.executeUpdate(conn, sqlCreateWorking);			
			commitTransaction(conn);
		}
		catch (SQLException ex){
			rollbackTransaction(conn);
			rollbackDDLs(conn,sqlDDLsRollback);
			throw new DAOWriteException(ex);
		}	
		finally{
			closeConnectionWithoutException(conn);
		}						
	}	
	
	@Override
	public void fillTableElementsInCommonByNames(String xMapId,String leftChecklistId,String rightChecklistId,boolean strict, UserKnowledgeLevelForRefinement userRefinementLevel) throws DAOException{
		Connection conn = getConnection();
		try{	
			XMap xMap = getXMap(xMapId,conn);
			Checklist leftChecklist = getChecklist(leftChecklistId,conn);
			Checklist rightChecklist = getChecklist(rightChecklistId,conn);			
			
			String elementsInCommon = getTableNameElementsInCommon(xMap.getPrefixTableDB());
			String xmapRefineInfo = getTableNameXmapRefineInfo(xMap.getPrefixTableDB());
			String nameuse1 = getTableNameChkListNameUse(leftChecklist.getPrefixTableDB());
			String nameuse2 = getTableNameChkListNameUse(rightChecklist.getPrefixTableDB());			
			String nameIdNameUsed = getNameIdNameUsed(strict);
						

			startTransaction(conn);			
			
			if (userRefinementLevel == UserKnowledgeLevelForRefinement.NONE){						
			    //Insert relationship between taxa with the same names (when the rank of this scientific name is species)
				String sqlInsertElementsInCommon = "INSERT IGNORE INTO " + elementsInCommon + " (taxId1, ed1, taxId2, ed2, confirmed)" +
					" SELECT LeftNameUse.taxonId, '"+leftChecklist.getId() + "', RightNameUse.taxonId,'"+rightChecklist.getId() + "', 1" +
					" FROM "+nameuse1+" as LeftNameUse " +
					" 	inner join "+nameuse2+" as RightNameUse on LeftNameUse."+nameIdNameUsed+" = RightNameUse." + nameIdNameUsed + " and LeftNameUse.rank = RightNameUse.rank" +
					" WHERE LeftNameUse.rank = 'species' and LeftNameUse.status in ('accepted','synonym') and RightNameUse.status in ('accepted','synonym');";
				
				helper.executeUpdate(conn, sqlInsertElementsInCommon);
			}
			else{				
				//Add to the table xmapRefineInfo the names that have been refined for this xmap
				String sqlInsertXmapRefinementLevelXMap = "INSERT IGNORE INTO " + xmapRefineInfo + " (name1,relationship,name2) " +
								" SELECT name1,relationship,name2 FROM UserKnowledge WHERE xMapId='" + xMapId + "';"; 		
				helper.executeUpdate(conn, sqlInsertXmapRefinementLevelXMap);
				
				if (userRefinementLevel.compareTo(UserKnowledgeLevelForRefinement.USER)>=0){
					//Add to the table xmapRefineInfo the names that have been refined for this users in different xmaps and don't conflict amongst themselves and neither
					//with knowledge define in this xmap 
					String sqlInsertXmapRefinementLevelUser ="INSERT IGNORE INTO " + xmapRefineInfo + " (name1,relationship,name2)"+		
							" SELECT refineInOthers.name1,refineInOthers.relationship,refineInOthers.name2"+
							" FROM UserKnowledge as refineInOthers"+
							"	left join " + xmapRefineInfo + " as refineInThis on refineInThis.name1=refineInOthers.name1 and refineInThis.name2=refineInOthers.name2"+ 
							" WHERE refineInThis.name1 is null and refineInOthers.user='" + xMap.getUser() + "' and refineInOthers.scope='" + xMap.getScope() + "'"+
							" GROUP BY refineInOthers.name1,refineInOthers.name2" +
							" HAVING count(distinct refineInOthers.relationship)=1;";
					helper.executeUpdate(conn, sqlInsertXmapRefinementLevelUser);
				}
				
				
				if (userRefinementLevel.compareTo(UserKnowledgeLevelForRefinement.GLOBAL)>=0){
					//Add to the table xmapRefineInfo the names that have been refined for other users and don't conflict amongst themselves and neither
					//with knowledge define for the user
					String sqlInsertXmapRefinementLevelGlobal ="INSERT IGNORE INTO " + xmapRefineInfo + " (name1,relationship,name2)"+		
							" SELECT refineInOthers.name1,refineInOthers.relationship,refineInOthers.name2"+
							" FROM UserKnowledge as refineInOthers"+
							"	left join " + xmapRefineInfo + " as refineInThis on refineInThis.name1=refineInOthers.name1 and refineInThis.name2=refineInOthers.name2"+ 
							" WHERE refineInThis.name1 is null" + 
							" GROUP BY refineInOthers.name1,refineInOthers.name2" +
							" HAVING count(distinct refineInOthers.relationship)=1;";		
					helper.executeUpdate(conn, sqlInsertXmapRefinementLevelGlobal);
				}	
				
				
				//Add entries in names in common that match but they are not marked as 'not equal' in the table xmapRefineInfo
				String sqlInsertElementsInCommonMatch = "INSERT IGNORE INTO " + elementsInCommon + " (taxId1, ed1, taxId2, ed2, confirmed)"+
						" SELECT LeftNameUse.taxonId, '"+leftChecklist.getId() + "', RightNameUse.taxonId,'"+rightChecklist.getId() + "', 1" +
						" FROM "+nameuse1+" as LeftNameUse " +
						" 	inner join " + nameuse2 + " as RightNameUse on LeftNameUse."+nameIdNameUsed+" = RightNameUse." + nameIdNameUsed + " and LeftNameUse.rank = RightNameUse.rank" +
						"	left join " + xmapRefineInfo + " as xmapRefineInfo on xmapRefineInfo.name1 = LeftNameUse.nameXMap and xmapRefineInfo.name2 = RightNameUse.nameXMap"+
						" WHERE  LeftNameUse.rank = 'species' and LeftNameUse.status in ('accepted','synonym') and RightNameUse.status in ('accepted','synonym')"+
						"	and (xmapRefineInfo.relationship is null or xmapRefineInfo.relationship!='NOT_EQUAL');";
				helper.executeUpdate(conn, sqlInsertElementsInCommonMatch);
													
				
				//Add entries in names in common that although they didn't match, they have been marked as 'equal to' in the user refinement stage
				String sqlInsertElementsInCommonNoMatch = "INSERT IGNORE INTO " + elementsInCommon + " (taxId1, ed1, taxId2, ed2, confirmed)"+
						" SELECT LeftNameUse.taxonId, '"+leftChecklist.getId() + "', RightNameUse.taxonId,'"+rightChecklist.getId() + "', 1" +
						" FROM " + xmapRefineInfo + " as xmapRefineInfo" +		
						" inner join " + nameuse1 + " as LeftNameUse on LeftNameUse.nameXMap = xmapRefineInfo.name1"+
						" inner join " + nameuse2 + " as RightNameUse on RightNameUse.nameXMap = xmapRefineInfo.name2"+
						" WHERE xmapRefineInfo.relationship='EQUAL'";
				helper.executeUpdate(conn, sqlInsertElementsInCommonNoMatch);								
			}
		
										
			//Delete the entries in elements in common that the rank of the accepted name of either taxa (left or right) is not species
			String sqlDeleteSubspecies = "DELETE nc FROM " + elementsInCommon + " as nc" +
						" inner join "+nameuse1+" as l on l.taxonId=nc.taxId1" +
						" inner join "+nameuse2+" as r on r.taxonId=nc.taxId2" +
						" where l.status='accepted' and r.status='accepted' and (l.rank!='species' or r.rank!='species');";								
			helper.executeUpdate(conn, sqlDeleteSubspecies);	
			
			
			commitTransaction(conn);
		}
		catch (SQLException ex){
			rollbackTransaction(conn);
			throw new DAOWriteException(ex);
		}	
		finally{
			closeConnectionWithoutException(conn);
		}		
	}
	
	@Override
	public void fillTablesToMany(String xMapId) throws DAOException{
		Connection conn = getConnection();		
		try{			
			XMap xMap = getXMap(xMapId,conn);
			
			String list1tomanys = getTableNameListToManys(xMap.getPrefixTableDB(),1);
			String list2tomanys = getTableNameListToManys(xMap.getPrefixTableDB(),2);							
			String elementsInCommon = getTableNameElementsInCommon(xMap.getPrefixTableDB());

			String sqlInsertListToManys1 = "INSERT INTO " + list1tomanys + 
					" SELECT taxId1 FROM " + elementsInCommon +
					" GROUP BY taxId1 HAVING count(taxId2) > 1;";	
			
			String sqlInsertListToManys2 = "INSERT INTO " + list2tomanys + 
					" SELECT taxId2 FROM " + elementsInCommon +
					" GROUP BY taxId2 HAVING count(taxId1) > 1;";							
			
			startTransaction(conn);
			helper.executeUpdate(conn, sqlInsertListToManys1);			
			helper.executeUpdate(conn, sqlInsertListToManys2);
			commitTransaction(conn);		
		}
		catch (SQLException ex){
			rollbackTransaction(conn);
			throw new DAOWriteException(ex);
		}	
		finally{
			closeConnectionWithoutException(conn);
		}			
	}
	
	/**
	 * Method responsible to obtain not found in relationships and insert them in the flat table
	 * @param xMapId
	 * @param leftChecklistId
	 * @param rightChecklistId
	 * @throws DAOException
	 */
	@Override
	public void obtainNotFoundInRelationships(String xMapId, String leftChecklistId, String rightChecklistId, TaxonomicRank rank) throws DAOException{
		Connection conn = getConnection();		
		try{	
			XMap xMap = getXMap(xMapId,conn);
			Checklist leftChecklist = getChecklist(leftChecklistId,conn);
			Checklist rightChecklist = getChecklist(rightChecklistId,conn);
			
			String flatTable =  getTableNameXmapFlat(xMap.getPrefixTableDB());
			String nameuse1 = getTableNameChkListNameUse(leftChecklist.getPrefixTableDB());
			String nameuse2 = getTableNameChkListNameUse(rightChecklist.getPrefixTableDB());
			String taxon1 = getTableNameChkListTaxon(leftChecklist.getPrefixTableDB());
			String taxon2 = getTableNameChkListTaxon(rightChecklist.getPrefixTableDB());
			String elementsInCommon = getTableNameElementsInCommon(xMap.getPrefixTableDB());
			String working = getTableNameWorking(xMap.getPrefixTableDB());
					
			String sqlDeleteWorking = "DELETE FROM " + working;
						
			//Obtain taxa in left checklist that don't appear in the right checklist. 
			String sqlInsertLeftNotFoundInWorking = "INSERT IGNORE INTO " + working + " (taxId1,ed1,relationship,taxId2,ed2)" +
					" SELECT n1.taxonId, '"+ leftChecklist.getId() + "','not_found_in', -1,'" + rightChecklist.getId() + "'" +
					" FROM " + nameuse1 + " as n1 " +
					"	left join " + elementsInCommon + " as nc on nc.taxId1 = n1.taxonId" +
					" WHERE n1.rank='"+ rank.getName() + "' and n1.status='accepted' and nc.taxId1 is null;";
				
			//From the taxa previously obtained, insert them into the flat table
			String sqlInsertLeftNotFoundInFlat="INSERT INTO " + flatTable +	" (accName1,uuid1,taxId1,rank1,ed1,relationship,accName2,uuid2,taxId2,rank2,ed2)" +		
					" SELECT n1.tidyName, t1.uuid, wrk.taxId1, t1.rank, wrk.ed1, wrk.relationship,'','', wrk.taxId2,'', wrk.ed2" +
					" FROM " + working + " as wrk " +
					"	inner join " + nameuse1 + " as n1 on n1.taxonId = wrk.taxId1" +
					" 	inner join " + taxon1 + " as t1 on t1.id = n1.taxonId" +					
					" WHERE n1.rank='"+ rank.getName() + "' and n1.status='accepted';";
			
			//Obtain taxa in right checklist  that don't appear in the left checklist. 
			String sqlInsertRightNotFoundInWorking = "INSERT IGNORE INTO " + working + " (taxId1,ed1,relationship,taxId2,ed2)" +
					" SELECT -1,'" + leftChecklist.getId() + "','not_found_in', n2.taxonId,'"+ rightChecklist.getId() + "'" +
					" FROM " + nameuse2 + " as n2 " +
					"	left join " + elementsInCommon + " as nc on nc.taxId2 = n2.taxonId" +
					" WHERE n2.rank='"+ rank.getName() + "' and n2.status='accepted' and nc.taxId2 is null;";
				
			//From the taxa previously obtained, insert them into the flat table
			String sqlInsertRightNotFoundInFlat="INSERT INTO " + flatTable +	" (accName1,uuid1,taxId1,rank1,ed1,relationship,relConfirmed,accName2,uuid2,taxId2,rank2,ed2)" +		
					" SELECT '','', wrk.taxId1,'', wrk.ed1, wrk.relationship, 1, n2.tidyName, t2.uuid, wrk.taxId2, t2.rank, wrk.ed2" +
					" FROM " + working + " as wrk " +
					"	inner join " + nameuse2 + " as n2 on n2.taxonId = wrk.taxId2" +
					" 	inner join " + taxon2 + " as t2 on t2.id = n2.taxonId" +					
					" WHERE n2.rank='"+ rank.getName() + "' and n2.status='accepted';";			
			
			startTransaction(conn);
			helper.executeUpdate(conn, sqlDeleteWorking);
			helper.executeUpdate(conn, sqlInsertLeftNotFoundInWorking);
			helper.executeUpdate(conn, sqlInsertLeftNotFoundInFlat);
			helper.executeUpdate(conn, sqlDeleteWorking);
			helper.executeUpdate(conn, sqlInsertRightNotFoundInWorking);
			helper.executeUpdate(conn, sqlInsertRightNotFoundInFlat);			
			commitTransaction(conn);
			
		}
		catch (SQLException ex){
			rollbackTransaction(conn);
			throw new DAOWriteException(ex);
		}	
		finally{
			closeConnectionWithoutException(conn);
		}						
	}		
	
	@Override
	public void obtainCorrespondRelationships(String xMapId,String leftChecklistId,String rightChecklistId, TaxonomicRank rank) throws DAOException{
		Connection conn = getConnection();	
		try{
			XMap xMap = getXMap(xMapId,conn);
			Checklist leftChecklist = getChecklist(leftChecklistId,conn);
			Checklist rightChecklist = getChecklist(rightChecklistId,conn);	
			
			String flatTable =  getTableNameXmapFlat(xMap.getPrefixTableDB());
			String nameuse1 = getTableNameChkListNameUse(leftChecklist.getPrefixTableDB());
			String taxon1 = getTableNameChkListTaxon(leftChecklist.getPrefixTableDB());
			String nameuse2 = getTableNameChkListNameUse(rightChecklist.getPrefixTableDB());
			String taxon2 = getTableNameChkListTaxon(rightChecklist.getPrefixTableDB());			
			String elementsInCommon = getTableNameElementsInCommon(xMap.getPrefixTableDB());
			String list1tomanys = getTableNameListToManys(xMap.getPrefixTableDB(),1);
			String list2tomanys = getTableNameListToManys(xMap.getPrefixTableDB(),2);			
			String working = getTableNameWorking(xMap.getPrefixTableDB());
			
			String sqlDeleteWorking = "DELETE FROM " + working;
			
			//Obtain pairs of taxa that have a corresponds relationships. 
			String sqlInsertInWorking = "INSERT IGNORE INTO " + working + " (taxId1,ed1,relationship,taxId2,ed2)" +
					" SELECT nc.taxId1, '"+ leftChecklist.getId() + "','corresponds',nc.taxId2,'" + rightChecklist.getId() + "'" +
					" FROM " + elementsInCommon + " as nc " +
					"	left join " + list1tomanys + " as tm1 on tm1.taxonId = nc.taxId1" +
					"	left join " + list2tomanys + " as tm2 on tm2.taxonId = nc.taxId2" +
					" WHERE tm1.taxonId is null and tm2.taxonId is null;";
																
			//From the taxa previously obtained, insert them into the flat table
			String sqlInsertInFlatCorresponds = "INSERT INTO " + flatTable + " (accName1,uuid1,taxId1,rank1,ed1,relationship,relConfirmed,accName2,uuid2,taxId2,rank2,ed2)" + 
					" SELECT n1.tidyName, t1.uuid, wrk.taxId1, t1.rank, wrk.ed1, wrk.relationship, 1, n2.tidyName, t2.uuid, wrk.taxId2, t2.rank, wrk.ed2" +
					" FROM " + working + " as wrk " +
					" 	inner join "+ nameuse1 + " as n1 on n1.taxonId = wrk.taxId1" +
					" 	inner join "+ taxon1 + " as t1 on t1.id = wrk.taxId1" +
					" 	inner join "+ nameuse2 + " as n2 on n2.taxonId = wrk.taxId2" +
					" 	inner join "+ taxon2 + " as t2 on t2.id = wrk.taxId2" +
					" WHERE n1.rank='"+ rank.getName() + "' and n1.status='accepted' and n2.rank='"+ rank.getName() + "' and n2.status='accepted';";
						
			startTransaction(conn);
			helper.executeUpdate(conn, sqlDeleteWorking);
			helper.executeUpdate(conn, sqlInsertInWorking);
			helper.executeUpdate(conn, sqlInsertInFlatCorresponds);
			commitTransaction(conn);
			
		}
		catch (SQLException ex){
			throw new DAOWriteException(ex);
		}	
		finally{
			closeConnectionWithoutException(conn);
		}			
	}
	
	@Override
	public void obtainIncludesRelationships(String xMapId,String leftChecklistId,String rightChecklistId, TaxonomicRank rank) throws DAOException{
		Connection conn = getConnection();	
		try{
			XMap xMap = getXMap(xMapId,conn);
			Checklist leftChecklist = getChecklist(leftChecklistId,conn);
			Checklist rightChecklist = getChecklist(rightChecklistId,conn);	
			
			String flatTable =  getTableNameXmapFlat(xMap.getPrefixTableDB());
			String nameuse1 = getTableNameChkListNameUse(leftChecklist.getPrefixTableDB());
			String taxon1 = getTableNameChkListTaxon(leftChecklist.getPrefixTableDB());
			String nameuse2 = getTableNameChkListNameUse(rightChecklist.getPrefixTableDB());
			String taxon2 = getTableNameChkListTaxon(rightChecklist.getPrefixTableDB());			
			String elementsInCommon = getTableNameElementsInCommon(xMap.getPrefixTableDB());
			String list1tomanys = getTableNameListToManys(xMap.getPrefixTableDB(),1);	
			String list2tomanys = getTableNameListToManys(xMap.getPrefixTableDB(),2);	
			String working = getTableNameWorking(xMap.getPrefixTableDB());
			
			String sqlDeleteWorking = "DELETE FROM " + working;
			
			//Obtain pairs of taxa that have an includes relationships. 
			String sqlInsertInWorking = "INSERT IGNORE INTO " + working + " (taxId1,ed1,relationship,taxId2,ed2)" +
					" SELECT nc.taxId1, '"+ leftChecklist.getId() + "','includes',nc.taxId2,'" + rightChecklist.getId() + "'" +
					" FROM " + elementsInCommon + " as nc " +
					"	inner join " + list1tomanys + " as tm1 on tm1.taxonId = nc.taxId1" +
					"	left join " + list2tomanys + " as tm2 on tm2.taxonId = nc.taxId2" +
					" WHERE tm2.taxonId is null;";
																
			//From the taxa previously obtained, insert them into the flat table
			String sqlInsertInFlatIncludes = "INSERT INTO " + flatTable + " (accName1,uuid1,taxId1,rank1,ed1,relationship,relConfirmed,accName2,uuid2,taxId2,rank2,ed2)" + 
					" SELECT n1.tidyName, t1.uuid, wrk.taxId1, t1.rank, wrk.ed1, wrk.relationship, 1, n2.tidyName, t2.uuid, wrk.taxId2, t2.rank, wrk.ed2" +
					" FROM " + working + " as wrk " +
					" 	inner join "+ nameuse1 + " as n1 on n1.taxonId = wrk.taxId1" +
					" 	inner join "+ taxon1 + " as t1 on t1.id = wrk.taxId1" +
					" 	inner join "+ nameuse2 + " as n2 on n2.taxonId = wrk.taxId2" +
					" 	inner join "+ taxon2 + " as t2 on t2.id = wrk.taxId2" +
					" WHERE n1.rank='"+ rank.getName() + "' and n1.status='accepted' and n2.rank='"+ rank.getName() + "' and n2.status='accepted';";
			
			startTransaction(conn);
			helper.executeUpdate(conn, sqlDeleteWorking);
			helper.executeUpdate(conn, sqlInsertInWorking);
			helper.executeUpdate(conn, sqlInsertInFlatIncludes);
			commitTransaction(conn);	
		}
		catch (SQLException ex){
			rollbackTransaction(conn);
			throw new DAOWriteException(ex);
		}	
		finally{
			closeConnectionWithoutException(conn);
		}			
	}	
	
	
	@Override
	public void obtainIncludedByRelationships(String xMapId,String leftChecklistId,String rightChecklistId, TaxonomicRank rank) throws DAOException{
		Connection conn = getConnection();	
		try{
			XMap xMap = getXMap(xMapId,conn);
			Checklist leftChecklist = getChecklist(leftChecklistId,conn);
			Checklist rightChecklist = getChecklist(rightChecklistId,conn);	
			
			String flatTable =  getTableNameXmapFlat(xMap.getPrefixTableDB());
			String nameuse1 = getTableNameChkListNameUse(leftChecklist.getPrefixTableDB());
			String taxon1 = getTableNameChkListTaxon(leftChecklist.getPrefixTableDB());
			String nameuse2 = getTableNameChkListNameUse(rightChecklist.getPrefixTableDB());
			String taxon2 = getTableNameChkListTaxon(rightChecklist.getPrefixTableDB());			
			String elementsInCommon = getTableNameElementsInCommon(xMap.getPrefixTableDB());
			String list1tomanys = getTableNameListToManys(xMap.getPrefixTableDB(),1);	
			String list2tomanys = getTableNameListToManys(xMap.getPrefixTableDB(),2);					
			String working = getTableNameWorking(xMap.getPrefixTableDB());
			
			
			String sqlDeleteWorking = "DELETE FROM " + working;
			
			//Obtain pairs of taxa that have an included_by relationships. 
			String sqlInsertInWorking = "INSERT IGNORE INTO " + working + " (taxId1,ed1,relationship,taxId2,ed2)" +
					" SELECT nc.taxId1, '"+ leftChecklist.getId() + "','included_by',nc.taxId2,'" + rightChecklist.getId() + "'" +
					" FROM " + elementsInCommon + " as nc " +
					"	inner join " + list2tomanys + " as tm2 on tm2.taxonId = nc.taxId2" +
					"	left join " + list1tomanys + " as tm1 on tm1.taxonId = nc.taxId1" +
					" WHERE tm1.taxonId is null;";
																
			//From the taxa previously obtained, insert them into the flat table
			String sqlInsertInFlatIncludedBy = "INSERT INTO " + flatTable + " (accName1,uuid1,taxId1,rank1,ed1,relationship,relConfirmed,accName2,uuid2,taxId2,rank2,ed2)" + 
					" SELECT n1.tidyName, t1.uuid, wrk.taxId1, t1.rank, wrk.ed1, wrk.relationship, 1, n2.tidyName, t2.uuid, wrk.taxId2, t2.rank, wrk.ed2" +
					" FROM " + working + " as wrk " +
					" 	inner join "+ nameuse1 + " as n1 on n1.taxonId = wrk.taxId1" +
					" 	inner join "+ taxon1 + " as t1 on t1.id = wrk.taxId1" +
					" 	inner join "+ nameuse2 + " as n2 on n2.taxonId = wrk.taxId2" +
					" 	inner join "+ taxon2 + " as t2 on t2.id = wrk.taxId2" +
					" WHERE n1.rank='"+ rank.getName() + "' and n1.status='accepted' and n2.rank='"+ rank.getName() + "' and n2.status='accepted';";
			
			startTransaction(conn);
			helper.executeUpdate(conn, sqlDeleteWorking);
			helper.executeUpdate(conn, sqlInsertInWorking);
			helper.executeUpdate(conn, sqlInsertInFlatIncludedBy);
			commitTransaction(conn);
		}
		catch (SQLException ex){
			rollbackTransaction(conn);
			throw new DAOWriteException(ex);
		}	
		finally{
			closeConnectionWithoutException(conn);
		}			
	}		
	
	
	@Override
	public void obtainOverlapsRelationships(String xMapId,String leftChecklistId,String rightChecklistId, TaxonomicRank rank) throws DAOException{
		Connection conn = getConnection();	
		try{
			XMap xMap = getXMap(xMapId,conn);
			Checklist leftChecklist = getChecklist(leftChecklistId,conn);
			Checklist rightChecklist = getChecklist(rightChecklistId,conn);	
			
			String flatTable =  getTableNameXmapFlat(xMap.getPrefixTableDB());
			String nameuse1 = getTableNameChkListNameUse(leftChecklist.getPrefixTableDB());
			String taxon1 = getTableNameChkListTaxon(leftChecklist.getPrefixTableDB());
			String nameuse2 = getTableNameChkListNameUse(rightChecklist.getPrefixTableDB());
			String taxon2 = getTableNameChkListTaxon(rightChecklist.getPrefixTableDB());			
			String elementsInCommon = getTableNameElementsInCommon(xMap.getPrefixTableDB());			
			String list1tomanys = getTableNameListToManys(xMap.getPrefixTableDB(),1);	
			String list2tomanys = getTableNameListToManys(xMap.getPrefixTableDB(),2);				
			String working = getTableNameWorking(xMap.getPrefixTableDB());
			
			
			String sqlDeleteWorking = "DELETE FROM " + working;
			
			//Obtain pairs of taxa that have an overlaps relationships. 
			String sqlInsertInWorking = "INSERT IGNORE INTO " + working + " (taxId1,ed1,relationship,taxId2,ed2)" +
					" SELECT nc.taxId1, '"+ leftChecklist.getId() + "','overlaps',nc.taxId2,'" + rightChecklist.getId() + "'" +
					" FROM " + elementsInCommon + " as nc " +					
					"	inner join " + list1tomanys + " as tm1 on tm1.taxonId = nc.taxId1" +
					"	inner join " + list2tomanys + " as tm2 on tm2.taxonId = nc.taxId2;";
				
			//From the taxa previously obtained, insert them into the flat table
			String sqlInsertInFlatOverlaps = "INSERT INTO " + flatTable +  " (accName1,uuid1,taxId1,rank1,ed1,relationship,relConfirmed,accName2,uuid2,taxId2,rank2,ed2)" +
					" SELECT n1.tidyName, t1.uuid, wrk.taxId1, t1.rank, wrk.ed1, wrk.relationship, 1, n2.tidyName, t2.uuid, wrk.taxId2, t2.rank, wrk.ed2" +
					" FROM " + working + " as wrk " +
					" 	inner join "+ nameuse1 + " as n1 on n1.taxonId = wrk.taxId1" +
					" 	inner join "+ taxon1 + " as t1 on t1.id = wrk.taxId1" +
					" 	inner join "+ nameuse2 + " as n2 on n2.taxonId = wrk.taxId2" +
					" 	inner join "+ taxon2 + " as t2 on t2.id = wrk.taxId2" +
					" WHERE n1.rank='"+ rank.getName() + "' and n1.status='accepted' and n2.rank='"+ rank.getName() + "' and n2.status='accepted';";
			
			startTransaction(conn);
			helper.executeUpdate(conn, sqlDeleteWorking);
			helper.executeUpdate(conn, sqlInsertInWorking);
			helper.executeUpdate(conn, sqlInsertInFlatOverlaps);
			commitTransaction(conn);									
		}
		catch (SQLException ex){
			throw new DAOWriteException(ex);
		}	
		finally{
			closeConnectionWithoutException(conn);
		}			
	}

		
	@Override
	public void reevaluateLowerTaxaNotFoundWithPossNameMatch(String xMapId,String leftChecklistId,String rightChecklistId, UserKnowledgeLevelForRefinement userRefinementLevel) throws DAOException{
		Connection conn = getConnection();		
		try{	
			XMap xMap = getXMap(xMapId,conn);
			Checklist leftChecklist = getChecklist(leftChecklistId,conn);
			Checklist rightChecklist = getChecklist(rightChecklistId,conn);			
			
			String flatTable =  getTableNameXmapFlat(xMap.getPrefixTableDB());	
			String elementsInCommon = getTableNameElementsInCommon(xMap.getPrefixTableDB());
			String nameuse1 = getTableNameChkListNameUse(leftChecklist.getPrefixTableDB());
			String taxon1 = getTableNameChkListTaxon(leftChecklist.getPrefixTableDB());
			String nameuse2 = getTableNameChkListNameUse(rightChecklist.getPrefixTableDB());
			String taxon2 = getTableNameChkListTaxon(rightChecklist.getPrefixTableDB());
			String working = getTableNameWorking(xMap.getPrefixTableDB());
			String xmapRefineInfo = getTableNameXmapRefineInfo(xMap.getPrefixTableDB());
			
			
			String sqlDeleteWorking = "DELETE FROM " + working;
			
			//Obtain pairs of taxa that have a poss_name_match relationship from those taxa in the left checklist that have not been found a relationship yet
			String sqlInsertInWorkingPossNameMatchLeftToRight = "INSERT IGNORE INTO " + working + " (taxId1,ed1,relationship,taxId2,ed2)" +
					" SELECT f.taxId1, '"+ leftChecklist.getId() + "', 'poss_name_match', n2.taxonId, '" + rightChecklist.getId() + "'" +
					" FROM " + flatTable + " as f" +
					"	inner join "+ nameuse1 +" as n1 on n1.taxonId = f.taxId1" +
					"	inner join "+ nameuse2 +" as n2 on n2.rank = n1.rank and n2.nameNoAuthorXMap = n1.nameNoAuthorXMap" +    	
					"   left join "+ xmapRefineInfo +" as xmapRefineInfo on xmapRefineInfo.name1=n1.nameXMap and xmapRefineInfo.name2=n2.nameXMap and xmapRefineInfo.relationship='NOT_EQUAL'" +
					" WHERE f.relationship = 'not_found_in' and f.taxId1!='-1' and n1.rank = 'species' and xmapRefineInfo.name1 is null;";
			
			//Obtain pairs of taxa that have a poss_name_match relationships from those taxa in the right checklist that have not been found a relationship yet 
			String sqlInsertInWorkingPossNameMatchRightToLeft = "INSERT IGNORE INTO " + working + " (taxId1,ed1,relationship,taxId2,ed2)" +
					" SELECT n1.taxonId, '"+ leftChecklist.getId() + "', 'poss_name_match', f.taxId2, '" + rightChecklist.getId() + "'" +
					" FROM " + flatTable + " as f" +
					"	inner join "+ nameuse2 +" as n2 on n2.taxonId = f.taxId2" +
					"	inner join "+ nameuse1 +" as n1 on n1.rank = n2.rank and n1.nameNoAuthorXMap = n2.nameNoAuthorXMap" +    	
					"   left join "+ xmapRefineInfo +" as xmapRefineInfo on xmapRefineInfo.name1=n1.nameXMap and xmapRefineInfo.name2=n2.nameXMap and xmapRefineInfo.relationship='NOT_EQUAL'" +
					" WHERE f.relationship = 'not_found_in' and f.taxId2!='-1' and n2.rank = 'species' and xmapRefineInfo.name1 is null;";
						
			//Delete the entries in working that the rank of the accepted name of either taxa (left or right) is not species
			String sqlDeleteSubspecies = "DELETE wrk FROM " + working + " as wrk" +
						" inner join "+nameuse1+" as l on l.taxonId=wrk.taxId1" +
						" inner join "+nameuse2+" as r on r.taxonId=wrk.taxId2" +
						" where l.status='accepted' and r.status='accepted' and (l.rank!='species' or r.rank!='species');";		
			
			//Insert relationship between taxa with the same names (when the rank of this scientific name is species)
			String sqlInsertInElementsInCommon = "INSERT IGNORE INTO " + elementsInCommon + " (taxId1, ed1, taxId2, ed2, confirmed)" +
				" SELECT  wrk.taxId1,  wrk.ed1, wrk.taxId2, wrk.ed2, 0" +
				" FROM " + working + " as wrk;";				
			
			//Delete entries in the table flat table that had a not_found_in relationship but it has been reevaluated to poss_name_match
			String sqlDeleteNegativeNotFoundInFlat = "DELETE FROM " + flatTable + " USING " + flatTable + " INNER JOIN " + working + " WHERE " + flatTable + ".relationship = 'not_found_in' and (" + flatTable + ".taxId1 = " + working + ".taxId1 or " + flatTable + ".taxId2 = " + working + ".taxId2);";
						
			//From the taxa previously obtained, insert them into the flat table
			String sqlInsertInFlatPossNameMatch = "INSERT INTO " + flatTable +  " (accName1,uuid1,taxId1,rank1,ed1,relationship,relConfirmed,accName2,uuid2,taxId2,rank2,ed2)" +
					" SELECT n1.tidyName, t1.uuid, wrk.taxId1, t1.rank, wrk.ed1, wrk.relationship, 0, n2.tidyName, t2.uuid, wrk.taxId2, t2.rank, wrk.ed2" +
					" FROM " + working + " as wrk " +
					" 	inner join "+ nameuse1 + " as n1 on n1.taxonId = wrk.taxId1" +
					" 	inner join "+ taxon1 + " as t1 on t1.id = wrk.taxId1" +
					" 	inner join "+ nameuse2 + " as n2 on n2.taxonId = wrk.taxId2" +
					" 	inner join "+ taxon2 + " as t2 on t2.id = wrk.taxId2" +
					" WHERE n1.rank='species' and n1.status='accepted' and n2.rank='species' and n2.status='accepted';";
						
			
			startTransaction(conn);
			helper.executeUpdate(conn, sqlDeleteWorking);
			helper.executeUpdate(conn, sqlInsertInWorkingPossNameMatchLeftToRight);
			helper.executeUpdate(conn, sqlDeleteSubspecies);
			helper.executeUpdate(conn, sqlInsertInElementsInCommon);
			helper.executeUpdate(conn, sqlDeleteNegativeNotFoundInFlat);			
			helper.executeUpdate(conn, sqlInsertInFlatPossNameMatch);
			
			helper.executeUpdate(conn, sqlDeleteWorking);
			helper.executeUpdate(conn, sqlInsertInWorkingPossNameMatchRightToLeft);
			helper.executeUpdate(conn, sqlDeleteSubspecies);
			helper.executeUpdate(conn, sqlInsertInElementsInCommon);
			helper.executeUpdate(conn, sqlDeleteNegativeNotFoundInFlat);			
			helper.executeUpdate(conn, sqlInsertInFlatPossNameMatch);						
			commitTransaction(conn);	
		}
		catch (SQLException ex){
			rollbackTransaction(conn);
			throw new DAOWriteException(ex);
		}	
		finally{
			closeConnectionWithoutException(conn);
		}					
	}
	
	@Override
	public void reevaluateLowerTaxaNotFoundWithPossGenTrnfr(String xMapId,String leftChecklistId,String rightChecklistId, UserKnowledgeLevelForRefinement userRefinementLevel) throws DAOException{
		Connection conn = getConnection();		
		try{	
			XMap xMap = getXMap(xMapId,conn);
			Checklist leftChecklist = getChecklist(leftChecklistId,conn);
			Checklist rightChecklist = getChecklist(rightChecklistId,conn);	
			
			String flatTable =  getTableNameXmapFlat(xMap.getPrefixTableDB());	
			String elementsInCommon = getTableNameElementsInCommon(xMap.getPrefixTableDB());
			String nameuse1 = getTableNameChkListNameUse(leftChecklist.getPrefixTableDB());
			String taxon1 = getTableNameChkListTaxon(leftChecklist.getPrefixTableDB());
			String nameuse2 = getTableNameChkListNameUse(rightChecklist.getPrefixTableDB());
			String taxon2 = getTableNameChkListTaxon(rightChecklist.getPrefixTableDB());
			String working = getTableNameWorking(xMap.getPrefixTableDB());
			String xmapRefineInfo = getTableNameXmapRefineInfo(xMap.getPrefixTableDB());
			
			String sqlDeleteWorking = "DELETE FROM " + working;
			
			//Obtain pairs of taxa that have an poss_gen_trnfr relationships from those taxa in the left checklist that have not been found a relationship yet
			String sqlInsertInWorkingPossGenTrfLeftToRight = "INSERT IGNORE INTO " + working + " (taxId1,ed1,relationship,taxId2,ed2)" +
					" SELECT f.taxId1, '"+ leftChecklist.getId() + "', 'poss_gen_trnfr', n2.taxonId, '" + rightChecklist.getId() + "'" +
					" FROM " + flatTable + " as f" +
					"	inner join "+ nameuse1 +" as n1 on n1.taxonId = f.taxId1" +
					"	inner join "+ nameuse2 +" as n2 on n2.rank = n1.rank and n2.epithetXMap = n1.epithetXMap " +    	
					"   left join "+ xmapRefineInfo +" as xmapRefineInfo on xmapRefineInfo.name1=n1.nameXMap and xmapRefineInfo.name2=n2.nameXMap and xmapRefineInfo.relationship='NOT_EQUAL'" +
					" WHERE f.relationship = 'not_found_in' and f.taxId1!='-1' and n1.rank = 'species' and xmapRefineInfo.name1 is null" +
					"  and ((n1.authority != '' AND LOCATE(n1.authority, n2.authority)>0) OR (n2.authority != '' AND LOCATE(n2.authority, n1.authority)>0));";
			
			//Obtain pairs of taxa that have an poss_gen_trnfr relationships from those taxa in the right checklist that have not been found a relationship yet
			String sqlInsertInWorkingPossGenTrfRightToLeft = "INSERT IGNORE INTO " + working + " (taxId1,ed1,relationship,taxId2,ed2)" +
					" SELECT n1.taxonId, '"+ leftChecklist.getId() + "', 'poss_gen_trnfr', f.taxId2, '" + rightChecklist.getId() + "'" +
					" FROM " + flatTable + " as f" +
					"	inner join "+ nameuse2 +" as n2 on n2.taxonId = f.taxId2" +
					"	inner join "+ nameuse1 +" as n1 on n1.rank = n2.rank and n1.epithetXMap = n2.epithetXMap " +    	
					"   left join "+ xmapRefineInfo +" as xmapRefineInfo on xmapRefineInfo.name1=n1.nameXMap and xmapRefineInfo.name2=n2.nameXMap and xmapRefineInfo.relationship='NOT_EQUAL'" +
					" WHERE f.relationship = 'not_found_in' and f.taxId2!='-1' and n2.rank = 'species' and xmapRefineInfo.name1 is null" +
					"  and ((n2.authority != '' AND LOCATE(n2.authority, n1.authority)>0) OR (n1.authority != '' AND LOCATE(n1.authority, n2.authority)>0));";	
			
			//Delete the entries in working that the rank of the accepted name of either taxa (left or right) is not species
			String sqlDeleteSubspecies = "DELETE wrk FROM " + working + " as wrk" +
						" inner join "+nameuse1+" as l on l.taxonId=wrk.taxId1" +
						" inner join "+nameuse2+" as r on r.taxonId=wrk.taxId2" +
						" where l.status='accepted' and r.status='accepted' and (l.rank!='species' or r.rank!='species');";		
			
			//Insert relationship between taxa with the same names (when the rank of this scientific name is species)
			String sqlInsertInElementsInCommon = "INSERT IGNORE INTO " + elementsInCommon + " (taxId1, ed1, taxId2, ed2, confirmed)" +
				" SELECT  wrk.taxId1,  wrk.ed1, wrk.taxId2, wrk.ed2, 0" +
				" FROM " + working + " as wrk;";					

			//Delete entries in the table flat table that had a not_found_in relationship but it has been reevaluated to poss_gen_trnfr
			String sqlDeleteNegativeNotFoundInFlat = "DELETE FROM " + flatTable + " USING " + flatTable + " INNER JOIN " + working + " WHERE " + flatTable + ".relationship = 'not_found_in' and (" + flatTable + ".taxId1 = " + working + ".taxId1 or " + flatTable + ".taxId2 = " + working + ".taxId2);";
			
			//From the taxa previously obtained, insert them into the flat table
			String sqlInsertInFlatPossGenTrnfr = "INSERT INTO " + flatTable +  " (accName1,uuid1,taxId1,rank1,ed1,relationship,relConfirmed,accName2,uuid2,taxId2,rank2,ed2)" +
					" SELECT n1.tidyName, t1.uuid, wrk.taxId1, t1.rank, wrk.ed1, wrk.relationship, 0, n2.tidyName, t2.uuid, wrk.taxId2, t2.rank, wrk.ed2" +
					" FROM " + working + " as wrk " +
					" 	inner join "+ nameuse1 + " as n1 on n1.taxonId = wrk.taxId1" +
					" 	inner join "+ taxon1 + " as t1 on t1.id = wrk.taxId1" +
					" 	inner join "+ nameuse2 + " as n2 on n2.taxonId = wrk.taxId2" +
					" 	inner join "+ taxon2 + " as t2 on t2.id = wrk.taxId2" +
					" WHERE n1.rank='species' and n1.status='accepted' and n2.rank='species' and n2.status='accepted';";
			
			startTransaction(conn);
			helper.executeUpdate(conn, sqlDeleteWorking);
			helper.executeUpdate(conn, sqlInsertInWorkingPossGenTrfLeftToRight);
			helper.executeUpdate(conn, sqlDeleteSubspecies);			
			helper.executeUpdate(conn, sqlInsertInElementsInCommon);
			helper.executeUpdate(conn, sqlDeleteNegativeNotFoundInFlat);
			helper.executeUpdate(conn, sqlInsertInFlatPossGenTrnfr);
			
			helper.executeUpdate(conn, sqlDeleteWorking);
			helper.executeUpdate(conn, sqlInsertInWorkingPossGenTrfRightToLeft);
			helper.executeUpdate(conn, sqlDeleteSubspecies);
			helper.executeUpdate(conn, sqlInsertInElementsInCommon);
			helper.executeUpdate(conn, sqlDeleteNegativeNotFoundInFlat);			
			helper.executeUpdate(conn, sqlInsertInFlatPossGenTrnfr);
			commitTransaction(conn);						
		}
		catch (SQLException ex){
			rollbackTransaction(conn);
			throw new DAOWriteException(ex);
		}	
		finally{			
			closeConnectionWithoutException(conn);
		}				
	}
	
	
	@Override
	public void dropTemporaryTables(String xMapId) throws DAOException{
		Connection conn = getConnection();
		try{
			dropTemporaryTables(xMapId,conn);
		}
		finally{
			closeConnectionWithoutException(conn);
		}			
	}
	
	/**
	 * Method responsible to create the tables needed for crossmappin higher taxa 
	 * @param xMapId id of the xmap
	 */
	@Override	
	public void createTablesForXMapHigherTaxa(String xMapId) throws DAOException{
		Connection conn = getConnection();		
		List<String> sqlDDLsRollback = new ArrayList<String>();
		try{	
			XMap xMap = getXMap(xMapId,conn);
			
			String higherTaxaInCommon =  getTableNameHigherTaxaInCommon(xMap.getPrefixTableDB());


			String sqlCreateHigherTaxaInCommon="CREATE TABLE " + higherTaxaInCommon + " (" +
					" `id` int(11) NOT NULL AUTO_INCREMENT, " +
					" `taxId1` VARCHAR(60) NOT NULL," +
					" `ancestorTaxId1` VARCHAR(60) DEFAULT NULL," +
					" `ed1` int(10) NOT NULL," +
					" `taxId2` VARCHAR(60) NOT NULL," +
					" `ancestorTaxId2` VARCHAR(60) DEFAULT NULL," +
					" `ed2` int(10) NOT NULL," +
					" `confirmed` tinyint(1) NOT NULL DEFAULT 1," +
					" PRIMARY KEY (`id`), " +
					" KEY `ed1` (`ed1`), KEY `ed2` (`ed2`)," +
					" KEY `taxId1` (`taxId1`),  KEY `taxId2` (`taxId2`)," +
					" UNIQUE KEY `pairTaxa` (`taxId1`,`taxId2`)" +
					");";				
			
		    //Add ddl instructions for the rollback
		    sqlDDLsRollback.add("DROP TABLE IF EXISTS " + higherTaxaInCommon + ";");		    			
	    
		    
			startTransaction(conn);
			helper.executeUpdate(conn, sqlCreateHigherTaxaInCommon);						
			commitTransaction(conn);
		}
		catch (SQLException ex){
			rollbackTransaction(conn);
			rollbackDDLs(conn,sqlDDLsRollback);
			throw new DAOWriteException(ex);
		}	
		finally{
			closeConnectionWithoutException(conn);
		}				
	}
	
	public void reFillTableHigherTaxaInCommon(String xMapId, TaxonomicRank rank) throws DAOException{
		Connection conn = getConnection();		
		List<String> sqlDDLsRollback = new ArrayList<String>();
		try{			
			XMap xMap = getXMap(xMapId,conn);
			Checklist leftChecklist = getChecklist(xMap.getLeftChecklistId(),conn);
			Checklist rightChecklist = getChecklist(xMap.getRightChecklistId(),conn);
			
			String higherTaxaInCommon = getTableNameHigherTaxaInCommon(xMap.getPrefixTableDB());						
			String elementsInCommon = getTableNameElementsInCommon(xMap.getPrefixTableDB());
			String tempLeftAncestors = getTableNameTempAncestors(xMap.getPrefixTableDB(),1);
			String tempRightAncestors = getTableNameTempAncestors(xMap.getPrefixTableDB(),2);
			String taxon1 = getTableNameChkListTaxon(leftChecklist.getPrefixTableDB());
			String taxon2 = getTableNameChkListTaxon(rightChecklist.getPrefixTableDB());			

			String sqlDeletePreviousContent = "DELETE FROM " + higherTaxaInCommon;	
			
			//To optimize the time spent in finding the right ancestor, it is better to get all the different parents of the taxa in the
			//previous level and get the ancestor from them rather than obtaining the ancestor of each taxa.
			String sqlCreateTempLeftAncestor = " CREATE TABLE "+tempLeftAncestors+" (" +
					" taxonId varchar(60), ancestorId varchar(60)," +
					" PRIMARY KEY (`taxonId`));";			
						
			String sqlCreateTempRightAncestor = " CREATE TABLE "+tempRightAncestors+" (" +
					" taxonId varchar(60), ancestorId varchar(60)," +
					" PRIMARY KEY (`taxonId`));";				
			
			String sqlInsertTempLeftAncestor = "INSERT INTO " + tempLeftAncestors + " (taxonId)" + 
					" select distinct(t.parentId) from " + taxon1 + " as t" +
					" inner join " + elementsInCommon + " as e on e.taxId1 = t.id";
			
			String sqlInsertTempRightAncestor = "INSERT INTO " + tempRightAncestors + " (taxonId)" + 
					" select distinct(t.parentId) from " + taxon2 + " as t" +
					" inner join " + elementsInCommon + " as e on e.taxId2 = t.id";			
			
			String sqlUpdateTempLeftAncestor = "UPDATE " + tempLeftAncestors + " set ancestorId="+getFunctionNameGetAncestor(leftChecklist.getPrefixTableDB()) +"(taxonId,'" + rank.getName() +"');";
			String sqlUpdateTempRightAncestor = "UPDATE " + tempRightAncestors + " set ancestorId="+getFunctionNameGetAncestor(rightChecklist.getPrefixTableDB()) +"(taxonId,'" + rank.getName() +"');";
							
			
			//Insert into the table higherTaxaInCommon the entries in elements in common plus which are their ancestors for the desire rank			
			String sqlInsertHigherTaxaInCommon = "INSERT INTO " + higherTaxaInCommon + " (taxId1,ancestorTaxId1,ed1,taxId2,ancestorTaxId2,ed2,confirmed)" +
					" SELECT e.taxId1, a1.ancestorId, e.ed1," +
					" e.taxId2, a2.ancestorId, e.ed2, e.confirmed" +
					" FROM " + elementsInCommon + " as e" +
					"	inner join " + taxon1 + " as t1 on t1.id = e.taxId1" +
					" 	inner join " + tempLeftAncestors + " as a1 on a1.taxonId = t1.parentId" +
					" 	inner join " + taxon2 + " as t2 on t2.id = e.taxId2" + 
					" 	inner join " + tempRightAncestors + " as a2 on a2.taxonId = t2.parentId;";
			
			String sqlDropTempLeftAncestor = "DROP TABLE IF EXISTS " + tempLeftAncestors + ";";
			String sqlDropTempRightAncestor = "DROP TABLE IF EXISTS " + tempRightAncestors + ";";	
			
			 //Add ddl instruction for the rollback
		    sqlDDLsRollback.add(sqlDropTempLeftAncestor);				
		    sqlDDLsRollback.add(sqlDropTempRightAncestor);
			
			startTransaction(conn);
			helper.executeUpdate(conn, sqlDeletePreviousContent);			
			helper.executeUpdate(conn, sqlCreateTempLeftAncestor);
			helper.executeUpdate(conn, sqlCreateTempRightAncestor);
			helper.executeUpdate(conn, sqlInsertTempLeftAncestor);
			helper.executeUpdate(conn, sqlInsertTempRightAncestor);
			helper.executeUpdate(conn, sqlUpdateTempLeftAncestor);
			helper.executeUpdate(conn, sqlUpdateTempRightAncestor);			
			helper.executeUpdate(conn, sqlInsertHigherTaxaInCommon);			
			helper.executeUpdate(conn, sqlDropTempLeftAncestor);
			helper.executeUpdate(conn, sqlDropTempRightAncestor);
			commitTransaction(conn);		
		}
		catch (SQLException ex){
			rollbackTransaction(conn);
			rollbackDDLs(conn,sqlDDLsRollback);
			throw new DAOWriteException(ex);
		}	
		finally{
			closeConnectionWithoutException(conn);
		}		
	}
	
	public void cleanPreviousXMapCalculationTables(String xMapId) throws DAOException{
		Connection conn = getConnection();		
		try{			
			XMap xMap = getXMap(xMapId,conn);
									
			String elementsInCommon = getTableNameElementsInCommon(xMap.getPrefixTableDB());
			String toMany1 = getTableNameListToManys(xMap.getPrefixTableDB(),1);
			String toMany2 = getTableNameListToManys(xMap.getPrefixTableDB(),2);

			String sqlDeleteContentElementInCommon = "DELETE FROM " + elementsInCommon;	
			String sqlDeleteContentToMany1 = "DELETE FROM " + toMany1;
			String sqlDeleteContentToMany2 = "DELETE FROM " + toMany2;
												
			startTransaction(conn);
			helper.executeUpdate(conn, sqlDeleteContentElementInCommon);			
			helper.executeUpdate(conn, sqlDeleteContentToMany1);			
			helper.executeUpdate(conn, sqlDeleteContentToMany2);
			commitTransaction(conn);		
		}
		catch (SQLException ex){
			rollbackTransaction(conn);
			throw new DAOWriteException(ex);
		}	
		finally{
			closeConnectionWithoutException(conn);
		}			
	}
	
	public void fillTableElementsInCommonByCurrentLevel(String xMapId) throws DAOException{
		Connection conn = getConnection();		
		try{			
			XMap xMap = getXMap(xMapId,conn);
									
			String elementsInCommon = getTableNameElementsInCommon(xMap.getPrefixTableDB());
			String higherTaxaInCommon = getTableNameHigherTaxaInCommon(xMap.getPrefixTableDB());
			
			String sqlInsert = "INSERT IGNORE INTO " + elementsInCommon + " (taxId1,ed1,taxId2,ed2,confirmed)" +
					" SELECT ancestorTaxId1, ed1, ancestorTaxId2, ed2, confirmed" +
					" FROM " + higherTaxaInCommon + 
					" WHERE ancestorTaxId1 is not null and ancestorTaxId2 is not null and confirmed=1;";				
												
			startTransaction(conn);
			helper.executeUpdate(conn, sqlInsert);
			commitTransaction(conn);		
		}
		catch (SQLException ex){
			rollbackTransaction(conn);
			throw new DAOWriteException(ex);
		}	
		finally{
			closeConnectionWithoutException(conn);
		}			
	}
	
	@Override
	public void reevaluateHigherTaxaNotFound(String xMapId, String leftChecklistId, String rightChecklistId, TaxonomicRank rank) throws DAOException{
		Connection conn = getConnection();		
		try{	
			XMap xMap = getXMap(xMapId,conn);
			Checklist leftChecklist = getChecklist(leftChecklistId,conn);
			Checklist rightChecklist = getChecklist(rightChecklistId,conn);	
			
			String flatTable =  getTableNameXmapFlat(xMap.getPrefixTableDB());	
			String elementsInCommon = getTableNameElementsInCommon(xMap.getPrefixTableDB());
			String higherTaxaInCommon = getTableNameHigherTaxaInCommon(xMap.getPrefixTableDB());
			String nameuse1 = getTableNameChkListNameUse(leftChecklist.getPrefixTableDB());
			String taxon1 = getTableNameChkListTaxon(leftChecklist.getPrefixTableDB());
			String nameuse2 = getTableNameChkListNameUse(rightChecklist.getPrefixTableDB());
			String taxon2 = getTableNameChkListTaxon(rightChecklist.getPrefixTableDB());			
			
			//Insert possible matches in elements in common						
			String sqlInsertInElementsInCommon = "INSERT IGNORE INTO " + elementsInCommon + " (taxId1,ed1,taxId2,ed2,confirmed)" +
					" SELECT ancestorTaxId1, ed1, ancestorTaxId2, ed2, confirmed" +
					" FROM " + higherTaxaInCommon + 
					" WHERE ancestorTaxId1 is not null and ancestorTaxId2 is not null and confirmed=0;";	
			

			//Delete entries in the table flat table that had a not_found_in relationship but it has been reevaluated to poss_match
			String sqlDeleteNegativeNotFoundInFlat = "DELETE FROM " + flatTable + " USING " + flatTable + " INNER JOIN " + elementsInCommon 
					+ " WHERE " + flatTable + ".relationship = 'not_found_in' and " + elementsInCommon + ".confirmed = 0 and (" + flatTable + ".taxId1 = " + elementsInCommon + ".taxId1 or " + flatTable + ".taxId2 = " + elementsInCommon + ".taxId2);";
			
			//Add the possible matches to the flat table
			String sqlInsertInFlatPossMatches = "INSERT INTO " + flatTable +  " (accName1,uuid1,taxId1,rank1,ed1,relationship,relConfirmed,accName2,uuid2,taxId2,rank2,ed2)" +
					" SELECT n1.tidyName, t1.uuid, ec.taxId1, t1.rank, ec.ed1, 'poss_match', 0, n2.tidyName, t2.uuid, ec.taxId2, t2.rank, ec.ed2" +
					" FROM " + elementsInCommon + " as ec " +
					" 	inner join "+ nameuse1 + " as n1 on n1.taxonId = ec.taxId1" +
					" 	inner join "+ taxon1 + " as t1 on t1.id = ec.taxId1" +
					" 	inner join "+ nameuse2 + " as n2 on n2.taxonId = ec.taxId2" +
					" 	inner join "+ taxon2 + " as t2 on t2.id = ec.taxId2" +
					" WHERE ec.confirmed=0 and n1.rank='"+rank.getName()+"' and n1.status='accepted' and n2.rank='"+rank.getName()+"' and n2.status='accepted';";
			
			startTransaction(conn);
			helper.executeUpdate(conn, sqlInsertInElementsInCommon);
			helper.executeUpdate(conn, sqlDeleteNegativeNotFoundInFlat);
			helper.executeUpdate(conn, sqlInsertInFlatPossMatches);			
			commitTransaction(conn);				
		}
		catch (SQLException ex){
			rollbackTransaction(conn);
			throw new DAOWriteException(ex);
		}	
		finally{			
			closeConnectionWithoutException(conn);
		}				
	}	
	
	public void addToTableElementsInCommonEntriesWithoutAncestorForCurrentLevel(String xMapId) throws DAOException{
		Connection conn = getConnection();		
		try{			
			XMap xMap = getXMap(xMapId,conn);
									
			String elementsInCommon = getTableNameElementsInCommon(xMap.getPrefixTableDB());
			String higherTaxaInCommon = getTableNameHigherTaxaInCommon(xMap.getPrefixTableDB());
			
			String sqlInsert = "INSERT IGNORE INTO " + elementsInCommon + " (taxId1,ed1,taxId2,ed2,confirmed)" +
					" SELECT taxId1, ed1, taxId2, ed2, confirmed" +
					" FROM " + higherTaxaInCommon + 
					" WHERE ancestorTaxId1 is null or ancestorTaxId2 is null;";				
												
			startTransaction(conn);
			helper.executeUpdate(conn, sqlInsert);
			commitTransaction(conn);		
		}
		catch (SQLException ex){
			rollbackTransaction(conn);
			throw new DAOWriteException(ex);
		}	
		finally{
			closeConnectionWithoutException(conn);
		}			
	}

	
		
	@Override
	public void exportFlatTableAdditTaxa(String xMapId, CSVWriter csvWriter, boolean left) throws DAOException, IOException{
		Connection conn = getConnection();
		PreparedStatement stGetAdditTaxa = null;	
		PreparedStatement stGetInfraspeciesTaxa = null;		
		try{					
			XMap xMap = getXMap(xMapId,conn);
			
			String flatTable =  getTableNameXmapFlat(xMap.getPrefixTableDB());							
			String rawTable="";
			String field="";
			
			if (left){
				Checklist checklist = getChecklist(xMap.getLeftChecklistId(),conn);
				rawTable =  getTableNameChkListRaw(checklist.getPrefixTableDB());
				field =  "taxId1";
			}
			else{
				Checklist checklist = getChecklist(xMap.getRightChecklistId(),conn);
				rawTable =  getTableNameChkListRaw(checklist.getPrefixTableDB());
				field =  "taxId2";
			}
			
			
			String sqlGetAdditTaxa = "";
			String sqlGetInfraspeciesTaxa="";
			List<String> fields = getColumnsInTable(rawTable, conn);			
			if (fields.contains("acceptedNameUsageID")) {			
				sqlGetAdditTaxa = "SELECT * FROM "+rawTable +" WHERE (IF ((acceptedNameUsageID = ''),taxonID, acceptedNameUsageID)) IN (SELECT "+field+" FROM "+flatTable+" WHERE relationship = 'not_found_in')";
				//Add infra-species to addit taxa while they are not yet processed
				//TODO: Compare infraspecies
				sqlGetInfraspeciesTaxa = "SELECT * FROM "+rawTable +" WHERE taxonRank='infraspecies' and (IF ((acceptedNameUsageID = ''),taxonID, acceptedNameUsageID)) not IN (SELECT "+field+" FROM "+flatTable+")";
			}
			else{
				sqlGetAdditTaxa = "SELECT * FROM "+rawTable +" WHERE taxonID IN (SELECT taxId1 FROM "+flatTable+" WHERE relationship = 'not_found_in')";
				//Add infra-species to addit taxa while they are not yet processed
				//TODO: Compare infraspecies
				sqlGetInfraspeciesTaxa = "SELECT * FROM "+rawTable +" WHERE taxonRank='infraspecies' and taxonID not IN (SELECT "+field+" FROM "+flatTable+")";
			}
											
			stGetAdditTaxa = helper.createGeneratedKeysPreparedStatement(conn,sqlGetAdditTaxa);
			ResultSet rsGetAdditTaxa = stGetAdditTaxa.executeQuery();										
			csvWriter.writeAll(rsGetAdditTaxa, true);
			rsGetAdditTaxa.close();
			
			
			stGetInfraspeciesTaxa = helper.createGeneratedKeysPreparedStatement(conn,sqlGetInfraspeciesTaxa);
			ResultSet rsInfraspeciesTaxa = stGetInfraspeciesTaxa.executeQuery();										
			csvWriter.writeAll(rsInfraspeciesTaxa, false);
			rsGetAdditTaxa.close();			
			
		}
		catch(SQLException ex){
			throw new DAOReadException(ex);
		}	
		finally{
			closeStatementWithoutException(stGetAdditTaxa);
			closeStatementWithoutException(stGetInfraspeciesTaxa);
			closeConnectionWithoutException(conn);
		}			
	}
	
	@Override
	public void exportFlatTable(String xMapId, boolean includeAcceptedNames, CSVWriter csvWriter) throws DAOException, IOException{
		Connection conn = getConnection();		
		PreparedStatement st = null;
		try{					
			String[] header;
			String sql;
			
			XMap xMap = getXMap(xMapId,conn);		
			String flatTable =  getTableNameXmapFlat(xMap.getPrefixTableDB());
			
			if (includeAcceptedNames){
				header = "taxonIdLeft#checklistNameLeft#rankLeft#acceptedNameLeft#uuidLeft#relationship#taxonIdRight#checklistNameRight#rankRight#acceptedNameRight#uuidRight".split("#");
				sql ="SELECT flat.taxId1, leftChk.checklistName, flat.rank1, flat.accName1, flat.uuid1, flat.relationship, flat.taxId2, rightChk.checklistName, flat.rank2, flat.accName2, flat.uuid2 " +
					" FROM " + flatTable + " as flat inner join Checklist as leftChk on leftChk.id = flat.ed1" +
					" inner join Checklist as rightChk on rightChk.id = flat.ed2;";
			}
			else{
				header = "taxonIdLeft#checklistNameLeft#rankLeft#uuidLeft#relationship#taxonIdRight#checklistNameRight#rankRight#uuidRight".split("#");
				sql ="SELECT flat.taxId1, leftChk.checklistName, flat.rank1, flat.uuid1, flat.relationship, flat.taxId2, rightChk.checklistName, flat.rank2, flat.uuid2 " +
						" FROM " + flatTable + " as flat inner join Checklist as leftChk on leftChk.id = flat.ed1" +
						" inner join Checklist as rightChk on rightChk.id = flat.ed2;";
			}
						
			csvWriter.writeNext(header);							
			
			st = helper.createGeneratedKeysPreparedStatement(conn,sql);
			ResultSet rs = st.executeQuery();		
			csvWriter.writeAll(rs, false);
			rs.close();
		}
		catch(SQLException ex){
			throw new DAOReadException(ex);
		}		
		finally{
			closeStatementWithoutException(st);
			closeConnectionWithoutException(conn);
		}			
	}
	
	@Override
	public void exportFlatTableNameUses(String xMapId, CSVWriter csvWriter) throws DAOException, IOException{
		Connection conn = getConnection();	
		List<String> sqlDDLsRollback = new ArrayList<String>();
		PreparedStatement stGetAllNames=null;
		try{					
			String[] header = "taxonId#checklistName#scientificName#genus#epithet#infraspecificEpithet#authority#rank#taxonomicStatus".split("#");
			csvWriter.writeNext(header);
			
			XMap xMap = getXMap(xMapId,conn);
			String flatTable =  getTableNameXmapFlat(xMap.getPrefixTableDB());
			String tempNamesTable =  getTableNameTempNames(xMap.getPrefixTableDB());		
									
			String leftCheckListId = xMap.getLeftChecklistId();
			Checklist leftChecklist = getChecklist(leftCheckListId,conn);
			
			String rightCheckListId = xMap.getRightChecklistId();
			Checklist rightChecklist = getChecklist(rightCheckListId,conn);
			
			String nameUseTable1 = getTableNameChkListNameUse(leftChecklist.getPrefixTableDB());
			String nameUseTable2 = getTableNameChkListNameUse(rightChecklist.getPrefixTableDB());			
			
		
			//Sql to create a temp table to hold the names of the taxa in both checklists
			String sqlCreateTempNames = " CREATE TABLE "+tempNamesTable+" (" +
					" nameXMap varchar(255), taxonId varchar(60), checklistId int(10), checklistName varchar(50)," +
					" tidyName varchar(255), genus varchar(255), epithet varchar(255)," +
					" infraspecificEpithet varchar(255), authority varchar(255)," +
					" rank varchar(20), status varchar(20)," +
					" unique key `thekey` (`nameXMap`, `taxonId`, `checklistId`));";		
			
			// Write out all names from ed1 on LHS
			String sqlInsertEd1OnLHS = "INSERT IGNORE INTO "+tempNamesTable+
					" (nameXMap, taxonId, checklistId, checklistName, tidyName, genus, epithet, infraspecificEpithet, authority, rank, status)"+
					" SELECT n.nameXMap, n.taxonId, t.ed1, leftChk.checklistName, n.tidyName, n.higher, n.species, n.infraspecies, n.authority, n.rank, n.status"+
					"	FROM "+nameUseTable1+" as n inner join "+flatTable+" as t on n.taxonId = t.taxId1" +
					"   inner join Checklist as leftChk on leftChk.id=t.ed1" +
					"	WHERE t.ed1 = '"+leftCheckListId+"';";
						
			// Write out all names from ed2 on RHS
			String sqlInsertEd2OnRHS = "INSERT IGNORE INTO "+tempNamesTable+
					" (nameXMap, taxonId, checklistId, checklistName, tidyName, genus, epithet, infraspecificEpithet, authority, rank, status)"+
					" SELECT n.nameXMap, n.taxonId, t.ed2, rightChk.checklistName, n.tidyName, n.higher, n.species, n.infraspecies, n.authority, n.rank, n.status"+
					"	FROM "+nameUseTable2+" as n inner join "+flatTable+" as t on n.taxonId = t.taxId2"+
					"   inner join Checklist as rightChk on rightChk.id=t.ed2" +
					"	WHERE t.taxId2!=-1 AND t.ed2 = '"+rightCheckListId+"';";
			

			String sqlGetAllNames = "SELECT taxonId, checklistName, tidyName, genus, epithet," +
										" infraspecificEpithet, authority, rank, status" +
										" from " + tempNamesTable + ";";
			
			String sqlDropTmpNamesTable = "DROP TABLE IF EXISTS " + tempNamesTable + ";";
								
		    //Add ddl instruction for the rollback
		    sqlDDLsRollback.add(sqlDropTmpNamesTable);	
			
			startTransaction(conn);
			try{					
				helper.executeUpdate(conn, sqlCreateTempNames);
				helper.executeUpdate(conn, sqlInsertEd1OnLHS);
				//ACJ MODIFIED 21 JULY 2012. NO LONGER REQUIRED, NOW THAT ALL ed1s ARE ON LHS AND ALL ed2s ARE ON RHS
				//helper.executeUpdate(conn, sqlInsertEd1OnRHS);
				//helper.executeUpdate(conn, sqlInsertEd2OnLHS);
				helper.executeUpdate(conn, sqlInsertEd2OnRHS);
				stGetAllNames = helper.createGeneratedKeysPreparedStatement(conn,sqlGetAllNames);
				ResultSet rsAllNames = stGetAllNames.executeQuery();					
				csvWriter.writeAll(rsAllNames, false);
				rsAllNames.close();
				helper.executeUpdate(conn, sqlDropTmpNamesTable);
				commitTransaction(conn);
			}
			catch(SQLException ex){
				rollbackTransaction(conn);
				rollbackDDLs(conn,sqlDDLsRollback);
				throw ex;
			}				
		}
		catch(SQLException ex){			
			throw new DAOWriteException(ex);
		}		
		finally{
			closeStatementWithoutException(stGetAllNames);			
			closeConnectionWithoutException(conn);
		}			
	}
	
		
	@Override
	public ChecklistSeq getChecklists(String user, String scope, TaskStatus status) throws DAOException  {
		ChecklistSeq checklists = new ChecklistSeq();
		Connection conn = getConnection();
		PreparedStatement st=null;
		try{			
			String sql = "select chk.id,chk.checklistName,chk.fileName,chk.user,chk.scope,chk.prefixTableDB,chk.taskId,t.status" +
					" from Checklist as chk inner join Task as t on t.id = chk.taskId" +
					" where chk.user=? and chk.scope=?" + (status!=null?" and t.status=?":"");
			
			st = helper.createGeneratedKeysPreparedStatement(conn,sql);
			st.setString(1, user);
			st.setString(2, scope);
			if (status!=null){
				st.setString(3, status.value());
			}
			ResultSet rs = st.executeQuery();
            while (rs.next()){
            	Checklist checklist = parseChecklist(rs);
            	checklists.getChecklist().add(checklist);		            	
            } 
            rs.close();
            
            return checklists;
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
	public XMapSeq getCrossMaps(String user, String scope, TaskStatus status) throws DAOException{
		Connection conn = getConnection();
		PreparedStatement st = null;
		XMapSeq crossMaps = new XMapSeq();
		try{
			String sqlGetCrossMap = "select xmap.id,xmap.shortname,xmap.user,xmap.scope,xmap.longname," +
					" xmap.leftChecklistId,leftChk.checklistName as leftChecklistName,xmap.rightChecklistId,rightChk.checklistName as rightChecklistName," +
					" xmap.strict,xmap.identifyExtraTaxa,xmap.compareHigherTaxa,xmap.highestRankIdToCompare," +
					" xmap.prefixTableDB,xmap.taskId,t.status" +
					" from Crossmap as xmap inner join Task as t on t.id = xmap.taskId" +
					" inner join Checklist as leftChk on leftChk.id = xmap.leftChecklistId" +
					" inner join Checklist as rightChk on rightChk.id = xmap.rightChecklistId" +
                    " where xmap.user=? and xmap.scope=?" + (status!=null?" and t.status=?":"");		
			
			st = helper.createGeneratedKeysPreparedStatement(conn,sqlGetCrossMap);
			st.setString(1, user);
			st.setString(2, scope);
			if (status!=null){
				st.setString(3, status.value());
			}
			
			ResultSet rs = st.executeQuery();
            while (rs.next()){
            	XMap xmap = parseXMap(rs);
            	crossMaps.getXMap().add(xmap);            	
            }
            rs.close();
            
            return crossMaps;			
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
	 * Mehtod responsible to return a checklist given its id
	 * @param checklistId
	 * @return checklist given its id
	 * @throws DAOException 
	 */
	public Checklist getChecklist(String checklistId) throws DAOException{
		Connection conn = getConnection();
		try{
			return getChecklist(checklistId,conn);
		}
		finally{
			closeConnectionWithoutException(conn);
		}				
	}		
	
	/**
	 * Method responsible to return a XMap given its id
	 * @param xMapId
	 * @return xmap object given its id
	 * @throws DAOException 
	 */
	@Override
	public XMap getCrossMap(String xMapId) throws DAOException{
		Connection conn = getConnection();		
		try{
			return getXMap(xMapId,conn);
		}
		finally{
			closeConnectionWithoutException(conn);
		}				
	}	
	
	
		
	@Override
	public ResultExportXmapSeq getCrossMapResultsExported(String user, String scope, TaskStatus status) throws DAOException {
		Connection conn = getConnection();
		PreparedStatement st = null;
		ResultExportXmapSeq xMapResluts = new ResultExportXmapSeq();
		try{
			String sqlGetCrossMap = "select exp.id,exp.name,exp.xMapId,exp.includeAcceptedNames,exp.exportDate,exp.user,exp.scope,exp.resultFileURL,exp.taskId,t.status" +
					" from ExportCrossmapResult as exp inner join Task as t on t.id = exp.taskId" +
					" where exp.user=? and exp.scope=?" + (status!=null?" and t.status=?":"");
			
			st = helper.createGeneratedKeysPreparedStatement(conn,sqlGetCrossMap);
			st.setString(1, user);
			st.setString(2, scope);
			if (status!=null){
				st.setString(3, status.value());
			}
			
			ResultSet rs = st.executeQuery();
            while (rs.next()){
            	ResultExportXmap xMapReslut = parseXMapResult(rs);
            	xMapResluts.getExportResultXmap().add(xMapReslut);            	
            }
            rs.close();
            
            return xMapResluts;			
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
	public ResultExportXmap getCrossMapResult(String xMapResultId) throws DAOException {
		Connection conn = getConnection();		
		try{
			return getXMapResult(xMapResultId,conn);
		}
		finally{
			closeConnectionWithoutException(conn);
		}			
	}
	
	@Override
	public boolean existXMapResultInRepository(String xMapId,String user,String scope) throws DAOException{
		Connection conn = getConnection();
		PreparedStatement st = null;
		boolean existXMapResult;
		try{
			String sqlGetCrossMapByName = "select id from ExportCrossmapResult where xMapId=? and user=? and scope=?";			
			
			st = helper.createGeneratedKeysPreparedStatement(conn,sqlGetCrossMapByName);
			st.setString(1, xMapId);
			st.setString(2, user);
			st.setString(3, scope);
			
			ResultSet rs = st.executeQuery();
            
			if (rs.next()){
				existXMapResult=true;
			}
			else{
				existXMapResult=false;
			}						
            rs.close();
            
            return existXMapResult;
			
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
	public String addXMapResultToRepository(String name, String xMapId, boolean includeAcceptedNames, String user, 
			String scope, TaskId taskId) throws DAOException{
		
		Connection conn = getConnection();	
		PreparedStatement st = null;
		try{				
			String sqlInsertXMap = "INSERT INTO ExportCrossmapResult(name,xMapId,includeAcceptedNames,exportDate,user,scope,taskId) " +
					" VALUES (?,?,?,?,?,?,?);";
			
			st = helper.createGeneratedKeysPreparedStatement(conn,sqlInsertXMap);
			st.setString(1, name);
			st.setString(2,xMapId);
			st.setBoolean(3,includeAcceptedNames);
			st.setTimestamp(4, new java.sql.Timestamp(new java.util.Date().getTime()));					
			st.setString(5,user);
			st.setString(6,scope);
			st.setString(7, taskId.getValue());
			
			st.executeUpdate();
			
			ResultSet rs = st.getGeneratedKeys();
            if ( !rs.next() ) {
                throw new RuntimeException("cannot obtain id of xmap");
            }
            return rs.getString(1);
            
		}
		catch (SQLException ex){
			throw new DAOWriteException(ex);
		}	
		finally{
			closeStatementWithoutException(st);
			closeConnectionWithoutException(conn);
		}			
	}
	
	@Override
	public void updateExportResultCrossmap(String xMapResultId, String resultFileURL) throws DAOException{
		Connection conn = getConnection();
		PreparedStatement st = null;
		try{
			String sqlUpdateStatus = "UPDATE ExportCrossmapResult set resultFileURL = ? where id = ?;";			
			
			st = helper.createGeneratedKeysPreparedStatement(conn,sqlUpdateStatus);			
			if (resultFileURL!=null){
				st.setString(1, resultFileURL);
			}
			else {
				st.setNull(1,java.sql.Types.VARCHAR);
			}			
			st.setString(2, xMapResultId);
			
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
	
	@Override
	public void deleteImportedChecklist(String checklistId) throws DAOException {		
		Connection conn = getConnection();		
		try{
			Checklist checklist = getChecklist(checklistId,conn);
					
			String tableNameUse = getTableNameChkListNameUse(checklist.getPrefixTableDB());
			String viewTableNameUse = getViewNameChkListNameUse(checklist.getPrefixTableDB());
			String tableTaxon = getTableNameChkListTaxon(checklist.getPrefixTableDB());
			String tableRaw = getTableNameChkListRaw(checklist.getPrefixTableDB());
			String funcGetAncestor = getFunctionNameGetAncestor(checklist.getPrefixTableDB()); 
					
			String sqlDropViewTableNameUse = "DROP VIEW IF EXISTS " + viewTableNameUse;
			String sqlDropTableNameUse = "DROP TABLE IF EXISTS " + tableNameUse;
			String sqlDropTableTaxon = "DROP TABLE IF EXISTS " + tableTaxon;
			String sqlDropTableTaxa = "DROP TABLE IF EXISTS " + tableRaw;
			String sqlDropFuncGetAncestor = "DROP FUNCTION IF EXISTS " + funcGetAncestor;
			String sqlDeleteEntryCrossMap = "delete from Checklist where id='" + checklistId +"'";
															
			startTransaction(conn);			
			helper.executeUpdate(conn, sqlDropViewTableNameUse);
			helper.executeUpdate(conn, sqlDropTableNameUse);				
			helper.executeUpdate(conn, sqlDropTableTaxon);
			helper.executeUpdate(conn, sqlDropTableTaxa);			
			helper.executeUpdate(conn, sqlDropFuncGetAncestor);
			helper.executeUpdate(conn, sqlDeleteEntryCrossMap);			
			commitTransaction(conn);
		}
		catch (SQLException ex){
			rollbackTransaction(conn);
			throw new DAOWriteException(ex);			
		}	
		finally{
			closeConnectionWithoutException(conn);
		}		
	}
	
	
	@Override
	public void deleteCrossMap(String xMapId, boolean includingEntryInRepository) throws DAOException {
		Connection conn = getConnection();		
		try{
			XMap xMap = getXMap(xMapId,conn);	
					
			String flatTable = getTableNameXmapFlat(xMap.getPrefixTableDB());		
			String viewFlatTable = getViewNameXmapFlatTable(xMap.getPrefixTableDB());		
			String nameInCommon = getTableNameElementsInCommon(xMap.getPrefixTableDB());
			String higherTaxaInCommon = getTableNameHigherTaxaInCommon(xMap.getPrefixTableDB());
			String list1tomanys =  getTableNameListToManys(xMap.getPrefixTableDB(),1);
			String list2tomanys = getTableNameListToManys(xMap.getPrefixTableDB(),2);					
			String working = getTableNameWorking(xMap.getPrefixTableDB());
			String xmapRefineInfo = getTableNameXmapRefineInfo(xMap.getPrefixTableDB());
			
			String sqlDropViewFlatTable = "DROP VIEW IF EXISTS " + viewFlatTable;
			String sqlDropFlatTable = "DROP TABLE IF EXISTS " + flatTable;			
			String sqlDropWorkingTable = "DROP TABLE IF EXISTS " + working;					
			String sqlDropList1ToManysTable = "DROP TABLE IF EXISTS " + list1tomanys;
			String sqlDropList2ToManysTable = "DROP TABLE IF EXISTS " + list2tomanys;
			String sqlDropNameInCommonTable = "DROP TABLE IF EXISTS " + nameInCommon;
			String sqlDropHigherTaxaInCommonTable = "DROP TABLE IF EXISTS " + higherTaxaInCommon;
			String sqlDropRefineInfo = "DROP TABLE IF EXISTS " + xmapRefineInfo;			
			String sqlDeleteEntryCrossMap = "delete from Crossmap where id='" + xMapId +"'";			
														
			startTransaction(conn);									
			helper.executeUpdate(conn, sqlDropWorkingTable);
			helper.executeUpdate(conn, sqlDropList1ToManysTable);
			helper.executeUpdate(conn, sqlDropList2ToManysTable);
			helper.executeUpdate(conn, sqlDropNameInCommonTable);
			helper.executeUpdate(conn, sqlDropHigherTaxaInCommonTable);
			helper.executeUpdate(conn, sqlDropRefineInfo);
			helper.executeUpdate(conn, sqlDropViewFlatTable);	
			helper.executeUpdate(conn, sqlDropFlatTable);
			if (includingEntryInRepository){
				helper.executeUpdate(conn, sqlDeleteEntryCrossMap);
			}
			commitTransaction(conn);
		}
		catch (SQLException ex){
			rollbackTransaction(conn);
			throw new DAOWriteException(ex);			
		}	
		finally{
			closeConnectionWithoutException(conn);
		}		
	}

	@Override
	public void deleteCrossMapResult(String xMapResultId) throws DAOException {
		Connection conn = getConnection();		
		try{
			ResultExportXmap resultExpXMap = getXMapResult(xMapResultId,conn);
			XMap xMap = getXMap(resultExpXMap.getXMapId(),conn);
			
			String tempNamesTable =  getTableNameTempNames(xMap.getPrefixTableDB());
						
			String sqlDropTempNamesTable = "drop table if exists " + tempNamesTable + ";";
			String sqlDeleteEntryXMapResult = "delete from ExportCrossmapResult where id='" + xMapResultId + "'";
			
			
			startTransaction(conn);		
			helper.executeUpdate(conn, sqlDropTempNamesTable);
			helper.executeUpdate(conn, sqlDeleteEntryXMapResult);
			commitTransaction(conn);	
		}
		catch (SQLException ex){
			rollbackTransaction(conn);
			throw new DAOWriteException(ex);			
		}	
		finally{
			closeConnectionWithoutException(conn);
		}		
	}
	
	@Override	
	public XMapSeq getCrossMapsByChecklist(String checklistId) throws DAOException{
		Connection conn = getConnection();
		PreparedStatement st = null;
		XMapSeq crossMaps = new XMapSeq();
		try{
			String sqlGetCrossMap = "select xmap.id,xmap.shortname,xmap.user,xmap.scope,xmap.longname," +
					" xmap.leftChecklistId,leftChk.checklistName as leftChecklistName,xmap.rightChecklistId,rightChk.checklistName as rightChecklistName," +
					" xmap.strict,xmap.identifyExtraTaxa,xmap.compareHigherTaxa,xmap.highestRankIdToCompare," +
					" xmap.prefixTableDB,xmap.taskId,t.status" +
					" from Crossmap as xmap inner join Task as t on t.id = xmap.taskId" +
					" inner join Checklist as leftChk on leftChk.id = xmap.leftChecklistId" +
					" inner join Checklist as rightChk on rightChk.id = xmap.rightChecklistId" +
                    " where (xmap.leftChecklistId=? or xmap.rightChecklistId=?)  and t.status!=?";			
			
			st = helper.createGeneratedKeysPreparedStatement(conn,sqlGetCrossMap);
			st.setString(1, checklistId);
			st.setString(2, checklistId);
			st.setString(3, TaskStatus.FAILED.value());
			
			ResultSet rs = st.executeQuery();
            while (rs.next()){
            	XMap xmap = parseXMap(rs);
            	crossMaps.getXMap().add(xmap);            	
            }
            rs.close();
            
            return crossMaps;			
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
	public ResultExportXmapSeq getCrossMapsExportResultsByXmap(String xMapId) throws DAOException{
		Connection conn = getConnection();
		PreparedStatement st = null;
		ResultExportXmapSeq xMapResluts = new ResultExportXmapSeq();
		try{
			String sqlGetResCrossMap = "select exp.id,exp.name,exp.xMapId,exp.includeAcceptedNames,exp.exportDate,exp.user,exp.scope,exp.resultFileURL,exp.taskId,t.status" +
					" from ExportCrossmapResult as exp inner join Task as t on t.id = exp.taskId" +
					" where exp.xMapId=? and t.status!=?";			
			
			st = helper.createGeneratedKeysPreparedStatement(conn,sqlGetResCrossMap);
			st.setString(1, xMapId);
			st.setString(2, TaskStatus.FAILED.value());
			
			ResultSet rs = st.executeQuery();
            while (rs.next()){
            	ResultExportXmap xMapReslut = parseXMapResult(rs);
            	xMapResluts.getExportResultXmap().add(xMapReslut);            	
            }
            rs.close();
            
            return xMapResluts;			
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
	public EntriesInChecklistSeq getEntriesInChecklist(String checklistId, DataRetrievalFilterSeq filters, DataRetrievalSortSeq sorts, int start, int end)  throws DAOException{		
		Connection conn = getConnection();
		PreparedStatement st = null;
		EntriesInChecklistSeq entries = new EntriesInChecklistSeq();
		try{
			
			if (start<0){
				throw new RuntimeException("The value for 'start' should be positive");
			}
			else if (end!=-1 && end<start){
				throw new RuntimeException("The value for 'end' should be -1 or greater or equals than start");
			}			
						
			Checklist checklist = getChecklist(checklistId,conn);
			String viewNameUse = getViewNameChkListNameUse(checklist.getPrefixTableDB());			
						
			//Get the total of entries that match the filter
			int totalEntries = countElementsInTable(viewNameUse,filters,conn);

			//Calculate limit and offset values for the sql 
			int limit = end==-1?totalEntries:end-start;
			int offset = start;
				
			entries.setStart(start);
			entries.setEnd(end);
			entries.setTotalEntries(totalEntries);	
			entries.setReturnedEntries(start>=totalEntries?0:Math.min(offset+limit,totalEntries)-start);
					
			
			String sqlGetEntries = "select * from " + viewNameUse + getSQLFilterPart(filters) + getSQLSortPart(sorts) + " limit " + limit + " offset " + offset + ";";
									
			st = helper.createGeneratedKeysPreparedStatement(conn,sqlGetEntries);			
			ResultSet rs = st.executeQuery();
            while (rs.next()){
            	EntryInChecklist entry = parseEntryInChecklist(rs);
            	entries.getEntry().add(entry);            	
            }
            rs.close();
            
            return entries;			
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
	public EntriesInXMapSeq getEntriesInXMap(String xMapId, DataRetrievalFilterSeq filters, DataRetrievalSortSeq sorts, int start, int end)  throws DAOException{		
		Connection conn = getConnection();
		PreparedStatement st = null;
		EntriesInXMapSeq entries = new EntriesInXMapSeq();
		try{
			
			if (start<0){
				throw new RuntimeException("The value for 'start' should be positive");
			}
			else if (end!=-1 && end<start){
				throw new RuntimeException("The value for 'end' should be -1 or greater or equals than start");
			}
			
			XMap xmap = getXMap(xMapId,conn);
			String viewFlat = getViewNameXmapFlatTable(xmap.getPrefixTableDB());			
			
			int totalEntries = countElementsInTable(viewFlat,filters,conn);
			int limit = end==-1?totalEntries:end-start;
			int offset = start;
							
			entries.setStart(start);
			entries.setEnd(end);
			entries.setTotalEntries(totalEntries);	
			entries.setReturnedEntries(start>=totalEntries?0:Math.min(offset+limit,totalEntries)-start);	
			
			String sqlGetEntries = "select * from " + viewFlat + getSQLFilterPart(filters) + getSQLSortPart(sorts) + " limit " + limit + " offset " + offset + ";";
									
			st = helper.createGeneratedKeysPreparedStatement(conn,sqlGetEntries);			
			ResultSet rs = st.executeQuery();
            while (rs.next()){
            	EntryInXMap entry = parseEntryInXMap(rs);
            	entries.getEntry().add(entry);            	
            }
            rs.close();
            
            return entries;			
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
	public EntriesInChecklistSeq getEntriesInChecklistForTaxon(String checklistId, String taxonId, String status) throws DAOException {
		Connection conn = getConnection();
		PreparedStatement st = null;
		EntriesInChecklistSeq entries = new EntriesInChecklistSeq();
		try{
			Checklist checklist = getChecklist(checklistId,conn);
			String viewNameUse = getViewNameChkListNameUse(checklist.getPrefixTableDB());				
			
			String sqlGetEntries = "select * from " + viewNameUse + " where taxonId=? and status=?;";									
			st = helper.createGeneratedKeysPreparedStatement(conn,sqlGetEntries);			
			st.setString(1, taxonId);
			st.setString(2, status);
			
			ResultSet rs = st.executeQuery();
            while (rs.next()){
            	EntryInChecklist entry = parseEntryInChecklist(rs);
            	entries.getEntry().add(entry);            	
            }
            rs.close();
            
            return entries;			
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
	public EntriesInXMapSeq getEntriesInXMapForTaxon(String xMapId, String taxonId, boolean left)  throws DAOException{		
		Connection conn = getConnection();
		PreparedStatement st = null;
		EntriesInXMapSeq entries = new EntriesInXMapSeq();
		try{			
			XMap xmap = getXMap(xMapId,conn);
			String viewFlat = getViewNameXmapFlatTable(xmap.getPrefixTableDB());			
			
			String sqlGetEntries = "select * from " + viewFlat + " where "  + (left?"taxonIdLeft":"taxonIdRight") + "=?;";									
			st = helper.createGeneratedKeysPreparedStatement(conn,sqlGetEntries);			
			st.setString(1, taxonId);
																
			ResultSet rs = st.executeQuery();
            while (rs.next()){
            	EntryInXMap entry = parseEntryInXMap(rs);
            	if (!left){
            		if (entry.getRelationship().equalsIgnoreCase("includes")){
            			entry.setRelationship("included_by");
            		}
            		else if (entry.getRelationship().equalsIgnoreCase("included_by")){
            			entry.setRelationship("includes");
            		}
            	}            	
            	entries.getEntry().add(entry);            	
            }
            rs.close();
            
            return entries;			
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
	public List<String> getTaxonChildrenId(String checklistId, String taxonId) throws DAOException {
		Connection conn = getConnection();
		PreparedStatement st = null;
		List<String> children = new ArrayList<String>();
		try{
			Checklist checklist = getChecklist(checklistId,conn);
			String taxonTable = getTableNameChkListTaxon(checklist.getPrefixTableDB());
			
			String sqlGetChildren = "select id from " + taxonTable + " where parentId=? and id!=parentId;";									
			st = helper.createGeneratedKeysPreparedStatement(conn,sqlGetChildren);			
			st.setString(1, taxonId);
						
			ResultSet rs = st.executeQuery();
            while (rs.next()){
            	children.add(rs.getString("id"));
            }
            rs.close();
            
            return children;			
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
	public List<String> getPathTaxonInChecklist(String checklistId, String taxonId) throws DAOException {
		Connection conn = getConnection();
		PreparedStatement st = null;
		List<String> path = new ArrayList<String>();
		try{
			Checklist checklist = getChecklist(checklistId,conn);
			String taxonTable = getTableNameChkListTaxon(checklist.getPrefixTableDB());
			
			path.add(taxonId);
			
			String auxTaxonId = taxonId;			
			while (!auxTaxonId.equalsIgnoreCase("0")){
				String sqlGetParent = "select parentId from " + taxonTable + " where id=? and parentId!=id;";									
				st = helper.createGeneratedKeysPreparedStatement(conn,sqlGetParent);			
				st.setString(1, auxTaxonId);
							
				ResultSet rs = st.executeQuery();
				if (rs.next()){
					auxTaxonId = rs.getString("parentId");
					path.add(auxTaxonId);
				}
				else{
					auxTaxonId = "0";
				}
			}            	
            
            return path;			
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
	public RawData getTaxonRawData(String checklistId, String taxonId) 	throws DAOException {
		Connection conn = getConnection();
		PreparedStatement st = null;
		RawData rawData = new RawData();
		
		RawDataRegisterSeq registers = new RawDataRegisterSeq();
		try{
			Checklist checklist = getChecklist(checklistId,conn);
			String rawTable = getTableNameChkListRaw(checklist.getPrefixTableDB());		
			
			List<String> fields = getColumnsInTable(rawTable, conn);			
			if (fields.contains("acceptedNameUsageID")) {
				String sql = "select * from " + rawTable + " where taxonID=? or acceptedNameUsageID=?;";									
				st = helper.createGeneratedKeysPreparedStatement(conn,sql);			
				st.setString(1, taxonId);
				st.setString(2, taxonId);
			}else{
				String sql = "select * from " + rawTable + " where taxonID=?;";									
				st = helper.createGeneratedKeysPreparedStatement(conn,sql);			
				st.setString(1, taxonId);
			}
			
			ResultSet rs = st.executeQuery();
            while (rs.next()){
            	RawDataRegister register = parseRawDataRegister(rs);
            	registers.getRegister().add(register);   	
            }
            rs.close();
            
            rawData.setRegisters(registers);
            
            return rawData;			
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
	public RelationshipDetail getXMapRelationshipDetail(String xMapId, String leftTaxonId, String rightTaxonId) throws DAOException{
		Connection conn = getConnection();		
		try{		
			RelationshipDetail relationshipDetail = new RelationshipDetail();
			
			startTransaction(conn);		
			
            //Get all taxa in the right checklist related to leftTaxonId
            List<String> taxaInRightRelatedToTaxonLeft = getTaxaInRightRelatedToTaxon(xMapId,leftTaxonId,conn); 
            
            //Get all taxa in the left checklist related to rightTaxonId
            List<String> taxaInLeftRelatedToTaxonRight = getTaxaInLeftRelatedToTaxon(xMapId,rightTaxonId,conn);             
            
            //Get the direct relationship between the pair of taxa received. In other words the relationship between its elements without considering any other taxa
            RelationshipPairTaxa directRel = getDetailsIsolatedRelationshipPairTaxa(xMapId,leftTaxonId,rightTaxonId,conn);
            relationshipDetail.setDirectRelationship(directRel);
            
            //Get relationship between the leftTaxonId and other taxa in the right checklist
            List<RelationshipPairTaxa> pairsIndirectRelL2R = new ArrayList<RelationshipPairTaxa>();
            for (String otherRightTaxonId:taxaInRightRelatedToTaxonLeft){
            	if (!otherRightTaxonId.equalsIgnoreCase(rightTaxonId)){
            		RelationshipPairTaxa indirectRel = getDetailsIsolatedRelationshipPairTaxa(xMapId,leftTaxonId,otherRightTaxonId,conn);		
            		pairsIndirectRelL2R.add(indirectRel);
            	}
            }
            relationshipDetail.getIndirectRelationshipLeft2Right().addAll(pairsIndirectRelL2R);
            
            //Get relationship between the rightTaxonId and other taxa in the left checklist
            List<RelationshipPairTaxa> pairsIndirectRelR2L = new ArrayList<RelationshipPairTaxa>();
            for (String otherLeftTaxonId:taxaInLeftRelatedToTaxonRight){
            	if (!otherLeftTaxonId.equalsIgnoreCase(leftTaxonId)){
            		RelationshipPairTaxa indirectRel = getDetailsIsolatedRelationshipPairTaxa(xMapId,otherLeftTaxonId,rightTaxonId,conn);		
            		pairsIndirectRelR2L.add(indirectRel);
            	}
            }
            relationshipDetail.getIndirectRelationshipRight2Left().addAll(pairsIndirectRelR2L);	
                        						
			
			commitTransaction(conn);	
			
			return relationshipDetail;		
		}
		catch (Exception ex){
			rollbackTransaction(conn);			
			throw new DAOWriteException(ex);			
		}	
		finally{					
			closeConnectionWithoutException(conn);
		}
	}
		
		
	@Override
	public TaxonomicRank getNextTaxonomicRank(TaxonomicRank rank, boolean up) throws DAOException{
		Connection conn = getConnection();
		try{
			return getNextTaxonomicRank(rank,up,conn);
		}
		finally{
			closeConnectionWithoutException(conn);
		}		
	}
	
	
	
	@Override
	public int compareTaxonomicRanks(TaxonomicRank rankA, TaxonomicRank rankB) throws DAOException{
		Connection conn = getConnection();
		try{
			return compareTaxonomicRanks(rankA,rankB,conn);
		}
		finally{
			closeConnectionWithoutException(conn);
		}		
	}	
		
	
	@Override
	public TaxonomicRankSeq getTaxonomicRanks() throws DAOException{
		TaxonomicRankSeq ranks = new TaxonomicRankSeq();
		Connection conn = getConnection();
		PreparedStatement st=null;
		try{			
			String sql = "select id,rank,parentId,isHigherRank from TaxonomicRankHierarchy;";			
			st = helper.createGeneratedKeysPreparedStatement(conn,sql);
			ResultSet rs = st.executeQuery();
            while (rs.next()){
            	TaxonomicRank rank = parseTaxonomicRank(rs);
            	ranks.getRank().add(rank);		            	
            } 
            rs.close();
            
            return ranks;
		}
		catch (SQLException ex){
			throw new DAOReadException(ex);	
		}	
		finally{
			closeStatementWithoutException(st);
			closeConnectionWithoutException(conn);
		}		
	}
	
	public TaxonomicRank getTaxonomicRankByName(String name) throws DAOException{		
		Connection conn = getConnection();
		try{			
			return getTaxonomicRankByName(name,conn);
		}
		finally{
			closeConnectionWithoutException(conn);
		}			
	}

	
	public TaxonomicRankSeq getTaxonomicRanksInChecklist(String checklistId) throws DAOException{
		TaxonomicRankSeq ranks = new TaxonomicRankSeq();
		Connection conn = getConnection();
		try{			
			Checklist checklist = getChecklist(checklistId, conn);
			String taxonTable = getTableNameChkListTaxon(checklist.getPrefixTableDB());
			
			String sql = "select id,rank,parentId,isHigherRank " +
					"from TaxonomicRankHierarchy " +
					"where rank in (select distinct rank from " + taxonTable + ");";
			
			PreparedStatement  st = helper.createGeneratedKeysPreparedStatement(conn,sql);			
			ResultSet rs = st.executeQuery();
            while (rs.next()){
            	TaxonomicRank rank = parseTaxonomicRank(rs);	
            	ranks.getRank().add(rank);            	
            }
            rs.close();
            
            return ranks;			
		}
		catch (SQLException ex){
			throw new DAOReadException(ex);	
		}	
		finally{
			closeConnectionWithoutException(conn);
		}				
	}
		
	
	@Override
	public EntryInUserKnowledge getEntryInUserKnowledge(String userKnowledgeId) throws DAOException {
		Connection conn = getConnection();	
		PreparedStatement st = null;
		try{			
			String sql = "select knowledge.*, xMap.shortname as xMapName from UserKnowledge as knowledge" +
					" left join Crossmap as xMap on xMap.id=knowledge.xMapId where knowledge.id=?;";			
			
			st = helper.createGeneratedKeysPreparedStatement(conn,sql);		
			st.setString(1, userKnowledgeId);			
						
			ResultSet rs = st.executeQuery();            
			if (rs.next()){
				EntryInUserKnowledge entry = parseEntryInUserKnowledge(rs);
				return entry;
			}	
			else{
				return null;
			}
			
		}
		catch (SQLException ex){
			throw new DAOWriteException(ex);			
		}	
		finally{
			closeStatementWithoutException(st);
			closeConnectionWithoutException(conn);
		}		
	}


	@Override
	public EntriesInUserKnowledgeSeq getEntriesInUserKnowledge(DataRetrievalFilterSeq filters, DataRetrievalSortSeq sorts, int start, int end) throws DAOException {
		Connection conn = getConnection();
		PreparedStatement st = null;
		EntriesInUserKnowledgeSeq entries = new EntriesInUserKnowledgeSeq();
		try{
			
			if (start<0){
				throw new RuntimeException("The value for 'start' should be positive");
			}
			else if (end!=-1 && end<start){
				throw new RuntimeException("The value for 'end' should be -1 or greater or equals than start");
			}			
				
			//Get the total of entries that match the filter
			int totalEntries = countElementsInTable("UserKnowledge",filters,conn);

			//Calculate limit and offset values for the sql 
			int limit = end==-1?totalEntries:end-start;
			int offset = start;
				
			entries.setStart(start);
			entries.setEnd(end);
			entries.setTotalEntries(totalEntries);	
			entries.setReturnedEntries(start>=totalEntries?0:Math.min(offset+limit,totalEntries)-start);
					
			
			String sqlGetEntries = "select knowledge.*, xMap.shortname as xMapName from UserKnowledge as knowledge" +
					" left join Crossmap as xMap on xMap.id=knowledge.xMapId " +
					getSQLFilterPart(filters) + getSQLSortPart(sorts) + " limit " + limit + " offset " + offset + ";";
									
			st = helper.createGeneratedKeysPreparedStatement(conn,sqlGetEntries);			
			ResultSet rs = st.executeQuery();
            while (rs.next()){
            	EntryInUserKnowledge entry = parseEntryInUserKnowledge(rs);
            	entries.getEntry().add(entry);            	
            }
            rs.close();
            
            return entries;			
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
	public String addEntryInUserKnowledge(EntryInUserKnowledge entry) throws DAOException {
		Connection conn = getConnection();	
		PreparedStatement st = null;
		try{				
			String sqlInsertXMap = "INSERT INTO UserKnowledge (name1,relationship,name2,xMapId,user,scope,comment) " +
					" VALUES (?,?,?,?,?,?,?);";
			
			st = helper.createGeneratedKeysPreparedStatement(conn,sqlInsertXMap);
			st.setString(1,entry.getName1());
			st.setString(2,entry.getRelationship().value());
			st.setString(3,entry.getName2());
			st.setString(4,entry.getXMapId());		
			st.setString(5,entry.getUser());
			st.setString(6,entry.getScope());
			st.setString(7,entry.getComment());
			
			st.executeUpdate();
			
			ResultSet rs = st.getGeneratedKeys();
            if ( !rs.next() ) {
                throw new RuntimeException("cannot obtain id of user knowledge");
            }
            return rs.getString(1);
            
		}
		catch (SQLException ex){
			throw new DAOWriteException(ex);
		}	
		finally{
			closeStatementWithoutException(st);
			closeConnectionWithoutException(conn);
		}		
	}


	@Override
	public void deleteEntryInUserKnowledge(String userKnowledgeId) throws DAOException {
		Connection conn = getConnection();		
		PreparedStatement st = null;
		try{			
			String sql = "delete from UserKnowledge where id=?";				
			st = helper.createGeneratedKeysPreparedStatement(conn,sql);		
			st.setString(1, userKnowledgeId);		
			st.execute();		
		}
		catch (SQLException ex){
			throw new DAOWriteException(ex);			
		}	
		finally{
			closeStatementWithoutException(st);
			closeConnectionWithoutException(conn);
		}		
	}


	public void startTransaction() throws DAOException{
		Connection connTx = getConnection();
		startTransaction(connTx);
	}
	
	public void commitTransaction() throws DAOException{
		Connection connTx = getConnection();
		isOuterMostTransaction = true;
		try{			
			commitTransaction(connTx);
		}
		catch(DAOException ex){
			rollbackTransaction(connTx);
		}
		finally{
			closeConnection(connTx);			
		}
	}
	
	public void rollbackTransaction() throws DAOException{
		Connection connTx = getConnection();
		isOuterMostTransaction = true;
		rollbackTransaction(connTx);
		closeConnection(connTx);
	}
	
			
	/****************************************************************************************/
	/* PROTECTED METHODS																	*/														
	/****************************************************************************************/		
		
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
	    	//If we don't have already a connection, we obtain one from the pool of the datasource
	    	//in other case we return the existing connection (that probably has set autocommit to false)
	        if (conn==null){
	        	conn = ds.getConnection();
	        }
	        isOuterMostTransaction = conn.getAutoCommit();
	        
	        return conn;
	        
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
	        if (isOuterMostTransaction) {        	
	            conn.close();	             
	            //Close general object connection
	            this.conn.close();
	            this.conn = null;
	        }
	    }
	    catch (SQLException e) {
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
	    catch (DAOException e) {
	    	e.printStackTrace();
	        System.err.println("[IGNORED]");
	    }
	}
	
    
	protected void setTransactionIsolationWithoutException(Connection conn, int isolationLevel) {
	    try {
	        conn.setTransactionIsolation(isolationLevel);
	    }
	    catch (SQLException e) {
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
	
	
	protected void startTransaction(Connection conn) throws DAOException{
		try{
			isOuterMostTransaction = conn.getAutoCommit();
			if (isOuterMostTransaction){
				conn.setAutoCommit(false);
			}
		}
		catch (SQLException e){
			 throw new DAOConnectionException(e);
		}
	}
	
	
	protected void rollbackTransaction(Connection conn) throws DAOException{
		try{
			if (isOuterMostTransaction) {
				conn.rollback();
				conn.setAutoCommit(true);
			}
		}
		catch (SQLException e){
			 throw new DAOConnectionException(e);
		}	
	}
	
	protected void commitTransaction(Connection conn) throws DAOException{
		try{
			if (isOuterMostTransaction){
				conn.commit();
				conn.setAutoCommit(true);
			}
		}
		catch (SQLException e){
			 throw new DAOConnectionException(e);
		}			
	}	
	
	
	protected void rollbackDDLs(Connection conn, List<String> sqlDDLs){
	    try {
	       for (String sqlDDL:sqlDDLs){
	    	   helper.executeUpdate(conn, sqlDDL);
	       }
	    }
	    catch (SQLException e) {
	    	e.printStackTrace();
	        System.err.println("[IGNORED]");
	    }		
	}	
	
	
	/**
	 * Method that will start up the databases, detecting if the required tables for do XMap  
	 * already exists and if don't, read the schema db from a file and create its tables.
	 * @throws DAOException if there is a problem executing this method
	 */      
	protected synchronized void startUpDatabase() throws DAOException {
		Connection conn = getConnection();		
		try{
			DatabaseMetaData metadata = conn.getMetaData();
	        ResultSet rsTables = metadata.getTables(null, null, "Crossmap", null);
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
	
	
	
	/****************************************************************************************/
	/* PRIVATE METHODS																	*/														
	/****************************************************************************************/			
			
	private String getTableNameXmapFlat(String xMapPrefix){
		return "`" + xMapPrefix + "_Flat`";
	}	
	
	private String getViewNameXmapFlatTable(String xMapPrefix){
		return "`" + xMapPrefix + "_Flat_vw`";
	}		
	
	private String getTableNameChkListNameUse(String checklistPrefix){
		return "`" + checklistPrefix + "_NameUse`";
	}
	
	private String getViewNameChkListNameUse(String checklistPrefix){
		return "`" + checklistPrefix + "_NameUse_vw`";
	}	
	
	private String getTableNameChkListTaxon(String checklistPrefix){
		return "`" + checklistPrefix + "_Taxon`";
	}	
	
	private String getTableNameChkListRaw(String checklistPrefix){
		return "`" + checklistPrefix + "_Raw`";
	}		
		
	private String getTableNameElementsInCommon(String xMapPrefix){
		return "`" + xMapPrefix + "_ElementsInCommon`";
	}	
	
	private String getTableNameListToManys(String xMapPrefix, int num){
		return "`" + xMapPrefix + "_ToManys" + num + "`";
	}
	
	private String getTableNameHigherTaxaInCommon(String xMapPrefix){
		return "`" + xMapPrefix + "_HigherTaxaInCommon`";
	}
		
	private String getTableNameWorking(String xMapPrefix){
		return "`" + xMapPrefix + "_Working`";
	}
	
	private String getTableNameXmapRefineInfo(String xMapPrefix){
		return "`" + xMapPrefix + "_RefinementInfo`";
	}
	
	private String getNameIdNameUsed(boolean strict){
		return (strict?"nameXMap":"nameNoAuthorXMap");		
	}
	
	private String getTableNameTempNames(String xMapPrefix){
		return "`" + xMapPrefix + "_tempnames`";
	}			
	
	private String getTableNameTempAncestors(String xMapPrefix, int num){
		return "`" + xMapPrefix + "_tempancestors_" + num +"`";
	}			
	
	private String getFunctionNameGetAncestor(String checklistPrefix){
		return "getAncestor_" + checklistPrefix;		
	}

	
	private String getTableNameRelTaxaInXMap(String xMapPrefix, String leftTaxonId, String rightTaxonId, int num){
		return "`" + xMapPrefix + "_RelTaxa_" + getNthRightCharacters(leftTaxonId,5).replaceAll("\\s","") + "_" +   getNthRightCharacters(rightTaxonId,5).replaceAll("\\s","") +  "_" + num + "`";			
	}		
	
	private String getTableNameRelTaxaElemsInCommonInXMap(String xMapPrefix, String leftTaxonId, String rightTaxonId){
		return "`" + xMapPrefix + "_RelTaxa_EC_" + getNthRightCharacters(leftTaxonId,5).replaceAll("\\s","") + "_" +   getNthRightCharacters(rightTaxonId,5).replaceAll("\\s","") + "`";			
	}		
	
	
	
    public String getNthRightCharacters(String str, int len) {
        if (str == null) {
            return null;
        }
        if (len < 0) {
            return "";
        }
        if (str.length() <= len) {
            return str;
        }
        return str.substring(str.length() - len);
    }	
	
	
	/**
	 * Mehtod responsible to return a checklist given its id
	 * @param checklistId
	 * @return checklist given its id
	 * @throws DAOException 
	 */
	private Checklist getChecklist(String checklistId, Connection conn) throws DAOException{
		PreparedStatement st = null;
		try{
			String sqlGetChecklistById = "select chk.id,chk.checklistName,chk.fileName,chk.user,chk.scope,chk.prefixTableDB,chk.taskId,t.status" +
					" from Checklist as chk inner join Task as t on t.id = chk.taskId"+
					" where chk.id=?";			
			
			st = helper.createGeneratedKeysPreparedStatement(conn,sqlGetChecklistById);
			st.setString(1, checklistId);
						
			ResultSet rs = st.executeQuery();
            
			if (rs.next()){
				Checklist checklist = parseChecklist(rs);
				return checklist;
			}	
			else{
				throw new RuntimeException("Checklist not found");
			}
		}
		catch (SQLException ex){
			throw new DAOReadException(ex);			
		}	
		finally{
			closeStatementWithoutException(st);
		}				
	}		
	
	private XMap getXMap(String xMapId, Connection conn) throws DAOException{	
		PreparedStatement st = null;
		try{
			String sqlGetXMapById = "select xmap.id,xmap.shortname,xmap.user,xmap.scope,xmap.longname," +
					" xmap.leftChecklistId,leftChk.checklistName as leftChecklistName,xmap.rightChecklistId,rightChk.checklistName as rightChecklistName," +
					" xmap.strict,xmap.identifyExtraTaxa,xmap.compareHigherTaxa,xmap.highestRankIdToCompare," +
					" xmap.prefixTableDB,xmap.taskId,t.status" +
					" from Crossmap as xmap inner join Task as t on t.id = xmap.taskId" +
					" inner join Checklist as leftChk on leftChk.id = xmap.leftChecklistId" +
					" inner join Checklist as rightChk on rightChk.id = xmap.rightChecklistId" +
                    " where xmap.id=?";	
			
			st = helper.createGeneratedKeysPreparedStatement(conn,sqlGetXMapById);
			st.setString(1, xMapId);
						
			ResultSet rs = st.executeQuery();
            
			if (rs.next()){
				XMap xMap = parseXMap(rs);				
				return xMap;
			}	
			else{
				throw new RuntimeException("crossmap not found");
			}
		}
		catch (SQLException ex){
			throw new DAOReadException(ex);			
		}	
		finally{
			closeStatementWithoutException(st);
		}
	}
			
	private ResultExportXmap getXMapResult(String xMapResultId, Connection conn) throws DAOException{	
		PreparedStatement st = null;
		try{		
			String sqlGetResXMapById = "select exp.id,exp.name,exp.xMapId,exp.includeAcceptedNames,exp.exportDate,exp.user,exp.scope,exp.resultFileURL,exp.taskId,t.status" +
					" from ExportCrossmapResult as exp inner join Task as t on t.id = exp.taskId" +
					" where exp.id=?";			
									
			st = helper.createGeneratedKeysPreparedStatement(conn,sqlGetResXMapById);
			st.setString(1, xMapResultId);
						
			ResultSet rs = st.executeQuery();
            
			if (rs.next()){
				ResultExportXmap xMapResult = parseXMapResult(rs);				
				return xMapResult;
			}	
			else{
				throw new RuntimeException("crossmap result not found");
			}
		}
		catch (SQLException ex){
			throw new DAOReadException(ex);			
		}	
		finally{
			closeStatementWithoutException(st);
		}
	}
	
	private TaxonomicRank getTaxonomicRankById(String id, Connection conn) throws DAOException{
		TaxonomicRank rank = null;
		PreparedStatement st=null;
		try{			
			String sql = "select id,rank,parentId,isHigherRank from TaxonomicRankHierarchy where id=?;";			
			st = helper.createGeneratedKeysPreparedStatement(conn,sql);
			st.setString(1, id);
			
			ResultSet rs = st.executeQuery();
            if (rs.next()){
            	rank = parseTaxonomicRank(rs);		            	
            } 
            rs.close();
            
            return rank;
		}
		catch (SQLException ex){
			throw new DAOReadException(ex);	
		}	
		finally{
			closeStatementWithoutException(st);
		}	
	}	
	
	private void dropTemporaryTables(String xMapId,Connection conn) throws DAOException{		
		try{
			XMap xMap = getXMap(xMapId,conn);					
			String elementsInCommon = getTableNameElementsInCommon(xMap.getPrefixTableDB());
			String higherTaxaInCommon = getTableNameHigherTaxaInCommon(xMap.getPrefixTableDB());
			String list1tomanys =  getTableNameListToManys(xMap.getPrefixTableDB(),1);
			String list2tomanys = getTableNameListToManys(xMap.getPrefixTableDB(),2);						
			String working = getTableNameWorking(xMap.getPrefixTableDB());		
			
						
			String sqlDropList1ToManysTable = "DROP TABLE IF EXISTS " + list1tomanys;
			String sqlDropList2ToManysTable = "DROP TABLE IF EXISTS " + list2tomanys;
			String sqlDropWorkingTable = "DROP TABLE IF EXISTS " + working;			
			String sqlDropElementsInCommonTable = "DROP TABLE IF EXISTS " + elementsInCommon;	
			String sqlDropHigherTaxaInCommonTable = "DROP TABLE IF EXISTS " + higherTaxaInCommon;
						
			startTransaction(conn);			
			helper.executeUpdate(conn, sqlDropList1ToManysTable);
			helper.executeUpdate(conn, sqlDropList2ToManysTable);
			helper.executeUpdate(conn, sqlDropWorkingTable);				
			helper.executeUpdate(conn, sqlDropElementsInCommonTable);
			helper.executeUpdate(conn, sqlDropHigherTaxaInCommonTable);			
			commitTransaction(conn);
		}
		catch (SQLException ex){
			rollbackTransaction(conn);
			throw new DAOWriteException(ex);
		}	
	}	
	
	private int countElementsInTable(String tableName, DataRetrievalFilterSeq filters,Connection conn) throws SQLException	{
		String sqlCountElement = "select count(*) as rowcount from " + tableName + getSQLFilterPart(filters) + ";";	
		PreparedStatement st = helper.createGeneratedKeysPreparedStatement(conn,sqlCountElement);			
		ResultSet rs = st.executeQuery();
		rs.next();
		int count = rs.getInt("rowcount") ;
		rs.close();
		return count;
	}	

	
	private String getSQLFilterPart(DataRetrievalFilterSeq filters){
		String sqlFilter = "";
		if (filters!=null && filters.getFilter().size()>0){			
			for (DataRetrievalFilter filter:filters.getFilter()){
				sqlFilter += (sqlFilter.isEmpty()?" where ":" and ") + filter.getColumn();
				switch (filter.getType()){
					case EXACT:
						sqlFilter += " ='"  + StringEscapeUtils.escapeSql(filter.getValue()) + "'";
						break;
					case START_WITH:
						sqlFilter += " like '" + StringEscapeUtils.escapeSql(filter.getValue()) + "%'";
						break;
					case CONTAINS:
						sqlFilter += " like '%" + StringEscapeUtils.escapeSql(filter.getValue()) + "%'";
						break;
					case END_WITH:
						sqlFilter += " like '%" + StringEscapeUtils.escapeSql(filter.getValue()) + "'";
						break;
				}
			}
		}
		return sqlFilter;
	}
	
	private String getSQLSortPart(DataRetrievalSortSeq sorts){
		String sqlSort = "";
		if (sorts!=null && sorts.getSort().size()>0){			
			for (DataRetrievalSort sort:sorts.getSort()){
				sqlSort += (sqlSort.isEmpty()?" order by ":" ,") + sort.getColumn() + " " + sort.getDirection().value(); 				
			}
		}		
		return sqlSort;
	}
	
	private EntryInChecklist parseEntryInChecklist(ResultSet rs) throws SQLException{
		EntryInChecklist entry = new EntryInChecklist();
		entry.setTaxonId(rs.getString("taxonId"));
		entry.setTidyName(rs.getString("tidyName"));
		entry.setStatus(rs.getString("status"));
		entry.setHigher(rs.getString("higher"));
		entry.setSpecies(rs.getString("species"));
		entry.setInfraspecies(rs.getString("infraspecies"));
		entry.setAuthority(rs.getString("authority"));
		entry.setRank(rs.getString("rank"));		
		entry.setParentTaxonId(rs.getString("parentId"));	
		
		return entry;		
	}
	
	private EntryInXMap parseEntryInXMap(ResultSet rs) throws SQLException{
		EntryInXMap entry = new EntryInXMap();
		entry.setTaxonIdLeft(rs.getString("taxonIdLeft"));
		entry.setChecklistNameLeft(rs.getString("checklistNameLeft"));
		entry.setRankLeft(rs.getString("rankLeft"));
		entry.setAcceptedNameLeft(rs.getString("acceptedNameLeft"));
		entry.setUuidLeft(rs.getString("uuidLeft"));
		entry.setRelationship(rs.getString("relationship"));
		entry.setTaxonIdRight(rs.getString("taxonIdRight"));
		entry.setChecklistNameRight(rs.getString("checklistNameRight"));
		entry.setRankRight(rs.getString("rankRight"));
		entry.setAcceptedNameRight(rs.getString("acceptedNameRight"));
		entry.setUuidRight(rs.getString("uuidRight"));
		return entry;		
	}	
	
	
	private EntryInUserKnowledge parseEntryInUserKnowledge(ResultSet rs) throws SQLException{
		EntryInUserKnowledge entry = new EntryInUserKnowledge();
		entry.setId(rs.getString("id"));
		entry.setName1(rs.getString("name1"));
		entry.setRelationship(NamesRelationshipType.fromValue(rs.getString("relationship")));
		entry.setName2(rs.getString("name2"));
		entry.setXMapId(rs.getString("xMapId"));
		entry.setXMapName(rs.getString("xMapName"));
		entry.setUser(rs.getString("user"));
		entry.setScope(rs.getString("scope"));
		entry.setComment(rs.getString("comment"));
		return entry;		
	}		
	
	
	private Checklist parseChecklist(ResultSet rs) throws SQLException{
		Checklist checklist = new Checklist();
		checklist.setId(rs.getString("id"));
		checklist.setName(rs.getString("checklistName"));
		checklist.setImportedFileName(rs.getString("fileName"));
		checklist.setUser(rs.getString("user"));
		checklist.setScope(rs.getString("scope"));
		checklist.setPrefixTableDB(rs.getString("prefixTableDB"));
		checklist.setStatus(TaskStatus.fromValue(rs.getString("status")));
		
		String sTaskId = rs.getString("taskId");
		if (!rs.wasNull()){
			checklist.setTaskId(new TaskId(sTaskId));
		}
		
		return checklist;
	}
	
	private XMap parseXMap(ResultSet rs) throws SQLException, DAOException{
		XMap xMap = new XMap();
		xMap.setId(rs.getString("id"));
		xMap.setShortName(rs.getString("shortname"));
		xMap.setUser(rs.getString("user"));
		xMap.setScope(rs.getString("scope"));
		xMap.setLongName(rs.getString("longname"));
		xMap.setLeftChecklistId(rs.getString("leftChecklistId"));
		xMap.setLeftChecklistName(rs.getString("leftChecklistName"));
		xMap.setRightChecklistId(rs.getString("rightChecklistId"));
		xMap.setRightChecklistName(rs.getString("rightChecklistName"));
		xMap.setStrict(rs.getBoolean("strict"));		
		String sIdentifyExtraTaxa = rs.getString("identifyExtraTaxa");
		if (!rs.wasNull()){			
			xMap.setIdentifyExtraTaxa(IdentifyExtraTaxaType.fromValue(sIdentifyExtraTaxa));
		}
		xMap.setCompareHigherTaxa(rs.getBoolean("compareHigherTaxa"));		
		String sRankId = rs.getString("highestRankIdToCompare");
		if (!rs.wasNull()){			
			xMap.setHigherRankToCompare(getTaxonomicRankById(sRankId,rs.getStatement().getConnection()));
		}				
		xMap.setPrefixTableDB(rs.getString("prefixTableDB"));
		xMap.setStatus(TaskStatus.fromValue(rs.getString("status")));
		
		String sTaskId = rs.getString("taskId");
		if (!rs.wasNull()){
			xMap.setTaskId(new TaskId(sTaskId));
		}
    	return xMap;
	}
	

	private ResultExportXmap parseXMapResult(ResultSet rs) throws SQLException {
		ResultExportXmap resultExportXMap = new ResultExportXmap();
		resultExportXMap.setId(rs.getString("id"));
		resultExportXMap.setName(rs.getString("name"));
		resultExportXMap.setXMapId(rs.getString("xMapId"));
		resultExportXMap.setIncludeAcceptedName(rs.getBoolean("includeAcceptedNames"));
		resultExportXMap.setExportDate(rs.getTimestamp("exportDate").getTime());
		resultExportXMap.setUser(rs.getString("user"));
		resultExportXMap.setScope(rs.getString("scope"));
		resultExportXMap.setStatus(TaskStatus.fromValue(rs.getString("status")));
		resultExportXMap.setResultFileURL(rs.getString("resultFileURL"));
		
		String sTaskId = rs.getString("taskId");
		if (!rs.wasNull()){
			resultExportXMap.setTaskId(new TaskId(sTaskId));
		}
		
		return resultExportXMap;
	}

	
	private TaxonomicRank parseTaxonomicRank(ResultSet rs) throws SQLException {
		TaxonomicRank rank = new TaxonomicRank();
		rank.setId(rs.getString("id"));
		rank.setName(rs.getString("rank"));
		String parentId = rs.getString("parentId");
		if (!rs.wasNull()){
			rank.setParentId(parentId);
		}
		
		rank.setIsHigherRank(rs.getBoolean("isHigherRank"));
		
		return rank;
	}
	
	
	private RelationshipPairTaxaEntry parseRelationshipPairTaxaEntry(ResultSet rs) throws SQLException {
		RelationshipPairTaxaEntry entry = new RelationshipPairTaxaEntry();
		entry.setTaxId1(rs.getString("leftTaxonId"));
		entry.setElement1(rs.getString("leftName"));
		entry.setExtra1(rs.getString("leftComment"));
		entry.setRelType(rs.getString("relType"));		
		entry.setTaxId2(rs.getString("rightTaxonId"));
		entry.setElement2(rs.getString("rightName"));
		entry.setExtra2(rs.getString("rightComment"));
		entry.setInOthers(rs.getBoolean("inOthers"));
		entry.setNameLevel(rs.getBoolean("nameLevel"));
		return entry;
	}		
	
	private RawDataRegister parseRawDataRegister(ResultSet rs) throws SQLException{
		RawDataRegister register = new RawDataRegister();
		ResultSetMetaData rsmd = rs.getMetaData();
		
		List<RawDataField> rowFields = new ArrayList<RawDataField>();
		
		int columnCount = rsmd.getColumnCount();
		for (int i = 1; i < columnCount + 1; i++ ) {
		  String name = rsmd.getColumnName(i);
		  String value = rs.getString(i);
		  RawDataField rowField = new RawDataField(name,value);
		  rowFields.add(rowField);		  
		}
		
		register.getRawDataField().addAll(rowFields);
		
		return register;
	}
	
	private void createTablesForGetXMapRelationshipDetail(String xMapId, String leftTaxonId, String rightTaxonId, Connection conn)  throws DAOException{
		try{
			XMap xmap = getXMap(xMapId,conn);
			String tableGetRelTaxaLeft= getTableNameRelTaxaInXMap(xmap.getPrefixTableDB(),leftTaxonId,rightTaxonId,1);
			String tableGetRelTaxaRight = getTableNameRelTaxaInXMap(xmap.getPrefixTableDB(),leftTaxonId,rightTaxonId,2);
			String tableGetRelTaxaElemsInCommon = getTableNameRelTaxaElemsInCommonInXMap(xmap.getPrefixTableDB(),leftTaxonId,rightTaxonId);
								
			String sqlCreateTableGetRelTaxaLeft="CREATE TABLE "+tableGetRelTaxaLeft+ " (" +
					" `taxonId` VARCHAR(60)," +					
					" `nameXMap` varchar(255)," +
					" `nameNoAuthorXMap` varchar(255)," +
					" `epithetXMap` varchar(255)," +
					" `authority` varchar(255)," +
					" `comment` varchar(255));";	
			
			String sqlCreateTableGetRelTaxaRight="CREATE TABLE "+tableGetRelTaxaRight+ " (" +
					" `taxonId` VARCHAR(60)," +					
					" `nameXMap` varchar(255)," +
					" `nameNoAuthorXMap` varchar(255)," +
					" `epithetXMap` varchar(255)," +
					" `authority` varchar(255)," +
					" `comment` varchar(255));";		
			
			String sqlCreateTableRelTaxaElemsInCommon="CREATE TABLE "+tableGetRelTaxaElemsInCommon+ " (" +
					" `leftTaxonId` VARCHAR(60)," +					
					" `leftName` varchar(255)," +
					" `leftComment` varchar(255)," +
					" `relType` varchar(255)," +
					" `rightTaxonId` VARCHAR(60)," +
					" `rightName` varchar(255),"+
					" `rightComment` varchar(255)," +
					" `inOthers` tinyint(1)," +
					" `nameLevel` tinyint(1));";	
			
			helper.executeUpdate(conn, sqlCreateTableGetRelTaxaLeft);
			helper.executeUpdate(conn, sqlCreateTableGetRelTaxaRight);
			helper.executeUpdate(conn, sqlCreateTableRelTaxaElemsInCommon);
		}
		catch (SQLException ex){
			throw new DAOWriteException(ex);	
		}				
	}
	
	private void dropTablesForGetXMapRelationshipDetail(String xMapId, String leftTaxonId, String rightTaxonId, Connection conn){
		try{
			XMap xmap = getXMap(xMapId,conn);
			String tableGetRelTaxaLeft= getTableNameRelTaxaInXMap(xmap.getPrefixTableDB(),leftTaxonId,rightTaxonId,1);
			String tableGetRelTaxaRight = getTableNameRelTaxaInXMap(xmap.getPrefixTableDB(),leftTaxonId,rightTaxonId,2);
			String tableGetRelTaxaElemsInCommon = getTableNameRelTaxaElemsInCommonInXMap(xmap.getPrefixTableDB(),leftTaxonId,rightTaxonId);
								
			String sqlDropTableGetRelTaxaLeft = "DROP TABLE IF EXISTS " + tableGetRelTaxaLeft + ";";
			String sqlDropTableGetRelTaxaRight = "DROP TABLE IF EXISTS " + tableGetRelTaxaRight + ";";			
			String sqlDropTableGetRelTaxaElemsInCommon = "DROP TABLE IF EXISTS " + tableGetRelTaxaElemsInCommon + ";";
			
			helper.executeUpdate(conn, sqlDropTableGetRelTaxaLeft);
			helper.executeUpdate(conn, sqlDropTableGetRelTaxaRight);
			helper.executeUpdate(conn, sqlDropTableGetRelTaxaElemsInCommon);
		}
	    catch (Exception e) {
	    	e.printStackTrace();
	        System.err.println("[IGNORED]");
	    }		
	}	
	
			
	private TaxonomicRank getTaxonomicRankTaxon(String checklistId, String taxonId, Connection conn)  throws DAOException{
		TaxonomicRank rank = null;		
		PreparedStatement st=null;
		try{			
			Checklist checklist = getChecklist(checklistId,conn);
			String tableNameuse = getTableNameChkListNameUse(checklist.getPrefixTableDB());
			
			String sql = "select rank from " + tableNameuse + " where taxonId=? and status=?;";			
			st = helper.createGeneratedKeysPreparedStatement(conn,sql);
			st.setString(1, taxonId);
			st.setString(2, "accepted");			
			ResultSet rs = st.executeQuery();
	        if (!rs.next()) throw new RuntimeException("It can not be determined the rank of taxon " + taxonId);
	        rank = getTaxonomicRankByName(rs.getString("rank"), conn);		            	 
	        rs.close();		
	        
	        return rank;
		}
		catch (SQLException ex){
			throw new DAOReadException(ex);	
		}	
		finally{
			closeStatementWithoutException(st);
		}		        
	}
	
	
	private int compareTaxonomicRanks(TaxonomicRank rankA, TaxonomicRank rankB, Connection conn) throws DAOException{
		int result;
		if (rankA.getId().equalsIgnoreCase(rankB.getId())){
			result = 0;
		}
		else{			
			List<TaxonomicRank> ancestorsB = new ArrayList<TaxonomicRank>();
			TaxonomicRank auxRank = getNextTaxonomicRank(rankB,true,conn);
			while (auxRank!=null) {
				ancestorsB.add(auxRank); 
				auxRank = getNextTaxonomicRank(auxRank,true,conn);
			}			
			
			for (TaxonomicRank ancestorB: ancestorsB){
				if (ancestorB.getId().equalsIgnoreCase(rankA.getId())){
					result = 1;
					break;
				}
			}
			result = -1;
		}
		return result;
	}			
	
	private TaxonomicRank getNextTaxonomicRank(TaxonomicRank rank, boolean up, Connection conn) throws DAOException{
		PreparedStatement st = null;
		TaxonomicRank nextRank = null;
		try{
			String sqlGetNextRank = "";
			if (up){ //obtain the parent
				sqlGetNextRank = "select r2.id, r2.rank, r2.parentId, r2.isHigherRank" +
					" from TaxonomicRankHierarchy as r1" +
					" inner join TaxonomicRankHierarchy as r2 on r2.id = r1.parentId" +
                    " where r1.id=?";
			}
			else{ //obtain the child
				sqlGetNextRank = "select r2.id, r2.rank, r2.parentId, r2.isHigherRank" +
						" from TaxonomicRankHierarchy as r1" +
						" inner join TaxonomicRankHierarchy as r2 on r2.parentId = r1.id" +
	                    " where r1.id=?";				
			}
				
			
			st = helper.createGeneratedKeysPreparedStatement(conn,sqlGetNextRank);
			st.setString(1, rank.getId());
						
			ResultSet rs = st.executeQuery();
            
			if (rs.next()){
				nextRank = parseTaxonomicRank(rs);
			}
			
			return nextRank;
		}
		catch (SQLException ex){
			throw new DAOReadException(ex);			
		}	
		finally{
			closeStatementWithoutException(st);
		}		
	} 	
	
	private String getTypeRelationshipBetweenTaxa (String xMapId, String leftTaxonId, String rightTaxonId, Connection conn) throws DAOException{
		String relationship = null;		
		PreparedStatement st=null;
		try{
			XMap xMap = getXMap(xMapId,conn);
			String flatTable = getTableNameXmapFlat(xMap.getPrefixTableDB());		

			String sql = "select relationship from " + flatTable + " where taxId1=? and taxId2=?;";			
			st = helper.createGeneratedKeysPreparedStatement(conn,sql);
			st.setString(1, leftTaxonId);
			st.setString(2, rightTaxonId);			
			ResultSet rs= st.executeQuery();
		    if (!rs.next()) throw new RuntimeException("It can not be determined the type of relationship");
		    relationship = rs.getString("relationship");           	 
		    rs.close();
		    
		    return relationship;
		}
		catch (SQLException ex){
			throw new DAOReadException(ex);	
		}	
		finally{
			closeStatementWithoutException(st);
		}			
	}
		
	
	/**
	 * Method responsible to get the isolated relationship between the pair of taxa received. In other words the relationship between its elemnts without considering any other taxa
	 * @param xMapId
	 * @param leftTaxonId
	 * @param rightTaxonId
	 * @param conn
	 * @return
	 * @throws DAOException
	 */
	private RelationshipPairTaxa getDetailsIsolatedRelationshipPairTaxa (String xMapId, String leftTaxonId, String rightTaxonId, Connection conn) throws DAOException{
		RelationshipPairTaxa pairIsolatedRel = new RelationshipPairTaxa();
		XMap xmap = getXMap(xMapId,conn);
				
		//Get string that defines the type of relationship between this pair of taxa
		String rel = getTypeRelationshipBetweenTaxa(xMapId,leftTaxonId,rightTaxonId,conn);	
		
    	//Set the values for the relationship and the accepted names and ranks of leftTaxon and rightTaxon
		pairIsolatedRel.setLeftTaxonId(leftTaxonId);
		pairIsolatedRel.setRightTaxonId(rightTaxonId);
    	pairIsolatedRel.setRelationship(rel);    	
    	
    	if (!leftTaxonId.equalsIgnoreCase("-1")){
	    	EntryInChecklist acceptedEntryLeftTaxon = getAcceptedEntryTaxon(xmap.getLeftChecklistId(),leftTaxonId,conn);
	    	pairIsolatedRel.setLeftTaxonName(acceptedEntryLeftTaxon.getTidyName());
	    	pairIsolatedRel.setLeftTaxonRank(acceptedEntryLeftTaxon.getRank());
    	}
    	else{
    		pairIsolatedRel.setLeftTaxonName("");
    		pairIsolatedRel.setLeftTaxonRank("");
    	}
    	
    	if (!rightTaxonId.equalsIgnoreCase("-1")){
	    	EntryInChecklist acceptedEntryRightTaxon = getAcceptedEntryTaxon(xmap.getRightChecklistId(),rightTaxonId,conn);
	    	pairIsolatedRel.setRightTaxonName(acceptedEntryRightTaxon.getTidyName());
	    	pairIsolatedRel.setRightTaxonRank(acceptedEntryRightTaxon.getRank());
    	}
    	else{
    		pairIsolatedRel.setRightTaxonName("");
    		pairIsolatedRel.setRightTaxonRank("");
    	}
		
    	//Get the elements related in this pair of taxa
    	List<RelationshipPairTaxaEntry> elements = getElementRelatedPairTaxa(xMapId,leftTaxonId,rightTaxonId,conn);
    	pairIsolatedRel.getElementRel().addAll(elements);		
    	
    	return pairIsolatedRel;

	}	
	
	private List<RelationshipPairTaxaEntry> getElementRelatedPairTaxa(String xMapId, String leftTaxonId, String rightTaxonId, Connection conn) throws DAOException{
		List<RelationshipPairTaxaEntry> elements = new ArrayList<RelationshipPairTaxaEntry>();
		try{
			XMap xmap = getXMap(xMapId,conn);
			Checklist leftChecklist = getChecklist(xmap.getLeftChecklistId(),conn);
			Checklist rightChecklist = getChecklist(xmap.getRightChecklistId(),conn);
			    		    	
			//Get the rank of one of the taxon. Note: We suppose that the rank of the taxon are equals due that the xmap is only matching taxa of same rank
			TaxonomicRank rankTaxa;
			if (!leftTaxonId.equalsIgnoreCase("-1")){
				rankTaxa = getTaxonomicRankTaxon(xmap.getLeftChecklistId(),leftTaxonId,conn);
			}
			else{
				rankTaxa = getTaxonomicRankTaxon(xmap.getRightChecklistId(),rightTaxonId,conn);
			}
			
			//Create temp tables to get the comunalities between the 2 taxa
			createTablesForGetXMapRelationshipDetail(xMapId,leftTaxonId,rightTaxonId,conn);
						
			//Get the elements related
			String nameIdNameUsed = getNameIdNameUsed(xmap.isStrict());
    		String flatTable = getTableNameXmapFlat(xmap.getPrefixTableDB());
    		String tableNameuse1 = getTableNameChkListNameUse(leftChecklist.getPrefixTableDB());
			String tableNameuse2 = getTableNameChkListNameUse(rightChecklist.getPrefixTableDB());				
			String tableTaxon1 = getTableNameChkListTaxon(leftChecklist.getPrefixTableDB());
			String tableTaxon2 = getTableNameChkListTaxon(rightChecklist.getPrefixTableDB());
			String tableGetRelTaxaLeft= getTableNameRelTaxaInXMap(xmap.getPrefixTableDB(),leftTaxonId,rightTaxonId,1);
			String tableGetRelTaxaRight = getTableNameRelTaxaInXMap(xmap.getPrefixTableDB(),leftTaxonId,rightTaxonId,2);
			String tableRelTaxaElemsInCommon = getTableNameRelTaxaElemsInCommonInXMap(xmap.getPrefixTableDB(),leftTaxonId,rightTaxonId);
			
			
	    	if (!rankTaxa.isIsHigherRank()){
            	//Fill tables with all the names of left taxon and right taxon
	    		String sqlInsertElementsInGetRelTaxaLeft = "insert into " + tableGetRelTaxaLeft + " (taxonId, nameXMap, nameNoAuthorXMap, epithetXMap, authority, comment) " +
            			" select taxonId, nameXMap, nameNoAuthorXMap, epithetXMap, authority, status from " + tableNameuse1 +" where taxonId=?";

            	String sqlInsertElementsInGetRelTaxaRight = "insert into " + tableGetRelTaxaRight + " (taxonId, nameXMap, nameNoAuthorXMap, epithetXMap, authority, comment) " +
            			" select taxonId, nameXMap, nameNoAuthorXMap, epithetXMap, authority, status from " + tableNameuse2 +" where taxonId=?";     
            	
            	PreparedStatement stInsertElements1 = helper.createGeneratedKeysPreparedStatement(conn,sqlInsertElementsInGetRelTaxaLeft);
            	stInsertElements1.setString(1, leftTaxonId);
    			
            	PreparedStatement stInsertElements2 = helper.createGeneratedKeysPreparedStatement(conn,sqlInsertElementsInGetRelTaxaRight);
                stInsertElements2.setString(1, rightTaxonId);
                
                stInsertElements1.executeUpdate();
                stInsertElements2.executeUpdate();            	
	    		              				    
	    		//Get confirmed names in common between the left and the right taxon
	    		String sqlGetNamesInCommonConfirmed = "insert into " + tableRelTaxaElemsInCommon + "(leftTaxonId,leftName,leftComment,relType,rightTaxonId,rightName,rightComment,inOthers,nameLevel)" +
	    				" SELECT distinct l.taxonId, l.nameXMap, l.comment, 'match', r.taxonId, r.nameXMap, r.comment, '0', '1'" +
	    				" FROM "+tableGetRelTaxaLeft+" as l" +
	    				" 	inner join "+tableGetRelTaxaRight+" as r on r."+ nameIdNameUsed + " = l." + nameIdNameUsed + ";";
				PreparedStatement stGetNamesInCommonConfirmed = helper.createGeneratedKeysPreparedStatement(conn,sqlGetNamesInCommonConfirmed);    						
				stGetNamesInCommonConfirmed.executeUpdate();
										
				if (xmap.isStrict() && xmap.getIdentifyExtraTaxa()!=IdentifyExtraTaxaType.NONE){
					//PossNameMatch		    		
		    		String sqlGetNamesInCommonPossNameMatch = "insert into " + tableRelTaxaElemsInCommon + "(leftTaxonId,leftName,leftComment,relType,rightTaxonId,rightName,rightComment,inOthers,nameLevel)" + 
		    				" SELECT distinct l.taxonId, l.nameXMap, l.comment, 'poss_name_match', r.taxonId, r.nameXMap, r.comment, '0', '1'" +
		    				" FROM "+tableGetRelTaxaLeft+" as l" +
		    				" 	inner join "+tableGetRelTaxaRight+" as r on r.nameNoAuthorXMap = l.nameNoAuthorXMap and r.nameXMap != l.nameXMap;";
					PreparedStatement stGetNamesInCommonPossNameMatch = helper.createGeneratedKeysPreparedStatement(conn,sqlGetNamesInCommonPossNameMatch);    						
					stGetNamesInCommonPossNameMatch.executeUpdate();
													
					if (xmap.getIdentifyExtraTaxa()==IdentifyExtraTaxaType.GENERIC_TRANSFER){
						//PossGenTraf
			    		String sqlGetNamesInCommonPossGenTrf = "insert into " + tableRelTaxaElemsInCommon + "(leftTaxonId,leftName,leftComment,relType,rightTaxonId,rightName,rightComment,inOthers,nameLevel)" + 
			    				" SELECT distinct l.taxonId, l.nameXMap, l.comment, 'poss_gen_trnfr' , r.taxonId, r.nameXMap, r.comment, '0', '1'" +
								" FROM "+tableGetRelTaxaLeft+" as l, "+tableGetRelTaxaRight+" as r" +
								"	WHERE r.nameNoAuthorXMap != l.nameNoAuthorXMap and r.epithetXMap = l.epithetXMap and" +
								"   ((l.authority != '' AND LOCATE(l.authority, r.authority)>0) OR (r.authority != '' AND LOCATE(r.authority, l.authority)>0));";
						PreparedStatement stGetNamesInCommonPossGenTrf = helper.createGeneratedKeysPreparedStatement(conn,sqlGetNamesInCommonPossGenTrf);    		
						stGetNamesInCommonPossGenTrf.executeUpdate();												
					}
				}
											
				String sqlGetNamesInCommon = "select leftTaxonId,leftName,leftComment,relType,rightTaxonId,rightName,rightComment,inOthers,nameLevel from " + tableRelTaxaElemsInCommon + ";"; 
				PreparedStatement stGetNamesInCommon = helper.createGeneratedKeysPreparedStatement(conn,sqlGetNamesInCommon);    						
				ResultSet rsGetNamesInCommon = stGetNamesInCommon.executeQuery();							
				while (rsGetNamesInCommon.next()){
					elements.add(parseRelationshipPairTaxaEntry(rsGetNamesInCommon));
				}
				rsGetNamesInCommon.close();
				rsGetNamesInCommon.close();	
									
				//Names only in taxon1
	    		String sqlGetNamesOnlyInLeft = "SELECT distinct l.taxonId as leftTaxonId, l.nameXMap as leftName, l.comment as leftComment, 'not match' as relType, '' as rightTaxonId, '' as rightName, '' as rightComment, IF(nr.taxonId IS NULL, '0', '1') as inOthers, '1' as nameLevel" +
	    				" FROM "+tableGetRelTaxaLeft+" as l" +
	    				" 	left join "+tableRelTaxaElemsInCommon+" as ec on ec.leftName = l.nameXMap" +
	    				" 	left join "+tableNameuse2+" as nr on nr." + nameIdNameUsed + " = l." + nameIdNameUsed + 
	    				" WHERE ec.leftTaxonId is null and (nr.taxonId is null or nr.taxonId!=?);";
				PreparedStatement stGetNamesOnlyInLeft = helper.createGeneratedKeysPreparedStatement(conn,sqlGetNamesOnlyInLeft);
				stGetNamesOnlyInLeft.setString(1, rightTaxonId);				
				ResultSet rsGetNamesOnlyInLeft = stGetNamesOnlyInLeft.executeQuery();
				
				while (rsGetNamesOnlyInLeft.next()){
					elements.add(parseRelationshipPairTaxaEntry(rsGetNamesOnlyInLeft));
				}
				rsGetNamesOnlyInLeft.close();    	
				stGetNamesOnlyInLeft.close();
				
				//Names only in taxon2
				String sqlGetNamesOnlyInRight = "SELECT  '' as leftTaxonId, '' as leftName, '' as leftComment, 'not match' as relType,  r.taxonId as rightTaxonId, r.nameXMap as rightName, r.comment as rightComment, IF(nl.taxonId IS NULL, '0', '1') as inOthers, '1' as nameLevel" +
					" FROM "+tableGetRelTaxaRight+" as r" +
					" 	left join "+tableRelTaxaElemsInCommon+" as ec on ec.rightName = r.nameXMap" +
					" 	left join "+tableNameuse1+" as nl on nl." + nameIdNameUsed + " = r." + nameIdNameUsed +
					" WHERE ec.rightTaxonId is null and (nl.taxonId is null or nl.taxonId!=?);";								
				PreparedStatement stGetNamesOnlyInRight = helper.createGeneratedKeysPreparedStatement(conn,sqlGetNamesOnlyInRight);  
				stGetNamesOnlyInRight.setString(1, leftTaxonId);				
				ResultSet rsGetNamesOnlyInRight = stGetNamesOnlyInRight.executeQuery();
				
				while (rsGetNamesOnlyInRight.next()){
					elements.add(parseRelationshipPairTaxaEntry(rsGetNamesOnlyInRight));
				}
				rsGetNamesOnlyInLeft.close();     
				stGetNamesOnlyInRight.close();    			
	    	}
	    	else{ //Taxa higher rank    
	    		//Fil tables
            	String sqlInsertElementsInGetRelTaxaLeft = "insert into " + tableGetRelTaxaLeft + " (taxonId,nameXMap,comment) " +
            			" select n.taxonId,n.tidyName,n.rank from " + tableNameuse1 +" as n " +
            			"	inner join " + tableTaxon1 +" as t on t.id = n.taxonId " +
            			" where t.parentId = ? and t.parentId!=t.id and n.status='accepted';";

            	String sqlInsertElementsInGetRelTaxaRight = "insert into " + tableGetRelTaxaRight + " (taxonId,nameXMap,comment) " +
            			" select n.taxonId,n.tidyName,n.rank from " + tableNameuse2 +" as n " +
            			"	inner join " + tableTaxon2 +" as t on t.id = n.taxonId " +
            			" where t.parentId = ? and t.parentId!=t.id and n.status='accepted';";       
	    		
            	PreparedStatement stInsertElements1 = helper.createGeneratedKeysPreparedStatement(conn,sqlInsertElementsInGetRelTaxaLeft);
            	stInsertElements1.setString(1, leftTaxonId);
    			
            	PreparedStatement stInsertElements2 = helper.createGeneratedKeysPreparedStatement(conn,sqlInsertElementsInGetRelTaxaRight);
                stInsertElements2.setString(1, rightTaxonId);
                
                stInsertElements1.executeUpdate();
                stInsertElements2.executeUpdate();                  	
	    		
	    		//Children in common between taxon1 and taxon2
	    		String sqlGetChildrenInCommon = "SELECT distinct l.taxonId as leftTaxonId, l.nameXMap as leftName, l.comment as leftComment,f.relationship as relType, r.taxonId as rightTaxonId, r.nameXMap as rightName, r.comment as rightComment, '0' as inOthers, '0' as nameLevel" +
					" FROM " + flatTable + " as f"+
					"	inner join "+tableGetRelTaxaLeft+" as l on l.taxonId = f.taxId1"+
					" 	inner join "+tableGetRelTaxaRight+" as r on r.taxonId = f.taxId2"+
					"	inner join "+ tableTaxon1+" as t1 on t1.id = f.taxId1" +
					"	inner join "+ tableTaxon2+" as t2 on t2.id = f.taxId2" +
					" WHERE t1.parentId=? and t2.parentId=? and t1.id!=t1.parentId and t2.id!=t2.parentId;";
					PreparedStatement stGetChildrenInCommon = helper.createGeneratedKeysPreparedStatement(conn,sqlGetChildrenInCommon);    						
				stGetChildrenInCommon.setString(1, leftTaxonId);
				stGetChildrenInCommon.setString(2, rightTaxonId);
				ResultSet rsGetChildrenInCommon = stGetChildrenInCommon.executeQuery();
				
				while (rsGetChildrenInCommon.next()){
					elements.add(parseRelationshipPairTaxaEntry(rsGetChildrenInCommon));
				}
				rsGetChildrenInCommon.close();
				stGetChildrenInCommon.close();
				
				//Children only in taxon1 and not in taxon2 ((but could be in other taxon of the right checklist))
				String sqlGetChildrenOnlyInLeft = "SELECT distinct l.taxonId as leftTaxonId, l.nameXMap as leftName, l.comment as leftComment, 'not match' as relType, '' as rightTaxonId, '' as rightName, '' as rightComment, IF(t2.parentId IS NULL, '0', '1') as inOthers, '0' as nameLevel" +
					" FROM " + tableGetRelTaxaLeft + " as l"+
					"	inner join "+ tableTaxon1+" as t1 on t1.id = l.taxonId" +	
					"	left join " + flatTable + " as f on f.taxId1 = l.taxonId"+
					"	left join "+ tableTaxon2+" as t2 on t2.id = f.taxId2"+	
					" WHERE t1.parentId=? and t1.id!=t1.parentId and (t2.id is null or t2.parentId!=?);";						
				PreparedStatement stGetChildrenOnlyInLeft = helper.createGeneratedKeysPreparedStatement(conn,sqlGetChildrenOnlyInLeft);    						
				stGetChildrenOnlyInLeft.setString(1, leftTaxonId);
				stGetChildrenOnlyInLeft.setString(2, rightTaxonId);
				ResultSet rsGetChildrenOnlyInLeft = stGetChildrenOnlyInLeft.executeQuery();
				
				while (rsGetChildrenOnlyInLeft.next()){
					elements.add(parseRelationshipPairTaxaEntry(rsGetChildrenOnlyInLeft));
				}
				rsGetChildrenOnlyInLeft.close();
				stGetChildrenOnlyInLeft.close();
				
	    		//Children only in taxon2 and not in taxon1 (but could be in other taxon of the left checklist)
				String sqlGetChildrenOnlyInRight = "SELECT distinct '' as leftTaxonId, '' as leftName, '' as leftComment, 'not match' as relType, r.taxonId as rightTaxonId, r.nameXMap as rightName, r.comment as rightComment, IF(t1.parentId IS NULL, '0', '1') as inOthers, '0' as nameLevel" +
					" FROM " + tableGetRelTaxaRight + " as r"+
					"	inner join " + tableTaxon2 + " as t2 on t2.id = r.taxonId" +	
					"	left join " + flatTable + " as f on f.taxId2 = r.taxonId"+
					"	left join " + tableTaxon1 + " as t1 on t1.id = f.taxId1"+	
					" WHERE t2.parentId=? and t2.id!=t2.parentId and (t1.id is null or t1.parentId!=?);";					
				
				PreparedStatement stGetChildrenOnlyInRight = helper.createGeneratedKeysPreparedStatement(conn,sqlGetChildrenOnlyInRight);    										
				stGetChildrenOnlyInRight.setString(1, rightTaxonId);
				stGetChildrenOnlyInRight.setString(2, leftTaxonId);
				ResultSet rsGetChildrenOnlyInRight = stGetChildrenOnlyInRight.executeQuery();
				
				while (rsGetChildrenOnlyInRight.next()){
					elements.add(parseRelationshipPairTaxaEntry(rsGetChildrenOnlyInRight));
				}
				rsGetChildrenOnlyInRight.close();
				stGetChildrenOnlyInRight.close();					    		
	    	}		
	    	return elements;
		}
		catch (SQLException ex){
			throw new DAOReadException(ex);	
		}	    	
		finally{
			//Drop temp tables that have the comunalities between the 2 taxa
			dropTablesForGetXMapRelationshipDetail(xMapId,leftTaxonId,rightTaxonId,conn);			
		}	    	
	}	
	
	private TaxonomicRank getTaxonomicRankByName(String name, Connection conn) throws DAOException{
		TaxonomicRank rank = null;		
		PreparedStatement st=null;
		try{			
			String sql = "select id,rank,parentId,isHigherRank from TaxonomicRankHierarchy where rank=?;";			
			st = helper.createGeneratedKeysPreparedStatement(conn,sql);
			st.setString(1, name);
			
			ResultSet rs = st.executeQuery();
            if (rs.next()){
            	rank = parseTaxonomicRank(rs);		            	
            } 
            rs.close();
            
            return rank;
		}
		catch (SQLException ex){
			throw new DAOReadException(ex);	
		}	
		finally{
			closeStatementWithoutException(st);
		}			
	}	
	
	
	//Get all taxa in right checklist related to taxonId
    private List<String> getTaxaInRightRelatedToTaxon(String xMapId, String taxonId, Connection conn) throws DAOException{
    	List<String> taxaInRightRelatedToTaxonId = new ArrayList<String>();		
		PreparedStatement st=null;
		try{	
			if (!taxonId.equalsIgnoreCase("-1")){
				XMap xmap = getXMap(xMapId,conn);
				String flatTable = getTableNameXmapFlat(xmap.getPrefixTableDB());
				
				String sql = "select distinct taxId2 from "+flatTable+" where taxId1=?;";			
				st = helper.createGeneratedKeysPreparedStatement(conn,sql);
				st.setString(1, taxonId);
				
				ResultSet rs = st.executeQuery();
	            while (rs.next()){
	            	taxaInRightRelatedToTaxonId.add(rs.getString("taxId2"));		            	
	            } 
	            rs.close();
			}    
	        return taxaInRightRelatedToTaxonId;
		}
		catch (SQLException ex){
			throw new DAOReadException(ex);	
		}	
		finally{
			closeStatementWithoutException(st);
		}			
	}	
    
	//Get all taxa in left checklist related to taxonId
    private List<String> getTaxaInLeftRelatedToTaxon(String xMapId, String taxonId, Connection conn) throws DAOException{
    	List<String> taxaInLeftRelatedToTaxonId = new ArrayList<String>();		
		PreparedStatement st=null;
		try{	
			if (!taxonId.equalsIgnoreCase("-1")){
				XMap xmap = getXMap(xMapId,conn);
				String flatTable = getTableNameXmapFlat(xmap.getPrefixTableDB());
				
				String sql = "select distinct taxId1 from "+flatTable+" where taxId2=?;";			
				st = helper.createGeneratedKeysPreparedStatement(conn,sql);
				st.setString(1, taxonId);
				
				ResultSet rs = st.executeQuery();
	            while (rs.next()){
	            	taxaInLeftRelatedToTaxonId.add(rs.getString("taxId1"));		            	
	            } 
	            rs.close();
			}
			 return taxaInLeftRelatedToTaxonId;
		}
		catch (SQLException ex){
			throw new DAOReadException(ex);	
		}	
		finally{
			closeStatementWithoutException(st);
		}			
	}	    


	//Get all taxa in left checklist related to taxonId
    private EntryInChecklist getAcceptedEntryTaxon(String checklistId, String taxonId, Connection conn) throws DAOException{
    	EntryInChecklist acceptedEntry;
		PreparedStatement st=null;
		try{	
			Checklist checklist = getChecklist(checklistId,conn);
			String viewNameUse = getViewNameChkListNameUse(checklist.getPrefixTableDB());				
			
			String sqlGetEntries = "select * from " + viewNameUse + " where taxonId=? and status='accepted';";									
			st = helper.createGeneratedKeysPreparedStatement(conn,sqlGetEntries);			
			st.setString(1, taxonId);
			
			ResultSet rs = st.executeQuery();
            if (rs.next()){
            	acceptedEntry = parseEntryInChecklist(rs);
            }
            else {
            	throw new RuntimeException("Accepted name not found for this taxon");
            }
            rs.close();
            
            
            return acceptedEntry;			
		}
		catch (SQLException ex){
			throw new DAOReadException(ex);	
		}	
		finally{
			closeStatementWithoutException(st);
		}			
	}	    
    
	private List<String> getColumnsInTable(String tableName, Connection conn) throws SQLException{
		List<String> columns = new ArrayList<String>();
		
	    String sqlGetColumnns = "select * from " + tableName + " where true=false";	    				
		PreparedStatement st = helper.createGeneratedKeysPreparedStatement(conn,sqlGetColumnns);			
		
		ResultSet rs = st.executeQuery();		
		ResultSetMetaData md = rs.getMetaData();
		int col = md.getColumnCount();
		for (int i = 1; i <= col; i++){
			columns.add(md.getColumnName(i));
		}				
        rs.close();
        
        return columns;		
	}    

}
