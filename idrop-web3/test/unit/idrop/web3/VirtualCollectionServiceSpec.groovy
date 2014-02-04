package idrop.web3



import grails.test.mixin.*

import org.irods.jargon.core.connection.IRODSAccount
import org.irods.jargon.vircoll.AbstractVirtualCollection
import org.irods.jargon.vircoll.VirtualCollectionServicesCreatingFactory
import org.irods.jargon.vircoll.impl.VirtualCollectionFactory
import org.junit.*

/**
 * See the API for {@link grails.test.mixin.services.ServiceUnitTestMixin} for usage instructions
 */
@TestFor(VirtualCollectionService)
class VirtualCollectionServiceSpec {

	@Before
	void setup() {
	}

	void testListUserVirtualCollections () {

		List<AbstractVirtualCollection> virtualCollections = new ArrayList<AbstractVirtualCollection>()

		def virtualCollectionFactory = mockFor(VirtualCollectionFactory)
		virtualCollectionFactory.demand.listDefaultUserCollections {irodsAccount -> return virtualCollections}
		virtualCollectionFactory = virtualCollectionFactory.createMock()

		def vcf = mockFor(VirtualCollectionServicesCreatingFactory)

		vcf.demand.instanceVirtualCollectionFactory {irodsAccount -> return virtualCollectionFactory}
		vcf = vcf.createMock()

		VirtualCollectionService vcs = new VirtualCollectionService()
		vcs.virtualCollectionServicesCreatingFactory = vcf
		IRODSAccount irodsAccount = IRODSAccount.instance("host", 1247,
				"user", "xxx", "", "zone", "")
		def actual = vcs.virtualCollectionHomeListingForUser(irodsAccount)
		assertNotNull(actual)
	}
}
