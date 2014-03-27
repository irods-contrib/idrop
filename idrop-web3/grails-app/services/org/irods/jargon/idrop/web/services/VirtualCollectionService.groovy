package org.irods.jargon.idrop.web.services

import org.irods.jargon.core.connection.IRODSAccount
import org.irods.jargon.core.pub.IRODSAccessObjectFactory
import org.irods.jargon.vircoll.VirtualCollectionContext
import org.irods.jargon.vircoll.VirtualCollectionContextImpl
import org.irods.jargon.vircoll.impl.VirtualCollectionFactory
import org.irods.jargon.vircoll.impl.VirtualCollectionFactoryImpl

class VirtualCollectionService {

	static transactional = false
	IRODSAccessObjectFactory irodsAccessObjectFactory


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
		VirtualCollectionContext context = new VirtualCollectionContextImpl(irodsAccessObjectFactory, irodsAccount)

		VirtualCollectionFactory virtualCollectionFactory = new VirtualCollectionFactoryImpl(context)
		return virtualCollectionFactory.listDefaultUserCollections()
	}
}
