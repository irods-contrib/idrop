package org.irods.mydrop.controller

import org.irods.jargon.core.connection.IRODSAccount
import org.irods.jargon.core.exception.DataNotFoundException
import org.irods.jargon.core.pub.CollectionAndDataObjectListAndSearchAO
import org.irods.jargon.core.pub.DataObjectAuditAO
import org.irods.jargon.core.pub.IRODSAccessObjectFactory
import org.irods.jargon.core.pub.domain.DataObject
import org.irods.jargon.core.pub.io.IRODSFile

class AuditController {

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

	def auditTable = {
		def absPath = params['absPath']
		if (absPath == null) {
			log.error "no absPath in request for showAclDetails()"
			def message = message(code:"error.no.path.provided")
			response.sendError(500,message)
		}

		log.info("auditListDataObject for absPath: ${absPath}")

		CollectionAndDataObjectListAndSearchAO collectionAndDataObjectListAndSearchAO = irodsAccessObjectFactory.getCollectionAndDataObjectListAndSearchAO(irodsAccount)
		def retObj = collectionAndDataObjectListAndSearchAO.getFullObjectForType(absPath)
		def isDataObject = retObj instanceof DataObject

		def auditedActions
		if (isDataObject) {
			log.info("is a data object, get audit for it")
			DataObjectAuditAO dataObjectAuditAO = irodsAccessObjectFactory.getDataObjectAuditAO(irodsAccount)
			IRODSFile dataObjectFile = irodsAccessObjectFactory.getIRODSFileFactory(irodsAccount).instanceIRODSFile(absPath)
			auditedActions = dataObjectAuditAO.findAllAuditRecordsForDataObject(dataObjectFile, 0, 1000)
		} else {
			log.info("is a collection, get audit info for it")
		}

		render(view:"auditTable", model:[auditedActions:auditedActions])
	}

	def auditInfo = {
		log.info("auditInfo()")
		def absPath = params['absPath']
		if (absPath == null) {
			log.error "no absPath in request "
			def message = message(code:"error.no.path.provided")
			response.sendError(500,message)
		}

		def id = params['id']
		if (id == null) {
			log.error "no id in request"
			def message = message(code:"error.no.id.provided")
			response.sendError(500,message)
		}

		log.info("get object and audit for absPath: ${absPath} and id: ${id}")

		CollectionAndDataObjectListAndSearchAO collectionAndDataObjectListAndSearchAO = irodsAccessObjectFactory.getCollectionAndDataObjectListAndSearchAO(irodsAccount)
		def retObj = collectionAndDataObjectListAndSearchAO.getFullObjectForType(absPath)
		def isDataObject = retObj instanceof DataObject

		def auditedAction

		try {

			if (isDataObject) {
				log.info("is a data object, get audit for it")
				DataObjectAuditAO dataObjectAuditAO = irodsAccessObjectFactory.getDataObjectAuditAO(irodsAccount)
				IRODSFile dataObjectFile = irodsAccessObjectFactory.getIRODSFileFactory(irodsAccount).instanceIRODSFile(absPath)
				auditedAction = dataObjectAuditAO.getAuditedActionForDataObject(dataObjectFile, Integer.parseInt(id))
			} else {
				log.info("is a collection, get audit info for it")
			}
		} catch (DataNotFoundException e) {
			log.error("no audit data found")
			response.sendError(500,e.message)
			return
		}

		render(view:"auditInfo", model:[auditedAction:auditedAction])
	}

	/**
	 * Load the audit details area for a data object, this will show the main form, and
	 *  subsequently, the table will be loaded via AJAX
	 */
	def auditList = {
		def absPath = params['absPath']
		if (absPath == null) {
			log.error "no absPath in request for showAclDetails()"
			def message = message(code:"error.no.path.provided")
			response.sendError(500,message)
		}

		log.info("auditListDataObject for absPath: ${absPath}")

		CollectionAndDataObjectListAndSearchAO collectionAndDataObjectListAndSearchAO = irodsAccessObjectFactory.getCollectionAndDataObjectListAndSearchAO(irodsAccount)
		def retObj = collectionAndDataObjectListAndSearchAO.getFullObjectForType(absPath)
		def isDataObject = retObj instanceof DataObject

		render(view:"auditDetails", model:[dataObject:retObj])
	}
}
