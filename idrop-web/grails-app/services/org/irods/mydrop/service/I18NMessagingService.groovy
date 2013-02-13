package org.irods.mydrop.service

class I18NMessagingService {

   static transactional = false

    def message(String code, List args) {
		message(code:code, args:args)
    }
	
	def message(String code) {
		message(code:code)
	}
	
	def message(Object error) {
		message(error:error)
	}
	
}
