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
		UserProfile userProfile
		try {
			 userProfile = profileService.retrieveProfile(irodsAccount)
		} catch (Exception e) {
			log.error("error retrieving user profile", e)
			def message = message("message.cannot.create.profile")
			response.sendError(500, message)
			return
		}
		
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
		try {
			profileService.updateProfile(irodsAccount, userProfile)
		} catch (Exception e) {
			log.error("error updating user profile", e)
			def message = message("message.cannot.create.profile")
			response.sendError(500, message)
			return
		}
		log.info "updated"
		
		render(view:"profileData", model:[userProfile:userProfile])
		
	}

	/**
	 * Show the password change dialog
	 * @return
	 */
	def showPasswordChangeDialog() {
		PasswordCommand cmd = new PasswordCommand()
		render (view:"changePasswordDialog", model:[password:cmd])
	}
	
	/**
	 * process a password change
	 * @return
	 */
	def changePassword(PasswordCommand cmd) {
		log.info "changePassword()"
		log.info "cmd: ${cmd}"

		/**
		 * If there is an error send back the view for redisplay with error messages
		 */
		if (!cmd.validate()) {
			log.info("errors in page, returning with error info:${cmd}")
			flash.error =  message(code:"error.data.error")
			render (view:"changePasswordDialog", model:[password:cmd])
			return
		}

		log.info("edits pass")
		
		UserAO userAO = irodsAccessObjectFactory.getUserAO(irodsAccount)
		userAO.changeAUserPasswordByThatUser(irodsAccount.userName, irodsAccount.password, cmd.password)
		irodsAccount.password = cmd.password
		log.info("password changed, fix account in session")
		flash.message = message(code:"message.update.successful")
		render (view:"changePasswordDialog", model:[password:cmd])

	}
}

class PasswordCommand {

	String password
	String confirmPassword

	static constraints = {
		password(blank:false)
		confirmPassword  validator: {
			val, obj ->
			if (!val) return ['error.confirm.password.missing']
         	if (val != obj.password) return['error.passwords.dont.match']
        }
	}
}
