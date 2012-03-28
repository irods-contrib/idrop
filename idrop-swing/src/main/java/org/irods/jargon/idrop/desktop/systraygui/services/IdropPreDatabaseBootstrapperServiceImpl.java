package org.irods.jargon.idrop.desktop.systraygui.services;

import java.io.*;
import java.util.Properties;
import org.irods.jargon.idrop.desktop.systraygui.utils.IdropPropertiesHelper;
import org.irods.jargon.idrop.exceptions.IdropException;
import org.slf4j.LoggerFactory;

/**
 * Represents an initial bootstrap service that will run before the iDrop database is started. This
 * can do any pre-load checks and potentially can clear or detect any necessary migrations/exports
 * before idrop itself starts
 *
 * @author Mike Conway - DICE (www.irods.org)
 */
public class IdropPreDatabaseBootstrapperServiceImpl implements IdropPreDatabaseBootstrapperService {

    private static final org.slf4j.Logger log = LoggerFactory.getLogger(IdropPreDatabaseBootstrapperServiceImpl.class);

    @Override
    public String detectPriorVersion(final String idropHomeDir) throws IdropException {

        log.info("detectPriorVersion()");

        if (idropHomeDir == null || idropHomeDir.isEmpty()) {
            throw new IllegalArgumentException("null or empty idropHomeDir");
        }

        String priorVersion = null;

        log.info("home dir is:{}", idropHomeDir);

        File versionFile = new File(idropHomeDir, IdropConfigurationService.IDROP_VERSION_FILE_NAME);
        if (versionFile.exists()) {
            log.debug("prior version exists...");
            try {
                priorVersion = readFileAsString(versionFile.getAbsolutePath()).trim();
            } catch (IOException ex) {
                log.error("error reading prior version file", ex);
                throw new IdropException("Unable to read prior version file", ex);
            }
        }

        return priorVersion;

    }

    @Override
    public void storePriorVersion(final String idropHomeDir, final String desiredVersionString) throws IdropException {

        log.info("storePriorVersion()");

        if (idropHomeDir == null || idropHomeDir.isEmpty()) {
            throw new IllegalArgumentException("null or empty idropHomeDir");
        }

        if (desiredVersionString == null || desiredVersionString.isEmpty()) {
            throw new IllegalArgumentException("null or empty desiredVersionString");
        }

        log.info("home dir is:{}", idropHomeDir);
        log.info("desired version string is:{}", desiredVersionString);

        /*
         * wipe out the previous version file and replace
         */
        File versionFile = new File(idropHomeDir,  IdropConfigurationService.IDROP_VERSION_FILE_NAME);
        versionFile.delete();

        try {
            versionFile.createNewFile();
            PrintWriter out = new PrintWriter(versionFile.getAbsolutePath());
            out.println(desiredVersionString.trim());
            out.flush();
            out.close();
        } catch (Exception ex) {
            log.error("error reading prior version file", ex);
            throw new IdropException("Unable to read prior version file", ex);
        }
    }

    /**
     * delete and make an empty .idrop directory in the given location
     *
     * @param idropHomeDir
     * <code>String</code> with the absolute path to the .idrop directory in the users home
     * directory
     * @throws IdropException
     */
    private void reinitializeIdropConfigData(final String idropHomeDir) throws IdropException {

        log.info("reinitializeIdropConfigData()");

        if (idropHomeDir == null || idropHomeDir.isEmpty()) {
            throw new IllegalArgumentException("null or empty idropHomeDir");
        }

        log.info("idropHomeDir:{}...deleting to reinitialize...", idropHomeDir);
        File homeDirFile = new File(idropHomeDir);
        deleteChildDirsAndDir(idropHomeDir);
        homeDirFile.mkdirs();
        log.info("home dir initialized");
    }

    @Override
    public void triggerMigrations(final String idropHomeDirectory, final String priorVersion, final String thisVersion) throws IdropException {

        log.info("triggerMigrations()");

        if (idropHomeDirectory == null || idropHomeDirectory.isEmpty()) {
            throw new IllegalArgumentException("idropHomeDirectory is null or empty");
        }

        if (thisVersion == null || thisVersion.isEmpty()) {
            throw new IllegalArgumentException("null or empty thisVersion");
        }

        /**
         * Right now the only migration is from the point of 3.1 where we started keeping version
         * numbers
         */
        if (priorVersion == null && thisVersion.equals(IdropConfigurationService.VERSION_3_1)) {
            log.warn("migrating to 3.1 from prior version.clear out old database");
            reinitializeIdropConfigData(idropHomeDirectory);
        }

        log.info("migration done");

    }

    /**
     * Load the default idrop properties from the classpath
     *
     * @return
     * <code>Properties</code> of idrop as in the provided class path
     * @throws IdropException
     */
    private Properties getDefaultIdropProperties() throws IdropException {
        log.info("getDefaultIdropProperties");
        IdropPropertiesHelper idropPropertiesHelper = new IdropPropertiesHelper();
        return idropPropertiesHelper.loadIdropProperties();
    }

    /**
     * Given a file path, read the contents of that file into a String value
     *
     * @param filePath
     * @return
     * @throws java.io.IOException
     */
    private static String readFileAsString(String filePath)
            throws java.io.IOException {
        StringBuilder fileData = new StringBuilder();
        BufferedReader reader = new BufferedReader(
                new FileReader(filePath));
        char[] buf = new char[1024];
        int numRead = 0;
        while ((numRead = reader.read(buf)) != -1) {
            String readData = String.valueOf(buf, 0, numRead);
            fileData.append(readData);
            buf = new char[1024];
        }
        reader.close();
        return fileData.toString();
    }

    private static void deleteChildDirsAndDir(String absolutePath) throws IdropException {
        if (absolutePath == null || absolutePath.isEmpty()) {
            throw new IllegalArgumentException("null or empty absolutepath");
        }

        // a little sanity check
        if (absolutePath.length() <= 6) {
            throw new IllegalArgumentException("path is too short, I shouldn't delete this");
        }

        File delFile = new File(absolutePath);
        try {
            delete(delFile);
        } catch (IOException ex) {
            log.error("error on delete of files under {}", absolutePath);
            throw new IdropException("error on delete", ex);
        }
    }

    private static void delete(File f) throws IOException {
        if (f.isDirectory()) {
            for (File c : f.listFiles()) {
                delete(c);
            }
        }
        if (!f.delete()) {
            throw new FileNotFoundException("Failed to delete file: " + f);
        }
    }
}
