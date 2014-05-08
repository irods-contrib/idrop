package org.irods.mydrop.config


/**
 * Value class to hold important iDrop server state in session
 * @author Mike Conway - DICE (www.irods.org)
 */
class ViewState implements Serializable {

	String rootPath = ""
	String browseView = ""
	String selectedPath = ""
}
