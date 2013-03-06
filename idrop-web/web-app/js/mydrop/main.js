/**
* Javascript for common elements in main layout template, such as menus,
 * headers, and footers.
 * 
 * author: Mike Conway - DICE
 */

/**
 * Information regarding the current file path and 'page' of data for that path being displayed 
 */
var baseAbsPath = "/";
var baseAbsPathAsArrayOfPathElements;
var displayPage = 1;
var displayIndex = 0;
/*
 * split mode is complicated.  Collections and Data objects are separate things, so there are two queries.  It is possible to encounter situations
 * where I have to separately page collections and data objects.  One cannot continuously page across collections and data objects unless we come up
 * with some fancy back end code.  Maybe later, for now, we let the user know and he/she can then decide what to display and page.
 * 
 * To help solve this, the idea of a split mode works like this:
 * 
 * -I do an initial listing, If I have collections to page then I will need to enter split mode, as I have not 'hit' the data objects yet 
 * -If I have collections and start on data objects before reaching the end of those, I also enter split mode
 * 
 * Split mode will cause an option to appear in the browse grid details where the user chooses to display collections or files, and paging occurs in only that 
 * domain
 * 
 *  n = no split mode
 * 	c = collection mode
 * 	d = data object mode
 */
var splitMode = 'n'; 

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
	
	/* bug!  what if root of tree is below the path...need to reset the tree */

	if (data.length <= baseAbsPath.length) {
		/*currentNode = $.jstree._reference(dataTree).get_container();
		var children = $.jstree._reference(dataTree)._get_children(currentNode);
		currentNode = children[0];

		$.jstree._reference(dataTree).open_node(currentNode);
		$.jstree._reference(dataTree).select_node(currentNode, true);*/
		if (data == "") {
			data = "/";
		}
		retrieveBrowserFirstView("path", data);
	} else {

		splitPathAndPerformOperationAtGivenTreePath(data, null, null, function(
				path, dataTree, currentNode) {
			$.jstree._reference(dataTree).open_node(currentNode);
			$.jstree._reference(dataTree).select_node(currentNode, true);

		});
	}
}

/**
 * Show the default storage resource dialog
 */
function showDefaultResourceDialog() {	
	var url = "/login/showDefaultResourceDialog";
	lcSendValueWithParamsAndPlugHtmlInDiv(url, null, "#defaultDialogDiv", null);
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

	showBlockingPanel();
	lcSendValueViaPostAndCallbackHtmlAfterErrorCheck("/tags/updateTags",
			params, null, "#infoUpdateArea", function() {
				setMessage("Tags and comments updated successfully");
				refreshTagCloud();
			});
	unblockPanel();
}

/**
 * Linked to update tags button on info view, update the tags in iRODS
 */
function updateTagsAtPath(path, tags, comment) {
	
	var params = {
		absPath : path,
		tags : tags,
		comment : comment
	}

	showBlockingPanel();
	lcSendValueViaPostAndCallbackHtmlAfterErrorCheck("/tags/updateTags",
			params, null, null, function() {
				setMessage("Tags and comments updated successfully");
				refreshTagCloud();
			});
	unblockPanel();
}















