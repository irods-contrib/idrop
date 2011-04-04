/**
 * Javascript for home page and data browser (refactor data browser?)
 * 
 * author: Mike Conway - DICE
 */

/**
 * Global var holds jquery ref to the dataTree
 */
var dataTree;
var browseOptionVal = "details";
var selectedPath = null;

/**
 * Initialize the tree control for the first view by issuing an ajax directory
 * browser request for the root directory.
 * 
 * @return
 */
function retrieveBrowserFirstView() {
	if (dataTree == null) {
		var url = "/browse/ajaxDirectoryListingUnderParent";
		lcSendValueAndCallbackWithJsonAfterErrorCheck(url, "dir=/",
				"#dataTreeDiv", browserFirstViewRetrieved);
	} else {

	}
}

/**
 * FIXME: intercept timeouts here? Callback to initialize a browser tree for the
 * first time, set to the root node as indicated in the data
 * 
 * @param data
 *            ajax response from browse controller containing the JSON
 *            representation of the collections and files underneath the given
 *            path
 * @return
 */
function browserFirstViewRetrieved(data) {

	dataTree = $("#dataTreeDiv").jstree({
		"core" : {
			"initially_open" : [ "/" ]
		},
		"json_data" : {
			"data" : [ data ],
			"ajax" : {
				"url" : context + "/browse/ajaxDirectoryListingUnderParent",
				"data" : function(n) {
					return {
						dir : n.attr ? n.attr("id") : 0
					};
				}
			}
		},
		"types" : {
			"types" : {
				"file" : {
					"valid_children" : "none",
					"icon" : {
						"image" : context + "/images/file.png"
					}
				},
				"folder" : {
					"valid_children" : [ "default", "folder", "file" ],
					"icon" : {
						"image" : context + "/images/folder.png"
					}
				}
			}

		},
		"ui" : {
			"select_limit" : 1,
			"initially_select" : [ "phtml_2" ]
		},

		"themes" : {
			"theme" : "default",
			"url" : context + "/css/style.css",
			"dots" : false,
			"icons" : true
		},
		"plugins" : [ "json_data", "types", "ui", "crmm", "themes" ]
	});

	$("#dataTreeDiv").bind("select_node.jstree", function(e, data) {
		nodeSelected(e, data.rslt.obj);
	});

}

/**
 * Event callback when a tree node has finished loading.
 * 
 * @return
 */
function nodeLoadedCallback() {
}

/**
 * called when a tree node is selected. Toggle the node as appropriate, and if
 * necessary retrieve data from iRODS to create the children
 * 
 * @param event
 *            javascript event containing a reference to the selected node
 * @return
 */
function nodeSelected(event, data) {
	// given the path, put in the node data

	var id = data[0].id;
	selectedPath = id;
	updateBrowseDetailsForPathBasedOnCurrentModel(id);

}


/**
 * On selection of a browser mode (from the top bar of the browse view), set the option such that selected directories in the
 * tree result in the given view in the right hand pane
 */
function setBrowseMode() {
	browseOptionVal = $("#browseDisplayOption").val();
	updateBrowseDetailsForPathBasedOnCurrentModel(selectedPath);
}

/**
 * Upon selection of a collection or data object from the tree, display the content on the right-hand side.  The type of 
 * detail shown is contingent on the 'browseOption' that is set in the drop-down above the browse area.
 */
function updateBrowseDetailsForPathBasedOnCurrentModel(absPath) {
	
	if (absPath == null) {
		return;
	}
	
	if (browseOptionVal === null) { 
		browseOptionVal = "info";
	}
	
	if (browseOptionVal == "details") {
	
	lcSendValueAndCallbackHtmlAfterErrorCheck(
				"/browse/displayBrowseGridDetails?absPath=" + absPath, "#infoDiv",
					"#infoDiv", null);
	} else if (browseOptionVal == "info") {
		lcSendValueAndCallbackHtmlAfterErrorCheck(
				"/browse/fileInfo?absPath=" + absPath, "#infoDiv",
					"#infoDiv", null);
	}  else if (browseOptionVal == "metadata") {
		lcSendValueAndCallbackHtmlAfterErrorCheck(
				"/metadata/listMetadata?absPath=" + absPath, "#infoDiv",
					"#infoDiv", null);
	}
}

/**
 * Show the dialog to allow upload of data
 */
function showUploadDialog() {
	
	if (selectedPath == null) {
		return;
	}
	
	var $dialog = $('<div id="uploadDialog"></div>')
	.html('Upload to iRODS')
	.dialog({
		autoOpen: true,
		modal: true,
		title: 'Upload to iRODS'
	});
	
	fillInUploadDialog(selectedPath);

	
}


function fillInUploadDialog(absolutePath) {
	
	if (absolutePath == null) {
		return;
	}
	
	
}


