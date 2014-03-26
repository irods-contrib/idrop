package org.irods.jargon.idrop.web.controllers

import grails.test.mixin.Mock
import grails.test.mixin.TestMixin

import grails.test.mixin.TestFor
import grails.test.mixin.web.ControllerUnitTestMixin


import org.irods.jargon.core.connection.AuthScheme
import org.irods.jargon.core.connection.IRODSAccount
import org.irods.jargon.core.connection.IRODSServerProperties
import org.irods.jargon.core.connection.auth.AuthResponse
import org.irods.jargon.core.exception.AuthenticationException
import org.irods.jargon.idrop.web.filters.AuthenticationFilters
import org.irods.jargon.idrop.web.filters.ConnectionClosingFilterFilters
import org.irods.jargon.idrop.web.services.AuthenticationService

import spock.lang.Specification

/**
 * See the API for {@link grails.test.mixin.web.ControllerUnitTestMixin} for usage instructions
 */
@TestFor(LoginController)
@Mock([AuthenticationFilters, ConnectionClosingFilterFilters])

class LoginControllerSpec extends Specification  {

	
/*
	void "test authenticate with a invalid credential"() {
		given:
		def authMock = mockFor(AuthenticationService)
		authMock.demand.authenticate { irodsAccount ->
			throw new AuthenticationException("no way")
		}

		controller.authenticationService = authMock.createMock()
		mockCommandObject(LoginCommand)
		
		Map mp = [host: 'host', port: 'port', zone:'zone', userName:'userName', password:'password', defaultStorageResource:'defaultresc', authType:AuthScheme.STANDARD]
		
		def loginCommand = new LoginCommand(mp) 
		/*loginCommand.host = "host"
		loginCommand.port = 1247
		loginCommand.zone = "zone"
		loginCommand.userName = "userName"
		loginCommand.password = "password"
		loginCommand.defaultStorageResource = "defaultStorageResource"
		loginCommand.authType = AuthScheme.STANDARD
                                  
		when:
		controller.save(loginCommand)

		then:
		thrown(AuthenticationException)
	}
*/

	/*
	void "test authenticate with a null command"() {
		given:
		def authMock = mockFor(AuthenticationService)

		controller.authenticationService = authMock.createMock()

		LoginCommand loginCommand = null

		when:
		controller.save(loginCommand)

		then:
		thrown(IllegalArgumentException)
	}
*/
	void "test authenticate with a valid credential"() {
		given:
		IRODSAccount testAcct = IRODSAccount.instance("host", 1247, "xxx", "xxx", "xxx", "xxx", "xxx")
		def authMock = mockFor(AuthenticationService)
		AuthResponse authResponse = new AuthResponse()
		authResponse.authenticatedIRODSAccount = testAcct
		authResponse.authenticatingIRODSAccount = testAcct
//		authResponse.irodsServerProperties = irodsServerProps
		
		authMock.demand.authenticate { irodsAccount ->
			return authResponse
		}
  
		controller.authenticationService = authMock.createMock()		
		//Map mp = [host: 'host', port: 1247, zone:'zone', userName:'userName', password:'password', defaultStorageResource:'defaultresc', authType:AuthScheme.STANDARD]
		//controller.metaClass.getParams { -> mp}
		
		params.host = 'host'
		params.port = 1247
		params.zone = 'zone'
		params.userName = 'user'
		params.password = 'password'
		params.defaultStorageResource = 'defresc'
		params.authType = AuthScheme.STANDARD
		
		when: "parameters are sent for login with valid"
	
		controller.save()

		then:
		controller.response.status == 200
		controller.session.authenticationSession != null
		log.info("response:${response.text}")
		assert '{"defaultStorageResource":null,"serverVersion":null,"userName":"xxx","zone":"xxx"}' == response.text
	}
/*
	void "test authenticate with a missing user gives validation error"() {
		given:
		def authMock = mockFor(AuthenticationService)
		authMock.demand.authenticate { irodsAccount ->
			return new AuthResponse()
		}

		controller.authenticationService = authMock.createMock()

		LoginCommand loginCommand = new LoginCommand()
		loginCommand.host = "host"
		loginCommand.port = 1247
		loginCommand.zone = "zone"
		loginCommand.userName = ""
		loginCommand.password = "password"
		loginCommand.defaultStorageResource = "defaultStorageResource"
		loginCommand.authType = AuthScheme.STANDARD

		assert !loginCommand.validate()
	}*/
}
