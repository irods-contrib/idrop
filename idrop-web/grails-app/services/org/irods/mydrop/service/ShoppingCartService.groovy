package org.irods.mydrop.service
import javax.servlet.http.HttpSession

import org.irods.jargon.core.connection.IRODSAccount
import org.irods.jargon.datautils.shoppingcart.FileShoppingCart
import org.irods.jargon.datautils.shoppingcart.ShoppingCartEntry
import org.springframework.web.context.request.RequestContextHolder

class ShoppingCartService {

    static transactional = false
	//static scope = "session"
	static final String CART_ATTRIBUTE = "shoppingCart"

	/**
	 * Add the item to the shopping cart
	 * @param irodsFileAbsolutePath <code>String</code> with the absolute path to the iRODS file to add to the cart
	 * @param irodsAccount <code>IRODSAccount</code> for which the cart will be associated
	 * @return
	 */
    public void  addToCart(String irodsFileAbsolutePath, IRODSAccount irodsAccount) {
		FileShoppingCart fileShoppingCart = getCartFromSessionAndCreateIfNotThere(irodsAccount)
		fileShoppingCart.addAnItem(new ShoppingCartEntry(irodsFileAbsolutePath))
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
			shoppingCart.removeAnItem(fileName);
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
	
	private FileShoppingCart getCartFromSessionAndCreateIfNotThere(IRODSAccount irodsAccount) {
		FileShoppingCart shoppingCart = getSession().shoppingCart
		if (!shoppingCart) {
			log.info("no shopping cart, create one")
			shoppingCart = new FileShoppingCart(irodsAccount)
			getSession().shoppingCart = shoppingCart
		}
		return shoppingCart
		
	}
	
	private HttpSession getSession() {
		return RequestContextHolder.currentRequestAttributes().getSession()
	  }
	
}
