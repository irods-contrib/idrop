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

function selectVocabularies(){
	var formData = $("#hiveVocabularyForm").serializeArray();
	if (formData == null) {
		setErrorMessage(jQuery.i18n.prop('msg_no_ticket_data'));
		return false;
	}
	
	lcShowBusyIconInDiv("#hivePanelInner");

	var jqxhr = $.post(context + "/hive/showTreeForSelectedVocabularies", formData,
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
