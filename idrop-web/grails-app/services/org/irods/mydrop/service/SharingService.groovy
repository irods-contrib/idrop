package org.irods.mydrop.service

import org.irods.jargon.core.connection.IRODSAccount
import org.irods.jargon.core.exception.JargonException
import org.irods.jargon.core.pub.IRODSAccessObjectFactory
import org.irods.jargon.usertagging.domain.IRODSSharedFileOrCollection
import org.irods.jargon.usertagging.sharing.IRODSSharingService
import org.irods.jargon.usertagging.sharing.IRODSSharingServiceImpl

/**
 * Service to manage shares, which are treated like first class objects, managing marking as a share and updating ACLs via the jargon-user-tagging
 * share services.
 * 
 * @author Mike Conway - DICE (www.irods.org).
 *
 */
class SharingService {
	
	static transactional = false
	IRODSAccessObjectFactory irodsAccessObjectFactory

	/**
	 * List all shares owned by the given user (Shared by me with others)
	 * @param irodsAccount
	 * @return
	 * @throws JargonException
	 */
	List<IRODSSharedFileOrCollection> listCollectionsSharedByMe(IRODSAccount irodsAccount) throws JargonException {
		if (irodsAccount == null) {
			throw new IllegalArgumentException("null irodsAccount");
		}

		IRODSSharingService irodsSharingService = new IRODSSharingServiceImpl(irodsAccessObjectFactory, irodsAccount)
		return irodsSharingService.listSharedCollectionsOwnedByAUser(irodsAccount.getUserName(), irodsAccount.getZone())
	}
	
	/**
	 * List all shares with the given user (shared by others with me)
	 * @param irodsAccount
	 * @return
	 * @throws JargonException
	 */
	List<IRODSSharedFileOrCollection> listCollectionsSharedWithMe(IRODSAccount irodsAccount) throws JargonException {
		if (irodsAccount == null) {
			throw new IllegalArgumentException("null irodsAccount");
		}

		IRODSSharingService irodsSharingService = new IRODSSharingServiceImpl(irodsAccessObjectFactory, irodsAccount)
		return irodsSharingService.listSharedCollectionsSharedWithUser(irodsAccount.getUserName(), irodsAccount.getZone())
	}


    
}
