package org.irods.mydrop.controller

import org.irods.jargon.core.connection.IRODSAccount
import org.irods.jargon.core.exception.CatNoAccessException
import org.irods.jargon.core.exception.DataNotFoundException
import org.irods.jargon.core.pub.CollectionAndDataObjectListAndSearchAO
import org.irods.jargon.core.pub.CollectionAuditAO
import org.irods.jargon.core.pub.DataObjectAuditAO
import org.irods.jargon.core.pub.IRODSAccessObjectFactory
import org.irods.jargon.core.pub.domain.AuditedAction
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

	def auditInfo = {
		log.info("auditInfo()")
		def absPath = params['absPath']
		if (absPath == null) {
			log.error "no absPath in request "
			def message = message(code:"error.no.path.provided")
			response.sendError(500,message)
		}

		def actionCode = params['actionCode']
		if (actionCode == null) {
			log.error "no actionCode in request"
			def message = message(code:"error.no.id.provided")
			response.sendError(500,message)
		}

		def timeStamp = params['timeStamp']
		if (timeStamp == null) {
			log.error "no timeStamp in request"
			def message = message(code:"error.no.timestamp.provided")
			response.sendError(500,message)
		}

		log.info("get object and audit for absPath: ${absPath} and actionCode: ${actionCode}")

		CollectionAndDataObjectListAndSearchAO collectionAndDataObjectListAndSearchAO = irodsAccessObjectFactory.getCollectionAndDataObjectListAndSearchAO(irodsAccount)
		def retObj = collectionAndDataObjectListAndSearchAO.getFullObjectForType(absPath)
		def isDataObject = retObj instanceof DataObject

		def auditedAction

		try {

			if (isDataObject) {
				log.info("is a data object, get audit for it")
				DataObjectAuditAO dataObjectAuditAO = irodsAccessObjectFactory.getDataObjectAuditAO(irodsAccount)
				IRODSFile dataObjectFile = irodsAccessObjectFactory.getIRODSFileFactory(irodsAccount).instanceIRODSFile(absPath)
				auditedAction = dataObjectAuditAO.getAuditedActionForDataObject(dataObjectFile, actionCode, timeStamp)
			} else {
				log.info("is a collection, get audit info for it")
				CollectionAuditAO collectionAuditAO = irodsAccessObjectFactory.getCollectionAuditAO(irodsAccount)
				IRODSFile collectionFile = irodsAccessObjectFactory.getIRODSFileFactory(irodsAccount).instanceIRODSFile(absPath)
				auditedAction = collectionAuditAO.getAuditedActionForCollection(collectionFile, actionCode, timeStamp)
			}
		} catch (DataNotFoundException e) {
			log.error("no audit data found")
			response.sendError(500,e.message)
			return
		}

		render(view:"auditInfo", model:[auditedAction:auditedAction])
	}


	def auditTable = {
		def absPath = params['absPath']
		if (absPath == null) {
			log.error "no absPath in request for showAclDetails()"
			def message = message(code:"error.no.path.provided")
			response.sendError(500,message)
		}

		def pageSize = params['pageSize']
		if (pageSize == null) {
			pageSize = "1000"
		}

		def offset = params["offset"]
		if (offset == null) {
			offset = "0"
		}

		int usePageSize = Integer.parseInt(pageSize)
		int useOffset = Integer.parseInt(offset)

		log.info("auditListDataObject for absPath: ${absPath}")

		CollectionAndDataObjectListAndSearchAO collectionAndDataObjectListAndSearchAO = irodsAccessObjectFactory.getCollectionAndDataObjectListAndSearchAO(irodsAccount)
		def retObj = collectionAndDataObjectListAndSearchAO.getFullObjectForType(absPath)
		def isDataObject = retObj instanceof DataObject

		try {
			def auditedActions
			if (isDataObject) {
				log.info("is a data object, get audit for it")
				DataObjectAuditAO dataObjectAuditAO = irodsAccessObjectFactory.getDataObjectAuditAO(irodsAccount)
				IRODSFile dataObjectFile = irodsAccessObjectFactory.getIRODSFileFactory(irodsAccount).instanceIRODSFile(absPath)
				auditedActions = dataObjectAuditAO.findAllAuditRecordsForDataObject(dataObjectFile, useOffset, usePageSize)
			} else {
				log.info("is a collection, get audit info for it")
				CollectionAuditAO collectionAuditAO = irodsAccessObjectFactory.getCollectionAuditAO(irodsAccount)
				IRODSFile dataObjectFile = irodsAccessObjectFactory.getIRODSFileFactory(irodsAccount).instanceIRODSFile(absPath)
				auditedActions = collectionAuditAO.findAllAuditRecordsForCollection(dataObjectFile, useOffset, usePageSize)
			}

			boolean pageableForward = false
			boolean pageableBackwards = false
			int firstCount = 0
			int lastCount = 0

			if (!auditedActions.empty) {
				AuditedAction first = auditedActions.get(0)
				firstCount = first.count
				if (first.getCount() > 1) {
					pageableBackwards = true
				}
				AuditedAction last = auditedActions.get(auditedActions.size() -1)
				lastCount = lastCount
				if (!last.lastResult)  {
					pageableForward = true
				}
			}

			log.info("pageable forward:${pageableForward}")
			log.info("pageable backwards:${pageableBackwards}")

			render(view:"auditTable", model:[auditedActions:auditedActions, pageableForward:pageableForward, pageableBackwards:pageableBackwards, firstCount:firstCount, lastCount:lastCount])
			return
		} catch(CatNoAccessException cna) {
			render(view:"auditNoAccess")
			return
		}
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

		try {
			CollectionAndDataObjectListAndSearchAO collectionAndDataObjectListAndSearchAO = irodsAccessObjectFactory.getCollectionAndDataObjectListAndSearchAO(irodsAccount)
			def retObj = collectionAndDataObjectListAndSearchAO.getFullObjectForType(absPath)
			def isDataObject = retObj instanceof DataObject

			render(view:"auditDetails", model:[dataObject:retObj])
		} catch (org.irods.jargon.core.exception.FileNotFoundException fnf) {
			log.info("file not found looking for data, show stand-in page", fnf)
			render(view:"/browse/noInfo")
		}
	}
}
