package org.irods.jargon.idrop.web.controllers

import grails.test.mixin.*

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

	void "test authenticate with a valid credential"() {
		given:
		def authMock = mockFor(AuthenticationService)

		controller.authenticationService = authMock.createMock()

		when:
		controller.save()

		then:
		controller.response.text.contains "Found 2 results"
	}
}
