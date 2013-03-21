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
		targetDiv = "#infoAccordionHiveInner";
		// I am not embedded, so manipulate the toolbars
	}

	try {

		lcSendValueAndCallbackHtmlAfterErrorCheckThrowsException("/hive/index",
				targetDiv, function(data) {
					// alert("data is:" + data);
					$(targetDiv).html(data);
				}, function() {
					setHiveNoData();
				});
	} catch (err) {
		setHiveNoData();
	}

}

/**
 * Set the concept browser view to the given uri
 * 
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

		lcSendValueWithParamsAndPlugHtmlInDiv("/hive/conceptBrowser", params,
				"#infoAccordionHiveInner", null);

	} catch (err) {
		setHiveNoData();
	}

}

/**
 * Set the vocabulary to the top level of the provided vocabulary name.  If no vocabulary
 * is specified, the controller will pick the first of the selected vocabularies to display
 * @param vocabulary
 */
function resetVocabulary(vocabulary) {
	var params;
	if (vocabulary == null) {
		 params = {

		}
	} else {
		 params = {
			vocabulary : vocabulary
		}
	}
	
	try {
		lcSendValueWithParamsAndPlugHtmlInDiv("/hive/resetConceptBrowser", params,
				"#infoAccordionHiveInner", function(data) {
					$("#infoAccordionHiveInner").html(data);
				}, function() {
					setHiveNoData();
				});
	} catch (err) {
		setHiveNoData();
	}


}

function selectVocabularies() {
	var formData = $("#hiveVocabularyForm").serializeArray();
	if (formData == null) {
		setErrorMessage(jQuery.i18n.prop('msg_no_form_data'));
		return false;
	}

	lcShowBusyIconInDiv("#infoAccordionHiveInner");

	var jqxhr = $.post(context + "/hive/selectVocabularies", formData,
			function(data, status, xhr) {
			}, "html").success(function(data, status, xhr) {
		var continueReq = checkForSessionTimeout(data, xhr);
		if (!continueReq) {
			return false;
		}

		$("#infoAccordionHiveInner").html(data);

	}).error(function(xhr, status, error) {
		setErrorMessage(xhr.responseText);
	});

}

function searchConcept() {
	var formData = $("#searchConceptForm").serializeArray();
	if (formData == null) {
		setErrorMessage(jQuery.i18n.prop('msg_no_form_data'));
		return false;
	}
	
	lcShowBusyIconInDiv("#searchConceptForm");

	var jqxhr = $.post(context + "/hive/searchConcept", formData,
			function(data, status, xhr) {
			}, "html").success(function(data, status, xhr) {
		var continueReq = checkForSessionTimeout(data, xhr);
		if (!continueReq) {
			return false;
		}

		$("#searchConceptForm").html(data);

	}).error(function(xhr, status, error) {
		setErrorMessage(xhr.responseText);
	});
}

function setHiveNoData() {
	$("#infoAccordionHiveInner").html("No data to display");
}
