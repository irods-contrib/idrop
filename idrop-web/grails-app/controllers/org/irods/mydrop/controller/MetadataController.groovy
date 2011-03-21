package org.irods.mydrop.controller


import org.irods.jargon.core.connection.IRODSAccount
import org.irods.jargon.core.exception.JargonException
import org.irods.jargon.core.exception.JargonRuntimeException
import org.irods.jargon.core.pub.CollectionAO
import org.irods.jargon.core.pub.CollectionAndDataObjectListAndSearchAO
import org.irods.jargon.core.pub.DataObjectAO
import org.irods.jargon.core.pub.IRODSAccessObjectFactory
import org.irods.jargon.core.pub.domain.DataObject
import org.mockito.Mockito
import org.springframework.security.core.context.SecurityContextHolder

/**
 * Controller for displaying and updating various metadata for data objects or collections
 * @author Mike Conway - DICE (www.irods.org) 
 */
class MetadataController {

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
	 * For a given absolute path, which is a data object, list the AVU metadata values
	 */
	def listMetadata = {
		def absPath = params['absPath']
		if (absPath == null) {
			throw new JargonException("no absolute path passed to the method")
		}

		log.info("listMetadataForDataObject for absPath: ${absPath}")

		CollectionAndDataObjectListAndSearchAO collectionAndDataObjectListAndSearchAO = irodsAccessObjectFactory.getCollectionAndDataObjectListAndSearchAO(irodsAccount)

		// TODO: some sort of catch and display of no data available in info?
		def retObj = collectionAndDataObjectListAndSearchAO.getFullObjectForType(absPath)

		def isDataObject = retObj instanceof DataObject
		def metadata;
		
		if (isDataObject) {
			log.debug("retrieving meta data for a data object");
			DataObjectAO dataObjectAO = irodsAccessObjectFactory.getDataObjectAO(irodsAccount)
			metadata = dataObjectAO.findMetadataValuesForDataObject(retObj.collectionName, retObj.dataName)
		} else {
			CollectionAO collectionAO = irodsAccessObjectFactory.getCollectionAO(irodsAccount)
			metadata = collectionAO.findMetadataValuesForCollection(retObj.collectionName, 0) // FIXME: switch to no restart sig
		}

		render(view:"metadataDetails", model:[metadata:metadata])

	}
	
	
}
