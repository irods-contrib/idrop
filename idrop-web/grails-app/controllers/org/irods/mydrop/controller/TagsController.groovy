package org.irods.mydrop.controller

import org.irods.jargon.core.connection.IRODSAccount
import org.irods.jargon.core.exception.JargonRuntimeException
import org.irods.jargon.core.pub.IRODSAccessObjectFactory
import org.irods.jargon.usertagging.TaggingServiceFactory
import org.irods.jargon.usertagging.UserTagCloudService
import org.irods.jargon.usertagging.domain.UserTagCloudView
import org.springframework.security.core.context.SecurityContextHolder

class TagsController {
	
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


    def index = { }
	
	def tagCloud = {
		
		log.info("getting tag cloud for user: ${irodsAccount}")
		UserTagCloudService userTagCloudService = taggingServiceFactory.instanceUserTagCloudService(irodsAccount)
		UserTagCloudView userTagCloudView = userTagCloudService.getTagCloud()
		def entries = userTagCloudView.getTagCloudEntries().values()
		render(view:"tagCloud", model:[tagCloud:entries])

	}
	
}
