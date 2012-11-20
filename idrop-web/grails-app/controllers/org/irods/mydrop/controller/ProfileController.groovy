package org.irods.mydrop.controller

import org.irods.jargon.core.connection.IRODSAccount
import org.irods.jargon.core.pub.IRODSAccessObjectFactory
import org.irods.jargon.core.pub.UserAO
import org.irods.jargon.userprofile.UserProfile
import org.irods.mydrop.service.ProfileService
import org.jsoup.Jsoup
import org.jsoup.safety.Whitelist

class ProfileController {
	IRODSAccessObjectFactory irodsAccessObjectFactory
	IRODSAccount irodsAccount
	ProfileService profileService
	def grailsApplication

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
	 * Initial listing of profile data, this will create a profile if none exists
	 * @return
	 */
	def index() {
		log.info "index()"
		render(view:"index")	
	}
	
	def loadProfileData() {
		if (irodsAccount.userName == IRODSAccount.PUBLIC_USERNAME) {
			render(view:"noProfileData")
		} else {
		
			try {
				UserProfile userProfile = profileService.retrieveProfile(irodsAccount)
				render(view:"profileData", model:[userProfile:userProfile])
			} catch (Exception e) {
				response.sendError(500,e.message)
			}
		}
		
	}

	/**
	 * Update the profile
	 * @return
	 */
	def updateProfile() {
		
		log.info("updateProfile")

		/*
		 * Massage the params into the user profile
		 */
		
		UserProfile userProfile = profileService.retrieveProfile(irodsAccount)
		
		if (params['nickName'] == null) {
			def message = message(code:"default.null.message", args: ['nickName', 'parms'])
			response.sendError(500,message)
		}
		
		def nickName = Jsoup.clean(params['nickName'], Whitelist.basic())
	
		if ( params['description'] == null) {
			def message = message(code:"default.null.message", args: ['description', 'parms'])
			response.sendError(500,message)
		}
		def description = Jsoup.clean(params['description'], Whitelist.basic())
		
		if (params['email'] == null) {
			def message = message(code:"default.null.message", args: ['email', 'parms'])
			response.sendError(500,message)
		}
		def email = Jsoup.clean(params['email'], Whitelist.basic())
		
		userProfile.userProfilePublicFields.nickName = nickName
		userProfile.userProfilePublicFields.description = description
		userProfile.userProfileProtectedFields.mail = email
		
		log.info "updating profile...."
		profileService.updateProfile(irodsAccount, userProfile)
		log.info "updated"
		
		render(view:"profileData", model:[userProfile:userProfile])
		
	}

	
}

