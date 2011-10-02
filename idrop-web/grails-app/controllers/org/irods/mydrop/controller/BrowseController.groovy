package org.irods.mydrop.controller

import grails.converters.*

import org.irods.jargon.core.connection.*
import org.irods.jargon.core.exception.*
import org.irods.jargon.core.pub.*
import org.irods.jargon.core.pub.domain.DataObject
import org.irods.jargon.usertagging.FreeTaggingService
import org.irods.jargon.usertagging.TaggingServiceFactory
import org.springframework.security.core.context.SecurityContextHolder


/**
 * Controller for browser functionality
 * @author Mike Conway - DICE (www.irods.org)
 */

class BrowseController {

	IRODSAccessObjectFactory irodsAccessObjectFactory
	TaggingServiceFactory taggingServiceFactory
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
	 * Set the parent dir based on the possibility of 'strict ACL' being set
	 */
	def establishParentDir = {
		
		log.info("establishParentDir")
		
		def parent = params['dir']
		log.info "loading tree for parent path: ${parent}"
		
		if (!parent) {
			log.error "no parent param set"
			throw new 
			JargonException("no parent param set")
		}
		
		if (parent != "/") {
			log.info "parent not root use as is"
		} else {
			log.info "parent set to root, see if strict acl set"
			def environmentalInfoAO = irodsAccessObjectFactory.getEnvironmentalInfoAO(irodsAccount)
			def isStrict = environmentalInfoAO.isStrictACLs()
			log.info "is strict?:{isStrict}"
			if (isStrict) {
				parent = "/" + irodsAccount.zone + "/home/" + irodsAccount.userName + "/"
			}
			
		}

		log.info "set root dir as: ${parent}"	
		def jsonResult = ["parent" : parent]
			
		log.info "jsonResult:${jsonResult}"
		
		render jsonResult as JSON
	
	}

	/**
	 * Render the tree node data for the given parent.  This will use the HTML style AJAX response to depict the children using unordered lists.
	 * <p/>
	 *  Requires param 'dir' from request to derive parent
	 *
	 */
	def loadTree = {
		def parent = params['dir']
		log.info "loading tree for parent path: ${parent}"
		def collectionAndDataObjectListAndSearchAO = irodsAccessObjectFactory.getCollectionAndDataObjectListAndSearchAO(irodsAccount)
		def collectionAndDataObjectList = collectionAndDataObjectListAndSearchAO.listDataObjectsAndCollectionsUnderPath(parent)
		log.debug("retrieved collectionAndDataObjectList: ${collectionAndDataObjectList}")
		render collectionAndDataObjectList as JSON
	}

	def ajaxDirectoryListingUnderParent = {
		def parent = params['dir']
		log.info "ajaxDirectoryListingUnderParent path: ${parent}"
		
		if (!parent) {
			log.error "no dir param set"
			throw new
			JargonException("no dir param set")
		}
		
		if (parent != "/") {
			log.info "parent not root use as is"
		} else {
			log.info "parent set to root, see if strict acl set"
			def environmentalInfoAO = irodsAccessObjectFactory.getEnvironmentalInfoAO(irodsAccount)
			def isStrict = environmentalInfoAO.isStrictACLs()
			log.info "is strict?:{isStrict}"
			if (isStrict) {
				parent = "/" + irodsAccount.zone + "/home/" + irodsAccount.userName 
			}
			
		}
		
		def collectionAndDataObjectListAndSearchAO = irodsAccessObjectFactory.getCollectionAndDataObjectListAndSearchAO(irodsAccount)
		def collectionAndDataObjectList = collectionAndDataObjectListAndSearchAO.listDataObjectsAndCollectionsUnderPath(parent)
		log.debug("retrieved collectionAndDataObjectList: ${collectionAndDataObjectList}")
		
		def jsonBuff = []
		//jsonBuff.add(['parent':parent])

		collectionAndDataObjectList.each {

			def icon
			def state
			def type
			if (it.isDataObject()) {
				icon = "../images/file.png"
				state = "open"
				type = "file"
			} else {
				icon = "folder" 
				state = "closed"
				type = "folder"
			}

			def attrBuf = ["id":it.formattedAbsolutePath, "rel":type]

			jsonBuff.add(
					["data": it.nodeLabelDisplayValue,"attr":attrBuf, "state":state,"icon":icon, "type":type]
					)
		}

		render jsonBuff as JSON
	}
	
	def displayBrowseGridDetails = {
		def absPath = params['absPath']
		if (absPath == null) {
			throw new JargonException("no absolute path passed to the method")
		}
		
		log.info "displayBrowseGridDetails for absPath: ${absPath}"
		CollectionAndDataObjectListAndSearchAO collectionAndDataObjectListAndSearchAO = irodsAccessObjectFactory.getCollectionAndDataObjectListAndSearchAO(irodsAccount)
		def entries = collectionAndDataObjectListAndSearchAO.listDataObjectsAndCollectionsUnderPath(absPath)
		log.debug("retrieved collectionAndDataObjectList: ${entries}")
		render(view:"browseDetails", model:[collection:entries])
		
	}
	
	def displayPulldownDataDetails = {
		def absPath = params['absPath']
		if (absPath == null) {
			throw new JargonException("no absolute path passed to the method")
		}
		
		log.info "displayPulldownDataDetails for absPath: ${absPath}"
		render(view:"pulldownDataDetails")
		
	}


	/**
	 * Build data for the 'large' file info display
	 */
	def fileInfo = {
		def absPath = params['absPath']
		if (absPath == null) {
			throw new JargonException("no absolute path passed to the method")
		}

		log.info "fileInfo for absPath: ${absPath}"
		CollectionAndDataObjectListAndSearchAO collectionAndDataObjectListAndSearchAO = irodsAccessObjectFactory.getCollectionAndDataObjectListAndSearchAO(irodsAccount)

		// TODO: some sort of catch and display of no data available in info?
		def retObj = collectionAndDataObjectListAndSearchAO.getFullObjectForType(absPath)

		def isDataObject = retObj instanceof DataObject

		log.info "is this a data object? ${isDataObject}"

		FreeTaggingService freeTaggingService = taggingServiceFactory.instanceFreeTaggingService(irodsAccount)
		if (isDataObject) {
			log.info("getting free tags for data object")
			def freeTags = freeTaggingService.getTagsForDataObjectInFreeTagForm(absPath)
			log.info("rendering as data object: ${retObj}")
			render(view:"dataObjectInfo", model:[dataObject:retObj,tags:freeTags])
		} else {
			log.info("getting free tags for collection")
			def freeTags = freeTaggingService.getTagsForCollectionInFreeTagForm(absPath)
			log.info("rendering as collection: ${retObj}")
			render(view:"collectionInfo", model:[collection:retObj,tags:freeTags])
		}
	}
	
	/**
	* Build data for the 'large' file info display
	*/
   def miniInfo = {
	   def absPath = params['absPath']
	   if (absPath == null) {
		   throw new JargonException("no absolute path passed to the method")
	   }

	   log.info "mini for absPath: ${absPath}"
	   CollectionAndDataObjectListAndSearchAO collectionAndDataObjectListAndSearchAO = irodsAccessObjectFactory.getCollectionAndDataObjectListAndSearchAO(irodsAccount)

	   def retObj = collectionAndDataObjectListAndSearchAO.getFullObjectForType(absPath)

	   def isDataObject = retObj instanceof DataObject

	   log.info "is this a data object? ${isDataObject}"

	   FreeTaggingService freeTaggingService = taggingServiceFactory.instanceFreeTaggingService(irodsAccount)
	   if (isDataObject) {
		   log.info("getting free tags for data object")
		   def freeTags = freeTaggingService.getTagsForDataObjectInFreeTagForm(absPath)
		   log.info("rendering as data object: ${retObj}")
		   render(view:"miniInfoDataObject", model:[dataObject:retObj,tags:freeTags])
	   } else {
		   log.info("getting free tags for collection")
		   def freeTags = freeTaggingService.getTagsForCollectionInFreeTagForm(absPath)
		   log.info("rendering as collection: ${retObj}")
		   render(view:"miniInfoCollection", model:[collection:retObj,tags:freeTags])
	   }
   }
}
