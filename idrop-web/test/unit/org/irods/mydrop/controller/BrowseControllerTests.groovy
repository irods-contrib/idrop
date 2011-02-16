
package org.irods.mydrop.controller

import grails.test.*
import org.irods.jargon.core.exception.JargonException
import java.util.Properties
import org.irods.jargon.core.connection.IRODSAccount
import org.irods.jargon.core.pub.IRODSAccessObjectFactory
import org.irods.jargon.core.pub.IRODSFileSystem
import org.irods.jargon.core.pub.CollectionAndDataObjectListAndSearchAO
import org.irods.jargon.core.pub.io.IRODSFile
import org.irods.jargon.core.query.CollectionAndDataObjectListingEntry
import org.irods.jargon.testutils.TestingPropertiesHelper
import org.irods.jargon.testutils.filemanip.FileGenerator
import org.irods.jargon.testutils.TestingPropertiesHelper
import org.irods.jargon.spring.security.IRODSAuthenticationToken
import org.irods.jargon.core.pub.domain.DataObject
import org.mockito.Mockito
import org.springframework.security.core.context.SecurityContextHolder
import grails.converters.*



class BrowseControllerTests extends ControllerUnitTestCase {

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
		def irodsAuthentication = new IRODSAuthenticationToken(irodsAccount)
		SecurityContextHolder.getContext().authentication = irodsAuthentication
	}

	protected void tearDown() {
		super.tearDown()
	}

	void testBrowse() {
		controller.params.dir = "/"
		controller.irodsAccessObjectFactory = irodsAccessObjectFactory
		controller.irodsAccount = irodsAccount
		controller.loadTree()
		def controllerResponse = controller.response.contentAsString
		def jsonResult = JSON.parse(controllerResponse)
		assertNotNull("missing json result", jsonResult)
	}

	void testAjaxDirectoryListingUnderParent() {
		controller.params.dir = "/"
		controller.irodsAccessObjectFactory = irodsAccessObjectFactory
		controller.irodsAccount = irodsAccount
		controller.ajaxDirectoryListingUnderParent()
		def controllerResponse = controller.response.contentAsString
		def jsonResult = JSON.parse(controllerResponse)
		assertNotNull("missing json result", jsonResult)
	}

	void testFileInfoNoParam() {
		controller.irodsAccessObjectFactory = irodsAccessObjectFactory
		controller.irodsAccount = irodsAccount
		shouldFail(JargonException) { controller.fileInfo() }
	}
	
	void testFileInfoWithPath() {
		def testPath = "/testpath.txt"
		def irodsAccessObjectFactory = Mockito.mock(IRODSAccessObjectFactory.class)
		CollectionAndDataObjectListAndSearchAO collectionListAndSearchAO = Mockito.mock(CollectionAndDataObjectListAndSearchAO.class)
		DataObject retObject = new DataObject()
		retObject.setDataName(testPath)
		Mockito.when(collectionListAndSearchAO.getFullObjectForType(testPath)).thenReturn(retObject)
		Mockito.when(irodsAccessObjectFactory.getCollectionAndDataObjectListAndSearchAO(irodsAccount)).thenReturn(collectionListAndSearchAO)
		controller.irodsAccessObjectFactory = irodsAccessObjectFactory
		controller.irodsAccount = irodsAccount
		controller.params.absPath = testPath
		controller.fileInfo()
		// FIXME: add factory for free tagging service, inject into controller to allow mocking of free tagging service
		def mav = controller.modelAndView
		def name = mav.viewName
		
		assertNotNull("null mav", mav)
		assertEquals("view name should be dataObjectInfo", "dataObjectInfo", name)
		def dataObj = mav.model.dataObject
		assertNotNull("null data object", dataObj)
		assertEquals("did not find expected path", testPath, dataObj.dataName)
		
	}
	
}
