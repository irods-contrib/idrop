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
import org.irods.jargon.ruleservice.composition.Rule
import org.irods.jargon.ticket.TicketDistributionContext
import org.irods.jargon.usertagging.domain.IRODSStarredFileOrCollection
import org.irods.jargon.usertagging.tags.FreeTaggingService
import org.irods.jargon.usertagging.tags.IRODSTaggingService
import org.irods.jargon.usertagging.tags.TaggingServiceFactory
import org.irods.mydrop.config.ViewState
import org.irods.mydrop.controller.utils.ViewNameAndModelValues
import org.irods.mydrop.service.RuleProcessingService
import org.irods.mydrop.service.StarringService
import org.irods.mydrop.service.ViewStateService

/**
 * Controller for browser functionality
 * @author Mike Conway - DICE (www.irods.org)
 */

class BrowseController {

	IRODSAccessObjectFactory irodsAccessObjectFactory
	TaggingServiceFactory taggingServiceFactory
	StarringService starringService
	ViewStateService viewStateService
	RuleProcessingService ruleProcessingService
	IRODSAccount irodsAccount

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

		ViewState viewState = viewStateService.getViewStateFromSessionAndCreateIfNotThere()
		log.info("viewState:${viewState}")

		if (mode == null && absPath == null) {
			log.info("coming in with no params for mode or path, check view state and use the mode and path there (if they exist)")
			absPath = viewState.rootPath
			if (absPath) {
				log.info("i have a previous abspath in the view state, so use that path and set mode to 'path' too: ${absPath}")
				mode = "path"
			}
		}

		if (mode == "path") {
			log.info("mode is path, should have an abspath to preset to")
			if (absPath == null) {
				def message = message(code:"error.no.path.provided")
				response.sendError(500,message)
				return
			} else {
				log.info("path is ${absPath}")
				viewState = viewStateService.saveRootPath(absPath)

				/*
				 * Decide what to do about the selected path, such that a path we set as root might need to wipe out the previous selected path.
				 *
				 * Keep the selected path if the new root is shorter than the selected path and it contains the path
				 */

				if (viewState.selectedPath.indexOf(viewState.rootPath) == -1) {
					log.info("getting rid of selected path, not under new root path")
					viewState = viewStateService.saveSelectedPath("")
				}
			}
		}


		render(view: "index", model:[mode:mode,path:absPath,viewState:viewState,irodsAccount:irodsAccount])
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

			/*
			 * Detect modes means I am being asked to decide what to show, based on things like whether
			 * strict acl's are enforced.
			 *
			 * If I have a preserved view state, initialize to that
			 */

			log.info("path type is detect")

			ViewState viewState = viewStateService.getViewStateFromSessionAndCreateIfNotThere()

			if (viewState.rootPath) {

				parent = viewState.rootPath

				icon = "folder"
				state = "closed"
				type = "folder"

				def attrBuf = ["id":parent, "rel":type, "absPath":parent]

				jsonBuff.add(
						["data": parent,"attr":attrBuf, "state":state,"icon":icon, "type":type]
						)

			} else {


				log.info("no parent parm set, detect display as either root or home")

				if (irodsAccount.userName ==  "anonymous") {
					log.info("user is anonymous, default to view the public directory")

					parent = "/" + irodsAccount.zone + "/home/public"

				} else {

					def isStrict;
					try {
						isStrict = environmentalInfoAO.isStrictACLs()
					} catch (JargonException je) {
						log.warn("error getting rule info for strict acl's currently overheaded see idrop bug [#1219] error on intiial display centos6")
						isStrict = false
					}

					log.info "is strict?:{isStrict}"
					if (isStrict) {
						parent = "/" + irodsAccount.zone + "/home/" + irodsAccount.userName
					} else {
						parent = "/"
					}
				}

				viewStateService.saveRootPath(parent)

				icon = "folder"
				state = "closed"
				type = "folder"

				def attrBuf = ["id":parent, "rel":type, "absPath":parent]

				jsonBuff.add(
						["data": parent,"attr":attrBuf, "state":state,"icon":icon, "type":type]
						)
			}

		} else if (pathType == "root") {

			log.info("display the root node")
			// display a root node
			parent = "/"

			icon = "folder"
			state = "closed"
			type = "folder"

			viewStateService.saveRootPath(parent)

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

			// display home node

			icon = "folder"
			state = "closed"
			type = "folder"

			viewStateService.saveRootPath(parent)

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

			viewStateService.saveRootPath(parent)

			def attrBuf = ["id":parent, "rel":type, "absPath":parent]

			jsonBuff.add(
					["data": parent,"attr":attrBuf, "state":state,"icon":icon, "type":type]
					)
		} else if (pathType == "list") {

			log.info("parent dir for listing provided as:${parent}")

			def pagingOffset = params['partialStart']
			def splitMode = params['splitMode']

			if (pagingOffset == null) {
				pagingOffset = 0;
			}

			if (splitMode == null) {
				throw new JargonException("missing the splitMode")
			}

			def collectionAndDataObjectListAndSearchAO = irodsAccessObjectFactory.getCollectionAndDataObjectListAndSearchAO(irodsAccount)

			try {

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
			} catch (ZoneUnavailableException e) {
				log.error("zone unavailable exception", e)
				response.sendError(500, message(code:"message.zone.unavailable"))
			} catch (JargonException e) {
				log.error("jargon exception", e)
				response.sendError(500, e.message)
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

		ViewState viewState = viewStateService.saveViewModeAndSelectedPath("browse", absPath)

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
			int pageSize = irodsAccessObjectFactory.jargonProperties.maxFilesAndDirsQueryMax
			//PagingActions pagingActions = PagingAnalyser.buildPagingActionsFromListOfIRODSDomainObjects(entries, pageSize)
			//log.debug("retrieved collectionAndDataObjectList: ${entries}")
			//log.debug("pagingActions:${pagingActions}")

			render(view:"browseDetails", model:[collection:entries, parent:retObj, showLite:collectionAndDataObjectListAndSearchAO.getIRODSServerProperties().isTheIrodsServerAtLeastAtTheGivenReleaseVersion("rods3.0"), viewState:viewState])
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

		viewStateService.saveViewModeAndSelectedPath("info", absPath)

		ViewNameAndModelValues mav = handleInfoLookup(absPath)
		render(view:mav.view, model:mav.model)
		return

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
		String parentPath = targetFile.parent

		log.info("target file obtained")
		if (!targetFile.exists()) {
			log.error "absPath does not exist in iRODS"
			def message = message(code:"error.no.data.found")
			response.sendError(500,message)
		}

		log.info("target file exists")

		String fileName = targetFile.name
		render(view:"renameDialog", model:[fileName:fileName, absPath:absPath, parentPath:parentPath])
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

	/**
	 * Prepare the 'new folder' dialog
	 */
	def prepareStarDialog = {
		log.info("prepareStarDialog()")

		def absPath = params['absPath']
		if (absPath == null) {
			log.error "no absPath in request"
			def message = message(code:"error.no.path.provided")
			response.sendError(500,message)
		}

		log.info("abs path:${absPath}")


		render(view:"starDialog", model:[absPath:absPath])
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

		viewStateService.saveViewModeAndSelectedPath("gallery", absPath)

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

	/**
	 * Set a folder to starred
	 */
	def starFile = {
		log.info("starFile()")
		def absPath = params['absPath']
		if (absPath == null) {
			def message = message(code:"error.no.path.provided")
			response.sendError(500,message)
		}

		def description = params['description']
		if (description == null) {
			def message = message(code:"error.no.description.provided")
			response.sendError(500,message)
		}

		try {
			log.info "starring absPath: ${absPath}"
			starringService.star(irodsAccount, absPath, description)
			log.info("star successful")
			render "OK"
		} catch (org.irods.jargon.core.exception.FileNotFoundException fnf) {
			def message = message(code:"error.file.not.found")
			response.sendError(500,message)
		}
	}

	/**
	 * Set a folder to not starred
	 */
	def unstarFile = {
		log.info("unstarFile()")
		def absPath = params['absPath']
		if (absPath == null) {
			def message = message(code:"error.no.path.provided")
			response.sendError(500,message)
		}

		try {
			log.info "unstarring absPath: ${absPath}"
			starringService.unStar(irodsAccount, absPath)
			log.info("unstar successful")
			render "OK"
		} catch (org.irods.jargon.core.exception.FileNotFoundException fnf) {
			def message = message(code:"error.file.not.found")
			response.sendError(500,message)
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


		IRODSStarredFileOrCollection irodsStarredFileOrCollection;
		log.info("seeing if this is starred")
		try {
			irodsStarredFileOrCollection = starringService.findStarred(irodsAccount, absPath)
		} catch (SpecificQueryException sqe) {
			irodsStarredFileOrCollection = null
		}
		log.info "starring info:${irodsStarredFileOrCollection}"

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


			Rule rule = null
			if (ruleProcessingService.isRule(absPath)) {
				log.info("is a .r file, see if a rule")
				try {

					rule = ruleProcessingService.loadRuleFromIrodsFile(irodsAccount, absPath)
				} catch (JargonException je) {
					log.error("exception attempting to load rule, do not show tab")
				}
			}

			mav.view = "dataObjectInfo"
			mav.model = [dataObject:retObj,tags:freeTags,comment:comment,getThumbnail:getThumbnail,renderMedia:renderMedia,isDataObject:isDataObject,irodsStarredFileOrCollection:irodsStarredFileOrCollection,showLite:collectionAndDataObjectListAndSearchAO.getIRODSServerProperties().isTheIrodsServerAtLeastAtTheGivenReleaseVersion("rods3.0"), rule:rule]
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
			mav.model = [collection:retObj,comment:comment,tags:freeTags,  isDataObject:isDataObject, irodsStarredFileOrCollection:irodsStarredFileOrCollection,showLite:collectionAndDataObjectListAndSearchAO.getIRODSServerProperties().isTheIrodsServerAtLeastAtTheGivenReleaseVersion("rods3.0")]
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

		try {
			resources.addAll(resourceAO.listResourceAndResourceGroupNames())
		} catch (Exception e) {
			log.error("error listing resorces...error is muted",e)
		}
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
		session["SPRING_SECURITY_CONTEXT"] = irodsAccount
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
