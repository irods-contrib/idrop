package org.irods.jargon.idrop.lite;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.tree.TreePath;
import org.slf4j.LoggerFactory;

/**
 * Utilities for working with local file systems
 * 
 * @author Mike Conway - DICE (www.irods.org)
 */
public class LocalFileUtils {
	private static final org.slf4j.Logger log = LoggerFactory.getLogger(iDropLiteApplet.class);

    public static List<String> listFileRootsForSystem() {
        List<String> fileRoots = new ArrayList<String>();
        File[] roots = File.listRoots();
        for (File root : roots) {
            fileRoots.add(root.getPath());
        }

        return fileRoots;

    }
    
    public static String makeLocalFilePath(TreePath treePath) {
    	
    	String filePath = "";
    	
    	for(Object node: treePath.getPath()) {
    		if(node.toString().equals("/")) continue;
    		filePath += System.getProperty("file.separator") + node.toString();
    	}
    	
    	return filePath;
    }
    
public static String makeLocalFilePath(TreePath treePath, Object drive) {
    	
    	String filePath = "";
    	String truncDrive = "";
    	
    	for(Object node: treePath.getPath()) {
    		if(node.toString().equals("/")) continue;
    		filePath += System.getProperty("file.separator") + node.toString();
    	}
    	
    	if (drive != null) {
    		truncDrive = drive.toString().substring(0, drive.toString().length()-1);
    		return truncDrive + filePath;
    	}
    	else {
    		return filePath;
    	}
    }
}
