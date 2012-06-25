class UrlMappings {

	static mappings = {

		"/login/authenticate/*"(controller:"login"){
			action = [GET:"login", POST:"authenticate"]
		}

		//"/login/authenticate/normalLogin"(controller:"login", action:'login')

		"/$controller/$action?/$id?"{ constraints {
				// apply constraints here
			} }

		"/file/**" (controller:"file", action:"index"){
			name = { request.requestURI }
		}

		"/$controller/$action?"{ constraints {
				// apply constraints here
			} }

		"/"(controller:"home", action:"index")

		"500"(view:'/error')
	}
}
