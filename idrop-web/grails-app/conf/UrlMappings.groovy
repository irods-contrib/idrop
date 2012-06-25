class UrlMappings {

	static mappings = {
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

		"/login/authenticate"(controller:"login") {
			action = [GET:"login", PUT:"authenticate"]
		}

		"500"(view:'/error')
	}
}
