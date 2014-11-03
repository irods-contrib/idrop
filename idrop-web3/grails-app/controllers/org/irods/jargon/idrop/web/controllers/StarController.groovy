package org.irods.jargon.idrop.web.controllers

import grails.rest.RestfulController

import org.irods.jargon.idrop.web.services.StarService

/**
 * Implements REST actions for 
 * @author mikeconway
 *
 */
class StarController extends RestfulController {

	StarService starService
	static responseFormats = ['json']

	/**
	 * PUT method to star a file or collection, this is idempotent, and returns a 204 (succss no data)
	 * @return
	 */
	def update() {
		log.info("update()...respond to put that adds a star")
		def path = params.path
		if (!path) {
			throw new IllegalArgumentException("null path")
		}

		log.info("path:${path}")

		def irodsAccount = request.irodsAccount

		if (!irodsAccount) {
			throw new IllegalArgumentException("null irodsAccount")
		}

		log.info("irodsAccount:${irodsAccount}")

		starService.addStar(path, "starred from iDrop web")
		log.info("star added successfull")
		render(status:204)
	}
}
