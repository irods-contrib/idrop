/**
 * Javascript for common elements in main layout template, such as menus, headers, and footers.
 * 
 * author: Mike Conway - DICE
 */

function search() {
		var searchTerm = $("#searchTerm").val();
		var searchType = $("#searchType").val();

		$('#tabs').tabs({ selected: 1 }); // activate search results tab
		prosecuteSearch(searchTerm, searchType);
}

/**
 * Linked to update tags button on info view, update the tags in iRODS
 */
function updateTags() {
	var infoTagsVal = $("#infoTags").val();
	var infoCommentVal = $("#infoComment").val();
	var absPathVal = $("#infoAbsPath").val();

	var params = {
		absPath : absPathVal,
		tags : infoTagsVal,
		comment: infoCommentVal
	}

	lcSendValueViaPostAndCallbackHtmlAfterErrorCheck("/tags/updateTags",
			params, null, "#infoUpdateArea", function() {
				$("#infoUpdateArea").html("Tags updated");
				refreshTagCloud();
			});
}

/*
 * Update the information in the tag cloud
 */
function haveTagCloudData(data) {
	$("#tagCloudDiv").empty();
	//$("#tagCloudDiv").jQCloud(data, new function(){	$(".w1").each(function(index, value) { value.bind("click", function(data) { alert("clicked for data " + data);});});});
	$("#tagCloudDiv").jQCloud(data, new function(){});

}

function clickInTagCloud(data) {
	$('#tabs').tabs({ selected: 1 }); // activate search results tab
	prosecuteSearch(data, "tag");
}

function refreshTagCloud() {
	lcShowBusyIconInDiv("#tagCloudDiv");
	lcSendValueAndCallbackWithJsonAfterErrorCheck("/tags/tagCloudFormatted", null,
			"#tagCloudDiv",  function(data){haveTagCloudData(data);});
	
}

function logout() {
	window.location = context + "/j_spring_security_logout"; 
}

/**
 * On main panel, show the user panel
 */
function showUserPanel() {
	
	
	
	
	
}



