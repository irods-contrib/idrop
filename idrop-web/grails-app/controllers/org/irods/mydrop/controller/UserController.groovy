package org.irods.mydrop.controller

import org.irods.jargon.core.connection.IRODSAccount
import org.irods.jargon.core.exception.JargonRuntimeException
import org.irods.jargon.core.pub.IRODSAccessObjectFactory
import org.irods.jargon.core.pub.UserAO
import org.irods.jargon.core.pub.UserGroupAO
import org.irods.jargon.core.pub.domain.User
import org.irods.jargon.core.query.RodsGenQueryEnum
import org.springframework.security.core.context.SecurityContextHolder

class UserController {

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
	 * initial view of user tab
	 */
	def index = {  render(view: "index") }

	/**
	 * search for a list of users who have a name 'like%' a given parameter
	 */
	def userSearchByNameLike = {
		log.info("userSearchByNameLike()")

		String userSearchTerm = params['userSearchTerm']
		if (userSearchTerm == null) {
			log.error "no userSearchTerm in request"
			def message = message(code:"error.no.user.name.provided")
			response.sendError(500,message)
		}

		UserAO userAO = irodsAccessObjectFactory.getUserAO(irodsAccount)
		String whereClause = RodsGenQueryEnum.COL_USER_NAME.name + " LIKE '" + userSearchTerm.trim() + "%'"
		List<User> users = userAO.findWhere(whereClause)
		log.info("user list: ${users}")
		render(view:"userList", model:[users:users])
	}

	/**
	 * search for a list of users who have a name 'like%' a given parameter
	 */
	def userSearchByGroup = {
		log.info("userSearchByGroup()")

		String userSearchTerm = params['userGroup']
		if (userSearchTerm == null) {
			log.error "no userSearchTerm in request"
			def message = message(code:"error.no.user.name.provided")
			response.sendError(500,message)
		}

		UserGroupAO userGroupAO = irodsAccessObjectFactory.getUserGroupAO(irodsAccount)

		List<User> users = userGroupAO.listUserGroupMembers(userSearchTerm)
		log.info("user list: ${users}")
		render(view:"userList", model:[users:users])
	}
}
