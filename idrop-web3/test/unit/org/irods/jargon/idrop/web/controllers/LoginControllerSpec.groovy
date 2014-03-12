package org.irods.jargon.idrop.web.controllers

import grails.test.mixin.*

import org.irods.jargon.core.connection.AuthScheme
import org.irods.jargon.core.connection.auth.AuthResponse
import org.irods.jargon.core.exception.AuthenticationException
import org.irods.jargon.idrop.web.filters.AuthenticationFilters
import org.irods.jargon.idrop.web.filters.ConnectionClosingFilterFilters
import org.irods.jargon.idrop.web.services.AuthenticationService
import org.junit.*

import spock.lang.Specification

/**
 * See the API for {@link grails.test.mixin.web.ControllerUnitTestMixin} for usage instructions
 */
@TestFor(LoginController)
@Mock([AuthenticationFilters, ConnectionClosingFilterFilters])

class LoginControllerSpec extends Specification  {

	void "test authenticate with a invalid credential"() {
		given:
		def authMock = mockFor(AuthenticationService)
		authMock.demand.authenticate { irodsAccount ->
			throw new AuthenticationException("no way")
		}

		controller.authenticationService = authMock.createMock()

		LoginCommand loginCommand = new LoginCommand()
		loginCommand.host = "host"
		loginCommand.port = 1247
		loginCommand.zone = "zone"
		loginCommand.userName = "userName"
		loginCommand.password = "password"
		loginCommand.defaultStorageResource = "defaultStorageResource"
		loginCommand.authType = AuthScheme.STANDARD


		when:
		controller.save(loginCommand)

		then:
		controller.response.status == 500
	}

	void "test authenticate with a valid credential"() {
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
		loginCommand.userName = "userName"
		loginCommand.password = "password"
		loginCommand.defaultStorageResource = "defaultStorageResource"
		loginCommand.authType = AuthScheme.STANDARD


		when:
		controller.save(loginCommand)

		then:
		controller.response.status == 200
	}
}
