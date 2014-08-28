package org.irods.mydrop.controller


import grails.converters.*

import org.irods.jargon.core.connection.IRODSAccount
import org.irods.jargon.core.exception.CatNoAccessException
import org.irods.jargon.core.exception.DataNotFoundException
import org.irods.jargon.core.exception.JargonException
import org.irods.jargon.core.exception.NoResourceDefinedException
import org.irods.jargon.core.pub.CollectionAndDataObjectListAndSearchAO
import org.irods.jargon.core.pub.DataTransferOperations
import org.irods.jargon.core.pub.IRODSAccessObjectFactory
import org.irods.jargon.core.pub.Stream2StreamAO
import org.irods.jargon.core.pub.domain.DataObject
import org.irods.jargon.core.pub.io.IRODSFile
import org.irods.jargon.core.pub.io.IRODSFileFactory
import org.irods.jargon.core.pub.io.IRODSFileInputStream
import org.irods.jargon.datautils.uploads.UploadsService
import org.irods.jargon.datautils.uploads.UploadsServiceImpl
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.multipart.MultipartHttpServletRequest


class FileController {

	IRODSAccessObjectFactory irodsAccessObjectFactory
	IRODSAccount irodsAccount
	static long MAX_UPLOAD = 3221225472  // shooting for 3GB max, make parm?

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
	 * This is the download action
	 */
	def index = {

		log.info("index action")
		String parseString = "/file/download"
		def parseStringLength = parseString.length()
		String fullPath = params.path
		if (fullPath == null || fullPath.isEmpty()) {
			log.error("null or missing path info")
			throw new JargonException("null or missing path info")
		}
		fullPath = URLDecoder.decode(fullPath, "UTF-8");
		//fullPath = StringEscapeUtils.unescapeHtml(fullPath);
		log.info("iRODS path for file is: ${fullPath}")

		try {
			IRODSFileFactory irodsFileFactory = irodsAccessObjectFactory.getIRODSFileFactory(irodsAccount)
			IRODSFileInputStream irodsFileInputStream = irodsFileFactory.instanceIRODSFileInputStream(fullPath)
			IRODSFile irodsFile = irodsFileFactory.instanceIRODSFile(fullPath)
			def length =  irodsFile.length()
			log.info("file length = ${length}")
			log.info("opened input stream")

			response.setContentType("application/octet-stream")
			response.setContentLength((int) length)
			response.setHeader("Content-disposition", "attachment;filename=\"${irodsFile.name}\"")

			//response.outputStream << irodsFileInputStream // Performing a binary stream copy

			Stream2StreamAO stream2Stream = irodsAccessObjectFactory.getStream2StreamAO(irodsAccount)
			def stats = stream2Stream
					.streamToStreamCopyUsingStandardIO(irodsFileInputStream, new BufferedOutputStream(response.outputStream, 32768))
			log.info("transferStats:${stats}")

		} catch (CatNoAccessException e) {
			log.error("no access error", e)
			response.sendError(500, message(code:"message.no.access"))
		}

	}

	/**
	 * Prepare a dialog to upload a file into the given collection
	 * 
	 */
	def prepareUploadDialog = {
		log.info("prepareUploadDialog")
		String irodsTargetCollection = params.irodsTargetCollection
		if (irodsTargetCollection == null || irodsTargetCollection.isEmpty()) {
			log.error("null or missing irodsTargetCollection")
		}

		log.info("looking up parent as: ${irodsTargetCollection}")

		/* here we could do any processing on irods, such as provisioning of metadata fields based on target
		 * for now, derive info about the target and normalize to a collection (could be a data object)
		 */

		CollectionAndDataObjectListAndSearchAO collectionAndDataObjectListAndSearchAO = irodsAccessObjectFactory.getCollectionAndDataObjectListAndSearchAO(irodsAccount)

		def targetObject = null
		try {
			targetObject = collectionAndDataObjectListAndSearchAO.getFullObjectForType(irodsTargetCollection)
		} catch (DataNotFoundException dnf) {
			log.error("no iRODS data found for: ${irodsTargetCollection}")
			throw new JargonException("No data found for ${irodsTargetCollection}")
		}

		if (targetObject instanceof DataObject) {
			log.info("substituting data object parent as path")
			DataObject dataObject = (DataObject) targetObject
			irodsTargetCollection = dataObject.collectionName
		}

		render(view:"uploadDialog", model:[irodsTargetCollection:irodsTargetCollection])
	}

	/**
	 * Prepare a quick upload dialog to upload a file to the default location using the quick upload service
	 *
	 */
	def prepareQuickUploadDialog = {
		log.info("prepareQuickUploadDialog")

		log.info("checking if uploads default directory needs to be created")

		/* here we could do any processing on irods, such as provisioning of metadata fields based on target
		 * for now, derive info about the target and normalize to a collection (could be a data object)
		 */

		UploadsService uploadsService = new UploadsServiceImpl(irodsAccessObjectFactory, irodsAccount)
		IRODSFile uploadsDir = uploadsService.getUploadsDirectory();

		render(view:"quickUploadDialog", model:[irodsTargetCollection:uploadsDir.absolutePath])
	}

	/**
	 * Process an actual call to upload data to iRODS as a multi-part file
	 */
	def upload = {
		log.info("upload action in file controller")

		MultipartFile uploadedFile = null
		String name = ""
		String originalFileName = ""

		if (request instanceof MultipartHttpServletRequest) {
			//Get the file's name from request
			name = request.getFileNames()[0]

			log.info("name from request:${name}")
			//Get a reference to the uploaded file.
			uploadedFile = request.getFile(name)
			originalFileName = uploadedFile.originalFilename
			log.info("original filename:${originalFileName}")

		}

		//get uploaded file's inputStream
		InputStream inputStream = uploadedFile.inputStream
		//get the file storage location

		InputStream fis = new BufferedInputStream(inputStream)

		log.info("name is : ${name}")
		def irodsCollectionPath = params.collectionParentName

		if (irodsCollectionPath == null || irodsCollectionPath.empty) {
			log.error("no target iRODS collection given in upload request")
			throw new JargonException("No iRODS target collection given for upload")
		}

		try {
			IRODSFileFactory irodsFileFactory = irodsAccessObjectFactory.getIRODSFileFactory(irodsAccount)
			IRODSFile targetFile = irodsFileFactory.instanceIRODSFile(irodsCollectionPath, originalFileName)
			targetFile.setResource(irodsAccount.defaultStorageResource)

			//	OutputStream outputStream = irodsAccessObjectFactory.getIRODSFileFactory(irodsAccount).instanceIRODSFileOutputStream(targetFile);
			Stream2StreamAO stream2Stream = irodsAccessObjectFactory.getStream2StreamAO(irodsAccount)
			def transferStats = stream2Stream.transferStreamToFileUsingIOStreams(fis, targetFile, 0, 1 * 1024 * 1024)

			//stream2Stream.streamToStreamCopy(fis,outputStream)
			log.info("transferStats:${transferStats}")
		} catch (NoResourceDefinedException nrd) {
			log.error("no resource defined exception", nrd)
			response.sendError(500, message(code:"message.no.resource"))
		} catch (CatNoAccessException e) {
			log.error("no access error", e)
			response.sendError(500, message(code:"message.no.access"))
		} catch (Exception e) {
			log.error("exception in upload transfer", e)
			response.sendError(500, message(code:"message.error.in.upload"))
		} finally {
			// stream2Stream will close input and output streams
		}

		render "{\"name\":\"${name}\",\"type\":\"image/jpeg\",\"size\":\"1000\"}"

	}

	/**
	 * Delete the given file or folder
	 */
	def deleteFileOrFolder = {
		log.info("delete file or folder")
		String absPath = params['absPath']
		if (!absPath) {
			log.error "no absPath in request"
			def message = message(code:"error.no.path.provided")
			response.sendError(500,message)
		}

		absPath = absPath.trim()

		log.info("name for delete folder:${absPath}")
		IRODSFileFactory irodsFileFactory = irodsAccessObjectFactory.getIRODSFileFactory(irodsAccount)
		IRODSFile targetFile = irodsFileFactory.instanceIRODSFile(absPath)

		try {
			targetFile.deleteWithForceOption()
			log.info("file deleted")
			render(view:"deleteResult", model:[absPath:targetFile.parent])
		} catch (CatNoAccessException e) {
			log.error("no access error", e)
			response.sendError(500, message(code:"message.no.access"))
		} catch (JargonException je) {
			log.error("exception on delete", je)
			response.sendError(500,je.message)
			return
		}
	}

	/**
	 * Process a bulk delete action based on data input from the browse details form
	 */
	def deleteBulkAction = {
		log.info("deleteBulkAction")

		log.info("params: ${params}")

		def filesToDelete = params['selectDetail']

		// if nothing selected, just jump out and return a message
		if (!filesToDelete) {
			log.info("no files to delete")
			render "OK"
			return
		}

		log.info("filesToDelete: ${filesToDelete}")

		IRODSFileFactory irodsFileFactory = irodsAccessObjectFactory.getIRODSFileFactory(irodsAccount)

		if (filesToDelete instanceof Object[]) {
			log.debug "is array"
			filesToDelete.each{
				log.info "filesToDelete: ${it}"
				IRODSFile toDelete = irodsFileFactory.instanceIRODSFile(it)
				toDelete.delete()
				log.info("...delete successful")
			}

		} else {
			log.debug "not array"
			log.info "deleting: ${filesToDelete}..."
			IRODSFile toDelete = irodsFileFactory.instanceIRODSFile(filesToDelete)
			toDelete.delete()
			log.info("...delete successful")
		}

		render "OK"
	}


	/**
	 * Add a new iRODS folder
	 */
	def createFolder = {
		log.info("create folder")

		String parent = params['parent']
		if (!parent) {
			log.error "no parent in request"
			def message = message(code:"error.no.path.provided")
			response.sendError(500,message)
		}

		parent = parent.trim()

		String newFolderName = params['name']
		if (!newFolderName) {
			log.error "no name in request"
			def message = message(code:"error.no.path.provided")
			response.sendError(500,message)
		}

		newFolderName = newFolderName.trim()

		try {
			log.info("name for create folder:${newFolderName}")
			IRODSFileFactory irodsFileFactory = irodsAccessObjectFactory.getIRODSFileFactory(irodsAccount)
			IRODSFile targetFile = irodsFileFactory.instanceIRODSFile(parent + "/" + newFolderName)

			if (targetFile.exists()) {
				log.error "no name in request"
				def message = message(code:"error.duplicate.file")
				response.sendError(500,message)
			}

			targetFile.mkdirs()
			log.info("file created:${targetFile.absolutePath}")
			render targetFile.getAbsolutePath()
		} catch (CatNoAccessException e) {
			log.error("no access error", e)
			response.sendError(500, message(code:"message.no.access"))
		}

	}

	/**
	 * Rename an iRODS file or folder
	 */
	def renameFile = {
		log.info("renameFile()")

		String prevAbsPath = params['prevAbsPath']
		if (!prevAbsPath) {
			log.error "no prevAbsPath in request"
			def message = message(code:"error.no.path.provided")
			response.sendError(500,message)
		}

		prevAbsPath = prevAbsPath.trim()

		String newName = params['newName']
		if (!newName) {
			log.error "no newName in request"
			def message = message(code:"error.no.path.provided")
			response.sendError(500,message)
		}

		newName = newName.trim()

		log.info("rename to :${newName}")
		IRODSFileFactory irodsFileFactory = irodsAccessObjectFactory.getIRODSFileFactory(irodsAccount)
		IRODSFile prevFile = irodsFileFactory.instanceIRODSFile(prevAbsPath)

		IRODSFile newFile = irodsFileFactory.instanceIRODSFile(prevFile.getParentFile(), newName)

		// don't rename to self
		if (newFile.absolutePath == prevFile.absolutePath) {
			log.info("ignoring rename to self")
			render prevFile.absolutePath
			return
		}
		try {
			prevFile.renameTo(newFile)

			// return the parent, which will be reloaded
			render newFile.parent
		} catch (CatNoAccessException e) {
			log.error("no access error", e)
			response.sendError(500, message(code:"message.no.access"))
		}

	}

	/**
	 * Move a file in iRODS
	 */
	def moveFile = {
		log.info("move file")
		String sourceAbsPath = params['sourceAbsPath']
		String targetAbsPath = params['targetAbsPath']

		if (!sourceAbsPath) {
			log.error "no source path in request"
			def message = message(code:"error.no.path.provided")
			response.sendError(500,message)
		}

		if (!targetAbsPath) {
			log.error "no target path in request"
			def message = message(code:"error.no.path.provided")
			response.sendError(500,message)
		}

		try {
			DataTransferOperations dataTransferOperations = irodsAccessObjectFactory.getDataTransferOperations(irodsAccount)
			log.info("moving ${sourceAbsPath} to ${targetAbsPath}")
			dataTransferOperations.move(sourceAbsPath, targetAbsPath)
		} catch (CatNoAccessException e) {
			log.error("no access error", e)
			response.sendError(500, message(code:"message.no.access"))
		} catch (NoResourceDefinedException nrd) {
			log.error "no default resource found for move operation"
			def message = message(code:"message.no.resource")
			response.sendError(500,message)
		}

		render targetAbsPath
	}

	/**
	 * Do a check for access rights to a file at a given path, if it exists, is a data object, and the user has read access, return "OK", otherwise
	 * throw an appropriate exception
	 */
	def screenForDownloadRights = {
		log.info("screenForDownloadRights")

		String sourceAbsPath = params['absPath']
		if (!sourceAbsPath) {
			log.error "no source path in request"
			def message = message(code:"error.no.path.provided")
			response.sendError(500,message)
		}

		log.info("getting file for path:${sourceAbsPath}")
		IRODSFile irodsFile = irodsAccessObjectFactory.getIRODSFileFactory(irodsAccount).instanceIRODSFile(sourceAbsPath)
		if (!irodsFile.exists()) {
			log.error "file does not exist"
			def message = message(code:"error.file.not.found")
			response.sendError(500,message)
		}

		if (!irodsFile.isFile()) {
			log.error "not a file"
			def message = message(code:"error.file.not.found")
			response.sendError(500,message)
		}

		if (!irodsFile.canRead()) {
			log.error "no access to file"
			def message = message(code:"error.no.access.permission")
			response.sendError(500,message)
		}

		render "OK"

	}

	/** 
	 * Copy a file in iRODS
	 */
	def copyFile = {
		log.info("copy file")
		String sourceAbsPath = params['sourceAbsPath']
		String targetAbsPath = params['targetAbsPath']

		if (!sourceAbsPath) {
			log.error "no source path in request"
			def message = message(code:"error.no.path.provided")
			response.sendError(500,message)
		}

		if (!targetAbsPath) {
			log.error "no target path in request"
			def message = message(code:"error.no.path.provided")
			response.sendError(500,message)
		}

		String defaultResource = irodsAccount.defaultStorageResource
		log.info("defaultResource:${defaultResource}")

		try {
			DataTransferOperations dataTransferOperations = irodsAccessObjectFactory.getDataTransferOperations(irodsAccount)
			log.info("copy ${sourceAbsPath} to ${targetAbsPath}")
			dataTransferOperations.copy(sourceAbsPath,defaultResource, targetAbsPath,null, null)
		} catch (NoResourceDefinedException nrd) {
			log.error "no default resource found for copy operation"
			def message = message(code:"message.no.resource")
			response.sendError(500,message)
		}  catch (CatNoAccessException e) {
			log.error("no access error", e)
			response.sendError(500, message(code:"message.no.access"))
		}


		render targetAbsPath

	}
}
