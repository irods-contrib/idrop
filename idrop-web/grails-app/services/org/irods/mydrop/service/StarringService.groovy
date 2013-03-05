package org.irods.mydrop.service

import org.irods.jargon.core.connection.IRODSAccount
import org.irods.jargon.core.exception.JargonException
import org.irods.jargon.core.pub.IRODSAccessObjectFactory
import org.irods.jargon.usertagging.domain.IRODSStarredFileOrCollection
import org.irods.jargon.usertagging.starring.IRODSStarringService
import org.irods.jargon.usertagging.starring.IRODSStarringServiceImpl

/**
 * Service to 'star' or favorite folders and files
 * @author Mike Conway - DICE (www.irods.org)
 *
 */
class StarringService {

	static transactional = false
	IRODSAccessObjectFactory irodsAccessObjectFactory


	IRODSStarredFileOrCollection findStarred(IRODSAccount irodsAccount, String irodsAbsolutePath) throws FileNotFoundException, JargonException  {

		if (irodsAccount == null) {
			throw new IllegalArgumentException("null irodsAccount");
		}

		IRODSStarringService irodsStarringService = new IRODSStarringServiceImpl(irodsAccessObjectFactory, irodsAccount)
		return irodsStarringService.findStarredForAbsolutePath(irodsAbsolutePath)
	}

	void star(IRODSAccount irodsAccount, String irodsAbsolutePath, String description) throws FileNotFoundException, JargonException {
		if (irodsAccount == null) {
			throw new IllegalArgumentException("null irodsAccount");
		}

		IRODSStarringService irodsStarringService = new IRODSStarringServiceImpl(irodsAccessObjectFactory, irodsAccount)
		irodsStarringService.starFileOrCollection(irodsAbsolutePath, description)
	}

	void unStar(IRODSAccount irodsAccount, String irodsAbsolutePath) throws FileNotFoundException, JargonException {
		if (irodsAccount == null) {
			throw new IllegalArgumentException("null irodsAccount");
		}

		IRODSStarringService irodsStarringService = new IRODSStarringServiceImpl(irodsAccessObjectFactory, irodsAccount)
		irodsStarringService.unstarFileOrCollection(irodsAbsolutePath)
	}

	List<IRODSStarredFileOrCollection> listStarredCollections(IRODSAccount irodsAccount, int pagingOffset) throws JargonException {
		if (irodsAccount == null) {
			throw new IllegalArgumentException("null irodsAccount");
		}

		IRODSStarringService irodsStarringService = new IRODSStarringServiceImpl(irodsAccessObjectFactory, irodsAccount)
		return irodsStarringService.listStarredCollections(pagingOffset)
	}

	List<IRODSStarredFileOrCollection> listStarredDataObjects(IRODSAccount irodsAccount, int pagingOffset) throws JargonException {
		if (irodsAccount == null) {
			throw new IllegalArgumentException("null irodsAccount");
		}

		IRODSStarringService irodsStarringService = new IRODSStarringServiceImpl(irodsAccessObjectFactory, irodsAccount)
		return irodsStarringService.listStarredDataObjects(pagingOffset)
	}
}
