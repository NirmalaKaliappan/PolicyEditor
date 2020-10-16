package logic;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;
import net.lingala.zip4j.core.ZipFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.io.*;

public class Unzipper {
	private String temppath = "temp";
	private String Policy;
	private String destination;
	private String source;
	
	public Unzipper(String Policy, String tempSource) {
		this.Policy = Policy;
		String[] file_path_parts;
		String spliter;
		if (File.separator.equals("\\")) {
			spliter = "\\\\";
		} else {
			spliter = File.separator;
		}
		file_path_parts = this.Policy.split(spliter);
		String pol_name = file_path_parts[file_path_parts.length-1];
		if (tempSource != null) {
			this.destination = tempSource;
		} else {
			this.destination = this.temppath + File.separator + pol_name.substring(0,  pol_name.length() - 4);
		}
		this.source = this.Policy;
	}
	
	public String unzip() {
		
		//System.out.println("Working directory: " + System.getProperty("user.dir"));
		//System.out.println("Zip file: " + this.Policy);
		File dirtemp = new File(this.destination);
		if (dirtemp.exists()) {
			try {
				FileUtils.deleteDirectory(dirtemp);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		String returnPath = "";
	    try {
	         ZipFile zipFile = new ZipFile(this.source);
	         zipFile.extractAll(this.destination);
	         returnPath = this.destination;
	         //System.out.println("Files extracted.");
	    } catch (ZipException e) {
	    	returnPath = "";
	        e.printStackTrace();
	    }
	    return returnPath;
	}
	public void zip() {
		ZipFile zipFile;
		ArrayList files = null;
		ArrayList files_collectors = null;
		try {
			zipFile = new ZipFile(this.source);
			ZipParameters parameters = new ZipParameters();
			parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);
			parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);
			//System.out.println("Destination: " + this.destination);
			files = listf(this.destination);
			files_collectors = listf(this.destination + File.separator + "Collectors");
			zipFile.addFiles(files, parameters);
			zipFile.addFolder(this.destination + File.separator + "Collectors", parameters);
			//System.out.println("Files zipped.");
		} catch (ZipException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		/*
		ArrayList files = null;
		files = listf(this.destination);
		for (int i = 0; i < files.size(); i++) {
			File file = new File(files.get(i).toString());
			file.delete();
		}
		for (int i = 0; i < files_collectors.size(); i++) {
			File file = new File(files_collectors.get(i).toString());
			file.delete();
		}*/
		//File dirtodeltete = new File(this.destination + "/Collectors");
		//dirtodeltete.delete();
		//dirtodeltete = new File(this.destination);
		//dirtodeltete.delete();
		//System.out.println("Zipped files deleted.");
	}
	
	public ArrayList<File> listf(String directoryName) {
		ArrayList<File> files = new ArrayList();
	    File directory = new File(directoryName);
	    // get all the files from a directory
	    File[] fList = directory.listFiles();
	    for (File file : fList) {
	        if (file.isFile()) {
	            files.add(file);
	        } else if (file.isDirectory()) {
	            
	        }
	    }
	    return files;
	}
}