package org.irods.mydrop.taglib

class AuthTagLib {
	
	private boolean isAuthenticated() {
		//auth = session.SPRING_SECURITY_CONTEXT?.authentication?.authenticated
		def auth = session["SPRING_SECURITY_CONTEXT"]
		//log.info("auth is: ${auth}")
		return auth != null
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
