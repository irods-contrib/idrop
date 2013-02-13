package org.irods.mydrop.controller

import grails.test.ControllerUnitTestCase

import org.irods.jargon.core.connection.IRODSAccount
import org.irods.jargon.core.exception.JargonException
import org.irods.jargon.core.pub.CollectionAndDataObjectListAndSearchAO
import org.irods.jargon.core.pub.IRODSAccessObjectFactory
import org.irods.jargon.core.pub.IRODSFileSystem
import org.irods.jargon.core.query.CollectionAndDataObjectListingEntry
import org.irods.jargon.testutils.TestingPropertiesHelper
import org.irods.jargon.usertagging.domain.TagQuerySearchResult
import org.irods.jargon.usertagging.tags.FreeTaggingService
import org.irods.jargon.usertagging.tags.TaggingServiceFactory
import org.mockito.Mockito

class SearchControllerTests extends ControllerUnitTestCase {

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

	void testSearchFile() {
		def searchTerm = "searchTerm"
		def searchType = "file"
		def irodsAccessObjectFactory = Mockito.mock(IRODSAccessObjectFactory.class)
		CollectionAndDataObjectListAndSearchAO collectionListAndSearchAO = Mockito.mock(CollectionAndDataObjectListAndSearchAO.class)
		Mockito.when(irodsAccessObjectFactory.getCollectionAndDataObjectListAndSearchAO(irodsAccount)).thenReturn(collectionListAndSearchAO)

		def entries = new ArrayList<CollectionAndDataObjectListingEntry>()
		Mockito.when(collectionListAndSearchAO.searchCollectionsAndDataObjectsBasedOnName(searchTerm)).thenReturn(entries)

		controller.irodsAccount = irodsAccount
		controller.irodsAccessObjectFactory = irodsAccessObjectFactory
		controller.params.searchTerm = searchTerm
		controller.params.searchType = searchType
		controller.search()
		def mav = controller.modelAndView
		def name = mav.viewName

		assertNotNull("null mav", mav)
		assertEquals("view name should be searchResult", "searchResult", name)
		def resultObj = mav.model.results
		assertNotNull("null results", resultObj)
	}


	void testSearchFileNullSearchTerm() {
		def searchType = "file"
		def searchTerm = "searchTerm"
		def irodsAccessObjectFactory = Mockito.mock(IRODSAccessObjectFactory.class)
		CollectionAndDataObjectListAndSearchAO collectionListAndSearchAO = Mockito.mock(CollectionAndDataObjectListAndSearchAO.class)
		Mockito.when(irodsAccessObjectFactory.getCollectionAndDataObjectListAndSearchAO(irodsAccount)).thenReturn(collectionListAndSearchAO)

		def entries = new ArrayList<CollectionAndDataObjectListingEntry>()
		Mockito.when(collectionListAndSearchAO.searchCollectionsAndDataObjectsBasedOnName(searchTerm)).thenReturn(entries)

		controller.irodsAccount = irodsAccount
		controller.irodsAccessObjectFactory = irodsAccessObjectFactory
		controller.params.searchType = searchType
		shouldFail(JargonException) { controller.search() }
	}

	void testSearchFileBlankSearchTerm() {
		def searchTerm = ""
		def searchType = "file"
		def irodsAccessObjectFactory = Mockito.mock(IRODSAccessObjectFactory.class)
		CollectionAndDataObjectListAndSearchAO collectionListAndSearchAO = Mockito.mock(CollectionAndDataObjectListAndSearchAO.class)
		Mockito.when(irodsAccessObjectFactory.getCollectionAndDataObjectListAndSearchAO(irodsAccount)).thenReturn(collectionListAndSearchAO)

		def entries = new ArrayList<CollectionAndDataObjectListingEntry>()
		Mockito.when(collectionListAndSearchAO.searchCollectionsAndDataObjectsBasedOnName(searchTerm)).thenReturn(entries)

		controller.irodsAccount = irodsAccount
		controller.irodsAccessObjectFactory = irodsAccessObjectFactory
		controller.params.searchTerm = searchTerm
		controller.params.searchType = searchType
		shouldFail(JargonException) { controller.search() }
	}

	void testSearchTag() {
		def searchTerm = "searchTerm"
		def searchType = "tag"
		def irodsAccessObjectFactory = Mockito.mock(IRODSAccessObjectFactory.class)

		CollectionAndDataObjectListAndSearchAO collectionListAndSearchAO = Mockito.mock(CollectionAndDataObjectListAndSearchAO.class)
		Mockito.when(irodsAccessObjectFactory.getCollectionAndDataObjectListAndSearchAO(irodsAccount)).thenReturn(collectionListAndSearchAO)

		FreeTaggingService freeTaggingService = Mockito.mock(FreeTaggingService.class)
		def entries = new ArrayList<CollectionAndDataObjectListingEntry>()
		def tagQuerySearchResult = TagQuerySearchResult.instance("tags", entries)
		Mockito.when(freeTaggingService.searchUsingFreeTagString(searchTerm)).thenReturn(tagQuerySearchResult)

		TaggingServiceFactory taggingServiceFactory = Mockito.mock(TaggingServiceFactory.class)
		Mockito.when(taggingServiceFactory.instanceFreeTaggingService(irodsAccount)).thenReturn(freeTaggingService)


		controller.irodsAccount = irodsAccount
		controller.irodsAccessObjectFactory = irodsAccessObjectFactory
		controller.taggingServiceFactory = taggingServiceFactory
		controller.params.searchTerm = searchTerm
		controller.params.searchType = searchType
		controller.search()
		def mav = controller.modelAndView
		def name = mav.viewName

		assertNotNull("null mav", mav)
		assertEquals("view name should be searchResult", "searchResult", name)
		def resultObj = mav.model.results
		assertNotNull("null results", resultObj)
	}
}
