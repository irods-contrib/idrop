package org.irods.jargon.idrop.web.controllers



import grails.test.mixin.TestFor
import groovy.mock.interceptor.StubFor

import org.irods.jargon.core.connection.IRODSAccount
import org.irods.jargon.core.connection.auth.AuthResponse
import org.irods.jargon.idrop.web.services.AuthenticationService
import org.mockito.*

import spock.lang.*

/**
 * Integration style tests for the login controller
 */
@TestFor(LoginController)

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

		lc.save()

		def result = controller.response.contentAsString
		assert result == '{"authMessage":"","authenticatedIRODSAccount":{"anonymousAccount":false,"authenticationScheme":{"enumType":"org.irods.jargon.core.connection.AuthScheme","name":"STANDARD"},"class":"org.irods.jargon.core.connection.IRODSAccount","defaultStorageResource":"resc","homeDirectory":"","host":"host","password":"password","port":1247,"proxyName":"test","proxyZone":"zone","userName":"test","zone":"zone"},"authenticatingIRODSAccount":{"anonymousAccount":false,"authenticationScheme":{"enumType":"org.irods.jargon.core.connection.AuthScheme","name":"STANDARD"},"class":"org.irods.jargon.core.connection.IRODSAccount","defaultStorageResource":"resc","homeDirectory":"","host":"host","password":"password","port":1247,"proxyName":"test","proxyZone":"zone","userName":"test","zone":"zone"},"challengeValue":"","class":"org.irods.jargon.core.connection.auth.AuthResponse","responseProperties":{},"startupResponse":null,"successful":false}'
	}
}
