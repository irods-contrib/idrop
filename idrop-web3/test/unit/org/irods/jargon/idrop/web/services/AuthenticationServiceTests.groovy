package org.irods.jargon.idrop.web.services


import grails.test.mixin.*

import org.irods.jargon.core.connection.IRODSAccount
import org.irods.jargon.core.connection.auth.AuthResponse
import org.irods.jargon.core.pub.IRODSAccessObjectFactory
import org.junit.Before
import org.irods.jargon.idrop.web.services.AuthenticationService
import spock.lang.Specification


/**
 * See the API for {@link grails.test.mixin.services.ServiceUnitTestMixin} for
 * usage instructions
 */
@TestFor(AuthenticationService)
class AuthenticationServiceTests extends Specification {

	@Before
	void setup() {
	}

	void testAuthenticateValid() {

		AuthResponse authResponse = new AuthResponse()
		def irodsAccessObjectFactory = mockFor(IRODSAccessObjectFactory)
		irodsAccessObjectFactory.demand.authenticateIRODSAccount{irodsAccount -> return authResponse}
		irodsAccessObjectFactory = irodsAccessObjectFactory.createMock()
		AuthenticationService authenticationService = new AuthenticationService()
		authenticationService.irodsAccessObjectFactory = irodsAccessObjectFactory
		IRODSAccount irodsAccount = IRODSAccount.instance("host", 1247,
				"user", "xxx", "", "zone", "")
		AuthResponse actual = authenticationService.authenticate(irodsAccount)
		assertNotNull(actual)
		log.info("actual response:${actual}")
	}
}
