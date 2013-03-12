/**
 * Show the hive view
 * 
 * @param absPath
 * @returns {Boolean}
 */
function showHiveView(absPath, targetDiv) {
	if (absPath == null) {
		absPath = baseAbsPath;
		return false;
	}

	try {
		var params = {
			absPath : absPath
		}

		lcSendValueWithParamsAndPlugHtmlInDiv("/hive/index", params,
				"#infoAccordionHiveInner", null);

	} catch (err) {
		setErrorMessage(err);
		setHiveNoData();
	}

}

/**
 * Set the concept browser view to the given uri
 * 
 * @param uri
 * @returns
 */
function browseToUri(uri, absPath) {

	if (uri == null) {
		setErrorMessage(jQuery.i18n.prop('msg_no_uri'));
		return false;
	}

	if (absPath == null) {
		setErrorMessage(jQuery.i18n.prop('msg_path_missing'));
		return false;
	}

	try {
		var params = {
			targetURI : uri,
			absPath : absPath
		}

		lcSendValueWithParamsAndPlugHtmlInDiv("/hive/conceptBrowser", params,
				"#infoAccordionHiveInner", null);

	} catch (err) {
		setErrorMessage(err);
		setHiveNoData();
	}

}

/**
 * Set the vocabulary to the top level of the provided vocabulary name. If no
 * vocabulary is specified, the controller will pick the first of the selected
 * vocabularies to display
 * 
 * @param absPath
 *            current iRODS path
 * @param vocabulary
 *            HIVE vocabulary name
 */
function resetVocabulary(vocabulary, absPath) {

	if (absPath == null) {
		setErrorMessage(jQuery.i18n.prop('msg_path_missing'));
		return false;
	}

	try {
		var params;
		if (vocabulary == null) {
			params = {
				absPath : absPath
			}
		} else {
			params = {
				absPath : absPath,
				vocabulary : vocabulary
			}
		}
		lcSendValueWithParamsAndPlugHtmlInDiv("/hive/resetConceptBrowser",
				params, "#infoAccordionHiveInner", function(data) {
					$("#infoAccordionHiveInner").html(data);
				}, function() {
					setHiveNoData();
				});
	} catch (err) {
		setErrorMessage(err);
		setHiveNoData();
	}

}

function selectVocabularies(absPath) {
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

function setHiveNoData() {
	$("#infoAccordionHiveInner").html("No data to display");
}
