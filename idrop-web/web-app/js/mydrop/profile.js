/**
 * Javascript for profile functions
 */


/**
 * Update the profile information
 */
function updateUserProfile() {

	/*
	var params = {
			nickName : $("#nickName").val(),
			description : $("#description").val(),
			email : $("#email").val()
		}
*/
		showBlockingPanel();
		$("#userProfileForm").submit();

		/*
		var jqxhr = $.post(context + "/profile/updateProfile", params,
				function(data, status, xhr) {
				}, "html").success(function(returnedData, status, xhr) {
			var continueReq = checkForSessionTimeout(returnedData, xhr);
			if (!continueReq) {
				return false;
			}
			setMessage(jQuery.i18n.prop('msg_profile_update_successful'));
			$("#profileDataArea").html(returnedData);
			unblockPanel();
		}).error(function(xhr, status, error) {
			setErrorMessage(xhr.responseText);
			unblockPanel();
		});
		*/
}


/**
 * load the profile details information
 */
function loadProfileData() {
	/*var targetDiv = "#profileDataArea";
	lcSendValueAndCallbackHtmlAfterErrorCheckPreserveMessage(
			"/profile/loadProfileData",
			targetDiv, targetDiv, null);*/
	 window.location=context + '/profile/index';
}



