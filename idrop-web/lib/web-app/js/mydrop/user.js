/**
 * Javascript for user functions 
 * author: Mike Conway - DICE
 */

/**
 * Search for users based on the search type and return a data table with the results
 * @param userSearchType 1=name
 * @param userName (can be blank, which searches all, does a 'like userName%' search otherwise)
 * @param targetDiv jquery selector for target div for data, if blank defaults to '#userTableDiv'
 */
function searchUsers(userSearchType, userName, targetDiv) {
	if (userSearchType == null || userSearchType.length == 0) {
		throw new Exception("No user search type provided");
	}
	
	if (userName == null) {
		throw new Exception("No user name provided");
	}
	
	if (targetDiv == null || targetDiv.length==0) {
		targetDiv = "#userTableDiv";
	}
	
	
	//var userSearchType = $("#userSearchType").val();
	//var userName = $("#userSearchTerm").val();
	lcSendValueAndCallbackHtmlAfterErrorCheckPreserveMessage("/user/userSearchByNameLike?userSearchTerm="
			+ encodeURIComponent(userName), targetDiv, targetDiv, null);
	
	
	
}

