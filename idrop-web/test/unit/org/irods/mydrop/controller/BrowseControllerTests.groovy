
package org.irods.mydrop.controller

import grails.converters.*
import grails.test.*

import org.irods.jargon.core.connection.IRODSAccount
import org.irods.jargon.core.connection.IRODSServerProperties
import org.irods.jargon.core.exception.JargonException
import org.irods.jargon.core.pub.CollectionAndDataObjectListAndSearchAO
import org.irods.jargon.core.pub.EnvironmentalInfoAO
import org.irods.jargon.core.pub.IRODSAccessObjectFactory
import org.irods.jargon.core.pub.IRODSFileSystem
import org.irods.jargon.core.pub.domain.Collection
import org.irods.jargon.core.pub.domain.DataObject
import org.irods.jargon.core.query.CollectionAndDataObjectListingEntry
import org.irods.jargon.core.query.MetaDataAndDomainData
import org.irods.jargon.testutils.TestingPropertiesHelper
import org.irods.jargon.usertagging.domain.IRODSTagGrouping
import org.irods.jargon.usertagging.domain.IRODSTagValue
import org.irods.jargon.usertagging.tags.FreeTaggingService
import org.irods.jargon.usertagging.tags.IRODSTaggingService
import org.irods.jargon.usertagging.tags.TaggingServiceFactory
import org.mockito.Matchers
import org.mockito.Mockito

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
		//def irodsAuthentication = new IRODSAuthenticationToken(irodsAccount)
		//SecurityContextHolder.getContext().authentication = irodsAuthentication
		controller.session["SPRING_SECURITY_CONTEXT"] = irodsAccount
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

	void testEstablishParentDirWhenStrictACL() {
		def testPath = "/"
		def irodsAccessObjectFactory = Mockito.mock(IRODSAccessObjectFactory.class)
		def environmentalInfoAO = Mockito.mock(EnvironmentalInfoAO.class)
		Mockito.when(environmentalInfoAO.isStrictACLs()).thenReturn(true)
		Mockito.when(irodsAccessObjectFactory.getEnvironmentalInfoAO(irodsAccount)).thenReturn(environmentalInfoAO)
		controller.irodsAccessObjectFactory = irodsAccessObjectFactory
		controller.irodsAccount = irodsAccount
		controller.params.dir = testPath
		controller.establishParentDir()
		def controllerResponse = controller.response.contentAsString
		def jsonResult = JSON.parse(controllerResponse)
		assertNotNull("missing json result", jsonResult)

		assert jsonResult.parent == "/" + irodsAccount.zone + "/home/" + irodsAccount.userName + "/"
	}

	void testEstablishParentDirWhenNotStrictACL() {
		def testPath = "/"
		def irodsAccessObjectFactory = Mockito.mock(IRODSAccessObjectFactory.class)
		def environmentalInfoAO = Mockito.mock(EnvironmentalInfoAO.class)
		Mockito.when(environmentalInfoAO.isStrictACLs()).thenReturn(false)
		Mockito.when(irodsAccessObjectFactory.getEnvironmentalInfoAO(irodsAccount)).thenReturn(environmentalInfoAO)
		controller.irodsAccessObjectFactory = irodsAccessObjectFactory
		controller.irodsAccount = irodsAccount
		controller.params.dir = testPath
		controller.establishParentDir()
		def controllerResponse = controller.response.contentAsString
		def jsonResult = JSON.parse(controllerResponse)
		assertNotNull("missing json result", jsonResult)

		assert jsonResult.parent == "/"
	}

	void testFileInfoWithPath() {
		def testPath = "/testpath.txt"
		def irodsAccessObjectFactory = Mockito.mock(IRODSAccessObjectFactory.class)
		CollectionAndDataObjectListAndSearchAO collectionListAndSearchAO = Mockito.mock(CollectionAndDataObjectListAndSearchAO.class)
		IRODSServerProperties irodsServerProperties = IRODSServerProperties.instance(IRODSServerProperties.IcatEnabled.ICAT_ENABLED, 0, "rods3.0", "rods3.0", "zone")
		Mockito.when(collectionListAndSearchAO.getIRODSServerProperties()).thenReturn(irodsServerProperties)

		DataObject retObject = new DataObject()
		retObject.setDataName(testPath)
		Mockito.when(collectionListAndSearchAO.getFullObjectForType(testPath)).thenReturn(retObject)
		Mockito.when(irodsAccessObjectFactory.getCollectionAndDataObjectListAndSearchAO(irodsAccount)).thenReturn(collectionListAndSearchAO)
		controller.irodsAccessObjectFactory = irodsAccessObjectFactory
		FreeTaggingService freeTaggingService = Mockito.mock(FreeTaggingService.class)

		TaggingServiceFactory taggingServiceFactory = Mockito.mock(TaggingServiceFactory.class)
		IRODSTagGrouping grouping = new IRODSTagGrouping(MetaDataAndDomainData.MetadataDomain.DATA, "name", "tags", "user")
		Mockito.when(freeTaggingService.getTagsForDataObjectInFreeTagForm(testPath)).thenReturn(grouping)
		Mockito.when(taggingServiceFactory.instanceFreeTaggingService(irodsAccount)).thenReturn(freeTaggingService)

		IRODSTagValue tagValue = new IRODSTagValue("xxx", "yyy")
		IRODSTaggingService irodsTaggingService = Mockito.mock(IRODSTaggingService.class)
		Mockito.when(irodsTaggingService.getDescriptionOnDataObjectForLoggedInUser(Matchers.anyString())).thenReturn(tagValue)
		Mockito.when(taggingServiceFactory.instanceIrodsTaggingService(irodsAccount)).thenReturn(irodsTaggingService)

		controller.irodsAccount = irodsAccount
		controller.taggingServiceFactory = taggingServiceFactory
		controller.params.absPath = testPath
		controller.fileInfo()
		def mav = controller.modelAndView
		def name = mav.viewName

		assertNotNull("null mav", mav)
		assertEquals("view name should be dataObjectInfo", "dataObjectInfo", name)
		def dataObj = mav.model.dataObject
		assertNotNull("null data object", dataObj)
		assertEquals("did not find expected path", testPath, dataObj.dataName)
		def tags = mav.model.tags
		assertNotNull("null tag in model", tags)
	}

	void testFileInfoWithPathWhenCollection() {
		def testPath = "/testpath"
		def irodsAccessObjectFactory = Mockito.mock(IRODSAccessObjectFactory.class)
		CollectionAndDataObjectListAndSearchAO collectionListAndSearchAO = Mockito.mock(CollectionAndDataObjectListAndSearchAO.class)
		IRODSServerProperties irodsServerProperties = Mockito.mock(IRODSServerProperties.class)
		Mockito.when(irodsServerProperties.isTheIrodsServerAtLeastAtTheGivenReleaseVersion("rods3.0")).thenReturn(true)
		Mockito.when(collectionListAndSearchAO.getIRODSServerProperties()).thenReturn(irodsServerProperties)

		Collection retObject = new Collection()
		retObject.setCollectionName(testPath)
		Mockito.when(collectionListAndSearchAO.getFullObjectForType(testPath)).thenReturn(retObject)
		Mockito.when(irodsAccessObjectFactory.getCollectionAndDataObjectListAndSearchAO(irodsAccount)).thenReturn(collectionListAndSearchAO)
		controller.irodsAccessObjectFactory = irodsAccessObjectFactory
		FreeTaggingService freeTaggingService = Mockito.mock(FreeTaggingService.class)
		TaggingServiceFactory taggingServiceFactory = Mockito.mock(TaggingServiceFactory.class)
		IRODSTagGrouping grouping = new IRODSTagGrouping(MetaDataAndDomainData.MetadataDomain.COLLECTION, "name", "tags", "user")
		Mockito.when(freeTaggingService.getTagsForCollectionInFreeTagForm(testPath)).thenReturn(grouping)
		Mockito.when(taggingServiceFactory.instanceFreeTaggingService(irodsAccount)).thenReturn(freeTaggingService)

		IRODSTagValue tagValue = new IRODSTagValue("xxx", "yyy")
		IRODSTaggingService irodsTaggingService = Mockito.mock(IRODSTaggingService.class)
		Mockito.when(irodsTaggingService.getDescriptionOnCollectionForLoggedInUser(Matchers.anyString())).thenReturn(tagValue)
		Mockito.when(taggingServiceFactory.instanceIrodsTaggingService(irodsAccount)).thenReturn(irodsTaggingService)

		controller.irodsAccount = irodsAccount
		controller.taggingServiceFactory = taggingServiceFactory
		controller.params.absPath = testPath
		controller.fileInfo()
		def mav = controller.modelAndView
		def name = mav.viewName

		assertNotNull("null mav", mav)
		assertEquals("view name should be collectionInfo", "collectionInfo", name)
		def collection = mav.model.collection
		assertNotNull("null collection object", collection)
		assertEquals("did not find expected path", testPath, collection.collectionName)
		def tags = mav.model.tags
		assertNotNull("null tag in model", tags)
	}

	void testBrowseDetailsWithCollection() {
		def testPath = "/testpath"
		def irodsAccessObjectFactory = Mockito.mock(IRODSAccessObjectFactory.class)
		CollectionAndDataObjectListAndSearchAO collectionListAndSearchAO = Mockito.mock(CollectionAndDataObjectListAndSearchAO.class)
		IRODSServerProperties irodsServerProperties = Mockito.mock(IRODSServerProperties.class)
		Mockito.when(irodsServerProperties.isTheIrodsServerAtLeastAtTheGivenReleaseVersion("rods3.0")).thenReturn(true)
		Mockito.when(collectionListAndSearchAO.getIRODSServerProperties()).thenReturn(irodsServerProperties)

		def retObject = new ArrayList<CollectionAndDataObjectListingEntry>()
		Mockito.when(collectionListAndSearchAO.listDataObjectsAndCollectionsUnderPath(testPath)).thenReturn(retObject)
		Mockito.when(irodsAccessObjectFactory.getCollectionAndDataObjectListAndSearchAO(irodsAccount)).thenReturn(collectionListAndSearchAO)
		controller.irodsAccessObjectFactory = irodsAccessObjectFactory
		controller.irodsAccount = irodsAccount
		controller.params.absPath = testPath
		controller.displayBrowseGridDetails()
		def mav = controller.modelAndView
		def name = mav.viewName
		assertNotNull("null mav", mav)
		assertEquals("view name should be browseDetails", "browseDetails", name)
		def collection = mav.model.collection
		assertNotNull("null collection object", collection)
	}
}
