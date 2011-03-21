
package org.irods.mydrop.controller

import grails.converters.*
import grails.test.*

import java.util.Properties

import java.util.List;

import org.irods.jargon.core.query.MetaDataAndDomainData;

import java.util.Properties;

import org.irods.jargon.core.connection.IRODSAccount;
import org.irods.jargon.core.pub.IRODSAccessObjectFactory;
import org.irods.jargon.core.pub.IRODSFileSystem;
import org.irods.jargon.testutils.TestingPropertiesHelper;


import org.irods.jargon.core.connection.IRODSAccount
import org.irods.jargon.core.exception.JargonException
import org.irods.jargon.core.pub.CollectionAndDataObjectListAndSearchAO
import org.irods.jargon.core.pub.IRODSAccessObjectFactory
import org.irods.jargon.core.pub.IRODSFileSystem
import org.irods.jargon.core.pub.domain.Collection
import org.irods.jargon.core.pub.CollectionAO
import org.irods.jargon.core.pub.domain.DataObject
import org.irods.jargon.core.query.CollectionAndDataObjectListingEntry
import org.irods.jargon.core.query.MetaDataAndDomainData
import org.irods.jargon.spring.security.IRODSAuthenticationToken
import org.irods.jargon.testutils.TestingPropertiesHelper
import org.irods.jargon.usertagging.FreeTaggingService
import org.irods.jargon.usertagging.TaggingServiceFactory
import org.irods.jargon.usertagging.domain.IRODSTagGrouping
import org.mockito.Mockito
import org.springframework.security.core.context.SecurityContextHolder


class MetadataControllerTests extends ControllerUnitTestCase {

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
	
	void testMetadataWhenCollection() {
		def testPath = "/testpath"
		def irodsAccessObjectFactory = Mockito.mock(IRODSAccessObjectFactory.class)
		CollectionAndDataObjectListAndSearchAO collectionListAndSearchAO = Mockito.mock(CollectionAndDataObjectListAndSearchAO.class)
		Collection retObject = new Collection()
		retObject.setCollectionName(testPath)
		Mockito.when(collectionListAndSearchAO.getFullObjectForType(testPath)).thenReturn(retObject)
		Mockito.when(irodsAccessObjectFactory.getCollectionAndDataObjectListAndSearchAO(irodsAccount)).thenReturn(collectionListAndSearchAO)
		
		CollectionAO collectionAO = Mockito.mock(CollectionAO.class)
		List<MetaDataAndDomainData> mockMetadata = new ArrayList<MetaDataAndDomainData>();
		Mockito.when(collectionAO.findMetadataValuesForCollection(testPath, 0)).thenReturn(mockMetadata);
		Mockito.when(irodsAccessObjectFactory.getCollectionAO(irodsAccount)).thenReturn(collectionAO)
		
		controller.irodsAccessObjectFactory = irodsAccessObjectFactory		
		controller.irodsAccount = irodsAccount
		controller.params.absPath = testPath
		controller.listMetadata()
		def mav = controller.modelAndView
		def name = mav.viewName

		assertNotNull("null mav", mav)
		assertEquals("view name should be metadataDetails", "metadataDetails", name)
		def metadata = mav.model.metadata
		assertNotNull("null metadata object", metadata)
		
	}
}
