package org.irods.mydrop.service
import javax.servlet.http.HttpSession

import org.irods.jargon.datautils.shoppingcart.FileShoppingCart
import org.irods.jargon.datautils.shoppingcart.ShoppingCartEntry
import org.springframework.web.context.request.RequestContextHolder

class ShoppingCartSessionService {

	static transactional = false
	//static scope = "session"
	static final String CART_ATTRIBUTE = "shoppingCart"

	/**
	 * Add the item to the shopping cart
	 * @param irodsFileAbsolutePath <code>String</code> with the absolute path to the iRODS file to add to the cart
	 * @param irodsAccount <code>IRODSAccount</code> for which the cart will be associated
	 * @return
	 */
	public void  addToCart(String irodsFileAbsolutePath) {
		FileShoppingCart fileShoppingCart = getCartFromSessionAndCreateIfNotThere()
		fileShoppingCart.addAnItem(new ShoppingCartEntry(irodsFileAbsolutePath))
	}
	
	/**
	 * Get a count of items in the cart (right now high level items, trying to cut down on queries)
	 * @return <code>int</code> with count of entries in the cart
	 */
	public int getCartItemsCount() {
		FileShoppingCart fileShoppingCart = getCartFromSession()
		return fileShoppingCart.shoppingCartEntries.size()
		
	}
	

	/**
	 * List the contents of the cart as a list of file names
	 * @return
	 */
	public List<String> listCart() {
		List<String> results = new ArrayList<String>()
		FileShoppingCart fileShoppingCart = getCartFromSession()
		if (fileShoppingCart) {
			results = fileShoppingCart.getShoppingCartFileList()
		}
		return results
	}

	/**
	 * Clear the files in the cart
	 */
	public void clearCart() {
		FileShoppingCart fileShoppingCart = getCartFromSession()
		if (fileShoppingCart) {
			fileShoppingCart.clearCart()
		}
	}

	public void deleteFromCart(String fileName) {
		FileShoppingCart shoppingCart = getSession().shoppingCart
		if (shoppingCart) {
			log.info("have a shopping cart, delete ${fileName}")
			shoppingCart.removeAnItem(fileName)
		}
	}

	/**
	 * Get shopping cart from session if present, but don't create one if not present (saves storing unnecesary session state)
	 * @return {@link FileShoppingCart} if stored in session, or <code>null</code> if not stored
	 */
	private FileShoppingCart getCartFromSession() {
		FileShoppingCart shoppingCart = getSession().shoppingCart
		return shoppingCart
	}

	private FileShoppingCart getCartFromSessionAndCreateIfNotThere() {
		FileShoppingCart shoppingCart = getSession().shoppingCart
		if (!shoppingCart) {
			log.info("no shopping cart, create one")
			shoppingCart = new FileShoppingCart()
			getSession().shoppingCart = shoppingCart
		}
		return shoppingCart

	}

	private HttpSession getSession() {
		return RequestContextHolder.currentRequestAttributes().getSession()
	}

}
