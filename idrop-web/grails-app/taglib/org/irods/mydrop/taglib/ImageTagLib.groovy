package org.irods.mydrop.taglib

import org.irods.jargon.datautils.image.MediaHandlingUtils

/**
 * Tag library for handling images and media
 * @author Mike Conway - DICE (www.irods.org)
 *
 */
class ImageTagLib {
	/**
	 * Renders the enclosed gallery tag if the given <code>CollectionAndDataObjectListingEntry</code> is an 
	 * image.
	 * @attr entry REQUIRED the {@link CollectionAndDataObjectListingEntry}
	 */
	def galleryEntryIfImage = { attrs, body ->
		if (MediaHandlingUtils.isImageFile(attrs.entry)) {
			out << body()
		}
	}
}
