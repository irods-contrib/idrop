package org.irods.mydrop.service

import javax.servlet.http.HttpSession

import org.irods.mydrop.config.ViewState
import org.springframework.web.context.request.RequestContextHolder

class ViewStateService {
	
	static transactional = false
	//static scope = "session"	
	
	/**
	 * Get the stored root path
	 * @return
	 */
	public String retrieveRootPath() {
		ViewState viewState = getViewStateFromSessionAndCreateIfNotThere()
		return viewState.rootPath
	}
	
	/**
	 * clear the view state
	 
	 */
	public void clearViewState() {
		getSession().viewState = null
		
	}
	
	/**
	 * Store the root Path
	 * @param path
	 * @return
	 */
	public void saveRootPath(String path) {
		if (path == null) {
			throw new IllegalArgumentException("null path")
		}
		
		ViewState viewState = getViewStateFromSessionAndCreateIfNotThere()
		viewState.rootPath = path
		
	}
	
	/**
	 * Returnt the view mode associated with the browser
	 * @return
	 */
	public String retrieveViewMode() {
		ViewState viewState = getViewStateFromSessionAndCreateIfNotThere()
		return viewState.browseView
	}
	
	public void saveViewMode(String viewMode) {
		if (viewMode == null) {
			throw new IllegalArgumentException("null viewMode")
		}
		ViewState viewState = getViewStateFromSessionAndCreateIfNotThere()
		viewState.browseView = viewMode
	}
	

   private ViewState getStateFromSession() {
		ViewState viewState = getSession().viewState
		return viewState
	}

   public ViewState getViewStateFromSessionAndCreateIfNotThere() {
		ViewState viewState = getStateFromSession()
		if (!viewState) {
			log.info("no viewStat, create one")
			viewState = new ViewState()
			getSession().viewState = viewState
		}
		return viewState

	}

	private HttpSession getSession() {
		return RequestContextHolder.currentRequestAttributes().getSession()
	}

}
