/**
 * Process hashChange events through the bbq plug in for back button support for
 * the browser
 * 
 * @param state
 * @returns {Boolean}
 */
function processTagSearchStateChange(state) {
	var tab = state["tab"];
	var tag = state["tag"];

	if (tab) {
		var selector = '#searchTabs a[href="' + tab + '"]';
		$(selector).tab('show');
	}
}

function clickInTagCloud(data) {
	$('#tabs').tabs({
		selected : 1
	}); // activate search results tab
	searchWithTag(data);
}

function refreshTagCloud() {
	lcShowBusyIconInDiv("#tagCloudDiv");
	lcSendValueAndCallbackWithJsonAfterErrorCheck("/tags/tagCloudFormatted",
			null, "#tagCloudDiv", function(data) {
				haveTagCloudData(data);
			});
}

/**
 * 
 * @param data
 * @returns {Boolean}
 */
function searchWithTag(data) {
	if (data == null || data == "") {
		setErrorMessage(jQuery.i18n.prop('msg_search_missing'));
		return false;
	}

	var params = {
		searchTerm : data,
		searchType : "tag"
	}
	
	var tableParams = {"bJQueryUI" : false, "bFilter" : false, "iDisplayLength":"5000"}

	// show result tab
	$('#searchTabs a[href="#resultsTab"]').tab('show');
	lcSendValueAndBuildTable("/search/search", params, "#resultsTabInner",
			"#searchResultTable", searchDetailsClick, ".search-detail-icon", tableParams);

}

/*
 * Update the information in the tag cloud
 */
function haveTagCloudData(data) {
	$("#tagCloudDiv").empty();
	$("#tagCloudDiv").jQCloud(data, {
		width : 800,
		height : 600
	});
	
	

}