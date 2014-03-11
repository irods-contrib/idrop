package org.irods.jargon.idrop.web.controllers

import grails.converters.JSON

import com.sun.jndi.cosnaming.ExceptionMapper

/**
 * General error controller returns a REST depiction of the error
 * @author mikeconway
 *
 */
class ErrorController {

	def index() {

		def exception = request.exception.cause
		def message = ExceptionMapper.mapException(exception)
		def status = message.status

		log.error("error controller triggered for exception:${exception}")

		response.status = 500
		render([error: 'an error occurred'] as JSON)
	}
}

