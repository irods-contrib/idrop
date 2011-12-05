/**
 * Javascript for user functions 
 * author: Mike Conway - DICE
 */

/**
 * Search for users based on the search type and return a data table with the results
 */
function searchUsers() {
	var userSearchType = $("#userSearchType").val();
	var userName = $("#userSearchTerm").val();
	lcSendValueAndCallbackHtmlAfterErrorCheckPreserveMessage("/user/userSearchByNameLike?userSearchTerm="
			+ encodeURIComponent(userName), "#userTableDiv", "#userTableDiv", null);
	
}

