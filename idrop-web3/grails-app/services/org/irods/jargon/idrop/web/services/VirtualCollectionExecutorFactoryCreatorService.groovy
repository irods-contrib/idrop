package org.irods.jargon.idrop.web.services

import org.irods.jargon.core.connection.IRODSAccount
import org.irods.jargon.core.pub.IRODSAccessObjectFactory
import org.irods.jargon.vircoll.impl.VirtualCollectionExecutorFactoryImpl

/**
 * This is a bit of slight-of-hand intended to make it easy to mock virtual collection services.  It abstracts out the creation of vc factories
 * as an injectable service.
 * @author Mike Conway - DICE
 *
 */
class VirtualCollectionExecutorFactoryCreatorService {

	static transactional = false
	IRODSAccessObjectFactory irodsAccessObjectFactory

	/**
	 * Get an instance of the virtual collection executor factory for the given account
	 * @param irodsAccount
	 * @return
	 */
	def instanceVirtualCollectionExecutorFactory(IRODSAccount irodsAccount) {
		return new VirtualCollectionExecutorFactoryImpl(irodsAccessObjectFactory, irodsAccount)
	}
}
