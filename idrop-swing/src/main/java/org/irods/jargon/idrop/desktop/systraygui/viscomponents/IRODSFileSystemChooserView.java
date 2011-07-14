package org.irods.jargon.idrop.desktop.systraygui.viscomponents;

import java.io.File;
import java.io.IOException;
import javax.swing.filechooser.FileSystemView;
import org.irods.jargon.core.exception.JargonException;
import org.irods.jargon.core.pub.IRODSFileSystem;
import org.irods.jargon.core.pub.io.IRODSFile;
import org.irods.jargon.idrop.desktop.systraygui.IDROPCore;
import org.irods.jargon.idrop.exceptions.IdropRuntimeException;
import org.openide.util.Exceptions;
import org.slf4j.LoggerFactory;

/**
 * Model for an iRODS file chooser.  Note that the irodsFIleSystem must be externally managed, such that the connection is closed
 * when the dialog is done
 * @author Mike Conway - DICE (www.irods.org)
 */
public class IRODSFileSystemChooserView extends FileSystemView {

    
    private final IDROPCore idropCore;
    private final IRODSFileSystem irodsFileSystem;
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(IRODSFileSystemChooserView.class);
    private File defaultDirectory = null;
    public static final String NEW_FOLDER = "New Folder";
        
    @Override
    public File getHomeDirectory() {
        return getDefaultDirectory();
    }

    IRODSFileSystemChooserView(final IDROPCore idropCore) {
        if (idropCore == null) {
            throw new IllegalArgumentException("null idropCore");
        }
        this.idropCore = idropCore;
        this.irodsFileSystem = idropCore.getIrodsFileSystem();
    }

    @Override
    public File createFileObject(File file, String fileName) {
        if (file == null) {
            throw new IllegalArgumentException("null file");
        }

        if (fileName == null || fileName.isEmpty()) {
            throw new IllegalArgumentException("fileName is null or empty");
        }

        log.info("createFileObject");
        log.info("parent:{}", file.getAbsolutePath());
        log.info("fileName:{}", fileName);
        IRODSFile newFile;
        try {
            newFile = idropCore.getIRODSFileFactoryForLoggedInAccount().instanceIRODSFile(file.getAbsolutePath(), fileName);
            //newFile.mkdirs();

        } catch (Exception ex) {
            log.error("error creating file", ex);
            throw new IdropRuntimeException("error creating file", ex);
        } 

        log.info("irodsFile created");
        return (File) newFile;
    }

    @Override
    public File createFileObject(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            throw new IllegalArgumentException("fileName is null or empty");
        }

        log.info("createFileObject");

        log.info("fileName:{}", fileName);
        IRODSFile newFile;
        try {
            newFile = idropCore.getIRODSFileFactoryForLoggedInAccount().instanceIRODSFile(fileName);
           // newFile.createNewFile();

        } catch (Exception ex) {
            log.error("error creating file", ex);
            throw new IdropRuntimeException("error creating file", ex);
        }

        log.info("irodsFile created");
        return (File) newFile;
    }

    @Override
    protected File createFileSystemRoot(File file) {
        return super.createFileSystemRoot(file);
    }

    @Override
    public File getChild(File file, String fileName) {

        if (file == null) {
            throw new IllegalArgumentException("null file");
        }
        
        if (fileName == null || fileName.isEmpty()) {
            throw new IllegalArgumentException("fileName is null or empty");
        }

        log.info("getChild");

        log.info("fileName:{}", fileName);
        log.info("file:{}", file.getAbsolutePath());
        IRODSFile newFile;
        try {
            newFile = idropCore.getIRODSFileFactoryForLoggedInAccount().instanceIRODSFile(fileName);

        } catch (Exception ex) {
            log.error("error creating file", ex);
            throw new IdropRuntimeException("error creating file", ex);
        }

        log.info("irodsFile retrieved");
        return (File) newFile;
    }

    @Override
    public File getDefaultDirectory() {
        // cache this for a bit more performance
        if (defaultDirectory != null) {
            return defaultDirectory;
        }
        
        String root = null;
        if (idropCore.getIdropConfig().isLoginPreset()) {
            log.info("using policy preset home directory");
            StringBuilder sb = new StringBuilder();
            sb.append("/");
            sb.append(idropCore.getIrodsAccount().getZone());
            sb.append("/");
            sb.append("home");
            root = sb.toString();
        } else {
            log.info("using root path, no login preset");
            root = "/";
        }
        IRODSFile newFile;
        try {
            newFile = idropCore.getIRODSFileFactoryForLoggedInAccount().instanceIRODSFile(root);
        } catch (Exception ex) {
            log.error("error creating file", ex);
            throw new IdropRuntimeException("error creating file", ex);
        }

        log.info("irodsFile retrieved");
        defaultDirectory = (File) newFile;
        return defaultDirectory;
    }

    @Override
    public File[] getFiles(File file, boolean showHidden) {
       
        log.info("getFiles");
         if (file == null) {
            throw new IllegalArgumentException("null file");
        }
    
        log.info("file:{}", file.getAbsolutePath());
        IRODSFile newFile;
        try {
            newFile = (IRODSFile) file;
             log.info("irodsFiles listing...");
            return newFile.listFiles();
        } catch (Exception ex) {
            log.error("error listing file", ex);
            throw new IdropRuntimeException("error listing file", ex);
        }
    }

    @Override
    public File[] getRoots() {
        log.info("getRoots() returns the default directory");
       File[] roots = { getDefaultDirectory()};
       return roots;
    }

    @Override
    public String getSystemDisplayName(File file) {
        return file.getName();
    }

    @Override
    public boolean isFileSystem(File file) {
       return true;
    }

    @Override
    public boolean isHiddenFile(File file) {
        // FIXME: implement this
        return super.isHiddenFile(file);
    }

    @Override
    public boolean isParent(File file, File file1) {
       return true;
    }

    @Override
    public boolean isRoot(File file) {
        log.info("isRoot");
        File rootFile = getDefaultDirectory();
        if (file.equals(rootFile)) {
            return true;
        } else {
            return false;
        }
        
    }

    @Override
    public Boolean isTraversable(File file) {
        return true;
    }

    
    @Override
    public File createNewFolder(File file) throws IOException {
          if (file == null) {
            throw new IllegalArgumentException("null file");
        }
          
        log.info("createNewFolder");
        log.info("folderName:{}", file.getAbsolutePath());
        IRODSFile newFile;
        try {
            newFile = idropCore.getIRODSFileFactoryForLoggedInAccount().instanceIRODSFile(file.getAbsolutePath(), NEW_FOLDER);
            //newFile.mkdirs();
        } catch (Exception ex) {
            log.error("error creating folder", ex);
            throw new IdropRuntimeException("error creating folder", ex);
        } 

        log.info("irodsFile created");
        return (File) newFile;
    }
}
