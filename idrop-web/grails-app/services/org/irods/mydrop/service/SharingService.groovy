package org.irods.mydrop.service

import grails.util.Holders

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
	 * Check to see if sharing is enabled, either by the idrop config setting (idrop.config.use.sharing), or because
	 * a previous request indicated that sharing was not supported for this grid
	 * 
	 * @param irodsAccount
	 * @return <code>boolean</code> that will be <code>false</code> if I should not bother with the sharing feature
	 * @throws JargonException
	 */
	boolean isSharingSupported(IRODSAccount irodsAccount) throws JargonException {

		boolean sharing =  Holders.config.idrop.config.use.sharing
		log.info("sharing supported in config:${sharing}")

		if (sharing) {
			log.info("supported in config, see if specific query is set up for sharing...")
			def prop = irodsAccessObjectFactory.discoveredServerPropertiesCache.retrieveValue(irodsAccount.getHost(), irodsAccount.getZone(), IRODSSharingService.SHARING_DISABLED_PROPERTY)
			if (prop) {
				log.info("sharing is disabled, no specific query support is enabled")
				sharing = false;
			}
		}

		return sharing
	}

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

	/**
	 * Find a share if it exists, otherwise null will be returned
	 * @param irodsAbsolutePath
	 * @return
	 * @throws JargonException
	 */
	IRODSSharedFileOrCollection findShareForPath(String irodsAbsolutePath, IRODSAccount irodsAccount) throws JargonException {

		if (irodsAbsolutePath == null || irodsAbsolutePath.isEmpty()) {
			throw new IllegalArgumentException("null or empty irodsAbsolutePath")
		}

		if (irodsAccount == null) {
			throw new IllegalArgumentException("null irodsAccount");
		}

		IRODSSharingService irodsSharingService = new IRODSSharingServiceImpl(irodsAccessObjectFactory, irodsAccount)
		return irodsSharingService.findShareByAbsolutePath(irodsAbsolutePath)
	}

	/**
	 * Create a share with a short-hand method that does not list users, these can be set by setting ACLs as normal
	 * @param irodsAbsolutePath
	 * @param shareName
	 * @param irodsAccount
	 * @throws JargonException
	 */
	IRODSSharedFileOrCollection createShare(String irodsAbsolutePath, String shareName, IRODSAccount irodsAccount) throws JargonException {

		if (irodsAbsolutePath == null || irodsAbsolutePath.isEmpty()) {
			throw new IllegalArgumentException("null or empty irodsAbsolutePath")
		}

		if (shareName == null || shareName.isEmpty()) {
			throw new IllegalArgumentException("null or empty shareName")
		}

		if (irodsAccount == null) {
			throw new IllegalArgumentException("null irodsAccount");
		}

		IRODSSharingService irodsSharingService = new IRODSSharingServiceImpl(irodsAccessObjectFactory, irodsAccount)
		irodsSharingService.createShare(irodsAbsolutePath, shareName);
		return irodsSharingService.findShareByAbsolutePath(irodsAbsolutePath)
	}

	/**
	 * Update the given share to the new share name
	 * @param irodsAbsolutePath
	 * @param newShareName
	 * @param irodsAccount
	 * @return
	 * @throws JargonException
	 */
	IRODSSharedFileOrCollection updateShare(String irodsAbsolutePath, String newShareName, IRODSAccount irodsAccount) throws JargonException {
		if (irodsAbsolutePath == null || irodsAbsolutePath.isEmpty()) {
			throw new IllegalArgumentException("null or empty irodsAbsolutePath")
		}

		if (newShareName == null || newShareName.isEmpty()) {
			throw new IllegalArgumentException("null or empty newShareName")
		}

		if (irodsAccount == null) {
			throw new IllegalArgumentException("null irodsAccount");
		}

		IRODSSharingService irodsSharingService = new IRODSSharingServiceImpl(irodsAccessObjectFactory, irodsAccount)
		irodsSharingService.updateShareName(irodsAbsolutePath, newShareName)
		return irodsSharingService.findShareByAbsolutePath(irodsAbsolutePath)
	}

	/**
	 * Delete the share at the given path
	 * @param irodsAbsolutePath
	 * @param irodsAccount
	 * @return
	 * @throws JargonException
	 */
	void deleteShare(String irodsAbsolutePath, IRODSAccount irodsAccount) throws JargonException {

		log.info("deleteShare()")
		if (irodsAbsolutePath == null || irodsAbsolutePath.isEmpty()) {
			throw new IllegalArgumentException("null or empty irodsAbsolutePath")
		}


		if (irodsAccount == null) {
			throw new IllegalArgumentException("null irodsAccount");
		}


		log.info("share to delete:${irodsAbsolutePath}")
		IRODSSharingService irodsSharingService = new IRODSSharingServiceImpl(irodsAccessObjectFactory, irodsAccount)
		irodsSharingService.removeShare(irodsAbsolutePath)
		log.info("share removed")
	}
}
