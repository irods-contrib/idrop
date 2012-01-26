package org.irods.jargon.idrop.lite;

import java.io.File;

public class UploadDataObj {
	
	private File sourceFile = null;
	private String sourceFileName = null;
	Boolean isURL = false;
	
	public UploadDataObj(File file) {
		this.sourceFile = file;
	}
	
	public UploadDataObj(File file, Boolean isURL) {
		this.sourceFile = file;
		this.isURL = isURL;
	}
	
	public UploadDataObj(String file) {
		this.sourceFileName = file;
	}
	
	public UploadDataObj(String file, Boolean isURL) {
		this.sourceFileName = file;
		this.isURL = isURL;
	}
	
	public void setFile(File file) {
		this.sourceFile = file;
	}
	
	public File getFile() {
		return this.sourceFile;
	}
	
	public void setFileName(String name) {
		this.sourceFileName = name;
	}
	
	public String getFileName() {
		return this.sourceFileName;
	}
	
	public Boolean isURL() {
		return this.isURL;
	}
	
	public void isURL(Boolean flag) {
		this.isURL = flag;
	}

}
