/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.irods.jargon.idrop.desktop.systraygui.services;

import java.io.File;
import java.util.Properties;
import junit.framework.TestCase;
import org.irods.jargon.idrop.desktop.systraygui.IDROPCore;
import org.irods.jargon.testutils.TestingPropertiesHelper;
import org.irods.jargon.testutils.filemanip.ScratchFileUtils;
import org.irods.jargon.transfer.TransferServiceFactoryImpl;
import org.irods.jargon.transfer.engine.ConfigurationService;
import org.junit.*;
import static org.junit.Assert.*;

/**
 *
 * @author mikeconway
 */
public class IdropPreDatabaseBootstrapperServiceImplTest {

    private static ConfigurationService configurationService;
    private static Properties testingProperties = new Properties();
    private static TestingPropertiesHelper testingPropertiesHelper = new TestingPropertiesHelper();
    private static ScratchFileUtils scratchFileUtils = null;
    private static final String TESTING_SUBDIR = "IdropPreDatabaseBootstrapperServiceImplTest";
    private static IdropPreDatabaseBootstrapperService preDatabaseBootstrapperService;

    public IdropPreDatabaseBootstrapperServiceImplTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        TestingPropertiesHelper testingPropertiesLoader = new TestingPropertiesHelper();
        testingProperties = testingPropertiesLoader.getTestProperties();
        scratchFileUtils = new ScratchFileUtils(testingProperties);
        scratchFileUtils.clearAndReinitializeScratchDirectory(TESTING_SUBDIR);
        preDatabaseBootstrapperService = new IdropPreDatabaseBootstrapperServiceImpl();
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testDetectPriorVersionWhenSet() throws Exception {
        String testIdropSubdir = "testDetectPriorVersionWhenNoPriorVersion";
        String version = "3.1.0";
        String absPath = scratchFileUtils.createAndReturnAbsoluteScratchPath(TESTING_SUBDIR);
        preDatabaseBootstrapperService.storePriorVersion(absPath, version);
        String returnedVersion = preDatabaseBootstrapperService.detectPriorVersion(absPath);
        TestCase.assertEquals("did not get same version I set", version, returnedVersion);
    }

    @Test
    public void testDetectPriorVersionWhenNotSet() throws Exception {
        String testIdropSubdir = "testDetectPriorVersionWhenNotSet";
        String absPath = scratchFileUtils.createAndReturnAbsoluteScratchPath(TESTING_SUBDIR + "/" + testIdropSubdir);
        File versionFile = new File(absPath);
        versionFile.delete();
        String returnedVersion = preDatabaseBootstrapperService.detectPriorVersion(absPath);
        TestCase.assertNull("should not get a version back", returnedVersion);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testStoreVersionNullFile() throws Exception {
        preDatabaseBootstrapperService.storePriorVersion(null, "xx");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testStoreVersionBlankVersion() throws Exception {
        preDatabaseBootstrapperService.storePriorVersion("xx", "");
    }
}
