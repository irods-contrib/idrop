package org.irods.mydrop.controller

import grails.test.ControllerUnitTestCase

import java.util.Properties

import org.irods.jargon.core.connection.IRODSAccount
import org.irods.jargon.core.exception.JargonException
import org.irods.jargon.core.pub.CollectionAndDataObjectListAndSearchAO
import org.irods.jargon.core.pub.IRODSAccessObjectFactory
import org.irods.jargon.core.pub.IRODSFileSystem
import org.irods.jargon.spring.security.IRODSAuthenticationToken
import org.irods.jargon.testutils.TestingPropertiesHelper
import org.irods.jargon.usertagging.domain.IRODSTagGrouping
import org.irods.jargon.core.query.CollectionAndDataObjectListingEntry
import org.mockito.Mockito
import org.springframework.security.core.context.SecurityContextHolder

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
		def irodsAuthentication = new IRODSAuthenticationToken(irodsAccount)
		SecurityContextHolder.getContext().authentication = irodsAuthentication
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
	
	
}
