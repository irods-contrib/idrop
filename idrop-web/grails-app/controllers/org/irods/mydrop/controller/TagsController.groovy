package org.irods.mydrop.controller

import grails.converters.*

import org.irods.jargon.core.connection.IRODSAccount
import org.irods.jargon.core.exception.JargonException
import org.irods.jargon.core.pub.IRODSAccessObjectFactory
import org.irods.jargon.core.pub.io.IRODSFile
import org.irods.jargon.usertagging.domain.IRODSTagValue
import org.irods.jargon.usertagging.domain.UserTagCloudView
import org.irods.jargon.usertagging.tags.FreeTaggingService
import org.irods.jargon.usertagging.tags.IRODSTaggingService
import org.irods.jargon.usertagging.tags.TaggingServiceFactory
import org.irods.jargon.usertagging.tags.UserTagCloudService
import org.jsoup.Jsoup
import org.jsoup.safety.Whitelist



class TagsController {

	IRODSAccessObjectFactory irodsAccessObjectFactory
	TaggingServiceFactory taggingServiceFactory
	IRODSAccount irodsAccount

	def allowedMethods = [
		updateTags:['POST']]

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
		log.debug("closing any open sessions")
		irodsAccessObjectFactory.closeSession()
	}

	def index = { }

	/**
	 * Retrieve a tag cloud for the user
	 */
	def tagCloud = {

		log.info("getting tag cloud for user: ${irodsAccount}")
		UserTagCloudService userTagCloudService = taggingServiceFactory.instanceUserTagCloudService(irodsAccount)
		UserTagCloudView userTagCloudView = userTagCloudService.getTagCloud()
		def entries = userTagCloudView.getTagCloudEntries().values()
		render(view:"tagCloud", model:[tagCloud:entries])
	}

	/**
	 * Retrieve a formattable tag cloud for the user
	 */
	def tagCloudFormatted = {

		log.info("getting tag cloud for user: ${irodsAccount}")
		UserTagCloudService userTagCloudService = taggingServiceFactory.instanceUserTagCloudService(irodsAccount)
		UserTagCloudView userTagCloudView = userTagCloudService.getTagCloud()
		def entries = userTagCloudView.getTagCloudEntries().values()
		def jsonBuff = []

		entries.each {

			jsonBuff.add(
					["text": it.irodsTagValue.tagData,"weight":2 + it.countOfFiles])
		}

		render jsonBuff as JSON
	}

	/**
	 * update the tag for the collection or data object based on a free tag string 
	 */
	def updateTags = {
		String absPath = params['absPath']
		def tagString = Jsoup.clean(params['tags'], Whitelist.basic())

		def comment = ""
		if (params['comment']) {
			comment = Jsoup.clean(params['comment'], Whitelist.basic())
		}

		if (absPath == null || absPath.isEmpty()) {
			throw new JargonException("no absPath passed to method")
		}

		if (tagString == null) {
			throw new JargonException("null tags passed to method")
		}

		if (comment == null) {
			throw new JargonException("null comment passed to method")
		}

		log.info("updating tags for file: ${absPath} for user: ${irodsAccount.userName}")
		IRODSTaggingService irodsTaggingService = taggingServiceFactory.instanceIrodsTaggingService(irodsAccount)

		IRODSFile irodsFile = irodsAccessObjectFactory.getIRODSFileFactory(irodsAccount).instanceIRODSFile(absPath)

		if (irodsFile.isFile()) {
			log.info("saving comments for a file")
			def irodsTagValue = new IRODSTagValue(comment, irodsAccount.userName)
			irodsTaggingService.addDescriptionToDataObject(absPath, irodsTagValue)
			log.info("comment added")
		} else {
			log.info("saving comments for a collecton")
			def irodsTagValue = new IRODSTagValue(comment, irodsAccount.userName)
			irodsTaggingService.addDescriptionToCollection(absPath, irodsTagValue)
			log.info("comment added")
		}

		FreeTaggingService freeTaggingService = taggingServiceFactory.instanceFreeTaggingService(irodsAccount)
		freeTaggingService.updateTagsForUserForADataObjectOrCollection(absPath, irodsAccount.userName, tagString)

		log.info("tags updated")
		render "success"
	}
}
