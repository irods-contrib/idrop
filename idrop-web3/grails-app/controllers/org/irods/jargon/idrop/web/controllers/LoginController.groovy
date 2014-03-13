package org.irods.jargon.idrop.web.controllers


import static org.springframework.http.HttpMethod.*
import static org.springframework.http.HttpStatus.*
import grails.converters.JSON
import grails.rest.RestfulController
import grails.transaction.*

import org.irods.jargon.core.connection.IRODSAccount
import org.irods.jargon.core.connection.auth.AuthResponse
import org.irods.jargon.core.pub.IRODSAccessObjectFactory
import org.irods.jargon.idrop.web.services.AuthenticationService

class LoginController extends RestfulController {

	static responseFormats = ['json']

	IRODSAccessObjectFactory irodsAccessObjectFactory
	AuthenticationService authenticationService

	/**
	 * Processing of POST is a login action
	 * @param command
	 * @return
	 */
	def save(LoginCommand command) {

		log.info("login()");

		if (!command) {
			throw new IllegalArgumentException("null command")
		}

		if (command.hasErrors()) {
			command.password = ""
			log.warn("validation errors:${command}")
			response.status = 400
			render(command as JSON)
			return
		}

		log.info("no validation errors, authenticate")

		IRODSAccount irodsAccount = IRODSAccount.instance(command.host, command.port, command.userName, command.password, "", command.zone, command.defaultStorageResource)

		AuthResponse authResponse = authenticationService.authenticate(irodsAccount)

		log.info("auth successful, saving response in session and returning")
		session.authenticationSession = authResponse

		render authResponse as JSON
	}
}
@grails.validation.Validateable
class LoginCommand {
	String userName
	String password
	int port
	String zone
	String host
	String defaultStorageResource
	String authType
	boolean guestLogin
	boolean usePreset

	static constraints = {
		userName(blank: false)
		password(blank: false)
	}
}
