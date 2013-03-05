package org.irods.mydrop.controller

import org.irods.jargon.core.connection.IRODSAccount;
import org.irods.jargon.core.pub.IRODSAccessObjectFactory;
import org.irods.mydrop.service.ShoppingCartSessionService;

/**
 * Controller for shopping cart functionality
 * @author Mike Conway - DICE (www.irods.org)
 */

class ShoppingCartController {

	IRODSAccessObjectFactory irodsAccessObjectFactory
	IRODSAccount irodsAccount
	ShoppingCartSessionService shoppingCartSessionService
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
	 * Show the cart main view
	 */
	def index = {
		log.info("index")
		render(view:"index")
	}


	/**
	 * Build the JTable entries for the contents of the shopping cart
	 */
	def listCart = {
		log.info("listCart")
		List<String> cart = shoppingCartSessionService.listCart()
		render(view:"cartDetails", model:[cart:cart])
	}

	/**
	 * Clear the contents of the shopping cart
	 */
	def clearCart = {
		log.info("clearCart")
		shoppingCartSessionService.clearCart()
		render "OK"
	}

	/**
	 * Delete the given files from the shopping cart
	 */
	def deleteFromCart = {
		log.info("deleteFromCart")
		log.info("params: ${params}")

		def filesToDelete = params['selectCart']

		// if nothing selected, just jump out and return a message
		if (!filesToDelete) {
			log.info("no files to delete")
			render "OK"
			return
		}

		log.info("filesToDelete: ${filesToDelete}")

		if (filesToDelete instanceof Object[]) {
			log.debug "is array"
			filesToDelete.each{
				log.info "filesToDelete: ${it}"
				shoppingCartSessionService.deleteFromCart(it)
			}

		} else {
			log.debug "not array"
			log.info "deleting: ${filesToDelete}"
			shoppingCartSessionService.deleteFromCart(filesToDelete)
		}

		render "OK"
	}

	/**
	 * Add one file to the cart
	 */
	def addFileToCart = {
		log.info("addFileToCart")
		def absPath = params['absPath']

		log.info("absPath: ${absPath}")
		shoppingCartSessionService.addToCart(absPath)

		render absPath
	}


	/**
	 * Process a bulk add to cart action based on data input from the browse details form
	 */
	def addToCartBulkAction = {
		log.info("addToCartBulkAction")

		log.info("params: ${params}")

		def filesToAdd = params['selectDetail']

		// if nothing selected, just jump out and return a message
		if (!filesToAdd) {
			log.info("no files to add")
			render "OK"
			return
		}

		log.info("filesToAdd: ${filesToAdd}")


		if (filesToAdd instanceof Object[]) {
			log.debug "is array"
			filesToAdd.each{
				log.info "filesToAdd: ${it}"
				shoppingCartSessionService.addToCart(it)

			}

		} else {
			log.debug "not array"
			log.info "adding: ${filesToAdd}"
			shoppingCartSessionService.addToCart(filesToAdd)
		}

		render "OK"
	}

}
