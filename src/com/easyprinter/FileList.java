
package com.easyprinter;

public class FileList {
	private String path;
	private String name;
	private String completepath;
	
	/**
	 * Constructor for the file list
	 * @param path	the path
	 * @param name	the name
	 */
	public FileList(String path, String name){
		this.name = name;
		this.path = path;
		this.completepath = path + "/" + name;
	}
	
	public String getAbsolutePath(){
		return completepath;
	}
	
	/**
	 * get the path of the file
	 * @return		the path
	 */
	public String getPath(){
		return path;
	}
	
	/**
	 * get the name of the file
	 * @return		the file's name
	 */
	public String getName(){
		return name;
	}
}
