package org.irods.jargon.idrop.web.controllers


import static org.springframework.http.HttpMethod.*
import static org.springframework.http.HttpStatus.*
import grails.rest.RestfulController
import grails.transaction.*

import org.irods.jargon.core.connection.IRODSAccount
import org.irods.jargon.core.pub.IRODSAccessObjectFactory
import org.irods.jargon.idrop.web.services.AuthenticationService

class LoginController extends RestfulController {

	static responseFormats = ['json']



	IRODSAccessObjectFactory irodsAccessObjectFactory
	IRODSAccount irodsAccount
	AuthenticationService authenticationService


	def afterInterceptor = {
		log.debug("closing the session")
		irodsAccessObjectFactory.closeSession()
	}

	def save() {

		log.info("login()");

		def host = params.host
		def port = params.port
		def zone = params.zone
		def user = params.username
		def password = params.password
		def authType = params.authtype
	}
}
