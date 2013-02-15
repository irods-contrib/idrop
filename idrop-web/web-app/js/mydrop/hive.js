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
