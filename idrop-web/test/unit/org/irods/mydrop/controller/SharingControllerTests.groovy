package org.irods.mydrop.controller

import grails.converters.*
import grails.test.ControllerUnitTestCase

import java.util.Properties

import org.irods.jargon.core.connection.IRODSAccount
import org.irods.jargon.core.exception.JargonException
import org.irods.jargon.core.pub.CollectionAO
import org.irods.jargon.core.pub.CollectionAndDataObjectListAndSearchAO
import org.irods.jargon.core.pub.DataObjectAO
import org.irods.jargon.core.pub.IRODSAccessObjectFactory
import org.irods.jargon.core.pub.IRODSFileSystem
import org.irods.jargon.core.pub.UserAO
import org.irods.jargon.core.pub.domain.Collection
import org.irods.jargon.core.pub.domain.DataObject
import org.irods.jargon.core.pub.domain.UserFilePermission
import org.irods.jargon.spring.security.IRODSAuthenticationToken
import org.irods.jargon.testutils.TestingPropertiesHelper
import org.mockito.Mockito
import org.springframework.security.core.context.SecurityContextHolder


class SharingControllerTests extends ControllerUnitTestCase {
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

	void testListAclCollection() {
		def testPath = "/testpath"
		def irodsAccessObjectFactory = Mockito.mock(IRODSAccessObjectFactory.class)
		CollectionAndDataObjectListAndSearchAO collectionListAndSearchAO = Mockito.mock(CollectionAndDataObjectListAndSearchAO.class)
		Collection retObject = new Collection()
		retObject.setCollectionName(testPath)
		Mockito.when(collectionListAndSearchAO.getFullObjectForType(testPath)).thenReturn(retObject)
		Mockito.when(irodsAccessObjectFactory.getCollectionAndDataObjectListAndSearchAO(irodsAccount)).thenReturn(collectionListAndSearchAO)

		CollectionAO collectionAO = Mockito.mock(CollectionAO.class)
		List<UserFilePermission> mockMetadata = new ArrayList<UserFilePermission>();
		Mockito.when(collectionAO.listPermissionsForCollection(testPath)).thenReturn(mockMetadata);
		Mockito.when(irodsAccessObjectFactory.getCollectionAO(irodsAccount)).thenReturn(collectionAO)

		controller.irodsAccessObjectFactory = irodsAccessObjectFactory
		controller.irodsAccount = irodsAccount
		controller.params.absPath = testPath
		controller.listAcl()
		def mav = controller.modelAndView
		def name = mav.viewName

		assertNotNull("null mav", mav)
		assertEquals("view name should be aclDetails", "aclDetails", name)
		def metadata = mav.model.acls
		assertNotNull("null acls object", metadata)
	}


	void testListAclDataObject() {
		def testPath = "/testpath"
		def testFileName = "filename.txt"
		def irodsAccessObjectFactory = Mockito.mock(IRODSAccessObjectFactory.class)
		CollectionAndDataObjectListAndSearchAO collectionListAndSearchAO = Mockito.mock(CollectionAndDataObjectListAndSearchAO.class)
		DataObject retObject = new DataObject()
		retObject.setCollectionName(testPath)
		retObject.setDataName(testFileName)
		Mockito.when(collectionListAndSearchAO.getFullObjectForType(testPath)).thenReturn(retObject)
		Mockito.when(irodsAccessObjectFactory.getCollectionAndDataObjectListAndSearchAO(irodsAccount)).thenReturn(collectionListAndSearchAO)

		DataObjectAO dataObjectAO = Mockito.mock(DataObjectAO.class)
		List<UserFilePermission> mockMetadata = new ArrayList<UserFilePermission>();
		Mockito.when(dataObjectAO.listPermissionsForDataObject(testPath + "/" + testFileName)).thenReturn(mockMetadata);
		Mockito.when(irodsAccessObjectFactory.getDataObjectAO(irodsAccount)).thenReturn(dataObjectAO)

		controller.irodsAccessObjectFactory = irodsAccessObjectFactory
		controller.irodsAccount = irodsAccount
		controller.params.absPath = testPath
		controller.listAcl()
		def mav = controller.modelAndView
		def name = mav.viewName

		assertNotNull("null mav", mav)
		assertEquals("view name should be aclDetails", "aclDetails", name)
		def metadata = mav.model.acls
		assertNotNull("null acls object", metadata)
	}

	void testPrepareAclDialogWhenCreate() {
		def testPath = "/testpath"
		def testFileName = "filename.txt"
		def irodsAccessObjectFactory = Mockito.mock(IRODSAccessObjectFactory.class)

		controller.irodsAccessObjectFactory = irodsAccessObjectFactory
		controller.irodsAccount = irodsAccount
		controller.params.absPath = testPath
		controller.prepareAclDialog()
		def mav = controller.modelAndView
		def name = mav.viewName

		assertNotNull("null mav", mav)
		assertEquals("view name incorrect", "aclDialog", name)
		def absPath = mav.model.absPath
		assertNotNull("null absPath object", absPath)
		assertEquals("wrong abspath", testPath, absPath)
		assertNotNull("no acl options enum", mav.model.userPermissionEnum)
		assertNull("should not be a user name", mav.model.userName)
	}

	void testPrepareAclDialogWhenEdit() {
		def testPath = "/testpath"
		def testFileName = "filename.txt"
		def irodsAccessObjectFactory = Mockito.mock(IRODSAccessObjectFactory.class)

		controller.irodsAccessObjectFactory = irodsAccessObjectFactory
		controller.irodsAccount = irodsAccount
		controller.params.absPath = testPath
		controller.params.userName = "userName"
		controller.prepareAclDialog()
		def mav = controller.modelAndView
		def name = mav.viewName

		assertNotNull("null mav", mav)
		assertEquals("view name incorrect", "aclDialog", name)
		def absPath = mav.model.absPath
		assertNotNull("null absPath object", absPath)
		assertEquals("wrong abspath", testPath, absPath)
		assertNotNull("no acl options enum", mav.model.userPermissionEnum)
		assertNotNull("should be a user name", mav.model.userName)
	}

	void testPrepareAclDialogWhenNullAbsPath() {
		def testPath = null
		def testFileName = "filename.txt"
		def irodsAccessObjectFactory = Mockito.mock(IRODSAccessObjectFactory.class)

		controller.irodsAccessObjectFactory = irodsAccessObjectFactory
		controller.irodsAccount = irodsAccount
		controller.params.absPath = testPath
		controller.params.userName = "userName"

		shouldFail(JargonException) { controller.prepareAclDialog() }
	}
	
	void listUsersForAutocomplete() {
		def testUser = "t"
		def irodsAccessObjectFactory = Mockito.mock(IRODSAccessObjectFactory.class)
		UserAO userAO = Mockito.mock(UserAO.class)
		List<String> retUsers = new ArrayList<String>();
		retUsers.add("test1");
		retUsers.add('test2');
		Mockito.when(userAO.findUserNameLike(testUser)).thenReturn(retUsers);
		Mockito.when(irodsAccessObjectFactory.getUserAO(irodsAccount)).thenReturn(userAO)

		controller.irodsAccessObjectFactory = irodsAccessObjectFactory
		controller.irodsAccount = irodsAccount
		controller.params.term = testUser
		controller.listUsersForAutocomplete()

		def jsonResult = JSON.parse(controllerResponse)
		assertNotNull("missing json result", jsonResult)
	}
}
