package org.irods.mydrop.controller


import grails.converters.JSON

import org.irods.jargon.core.connection.IRODSAccount
import org.irods.jargon.core.exception.DataNotFoundException
import org.irods.jargon.core.exception.JargonException
import org.irods.jargon.core.exception.JargonRuntimeException
import org.irods.jargon.core.pub.CollectionAO
import org.irods.jargon.core.pub.CollectionAndDataObjectListAndSearchAO
import org.irods.jargon.core.pub.DataObjectAO
import org.irods.jargon.core.pub.IRODSAccessObjectFactory
import org.irods.jargon.core.pub.domain.AvuData
import org.irods.jargon.core.pub.domain.DataObject
import org.springframework.security.core.context.SecurityContextHolder

/**
 * Controller for displaying and updating various metadata for data objects or collections
 * @author Mike Conway - DICE (www.irods.org) 
 */
class MetadataController {
 
	IRODSAccessObjectFactory irodsAccessObjectFactory
	IRODSAccount irodsAccount

	/**
	 * Interceptor grabs IRODSAccount from the SecurityContextHolder
	 */
	def beforeInterceptor = {
		def irodsAuthentication = SecurityContextHolder.getContext().authentication

		if (irodsAuthentication == null) {
			throw new JargonRuntimeException("no irodsAuthentication in security context!")
		}

		irodsAccount = irodsAuthentication.irodsAccount
		log.debug("retrieved account for request: ${irodsAccount}")
	}

	def afterInterceptor = {
		log.debug("closing the session")
		irodsAccessObjectFactory.closeSession()
	}

	/**
	 * For a given absolute path, which is a data object, list the AVU metadata values
	 */
	def listMetadata = {
		def absPath = params['absPath']
		if (absPath == null) {
			throw new JargonException("no absolute path passed to the method")
		}

		log.info("listMetadataForDataObject for absPath: ${absPath}")

		CollectionAndDataObjectListAndSearchAO collectionAndDataObjectListAndSearchAO = irodsAccessObjectFactory.getCollectionAndDataObjectListAndSearchAO(irodsAccount)
		def metadata;

		/*
		 * Get the data object or collection at the given path, and access the relevant metadata 
		 */
		try {
			def retObj = collectionAndDataObjectListAndSearchAO.getFullObjectForType(absPath)
			def isDataObject = retObj instanceof DataObject

			if (isDataObject) {
				log.debug("retrieving meta data for a data object");
				DataObjectAO dataObjectAO = irodsAccessObjectFactory.getDataObjectAO(irodsAccount)
				metadata = dataObjectAO.findMetadataValuesForDataObject(retObj.collectionName, retObj.dataName)
			} else {
				CollectionAO collectionAO = irodsAccessObjectFactory.getCollectionAO(irodsAccount)
				metadata = collectionAO.findMetadataValuesForCollection(retObj.collectionName, 0) // FIXME: switch to no restart sig
			}
		} catch (DataNotFoundException dnf) {
			log.warn "cannot find data for path"
			flash.message="error.data.not.found"
		}

		render(view:"metadataDetails", model:[metadata:metadata])
	}

	/**
	 * Display an metadata dialog for an add or edit
	 */
	def prepareMetadataDialog = {
		log.info "prepareMetadataDialog"
		log.info "params: ${params}"


		def absPath = params['absPath']
		def isCreate = params['create']

		if (!absPath) {
			log.error "no absPath in request for prepareMetadataDialog()"
			throw new JargonException("a path was not supplied")
		}


		render(view:"metadataDialog", model:[absPath:absPath])
	}

	/**
	 * Add metadata 
	 */
	def addMetadata = { AddMetadataCommand cmd ->

		log.info "addMetadata"
		log.info "params: ${params}"
		
		log.info "cmd:${cmd}"
		
		def responseData = [:]
		def jsonData = [:]
		
		if (cmd.hasErrors()) {
			log.info "errors occured build error messages"
			def errorMessage = message(code:"error.data.error")
			responseData['errorMessage'] = errorMessage
			def errors = []
			def i = 0
			cmd.errors.allErrors.each() {
				log.info "error identified in validation: ${it}"
				errors.add(message(error:it))
			}
			responseData['errors'] = errors
			jsonData['response'] =responseData
		} else {
		
		log.info(" attribute: ${cmd.attribute} value: ${cmd.value} unit: ${cmd.unit}")
		
		responseData['attribute'] = cmd.attribute
		responseData['value'] = cmd.value
		responseData['unit']=cmd.unit
		responseData['absPath']=cmd.absPath
		responseData['message']= message(code:"message.update.successful")
		
		CollectionAndDataObjectListAndSearchAO collectionAndDataObjectListAndSearchAO = irodsAccessObjectFactory.getCollectionAndDataObjectListAndSearchAO(irodsAccount)

		def retObj = collectionAndDataObjectListAndSearchAO.getFullObjectForType(cmd.absPath)

		def avuData = AvuData.instance(cmd.attribute, cmd.value, cmd.unit)

		def isDataObject = retObj instanceof DataObject

		if (isDataObject) {
			log.debug("setting AVU for a data object")
			DataObjectAO dataObjectAO = irodsAccessObjectFactory.getDataObjectAO(irodsAccount)
			dataObjectAO.addAVUMetadata(cmd.absPath, avuData)
		} else {
			log.debug("setting AVU for collection")
			CollectionAO collectionAO = irodsAccessObjectFactory.getCollectionAO(irodsAccount)
			collectionAO.addAVUMetadata(cmd.absPath, avuData)
		}

		log.info("avu set successfully")
		jsonData['response'] =responseData
		
		}

		render jsonData as JSON
		
	}
}

/**
 * Command for adding metadata from the metadataDialog.gsp form
 */
class AddMetadataCommand {
	String absPath
	String attribute
	String value
	String unit
	static constraints = {
		attribute(blank:false)
		value(blank:false)
		absPath(blank:false)
	}
}

