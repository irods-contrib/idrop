package org.irods.mydrop.controller

import org.irods.jargon.core.connection.IRODSAccount
import org.irods.jargon.core.pub.IRODSAccessObjectFactory
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
				ProfileCommand profileCommand = new ProfileCommand()
				profileCommand.userName = Jsoup.clean(userProfile.userName,Whitelist.basic())
				profileCommand.nickName = Jsoup.clean(userProfile.userProfilePublicFields.nickName,Whitelist.basic())
				profileCommand.givenName = Jsoup.clean(userProfile.userProfilePublicFields.givenName,Whitelist.basic())
				profileCommand.lastName = Jsoup.clean(userProfile.userProfilePublicFields.sn,Whitelist.basic())
				profileCommand.city= Jsoup.clean(userProfile.userProfilePublicFields.localityName,Whitelist.basic())
				profileCommand.state= Jsoup.clean(userProfile.userProfilePublicFields.st,Whitelist.basic())
				profileCommand.email = Jsoup.clean(userProfile.userProfileProtectedFields.mail,Whitelist.basic())
				profileCommand.description = Jsoup.clean(userProfile.userProfilePublicFields.description,Whitelist.basic())
				profileCommand.labeledURL = Jsoup.clean(userProfile.userProfilePublicFields.labeledURL,Whitelist.basic())
				profileCommand.postalAddress = Jsoup.clean(userProfile.userProfilePublicFields.postalAddress,Whitelist.basic())
				profileCommand.postalCode = Jsoup.clean(userProfile.userProfilePublicFields.postalCode,Whitelist.basic())
				profileCommand.postOfficeBox = Jsoup.clean(userProfile.userProfilePublicFields.postOfficeBox,Whitelist.basic())
				profileCommand.telephoneNumber = Jsoup.clean(userProfile.userProfilePublicFields.telephoneNumber,Whitelist.basic())
				profileCommand.title = Jsoup.clean(userProfile.userProfilePublicFields.title,Whitelist.basic())

				render(view:"index", model:[userProfile:profileCommand])
			} catch (Exception e) {
				log.error("eception retrieving or creating user profile", e)
				response.sendError(500,e.message)
			}
		}
	}


	/**
	 * Update the profile
	 * @return
	 */
	def updateProfile(ProfileCommand profileCommand) {

		log.info("updateProfile")
		log.info "profileCommand: ${profileCommand}"

		/**
		 * If there is an error send back the view for redisplay with error messages
		 */
		if (!profileCommand.validate()) {
			log.info("errors in page, returning with error info")
			flash.error =  message(code:"error.data.error")
			render(view:"index", model:[userProfile:profileCommand])
			return
		}

		log.info("edits pass")


		/*
		 * Massage the params into the user profile
		 */


		UserProfile userProfile
		try {
			userProfile = profileService.retrieveProfile(irodsAccount)
		} catch (Exception e) {
			log.error("error retrieving user profile", e)
			flash.error =  e.message
			render(view:"index", model:[userProfile:profileCommand])
			return
		}

		userProfile.userName = irodsAccount.userName
		userProfile.userProfilePublicFields.nickName = Jsoup.clean(profileCommand.nickName,Whitelist.basic())
		userProfile.userProfilePublicFields.description = Jsoup.clean(profileCommand.description,Whitelist.basic())
		userProfile.userProfileProtectedFields.mail = Jsoup.clean(profileCommand.email,Whitelist.basic())
		userProfile.userProfilePublicFields.givenName = Jsoup.clean(profileCommand.givenName,Whitelist.basic())
		userProfile.userProfilePublicFields.sn = Jsoup.clean(profileCommand.lastName,Whitelist.basic())
		userProfile.userProfilePublicFields.localityName = Jsoup.clean(profileCommand.city,Whitelist.basic())
		userProfile.userProfilePublicFields.st = Jsoup.clean(profileCommand.state,Whitelist.basic())
		userProfile.userProfilePublicFields.description = Jsoup.clean(profileCommand.description,Whitelist.basic())
		userProfile.userProfilePublicFields.labeledURL = Jsoup.clean(profileCommand.labeledURL,Whitelist.basic())
		userProfile.userProfilePublicFields.postalAddress = Jsoup.clean(profileCommand.postalAddress,Whitelist.basic())
		userProfile.userProfilePublicFields.postalCode = Jsoup.clean(profileCommand.postalCode,Whitelist.basic())
		userProfile.userProfilePublicFields.postOfficeBox = Jsoup.clean(profileCommand.postOfficeBox,Whitelist.basic())
		userProfile.userProfilePublicFields.telephoneNumber = Jsoup.clean(profileCommand.telephoneNumber,Whitelist.basic())
		userProfile.userProfilePublicFields.title = Jsoup.clean(profileCommand.title,Whitelist.basic())

		log.info "updating profile...."
		try {
			profileService.updateProfile(irodsAccount, userProfile)
		} catch (Exception e) {
			log.error("error updating user profile", e)
			flash.error =  e.message
			render(view:"index", model:[userProfile:profileCommand])
			return
		}

		log.info "updated"
		flash.message =  message(code:"message.update.successful")

		redirect(view:"index", model:[userProfile:profileCommand])
	}
}

class ProfileCommand {
	String userName
	String nickName
	String givenName
	String lastName
	String city
	String state
	String email
	String description
	String labeledURL
	String postalAddress
	String postalCode
	String postOfficeBox
	String telephoneNumber
	String title

	static constraints = {
		nickName(null:false)
		givenName( null:false)
		lastName(null:false)
		city(null:false)
		state(null:false)
		email(null:false)
		description(null:false)
		labeledURL(null:false)
		postalAddress(null:false)
		postalCode(null:false)
		postOfficeBox(null:false)
		telephoneNumber(null:false)
		title(null:false)
	}
}

