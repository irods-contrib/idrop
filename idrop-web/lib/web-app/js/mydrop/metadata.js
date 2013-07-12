/**
 * Javascript for metadata functions
 * 
 * author: Mike Conway - DICE
 */

/**
 * Global var
 */
var metadataMessageAreaSelector = "#metadataMessageArea";
var metadataDialogMessageAreaSelector = "#metadataDialogMessageArea";
var metadataAddUrl = '/metadata/addMetadata';
var metadataUpdateUrl = '/metadata/updateMetadata';
var metadataDeleteUrl = '/metadata/deleteMetadata';
var metadataLoadUrl = '/metadata/listMetadata';

/**
 * Called by data table upon submit of an acl change
 */
function metadataUpdate(currentAvu, newAvu, path) {

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
		currentAttribute : currentAvu['attribute'],
		currentValue : currentAvu['value'],
		currentUnit : currentAvu['unit'],
		newAttribute : newAvu['attribute'],
		newValue : newAvu['value'],
		newUnit : newAvu['unit']
	}

	var jqxhr = $.post(context + metadataUpdateUrl, params,
			function(data, status, xhr) {
				lcClearDivAndDivClass(metadataMessageAreaSelector);
			}, "html").error(function(xhr, status, error) {
				lcClearDivAndDivClass(metadataMessageAreaSelector);
		setErrorMessage(xhr.responseText);
		reloadMetadataDetailsTable();
		throw (xhr.responseText);
	}).success(
			function(returnedData, status, xhr) {
				var continueReq = checkForSessionTimeout(returnedData, xhr);
				if (!continueReq) {
					return false;
				}
				setMessage(
						"Metadata update successful"); 
			});

	
}

/**
 * The metadata dialog is prepared and ready to display as a JQuery dialog box,
 * show it
 * 
 */
function prepareMetadataDialog(data) {

	if (selectedPath == null) {
		setErrorMessage("No path is selected, metadata cannot be entered");
		return;
	}
	
	$("#metadataDetailsArea").hide("slow");

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
 * The metadata dialog is prepared and ready to display as a JQuery dialog box,
 * show it
 * 
 * @param data
 */
function showMetadataDialog(data) {
	$("#metadataDialogArea").html(data).fadeIn('slow');

}

/**
 * Handle form submit to add AVU Metadata
 */
function submitMetadataDialog() {

	var attribute = $('[name=attribute]').val();
	var value = $('[name=value]').val();
	var unit = $('[name=unit]').val();

	if (selectedPath == null) {
		setErrorMessage("no collection or data object selected"); // FIXME: alert and i18n
		return false;
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
		setErrorMessage(xhr.responseText);
		lcClearDivAndDivClass(metadataMessageAreaSelector);
	}).success(
			function(data, status, xhr) {
				var continueReq = checkForSessionTimeout(data, xhr);
				if (!continueReq) {
					return false;
				}
				// on success (no exception), check for valid data or invalid
				// data and update appropriately
				var dataJSON = jQuery.parseJSON(data);
				if (dataJSON.response.errorMessage != null) {
					setErrorMessage(
							dataJSON.response.errorMessage);
				} else {
					setMessage("AVU saved successfully"); 
					reloadMetadataDetailsTable(selectedPath);
					closeMetadataDialog();
					
				}
			});
}

/**
 * Cause the add metadata dialog to be closed
 */
function closeMetadataDialog() {
	$("#metadataDialogArea").fadeOut('slow', new function() {
		$("#metadataDialogArea").html("");
		$("#metadataDetailsArea").show("slow");
	});
}

/**
 * Delete selected AVU information
 */
function deleteMetadata() {
	lcClearDivAndDivClass(metadataMessageAreaSelector);

	if (!confirm('Are you sure you want to delete?')) {
		setMessage("Delete cancelled"); // FIXME:i18n
		return;
	}

	var selectedRows = $('#metaDataDetailsTable :checked');
	
	var formFields = new Array();
	var pathInfo = new Object();
	pathInfo.name = "absPath";
	pathInfo.value = selectedPath;

	formFields.push(pathInfo);

	selectedRows.each(function(index, element) {

		var siblings = $(element).parent().siblings();
		siblings.each(function(index) {
			var sib = $(this);
		});

		var attr = siblings.filter(".avuAttribute");
		var value = siblings.filter(".avuValue");
		var unit = siblings.filter(".avuUnit");
		
		var attributeParm = new Object();
		attributeParm.name = "attribute";
		attributeParm.value = attr.html();
		formFields.push(attributeParm);

		var valueParm = new Object();
		valueParm.name = "value";
		valueParm.value = value.html();
		formFields.push(valueParm);

		var unitParm = new Object();
		unitParm.name = "unit";
		unitParm.value = unit.html();
		formFields.push(unitParm);

	});
	
	var jqxhr = $.post(context + metadataDeleteUrl, formFields,
			function(data, status, xhr) {
			}, "html").error(function(xhr, status, error) {
		setError(xhr.responseText);
	}).success(function(data, status, xhr) {
		var continueReq = checkForSessionTimeout(data, xhr);
		if (!continueReq) {
			return false;
		}
		reloadMetadataDetailsTable();
		setMessage("Delete successful"); 
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

/**
 * Reload the metadata details as a JQuery data table using an AJAX call to get
 * the data
 * 
 * @returns
 */
function reloadMetadataDetailsTable(absPath) {
	
	if (absPath == null) {
		absPath = selectedPath;
	}

	lcClearDivAndDivClass(metadataMessageAreaSelector);
	$("#metadataTableDiv").empty();
	lcShowBusyIconInDiv("#metadataTableDiv");
	
	

	var params = {
		absPath : absPath
	}

	var jqxhr = $.get(context + metadataLoadUrl, params,
			function(data, status, xhr) {
				$('#metadataTableDiv').html(data);
			}, "html").error(function(xhr, status, error) {
		setErrorMessage(xhr.responseText);
	}).success(function(data, status, xhr) {
		var continueReq = checkForSessionTimeout(data, xhr);
		if (!continueReq) {
			return false;
		}
		buildMetadataTableInPlace();
	});
}

/**
 * Given that the metadata details table data has been retrieved (as html
 * table), make it a dataTable, and add necessary code to process in-place
 * edit/update of metadata
 * 
 * @returns
 */
function buildMetadataTableInPlace() {

	  tableParams = {"bJQueryUI" : true,
          	"bLengthChange": false,
          	"bFilter": false,
          	"iDisplayLength" : 500,
          	 "aoColumns" : [
        	                {'sWidth': '20px', 'bSortable':false},
        	                null,
        	                null,
        	                null
        	            ]


          }
	var metaDataTable = lcBuildTableInPlace("#metaDataDetailsTable", null, null, tableParams);
	$("#infoDiv").resize();

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

		var siblings = currentNode.siblings();
		siblings.each(function(index) {
			var sib = $(this);
			if (sib.hasClass("avuAttribute")) {
				avu['attribute'] = sib.html();
				newAvu['attribute'] = sib.html();
			} else if (sib.hasClass("avuValue")) {
				avu['value'] = sib.html();
				newAvu['value'] = sib.html();
			} else if (sib.hasClass("avuUnit")) {
				avu['unit'] = sib.html();
				newAvu['unit'] = sib.html();
			}
		});

		if (selectedPath == null) {
			setErrorMessage("No path selected");
			return origData;
		}

		try {
			metadataUpdate(avu, newAvu, selectedPath);
		} catch (e) {
			setErrorMessage("Error updating metadata:" + e);
			return origData;
		}
		return content;

	}, {
		type : 'textarea',
		submit : 'OK',
		cancel : 'Cancel',
		onblur: 'ignore',
		tooltip : 'Click to edit...',
		placeholder : '',
		data : function(value, settings) {
			origData = value;
			return value;
		}

	});

	return metaDataTable;
}
