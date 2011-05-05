package org.irods.mydrop.controller


import org.irods.jargon.core.connection.IRODSAccount
import org.irods.jargon.core.exception.JargonException
import org.irods.jargon.core.exception.JargonRuntimeException
import org.irods.jargon.core.protovalues.FilePermissionEnum
import org.irods.jargon.core.pub.CollectionAO
import org.irods.jargon.core.pub.CollectionAndDataObjectListAndSearchAO
import org.irods.jargon.core.pub.DataObjectAO
import org.irods.jargon.core.pub.IRODSAccessObjectFactory
import org.irods.jargon.core.pub.domain.DataObject
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


	def listAcl = {

		def absPath = params['absPath']
		if (absPath == null) {
			throw new JargonException("no absolute path passed to the method")
		}

		log.info("listAcl for absPath: ${absPath}")
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
			flash.message = "Unable to find ACL data"
		}

		render(view:"aclDetails", model:[acls:acls])
	}
	
	/**
	 * Display an Acl dialog for an add or edit
	 */
	def prepareAclDialog = {
		log.info "prepareAclDialog"
		log.info "params: ${params}"
			
		// if a user is provided, this will be an edit, otherwise, it's a create
		def userName = params['userName'];
		def absPath = params['absPath']
		
		if (!absPath) {
			log.error "no absPath in request for prepareAclDialog()"
			throw new JargonException("a path was not supplied")
		}
		
		render(view:"aclDialog", model:[absPath:absPath, userName:userName, userPermissionEnum:FilePermissionEnum.listAllValues()])
		
	}
	
	/**
	 * Update the ACL by responding to an AJAX editable update on a node. This uses the editable feature of the
	 * ACL JQuery table
	 */
	def updateAcl = {
		log.info "updating ACL"
		log.info "params: ${params}"
		def userName = params['userName'];
		def acl = params['acl']
		def absPath = params['absPath']


		if (!userName) {
			throw new JargonException("userName not supplied")
		}

		if (!acl) {
			throw new JargonException("acl not supplied")
		}

		if (!absPath) {
			throw new JargonException("absPath not supplied")
		}

		log.info("updateACL userName: ${userName} acl: ${acl} absPath: ${absPath}")

		CollectionAndDataObjectListAndSearchAO collectionAndDataObjectListAndSearchAO = irodsAccessObjectFactory.getCollectionAndDataObjectListAndSearchAO(irodsAccount)

		def retObj = collectionAndDataObjectListAndSearchAO.getFullObjectForType(absPath)

		def isDataObject = retObj instanceof DataObject

		// FIXME: add this into the file object superclass in jargon-core

		if (isDataObject) {
			log.debug("setting ACLs for a data object")
			DataObjectAO dataObjectAO = irodsAccessObjectFactory.getDataObjectAO(irodsAccount)

			if (acl == "READ") {
				dataObjectAO.setAccessPermissionRead(irodsAccount.getZone(), absPath, userName)
			} else if (acl == "WRITE") {
				dataObjectAO.setAccessPermissionWrite(irodsAccount.getZone(), absPath, userName)
			} else if (acl == "OWN") {
				dataObjectAO.setAccessPermissionOwn(irodsAccount.getZone(), absPath, userName)
			} else {
				throw new JargonException("Unknown acl value ${acl}")
			}
		} else {
			log.debug("setting ACLs for collection")
			CollectionAO collectionAO = irodsAccessObjectFactory.getCollectionAO(irodsAccount)
			if (acl == "READ") {
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

		render "OK"
	}
}
