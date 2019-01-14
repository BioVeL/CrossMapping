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
package org.openbio.xmap.common.utils.misc;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.io.FilenameUtils;

/**
 * Class containing simple file utility methods
 * <p>
 * OpenBio XMap
 * <p>
 * Date: 15/05/12
 * @author scmjpg 
 */
public class FileUtil {
    
	/****************************************************************************************/
	/* PUBLIC METHODS																		*/														
	/****************************************************************************************/
	
	/**
	 * Method responsible to unzip the file received as parameter
	 * @param zipFile zip file
	 * @param deleteZip boolean that indicate if after decompress the file we should delete the zip file
	 * @return file of directory in which we have unzipped the zip file
	 * @throws IOException
	 */
	public static File decompressZipFile(File zipFile, boolean deleteZip) throws IOException{
		//Decompress DWC-A file if it is compressed	in a temp directory	
		File tmpDir = File.createTempFile(FilenameUtils.removeExtension(zipFile.getName()), "");		
		tmpDir.delete();
		tmpDir.mkdir();				
		unzipFile(tmpDir, zipFile);
		if (deleteZip){
			zipFile.delete();
		}
		return tmpDir;
	}		
	
	
    /**
     * Ensure a file is not readable by other users.
     * Note: This does not do anything on Windows.
     * @param path the name of a file to keep private
     * @throws IOException if modifying the file permissions fails
     */
    public static void ensureFilePrivate(String path) throws IOException {
        if (!System.getProperty("os.name").startsWith("Windows")) {
            Runtime.getRuntime().exec("chmod 600 " + path);
        }
    }
    
    
    /**
     * Method responsible to delete the content of a directory 
     * @param strDir
     * @return
     */
	public static int deleteDirectoryContent(String strDir){		
	    int exitCode = 0;
  	    
	    File directory = new File(strDir); 
	    
	    if (directory!=null && directory.isDirectory()){
		    File[] files = directory.listFiles();
	             
		    for (int i = 0; i < files.length; i++) {
		    	if (files[i].isDirectory()) {
		    		exitCode = deleteDirectoryContent(files[i].getAbsolutePath());
		    		if (exitCode!=0) {
		    			return exitCode;
		    		}
		    	}
		    	if (!files[i].delete()){	    		
	    			return 1;	    		
		    	}
		    }
	    }
		return exitCode;
	}   
	
	/**
	 * Method responsible to delete the directory received as parameter. 
	 * @param directory directory to be deleted
	 * @return true if the directory has been delete correctly, false otherwise
	 */
    public static boolean deleteDirectory(File directory) {
        if (directory.exists()) {
            File[] files = directory.listFiles();
            for (int i=0; i < files.length; i++) {
                if (files[i].isDirectory()) {
                    deleteDirectory(files[i]);
                }
                else {
                    files[i].delete();
                }
            }
        }
        return (directory.delete());
    }	
    

	/**
	 * Method responsible to copy the content of the input file into the output file 
	 * @param inputFile input file to be copied
	 * @param outputFile output file 
	 * @throws IOException
	 */
	public static void copyFile(File inputFile, File outputFile) throws IOException {		
		InputStream in = new FileInputStream(inputFile);
		OutputStream out = new FileOutputStream(outputFile);		
				
		copyInputStream(in,out);
	}    
	
	/**
	 * Method responsible to copy the content of the input stream into the output strem 
	 * @param in input stream to be copied
	 * @param out output stream 
	 * @throws IOException
	 */
	public static void copyInputStream(InputStream in, OutputStream out) throws IOException {
		byte[] buffer = new byte[1024];
		int len;
		while ((len = in.read(buffer)) >= 0) {
			out.write(buffer, 0, len);
		}
		out.flush();
		out.close();
		in.close();		
	}   
	
	
	public static String getOSIndependentPath(String path){
		return path.replace("\\", "/");
	}
	
	
	public static List<String> getListFilesInDirectory(File directory){
		List<String> fileNames = new ArrayList<String>();

		if (directory.exists() && directory.isDirectory()){
	    	File files[] = directory.listFiles();
		    Arrays.sort(files);
		    for (int i = 0, n = files.length; i < n; i++) {       
		    	fileNames.add(files[i].getName());     
		    }	  
		}
	    return fileNames;		
	}	
	
	public static void deleteFile(String filePath){
		File file = new File(filePath);
		if (file.exists()){
			file.delete();
		}	
		else {
			throw new RuntimeException ("File " + file.getName() + " doesn't exist"); 
		}
	}
	
	/****************************************************************************************/
	/* PRIVATE METHODS																		*/														
	/****************************************************************************************/
	
	/**
	 * Method to unzip a zip file into the directory received as parameter 
	 * @param directory directory in which unzip the zip file
	 * @param zipFile zip file to be unzipped
	 * @return list of the file that were inside the zip file  
	 * @throws IOException
	 */
	private static List<File> unzipFile(File directory, File zipFile) throws IOException {
		ZipFile zf = new ZipFile(zipFile);
		Enumeration<? extends ZipEntry> entries = zf.entries();
		List<File> files = new ArrayList<File>();
		while (entries.hasMoreElements()) {
			ZipEntry entry = entries.nextElement();
			if (entry.isDirectory()) {
				//log.warn("ZIP archive contains directories which are being ignored");
				continue;
			}
			String fn = new File(entry.getName()).getName();
			if (fn.startsWith(".")) {
				//log.warn("ZIP archive contains a hidden file which is being ignored");
				continue;
			}
			File targetFile = new File(directory, fn);
			files.add(targetFile);
			//log.debug("Extracting file: " + entry.getName() + " to: " + targetFile.getAbsolutePath());
			copyInputStream(zf.getInputStream(entry), new BufferedOutputStream(new FileOutputStream(targetFile)));			
		}
		zf.close();
		return files;
	}

	
 	
	
}
