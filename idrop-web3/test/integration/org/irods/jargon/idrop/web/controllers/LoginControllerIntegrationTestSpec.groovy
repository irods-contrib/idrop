package org.irods.jargon.idrop.web.controllers



import groovy.mock.interceptor.StubFor
import org.irods.jargon.core.connection.IRODSAccount
import org.irods.jargon.core.connection.auth.AuthResponse
import spock.lang.*
import org.irods.jargon.idrop.web.services.AuthenticationService
import org.mockito.*

/**
 * Integration style tests for the login controller
 */
class LoginControllerIntegrationTestSpec extends GroovyTestCase  {

    def setup() {
    }

    def cleanup() {
    }

     void testAuthentication() {
        LoginController lc = new LoginController()
		IRODSAccount irodsAccount = IRODSAccount.instance("host", 1247, "test", "password", "", "zone", "resc")
		AuthResponse authResponse = new AuthResponse();
		authResponse.authenticatedIRODSAccount = irodsAccount
		authResponse.authenticatingIRODSAccount = irodsAccount
		
		def authenticationServiceStub = new StubFor(AuthenticationService)
		authenticationServiceStub.demand.authenticate() {acct -> return authResponse }
		def authenticationService = authenticationServiceStub.proxyInstance()
		lc.setAuthenticationService(authenticationService)
		
		lc.response.format = 'json'
		
		lc.params.host = "host"
		lc.params.port = 1247
		lc.params.userName = "test"
		lc.params.password = "password"
		lc.params.zone = "zone"
		lc.params.defaultStorageResource = "resc"
		lc.params.authType = "STANDARD"
		
		def result = lc.save()		
		assert result

    }
}
