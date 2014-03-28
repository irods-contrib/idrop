

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
				log.info("closing conn in filter!")
				
				irodsAccessObjectFactory.closeSessionAndEatExceptions()
				return true
			}
			afterView = { Exception e ->
			}
		}
	}
}
