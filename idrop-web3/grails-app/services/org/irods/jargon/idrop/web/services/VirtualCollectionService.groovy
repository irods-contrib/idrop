package org.irods.jargon.idrop.web.services

import org.irods.jargon.core.connection.IRODSAccount
import org.irods.jargon.vircoll.impl.VirtualCollectionFactory

class VirtualCollectionService {

	static transactional = false

	/**
	 * Required dependency on the factory that will be used to create necessary services
	 */
	VirtualCollectionFactory virtualCollectionFactory

	/**
	 * Get the default list of virtual collections associated with a user
	 * @param irodsAccount {@link IRODSAccount} for the target user
	 * @return <code>List</code> of {@link AbstractVirtualCollection} 
	 */
	def virtualCollectionHomeListingForUser(IRODSAccount irodsAccount) {

		log.info("virtualCollectionHomeListingForUser()")

		if (!irodsAccount) {
			throw new IllegalArgumentException("null irodsAccount")
		}

		log.info("irodsAccount: ${irodsAccount}")

		VirtualCollectionFactory virtualCollectionFactory = virtualCollectionFactory.instanceVirtualCollectionFactory(irodsAccount)
		return virtualCollectionFactory.listDefaultUserCollections()
	}
}
