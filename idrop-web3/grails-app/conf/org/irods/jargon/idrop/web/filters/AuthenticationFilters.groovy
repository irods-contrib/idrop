package org.irods.jargon.idrop.web.filters

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
				if(!session[IdropConstants.AUTH_SESSION]) {
					response.sendError HttpServletResponse.SC_UNAUTHORIZED
					return
				}
				IRODSAccount irodsAccount = session.authenticationSession.authenticatedIRODSAccount
				request.irodsAccount = irodsAccount
			}
			after = { Map model ->
			}
			afterView = { Exception e ->
			}
		}
	}
}

