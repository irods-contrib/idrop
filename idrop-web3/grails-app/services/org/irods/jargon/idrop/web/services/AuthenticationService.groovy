package org.irods.jargon.idrop.web.services

import org.irods.jargon.core.connection.IRODSAccount
import org.irods.jargon.core.connection.auth.AuthResponse
import org.irods.jargon.core.pub.IRODSAccessObjectFactory

class AuthenticationService {

	static transactional = false

	IRODSAccessObjectFactory irodsAccessObjectFactory

	def authenticate(IRODSAccount irodsAccount) {

		log.info("authenticate()")
		if (!irodsAccount) {
			throw new IllegalArgumentException("null irodsAccount")
		}

		log.info("IRODSAccount ${irodsAccount}")
		AuthResponse authResponse = new AuthResponse()
		
	
		return authResponse;
	}
}
