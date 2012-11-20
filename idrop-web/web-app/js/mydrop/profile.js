/**
 * Javascript for profile functions
 */


/**
 * Update the profile information
 */
function updateUserProfile() {

	var params = {
			nickName : $("#nickName").val(),
			description : $("#description").val(),
			email : $("#email").val()
		}

		showBlockingPanel();

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
}

/**
 * Accomplish the password change 
 */
function submitChangePassword() {
	var formData = $("#changePasswordForm").serializeArray();

	if (formData == null) {
		setErrorMessage(jQuery.i18n.prop('msg_no_password_data')); 
		return false;
	}
	
	showBlockingPanel();

	var jqxhr = $.post(context + "/login/changePassword", formData,
			function(data, status, xhr) {
			}, "html").success(function(data, status, xhr) {
				var continueReq = checkForSessionTimeout(data, xhr);
				if (!continueReq) {
					return false;
				} 
				
				$("#profileDialogArea").html(data);
				closePasswordDialog();
				setMessage(jQuery.i18n.prop('msg_password_successful'));
				unblockPanel();
				
	}).error(function(xhr, status, error) {
		
		setErrorMessage(xhr.responseText);
		unblockPanel();
	});
}


/**
 * load the profile details information
 */
function loadProfileData() {
	var targetDiv = "#profileDataArea";
	lcSendValueAndCallbackHtmlAfterErrorCheckPreserveMessage(
			"/profile/loadProfileData",
			targetDiv, targetDiv, null);
}

/**
 * Show the password change dialog
 */

function showChangePasswordDialog() {
	var targetDiv = "#profileDialogArea";
	$("#profileDataArea").hide("slow");
        $("#profileToolbar").hide("slow");
	lcSendValueAndCallbackHtmlAfterErrorCheckPreserveMessage(
			"/profile/showPasswordChangeDialog",
			targetDiv, targetDiv, null);
}

/**
 * close the password dialog
 */
function closePasswordDialog() {
	$("#profileDialogArea").html("");
        $("#profileToolbar").show("slow");
	$("#profileDataArea").show("slow");
}




