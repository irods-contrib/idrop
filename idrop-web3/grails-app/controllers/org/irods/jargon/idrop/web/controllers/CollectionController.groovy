package org.irods.jargon.idrop.web.controllers

import grails.converters.JSON

import org.irods.jargon.core.exception.JargonException
import org.irods.jargon.core.pub.IRODSAccessObjectFactory
import org.irods.jargon.idrop.web.services.VirtualCollectionService
import org.irods.jargon.idrop.web.services.VirtualCollectionService.ListingType

/**
 * Controller for dealing with collection listings of various sorts
 * @author Mike Conway - DICE
 */

class CollectionController  {

	static responseFormats = ['json']
	IRODSAccessObjectFactory irodsAccessObjectFactory
	VirtualCollectionService virtualCollectionService

	/**
	 * Obtain a listing of collection contents
	 * 
	 * note that the path is overloaded to have a virtual collection name, then a collection path (url encoded) and then parameters to tune the listing/paging behavior
	 * 
	 * @return
	 */
	def show() {
		log.info("show")
		def irodsAccount = request.irodsAccount
		if (!irodsAccount) throw new IllegalStateException("no irodsAccount in request")
		log.info("getting virtual coll contents listing")
		def virtualCollection = params.virtualCollection
		if (!virtualCollection) throw new JargonException("no virtualCollection name provided")

		def path = params.path
		if (!path) path = ""

		def offset = params.offset
		if (!offset) offset = 0

		log.info("virtualCollection: ${virtualCollection}")
		log.info("path:${path}")
		log.info("offset:offset")

		def pagingAwareCollectionListing = virtualCollectionService.virtualCollectionListing(virtualCollection, ListingType.ALL, offset, irodsAccount, session)
		log.info("pagingAwareCollectionListing:${pagingAwareCollectionListing}")
		render pagingAwareCollectionListing as JSON
	}


	def index() {
		log.info("index()")
	}
}
