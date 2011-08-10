package org.irods.jargon.idrop.desktop.systraygui.utils;

/**
 * Misc iDrop utilities
 * 
 * @author Mike Conway - DICE (www.irods.org)
 */
public class IDropUtils {

    /**
     * Given a file name, return the name up to 100 chars, otherwise, redact and
     * abbreviate
     * 
     * @param fileName
     * @return
     */
    public static final String abbreviateFileName(final String fileName) {

        if (fileName == null) {
            throw new IllegalArgumentException("null fileName");
        }

        StringBuilder sb = new StringBuilder();
        if (fileName.length() < 100) {
            sb.append(fileName);
        } else {
            // gt 100 bytes, redact
            sb.append(fileName.substring(0, 50));
            sb.append(" ... ");
            sb.append(fileName.substring(fileName.length() - 50));
        }

        return sb.toString();

    }
}
