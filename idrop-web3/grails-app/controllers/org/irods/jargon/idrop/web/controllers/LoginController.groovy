package org.irods.jargon.idrop.web.controllers


import static org.springframework.http.HttpMethod.*
import static org.springframework.http.HttpStatus.*
import grails.converters.JSON
import grails.rest.RestfulController
import grails.transaction.*

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

		if (command.hasErrors()) {
			command.password = ""
			log.warn("validation errors:${command}")
			response.status = 400
			render(command as JSON)
			return
		}

		log.info("no validation errors, authenticate")
	}
}

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
		username(blank: false)
		password(blank: false)
	}
}
