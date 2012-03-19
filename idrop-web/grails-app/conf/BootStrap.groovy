class BootStrap {

	def init = { servletContext ->

		environments {
			production {}
			development {}
		}
	}
	def destroy = {
	}
}
