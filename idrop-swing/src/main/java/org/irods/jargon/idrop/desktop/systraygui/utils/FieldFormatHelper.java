
package org.irods.jargon.idrop.desktop.systraygui.utils;

import org.apache.commons.io.FileUtils;
import org.irods.jargon.core.security.IRODSPasswordUtilities;

/**
 * Helper methods for formatting various values for display
 * @author Mike Conway - DICE (www.irods.org)
 */
public class FieldFormatHelper {

    /**
     * Given a file length in <code>bytes</code> return a formatted string (e.g. 2 MB) for display purposes
     * @param bytes
     * @return 
     */
    public static String formatFileLength(long bytes) {
        return FileUtils.byteCountToDisplaySize(bytes);
    }
    
    /**
     * Given a file transfer progress snapshot in bytes, format a display label
     * @param totalBytes
     * @param currentBytes
     * @return 
     */
    public static String formatByteProgress(long totalBytes, long currentBytes, int padValue) {
        StringBuilder sb = new StringBuilder();
        sb.append(formatFileLength(currentBytes));
        sb.append(" / ");
        sb.append(formatFileLength(totalBytes));
        return IRODSPasswordUtilities.pad(sb.toString(), padValue, ' ');
    }
    
      /**
     * Given a file transfer progress snapshot in number of files, format a display label
     * @param totalBytes
     * @param currentBytes
     * @return 
     */
    public static String formatFileProgress(int totalFiles, int currentFiles, int padValue) {
        StringBuilder sb = new StringBuilder();
        sb.append(currentFiles);
        sb.append(" / ");
        sb.append(totalFiles);
        sb.append(" files");
        return IRODSPasswordUtilities.pad(sb.toString(), padValue, ' ');
    }
    
}
