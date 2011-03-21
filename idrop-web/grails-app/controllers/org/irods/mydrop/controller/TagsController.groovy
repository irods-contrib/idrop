package org.irods.mydrop.controller

import org.irods.jargon.core.connection.IRODSAccount
import org.irods.jargon.core.exception.JargonException
import org.irods.jargon.core.exception.JargonRuntimeException
import org.irods.jargon.core.pub.IRODSAccessObjectFactory
import org.irods.jargon.usertagging.FreeTaggingService
import org.irods.jargon.usertagging.TaggingServiceFactory
import org.irods.jargon.usertagging.UserTagCloudService
import org.irods.jargon.usertagging.domain.UserTagCloudView
import org.jsoup.Jsoup
import org.jsoup.safety.Whitelist
import org.springframework.security.core.context.SecurityContextHolder
import grails.converters.*



class TagsController {

	IRODSAccessObjectFactory irodsAccessObjectFactory
	TaggingServiceFactory taggingServiceFactory
	IRODSAccount irodsAccount

	def allowedMethods = [
		updateTags:['POST']]

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

		if (absPath == null || absPath.isEmpty()) {
			throw new JargonException("no absPath passed to method")
		}

		if (tagString == null) {
			throw new JargonException("null tags passed to method")
		}

		log.info("updating tags for file: ${absPath} for user: ${irodsAccount.userName}")

		FreeTaggingService freeTaggingService = taggingServiceFactory.instanceFreeTaggingService(irodsAccount)
		freeTaggingService.updateTagsForUserForADataObjectOrCollection(absPath, irodsAccount.userName, tagString)
		log.info("tags updated")
		render "success"
	}
}
