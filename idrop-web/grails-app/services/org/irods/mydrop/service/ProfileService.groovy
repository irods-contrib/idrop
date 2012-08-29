package org.irods.mydrop.service

import org.irods.jargon.core.connection.IRODSAccount
import org.irods.jargon.core.exception.DataNotFoundException
import org.irods.jargon.core.pub.IRODSAccessObjectFactory
import org.irods.jargon.userprofile.*

class ProfileService {
	static transactional = false
	IRODSAccessObjectFactory irodsAccessObjectFactory

	/**
	 * Given an irods account, retrieve the existing user profile, or create a skeleton and return this new skeleton profile
	 * @param irodsAccount
	 * @return
	 */
	UserProfile retrieveProfile(IRODSAccount irodsAccount) {
		log.info "retrieveProfile()"
		if (irodsAccount == null) {
			throw new IllegalArgumentException("null profile")
		}

		UserProfileService userProfileService = new UserProfileServiceImpl(irodsAccessObjectFactory, irodsAccount)
		log.info("attempting to retrieve profile for ${irodsAccount}")

		UserProfile userProfile
		try {
			userProfile = userProfileService.retrieveUserProfile(irodsAccount.userName)
		} catch (DataNotFoundException dnf) {
			log.info("no profile found, go ahead and create a basic one")
			userProfile = addSkeletonUserProfile(irodsAccount, userProfileService)
		}

		log.info("user profile ${userProfile}")
		return userProfile
	}

	private UserProfile addSkeletonUserProfile(IRODSAccount irodsAccount, UserProfileService userProfileService) {
		UserProfile userProfile = new UserProfile()
		userProfile.userName = irodsAccount.userName
		userProfile.zone = irodsAccount.zone
		userProfileService.addProfileForUser(irodsAccount.userName, userProfile)
		return userProfile
	}

	/**
	 * Given the user profile information, update the users profile and then return the new state
	 * @param irodsAccount
	 * @param userProfile
	 * @return
	 */
	UserProfile updateProfile(IRODSAccount irodsAccount, UserProfile userProfile) {
		log.info "updateProfile"

		if (irodsAccount == null) {
			throw new IllegalArgumentException("null profile")
		}

		if (userProfile == null) {
			throw new IllegalArgumentException("null userProfile")
		}

		UserProfileService userProfileService = new UserProfileServiceImpl(irodsAccessObjectFactory, irodsAccount)
		log.info("attempting to update profile for ${irodsAccount}")
		log.info("desired profile information: ${userProfile}")

		userProfileService.updateUserProfile(userProfile)
		log.info "updated...now retrieve and display"
		UserProfile updatedProfile = userProfileService.retrieveUserProfile(irodsAccount.userName)
		log.info("updated profile: ${updatedProfile}")
		return updatedProfile
	}
}
