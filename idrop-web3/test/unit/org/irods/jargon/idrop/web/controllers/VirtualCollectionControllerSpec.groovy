package org.irods.jargon.idrop.web.controllers



import grails.test.mixin.*

import org.irods.jargon.core.connection.IRODSAccount
import org.irods.jargon.idrop.web.services.VirtualCollectionService
import org.irods.jargon.vircoll.AbstractVirtualCollection
import org.irods.jargon.vircoll.impl.CollectionBasedVirtualCollection
import org.junit.*

/**
 * See the API for {@link grails.test.mixin.web.ControllerUnitTestMixin} for usage instructions
 */
@TestFor(VirtualCollectionController)
//@Mock([AuthenticationFilters, ConnectionClosingFilterFilters])
class VirtualCollectionControllerSpec {

	void testListVirtualCollections() {
		given:

		//IRODSAccount irodsAccount = IRODSAccount.instance("host", port, "user", "password", "zone", "", AuthScheme.STANDARD)

		/*
		 def accessObjFactory = mockFor(IRODSAccessObjectFactory)
		 def virtualCollectionContext = new VirtualCollectionContextImpl(accessObjFactory.createMock());
		 def virtualCollectionFactory = new VirtualCollectionFactoryImpl()
		 */

		// def virtualCollectionFactory = mockFor(VirtualCollectionFactory)
		// virtualCollectionFactory.demand

		def vcServiceMock = mockFor(VirtualCollectionService)

		CollectionBasedVirtualCollection rootColl = new CollectionBasedVirtualCollection("/")
		CollectionBasedVirtualCollection homeColl = new CollectionBasedVirtualCollection("/test/home/userhome")
		def virtualCollections = new ArrayList<AbstractVirtualCollection>()
		virtualCollections.add(rootColl)
		virtualCollections.add(homeColl)

		vcServiceMock.demand.virtualCollectionHomeListingForUser { irodsAccount -> return virtualCollections }

		controller.virtualCollectionService = vcServiceMock.createMock()
		IRODSAccount testAccount = IRODSAccount.instance("host", 1247, "user", "password", "","zone", "")
		request.irodsAccount = testAccount


		when:
		controller.index()

		then:
		controller.response.status == 200
		log.info("responseText:${response.text}")



	}
}
