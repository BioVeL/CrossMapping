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
package org.openbio.xmap.common.dao.importer.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import javax.sql.DataSource;
import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.openbio.xmap.common.dao.importer.CheckListImporterDAO;
import org.openbio.xmap.common.dao.util.exception.DAOConnectionException;
import org.openbio.xmap.common.dao.util.exception.DAOException;
import org.openbio.xmap.common.dao.util.exception.DAOReadException;
import org.openbio.xmap.common.dao.util.exception.DAOWriteException;
import org.openbio.xmap.common.dao.util.helper.RelationalHelper;
import org.openbio.xmap.common.serviceinterfaces.types.xmap.Checklist;
import org.openbio.xmap.common.serviceinterfaces.types.xmap.TaskId;
import org.openbio.xmap.common.serviceinterfaces.types.xmap.TaskStatus;
import org.openbio.xmap.common.utils.misc.FileUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import au.com.bytecode.opencsv.CSVReader;

public abstract class CheckListImporterDAORelationalImpl implements CheckListImporterDAO {


	/****************************************************************************************/
	/* ATTRIBUTES																			*/														
	/****************************************************************************************/	
	
	private Properties slqProp;	
	
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
	
	
	/****************************************************************************************/
	/* CONSTRUCTORS																			*/		
	/****************************************************************************************/	
	
	/**
	 * Constructor of XMapDAORelationalImpl that receives the datasource to connect with the database and an
	 * object of a class that implements the interface RelationalHelper. 
	 * @param ds datasource to connect to the database
	 * @param helper implementation of the interface RelationalHelper to help with the sql commands
	 * @throws DAOException
	 * @throws IOException 
	 */		
	public CheckListImporterDAORelationalImpl(DataSource ds,RelationalHelper helper) throws DAOException, IOException{
		this.ds = ds;
		this.helper = helper;
		this.slqProp = getPropertiesSQL();
				
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
	public boolean existChecklistNameInRepository(String checklistName, String user, String scope) throws DAOException{
		Connection conn = getConnection();
		PreparedStatement st = null;
		Boolean existChecklistName;
		try{
			String sqlGetChecklistByName = "select id from Checklist where checklistName=? and user=? and scope=?";			
			
			st = helper.createGeneratedKeysPreparedStatement(conn,sqlGetChecklistByName);
			st.setString(1, checklistName);
			st.setString(2, user);
			st.setString(3, scope);
			
			ResultSet rs = st.executeQuery();
            
			if (rs.next()){
				existChecklistName=true;
			}
			else{
				existChecklistName=false;
			}						
            rs.close();
            
            return existChecklistName;
			
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
	public String addChecklistToRepositoy(String checklistName, String fileName, String user, String scope, TaskId taskId) throws DAOException {
		Connection conn = getConnection();
		PreparedStatement stInsert = null;
		PreparedStatement stUpdate = null;		
		try{
			String sqlInsertChecklist = "INSERT INTO Checklist (checklistName,fileName,user,scope,taskId) " +
					" VALUES (?,?,?,?,?);";			
			
			stInsert = helper.createGeneratedKeysPreparedStatement(conn,sqlInsertChecklist);
			stInsert.setString(1, checklistName);
			stInsert.setString(2, fileName);
			stInsert.setString(3, user);
			stInsert.setString(4, scope);
			stInsert.setString(5, taskId.getValue());
			
			stInsert.executeUpdate();
			
			ResultSet rs = stInsert.getGeneratedKeys();
            if ( !rs.next() ) {
                throw new RuntimeException("cannot obtain id of checklist");
            }
            String checklistId = rs.getString(1);
            
            String sqlUpdate = "update Checklist set prefixTableDB=? where id=?;";			
            stUpdate = helper.createGeneratedKeysPreparedStatement(conn,sqlUpdate);
            stUpdate.setString(1, "chk_" + checklistId);
            stUpdate.setString(2, checklistId);            
            stUpdate.executeUpdate();
            
            return checklistId;			
		}
		catch (SQLException ex){
			throw new DAOReadException(ex);			
		}	
		finally{
			closeStatementWithoutException(stInsert);
			closeStatementWithoutException(stUpdate);
			closeConnectionWithoutException(conn);
		}			
	}
	
	@Override
	public void importChecklistIntoTableTaxa(String checklistId, File importFile) throws DAOException {
		try{
			String fileName = importFile.getName();
			int mid = fileName.lastIndexOf(".");
			String ext=fileName.substring(mid+1,fileName.length());			
			CoreFileProperties coreFileProperties = null;
			
			if (ext.equalsIgnoreCase("zip")){
				// A zipped Darwin Core Archive file has been provided.
				File unzippedDir = FileUtil.decompressZipFile(importFile, false);
		        try {
		        	coreFileProperties = getCoreTaxaFilePropertiesInDirectory(unzippedDir);
		        	importChecklistIntoTableTaxa(checklistId, coreFileProperties);          
		        }
				finally {
					FileUtil.deleteDirectory(unzippedDir);
		        }
			}
			else{
				// An simple Darwin Core file has been provided.
				coreFileProperties = getCoreTaxaFilePropertiesInIndividualFile(importFile);				
				importChecklistIntoTableTaxa(checklistId, coreFileProperties); 				
			}
		}
        catch (Exception ex){
        	throw new DAOReadException(ex);
        }	
	}
	
	private CoreFileProperties getCoreTaxaFilePropertiesInDirectory(File unzippedDirDwca) throws SAXException, IOException, ParserConfigurationException, XPathExpressionException{
		CoreFileProperties coreFileProperties = null;
		
		File metaXML = new File (unzippedDirDwca.getPath() + "/meta.xml");			
		if (metaXML.exists()){					
			DocumentBuilderFactory domFactory =  DocumentBuilderFactory.newInstance();
			domFactory.setNamespaceAware(true); 
			Document doc  = domFactory.newDocumentBuilder().parse(metaXML);		
			Element root = doc.getDocumentElement();
						
			XPath xPath =  XPathFactory.newInstance().newXPath();
			xPath.setNamespaceContext(new NamespaceContext(){
					public String getNamespaceURI(String prefix) {
						if ("dwca".equalsIgnoreCase(prefix)){
							return "http://rs.tdwg.org/dwc/text/";
						}
						return null;
					}

	            public String getPrefix(String namespaceURI) {
	                return null; // we are not using this.
	            }
	
	            public Iterator getPrefixes(String namespaceURI) {
	                return null; // we are not using this.
	            }
            });
			
			
			//Get the core element
			XPathExpression xPathExpression = xPath.compile("./dwca:core[1]");
			Node coreElement = (Node) xPathExpression.evaluate(root, XPathConstants.NODE);
			
			//Get location
			xPathExpression = xPath.compile("./dwca:files/dwca:location[1]");
			String locationFile = (String) xPathExpression.evaluate(coreElement,XPathConstants.STRING);
			coreFileProperties = new CoreFileProperties(FileUtil.getOSIndependentPath(unzippedDirDwca.getAbsolutePath() + "/" + locationFile));		
						
			//Get attributes of core element
			//record delimiter
			NamedNodeMap coreAttributes = coreElement.getAttributes();
			Node linesTerminatedBy = coreAttributes.getNamedItem("linesTerminatedBy");
			if (linesTerminatedBy!=null  && linesTerminatedBy.getNodeValue().length()>0){
				coreFileProperties.setRecordDelimiter(linesTerminatedBy.getNodeValue().charAt(0));				
			}
			//field delimiter
			Node fieldsTerminatedBy = coreAttributes.getNamedItem("fieldsTerminatedBy");
			if (fieldsTerminatedBy!=null && fieldsTerminatedBy.getNodeValue().length()>0){
				coreFileProperties.setFieldDelimiter(fieldsTerminatedBy.getNodeValue().charAt(0));				
			}
			//field encloser
			Node fieldsEnclosedBy = coreAttributes.getNamedItem("fieldsEnclosedBy");
			if (fieldsEnclosedBy!=null && fieldsEnclosedBy.getNodeValue().length()>0){
				coreFileProperties.setFieldEncloser(fieldsEnclosedBy.getNodeValue().charAt(0));				
			}	
			//number of header lines
			Node ignoreHeaderLines = coreAttributes.getNamedItem("ignoreHeaderLines");
			if (ignoreHeaderLines!=null){
				coreFileProperties.setNrHeaderLines(Integer.parseInt(ignoreHeaderLines.getNodeValue()));				
			}					
			//encoding
			Node encoding = coreAttributes.getNamedItem("encoding");
			if (ignoreHeaderLines!=null){
				coreFileProperties.setEncoding(encoding.getNodeValue());				
			}	
			
			//Get index id field			
			xPathExpression = xPath.compile("./dwca:id/@index");
			Node nodeIndexId = (Node) xPathExpression.evaluate(coreElement, XPathConstants.NODE);
			if (nodeIndexId!=null){
				coreFileProperties.setIndexIdField(Integer.parseInt(nodeIndexId.getNodeValue()));
			}			
						
			//Get the name of the fields present in the core file
			xPathExpression = xPath.compile("./dwca:field");
			NodeList fields = (NodeList) xPathExpression.evaluate(coreElement, XPathConstants.NODESET);
			for (int i=0;i<fields.getLength();i++){
				Node field = fields.item(i);
				String term = field.getAttributes().getNamedItem("term").getNodeValue();
				coreFileProperties.fields.add(term.substring(term.lastIndexOf('/') + 1, term.length()));
			}					
		}
		else{
			//There is no meta.xml, try if exist possible core files: taxa.txt taxon.txt taxon.tab
			File coreFile=null;
			List<String> potentialCoreFiles = Arrays.asList("taxa.txt","taxon.txt","taxon.tab");
			boolean found = false;
			int i = 0;
			while (i<potentialCoreFiles.size() && !found){
				coreFile = new File (unzippedDirDwca.getPath() + "/" + potentialCoreFiles.get(i));		 
				if (coreFile.exists()){
					found = true;
				}			
				else{
					i++;
				}
			}				 
			if (!found){
				throw new RuntimeException("Can't find a core taxon file in Darwin Core Archive");
			}
			else{				
				coreFileProperties = getCoreTaxaFilePropertiesInIndividualFile(coreFile);
			}
		}		
		
		return coreFileProperties;
	}
	
	private CoreFileProperties getCoreTaxaFilePropertiesInIndividualFile(File coreFile) throws IOException{
		CoreFileProperties coreFileProperties = new CoreFileProperties(FileUtil.getOSIndependentPath(coreFile.getAbsolutePath()));
		coreFileProperties.setFieldDelimiter(getSeparatorCharacter(coreFile));
		
		CSVReader csvReader = new CSVReader(new InputStreamReader(new FileInputStream(coreFileProperties.getFileName()), "UTF-8"), 
				coreFileProperties.getFieldDelimiter(),coreFileProperties.getFieldEncloser(),'\\');	
		String [] header = csvReader.readNext();				
		coreFileProperties.getFields().addAll(Arrays.asList(header));
		csvReader.close();	
		
		return  coreFileProperties;
	}
	
	private Character getSeparatorCharacter(File csvFile) throws IOException{
		Character separator = null;
		List<Character> potentialCharSeparators = Arrays.asList('\t',',',';');
		boolean found = false;
		int i = 0;
		while (i<potentialCharSeparators.size() && !found){		
			separator =  potentialCharSeparators.get(i);
			CSVReader csvReader = new CSVReader(new FileReader(csvFile),separator);
			String[] line1 =  csvReader.readNext();
			String[] line2 =  csvReader.readNext();
			
			if (line1==null || line2==null){
				throw new RuntimeException("The system can't determine the field separator character when the file has less than 2 lines");
			}
			else{
				if (line1.length==line2.length){
					found=true;
				}
				else{
					i++;
				}				
			}
		}
		
		if (!found){
			throw new RuntimeException("Can't not determine the field delimiter character for the file");
		}
		else{
			return separator;
		}
	}
	
	
	private void importChecklistIntoTableTaxa(String checklistId, CoreFileProperties coreFile) throws DAOException {
		Connection conn = getConnection();		
		List<String> sqlDDLsRollback = new ArrayList<String>();		
		try{		    
		    Checklist checklist = getChecklist(checklistId,conn);
		    String prefix = checklist.getPrefixTableDB() + "_";		    
			
			//Check that the essential fields come in the core file
		    if ((!coreFile.getFields().contains("taxonID") && !coreFile.getFields().contains("id") && !coreFile.getFields().contains("ID")) 
		    		|| !coreFile.getFields().contains("scientificName") 
		    		|| !coreFile.getFields().contains("taxonomicStatus")
		    		|| !coreFile.getFields().contains("genus")
		    		|| !coreFile.getFields().contains("specificEpithet")
		    		|| !coreFile.getFields().contains("infraspecificEpithet")
		    		|| !coreFile.getFields().contains("scientificNameAuthorship")
		    		|| !coreFile.getFields().contains("taxonRank")
		    		){
		    	throw new RuntimeException("Some of the essential fields (taxonID,scientificName,taxonomicStatus,genus,specificEpithet,infraspecificEpithet,scientificNameAuthorship)" +
		    			" are missing from input core taxon file");
		    }
		    
		    
		    //Note: Because the dwca file must follow the i4lifeProfile we know that the fields taxonID, acceptedNameUsageID and parentNameUsageID exist		   
		    String sqlCreateTableTaxa = "CREATE TABLE IF NOT EXISTS `"+ prefix +"Raw` (taxonID  VARCHAR(60), ";
		    for (int i=1;i<coreFile.fields.size();i++){
		    	if (coreFile.fields.get(i).equalsIgnoreCase("acceptedNameUsageID") || coreFile.fields.get(i).equalsIgnoreCase("parentNameUsageID")){
		    		sqlCreateTableTaxa += " `" + coreFile.fields.get(i) + "` VARCHAR(60),";
		    	}
		    	else{
		    		sqlCreateTableTaxa += " `" + coreFile.fields.get(i) + "` varchar(255) NOT NULL DEFAULT ''" + (i<coreFile.fields.size()-1?",":");");
		    	}
		    }
		   
		    String sqlAddIndexTableRaw = slqProp.getProperty("addIndexTableRaw");
		    sqlAddIndexTableRaw = sqlAddIndexTableRaw.replace("${prefix}", prefix);
		    
		    			
			String sqlLoadFile = slqProp.getProperty("loadChecklistFile");				
			sqlLoadFile = sqlLoadFile.replace("$importFile", coreFile.getFileName());
			sqlLoadFile = sqlLoadFile.replace("${prefix}", prefix);
			sqlLoadFile = sqlLoadFile.replace("$skipLines", Integer.toString(coreFile.getNrHeaderLines()));			
			
		    //Add ddl instructions for the rollback
		    sqlDDLsRollback.add("DROP TABLE IF EXISTS `"+ prefix +"Raw`;");			
			
			startTransaction(conn);
			helper.executeUpdate(conn, sqlCreateTableTaxa);							
			helper.executeUpdate(conn, sqlLoadFile);
			helper.executeUpdate(conn, sqlAddIndexTableRaw);
			commitTransaction(conn);
		}
		catch (Exception ex){
			rollbackTransaction(conn);
			rollbackDDLs(conn,sqlDDLsRollback);
			throw new DAOWriteException(ex);			
		}	
		finally{
			closeConnectionWithoutException(conn);
		}			
	}


	@Override
	public void fillNameUse(String checklistId) throws DAOException {
		Connection conn = getConnection();	
		List<String> sqlDDLsRollback = new ArrayList<String>();
		try{		
			Checklist checklist = getChecklist(checklistId,conn);
			String prefix = checklist.getPrefixTableDB() + "_";
			List<String> fieldsInRawTable = getColumnsInTable("`"+ prefix +"Raw`",conn);
			
			//Get the sql to be execute from the property file
			String sqlCreateTableNameUse = slqProp.getProperty("createTableNameUse");			
			String sqlFixNameUseRanksSubspecies = slqProp.getProperty("fixNameUseRanksSubspecies");
			String sqlFixNameUseRanksVariety = slqProp.getProperty("fixNameUseRanksVariety");
			String sqlFixNameUseRanksForm = slqProp.getProperty("fixNameUseRanksForm");
			String sqlRemoveMultipleSpacesTidyName = slqProp.getProperty("removeMultipleSpaces");
			
			//Replace template strings with specific values
			sqlCreateTableNameUse = sqlCreateTableNameUse.replace("${prefix}", prefix);
			
			sqlFixNameUseRanksSubspecies = sqlFixNameUseRanksSubspecies.replace("${prefix}", prefix);
			sqlFixNameUseRanksVariety = sqlFixNameUseRanksVariety.replace("${prefix}", prefix);
			sqlFixNameUseRanksForm = sqlFixNameUseRanksForm.replace("${prefix}", prefix);
					
			sqlRemoveMultipleSpacesTidyName = sqlRemoveMultipleSpacesTidyName.replace("$table", prefix + "NameUse");
			sqlRemoveMultipleSpacesTidyName = sqlRemoveMultipleSpacesTidyName.replace("$field", "tidyName");
			sqlRemoveMultipleSpacesTidyName = sqlRemoveMultipleSpacesTidyName.replace("$whereEd", "");
							
			//Add ddl instructions for the rollback
		    sqlDDLsRollback.add("DROP TABLE IF EXISTS `" + prefix + "NameUse`;");				
					
			startTransaction(conn);
			helper.executeUpdate(conn, sqlCreateTableNameUse);		

			//Import accepted names
			String filter = "taxonomicStatus LIKE 'accepted%' OR taxonomicStatus LIKE 'provisional%' OR (taxonomicStatus = ''" + (fieldsInRawTable.contains("acceptedNameUsageID")?" and (acceptedNameUsageID='' or acceptedNameUsageID=taxonID)":"") + ")";
			importBinomial(prefix, fieldsInRawTable, "accepted", filter, "taxonID", "''", (fieldsInRawTable.contains("nameAccordingTo")?"nameAccordingTo":"''"), conn);						
			
			if (fieldsInRawTable.contains("acceptedNameUsageID")){
				//Synonyms
				filter = "(taxonomicStatus = '' and acceptedNameUsageID!=taxonID) or (taxonomicStatus != '' AND taxonomicStatus NOT LIKE 'accepted%' AND taxonomicStatus NOT LIKE 'provisional%' AND taxonomicStatus NOT LIKE 'misappl%')";
				importBinomial(prefix, fieldsInRawTable, "synonym", filter, "acceptedNameUsageID", "''", "taxonomicStatus", conn);

				//Misapplied names
		        filter = "taxonomicStatus LIKE 'misappl%'";
				importBinomial(prefix, fieldsInRawTable, "misapplied", filter, "acceptedNameUsageID", "''", "taxonomicStatus", conn);
			}			

			//helper.executeUpdate(conn, sqlFixGenusSynonyms);			
			helper.executeUpdate(conn, sqlFixNameUseRanksSubspecies);
			helper.executeUpdate(conn, sqlFixNameUseRanksVariety);
			helper.executeUpdate(conn, sqlFixNameUseRanksForm);
			helper.executeUpdate(conn, sqlRemoveMultipleSpacesTidyName);
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
	public void buildAdjacentTree(String checklistId) throws DAOException {
		Connection conn = getConnection();	
		List<String> sqlDDLsRollback = new ArrayList<String>();
		try{			
			String sqlCreateTableTaxon = slqProp.getProperty("createTableCheckListTaxon");
			String sqlAddTopNode = slqProp.getProperty("addTopNode");
			String sqlFillTableTaxon = slqProp.getProperty("buildAdjacencyListTree");
			String sqlUpdateUnplacedNames = slqProp.getProperty("updateUnplacedNames");
			String sqlUpdateParentToTopNode = slqProp.getProperty("updateParentToTopNode");
					
			Checklist checklist = getChecklist(checklistId,conn);
			String prefix = checklist.getPrefixTableDB() + "_";
			List<String> fieldsInRawTable = getColumnsInTable("`"+ prefix +"Raw`",conn);
						
			String sqlCheckTaxonIdZero = "SELECT count(taxonID) as number FROM `" + prefix  + "NameUse` WHERE taxonID = '0'";
			PreparedStatement st = helper.createGeneratedKeysPreparedStatement(conn,sqlCheckTaxonIdZero);			
			ResultSet rs = st.executeQuery();
			if (!rs.next() || rs.getInt("number")>0){
				throw new RuntimeException("Zero is used as a taxon identifier");
			}
						
			sqlCreateTableTaxon = sqlCreateTableTaxon.replace("${prefix}", prefix);
			
			//Add a top node
			sqlAddTopNode = sqlAddTopNode.replace("${prefix}", prefix);
			sqlAddTopNode = sqlAddTopNode.replace("$id", "0");
			
			//Fill table taxon with the hierarchy of the taxa indicated by the accepted names
			sqlFillTableTaxon = sqlFillTableTaxon.replace("${prefix}", prefix);
			sqlFillTableTaxon = sqlFillTableTaxon.replace("$datasetFieldName", (fieldsInRawTable.contains("datasetID")?"datasetID":"''")); 
			sqlFillTableTaxon = sqlFillTableTaxon.replace("$parentNameUsageID", (fieldsInRawTable.contains("parentNameUsageID")?"parentNameUsageID":"'0'")); 
			sqlFillTableTaxon = sqlFillTableTaxon.replace("$uuid", (fieldsInRawTable.contains("identifier")?"identifier":"''"));
			sqlFillTableTaxon = sqlFillTableTaxon.replace("$acceptedFilter","taxonomicStatus LIKE 'accepted%' OR taxonomicStatus LIKE 'provisional%'");
					
			//Names of missing taxa will be marked "unplaced"
			sqlUpdateUnplacedNames = sqlUpdateUnplacedNames.replace("${prefix}", prefix); 
			
			sqlUpdateParentToTopNode = sqlUpdateParentToTopNode.replace("${prefix}", prefix); 
			
			String sqlCreateChecklistView = "CREATE OR REPLACE VIEW `" + prefix + "NameUse_vw` " +
					" AS SELECT n.taxonId, n.tidyName, n.status, n.higher, n.species, n.infraspecies, n.authority, n.rank, t.parentId" + 
					" FROM `" + prefix + "NameUse` as n inner join `" + prefix + "Taxon` as t on t.id=n.taxonId;";
						
									
			//Add ddl instructions for the rollback
		    sqlDDLsRollback.add("DROP TABLE IF EXISTS `"+ prefix +"Taxon`;");							
			sqlDDLsRollback.add("DROP VIEW IF EXISTS `" + prefix + "NameUse_vw`;");
			
			startTransaction(conn);
			helper.executeUpdate(conn, sqlCreateTableTaxon);
			helper.executeUpdate(conn, sqlAddTopNode);
			helper.executeUpdate(conn, sqlFillTableTaxon);
			helper.executeUpdate(conn, sqlUpdateUnplacedNames);
			helper.executeUpdate(conn, sqlUpdateParentToTopNode);			
			helper.executeUpdate(conn, sqlCreateChecklistView);
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
	
	
	public void buildNestedTree(String checklistId) throws DAOException {
		Connection conn = getConnection();	
		try{	
			startTransaction(conn);
			buildNestedTree(checklistId,null,0,conn);
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
	
	private void buildNestedTree(String checklistId,String nodeId, Integer seqNumber, Connection conn) throws SQLException{
		List<String> childrenId = getChildren(checklistId,nodeId,conn);		
		int left=0;
		int right=0;
		
		if (nodeId!=null){
			seqNumber++;
			left = seqNumber;					
		}

		if (childrenId.size()>0){
			for (String childId:childrenId){
				buildNestedTree(checklistId,childId,seqNumber,conn);
			}
		}
				
		if (nodeId!=null){
			right = seqNumber;
			seqNumber++;
			//TODO: Implement
			System.out.println("Update node " + nodeId + " left=" + left + " right=" + right);
			//updateNode(nodeId,left,right);	
		}				
	}
	
	private List<String> getChildren(String checklistId, String nodeId, Connection conn) throws SQLException{
		List<String> children = new ArrayList<String>();
		
	    String sqlGetChildren = "select id from chk_" + checklistId + "_Taxon where";
	    if (nodeId!=null){ 
	    	sqlGetChildren += " parentId='"+nodeId + "';";
	    }
	    else{
	    	sqlGetChildren += " parentId is null";
	    }
	    					
		PreparedStatement st = helper.createGeneratedKeysPreparedStatement(conn,sqlGetChildren);			
		ResultSet rs = st.executeQuery();
        while (rs.next()){
        	children.add(rs.getString("id"));     	
        }
        rs.close();
        
        return children;			
	}


	@Override
	public void preProccessDataForXMap(String checklistId) throws DAOException {
		Connection conn = getConnection();		
		try{
			String sqlProccessHigherTaxa = slqProp.getProperty("processHigerTaxa");
			String sqlProccessSpecies = slqProp.getProperty("processSpecies");
			String sqlProccessInfraspecies = slqProp.getProperty("processInfraspecies");
			
			String sqlCreateFunctionGetAncestor = slqProp.getProperty("createFunctionGetAncestor");
					
			Checklist checklist = getChecklist(checklistId,conn);
			String prefix = checklist.getPrefixTableDB() + "_";
			String sufix = "_" + checklist.getPrefixTableDB() ;
			sqlProccessHigherTaxa = sqlProccessHigherTaxa.replace("${prefix}", prefix);
			sqlProccessSpecies = sqlProccessSpecies.replace("${prefix}", prefix);
			sqlProccessInfraspecies = sqlProccessInfraspecies.replace("${prefix}", prefix);
			sqlCreateFunctionGetAncestor = sqlCreateFunctionGetAncestor.replace("${sufix}", sufix);
			sqlCreateFunctionGetAncestor = sqlCreateFunctionGetAncestor.replace("${prefix}", prefix);
													
			startTransaction(conn);
			helper.executeUpdate(conn, sqlProccessHigherTaxa);				
			helper.executeUpdate(conn, sqlProccessSpecies);
			helper.executeUpdate(conn, sqlProccessInfraspecies);
			helper.executeUpdate(conn, sqlCreateFunctionGetAncestor);
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
	public void deleteImportedChecklist(String checklistId) throws DAOException {				
		Connection conn = getConnection();		
		try{
			Checklist checklist = getChecklist(checklistId,conn);
			
			String tableNameUse = checklist.getPrefixTableDB() + "_NameUse";
			String viewTableNameUse =  checklist.getPrefixTableDB() + "_NameUse_vw";
			String tableTaxon = checklist.getPrefixTableDB() + "_Taxon";
			String tableTaxa = checklist.getPrefixTableDB() + "_Raw";
			String funcGetAncestor = "getAncestor_" + checklist.getPrefixTableDB();
						
			
			String sqlDropViewTableNameUse = "DROP VIEW IF EXISTS " + viewTableNameUse;
			String sqlDropTableNameUse = "DROP TABLE IF EXISTS " + tableNameUse;			
			String sqlDropTableTaxon = "DROP TABLE IF EXISTS " + tableTaxon;
			String sqlDropTableTaxa = "DROP TABLE IF EXISTS " + tableTaxa;
			String sqlDropFuncGetAncestor = "drop function IF EXISTS " + funcGetAncestor;
			String sqlDeleteEntryCrossMap = "delete from Checklist where id='" + checklistId +"'";
			
			
			// TODO Check if there are crossmaps pending or completed that use this checklist									
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
			if (isOuterMostTransaction){
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
	        ResultSet rsTables = metadata.getTables(null, null, "Checklist", null);
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
					" from Checklist as chk inner join Task as t on t.id = chk.taskId " +
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
	
	private Properties getPropertiesSQL() throws IOException{
		Properties p = new Properties();		
		p.load(this.getClass().getResourceAsStream("/sql/CheckListImporterSQL.properties"));
		return p;
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
	
	private void importBinomial(String prefix,List<String>fieldsInRawTable, 
			String status, String filter, String taxonId, String code, String comment, Connection conn) throws SQLException{
		
		PreparedStatement st = null;
		try{
			String sqlImportBinomial = slqProp.getProperty("importBinomialDwCCoL");
			
			String scientificNameAuthorship = "scientificNameAuthorship";
			String specificEpithet= "specificEpithet";
			String infraspecificEpithet= "infraspecificEpithet";			
			String tidyName = "IF ((scientificName LIKE CONCAT('%',scientificNameAuthorship)),scientificName,(IF((specificEpithet is null or specificEpithet=''),scientificName, TRIM(CONCAT_WS(' ', scientificName, scientificNameAuthorship)))))";				
			String higher = "IF((((SUBSTRING_INDEX(scientificName,' ',1)=genus)='0') or (genus='')), SUBSTRING_INDEX(scientificName,' ',1), genus)";
					
			sqlImportBinomial = sqlImportBinomial.replace("${prefix}", prefix);
			sqlImportBinomial = sqlImportBinomial.replace("$tidyName", tidyName); 
			sqlImportBinomial = sqlImportBinomial.replace("$higher", higher);
			sqlImportBinomial = sqlImportBinomial.replace("$specificEpithet", specificEpithet);
			sqlImportBinomial = sqlImportBinomial.replace("$infraspecificEpithet", infraspecificEpithet);
			sqlImportBinomial = sqlImportBinomial.replace("$scientificNameAuthorship", scientificNameAuthorship);
			sqlImportBinomial = sqlImportBinomial.replace("$taxonId",taxonId);
			sqlImportBinomial = sqlImportBinomial.replace("$code",code);
			sqlImportBinomial = sqlImportBinomial.replace("$status",status);
			sqlImportBinomial = sqlImportBinomial.replace("$comment",comment);
			sqlImportBinomial = sqlImportBinomial.replace("$filter",filter);	
			
			st = helper.createGeneratedKeysPreparedStatement(conn,sqlImportBinomial);
			st.executeUpdate();			
		}
		finally{
			closeStatementWithoutException(st);
		}
	}
	
	
	
	private class CoreFileProperties {
		private String fileName;
		private String encoding;
		private int nrHeaderLines;
		private char recordDelimiter;
		private char fieldDelimiter;
		private char fieldEncloser;
		private int indexIdField;
		private List<String> fields;
		
		public String getFileName() {
			return fileName;
		}
		public void setFilename(String fileName) {
			this.fileName = fileName;
		}
		public String getEncoding() {
			return encoding;
		}
		public void setEncoding(String encoding) {
			this.encoding = encoding;
		}
		public int getNrHeaderLines() {
			return nrHeaderLines;
		}
		public void setNrHeaderLines(int nrHeaderLines) {
			this.nrHeaderLines = nrHeaderLines;
		}
		public char getRecordDelimiter() {
			return recordDelimiter;
		}
		public void setRecordDelimiter(char recordDelimiter) {
			this.recordDelimiter = recordDelimiter;
		}
		public char getFieldDelimiter() {
			return fieldDelimiter;
		}
		public void setFieldDelimiter(char fieldDelimiter) {
			this.fieldDelimiter = fieldDelimiter;
		}
		public char getFieldEncloser() {
			return fieldEncloser;
		}
		public void setFieldEncloser(char fieldEncloser) {
			this.fieldEncloser = fieldEncloser;
		}
		public int getIndexIdField() {
			return indexIdField;
		}
		public void setIndexIdField(int indexIdField) {
			this.indexIdField = indexIdField;
		}
		
		public List<String> getFields() {
			return fields;
		}
		public void setFields(List<String> fields) {
			this.fields = fields;
		}		
		
		public CoreFileProperties(String fileName){
			this.fileName = fileName;
			this.encoding = "UTF-8";
			this.nrHeaderLines = 1;
			this.recordDelimiter = '\n';
			this.fieldDelimiter = ',';
			this.fieldEncloser = '\"';
			this.indexIdField = 0;
			this.fields = new ArrayList<String>();
		}						
	}
	
}
