package org.irods.jargon.idrop.web.filters

import org.irods.jargon.core.pub.IRODSAccessObjectFactory

/**
 * Filter to clean up all connections after processing a method
 * @author mikeconway
 *
 */
class ConnectionClosingFilterFilters {

	IRODSAccessObjectFactory irodsAccessObjectFactory

	def filters = {
		all(controller:'*', action:'*') {
			before = {
			}
			after = { Map model ->

				irodsAccessObjectFactory.closeSessionAndEatExceptions()
			}
			afterView = { Exception e ->
			}
		}
	}
}
