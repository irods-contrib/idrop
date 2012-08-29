package org.irods.mydrop.service

import org.irods.jargon.core.connection.IRODSAccount
import org.irods.jargon.core.pub.IRODSAccessObjectFactory

class ProfileService {
	static transactional = false
	IRODSAccessObjectFactory irodsAccessObjectFactory

	def retrieveProfile(IRODSAccount irodsAccount) {
		log.info "retrieveProfile()"
		if (irodsAccount == null) {
			throw new IllegalArgumentException("null profile")
		}
	}
}
