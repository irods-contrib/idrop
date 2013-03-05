package org.irods.mydrop.taglib

import org.irods.jargon.core.connection.IRODSAccount

class AuthTagLib {
	
	private boolean isAuthenticated() {
		//auth = session.SPRING_SECURITY_CONTEXT?.authentication?.authenticated
		def auth = session["SPRING_SECURITY_CONTEXT"]
		//log.info("auth is: ${auth}")
		return auth != null
	 }
	
	private boolean isGuestAccount() {
		//auth = session.SPRING_SECURITY_CONTEXT?.authentication?.authenticated
		IRODSAccount auth = (IRODSAccount) session["SPRING_SECURITY_CONTEXT"]
		if (auth == null) {
			return false
		}
		//log.info("auth is: ${auth}")
		return auth.anonymousAccount
	 }
	
	
	/**
	 * Tag will return true if this is not a guest account, handy for preventing the display
	 * of things that only a real user should be able to get to, such as profile stuff
	 */
	def ifNotGuestAccount = { attrs, body ->
		
		if (!isGuestAccount()) {
			out << body()
		}
		
	}
	
	
	
	def ifAuthenticated = { attrs, body ->
		if (isAuthenticated()) {
		   out << body()
		}
	 }
	 
	 def ifNotAuthenticated = { attrs, body ->
		if (!isAuthenticated()) {
		   out << body()
		}
	 }
	 
	 def isRodsAdmin = { attrs, body ->
		 // add extra check if auth
		 def auth = session["SPRING_SECURITY_CONTEXT"]
		/* List<GrantedAuthorities> grantedAuthorities = auth.grantedAuthorities
		 
		 boolean isRodsAdmin = false;
		 grantedAuthories.each() {it ->
			 if (it == "rodsadmin") {
				out << body()
				 break
			 }
		 }*/
	}

}
