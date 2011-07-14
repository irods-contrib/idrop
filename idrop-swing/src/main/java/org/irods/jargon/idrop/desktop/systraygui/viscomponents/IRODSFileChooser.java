package org.irods.jargon.idrop.desktop.systraygui.viscomponents;

import javax.swing.JFileChooser;
import org.irods.jargon.idrop.desktop.systraygui.IDROPCore;

/**
 * Chooser control for an iRODS file system
 * @author Mike Conway - DICE (www.irods.org)
 */
public class IRODSFileChooser extends JFileChooser {

    private final IDROPCore idropCore;
    
    public IRODSFileChooser(final IDROPCore idropCore, final String irodsAbsolutePath,  final IRODSFileSystemChooserView irodsFileSystemChooserView) {
        super(irodsAbsolutePath, irodsFileSystemChooserView);
        if (idropCore == null) {
            throw new IllegalArgumentException("null idropCore");
        }
       
        this.idropCore = idropCore;
        this.setMultiSelectionEnabled(false);
        this.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
    
    }
    
}
