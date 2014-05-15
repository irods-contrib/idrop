package org.irods.jargon.idrop.web.services

import org.irods.jargon.core.connection.IRODSAccount
import org.irods.jargon.core.pub.CollectionAndDataObjectListAndSearchAO
import org.irods.jargon.core.pub.IRODSAccessObjectFactory

/**
 * Service for navigating and manipulating iRODS collections.
 * @author Mike Conway
 *
 */
class IrodsCollectionService {

	static transactional = false
	IRODSAccessObjectFactory irodsAccessObjectFactory
	VirtualCollectionFactoryCreatorService virtualCollectionFactoryCreatorService
	enum ListingType {
		ALL, COLLECTIONS, DATA_OBJECTS
	}

	/**
	 * Generate a collection listing 
	 * @param absoluteParentPath
	 * @param listingType
	 * @param offset
	 * @param irodsAccount
	 * @return
	 */
	def collectionListing(String absoluteParentPath, ListingType listingType, int offset, IRODSAccount irodsAccount) {

		log.info("collectionListing()")

		if (!absoluteParentPath) {
			throw new IllegalArgumentException("null or empty absoluteParentPath")
		}

		if (!listingType) {
			throw new IllegalArgumentException("null or empty listingType")
		}

		if (!irodsAccount) {
			throw new IllegalArgumentException("null irodsAccount")
		}
		log.info("getting listing for path:${absoluteParentPath}")
		log.info("listing type:${listingType}, offset:${offset}")

		CollectionAndDataObjectListAndSearchAO collectionAndDataObjectListAndSearchAO = irodsAccessObjectFactory.getCollectionAndDataObjectListAndSearchAO(irodsAccount)

		// right now just does full listing..need to add paging
		return collectionAndDataObjectListAndSearchAO.listDataObjectsAndCollectionsUnderPathProducingPagingAwareCollectionListing(absoluteParentPath)

	}
}
