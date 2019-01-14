/*
 * #%L
 * Xmap Utils
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
package org.openbio.xmap.common.utils.net;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;

import javax.activation.DataHandler;

import org.apache.commons.io.FilenameUtils;
import org.openbio.xmap.common.utils.misc.FileUtil;
import org.openbio.xmap.common.utils.misc.Misc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class to provides method to upload and download files using different protocols
 * <p>
 * OpenBio XMap
 * <p>
 * Date: 15/05/12
 * @author Fran
 */
public class NetClient {

	/**
	 * slf4j logger for this class
	 */		
	private static Logger logger = LoggerFactory.getLogger(NetClient.class);	
	
	/****************************************************************************************/
	/* PUBLIC METHODS																		*/														
	/****************************************************************************************/
	
	/**
	 * Method responsible to upload the file received as second parameter to the endpoint received
	 * as first parameter
	 * @param endPoint end point in which we can upload the file
	 * @param fileToUpload file to be uploaded
	 * @throws Exception
	 */
	public void uploadFile (File fileToUpload, String endPointDir, String name) throws Exception{	
		if (endPointDir.startsWith("http")){
			uploadFileToHTTPServer(fileToUpload,endPointDir,name);
		}
		else if(endPointDir.startsWith("ftp")){
			uploadFileToFTPServer(fileToUpload,endPointDir,name);
		}
		else {
			uploadFileToFileSystem(fileToUpload,endPointDir,name);			
		}
	}	

	/**
	 * Method responsible to download the file situated in the endpoint received as parameter 
	 * @param endPoint endpoint of the file to be downloaded
	 * @return file object of the file downloaded
	 * @throws Exception
	 */
	public File downloadFile(String endPoint) throws Exception{
		File file;
		if (endPoint.startsWith("http")){
			file = downloadFileFromHTTPServer(endPoint);
		}
		else if(endPoint.startsWith("ftp")){
			file = downloadFileFromFTPServer(endPoint);
		}
		else {
			file = downloadFileFromFileSystem(endPoint);
		}
        return file;
	}
	
	
	public void saveDataHandlerFileToDirectory(DataHandler binaryData,String fileName, String endPointDirectory) throws Exception{
		//TODO: Implement properly. So far is writting the data hander file in the local file system, given the
		//end point as a htpp url expecting that it provides http visibility
		URL url = this.getClass().getClassLoader().getResource("");			
		File targetDirectory = new File (url.getFile() + "../.." + endPointDirectory.substring(Misc.nthIndexOf(endPointDirectory,'/',4)));
		
    	if (!targetDirectory.exists()){
    		targetDirectory.mkdirs();
    	}    	    	
    	
    	String pathTargetDirectory = FileUtil.getOSIndependentPath(targetDirectory.getCanonicalPath());
    	
    	File file = new File(pathTargetDirectory + "/" + fileName);
    	
    	logger.info("Uploading file " + fileName + " to " + endPointDirectory + " being the final path directory " + pathTargetDirectory);
    	
    	if (file.exists()){
    		throw new RuntimeException("There is already a file with this name in the server");
    	}
    	else{
	    	FileOutputStream outputStream = new FileOutputStream(file);	    
	    	binaryData.writeTo(outputStream);    
	    	outputStream.flush();
	    	outputStream.close();
    	}	    		    		    		
	}			
	
	
	/**
	 * Method that given a path e
	 * @param endPointDirectory
	 * @return
	 * @throws IOException
	 */	
	public String getLocalPathFolderForHTTPEndPoint(String endPointDirectory) throws IOException{
		//TODO: Implement properly
		URL url = this.getClass().getClassLoader().getResource("");			
		File targetDirectory = new File (url.getFile() + "../.." + endPointDirectory.substring(Misc.nthIndexOf(endPointDirectory,'/',4)));
		
    	if (!targetDirectory.exists()){
    		targetDirectory.mkdirs();
    	}  
		
		return FileUtil.getOSIndependentPath(targetDirectory.getCanonicalPath());	
	}
	
	
	public String getLocalPathFileForHTTPEndPoint(String endPointFile) throws IOException{
		//TODO: Implement properly
		URL url = this.getClass().getClassLoader().getResource("");			
		File targetFile = new File (url.getFile() + "../.." + endPointFile.substring(Misc.nthIndexOf(endPointFile,'/',4)));
	
		return FileUtil.getOSIndependentPath(targetFile.getCanonicalPath());	
	}	
	
	/****************************************************************************************/
	/* PRIVATE METHODS																		*/														
	/****************************************************************************************/		
	
	/**
	 * Method responsible to upload the file to the endpoint using http
	 * @param endPoint
	 * @param fileToUpload
	 * @throws Exception
	 */
	private void uploadFileToHTTPServer(File fileToUpload,String targetEndPointDirectory,String targetFileName) throws Exception{
		//TODO: Implement properly. So far is writting the file in the local file system expecting that
		//it provides http visibility
				
		URL url = this.getClass().getClassLoader().getResource("");			
		File targetDirectory = new File (url.getFile() + "../.." + targetEndPointDirectory.substring(Misc.nthIndexOf(targetEndPointDirectory,'/',4)));
		
		if (!targetDirectory.exists()){
    		targetDirectory.mkdirs();
    	}    	   		
		
		String localPathTargetDirectory = FileUtil.getOSIndependentPath(targetDirectory.getCanonicalPath());
		File targetFile = new File(localPathTargetDirectory + "/" + targetFileName);
		
		logger.info("Uploading file " + fileToUpload.getName() + " to " + targetEndPointDirectory + " being the final path directory " + localPathTargetDirectory);
		
		FileUtil.copyInputStream(new FileInputStream(fileToUpload),new FileOutputStream(targetFile));		
	}	

	/**
	 * Method responsible to upload the file to the endpoint using ftp
	 * @param endPoint
	 * @param fileToUpload
	 * @throws Exception
	 */
	private void uploadFileToFTPServer(File fileToUpload,String targetEndPointDirectory,String targetFileName) throws Exception{		
		URL  url = new URL(targetEndPointDirectory + "/" + targetFileName + ";type=i");
		URLConnection urlConnection = url.openConnection();
		urlConnection.connect();

		FileUtil.copyInputStream(new FileInputStream(fileToUpload),urlConnection.getOutputStream());
	}
	
	/**
	 * Method responsible to upload the file to the endpoint using scp
	 * @param endPoint
	 * @param fileToUpload
	 * @throws Exception
	 */
	private void uploadFileToFileSystem(File fileToUpload,String targetEndPointDirectory,String targetFileName) throws Exception{	
		if (!targetEndPointDirectory.startsWith("file")){
			targetEndPointDirectory = "file:///" + targetEndPointDirectory;
		}
		
		URL url = new URL(targetEndPointDirectory+"/"+targetFileName);
		File targetFile;
		try {
			targetFile = new File(url.toURI());
		} catch(URISyntaxException e) {
			targetFile = new File(url.getPath());
		}					
		FileUtil.copyInputStream(new FileInputStream(fileToUpload),new FileOutputStream(targetFile));		
	}		

	/**
	 * Method responsible to download the file from the endpoint using http
	 * @param endPoint
	 * @return file object of the file downloaded
	 * @throws Exception
	 */
	private File downloadFileFromHTTPServer(String endPoint) throws Exception{
		URL  url = new URL(endPoint);
		URLConnection urlConnection = url.openConnection();
		urlConnection.connect();
		
		String fileName = FilenameUtils.removeExtension(FilenameUtils.getName(url.getFile()));
		String fileExtension = "." + FilenameUtils.getExtension(url.getFile());	
		
		File file = File.createTempFile(fileName, fileExtension);	
		FileUtil.copyInputStream(urlConnection.getInputStream(),new FileOutputStream(file.getAbsoluteFile()));        

		return file;
	}

	/**
	 * Method responsible to download the file from the endpoint using ftp
	 * @param endPoint
	 * @return file object of the file downloaded
	 * @throws Exception
	 */
	private File downloadFileFromFTPServer(String endPoint) throws Exception{
		URL  url = new URL(endPoint + ";type=i");
		URLConnection urlConnection = url.openConnection();
		urlConnection.connect();
		
		String fileName = FilenameUtils.removeExtension(FilenameUtils.getName(url.getFile()));
		String fileExtension = "." + FilenameUtils.getExtension(url.getFile());		
		
		File file = File.createTempFile(fileName, fileExtension);	
		FileUtil.copyInputStream(urlConnection.getInputStream(),new FileOutputStream(file.getAbsoluteFile()));

		return file;
	}	
	
	/**
	 * Method responsible to download the file from the endpoint using scp
	 * @param endPoint
	 * @return file object of the file downloaded
	 * @throws Exception
	 */
	private File downloadFileFromFileSystem(String endPoint) throws Exception{
		if (!endPoint.startsWith("file")){
			endPoint = "file://" + endPoint;
		}		
		
		URL url = new URL(endPoint);
		URLConnection urlConnection = url.openConnection();
		urlConnection.connect();
		
		String fileName = FilenameUtils.removeExtension(FilenameUtils.getName(url.getFile()));
		String fileExtension = "." + FilenameUtils.getExtension(url.getFile());			
		
		File file = File.createTempFile(fileName, fileExtension);
		FileUtil.copyInputStream(urlConnection.getInputStream(),new FileOutputStream(file.getAbsoluteFile()));        

		return file;
	}	


}
