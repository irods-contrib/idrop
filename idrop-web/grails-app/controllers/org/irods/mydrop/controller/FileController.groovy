package org.irods.mydrop.controller


import org.irods.jargon.core.connection.IRODSAccount
import org.irods.jargon.core.exception.DataNotFoundException
import org.irods.jargon.core.exception.JargonException
import org.irods.jargon.core.exception.JargonRuntimeException
import org.irods.jargon.core.pub.CollectionAndDataObjectListAndSearchAO
import org.irods.jargon.core.pub.IRODSAccessObjectFactory
import org.irods.jargon.core.pub.domain.DataObject
import org.irods.jargon.core.pub.io.IRODSFileFactory
import org.irods.jargon.core.pub.io.IRODSFileInputStream
import org.irods.jargon.core.pub.io.IRODSFile
import org.irods.jargon.core.pub.io.IRODSFileOutputStream
import org.springframework.security.core.context.SecurityContextHolder
import org.irods.jargon.core.pub.io.IRODSFileImpl
import grails.converters.*


class FileController {

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
	 * This is the download action
	 */
	def index = {

		log.info("index action")
		String parseString = "/file/download"
		def parseStringLength = parseString.length()
		String fullPath = params.name
		if (fullPath == null || fullPath.isEmpty()) {
			log.error("null or missing path info")
			throw new JargonException("null or missing path info")
		}

		log.info("add path info is ${params.name}")
		def idx = fullPath.indexOf(parseString)
		log.debug("parsing out additional path info")
		if (idx == -1) {
			log.error("unable to parse addl path info for irods absolute path, path info was ${fullPath}")
			throw new JargonException("unable to parse addl path info for irods absolute path")
		}

		fullPath = fullPath.substring(idx + parseStringLength)
		log.info("iRODS path for file is: ${fullPath}")


		IRODSFileFactory irodsFileFactory = irodsAccessObjectFactory.getIRODSFileFactory(irodsAccount)
		IRODSFileInputStream irodsFileInputStream = irodsFileFactory.instanceIRODSFileInputStream(fullPath)
		IRODSFile irodsFile = irodsFileFactory.instanceIRODSFile(fullPath)
		def length =  irodsFile.length()
		log.info("file length = ${length}")
		log.info("opened input stream")

		response.setContentType("application/octet-stream")
		response.setContentLength((int) length)
		response.setHeader("Content-disposition", "attachment;filename=${fullPath}")

		response.outputStream << irodsFileInputStream // Performing a binary stream copy

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
	 * Process an actual call to upload data to iRODS as a multi-part file
	 */
	def upload = {
		log.info("upload action in file controller")
		def f = request.getFile('file')
		def name = f.getOriginalFilename()

		log.info("f is ${f}");

		log.info("name is : ${name}")
		def irodsCollectionPath = params.collectionParentName

		if (f == null || f.empty) {
			log.error("no file to upload")
			throw new JargonException("No file to upload")
		}

		if (irodsCollectionPath == null || irodsCollectionPath.empty) {
			log.error("no target iRODS collection given in upload request")
			throw new JargonException("No iRODS target collection given for upload")
		}

		InputStream fis = f.getInputStream()
		log.info("building irodsFile for file name: ${name}")

		IRODSFileFactory irodsFileFactory = irodsAccessObjectFactory.getIRODSFileFactory(irodsAccount)
		IRODSFile targetFile = irodsFileFactory.instanceIRODSFile(irodsCollectionPath, name)
		IRODSFileOutputStream irodsFileOutputStream = irodsFileFactory.instanceIRODSFileOutputStream(targetFile)
		log.info("initiating transfer to ${targetFile}")
		irodsFileOutputStream << fis
		irodsFileOutputStream.flush()
		irodsFileOutputStream.close()

		render "{\"name\":\"${name}\",\"type\":\"image/jpeg\",\"size\":\"1000\"}"
	}
	
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
		
		targetFile.deleteWithForceOption();
		log.info("file deleted")
		render targetFile.getAbsolutePath()
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

		log.info("name for create folder:${newFolderName}")
		IRODSFileFactory irodsFileFactory = irodsAccessObjectFactory.getIRODSFileFactory(irodsAccount)
		IRODSFile targetFile = irodsFileFactory.instanceIRODSFile(parent + "/" + newFolderName)
		targetFile.mkdirs()
		log.info("file created:${targetFile.absolutePath}")
		render targetFile.getAbsolutePath()
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
	   prevFile.renameTo(newFile)
	   
	   render newFile.getAbsolutePath()
   }
	
	
}
