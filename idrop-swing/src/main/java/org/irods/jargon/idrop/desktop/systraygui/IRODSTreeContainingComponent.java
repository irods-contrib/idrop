package org.irods.jargon.idrop.desktop.systraygui;

import java.awt.Cursor;

/**
 *
 * @author mikeconway
 */
public interface IRODSTreeContainingComponent {
    
    /**
     * Indicates a refresh action, this causes connections to be held open across node refreshes
     * @return 
     */
    boolean isRefreshingTree();
    
    /**
     * allows proper setting of cursor for tree node operations
     * @param cursor 
     */
    void setCursor(Cursor cursor);
}
