package org.irods.mydrop.taglib

import org.jsoup.Jsoup
import org.jsoup.safety.Whitelist


/**
 * Tag library for handling images and media
 * @author Mike Conway - DICE (www.irods.org)
 *
 */
class JsoupTagLib {


	/**
	 * Renders an anchor tag suitable for use by the jQuery media plugin to add a viewer for a given media type
	 * @attr absPath REQUIRED the absolute path to the file as a <code>String</code>
	 * @attr renderMedia REQUIRED <code>boolean</code> that is true if media should be rendered.
	 */
	def clean = {attrs, body ->

		//	if (attrs.renderMedia == 'true') {

		out Jsoup.clean(body, Whitelist.basic())
	}
}
