package org.irods.mydrop.controller

import grails.test.ControllerUnitTestCase

import java.util.Properties

import org.irods.jargon.core.connection.IRODSAccount
import org.irods.jargon.core.pub.CollectionAndDataObjectListAndSearchAO
import org.irods.jargon.core.pub.DataTransferOperations
import org.irods.jargon.core.pub.IRODSAccessObjectFactory
import org.irods.jargon.core.pub.IRODSFileSystem
import org.irods.jargon.core.pub.domain.Collection
import org.irods.jargon.core.pub.domain.DataObject
import org.irods.jargon.testutils.TestingPropertiesHelper
import org.mockito.Mockito

class FileControllerTests extends ControllerUnitTestCase {

	IRODSAccessObjectFactory irodsAccessObjectFactory
	IRODSAccount irodsAccount
	Properties testingProperties
	TestingPropertiesHelper testingPropertiesHelper
	IRODSFileSystem irodsFileSystem


	protected void setUp() {
		super.setUp()
		testingPropertiesHelper = new TestingPropertiesHelper()
		testingProperties = testingPropertiesHelper.getTestProperties()
		irodsAccount = testingPropertiesHelper.buildIRODSAccountFromTestProperties(testingProperties)
		irodsFileSystem = IRODSFileSystem.instance()
		irodsAccessObjectFactory = irodsFileSystem.getIRODSAccessObjectFactory()
		controller.session["SPRING_SECURITY_CONTEXT"] = irodsAccount
	}

	protected void tearDown() {
		super.tearDown()
	}

	void testPrepareUploadDialog() {

		def testPath = "/a/path/to/coll"
		def irodsAccessObjectFactory = Mockito.mock(IRODSAccessObjectFactory.class)
		CollectionAndDataObjectListAndSearchAO collectionListAndSearchAO = Mockito.mock(CollectionAndDataObjectListAndSearchAO.class)
		Collection retObject = new Collection()
		retObject.setCollectionName(testPath)
		Mockito.when(collectionListAndSearchAO.getFullObjectForType(testPath)).thenReturn(retObject)
		Mockito.when(irodsAccessObjectFactory.getCollectionAndDataObjectListAndSearchAO(irodsAccount)).thenReturn(collectionListAndSearchAO)
		controller.irodsAccessObjectFactory = irodsAccessObjectFactory
		controller.irodsAccount = irodsAccount

		controller.params.irodsTargetCollection = testPath
		controller.prepareUploadDialog()
		def mav = controller.modelAndView
		def name = mav.viewName

		assertNotNull("null mav", mav)
		assertEquals("view name should be uploadDialog", "uploadDialog", name)
		def irodsTargetCollection = mav.model.irodsTargetCollection
		assertNotNull("null targetIrodsCollection", irodsTargetCollection)
		assertEquals("did not find expected path", testPath,irodsTargetCollection)
	}

	void testPrepareUploadDialogAsDataObjectTarget() {

		def testPath = "/a/path/to/coll/file.txt"
		def collectionParentPath = "/a/path/to/coll"
		def irodsAccessObjectFactory = Mockito.mock(IRODSAccessObjectFactory.class)
		CollectionAndDataObjectListAndSearchAO collectionListAndSearchAO = Mockito.mock(CollectionAndDataObjectListAndSearchAO.class)
		DataObject retObject = new DataObject()
		retObject.setCollectionName(collectionParentPath)
		retObject.setDataName(testPath)
		Mockito.when(collectionListAndSearchAO.getFullObjectForType(testPath)).thenReturn(retObject)
		Mockito.when(irodsAccessObjectFactory.getCollectionAndDataObjectListAndSearchAO(irodsAccount)).thenReturn(collectionListAndSearchAO)
		controller.irodsAccessObjectFactory = irodsAccessObjectFactory
		controller.irodsAccount = irodsAccount

		controller.params.irodsTargetCollection = testPath
		controller.prepareUploadDialog()
		def mav = controller.modelAndView
		def name = mav.viewName

		assertNotNull("null mav", mav)
		assertEquals("view name should be uploadDialog", "uploadDialog", name)
		def irodsTargetCollection = mav.model.irodsTargetCollection
		assertNotNull("null targetIrodsCollection", irodsTargetCollection)
		assertEquals("did not find expected path, shold be parent path", collectionParentPath ,irodsTargetCollection)
	}

	void testMoveFile() {
		def sourcePath = "source"
		def targetPath = "target"
		def irodsAccessObjectFactory = Mockito.mock(IRODSAccessObjectFactory.class)
		DataTransferOperations dataTransferOperations = Mockito.mock(DataTransferOperations.class)
		Mockito.when(irodsAccessObjectFactory.getDataTransferOperations(irodsAccount)).thenReturn(dataTransferOperations)
		controller.irodsAccessObjectFactory = irodsAccessObjectFactory
		controller.irodsAccount = irodsAccount

		controller.params.sourceAbsPath = sourcePath
		controller.params.targetAbsPath = targetPath
		controller.moveFile()
		Mockito.verify(dataTransferOperations).move(sourcePath,targetPath)
	}

	void testCopyFile() {
		def sourcePath = "source"
		def targetPath = "target"
		def irodsAccessObjectFactory = Mockito.mock(IRODSAccessObjectFactory.class)
		DataTransferOperations dataTransferOperations = Mockito.mock(DataTransferOperations.class)
		Mockito.when(irodsAccessObjectFactory.getDataTransferOperations(irodsAccount)).thenReturn(dataTransferOperations)
		controller.irodsAccessObjectFactory = irodsAccessObjectFactory
		controller.irodsAccount = irodsAccount

		controller.params.sourceAbsPath = sourcePath
		controller.params.targetAbsPath = targetPath
		controller.copyFile()
		Mockito.verify(dataTransferOperations).copy(sourcePath,"",targetPath,null,false,null)
	}
}
