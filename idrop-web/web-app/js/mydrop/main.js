/**
 * Javascript for common elements in main layout template, such as menus,
 * headers, and footers.
 * 
 * author: Mike Conway - DICE
 */

function search() {
	var searchTerm = $("#searchTerm").val();

	if (searchTerm == "") {
		setMessage("Enter a search term");
		return false;
	}

	var searchType = $("#searchType").val();

	$('#tabs').tabs({
		selected : 1
	}); // activate search results tab
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
		comment : infoCommentVal
	}

	lcSendValueViaPostAndCallbackHtmlAfterErrorCheck("/tags/updateTags",
			params, null, "#infoUpdateArea", function() {
				setMessage("Tags and comments updated successfully");
				refreshTagCloud();
			});
}

/*
 * Update the information in the tag cloud
 */
function haveTagCloudData(data) {
	$("#tagCloudDiv").empty();
	$("#tagCloudDiv").jQCloud(data, {
		width : 300,
		height : 600
	});

}

function clickInTagCloud(data) {
	$('#tabs').tabs({
		selected : 1
	}); // activate search results tab
	prosecuteSearch(data, "tag");
}

function refreshTagCloud() {
	lcShowBusyIconInDiv("#tagCloudDiv");
	lcSendValueAndCallbackWithJsonAfterErrorCheck("/tags/tagCloudFormatted",
			null, "#tagCloudDiv", function(data) {
				haveTagCloudData(data);
			});

}

/**
 * Initial display of the user tab information in the sidebar
 */
function displayUserTab() {

}

function logout() {
	window.location = context + "/j_spring_security_logout";
}

/**
 * On main panel, show the user panel
 */
function showUserPanel() {
	lcSendValueAndCallbackHtmlAfterErrorCheckPreserveMessage("/user/index",
			"#userDiv", "#userDiv", null);
}

/**
 * Code to format a 'crumb trail' header
 * 
 * @param irodsAbsolutePath
 */
function setPathCrumbtrail(irodsAbsolutePath) {

	if (irodsAbsolutePath == null || irodsAbsolutePath.length == 0) {
		$("#infoDivPathArea").html("");
		return;
	}

	// else

	var pathArray = irodsAbsolutePath.split("/");
	var pathLen = irodsAbsolutePath.length;
	var compressedPathArray = new Array();
	var totLen = 0;

	// compress each part of the path
	$.each(pathArray, function(index, value) {
		compressedPathArray[index] = truncatePathPart(value);
		totLen += compressedPathArray[index].length;

	});

	var s = "";

	// a long path will drop the leading path parts, so indicate truncation
	if (totLen > 80) {
		s += "...";
	}

	$.each(compressedPathArray, function(index, value) {
		if (index > 0) {

			// if the path name is really long, just show the last 3 path
			// entries
			if (totLen > 80) {
				if (index > compressedPathArray.length - 3) {
					s += " / ";
					s += buildPathPartAnchor(index, pathArray);
					s += truncatePathPart(value);
					s += "</a>";

				}

			} else {

				s += " / ";
				s += buildPathPartAnchor(index, pathArray);
				s += truncatePathPart(value);
				s += "</a>";
			}
		}

	});

	$("#infoDivPathArea").html(s);

}

function buildPathPartAnchor(indexOfCurrentPathPart, pathArray) {

	var pathUrl = "<a href='#' id='";
	var absPathSubsection = "";
	$.each(pathArray, function(index, value) {
		// only building abs path for the current subsection of the link
		if (index > indexOfCurrentPathPart) {
			return;
		}

		if (value.length > 0) {
			absPathSubsection += "/";
			absPathSubsection += value;
		}

	});
	pathUrl += absPathSubsection;

	pathUrl += "' onclick='clickOnPathInCrumbtrail(this.id)'>";
	return pathUrl

}

/**
 * Take a part of a path and turn it into a truncated value
 * 
 * @param pathPart
 * @returns
 */
function truncatePathPart(pathPart) {
	if (pathPart == null || pathPart.length == 0) {
		return "";
	}

	// is not blank, if greater, turn into pathPart...lastPart format

	var pathPartLen = pathPart.length;
	if (pathPartLen > 40) {
		var s = pathPart.substring(0, 15);
		s += "...";
		s += pathPart.substring(pathPartLen - 15);
		return s;
	} else {
		return pathPart;
	}

}

/**
 * Called when a path component is clicked in the thumbtrail, align tree with
 * selected absolute path, which will show in the current view choice
 * 
 * @param data
 */
function clickOnPathInCrumbtrail(data) {

	if (data == null) {
		throw new Exception("no absolute path provided");
	}

	// if the id (abs path) length is less then or equal to the absolute path,
	// then show the root of the tree

	if (data.length <= baseAbsPath.length) {
		currentNode = $.jstree._reference(dataTree).get_container();
		var children = $.jstree._reference(dataTree)._get_children(currentNode);
		currentNode = children[0];

		$.jstree._reference(dataTree).open_node(currentNode);
		$.jstree._reference(dataTree).select_node(currentNode, true);
	} else {

		splitPathAndPerformOperationAtGivenTreePath(data, null, null, function(
				path, dataTree, currentNode) {
			$.jstree._reference(dataTree).open_node(currentNode);
			$.jstree._reference(dataTree).select_node(currentNode, true);

		});
	}
}
