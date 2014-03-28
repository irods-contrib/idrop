
import javax.servlet.http.HttpServletResponse

import org.irods.jargon.core.connection.*
import org.irods.jargon.idrop.web.services.AuthenticationService
import org.irods.jargon.idrop.web.utils.IdropConstants

class AuthenticationFilters {
 
	/**
	 * Injected authentication service
	 */
	AuthenticationService authenticationService

	def filters = {
		all(controller:'*', action:'*', controllerExclude:"(login|error)") {
			before = {
			
				log.info("filter for auth")
				
				if(!session[IdropConstants.AUTH_SESSION]) {
					log.info("not authorized")
					response.sendError HttpServletResponse.SC_UNAUTHORIZED
					return false
				}
				IRODSAccount irodsAccount = session.authenticationSession.authenticatedIRODSAccount
				request.irodsAccount = irodsAccount
				return true
			}
			after = { Map model ->
			}
			afterView = { Exception e ->
			}
		}
	}
}

