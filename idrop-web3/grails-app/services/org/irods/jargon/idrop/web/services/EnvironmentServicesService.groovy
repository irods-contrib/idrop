package org.irods.jargon.idrop.web.services

import org.irods.jargon.core.connection.IRODSAccount;
import org.irods.jargon.core.pub.IRODSAccessObjectFactory

/**
 * Service to derive environmental info about the logged in iRODS Grid
 * @author Mike
 *
 */
class EnvironmentServicesService {
	
	static transactional = false
	
	IRODSAccessObjectFactory irodsAccessObjectFactory

    def getIrodsServerProperties(IRODSAccount irodsAccount) {
		if (!irodsAccount) throw new IllegalArgumentException("no irodsAccount")
		
		log.info("getIrodsServerProperties")
		EnvironmentalInfoAO environmentalInfoAO
		
		

    }
}
