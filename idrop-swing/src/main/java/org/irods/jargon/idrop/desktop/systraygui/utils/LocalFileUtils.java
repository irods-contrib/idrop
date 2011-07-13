package org.irods.jargon.idrop.desktop.systraygui.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Utilities for working with local file systems
 * 
 * @author Mike Conway - DICE (www.irods.org)
 */
public class LocalFileUtils {

    public static List<String> listFileRootsForSystem() {
        List<String> fileRoots = new ArrayList<String>();
        File[] roots = File.listRoots();
        for (File root : roots) {
            fileRoots.add(root.getPath());
        }

        return fileRoots;

    }
}
