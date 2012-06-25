package org.irods.mydrop.controller

import org.irods.jargon.core.connection.*
import org.irods.jargon.core.exception.*
import org.irods.jargon.core.pub.*
import org.irods.jargon.usertagging.FreeTaggingService
import org.irods.jargon.usertagging.TaggingServiceFactory
import org.irods.jargon.usertagging.domain.TagQuerySearchResult
import org.irods.jargon.core.pub.IRODSAccessObjectFactory


class SearchController {

	IRODSAccessObjectFactory irodsAccessObjectFactory
	TaggingServiceFactory taggingServiceFactory
	IRODSAccount irodsAccount

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
	 * Search iRODS files and collections
	 */
	def search = {

		String searchTerm = params['searchTerm']
		String searchType = params['searchType']

		if (searchTerm == null || searchTerm.isEmpty()) {
			throw new JargonException("no searchTerm passed to the method")
		}

		if (searchType == null || searchType.isEmpty()) {
			throw new JargonException("no searchType passed to the method")
		}

		log.info "search for term: ${searchTerm}"

		if (searchType=="file") {
			log.info "search for file name"
			CollectionAndDataObjectListAndSearchAO collectionAndDataObjectListAndSearchAO = irodsAccessObjectFactory.getCollectionAndDataObjectListAndSearchAO(irodsAccount)
			def results = collectionAndDataObjectListAndSearchAO.searchCollectionsAndDataObjectsBasedOnName(searchTerm)
			render(view:"searchResult", model:[results:results])
		} else if (searchType=="tag") {
			log.info "search based on tag"
			FreeTaggingService freeTaggingService = taggingServiceFactory.instanceFreeTaggingService(irodsAccount)
			TagQuerySearchResult searchResult = freeTaggingService.searchUsingFreeTagString(searchTerm)
			render(view:"searchResult", model:[results:searchResult.queryResultEntries])
		}
	}
}
