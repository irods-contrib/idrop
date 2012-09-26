package org.irods.mydrop.controller

import grails.converters.*

import org.irods.jargon.core.connection.*
import org.irods.jargon.core.exception.*
import org.irods.jargon.core.protovalues.FilePermissionEnum
import org.irods.jargon.core.pub.*
import org.irods.jargon.core.pub.domain.DataObject
import org.irods.jargon.core.pub.io.IRODSFile
import org.irods.jargon.core.utils.IRODSUriUtils
import org.irods.jargon.core.utils.LocalFileUtils
import org.irods.jargon.datautils.image.MediaHandlingUtils
import org.irods.jargon.datautils.sharing.*
import org.irods.jargon.ticket.TicketDistributionContext
import org.irods.jargon.usertagging.FreeTaggingService
import org.irods.jargon.usertagging.IRODSTaggingService
import org.irods.jargon.usertagging.TaggingServiceFactory
import org.irods.mydrop.service.ShoppingCartSessionService

/**
 * Controller for browser functionality
 * @author Mike Conway - DICE (www.irods.org)
 */

class BrowseController {

	IRODSAccessObjectFactory irodsAccessObjectFactory
	TaggingServiceFactory taggingServiceFactory
	IRODSAccount irodsAccount
	ShoppingCartSessionService shoppingCartSessionService
	def grailsApplication

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
	
	
	def index = {
		log.info ("in index action")
		def mode = params['mode']
		def absPath = params['absPath']

		if (mode != null) {
			if (mode == "path") {
				log.info("mode is path, should have an abspath to preset to")
				if (absPath == null) {
					def message = message(code:"error.no.path.provided")
					response.sendError(500,message)
					return
				} else {
					log.info("path is ${absPath}")
				}
			}
		}

		render(view: "index", model:[mode:mode,path:absPath])
	}

	def showBrowseToolbar = {
		log.info("showBrowseToolbar")
		render(view:"browseToolbar")
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
			if (irodsAccount.userName ==  "anonymous") {
				log.info("user is anonymous, default to view the public directory")

				parent = "/" + irodsAccount.zone + "/home/public"
			} else {

				def isStrict = environmentalInfoAO.isStrictACLs()
				log.info "is strict?:{isStrict}"
				if (isStrict) {
					parent = "/" + irodsAccount.zone + "/home/" + irodsAccount.userName
				} else {
					parent = "/"
				}
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

	/**
	 * Called by tree control to load directories under a given node
	 */
	def ajaxDirectoryListingUnderParent = {
		log.info "ajaxDirectoryListingUnderParent()}"
		def parent = params['dir']
		def pathType = params['type']

		if (parent == null) {
			throw new JargonException("no parent parm passed to this method")
		}

		log.info("parent:${parent}")

		if (pathType == null) {
			pathType = "list"
		}

		log.info("parthType:${pathType}")

		def jsonBuff = []
		def icon
		def state
		def type

		icon = "folder"
		state = "closed"
		type = "folder"
		def environmentalInfoAO = irodsAccessObjectFactory.getEnvironmentalInfoAO(irodsAccount)

		// look at the type to decide how to set the root path
		if (pathType == "detect") {
			log.info("no parent parm set, detect display as either root or home")

			if (irodsAccount.userName ==  "anonymous") {
				log.info("user is anonymous, default to view the public directory")

				parent = "/" + irodsAccount.zone + "/home/public"

			} else {

				def isStrict = environmentalInfoAO.isStrictACLs()
				log.info "is strict?:{isStrict}"
				if (isStrict) {
					parent = "/" + irodsAccount.zone + "/home/" + irodsAccount.userName
				} else {
					parent = "/"
				}
			}

			icon = "folder"
			state = "closed"
			type = "folder"

			def attrBuf = ["id":parent, "rel":type, "absPath":parent]

			jsonBuff.add(
					["data": parent,"attr":attrBuf, "state":state,"icon":icon, "type":type]
					)

		} else if (pathType == "root") {

			log.info("display the root node")
			// display a root node
			parent = "/"

			icon = "folder"
			state = "closed"
			type = "folder"

			def attrBuf = ["id":parent, "rel":type, "absPath":parent]

			jsonBuff.add(
					["data": parent,"attr":attrBuf, "state":state,"icon":icon, "type":type]
					)


		} else if (pathType == "home") {

			if (irodsAccount.userName ==  "anonymous") {
				log.info("user is anonymous, default to view the public directory")

				parent = "/" + irodsAccount.zone + "/home/public"

			} else {

				parent = "/" + irodsAccount.zone + "/home/" + irodsAccount.userName
			}

			log.info("setting to home directory:${parent}")

			// display a root node
			// display a root node

			icon = "folder"
			state = "closed"
			type = "folder"

			def attrBuf = ["id":parent, "rel":type, "absPath":parent]

			jsonBuff.add(
					["data": parent,"attr":attrBuf, "state":state,"icon":icon, "type":type]
					)


		} else if (pathType == "path") {

			log.info("attempt to set to given path")
			if (parent == "") {
				parent = "/"
			}
			// display a root node

			icon = "folder"
			state = "closed"
			type = "folder"

			def attrBuf = ["id":parent, "rel":type, "absPath":parent]

			jsonBuff.add(
					["data": parent,"attr":attrBuf, "state":state,"icon":icon, "type":type]
					)
		} else if (pathType == "list") {

			log.info("parent dir for listing provided as:${parent}")
			def collectionAndDataObjectListAndSearchAO = irodsAccessObjectFactory.getCollectionAndDataObjectListAndSearchAO(irodsAccount)
			def collectionAndDataObjectList = collectionAndDataObjectListAndSearchAO.listDataObjectsAndCollectionsUnderPath(parent)
			log.debug("retrieved collectionAndDataObjectList: ${collectionAndDataObjectList}")
			collectionAndDataObjectList.each {


				if (it.isDataObject()) {
					icon = "../images/file.png"
					state = "open"
					type = "file"
				} else {
					icon = "folder"
					state = "closed"
					type = "folder"
				}

				def attrBuf = ["id":it.formattedAbsolutePath, "rel":type, "absPath":it.formattedAbsolutePath]

				jsonBuff.add(
						["data": it.nodeLabelDisplayValue,"attr":attrBuf, "state":state,"icon":icon, "type":type]
						)
			}
		} else {
			throw new JargonException("invalid path type:${pathType}")
		}

		render jsonBuff as JSON
	}

	def displayBrowseGridDetails = {
		def absPath = params['absPath']
		if (absPath == null) {
			throw new JargonException("no absolute path passed to the method")
		}
		
		log.info "displayBrowseGridDetails for absPath: ${absPath}"
		try {
			CollectionAndDataObjectListAndSearchAO collectionAndDataObjectListAndSearchAO = irodsAccessObjectFactory.getCollectionAndDataObjectListAndSearchAO(irodsAccount)
			def retObj = collectionAndDataObjectListAndSearchAO.getFullObjectForType(absPath)
			def isDataObject = retObj instanceof DataObject


			// if data object, show the info details instead...
			if (isDataObject) {
				ViewNameAndModelValues mav = handleInfoLookup(absPath)
				render(view:mav.view, model:mav.model)
				return
			}

			def entries = collectionAndDataObjectListAndSearchAO.listDataObjectsAndCollectionsUnderPath(absPath)
			log.debug("retrieved collectionAndDataObjectList: ${entries}")
			render(view:"browseDetails", model:[collection:entries, parent:retObj, showLite:collectionAndDataObjectListAndSearchAO.getIRODSServerProperties().isTheIrodsServerAtLeastAtTheGivenReleaseVersion("rods3.0")])
		} catch (FileNotFoundException fnf) {
			log.info("file not found looking for data, show stand-in page", fnf)
			render(view:"noInfo")
		}

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
		log.info("fileInfo()>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>")
		def absPath = params['absPath']
		if (absPath == null) {
			throw new JargonException("no absolute path passed to the method")
		}

		ViewNameAndModelValues mav = handleInfoLookup(absPath)
		render(view:mav.view, model:mav.model)
		return

		log.info "fileInfo for absPath: ${absPath}"
		CollectionAndDataObjectListAndSearchAO collectionAndDataObjectListAndSearchAO = irodsAccessObjectFactory.getCollectionAndDataObjectListAndSearchAO(irodsAccount)
		def retObj = null
		// If I cant find any data just put a message up in the display area
		try {
			retObj = collectionAndDataObjectListAndSearchAO.getFullObjectForType(absPath)

			if (!retObj) {
				log.error "no data found for path ${absPath}"
				render(view:"noInfo")
				return
			}
		} catch (DataNotFoundException) {
			render(view:"noInfo")
			return
		}

		def isDataObject = retObj instanceof DataObject
		def getThumbnail = false
		def renderMedia = false

		log.info "is this a data object? ${isDataObject}"

		FreeTaggingService freeTaggingService = taggingServiceFactory.instanceFreeTaggingService(irodsAccount)
		IRODSTaggingService irodsTaggingService = taggingServiceFactory.instanceIrodsTaggingService(irodsAccount)
		if (isDataObject) {
			long maxSize
			String maxSizeParm = grailsApplication.config.idrop.config.max.thumbnail.size.mb
			if (maxSizeParm != null) {
				try {
					maxSize = Long.valueOf(maxSizeParm) * 1024 * 1024
				} catch (Exception e) {
					maxSize = 32 * 1024 * 1024
				}
			}

			log.info("data size: ${retObj.dataSize}, max size: ${maxSize}")

			if (retObj.dataSize > maxSize) {
				log.info("do not render media")
				renderMedia = false
			}

			getThumbnail = MediaHandlingUtils.isImageFile(absPath)
			log.info("getThumbnail? ${getThumbnail}")

			if (!getThumbnail) {
				renderMedia = MediaHandlingUtils.isMediaFile(absPath)
				log.info("renderMedia? ${renderMedia}")
			}

			log.info("getting free tags for data object")
			def freeTags = freeTaggingService.getTagsForDataObjectInFreeTagForm(absPath)
			log.info("rendering as data object: ${retObj}")
			def commentTag = irodsTaggingService.getDescriptionOnDataObjectForLoggedInUser(absPath)

			def comment = ""
			if (commentTag) {
				comment = commentTag.getTagData()
			}

			render(view:"dataObjectInfo", model:[dataObject:retObj,tags:freeTags,comment:comment,getThumbnail:getThumbnail,renderMedia:renderMedia,isDataObject:isDataObject,showLite:collectionAndDataObjectListAndSearchAO.getIRODSServerProperties().isTheIrodsServerAtLeastAtTheGivenReleaseVersion("rods3.0")])
		} else {
			log.info("getting free tags for collection")
			def freeTags = freeTaggingService.getTagsForCollectionInFreeTagForm(absPath)
			def commentTag = irodsTaggingService.getDescriptionOnCollectionForLoggedInUser(absPath)

			def comment = ""
			if (commentTag) {
				comment = commentTag.getTagData()
			}
			log.info("rendering as collection: ${retObj}")
			render(view:"collectionInfo", model:[collection:retObj,comment:comment,tags:freeTags,  isDataObject:isDataObject, showLite:collectionAndDataObjectListAndSearchAO.getIRODSServerProperties().isTheIrodsServerAtLeastAtTheGivenReleaseVersion("rods3.0")])
		}
	}

	/**
	 * Build data for the 'mini' file info display
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

		def getThumbnail = false

		log.info "is this a data object? ${isDataObject}"

		FreeTaggingService freeTaggingService = taggingServiceFactory.instanceFreeTaggingService(irodsAccount)
		IRODSTaggingService irodsTaggingService = taggingServiceFactory.instanceIrodsTaggingService(irodsAccount)
		if (isDataObject) {
			log.info("getting free tags for data object")
			def freeTags = freeTaggingService.getTagsForDataObjectInFreeTagForm(absPath)
			def commentTag = irodsTaggingService.getDescriptionOnDataObjectForLoggedInUser(absPath)

			def comment = ""
			if (commentTag) {
				comment = commentTag.getTagData()
			}

			log.info("rendering as data object: ${retObj}")

			String extension = LocalFileUtils.getFileExtension(retObj.dataName).toUpperCase()
			log.info("extension is:${extension}")

			if (extension == ".JPG" || extension == ".GIF" || extension == ".PNG" || extension == ".TIFF" ||   extension == ".TIF") {
				getThumbnail = true
			}

			render(view:"dataObjectInfoMini", model:[dataObject:retObj,tags:freeTags,comment:comment,getThumbnail:getThumbnail])
		} else {
			log.info("getting free tags for collection")
			def freeTags = freeTaggingService.getTagsForCollectionInFreeTagForm(absPath)

			def commentTag = irodsTaggingService.getDescriptionOnCollectionForLoggedInUser(absPath)

			def comment = ""
			if (commentTag) {
				comment = commentTag.getTagData()
			}

			log.info("comment was:${comment}")

			log.info("rendering as collection: ${retObj}")
			render(view:"collectionInfoMini", model:[collection:retObj,comment:comment,tags:freeTags])
		}
	}

	/**
	 * build the rename dialog
	 */
	def prepareRenameDialog = {
		log.info("prepareRenameDialog()")

		def absPath = params['absPath']
		if (absPath == null) {
			log.error "no absPath in request"
			def message = message(code:"error.no.path.provided")
			response.sendError(500,message)
		}

		log.info("abs path:${absPath}")

		/*
		 * Get the last part of the path from the given absolute path
		 */

		IRODSFile targetFile = irodsAccessObjectFactory.getIRODSFileFactory(irodsAccount).instanceIRODSFile(absPath)

		log.info("target file obtained")
		if (!targetFile.exists()) {
			log.error "absPath does not exist in iRODS"
			def message = message(code:"error.no.data.found")
			response.sendError(500,message)
		}

		log.info("target file exists")

		String fileName = targetFile.name
		render(view:"renameDialog", model:[fileName:fileName, absPath:absPath])
	}


	/**
	 * Prepare the 'new folder' dialog
	 */
	def prepareNewFolderDialog = {
		log.info("prepareNewFolderDialog()")

		def absPath = params['absPath']
		if (absPath == null) {
			log.error "no absPath in request"
			def message = message(code:"error.no.path.provided")
			response.sendError(500,message)
		}

		log.info("abs path:${absPath}")

		/*
		 * If this is a data object, get the parent
		 */

		String parentPath = null

		IRODSFile targetFile = irodsAccessObjectFactory.getIRODSFileFactory(irodsAccount).instanceIRODSFile(absPath)

		log.info("target file obtained")
		if (!targetFile.exists()) {
			log.error "absPath does not exist in iRODS"
			def message = message(code:"error.no.data.found")
			response.sendError(500,message)
		}

		if (targetFile.isFile()) {
			log.info("is a file, use the parent collection as the parent of the new folder")
			parentPath = targetFile.getParent()
		} else {
			log.info("is a collection use abs path as parent of new folder")
			parentPath = targetFile.getAbsolutePath()
		}

		log.info("parent path:${parentPath}")

		String fileName = targetFile.name
		render(view:"newFolderDialog", model:[absPath:absPath])
	}

	def addFileToCart = {
		log.info ("addFileToCart")
		String fileName = params['absPath']
		if (!fileName) {
			log.error "no file name in request"
			def message = message(code:"error.no.path.provided")
			response.sendError(500,message)
		}

		log.info("adding ${fileName} to the shopping cart")

		shoppingCartSessionService.addToCart(fileName)

		log.info("shopping cart: ${session.shoppingCart}")

		log.info("file added")
		render fileName
	}

	/**
	 * Display the contents of the 'file cart tab'.  This is a cascading operation, such that the loading of the tab will call the process
	 * to load the cart contents table.
	 */
	def showCartTab = {
		log.info("showCartTab")
		render(view:"listCart")
	}

	/**
	 * Build the JTable entries for the contents of the shopping cart
	 */
	def listCart = {
		log.info("listCart")
		List<String> cart = shoppingCartSessionService.listCart()
		render(view:"cartDetails", model:[cart:cart])
	}

	/**
	 * Clear the contents of the shopping cart
	 */
	def clearCart = {
		log.info("clearCart")
		shoppingCartSessionService.clearCart()
		render "OK"
	}

	/**
	 * Delete the given files from the shopping cart
	 */
	def deleteFromCart = {
		log.info("deleteFromCart")
		log.info("params: ${params}")

		def filesToDelete = params['selectCart']

		// if nothing selected, just jump out and return a message
		if (!filesToDelete) {
			log.info("no files to delete")
			render "OK"
			return
		}

		log.info("filesToDelete: ${filesToDelete}")

		if (filesToDelete instanceof Object[]) {
			log.debug "is array"
			filesToDelete.each{
				log.info "filesToDelete: ${it}"
				shoppingCartSessionService.deleteFromCart(it)
			}

		} else {
			log.debug "not array"
			log.info "deleting: ${filesToDelete}"
			shoppingCartSessionService.deleteFromCart(filesToDelete)
		}

		render "OK"
	}


	/**
	 * Process a bulk add to cart action based on data input from the browse details form
	 */
	def addToCartBulkAction = {
		log.info("addToCartBulkAction")

		log.info("params: ${params}")

		def filesToAdd = params['selectDetail']

		// if nothing selected, just jump out and return a message
		if (!filesToAdd) {
			log.info("no files to add")
			render "OK"
			return
		}

		log.info("filesToAdd: ${filesToAdd}")


		if (filesToAdd instanceof Object[]) {
			log.debug "is array"
			filesToAdd.each{
				log.info "filesToAdd: ${it}"
				shoppingCartSessionService.addToCart(it)

			}

		} else {
			log.debug "not array"
			log.info "adding: ${filesToAdd}"
			shoppingCartSessionService.addToCart(filesToAdd)
		}

		render "OK"
	}

	/**
	 * Show gallery view for given directory
	 */
	def galleryView = {
		log.info("galleryView()")
		def absPath = params['absPath']
		if (absPath == null) {
			throw new JargonException("no absolute path passed to the method")
		}

		try {
			log.info "galleryView for absPath: ${absPath}"
			CollectionAndDataObjectListAndSearchAO collectionAndDataObjectListAndSearchAO = irodsAccessObjectFactory.getCollectionAndDataObjectListAndSearchAO(irodsAccount)
			def retObj = collectionAndDataObjectListAndSearchAO.getFullObjectForType(absPath)
			def isDataObject = retObj instanceof DataObject

			// if data object, show the info details instead...
			if (isDataObject) {
				redirect(action:"fileInfo", params:[absPath:absPath])
				return
			}

			def entries = collectionAndDataObjectListAndSearchAO.listDataObjectsUnderPath(absPath,0)
			log.debug("retrieved collectionAndDataObjectList: ${entries}")
			render(view:"galleryView", model:[collection:entries, parent:retObj, showLite:collectionAndDataObjectListAndSearchAO.getIRODSServerProperties().isTheIrodsServerAtLeastAtTheGivenReleaseVersion("rods3.0")])
		} catch (org.irods.jargon.core.exception.FileNotFoundException fnf) {
			log.info("file not found looking for data, show stand-in page", fnf)
			render(view:"noInfo")
		}
	}

	private ViewNameAndModelValues handleInfoLookup(String absPath) {

		log.info "fileInfo for absPath: ${absPath}"
		ViewNameAndModelValues mav = new ViewNameAndModelValues()
		CollectionAndDataObjectListAndSearchAO collectionAndDataObjectListAndSearchAO = irodsAccessObjectFactory.getCollectionAndDataObjectListAndSearchAO(irodsAccount)
		def retObj = null
		// If I cant find any data just put a message up in the display area
		try {
			retObj = collectionAndDataObjectListAndSearchAO.getFullObjectForType(absPath)

			if (!retObj) {
				log.error "no data found for path ${absPath}"
				mav.view = "noInfo"
				return mav
			}
		} catch (DataNotFoundException) {
			log.error "no data found for path ${absPath}"
			mav.view = "noInfo"
			return mav
		}

		def isDataObject = retObj instanceof DataObject
		def getThumbnail = false
		def renderMedia = false


		log.info "is this a data object? ${isDataObject}"

		FreeTaggingService freeTaggingService = taggingServiceFactory.instanceFreeTaggingService(irodsAccount)
		IRODSTaggingService irodsTaggingService = taggingServiceFactory.instanceIrodsTaggingService(irodsAccount)
		if (isDataObject) {
			long maxSize
			String maxSizeParm = grailsApplication.config.idrop.config.max.thumbnail.size.mb
			if (maxSizeParm != null) {
				try {
					maxSize = Long.valueOf(maxSizeParm) * 1024 * 1024
				} catch (Exception e) {
					maxSize = 32 * 1024 * 1024
				}
			}

			log.info("data size: ${retObj.dataSize}, max size: ${maxSize}")

			getThumbnail = MediaHandlingUtils.isImageFile(absPath)
			log.info("getThumbnail? ${getThumbnail}")

			renderMedia = MediaHandlingUtils.isMediaFile(absPath)
			log.info("renderMedia? ${renderMedia}")


			if (retObj.dataSize > maxSize) {
				log.info("do not render media or thumb")
				renderMedia = false
				getThumbnail = false
			}

			log.info("getting free tags for data object")
			def freeTags = freeTaggingService.getTagsForDataObjectInFreeTagForm(absPath)
			log.info("rendering as data object: ${retObj}")
			def commentTag = irodsTaggingService.getDescriptionOnDataObjectForLoggedInUser(absPath)

			def comment = ""
			if (commentTag) {
				comment = commentTag.getTagData()
			}

			mav.view = "dataObjectInfo"
			mav.model = [dataObject:retObj,tags:freeTags,comment:comment,getThumbnail:getThumbnail,renderMedia:renderMedia,isDataObject:isDataObject,showLite:collectionAndDataObjectListAndSearchAO.getIRODSServerProperties().isTheIrodsServerAtLeastAtTheGivenReleaseVersion("rods3.0")]
			return mav
		} else {
			log.info("getting free tags for collection")
			def freeTags = freeTaggingService.getTagsForCollectionInFreeTagForm(absPath)
			def commentTag = irodsTaggingService.getDescriptionOnCollectionForLoggedInUser(absPath)

			def comment = ""
			if (commentTag) {
				comment = commentTag.getTagData()
			}
			log.info("rendering as collection: ${retObj}")
			mav.view = "collectionInfo"
			mav.model = [collection:retObj,comment:comment,tags:freeTags,  isDataObject:isDataObject, showLite:collectionAndDataObjectListAndSearchAO.getIRODSServerProperties().isTheIrodsServerAtLeastAtTheGivenReleaseVersion("rods3.0")]
			return mav

		}
	}

	/**
	 * Show information about the current user/host
	 */
	def showLoginBar = {
		log.info("showLoginBar()")
		ResourceAO resourceAO = irodsAccessObjectFactory.getResourceAO(irodsAccount)
		List<String> resources = new ArrayList<String>()
		resources.add("")
		resources.addAll(resourceAO.listResourceAndResourceGroupNames())
		render(view:"loginInfo", model:[irodsAccount:irodsAccount, resources:resources])
	}

	/**
	 * Set the default storage resource in the IRODSAccount holding the login info
	 */
	def setDefautlResourceForAccount = {
		log.info("setDefautlResourceForAccount")
		def resource = params['resource']
		if (resource == null || resource == "") {
			log.error "no file name in request"
			def message = message(code:"error.no.resource")
			response.sendError(500,message)
		}
		irodsAccount.setDefaultStorageResource(resource)
		render "OK"
	}
	
	/**
	 * Create the contents of a 'public link' dialog that will either display a url to copy, or create the appropriate ACL alterations
	 * to support such a public link
	 */
	def preparePublicLinkDialog = {
		def absPath = params['absPath']
		if (absPath == null) {
				log.error "no file name in request"
			def message = message(code:"error.no.path.provided")
			response.sendError(500,message)
		}
		
		// see if anonymous already has access
		AnonymousAccessService anonymousAccessService = new AnonymousAccessServiceImpl(irodsAccessObjectFactory, irodsAccount)
		 
		boolean accessSet = anonymousAccessService.isAnonymousAccessSetUp(absPath)
		URI irodsUri = IRODSUriUtils.buildURIForAnAccountWithNoUserInformationIncluded(irodsAccount, absPath)
		String irodsUriPath = irodsUri.toString()
		String accessUrlString = buildAnonymousAccessUrl(irodsUriPath)
		render(view:"publicLinkDialog", model:[absPath:absPath, accessSet:accessSet, accessUrlString:accessUrlString])

	}	
	
	def updatePublicLinkDialog = {
		def absPath = params['absPath']
		if (absPath == null) {
				log.error "no file name in request"
			def message = message(code:"error.no.path.provided")
			response.sendError(500,message)
		}
		
		// see if anonymous already has access
		AnonymousAccessService anonymousAccessService = new AnonymousAccessServiceImpl(irodsAccessObjectFactory, irodsAccount)
		try {
			log.info("adding anonymous access...")
			anonymousAccessService.permitAnonymousToFileOrCollectionSettingCollectionAndDataObjectProperties(absPath, FilePermissionEnum.READ, null)
			log.info("add successful, link generated")
			} catch (JargonException je) {
			log.error("unable to update anonymous access", je)
			def message = message(code:"error.unable.to.add.anonymous.access")
			response.sendError(500,message)
			return
		}
		
		URI irodsUri = IRODSUriUtils.buildURIForAnAccountWithNoUserInformationIncluded(irodsAccount, absPath)
		String irodsUriPath = irodsUri.toString()
		String accessUrlString = buildAnonymousAccessUrl(irodsUriPath)
		//boolean accessSet = anonymousAccessService.isAnonymousAccessSetUp(absPath)
		boolean accessSet = true
		render(view:"publicLinkDialog", model:[absPath:absPath, accessSet:accessSet, accessUrlString:accessUrlString])
	}
	
	/**
	 * Build a url that will set up anonymous access to the given file
	 * @return
	 */
	private String buildAnonymousAccessUrl(String irodsUriString) {
		TicketDistributionContext ticketDistributionContext = new TicketDistributionContext()
		String grailsServerURL =  grailsApplication.config.grails.serverURL
		log.info("server URL for context: ${grailsServerURL}")
		grailsServerURL = grailsServerURL  + "/home/link?irodsURI=" + URLEncoder.encode(irodsUriString, grailsApplication.config.grails.views.gsp.encoding)
		return grailsServerURL	
	}
	
	
}

class ViewNameAndModelValues {
	String view
	Map<String,Object> model = new HashMap<String, Object>()
}
