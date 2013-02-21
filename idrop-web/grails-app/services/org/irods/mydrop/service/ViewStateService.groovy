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
	public ViewState saveRootPath(String path) {
		if (path == null) {
			throw new IllegalArgumentException("null path")
		}
		
		ViewState viewState = getViewStateFromSessionAndCreateIfNotThere()
		viewState.rootPath = path
		return viewState
		
	}
	
	/**
	 * Returnt the view mode associated with the browser
	 * @return
	 */
	public String retrieveViewMode() {
		ViewState viewState = getViewStateFromSessionAndCreateIfNotThere()
		return viewState.browseView
	}
	
	/**
	 * Save a path for later retrieval
	 * @param absolutePath
	 */
	public ViewState saveSelectedPath(String absolutePath) {
		
		
		if (absolutePath == null) {
			throw new IllegalArgumentException("null absolutePath")
		}
		
		ViewState viewState = getViewStateFromSessionAndCreateIfNotThere()
		viewState.selectedPath = absolutePath
		return viewState
	}
	
	/**
	 * Save a path and mode in the view state for later retrieval
	 * @param viewMode
	 * @param absolutePath
	 */
	public ViewState saveViewModeAndSelectedPath(String viewMode, String absolutePath) {
		
		if (viewMode == null) {
			throw new IllegalArgumentException("null viewMode")
		}
		
		if (absolutePath == null) {
			throw new IllegalArgumentException("null absolutePath")
		}
		
		ViewState viewState = getViewStateFromSessionAndCreateIfNotThere()
		viewState.browseView = viewMode
		viewState.selectedPath = absolutePath
		return viewState
	}
	
	public ViewState saveViewMode(String viewMode) {
		if (viewMode == null) {
			throw new IllegalArgumentException("null viewMode")
		}
		ViewState viewState = getViewStateFromSessionAndCreateIfNotThere()
		viewState.browseView = viewMode
		return viewState
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
