/**
 * Javascript for home page and data browser (refactor data browser?)
 * 
 * author: Mike Conway - DICE
 */

/**
 * Global var holds jquery ref to the dataTree
 */
var dataTree;

/**
 * Initialize the tree control for the first view by issuing an ajax directory
 * browser request for the root directory.
 * 
 * @return
 */
function retrieveBrowserFirstView() {
	var url = "/browse/ajaxDirectoryListingUnderParent";
	lcSendValueAndCallbackWithJsonAfterErrorCheck(url, "dir=/", "#dataTreeDiv",
			browserFirstViewRetrieved);
}

/**
 * Callback to initialize a browser tree for the first time, set to the root
 * node as indicated in the data
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
	lcSendValueAndCallbackHtmlAfterErrorCheck("/browse/fileInfo?absPath=" + id,
			"#infoDiv", "#infoDiv", null);

}

/***
 * Linked to update tags button on info view, update the tags in iRODS
 */
function updateTags() {
	var infoTagsVal = $("#infoTags").val();
	var absPathVal = $("#infoAbsPath").val();
	var params = {absPath:absPathVal, tags:infoTagsVal}
	lcSendValueViaPostAndCallbackHtmlAfterErrorCheck("/tags/updateTags", params,"#infoUpdateArea","#infoUpdateArea", function() {$("#infoUpdateArea").html("Tags updated");});
}
