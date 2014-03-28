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
class VirtualCollectionControllerSpec {

	void testListVirtualCollections() {
		given:

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
