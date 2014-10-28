package org.irods.jargon.idrop.web.controllers

import grails.converters.JSON
import grails.rest.RestfulController

import org.irods.jargon.idrop.web.services.DataProfileMidTierService
import org.irods.jargon.idrop.web.services.FileService

/**
 * Controller for an individual file, differentiated from the 'CollectionController' that handles listings within collections, and deals with the metadata about a collection or 
 * data object rather than the listing of children
 * @author Mike Conway - DICE 
 *
 */
class FileController extends RestfulController {

	static responseFormats = ['json']
	FileService fileService
	DataProfileMidTierService dataProfileMidTierService

	/**
	 * Get the iRODS catalog info for the given path
	 */
	def index() {

		log.info("index")
		def irodsAccount = request.irodsAccount
		if (!irodsAccount) throw new IllegalStateException("no irodsAccount in request")
		def path = params.path
		log.info("path:${path}")
		def dataProfile = dataProfileMidTierService.retrieveDataProfile(path, irodsAccount)
		render dataProfile as JSON
	}
}
