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

function requestUserPopup(event) {
	var user = event.currentTarget.id;
	lcPrepareForCall();

	lcShowBusyIconInDiv("#userPopupDialogArea");
	var url = "/user/userInfoDialog";

	var params = {
		user : user
	}

	lcSendValueWithParamsAndPlugHtmlInDiv(url, params, "#userPopupDialogArea", null);

}

/**
* Show a dialog that allows selection of multiple users such that a share permission can be set
* @param absPath absolute path to the file or collection to which the share will pertain
 */
function showBulkShareDialog(absPath) {
	lcPrepareForCall();

	lcShowBusyIconInDiv("#infoDialogArea");
	var url = "/sharing/userBulkSharingDialog";

	var params = {
		absPath : absPath
	}

	lcSendValueWithParamsAndPlugHtmlInDiv(url, params, "#infoDialogArea", null);

}

/**
* Do a user search for bulk sharing display
*/
function searchUsersBulkSharingDialog() {
	var userSearchType = $("#userBulkSharingSearchType").val();
	var userName = $("#userBulkSharingSearchTerm").val();
	lcSendValueAndCallbackHtmlAfterErrorCheckPreserveMessage("/user/userSearchByNameLike?userSearchTerm="
			+ encodeURIComponent(userName), "#userBulkSharingTableDiv", "#userBulkSharingTableDiv", null);
}



