
package org.irods.jargon.idrop.desktop.systraygui.viscomponents;

import org.irods.jargon.datautils.tree.FileTreeModel;

/**
 *  Data representing a diff view
 * @author Mike Conway - DICE
 */
public class DiffViewData {
    private String localAbsolutePath;
    private String irodsAbsolutePath;
    private FileTreeModel fileTreeModel;

    public String getLocalAbsolutePath() {
        return localAbsolutePath;
    }

    public void setLocalAbsolutePath(String localAbsolutePath) {
        this.localAbsolutePath = localAbsolutePath;
    }

    public String getIrodsAbsolutePath() {
        return irodsAbsolutePath;
    }

    public void setIrodsAbsolutePath(String irodsAbsolutePath) {
        this.irodsAbsolutePath = irodsAbsolutePath;
    }

    public FileTreeModel getFileTreeModel() {
        return fileTreeModel;
    }

    public void setFileTreeModel(FileTreeModel fileTreeModel) {
        this.fileTreeModel = fileTreeModel;
    }
    
}
