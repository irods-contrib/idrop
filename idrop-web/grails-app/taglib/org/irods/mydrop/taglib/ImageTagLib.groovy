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

	/**
	 * Renders an anchor tag suitable for use by the jQuery media plugin to add a viewer for a given media type
	 * @attr absPath REQUIRED the absolute path to the file as a <code>String</code>
	 * @attr renderMedia REQUIRED <code>boolean</code> that is true if media should be rendered.
	 */
	def addMediaTag = {attrs, body ->

		//	if (attrs.renderMedia == 'true') {

		out << "${createLink(absolute:true,controller:'file')}"
		//	out << "<a class='media' href='"
		//	out << "${resource(contextPath:'idrop-web',absolute:true,controller:'file',dir:'/file/download',file:attrs.absPath)}"
		//	out << "'>Media File</a>"
		//	}
	}
}
