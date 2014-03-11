class UrlMappings {

	static mappings = {
		"/$controller/$action?/$id?"{ constraints { // apply constraints here
			} }

		"500"(controller: "error")
		//	"/"(view:"/index-angularjs.html")
	}
}
