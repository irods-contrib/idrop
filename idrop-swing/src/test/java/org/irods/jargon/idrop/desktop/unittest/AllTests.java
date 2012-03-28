/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.irods.jargon.idrop.desktop.unittest;

import org.irods.jargon.idrop.desktop.systraygui.services.IdropConfigurationServiceTest;
import org.irods.jargon.idrop.desktop.systraygui.services.IdropPreDatabaseBootstrapperServiceImplTest;
import org.irods.jargon.idrop.desktop.systraygui.utils.IdropPropertiesHelperTest;
import org.irods.jargon.idrop.desktop.systraygui.utils.LocalFileUtilsTest;
import org.irods.jargon.idrop.desktop.systraygui.viscomponents.*;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({ IdropPropertiesHelperTest.class, LocalFileUtilsTest.class,
    FileSystemModelTest.class, IRODSSearchTableModelTest.class,
    LocalFileSystemModelTest.class, MetadataTableModelTest.class,
    QueueManagerDetailTableModelTest.class, QueueManagerMasterTableModelTest.class, IdropPreDatabaseBootstrapperServiceImplTest.class,IdropConfigurationServiceTest.class
		 })
public class AllTests {

}
