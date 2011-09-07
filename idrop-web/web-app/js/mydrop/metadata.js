/**
 * Javascript for metadata functions
 * 
 * author: Mike Conway - DICE
 */

/**
 * Global var
 */
var messageAreaSelector = "#metadataMessageArea";
var metadataUpdateUrl = '/metadata/updateMetadata'; 


/**
 * Called by data table upon submit of an acl change
 */
function  metadataUpdate(currentAcl, newAcl, path) {
	
	lcPrepareForCall();

	if (!path) {
		throw "No collection or data object selected";
	}
	
	if (!newAcl) {
		throw "No newAcl provided";
	}
	
	if (!currentAcl) {
		throw "no currentAcl provided";
	}

	lcShowBusyIconInDiv(messageAreaSelector);

	var params = {
		absPath : selectedPath,
		currentAclAttribute : currentAcl['attribute'],
		currentAclValue : currentAcl['value'],
		currentAclUnit : currentAcl['unit'],
		newAclAttribute : newAcl['attribute'],
		newAclValue : newAcl['value'],
		newAclUnit : newAcl['unit']
	}

	var jqxhr = $.post(context + metadataUpdateUrl, params,
			function(data, status, xhr) {
				lcClearDivAndDivClass(messageAreaSelector);
			}, "html").error(function() {
		setMessageInArea(messageAreaSelector, "Error updating metadata");
	}).complete(
			function() {
				setMessageInArea(messageAreaSelector,
						"Metadata update successful");
			});

}

/**
 * The metadata dialog is prepared and ready to display as a JQuery dialog box, show
 * it
 * 
 */
function prepareMetadataDialog(data) {
	
	if (selectedPath == null) {
		alert("No path is selected, metadata cannot be entered");
		return;
	}


	var url = "/metadata/prepareMetadataDialog";
	var params = {
		absPath : selectedPath,
		create : true
	}

	lcSendValueWithParamsAndPlugHtmlInDiv(url, params, "", function(data) {
		showMetadataDialog(data);
	});


}

/**
* The metadata dialog is prepared and ready to display as a JQuery dialog box, show
* it
* 
* @param data
*/
function showMetadataDialog(data) {
	lcPrepareForCall();
	$("#metadataDialogArea").html(data).fadeIn('slow');
	
	/**
	 * $("#aclDialogArea").html(data); $("#aclDialogArea").dialog({ "width" :
	 * 400, "modal" : true, "buttons" : { "Ok" : function() { submitAclDialog(); },
	 * "Cancel" : function() { $(this).dialog("close"); } }, "title" : "Edit
	 * Share Permission" });
	 */

}


