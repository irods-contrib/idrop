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
			function(data) {
				var dataJSON = jQuery.parseJSON(data);
				var response = dataJSON.response;
				if (response.errorMessage != null) {
					// build a message and set the class of the message area
					
					var message = "";
					message += "<div><span>";
					message += response.errorMessage;
					message += "</span><p/><ul>";
					
					var detailErrors = response.errors;
					for (i = 0; i < detailErrors.length; i++) {
						message += "<li>";
						message += detailErrors[i];
						message += "</li>"
					}
					
					message += "</ul></div>";
					setMessageInArea(metadataMessageAreaSelector,message);
					return;
				}
				
				closeMetadataDialog();
				addRowToMetadataDetailsTable(attribute,value,unit);
				setMessageInArea(metadataMessageAreaSelector,
						response.message);
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

function setupMetadataDetailsTable() {
	dataTable = lcBuildTableInPlace("#metaDataDetailsTable", null, null);	
	$('.editable').editable(function(content, settings) {
	
	     var avu = [];
	     var newAvu = [];

	     var currentNode = $(this);

	     if (currentNode.hasClass("avuAttribute")) {
		     avu['attribute'] = origData;
		     newAvu['attribute'] = content;
		 } else if (currentNode.hasClass("avuValue")) {
			 avu['value'] = origData;
		     newAvu['value'] = content;
		} else if (currentNode.hasClass("avuUnit")) {
			 avu['unit'] = origData;
		     newAvu['unit'] = content;
		}

		//var siblings = $(this).siblings();
		var siblings = currentNode.siblings();//parent().children();
		siblings.each(function(index) { 
			var sib = $(this);
			if (sib.hasClass("avuAttribute")) {
			     avu['attribute'] = sib.html();
			     newAvu['attribute'] =  sib.html();
			 } else if (sib.hasClass("avuValue")) {
				  avu['value'] =sib.html();
				     newAvu['value'] =  sib.html();
			} else if (sib.hasClass("avuUnit")) {
				  avu['unit'] = sib.html();
				     newAvu['unit'] =  sib.html();
			}
		});
		
		console.log("currentAVU:" + avu['attribute'] + "/" +  avu['value'] + "/" + avu['unit']);
		console.log("newAVU:" +  newAvu['attribute'] + "/" +  newAvu['value'] + "/" + newAvu['unit']);

		if (selectedPath == null) {
			throw "no collection or data object selected";
		}
		
		metadataUpdate(avu, newAvu, selectedPath);

	     
	     return(content);
	} , {type    : 'textarea',
	     submit  : 'OK',
	     cancel    : 'Cancel',
	     data: function(value, settings) {
	        origData = value;
	        return value;
	       }


	     });
	
	return dataTable;
}


