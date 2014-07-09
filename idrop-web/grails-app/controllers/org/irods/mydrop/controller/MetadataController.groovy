package org.irods.mydrop.controller

import grails.converters.JSON

import org.irods.jargon.core.connection.IRODSAccount
import org.irods.jargon.core.exception.CatNoAccessException
import org.irods.jargon.core.exception.DataNotFoundException
import org.irods.jargon.core.exception.DuplicateDataException
import org.irods.jargon.core.exception.JargonException
import org.irods.jargon.core.pub.CollectionAO
import org.irods.jargon.core.pub.CollectionAndDataObjectListAndSearchAO
import org.irods.jargon.core.pub.DataObjectAO
import org.irods.jargon.core.pub.IRODSAccessObjectFactory
import org.irods.jargon.core.pub.domain.AvuData
import org.irods.jargon.core.pub.domain.DataObject
import org.irods.jargon.core.utils.LocalFileUtils

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
	def beforeInterceptor = [action:this.&auth]

	def auth() {
		if(!session["SPRING_SECURITY_CONTEXT"]) {
			redirect(controller:"login", action:"login")
			return false
		}
		irodsAccount = session["SPRING_SECURITY_CONTEXT"]
	}


	def afterInterceptor = {
		log.debug("closing the session")
		irodsAccessObjectFactory.closeSession()
	}

	/**
	 * Load the metadata details area, this will show the main form, and subsequently, the table will be loaded via AJAX
	 */
	def showMetadataDetails = {

		def absPath = params['absPath']
		if (absPath == null) {
			log.error "no absPath in request for showAclDetails()"
			def message = message(code:"error.no.path.provided")
			response.sendError(500,message)
		}

		log.info("showMetadataDetails for absPath: ${absPath}")
		try {
			CollectionAndDataObjectListAndSearchAO collectionAndDataObjectListAndSearchAO = irodsAccessObjectFactory.getCollectionAndDataObjectListAndSearchAO(irodsAccount)
			def retObj = collectionAndDataObjectListAndSearchAO.getFullObjectForType(absPath)
			def isDataObject = retObj instanceof DataObject
			boolean getThumbnail = false

			if (isDataObject) {
				String extension = LocalFileUtils.getFileExtension(retObj.dataName).toUpperCase()
				log.info("extension is:${extension}")

				if (extension == ".JPG" || extension == ".GIF" || extension == ".PNG" || extension == ".TIFF" ||   extension == ".TIF") {
					getThumbnail = true
				}
			}

			render(view:"metadataDetails", model:[retObj:retObj, isDataObject:isDataObject,  absPath:absPath, getThumbnail:getThumbnail])
		} catch (org.irods.jargon.core.exception.FileNotFoundException fnf) {
			log.info("file not found looking for data, show stand-in page", fnf)
			render(view:"/browse/noInfo")
		} catch (CatNoAccessException e) {
			log.error("no access error", e)
			response.sendError(500, message(code:"message.no.access"))
		}
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
		def metadata

		/*
		 * Get the data object or collection at the given path, and access the relevant metadata 
		 */
		try {
			def retObj = collectionAndDataObjectListAndSearchAO.getFullObjectForType(absPath)
			def isDataObject = retObj instanceof DataObject

			if (isDataObject) {
				log.debug("retrieving meta data for a data object")
				DataObjectAO dataObjectAO = irodsAccessObjectFactory.getDataObjectAO(irodsAccount)
				metadata = dataObjectAO.findMetadataValuesForDataObject(retObj.collectionName, retObj.dataName)
			} else {
				CollectionAO collectionAO = irodsAccessObjectFactory.getCollectionAO(irodsAccount)
				metadata = collectionAO.findMetadataValuesForCollection(retObj.collectionName, 0)
			}

			log.info("metadata:${metadata}")
		} catch (DataNotFoundException dnf) {
			log.warn "cannot find data for path"
			flash.message="error.no.data.found"
		} catch (CatNoAccessException e) {
			log.error("no access error", e)
			response.sendError(500, message(code:"message.no.access"))
		} catch (Exception e) {
			log.warn "cannot find data for path"
			flash.message="error.no.data.found"
		}

		render(view:"metadataTable", model:[metadata:metadata])
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
			render jsonData as JSON
			return
		} else {

			log.info(" attribute: ${cmd.attribute} value: ${cmd.value} unit: ${cmd.unit}")

			responseData['attribute'] = cmd.attribute
			responseData['value'] = cmd.value
			responseData['unit']=cmd.unit
			responseData['absPath']=cmd.absPath
			responseData['message']= message(code:"message.update.successful")

			CollectionAndDataObjectListAndSearchAO collectionAndDataObjectListAndSearchAO = irodsAccessObjectFactory.getCollectionAndDataObjectListAndSearchAO(irodsAccount)

			def retObj = collectionAndDataObjectListAndSearchAO.getFullObjectForType(cmd.absPath)

			if (cmd.unit == null || cmd.unit.isEmpty()) {
				cmd.unit = ""
			}

			def avuData = AvuData.instance(cmd.attribute, cmd.value, cmd.unit)

			def isDataObject = retObj instanceof DataObject

			try {
				if (isDataObject) {
					log.debug("setting AVU for a data object")
					DataObjectAO dataObjectAO = irodsAccessObjectFactory.getDataObjectAO(irodsAccount)
					dataObjectAO.addAVUMetadata(cmd.absPath, avuData)
				} else {
					log.debug("setting AVU for collection")
					CollectionAO collectionAO = irodsAccessObjectFactory.getCollectionAO(irodsAccount)
					collectionAO.addAVUMetadata(cmd.absPath, avuData)
				}
			} catch (DuplicateDataException dde) {
				log.warn("duplicate data exception", dde)
				def errorMessage = message(code:"error.duplicate.metadata")
				response.sendError(500,errorMessage)
				return
			} catch (CatNoAccessException e) {
				log.error("no access error", e)
				response.sendError(500, message(code:"message.no.access"))
			}


			log.info("avu set successfully")
			jsonData['response'] =responseData
		}

		render jsonData as JSON
	}


	/**
	 * Update metadata, which requires both the current and the desired AVU triple
	 */
	def updateMetadata = { UpdateMetadataCommand cmd ->

		log.info "updateMetadata"
		log.info "params: ${params}"

		log.info "cmd:${cmd}"

		def responseData = [:]
		def jsonData = [:]

		if (cmd.hasErrors()) {

			log.info "errors occured build error messages"
			def errorMessage = message(code:"error.data.error")
			response.sendError(500,errorMessage)
			return
		} else {

			CollectionAndDataObjectListAndSearchAO collectionAndDataObjectListAndSearchAO = irodsAccessObjectFactory.getCollectionAndDataObjectListAndSearchAO(irodsAccount)

			def retObj = collectionAndDataObjectListAndSearchAO.getFullObjectForType(cmd.absPath)

			if (cmd.currentUnit == null || cmd.currentUnit.isEmpty()) {
				cmd.currentUnit = ""
			}


			def currentAvuData = AvuData.instance(cmd.currentAttribute, cmd.currentValue, cmd.currentUnit)

			if (cmd.newUnit == null || cmd.newUnit.isEmpty()) {
				cmd.newUnit = ""
			}

			def newAvuData = AvuData.instance(cmd.newAttribute, cmd.newValue, cmd.newUnit)

			def isDataObject = retObj instanceof DataObject

			try {
				if (isDataObject) {
					log.info("setting AVU for a data object:${newAvuData}")
					DataObjectAO dataObjectAO = irodsAccessObjectFactory.getDataObjectAO(irodsAccount)
					dataObjectAO.modifyAVUMetadata(cmd.absPath, currentAvuData, newAvuData)
				} else {
					log.info("setting AVU for collection:${newAvuData}")
					CollectionAO collectionAO = irodsAccessObjectFactory.getCollectionAO(irodsAccount)
					collectionAO.modifyAVUMetadata(cmd.absPath, currentAvuData, newAvuData)
				}
			} catch (CatNoAccessException e) {
				log.error("no access error", e)
				response.sendError(500, message(code:"message.no.access"))
			} catch (Exception e) {
				log.error("exception updating metadata:${e}")
				response.sendError(500,e.message)
				return
			}

			log.info("avu set successfully")
			render "OK"
		}
	}

	/**
	 * Delete one or more metadata values for a collection or data object
	 */
	def deleteMetadata = {
		log.info("deleteMetadata()")
		log.info(params)
		def absPath = params['absPath']
		CollectionAndDataObjectListAndSearchAO collectionAndDataObjectListAndSearchAO = irodsAccessObjectFactory.getCollectionAndDataObjectListAndSearchAO(irodsAccount)
		def retObj = collectionAndDataObjectListAndSearchAO.getFullObjectForType(absPath)
		def isDataObject = retObj instanceof DataObject
		log.info("deleting metadata for a data object")

		def DataObjectAO dataObjectAO
		def CollectionAO collectionAO

		if (isDataObject) {
			dataObjectAO = irodsAccessObjectFactory.getDataObjectAO(irodsAccount)
		} else {
			collectionAO = irodsAccessObjectFactory.getCollectionAO(irodsAccount)
		}

		if (!absPath) {
			log.error "no path provided"
			def errorMessage = message(code:"error.no.path.provided")
			response.sendError(500,errorMessage)
			return
		}

		def attributesToDelete = params['attribute']
		def valuesToDelete = params['value']
		def unitsToDelete = params["unit"]

		// if nothing selected, just jump out and return a message
		if (!attributesToDelete) {
			log.info("no avu to delete")
			def errorMessage = message(code:"error.nothing.selected")
			response.sendError(500,errorMessage)
			return
		}

		log.info("avusToDelete: ${attributesToDelete}")

		AvuData avuValue

		try {
			if (attributesToDelete instanceof Object[] || attributesToDelete instanceof List) {
				log.debug "is array"
				int i = 0
				attributesToDelete.each{
					log.info "avusToDelete: ${it} has index ${i}"

					def thisAttr = ((List) attributesToDelete).get(i)
					def thisVal = ((List) valuesToDelete).get(i)
					def thisUnit = ((List) unitsToDelete).get(i)

					avuValue = new AvuData(thisAttr,thisVal,thisUnit)
					log.info("avuValue: ${avuValue}")

					if (isDataObject) {
						log.info "delete as data object"
						deleteAvuForDataObject(absPath, avuValue, dataObjectAO)
					} else {
						deleteAvuForCollection(absPath, avuValue, collectionAO)
					}

					i++
				}

			} else {
				log.debug "not array"
				log.info "deleting: ${attributesToDelete}"
				avuValue = new AvuData(attributesToDelete, valuesToDelete, unitsToDelete)
				if (isDataObject) {
					log.info "delete as data object"
					deleteAvuForDataObject(absPath, avuValue, dataObjectAO)
				} else {
					deleteAvuForCollection(absPath, avuValue, collectionAO)
				}
			}
		} catch (CatNoAccessException e) {
			log.error("no access error", e)
			response.sendError(500, message(code:"message.no.access"))
			return
		}


		render "OK"

	}

	private void deleteAvuForDataObject(String absPath, AvuData avuData, DataObjectAO dataObjectAO) throws JargonException {

		if (!absPath) {
			throw new IllegalArgumentException("null absPath")
		}

		if (!avuData) {
			throw new IllegalArgumentException("null avuData")
		}

		if (!dataObjectAO) {
			throw new IllegalArgumentException("null dataObjectAO")
		}

		dataObjectAO.deleteAVUMetadata( absPath, avuData)

	}

	private void deleteAvuForCollection(String absPath,  AvuData avuData, CollectionAO collectionAO) throws JargonException {

		if (!absPath) {
			throw new IllegalArgumentException("null absPath")
		}

		if (!avuData) {
			throw new IllegalArgumentException("null avuData")
		}

		if (!collectionAO) {
			throw new IllegalArgumentException("null collectionAO")
		}

		collectionAO.deleteAVUMetadata( absPath, avuData)

	}

}

/**
 * Command for adding metadata from the metadataDialog.gsp form
 */
public class AddMetadataCommand {
	String absPath
	String attribute
	String value
	String unit
	static constraints = {
		attribute(blank:false)
		value(blank:false)
		unit(nullable:true)
		absPath(blank:false)
	}
}

/**
 * Command for updating metadata in place from the metadata details table
 */
public class UpdateMetadataCommand {
	String absPath
	String currentAttribute
	String currentValue
	String currentUnit
	String newAttribute
	String newValue
	String newUnit
	static constraints = {
		currentAttribute(blank:false)
		currentValue( nullable:false)
		currentUnit(nullable:true)
		newAttribute(blank:false)
		newValue(blank:false)
		newUnit(nullable:true)
		absPath(blank:false)
	}
}
