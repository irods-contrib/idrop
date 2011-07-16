/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.irods.jargon.idrop.desktop.systraygui.viscomponents;

import java.io.File;

/**
 *
 * @author mikeconway
 */
public interface RemoteChooserView {
        /** 
    * Returns the children of the parent directory. 
    */ 
    public File[] getChildren(File aDir); 
    /** 
    * Returns the root (in this case, Baseball). 
    */ 
    public File getRoot(); 
  
    /** 
    * Creates a new folder in the containing directory. 
    */ 
    public File createNewFolder(File aContainingDir); 
    /** 
    * Determines if the file should be displayed 
    * based on the current filter. 
    */ 
    public boolean acceptFilter(File aFile, 
    String aCurrentFilter); 
}
