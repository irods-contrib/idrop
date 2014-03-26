package org.irods.jargon.idrop.web.controllers

import grails.rest.RestfulController
import org.irods.jargon.idrop.web.services.VirtualCollectionService
import org.irods.jargon.core.pub.*

/**
* Handle iRODS virtual collections
*/
class VirtualCollectionController extends RestfulController {

	static responseFormats = ['json'] 

	IRODSAccessObjectFactory irodsAccessObjectFactory 
	VirtualCollectionService virtualCollectionService

    def index() { }
}
