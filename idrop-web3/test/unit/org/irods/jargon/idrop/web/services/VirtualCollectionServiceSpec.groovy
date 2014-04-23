package org.irods.jargon.idrop.web.services



import grails.test.mixin.*

import org.codehaus.groovy.grails.plugins.testing.GrailsMockHttpSession
import org.irods.jargon.core.pub.IRODSAccessObjectFactory
import org.irods.jargon.core.query.PagingAwareCollectionListing
import org.irods.jargon.idrop.web.services.VirtualCollectionService.ListingType
import org.irods.jargon.vircoll.AbstractVirtualCollection
import org.irods.jargon.vircoll.types.CollectionBasedVirtualCollection
import org.irods.jargon.vircoll.types.CollectionBasedVirtualCollectionExecutor
import org.junit.*

import spock.lang.Specification

/**
 * See the API for {@link grails.test.mixin.services.ServiceUnitTestMixin} for usage instructions
 */
@TestFor(VirtualCollectionService)
class VirtualCollectionServiceSpec  extends Specification  {

	void "test create listing from collection based vc"() {
		given:
		String uniqueName = "hithere"
		def irodsAccessObjectFactory = mockFor(IRODSAccessObjectFactory)
		irodsAccessObjectFactory.demand.getEnvironmentalInfoAO{ irodsAccount -> return envMock }
		def iafMock = irodsAccessObjectFactory.createMock()
		PagingAwareCollectionListing listing = new PagingAwareCollectionListing()

		def collectionBasedVirtualCollectionExecutor = mockFor(CollectionBasedVirtualCollectionExecutor)
		collectionBasedVirtualCollectionExecutor.demand.queryAll{return listing}
		def execMock = collectionBasedVirtualCollectionExecutor.createMock()

		def virtualCollectionExecutorFactoryCreatorService = mockFor(VirtualCollectionExecutorFactoryCreatorService)
		virtualCollectionExecutorFactoryCreatorService.demand.instanceVirtualCollectionExecutorFactory{irodsAccount -> return execMock}

		def virtualCollectionExecutorFactoryCreatorServiceMock = virtualCollectionExecutorFactoryCreatorService.createMock()

		def mockSession = new GrailsMockHttpSession()
		List<AbstractVirtualCollection> virColls = new ArrayList<AbstractVirtualCollection>()
		CollectionBasedVirtualCollection collBasedVirColl = new CollectionBasedVirtualCollection(uniqueName,"/a/path")
		virColls.add(collBasedVirColl)
		mockSession.virtualCollections = virColls

		VirtualCollectionService virtualCollectionService = new VirtualCollectionService()
		virtualCollectionService.irodsAccessObjectFactory = iafMock
		virtualCollectionService.virtualCollectionExecutorFactoryCreatorService = virtualCollectionExecutorFactoryCreatorServiceMock

		when:

		def actual = virtualCollectionService.virtualCollectionListing(uniqueName, ListingType.ALL, 0)

		then:

		actual != null
	}
}
