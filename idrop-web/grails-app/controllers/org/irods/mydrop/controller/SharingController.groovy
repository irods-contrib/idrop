package org.irods.mydrop.controller


import grails.converters.JSON

import org.irods.jargon.core.connection.IRODSAccount
import org.irods.jargon.core.exception.JargonException
import org.irods.jargon.core.exception.JargonRuntimeException
import org.irods.jargon.core.protovalues.FilePermissionEnum
import org.irods.jargon.core.pub.CollectionAO
import org.irods.jargon.core.pub.CollectionAndDataObjectListAndSearchAO
import org.irods.jargon.core.pub.DataObjectAO
import org.irods.jargon.core.pub.IRODSAccessObjectFactory
import org.irods.jargon.core.pub.UserAO
import org.irods.jargon.core.pub.domain.DataObject
import org.irods.mydrop.service.I18NMessagingService
import org.springframework.security.core.context.SecurityContextHolder


class SharingController {

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
	 * Load the acl details area, this will show the main form, and subsequently, the table will be loaded via AJAX
	 */
	def showAclDetails = {

		def absPath = params['absPath']
		if (absPath == null) {
			throw new JargonException("no absolute path passed to the method")
		}

		log.info("showAclDetails for absPath: ${absPath}")

		render(view:"aclDetails")
	}

	def renderAclDetailsTable = {

		def absPath = params['absPath']
		
		if (absPath == null) {
			log.error "no absPath in request for renderAclDetailsTable()"
			def message = message(code:"error.no.path.provided")
			response.sendError(500,message)
		}

		log.info("renderAclDetailsTable for absPath: ${absPath}")
		def acls;

		try {

			CollectionAndDataObjectListAndSearchAO collectionAndDataObjectListAndSearchAO = irodsAccessObjectFactory.getCollectionAndDataObjectListAndSearchAO(irodsAccount)

			def retObj = collectionAndDataObjectListAndSearchAO.getFullObjectForType(absPath)

			def isDataObject = retObj instanceof DataObject

			if (isDataObject) {
				log.debug("retrieving ACLs for a data object");
				DataObjectAO dataObjectAO = irodsAccessObjectFactory.getDataObjectAO(irodsAccount)
				acls = dataObjectAO.listPermissionsForDataObject(retObj.collectionName + "/" + retObj.dataName)
			} else {
				log.debug("retrieveing ACLs for collection")
				CollectionAO collectionAO = irodsAccessObjectFactory.getCollectionAO(irodsAccount)
				acls = collectionAO.listPermissionsForCollection(retObj.collectionName)
			}
		} catch (Exception je){
			log.error("exception getting acl data ${je}", je)
			response.sendError(500,je.getMessage());
		}

		render(view:"aclTable", model:[acls:acls])
	}

	/**
	 * Display an Acl dialog for an add or edit
	 */
	def prepareAclDialog = {
		log.info "prepareAclDialog"
		log.info "params: ${params}"

		// if a user is provided, this will be an edit, otherwise, it's a create
		def userName = params['userName']
		def absPath = params['absPath']
		def isCreate = params['create']

		if (!absPath) {
			log.error "no absPath in request for prepareAclDialog()"
			def message = message(code:"error.no.path.provided")
			response.sendError(500,message)
			return
		}

		render(view:"aclDialog", model:[absPath:absPath, userName:userName, userPermissionEnum:FilePermissionEnum.listAllValues(), isCreate:isCreate])

	}

	/**
	 * Add an ACL via the ACL dialog, then, in response, reload the ACL table
	 */
	def addAcl = {  AclCommand cmd ->
		log.info "addAcl"
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
			jsonData['response'] = responseData
			render jsonData as JSON
			return
		}

		// parameter vaues have been validated, proceed with updates

		CollectionAndDataObjectListAndSearchAO collectionAndDataObjectListAndSearchAO = irodsAccessObjectFactory.getCollectionAndDataObjectListAndSearchAO(irodsAccount)
		def retObj = collectionAndDataObjectListAndSearchAO.getFullObjectForType(cmd.absPath)
		def isDataObject = retObj instanceof DataObject
		log.info("adding ACLs for a data object")

		if (isDataObject) {
			DataObjectAO dataObjectAO = irodsAccessObjectFactory.getDataObjectAO(irodsAccount)

			if (cmd.acl == "READ") {
				dataObjectAO.setAccessPermissionRead(irodsAccount.getZone(), cmd.absPath, cmd.userName)
			} else if (cmd.acl == "WRITE") {
				dataObjectAO.setAccessPermissionWrite(irodsAccount.getZone(), cmd.absPath, cmd.userName)
			} else if (cmd.acl == "OWN") {
				dataObjectAO.setAccessPermissionOwn(irodsAccount.getZone(), cmd.absPath, cmd.userName)
			} else {
				log.error "invalid acl ${cmd.acl}"
				def errorMessage = message(code:"error.invalid.acl", args[cmd.acl])
				response.sendError(500,errorMessage)
				return
			}

		} else {

			log.info("setting ACLs for collection")
			CollectionAO collectionAO = irodsAccessObjectFactory.getCollectionAO(irodsAccount)

			if (cmd.acl == "READ") {
				collectionAO.setAccessPermissionRead(irodsAccount.getZone(), cmd.absPath, cmd.userName, true)
			} else if (cmd.acl == "WRITE") {
				collectionAO.setAccessPermissionWrite(irodsAccount.getZone(), cmd.absPath, cmd.userName, true)
			} else if (cmd.acl == "OWN") {
				collectionAO.setAccessPermissionOwn(irodsAccount.getZone(), cmd.absPath, cmd.userName, true)
			} else {
				log.error "invalid acl ${cmd.acl}"
				def errorMessage = message(code:"error.invalid.acl", args[cmd.acl])
				response.sendError(500,errorMessage)
				return
			}
		}

		log.info("acl set successfully")
		render "OK"

	}

	/**
	 * Update the ACL by responding to an AJAX editable update on a node. This uses the editable feature of the
	 * ACL JQuery table
	 */
	def updateAcl = {
		log.info "updating ACL"
		log.info "params: ${params}"
		def userName = params['userName']
		def acl = params['acl']
		def absPath = params['absPath']
		def isCreate = params['create']

		/*
		 if (!userName) {
		 def message = message(code:"error.no.user.name.provided")
		 throw new JargonException(message)
		 }
		 if (!acl) {
		 def message = message(code:"error.no.acl.provided")
		 throw new JargonException(message)
		 }
		 if (!absPath) {
		 def message = message(code:"error.no.path.provided")
		 throw new JargonException(message)
		 }
		 if (!isCreate) {
		 isCreate = true
		 }
		 log.info("updateACL userName: ${userName} acl: ${acl} absPath: ${absPath}")
		 CollectionAndDataObjectListAndSearchAO collectionAndDataObjectListAndSearchAO = irodsAccessObjectFactory.getCollectionAndDataObjectListAndSearchAO(irodsAccount)
		 def retObj = collectionAndDataObjectListAndSearchAO.getFullObjectForType(absPath)
		 def isDataObject = retObj instanceof DataObject
		 // FIXME: add this into the file object superclass in jargon-core
		 if (isDataObject) {
		 log.debug("setting ACLs for a data object")
		 DataObjectAO dataObjectAO = irodsAccessObjectFactory.getDataObjectAO(irodsAccount)
		 // if creating a new ACL, an ACL cannot already exist
		 if (isCreate) {
		 def existingPermission = dataObjectAO.getPermissionForDataObjectForUserName(absPath, userName)
		 if (existingPermission) {
		 def message = message(code:"error.duplicate.acl")
		 response.sendError(500,message)
		 return
		 }
		 }
		 if (cmd.acl == "READ") {
		 dataObjectAO.setAccessPermissionRead(irodsAccount.getZone(), cmd.absPath, cmd.userName)
		 } else if (acl == "WRITE") {
		 dataObjectAO.setAccessPermissionWrite(irodsAccount.getZone(), cmd.absPath, cmd.userName)
		 } else if (acl == "OWN") {
		 dataObjectAO.setAccessPermissionOwn(irodsAccount.getZone(), cmd.absPath, cmd.userName)
		 } else {
		 throw new JargonException("Unknown acl value ${cmd.acl}")
		 }
		 } else {
		 log.debug("setting ACLs for collection")
		 CollectionAO collectionAO = irodsAccessObjectFactory.getCollectionAO(irodsAccount)
		 if (isCreate) {
		 def existingPermission = collectionAO.getPermissionForUserName(absPath, userName)
		 if (existingPermission) {
		 response.sendError(500,"The given user already has a sharing permission")
		 return
		 }
		 }
		 if (cmd.acl == "READ") {
		 collectionAO.setAccessPermissionRead(irodsAccount.getZone(), absPath, userName, true)
		 } else if (acl == "WRITE") {
		 collectionAO.setAccessPermissionWrite(irodsAccount.getZone(), absPath, userName, true)
		 } else if (acl == "OWN") {
		 collectionAO.setAccessPermissionOwn(irodsAccount.getZone(), absPath, userName, true)
		 } else {
		 throw new JargonException("Unknown acl value ${acl}")
		 }
		 }
		 log.info("acl set successfully")
		 */

		render "OK"
	}

	/**
	 * List the users in iRODS based on user name.  A 'term' parameter may be supplied, in which case, a LIKE% query will be used to find
	 * matching user names.  This method returns JSON as expected for the JQuery UI autocomplete text box
	 */
	def listUsersForAutocomplete = {

		log.info("listUsersForAutocomplete")
		def term = params['term']
		if (!term) {
			term = ""
		}
		log.info("term:${term}")

		UserAO userAO = irodsAccessObjectFactory.getUserAO(irodsAccount)

		def userList = userAO.findUserNameLike(term);
		def jsonBuff = []


		userList.each {
			jsonBuff.add(
					["label": it]
					)
		}

		render jsonBuff as JSON

	}

	def deleteAcl = {
		log.info("deleteAcl")
		log.info(params)
		def absPath = params['absPath']

		if (!absPath) {
			throw new JargonException("The absPath is missing, no path specified")
		}

		def aclsToDelete = params['selectedAcl']

		log.info("aclsToDelete: ${aclsToDelete}")

		aclsToDelete.each(log.info("selectedAcl: ${it}"))

		render "OK"

	}


}

class AclCommand {
	String absPath
	String acl
	String userName
	String create
	static constraints = {
		create(blank:true, inList:["", "true", "false"])
		acl(blank:false, inList:["READ", "WRITE", "OWN"])
		userName(blank:false)
		absPath(blank:false)
	}
}

