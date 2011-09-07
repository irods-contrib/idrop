/**
 * Javascript for metadata functions
 * 
 * author: Mike Conway - DICE
 */

/**
 * Global var
 */
var metadataMessageAreaSelector = "#metadataMessageArea";
var metadataUpdateUrl = '/metadata/updateMetadata'; 
var metadataAddUrl = '/metadata/addMetadata'; 

/**
 * Called by data table upon submit of an acl change
 */
function  metadataUpdate(avu, newAvu, path) {
	
	lcPrepareForCall();

	if (!path) {
		throw "No collection or data object selected";
	}
	
	if (!newAvu) {
		throw "No newAvu provided";
	}
	
	if (!currentAvu) {
		throw "no currentAvu provided";
	}

	lcShowBusyIconInDiv(metadataMessageAreaSelector);

	var params = {
		absPath : selectedPath,
		currentAclAttribute : currentAcl['attribute'],
		currentAclValue : currentAcl['value'],
		currentAclUnit : currentAcl['unit'],
		newAclAttribute : newAcl['attribute'],
		newAclValue : newAcl['value'],
		newAclUnit : newAcl['unit']
	}

	var jqxhr = $.post(context + metadataAddUrl, params,
			function(data, status, xhr) {
				lcClearDivAndDivClass(metadataMessageAreaSelector);
			}, "html").error(function() {
		setMessageInArea(messageAreaSelector, "Error updating metadata");
	}).complete(
			function() {
				setMessageInArea(metadataMessageAreaSelector,
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
	
}

/**
 * Handle form submit to add AVU Metadata
 */
function submitMetadataDialog() {
	
	lcPrepareForCall();

	var attribute = $('[name=attribute]').val();
	var value = $('[name=value]').val();
	var unit = $('[name=unit]').val();
	

	if (selectedPath == null) {
		throw "no collection or data object selected";
	}

	var isCreate = $('[name=isCreate]').val();

	lcShowBusyIconInDiv(metadataMessageAreaSelector);

	var params = {
		absPath : selectedPath,
		attribute : attribute,
		value : value,
		unit : unit
	}

	var jqxhr = $.post(context + metadataAddUrl, params,
			function(data, status, xhr) {
				lcClearDivAndDivClass(metadataMessageAreaSelector);
			}, "html").error(function(xhr, status, error) {
		setMessageInArea(metadataMessageAreaSelector, xhr.responseText);
	}).success(
			function() {
				
				closeMetadataDialog();
				addRowToMetadataDetailsTable(attribute,value,unit);
				setMessageInArea(metadataMessageAreaSelector,
						"Metadata saved successfully");

			});
}

function closeMetadataDialog() {
	$("#metadataDialogArea").hide().fadeOut('slow', new function() {
		$("#metadataDialogArea").html("")
	});

}

function addRowToMetadataDetailsTable(attribute, value, unit) {
	
	var idxs = $("#metaDataDetailsTable")
			.dataTable()
			.fnAddData(
					[
							"<input id=\"selectedAcl\" type=\"checkbox\" name=\"selectedMetadata\">",
							attribute, value, unit ], true);
	var newNode = $("#metaDataDetailsTable").dataTable().fnGetNodes()[idxs[0]];
	$(newNode).attr("id", selectedPath);
	
}


