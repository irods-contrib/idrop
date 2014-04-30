package org.irods.jargon.idrop.web.controllers



import grails.test.mixin.*

import org.irods.jargon.core.connection.IRODSAccount
import org.irods.jargon.core.query.PagingAwareCollectionListing
import org.irods.jargon.idrop.web.services.VirtualCollectionService
import org.junit.*

/**
 * See the API for {@link grails.test.mixin.web.ControllerUnitTestMixin} for usage instructions
 */
@TestFor(CollectionController)
class CollectionControllerSpec {

	void testCollectionListJustVcName() {
		given:

		def vcServiceMock = mockFor(VirtualCollectionService)

		PagingAwareCollectionListing listing = new PagingAwareCollectionListing()

		vcServiceMock.demand.virtualCollectionListing{vcName, listingType, offset, irodsAccount, session -> return listing}

		controller.virtualCollectionService = vcServiceMock.createMock()
		IRODSAccount testAccount = IRODSAccount.instance("host", 1247, "user", "password", "","zone", "")
		request.irodsAccount = testAccount
		params.virtualCollection = "root"


		when:
		controller.show()

		then:
		controller.response.status == 200
		log.info("responseText:${response.text}")
	}


	void testCollectionListVcNameAndSubPath() {
		given:

		def vcServiceMock = mockFor(VirtualCollectionService)

		PagingAwareCollectionListing listing = new PagingAwareCollectionListing()

		vcServiceMock.demand.virtualCollectionListing{vcName, listingType, offset, irodsAccount, session -> return listing}

		controller.virtualCollectionService = vcServiceMock.createMock()
		IRODSAccount testAccount = IRODSAccount.instance("host", 1247, "user", "password", "","zone", "")
		request.irodsAccount = testAccount
		params.virtualCollection = "root"
		params.path = "/a/path"

		when:
		controller.show()

		then:
		controller.response.status == 200
		log.info("responseText:${response.text}")
	}
}
