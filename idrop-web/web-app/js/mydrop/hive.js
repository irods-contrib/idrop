/**
 * Show the hive view
 * 
 * @param absPath
 * @returns {Boolean}
 */
function showHiveView(absPath, targetDiv) {
	if (absPath == null) {
		absPath = baseAbsPath;
	}

	if (targetDiv == null) {
		targetDiv = "#infoDiv";
		// I am not embedded, so manipulate the toolbars
	}

	try {

		lcSendValueAndCallbackHtmlAfterErrorCheckThrowsException(
				"/hive/index", targetDiv,
				function(data) {
					// alert("data is:" + data);
					$(targetDiv).html(data);
				}, function() {
					setInfoDivNoData();
				});
	} catch (err) {
		setInfoDivNoData();
	}

}

/**
 * Retrieve the listing of vocabulary terms underneath a given parent term.  A null or blank parent term will default to the first level of the tree.
 * The results are formatted as an HTML table for insertion into the vocabulary navigation tab
 * @param vocabulary name for which the terms are retrieved
 * @param parentTerm optional (null or blank if top level) parent for which child terms are found
 * @param targetDiv jquery selector for the div into which the resulting content will be inserted
 */
function getVocabularyListing(vocabularyName, parentTerm, targetDiv) {
	
	if (vocabularyName == null || vocabularyName = "") {
		setErrorMessage(jQuery.i18n.prop('msg_no_vocabulary'));
		return false;
	}
	
	if (targetDiv == null || targetDiv = "") {
		setErrorMessage(jQuery.i18n.prop('msg_no_target_div'));
		return false;
	}
	
	
	
	
}

/**
 * Set the concept browser view to the given uri
 * @param uri
 * @returns
 */
function browseToUri(uri) {
	if (uri == null) {
		setErrorMessage(jQuery.i18n.prop('msg_no_uri'));
	}
	
	var params = {
			targetURI : uri
		}

	try {
		
		 lcSendValueWithParamsAndPlugHtmlInDiv("/hive/conceptBrowser", params, "#infoDiv",
					null);

	} catch (err) {
		setInfoDivNoData();
	}

}


function selectVocabularies(){
	var formData = $("#hiveVocabularyForm").serializeArray();
	if (formData == null) {
		setErrorMessage(jQuery.i18n.prop('msg_no_form_data'));
		return false;
	}
	
	lcShowBusyIconInDiv("#hivePanelInner");

	var jqxhr = $.post(context + "/hive/selectVocabularies", formData,
			function(data, status, xhr) {
			}, "html").success(function(data, status, xhr) {
				var continueReq = checkForSessionTimeout(data, xhr);
				if (!continueReq) {
					return false;
				} 
				
	$("#hivePanelInner").html(data);
				
	}).error(function(xhr, status, error) {
		setErrorMessage(xhr.responseText);
	});
	
}


