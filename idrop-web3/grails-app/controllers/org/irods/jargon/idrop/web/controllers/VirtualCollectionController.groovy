package org.irods.jargon.idrop.web.controllers

import grails.converters.JSON
import grails.rest.RestfulController

import org.irods.jargon.core.pub.IRODSAccessObjectFactory
import org.irods.jargon.idrop.web.services.VirtualCollectionService

/**
 * Handle iRODS virtual collections
 */
class VirtualCollectionController extends RestfulController {

	static responseFormats = ['json']
	IRODSAccessObjectFactory irodsAccessObjectFactory
	VirtualCollectionService virtualCollectionService

	/**
	 * Get user listing of virtual collections
	 */
	def index() {
		log.info("index()...get virtual collections for user")
		def irodsAccount = request.irodsAccount
		if (!irodsAccount) throw new IllegalStateException("no irodsAccount in request")
		log.info("getting virtual colls")
		def virColls = virtualCollectionService.virtualCollectionHomeListingForUser(irodsAccount)
		log.info("virColls:${virColls}")
		render virColls as JSON
	}
}
