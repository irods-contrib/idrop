/**
 * Javascript for home page and data browser (refactor data browser?)
 * 
 * author: Mike Conway - DICE
 */

/**
 * Global var holds jquery ref to the dataTree
 */

/**
 * ref to the jtree
 */
var dataTree;

/**
 * root path (which could be a multi-element path, such as a home directory
 */

var baseAbsPath = "/";
var baseAbsPathAsArrayOfPathElements;

var browseOptionVal = "browse";
var selectedPath = null;
var selectedNode = null;
var fileUploadUI = null;
var aclDialogMessageSelector = "#aclDialogMessageArea";
var aclMessageAreaSelector = "#aclMessageArea";
/**
 * presets for url's
 */

var aclUpdateUrl = '/sharing/updateAcl';
var aclAddUrl = '/sharing/addAcl';
var aclAddUserDialogUrl = '/sharing/processAddAclDialog';
var aclDeleteUrl = '/sharing/deleteAcl';
var aclTableLoadUrl = '/sharing/renderAclDetailsTable';
var idropLiteUrl = '/idropLite/appletLoader';
var thumbnailLoadUrl = '/image/generateThumbnail';

var folderAddUrl = '/file/createFolder';
var fileDeleteUrl = '/file/deleteFileOrFolder';
var deleteBulkActionUrl = '/file/deleteBulkAction';
var fileRenameUrl = '/file/renameFile';
var fileMoveUrl = '/file/moveFile';
var fileCopyUrl = '/file/copyFile';

/**
 * Initialize the tree control for the first view by issuing an ajax directory
 * browser request for the root directory.
 * 
 * @return
 */
function retrieveBrowserFirstView() {
	if (dataTree == null) {
		var url = "/browse/ajaxDirectoryListingUnderParent";
		lcSendValueAndCallbackWithJsonAfterErrorCheck(url, "dir=",
				"#dataTreeDiv", browserFirstViewRetrieved);
	}
}

/**
 * Upon initial load of data for the iRODS tree view, set up the tree control.
 * This will be called as a call back method when the initial AJAX load request
 * returns.
 * 
 * @param data
 *            ajax response from browse controller containing the JSON
 *            representation of the collections and files underneath the given
 *            path
 * @return
 */
function browserFirstViewRetrieved(data) {
	baseAbsPath = data[0].attr.absPath;
	baseAbsPathAsArrayOfPathElements = baseAbsPath.split("/");
	var parent = data['parent'];
	dataTree = $("#dataTreeDiv").jstree(
			{
				"plugins" : [ "themes", "contextmenu", "json_data", "types",
						"ui", "crrm", "dnd" ],
				"core" : {
					"initially_open" : [ baseAbsPath ]
				},
				"json_data" : {
					"data" : [ data ],

					"progressive_render" : true,
					"ajax" : {
						"url" : context
								+ "/browse/ajaxDirectoryListingUnderParent",
						"cache" : false,
						"data" : function(n) {
							dir = n.attr("id");
							return "dir=" + encodeURIComponent(dir);
						},
						"error" : function(n) {
							if (n.statusText == "success") {
								// ok
							} else {
								setMessage("Tree out of synch, refreshed...");
								refreshTree();
							}
						}
					}
				},
				"contextmenu" : {

					"items" : customMenu
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
				"dnd" : {
					"copy_modifier" : "shift"
				},
				"themes" : {
					"theme" : "default",
					"url" : context + "/css/style.css",
					"dots" : false,
					"icons" : true
				},
				"crrm" : {

				}

			});

	$("#dataTreeDiv").bind("select_node.jstree", function(e, data) {
		nodeSelected(e, data.rslt.obj);
	});

	$("#dataTreeDiv").bind("create.jstree", function(e, data) {
		nodeAdded(e, data.rslt.obj);
	});

	$("#dataTreeDiv").bind("remove.jstree", function(e, data) {
		nodeRemoved(e, data.rslt.obj);
	});

	$("#dataTreeDiv").bind("rename.jstree", function(e, data) {
		nodeRenamed(e, data);
	});

	$("#dataTreeDiv").bind("move_node.jstree", function(e, data) {
		var copy = false;
		if (data.args[3] == true) {
			copy = true;
		}

		var targetId = data.args[0].cr[0].id;
		var sourceId = data.args[0].o[0].id;
		var msg = "";
		if (copy) {
			msg += "Copy ";
		} else {
			msg += "Move ";
		}

		msg = msg + " from:" + sourceId + " to:" + targetId;

		var answer = confirm(msg); // FIXME: i18n

		if (!answer) {
			$.jstree.rollback(data.rlbk);
			return false;
		}

		// move/copy confirmed, process...

		if (copy) {
			copyFile(sourceId, targetId);
		} else {
			moveFile(sourceId, targetId);
		}

	});

	updateBrowseDetailsForPathBasedOnCurrentModel(baseAbsPath);

}

/**
 * Handling of actions and format of pop-up menu for browse tree.
 * 
 * @param node
 * @returns
 */
function customMenu(node) {

	selectedNode = node;
	selectedPath = node[0].idd;

	// The default set of all items FIXME: i18n
	var items = {
		refreshItem : { // The "refresh" menu item
			label : "Refresh",
			action : function() {
				$.jstree._reference(dataTree).refresh();
			}
		},
		renameItem : { // The "rename" menu item
			label : "Rename",
			action : function() {
				$.jstree._reference(dataTree).rename(node);
			}
		},
		deleteItem : { // The "delete" menu item
			label : "Delete",
			action : function() {

				var answer = confirm("Delete selected file?");
				if (answer) {
					$.jstree._reference(dataTree).remove(node);
				}
			}
		},
		newFolderItem : { // The "new" menu item
			label : "New Folder",
			action : function() {
				$.jstree._reference(dataTree).create(null, "inside", {
					data : name,
					state : "closed",
					attr : {
						rel : "folder"
					}
				}, function(data) {
				}, false);
			}
		},
		infoItem : { // The "info" menu item
			label : "Info",
			"separator_before" : true, // Insert a separator before the item
			action : function() {
				lcSendValueAndCallbackHtmlAfterErrorCheck(
						"/browse/fileInfo?absPath="
								+ encodeURIComponent(node[0].id), "#infoDiv",
						"#infoDiv", null);
			}
		},
		cutItem : { // The "cut" menu item
			label : "Cut",
			"separator_before" : true, // Insert a separator before the item
			action : function() {
				$.jstree._reference(dataTree).cut(node[0]);
				setMessage("File cut and placed in clipboard:" + node[0].id);
			}
		},
		copyItem : { // The "copy" menu item
			label : "Copy",
			action : function() {
				$.jstree._reference(dataTree).copy(node[0]);
				setMessage("File copied and placed in clipboard:" + node[0].id);
			}
		},
		pasteItem : { // The "paste" menu item
			label : "Paste",
			action : function() {
				$.jstree._reference(dataTree).paste(node[0]);
			}
		}

	};

	return items;
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
	selectedNode = data[0];
	updateBrowseDetailsForPathBasedOnCurrentModel(id);
}

/**
 * called when a tree node is added.
 * 
 * @param event
 *            javascript event containing a reference to the selected node
 * @return
 */
function nodeAdded(event, data) {

	var parent = $.trim(data[0].parentNode.parentNode.id);

	/*
	 * Firefox bug allows add of node outside of tree, so if my parent is
	 * 'dataTreeDiv' then rollback and set an error message
	 */

	if (parent == "dataTreeDiv") {
		$.jstree._reference(dataTree).refresh();
		setMessage("Cannot create a new folder outside of root of tree, select a tree node first");
		return false;
	}

	var name = $.trim(data[0].innerText);

	if (name == "") {
		/*
		 * If I can't find the name, this may be Firefox, which seems to behave
		 * strangely, so look for it by manipulating the node I got back
		 */
		var node = $(data[0]);
		var children = node.children();
		var target = $(children).filter("a:last-child");
		target = $(target).html();
		var pos = target.indexOf("</ins>");
		if (pos > -1) {
			name = target.substr(pos + 6);
		}
	}

	var params = {
		parent : parent,
		name : name
	}

	showBlockingPanel();

	var jqxhr = $.post(context + folderAddUrl, params,
			function(data, status, xhr) {
			}, "html").success(function(returnedData, status, xhr) {
		var continueReq = checkForSessionTimeout(returnedData, xhr);
		if (!continueReq) {
			return false;
		}
		setMessage("new folder created:" + xhr.responseText);
		data[0].id = xhr.responseText;
		updateBrowseDetailsForPathBasedOnCurrentModel(parent);
		unblockPanel();
	}).error(function(xhr, status, error) {
		setErrorMessage(xhr.responseText);
		refreshTree();
		unblockPanel();
	});
}

/**
 * called when a tree node is deleted. Toggle the node as appropriate, and if
 * necessary retrieve data from iRODS to create the children
 * 
 * @param event
 *            javascript event containing a reference to the selected node
 * @return
 */
function nodeRemoved(event, data) {
	// given the path, put in the node data
	var id = data[0].id;

	var params = {
		absPath : id
	}

	showBlockingPanel();

	var jqxhr = $.post(context + fileDeleteUrl, params,
			function(data, status, xhr) {
			}, "html").success(function(returnedData, status, xhr) {
		var continueReq = checkForSessionTimeout(returnedData, xhr);
		if (!continueReq) {
			return false;
		}
		setMessage("file deleted:" + id);
		selectedPqth = xhr.responseText;
		updateBrowseDetailsForPathBasedOnCurrentModel(selectedPath);
		unblockPanel();
	}).error(function(xhr, status, error) {
		setErrorMessage(xhr.responseText);
		refreshTree();
		unblockPanel();
	});
}

/**
 * called when a tree node is renamed. Rename the file in iRODS
 * 
 * @param event
 *            javascript event containing a reference to the selected node
 * @return
 */
function nodeRenamed(event, data) {
	// given the path, put in the node data

	var newName = data.rslt.new_name;
	var prevAbsPath = data.rslt.obj[0].id;

	var params = {
		prevAbsPath : prevAbsPath,
		newName : newName
	}

	showBlockingPanel();

	var jqxhr = $.post(context + fileRenameUrl, params,
			function(data, status, xhr) {
			}, "html").success(function(returnedData, status, xhr) {
		var continueReq = checkForSessionTimeout(returnedData, xhr);
		if (!continueReq) {
			return false;
		}
		var nodeRenamedTo = xhr.responseText + "/" + newName;
		setMessage("file renamed to:" + nodeRenamedTo);
		selectedPath = nodeRenamedTo;
		data.rslt.obj[0].id = nodeRenamedTo;
		data.rslt.obj[0].abspath = nodeRenamedTo;
		// refresh this node
		$.jstree._reference(dataTree).refresh(data.rslt.obj[0]);
		updateBrowseDetailsForPathBasedOnCurrentModel(nodeRenamedTo);
		unblockPanel();
	}).error(function(xhr, status, error) {
		setErrorMessage(xhr.responseText);
		refreshTree();
		unblockPanel();
	});

}

/**
 * Given a source and target absolute path, do a move
 * 
 * @param sourcePath
 * @param targetPath
 */
function moveFile(sourcePath, targetPath) {

	if (sourcePath == null || targetPath == null) {
		alert("cannot move, source and target path must be specified"); // FIXME:
		// i18n
		return;
	}

	var params = {
		sourceAbsPath : sourcePath,
		targetAbsPath : targetPath
	}

	showBlockingPanel();

	var jqxhr = $.post(context + fileMoveUrl, params,
			function(data, status, xhr) {
			}, "html").success(function(returnedData, status, xhr) {
		var continueReq = checkForSessionTimeout(returnedData, xhr);
		if (!continueReq) {
			return false;
		}
		setMessage("file moved to:" + xhr.responseText);
		selectedPath = targetPath;
		refreshTree();
		updateBrowseDetailsForPathBasedOnCurrentModel(targetPath);
		unblockPanel();
	}).error(function(xhr, status, error) {
		setErrorMessage(xhr.responseText);
		refreshTree();
		unblockPanel();
	});
}

/**
 * Given a source and target absolute path, do a copy
 * 
 * @param sourcePath
 * @param targetPath
 */
function copyFile(sourcePath, targetPath) {

	if (sourcePath == null || targetPath == null) {
		alert("cannot copy, source and target path must be specified"); // FIXME:
		// i18n
		return;
	}

	var params = {
		sourceAbsPath : sourcePath,
		targetAbsPath : targetPath
	}

	showBlockingPanel();

	var jqxhr = $.post(context + fileCopyUrl, params,
			function(data, status, xhr) {
			}, "html").success(function(returnedData, status, xhr) {
		var continueReq = checkForSessionTimeout(returnedData, xhr);
		if (!continueReq) {
			return false;
		}
		setMessage("file copied to:" + xhr.responseText);
		unblockPanel();
		selectTreePathFromIrodsPath(xhr.responseText);
		// refreshTree();
		// updateBrowseDetailsForPathBasedOnCurrentModel(targetPath);

	}).error(function(xhr, status, error) {
		setErrorMessage(xhr.responseText);
		refreshTree();
		unblockPanel();
	});
}

/**
 * On selection of a browser mode (from the top bar of the browse view), set the
 * option such that selected directories in the tree result in the given view in
 * the right hand pane
 */
function setBrowseMode() {
	browseOptionVal = $("#browseDisplayOption").val();
	updateBrowseDetailsForPathBasedOnCurrentModel(selectedPath);
}

/**
 * Upon selection of a collection or data object from the tree, display the
 * content on the right-hand side. The type of detail shown is contingent on the
 * 'browseOption' that is set in the drop-down above the browse area.
 */
function updateBrowseDetailsForPathBasedOnCurrentModel(absPath) {

	if (absPath == null) {
		absPath = baseAbsPath;
	}

	setPathCrumbtrail(absPath);

	if (browseOptionVal == null) {
		browseOptionVal = "info";
	}

	if (browseOptionVal == "browse") {
		showBrowseView(absPath);
	} else if (browseOptionVal == "info") {
		showInfoView(absPath);
	} else if (browseOptionVal == "gallery") {
		showGalleryView(absPath);
	} else if (browseOptionVal == "metadata") {
		showMetadataView(absPath);
	} else if (browseOptionVal == "sharing") {
		showSharingView(absPath);
	} else if (browseOptionVal == "audit") {
		lcSendValueAndCallbackHtmlAfterErrorCheckPreserveMessage(
				"/audit/auditList?absPath=" + encodeURIComponent(absPath),
				"#infoDiv", "#infoDiv", null);
	}
}

/**
 * Show the browse view
 * 
 * @param absPath
 *            absolute path to browse to
 */
function showBrowseView(absPath) {
	if (absPath == null) {
		absPath = baseAbsPath;
	}
	try {

		lcSendValueAndCallbackHtmlAfterErrorCheckThrowsException(
				"/browse/displayBrowseGridDetails?absPath="
						+ encodeURIComponent(absPath),
				"#infoDiv",
				function(data) {
					$("#infoDiv").html(data);
				},
				function() {
					setMessage("Unable to browse to location, please refresh the tree");
				});
	} catch (err) {
		// tree is out of synch, refresh it
		refreshTree();
	}

}

/**
 * Show the sharing view
 * 
 * @param absPath
 * @returns {Boolean}
 */
function showSharingView(absPath) {
	if (absPath == null) {
		absPath = baseAbsPath;
	}
	lcSendValueAndCallbackHtmlAfterErrorCheckPreserveMessage(
			"/sharing/showAclDetails?absPath=" + encodeURIComponent(absPath),
			"#infoDiv", "#infoDiv", null);
}

/**
 * Show the metadata view
 * 
 * @param absPath
 * @returns {Boolean}
 */
function showMetadataView(absPath) {
	if (absPath == null) {
		absPath = baseAbsPath;
	}
	lcSendValueAndCallbackHtmlAfterErrorCheckPreserveMessage(
			"/metadata/showMetadataDetails?absPath="
					+ encodeURIComponent(absPath), "#infoDiv", "#infoDiv", null);
}

/**
 * Show the info view
 * 
 * @param absPath
 * @returns {Boolean}
 */
function showInfoView(absPath) {
	if (absPath == null) {
		absPath = baseAbsPath;
	}
	lcSendValueAndCallbackHtmlAfterErrorCheckPreserveMessage(
			"/browse/fileInfo?absPath=" + encodeURIComponent(absPath),
			"#infoDiv", "#infoDiv", null);
}

/**
 * Show the gallery view
 * 
 * @param absPath
 * @returns {Boolean}
 */
function showGalleryView(absPath) {
	if (absPath == null) {
		absPath = baseAbsPath;
	}
	lcSendValueAndCallbackHtmlAfterErrorCheckPreserveMessage(
			"/browse/galleryView?absPath=" + encodeURIComponent(absPath),
			"#infoDiv", "#infoDiv", null);
}

/**
 * Show the dialog to allow upload of data
 */
function showUploadDialog() {
	if (selectedPath == null) {
		alert("No path was selected, use the tree to select an iRODS collection to upload the file to");
		return;
	}

	showUploadDialogUsingPath(selectedPath);

}

/**
 * Show the dialog to upload from the browse details view
 */
function showBrowseDetailsUploadDialog() {
	var path = $("#browseDetailsAbsPath").val();
	showUploadDialogUsingPath(path);
}

/**
 * Show the dialog to allow upload of data
 * 
 * @param path
 *            path of collection to upload to
 */
function showUploadDialogUsingPath(path) {

	if (path == null) {
		setErrorMessage("No path was selected, use the tree to select an iRODS collection to upload the file to");
		return;
	}

	var url = "/file/prepareUploadDialog";
	var params = {
		irodsTargetCollection : path
	}

	lcSendValueWithParamsAndPlugHtmlInDiv(url, params, "", function(data) {
		fillInUploadDialog(data);
	});
}

/**
 * On load of upload dialog, this will be called when the pre-set data is
 * available
 * 
 * @param data
 */
function fillInUploadDialog(data) {

	if (data == null) {
		return;
	}

	$('#uploadDialog').remove();

	var $dialog = $('<div id="uploadDialog"></div>').html(data).dialog({
		autoOpen : false,
		modal : true,
		width : 500,
		title : 'Upload to iRODS',
		create : function(event, ui) {
			initializeUploadDialogAjaxLoader();
		}
	});

	$dialog.dialog('open');
}

/**
 * Create the upload dialog for web (http) uploaded.
 */
function initializeUploadDialogAjaxLoader() {

	if (fileUploadUI != null) {
		$("#fileUploadForm").remove;
	}

	fileUploadUI = $('#uploadForm')
			.fileUploadUI(
					{
						uploadTable : $('#files'),
						downloadTable : $('#files'),

						buildUploadRow : function(files, index) {
							$("#upload_message_area").html("");
							$("#upload_message_area").removeClass();
							return $('<tr><td>'
									+ files[index].name
									+ '<\/td>'
									+ '<td class="file_upload_progress"><div><\/div><\/td>'
									+ '<\/tr>');
						},
						buildDownloadRow : function(file) {
							return $('<tr><td>' + file.name + '<\/td><\/tr>');
						},
						onComplete : function(event, files, index, xhr, handler) {
							setMessage("Upload complete");

							/*
							 * Find the uploaded file name, refresh the parent
							 * node and select the file If for some reason a
							 * psychotic quirk won't let me do this, just reload
							 * the parent
							 */

							/*
							 * if (files.length == 1) { var uploadFileName =
							 * files[0].name; // windows (ie) will put the whole
							 * file path in the name, so just reload the parent
							 * 
							 * if (uploadFileName.indexOf("\\") > -1) {
							 * reloadAndSelectTreePathBasedOnIrodsAbsolutePath(selectedPath);
							 * updateBrowseDetailsForPathBasedOnCurrentModel(selectedPath); }
							 * else { uploadFileName = selectedPath + "/" +
							 * uploadFileName;
							 * reloadAndSelectTreePathBasedOnIrodsAbsolutePath(selectedPath);
							 * updateBrowseDetailsForPathBasedOnCurrentModel(uploadFileName); } }
							 * else {
							 * reloadAndSelectTreePathBasedOnIrodsAbsolutePath(selectedPath);
							 * updateBrowseDetailsForPathBasedOnCurrentModel(selectedPath); }
							 */
							var uploadParent = $("#collectionParentName").val();

							if (uploadParent != null) {
								reloadAndSelectTreePathBasedOnIrodsAbsolutePath(uploadParent);
								updateBrowseDetailsForPathBasedOnCurrentModel(uploadParent);
							} else {
								reloadAndSelectTreePathBasedOnIrodsAbsolutePath(selectedPath);
								updateBrowseDetailsForPathBasedOnCurrentModel(selectedPath);
							}

							$('#uploadDialog').dialog('close');
							$('#uploadDialog').remove();

						},
						onError : function(event, files, index, xhr, handler) {
							setErrorMessage(xhr.responseText);
						}
					});

}

/**
 * Called by data table upon submit of an acl change
 */
function aclUpdate(value, settings, userName) {

	if (selectedPath == null) {
		throw "no collection or data object selected";
	}

	lcShowBusyIconInDiv(messageAreaSelector);

	var params = {
		absPath : selectedPath,
		acl : value,
		userName : userName
	}

	var jqxhr = $.post(context + aclAddUrl, params,
			function(data, status, xhr) {
				lcClearDivAndDivClass(messageAreaSelector);
			}, "html").error(function(xhr, status, error) {
		setErrorMessage(xhr.responseText);
	}).complete(function() {
		setMessage("File sharing update successful");
	});

	return value;

}

/**
 * Prepare the dialog to allow create of ACL data
 */
function prepareAclDialog(isNew) {

	if (selectedPath == null) {
		setErrorMessage("No path is selected, Share cannot be set");
		return;
	}

	if (isNew == null) {
		isNew = true;
	}

	var url = "/sharing/prepareAclDialog";
	var params = {
		absPath : selectedPath,
		create : true
	}

	lcSendValueWithParamsAndPlugHtmlInDiv(url, params, "", function(data) {
		showAclDialog(data);
	});

}

/**
 * The ACL dialog is prepared and ready to display as a JQuery dialog box, show
 * it
 * 
 * @param data
 */
function showAclDialog(data) {
	$("#aclDetailsArea").hide("slow");
	$("#aclDialogArea").html(data).show("slow");
	var mySource = context + "/sharing/listUsersForAutocomplete";
	$("#userName").autocomplete({
		minLength : 3,
		source : mySource
	});

}

/**
 * Submit the ACL dialog form to create a new ACL. The dialog will either have a
 * single user name, or it may have a pick list to send. If it has neither, it's
 * an error.
 */
function submitAclDialog() {

	var permissionVal = $('[name=acl]').val();
	if (permissionVal == null || permissionVal == "" || permissionVal == "NONE") {
		setErrorMessage("Please select a permission value in the drop-down");
		return false;
	}

	if (selectedPath == null) {
		setErrorMessage("no collection or data object selected");
		return false;
	}

	// see if there is form data (users in a pick list) that are selected
	var formData = $("#userDialogForm").serializeArray();

	if (formData == null) {
		setErrorMessage("Please select a user to share data with");
		return false;
	}

	var isCreate = $('[name=isCreate]').val();

	showBlockingPanel();

	var jqxhr = $.post(context + aclAddUserDialogUrl, formData,
			function(data, status, xhr) {
			}, "html").success(function(data, status, xhr) {
		var continueReq = checkForSessionTimeout(data, xhr);
		if (!continueReq) {
			return false;
		}

		reloadAclTable();
		closeAclAddDialog();
		setMessage("Sharing permission saved successfully"); // FIXME:
		// i18n
		unblockPanel();

	}).error(function(xhr, status, error) {
		setErrorMessage(xhr.responseText);
		unblockPanel();
	});
}

/**
 * Close the dialog for adding ACL's, reshow the acl details area
 */
function closeAclAddDialog() {
	try {
		$("#aclDialogArea").hide("slow", new function() {
			$("#aclDialogArea").html("");
			$("#aclDetailsArea").css('display', 'block');
			$("#aclDetailsArea").show("slow");
		});

	} catch (e) {

	}

}

/**
 * Retrieve the Acl information from iRODS for the given path as an HTML table,
 * this will subsequently be turned into a JTable.
 * 
 * Note that this clears the acl message area, so it should be called before
 * setting any message if used in any methods that update the acl area.
 * 
 * @param absPath
 */
function reloadAclTable(absPath) {

	lcClearDivAndDivClass(aclMessageAreaSelector);

	$("#aclTableDiv").empty();
	lcShowBusyIconInDiv("#aclTableDiv");

	var params = {
		absPath : selectedPath
	}

	var jqxhr = $.get(context + aclTableLoadUrl, params,
			function(data, status, xhr) {

			}, "html").error(function(xhr, status, error) {
		setErrorMessage(xhr.responseText);
	}).success(function(data, status, xhr) {
		var continueReq = checkForSessionTimeout(data, xhr);
		if (!continueReq) {
			return false;
		}
		$('#aclTableDiv').html(data);
		buildAclTableInPlace();
	});

}

/**
 * Given an acl details html table, wrap it in a jquery dataTable
 */
function buildAclTableInPlace() {
	dataTable = lcBuildTableInPlace("#aclDetailsTable", null, null);

	$('.forSharePermission', dataTable.fnGetNodes()).editable(
			function(value, settings) {
				var userName = this.parentNode.getAttribute('id');
				return aclUpdate(value, settings, userName);
			}, {
				"callback" : function(sValue, y) {
					var aPos = dataTable.fnGetPosition(this);
					dataTable.fnUpdate(sValue, aPos[0], aPos[1]);
				},
				'data' : "{'OWN':'OWN','READ':'READ','WRITE':'WRITE'}",
				'type' : 'select',
				'submit' : 'OK',
				'cancel' : 'Cancel',
				'onblur' : 'ignore',
				'indicator' : 'Saving'
			});
}

/**
 * Deprecated
 * 
 * @param userName
 * @param permission
 */
function addRowToAclDetailsTable(userName, permission) {
	var idxs = $("#aclDetailsTable")
			.dataTable()
			.fnAddData(
					[
							"<input id=\"selectedAcl\" type=\"checkbox\" name=\"selectedAcl\">",
							userName, permission ], true);
	var newNode = $("#aclDetailsTable").dataTable().fnGetNodes()[idxs[0]];
	$(newNode).attr("id", userName);
}

/**
 * Delete share selected in details dialog toolbar, send the data to delete the
 * selected elements
 */
function deleteAcl() {

	if (!confirm('Are you sure you want to delete?')) {
		setMessage("Delete cancelled"); // FIXME:
		// i18n
		return;
	}

	var formFields = $("#aclDetailsForm").serializeArray();
	var pathInfo = new Object();
	pathInfo.name = "absPath";
	pathInfo.value = selectedPath;

	formFields.push(pathInfo);

	var jqxhr = $.post(context + aclDeleteUrl, formFields,
			function(data, status, xhr) {

			}, "html").error(function(xhr, status, error) {
		setErrorMessage(xhr.responseText);
	}).success(function(data, status, xhr) {
		var continueReq = checkForSessionTimeout(data, xhr);
		if (!continueReq) {
			return false;
		}
		reloadAclTable();
		setMessage("Delete successful"); // FIXME:
		// i18n
	});
}

function buildFormFromACLDetailsTable() {
	var formData = $("#aclDetailsForm").serializeArray();
	formData.push({
		name : 'absPath',
		value : selectedPath
	});
	return formData;
}

/**
 * Close the iDrop lite applet area
 */
function closeApplet() {
	$("#idropLiteArea").animate({
		height : 'hide'
	}, 'slow');
	$("#toggleHtmlArea").show('slow');
	$("#toggleHtmlArea").height = "100%";
	$("#toggleHtmlArea").width = "100%";
	dataLayout.resizeAll();
	$("#idropLiteArea").empty();
	reloadAndSelectTreePathBasedOnIrodsAbsolutePath(selectedPath);
}

/**
 * Display the iDrop lite gui, passing in the given irods base collection name
 */
function showIdropLite() {

	var myPath = selectedPath;
	if (selectedPath == null) {
		myPath = "/";
	}

	showIdropLiteGivenPath(myPath, 2);
}

/**
 * Display the iDrop lite gui, passing in the given irods base collection name
 */
function showIdropLiteLocalAndIrods() {

	var myPath = selectedPath;
	if (selectedPath == null) {
		myPath = "/";
	}

	showIdropLiteGivenPath(myPath, 1);
}

/**
 * Display the iDrop lite gui, passing in the given irods base collection name,
 * from the browseDetails view
 */
function showBrowseDetailsIdropLite() {

	var path = selectedPath;// $("#browseDetailsAbsPath").val();

	if (path == null) {
		path = "/";
	}

	showIdropLiteGivenPath(path, 2);
}

/**
 * Display the iDrop lite gui, displaying in a local tree/irods tree view
 * (display mode 1)
 */
function showBrowseDetailsIdropLiteLocalAndIrods() {

	var path = selectedPath;

	if (path == null) {
		path = "/";
	}
	showIdropLiteGivenPath(path, 1);
}

/**
 * Given a path which is the parent collection to display in iDrop lite, show
 * the iDrop-lite applet
 * 
 * @param path
 *            parent path to which files will be uploaded in iDrop-lite
 * @param displayMode
 *            1=local/irods tree, 2=bulk upload
 */
function showIdropLiteGivenPath(path, displayMode) {
	var idropLiteSelector = "#idropLiteArea";
	if (path == null) {
		alert("No path was selected, use the tree to select an iRODS collection to upload the file to");
		return;
	}

	// close the shopping cart mode if open
	closeShoppingCartApplet();

	// first hide Browse Data Details table
	$("#toggleHtmlArea").hide('slow');
	$("#toggleHtmlArea").width = "0%";
	$("#toggleHtmlArea").height = "0%";

	lcShowBusyIconInDiv(idropLiteSelector);
	setMessage("This will launch the iDrop Lite applet, it may take a minute for the applet to load, please be patient");

	var params = {
		absPath : path
	}

	var jqxhr = $
			.post(context + idropLiteUrl, params, function(data, status, xhr) {
				lcClearDivAndDivClass(idropLiteSelector);
			}, "html")
			.error(function(xhr, status, error) {

				setErrorMessage(xhr.responseText);

			})
			.success(
					function(data, status, xhr) {

						var continueReq = checkForSessionTimeout(data, xhr);
						if (!continueReq) {
							return false;
						}
						var dataJSON = jQuery.parseJSON(data);
						var appletDiv = $("#idropLiteArea");
						$(appletDiv)
								.append(
										"<div id='appletMenu' class='fg-buttonset fg-buttonset-single' style='float:none'><button type='button' id='toggleMenuButton' class='ui-state-default ui-corner-all' value='closeIdropApplet' onclick='closeApplet()')>Close iDrop Lite</button></div>")
						var appletTagDiv = document.createElement('div');
						appletTagDiv.setAttribute('id', 'appletTagDiv');
						var a = document.createElement('applet');
						appletTagDiv.appendChild(a);
						a.setAttribute('code', dataJSON.appletCode);
						// a.setAttribute('codebase',
						// 'http://iren-web.renci.org/idrop-web/applet');//dataJSON.appletUrl);
						a.setAttribute('codebase', dataJSON.appletUrl);
						a.setAttribute('archive', dataJSON.archive);
						a.setAttribute('width', 700);
						a.setAttribute('height', 600);
						var p = document.createElement('param');
						p.setAttribute('name', 'mode');
						p.setAttribute('value', dataJSON.mode);
						a.appendChild(p);
						p = document.createElement('param');
						p.setAttribute('name', 'host');
						p.setAttribute('value', dataJSON.host);
						a.appendChild(p);
						p = document.createElement('param');
						p.setAttribute('name', 'port');
						p.setAttribute('value', dataJSON.port);
						a.appendChild(p);
						p = document.createElement('param');
						p.setAttribute('name', 'zone');
						p.setAttribute('value', dataJSON.zone);
						a.appendChild(p);
						p = document.createElement('param');
						p.setAttribute('name', 'user');
						p.setAttribute('value', dataJSON.user);
						a.appendChild(p);
						p = document.createElement('param');
						p.setAttribute('name', 'password');
						p.setAttribute('value', dataJSON.password);
						a.appendChild(p);
						p = document.createElement('param');
						p.setAttribute('name', 'absPath');
						p.setAttribute('value', dataJSON.absolutePath);
						a.appendChild(p);
						p = document.createElement('param');
						p.setAttribute('name', 'uploadDest');
						p.setAttribute('value', dataJSON.absolutePath);
						a.appendChild(p);
						p = document.createElement('param');
						p.setAttribute('name', 'defaultStorageResource');
						p
								.setAttribute('value',
										dataJSON.defaultStorageResource);
						a.appendChild(p);
						p = document.createElement('param');
						p.setAttribute('name', 'displayMode');
						p.setAttribute('value', displayMode);
						a.appendChild(p);
						appletDiv.append(appletTagDiv);

						$("#idropLiteArea").removeAttr('style');

					}).error(function(xhr, status, error) {
				setErrorMessage(xhr.responseText);
			});

}

/**
 * Ask for a thumbnail image for a selected path to be displayed on an info
 * panel. These use a consistent naming scheme for the various divs and data
 * elements.
 */
function requestThumbnailImageForInfoPane() {
	var absPath = $("#infoAbsPath").val();
	absPath = encodeURIComponent(absPath);
	var url = scheme + "://" + host + ":" + port + context + thumbnailLoadUrl
			+ "?absPath=" + absPath;
	var oImg = document.createElement("img");
	oImg.setAttribute('src', url);
	oImg.setAttribute('alt', 'na');
	oImg.setAttribute('class', 'thumb');
	$("#infoThumbnailLoadArea").append(oImg);

}

/**
 * Refresh the browse tree
 */
function refreshTree() {
	$.jstree._reference(dataTree).refresh();
}

/**
 * The download button has been selected from an info view, download the given
 * file
 */
function downloadViaToolbar() {
	var infoAbsPath = $("#infoAbsPath").val();
	window.open(context + '/file/download/' + infoAbsPath, '_self');

}

/**
 * The rename button has been selected from an info view, show the rename dialog
 */
function renameViaToolbar() {
	var infoAbsPath = $("#infoAbsPath").val();
	renameViaToolbarGivenPath(infoAbsPath);
}

/**
 * The rename button has been selected from the browse details view, show the
 * rename dialog
 */
function renameViaBrowseDetailsToolbar() {
	// var path = $("#browseDetailsAbsPath").val();
	renameViaToolbarGivenPath(selectedPath);
}

/**
 * Given a path for the file/collection to be renamed, show the rename dialog
 * 
 * @param path
 */
function renameViaToolbarGivenPath(path) {

	if (path == null) {
		setErrorMessage("No path was selected, use the tree to select an iRODS collection or file to rename"); // FIXME:
		// i18n
		return;
	}

	lcShowBusyIconInDiv("#infoDialogArea");
	var url = "/browse/prepareRenameDialog";

	var params = {
		absPath : path
	}

	lcSendValueWithParamsAndPlugHtmlInDiv(url, params, "#infoDialogArea", null);

}

/**
 * Delete was selected on the toolbar
 */
function deleteViaToolbar() {

	var infoDivAbsPath = $("#infoAbsPath").val();

	if (infoDivAbsPath != null) {
		deleteViaToolbarGivenPath(infoDivAbsPath);
	}

}

/**
 * Delete was selected on the browse details toolbar
 */
function deleteViaBrowseDetailsToolbar() {
	deleteViaToolbarGivenPath(selectedPath);
}

/**
 * Delete was selected from a toolbar, process given theabsolute path to delete
 * 
 * @param path
 *            absolute path to delete
 */
function deleteViaToolbarGivenPath(path) {

	if (path == null) {
		setErrorMessage("No path was selected, use the tree to select an iRODS collection or file to delete"); // FIXME:
		// i18n
		return false;
	}

	var answer = confirm("Delete selected file?"); // FIXME: i18n

	if (answer) {

		showBlockingPanel();

		var params = {
			absPath : path
		}
		var jqxhr = $
				.post(context + fileDeleteUrl, params,
						function(data, status, xhr) {
						}, "html")
				.success(
						function(returnedData, status, xhr) {
							var continueReq = checkForSessionTimeout(
									returnedData, xhr);
							if (!continueReq) {
								return false;
							}

							setMessage("file deleted:" + xhr.responseText);

							$("#infoDiv").html("<h2>File Deleted</h2>");

							/*
							 * delete the node from the tree, select the parent
							 * node and update the display to the parent node
							 */
							splitPathAndPerformOperationAtGivenTreePath(
									path,
									null,
									null,
									function(treePath, tree, currentNode) {
										// get the parent node
										var parent = $.jstree._reference(
												dataTree)._get_parent(
												currentNode);
										if (parent == null) {
											refreshTree();
											return false;
										}
										// remove node..

										$.jstree._reference(dataTree)
												._get_parent(currentNode);
										// $.jstree._reference(dataTree).remove(
										// currentNode);

										var parent = $.jstree._reference(
												dataTree).refresh(parent);
										selectedPath = xhr.responseText;
										updateBrowseDetailsForPathBasedOnCurrentModel(selectedPath);

									});

							unblockPanel();

						}).error(function(xhr, status, error) {
					refreshTree();
					setErrorMessage(xhr.responseText);
					unblockPanel();
				});
	}

}

/**
 * new folder was selected from the toolbar
 */
function newFolderViaToolbar() {
	var infoAbsPath = $("#infoAbsPath").val();
	newFolderViaToolbarGivenPath(infoAbsPath);
}

/**
 * new folder was selected from the browse details toolbar
 */
function newFolderViaBrowseDetailsToolbar() {
	newFolderViaToolbarGivenPath(selectedPath);
}

/**
 * Given a path, show the new folder dialog
 * 
 * @param path
 *            path for the parent of the new folder.
 */
function newFolderViaToolbarGivenPath(path) {

	if (path == null) {
		alert("No path was selected, use the tree to select an iRODS collection to upload the file to");
		return;
	}

	lcShowBusyIconInDiv("#infoDialogArea");
	var url = "/browse/prepareNewFolderDialog";

	var params = {
		absPath : path
	}

	lcSendValueWithParamsAndPlugHtmlInDiv(url, params, "#infoDialogArea", null);
}

/**
 * Close the rename dialog that would have been opened by pressing the 'rename'
 * button on the toolbar.
 */
function closeRenameDialog() {
	$("#renameDialog").dialog('close');
	$("#renameDialog").remove();
}

/**
 * Close the ne folder dialog that would have been opened by pressing the 'new
 * folder' button on the toolbar.
 */
function closeNewFolderDialog() {
	$("#newFolderDialog").dialog('close');
	$("#newFolderDialog").remove();
}

/**
 * Process a rename operation requested from the toolbar by processing the
 * submitted rename dialog
 */
function submitRenameDialog() {
	lcClearDivAndDivClass("#renameDialogMessageArea");
	var absPath = $("#renameDialogAbsPath").val();
	var newName = $.trim($("#fileName").val());
	// name must be entered
	if (newName == null || newName.length == 0) {
		setMessage("Please enter a new name");
		return;
	}

	var params = {
		prevAbsPath : absPath,
		newName : newName
	}

	showBlockingPanel();

	var jqxhr = $.post(context + fileRenameUrl, params,
			function(data, status, xhr) {
			}, "html").success(
			function(returnedData, status, xhr) {
				var continueReq = checkForSessionTimeout(returnedData, xhr);
				if (!continueReq) {
					return false;
				}
				setMessage("file renamed to:" + xhr.responseText);
				selectedPath = xhr.responseText;
				closeRenameDialog();
				// refreshTree();
				reloadAndSelectTreePathBasedOnIrodsAbsolutePath(selectedPath);
				// selectTreePathFromIrodsPath(selectedPath);
				updateBrowseDetailsForPathBasedOnCurrentModel(selectedPath
						+ "/" + newName);
				unblockPanel();
			}).error(function(xhr, status, error) {
		refreshTree();
		setErrorMessage(xhr.responseText);
		unblockPanel();
	});

}

/**
 * Process a new folder operation requested from the toolbar by processing the
 * submitted new folder dialog
 */
function submitNewFolderDialog() {

	lcClearDivAndDivClass("#newFolderDialogMessageArea");
	var absPath = $.trim($("#newFolderDialogAbsPath").val());
	var newName = $.trim($("#fileName").val());
	// name must be entered
	if (newName == null || newName.length == 0) {
		setErrorMessage("Please enter a new folder name");
		return;
	}

	var params = {
		parent : absPath,
		name : newName
	}

	showBlockingPanel();

	var jqxhr = $.post(context + folderAddUrl, params,
			function(data, status, xhr) {
			}, "html").success(function(returnedData, status, xhr) {
		var continueReq = checkForSessionTimeout(returnedData, xhr);
		if (!continueReq) {
			return false;
		}
		setMessage("New folder created:" + xhr.responseText);
		// selectedPath = xhr.responseText;
		closeNewFolderDialog();

		// refresh the parent node and open
		addANodeToTheParentInTheTree(absPath, newName);
		// refreshTree();
		updateBrowseDetailsForPathBasedOnCurrentModel(absPath);
		unblockPanel();

	}).error(function(xhr, status, error) {
		refreshTree();
		setErrorMessage(xhr.responseText);
		unblockPanel();
	});

}

/**
 * Delete files based on inputs in the browse details table. Note that
 * confirmation has already been provided.
 */
function deleteFilesBulkAction() {

	var formData = $("#browseDetailsForm").serializeArray();
	showBlockingPanel();

	var jqxhr = $.post(context + deleteBulkActionUrl, formData, "html")
			.success(function(returnedData, status, xhr) {
				var continueReq = checkForSessionTimeout(returnedData, xhr);
				if (!continueReq) {
					return false;
				}
				refreshTree();
				updateBrowseDetailsForPathBasedOnCurrentModel(selectedPath);
				setMessage("Delete action successful");
				unblockPanel();
			}).error(function(xhr, status, error) {
				setErrorMessage(xhr.responseText);
				unblockPanel();
			});

}

function addANodeToTheParentInTheTree(parentAbsolutePath, childRelativeName) {

	if (parentAbsolutePath == null || parentAbsolutePath.length == 0) {
		throw ("no path provided, cannot add a node to the tree");
	}

	if (childRelativeName == null || childRelativeName.length == 0) {
		throw ("no childRelativeName provided, cannot add a node to the tree");
	}

	var childAbsolutePath = parentAbsolutePath + "/" + childRelativeName;

	// find and open the parent node, then add the child to it
	splitPathAndPerformOperationAtGivenTreePath(parentAbsolutePath, null, null,
			function(path, dataTree, currentNode) {

				if ($.jstree._reference(dataTree)._is_loaded(currentNode)) {

					// parent node was already loaded, so it makes sense to add
					// the node to
					// the parent

					var icon = "folder";
					var state = "closed";
					var type = "folder";

					var attrBuf = new Object();
					attrBuf.id = childAbsolutePath;
					attrBuf.rel = type;
					attrBuf.absPath = childAbsolutePath;

					var nodeProps = new Object();
					nodeProps.data = childRelativeName;
					nodeProps.attr = attrBuf;
					nodeProps.state = state;

					$.jstree._reference(dataTree).create_node(currentNode,
							"inside", nodeProps, null, false);

				}

				// select and open this new node
				selectTreePathFromIrodsPath(parentAbsolutePath);

			});

}

/**
 * Given an iRODS absolute path to a node, find and select that node
 * 
 * @param irodsAbsolutePath
 *            irods absolute path
 */
function selectTreePathFromIrodsPath(irodsAbsolutePath) {
	if (irodsAbsolutePath == null || irodsAbsolutePath.length == 0) {
		throw "irodsAbsolutePath is missing";
	}

	selectTreePath(irodsAbsolutePath.split("/"), null, null);

}

/**
 * Find the given iRODS absolute path in the tree, clear the children and reload
 * 
 * @param path
 */
function reloadAndSelectTreePathBasedOnIrodsAbsolutePath(path) {

	if (path == null) {
		throw "No path provided";
	}

	splitPath = path.split("/");

	performOperationAtGivenTreePath(splitPath, null, null, function(thisPath,
			dataTree, currentNode) {

		$.jstree._reference(dataTree).refresh(currentNode);
		$.jstree._reference(dataTree).select_node(currentNode, true);

	});
}

/**
 * Given the tree path in the text box, recursively open the nodes in the tree
 * based on the path.
 * 
 * If called with no parameters, it assumes this is the top node, it will open
 * the top node.
 * 
 * If called with a last
 * 
 * 
 * @param lastIndex
 * @param currentNode
 * @returns {Boolean}
 */
function selectTreePath(path, currentNode, currentIndex) {

	if (currentIndex == null) {
		currentIndex = 0;
	}

	if (path == null) {
		var val = $("#searchTerm").val();
		if (val == "") {
			setMessage("enter a path to search");
			return false;
		}

		if (val.charAt(val.length - 1) == "/") {
			val = val.substring(0, val.length - 1);
		}

		// alert("select tree path:" + val);
		path = val.split("/");
	}

	performOperationAtGivenTreePath(path, null, null, function(path, dataTree,
			currentNode) {
		$.jstree._reference(dataTree).open_node(currentNode);
		$.jstree._reference(dataTree).select_node(currentNode, true);
	});
}

/**
 * among the children in the given tree node, find the node who's title is the
 * given target path
 * 
 * @param childNodes
 * @param targetPath
 * @returns
 */
function getPathInNode(childNodes, targetPath) {
	var foundChild = null;
	var nodeText = null;
	$.each(childNodes, function(index, value) {
		var theChild = $.jstree._reference(dataTree)._get_node(value);
		nodeText = $.jstree._reference(dataTree).get_text(theChild);

		if (nodeText == targetPath) {
			foundChild = theChild;
			return;
		} else if (nodeText == "/" && targetPath == "") {
			// this matches the root node
			foundChild = theChild;
			return;
		}

	});

	return foundChild;

}

/**
 * Given an iRODS absolute path in string form, find that path in the tree and
 * perform the given operation on that path. The passed in function wil be
 * called with the params(path, tree, currentNode)
 * 
 * @param path
 * @param currentNode
 * @param currentIndex
 * @param operationToPerform
 */
function splitPathAndPerformOperationAtGivenTreePath(path, currentNode,
		currentIndex, operationToPerform) {
	splitPath = path.split("/");
	performOperationAtGivenTreePath(splitPath, currentNode, currentIndex,
			operationToPerform);
}

/**
 * Given the tree path in the text box, call the given function, passing in the
 * path array, tree and the current node as arugments to the function
 * 
 * @param path
 *            array of strings for each part of the path
 * @param currentNode
 *            current tree node (especially if calling recursively, this will
 *            match the 'lastIndex' in the path
 * @param lastIndex
 *            ponter to position in tree path array that is an absolute path to
 *            the curentNode
 * @param operationToPerform
 *            a function(path, tree, currentNode) that will be called when at
 *            the end of the path
 * @returns {Boolean}
 */
function performOperationAtGivenTreePath(path, currentNode, currentIndex,
		operationToPerform) {

	if (currentIndex == null) {
		currentIndex = 0;
	}

	if (path == null) {
		var val = $("#searchTerm").val();

		// alert("select tree path:" + val);
		path = val.split("/");
	}

	// if called with no params, get the root node, open it, and process the
	// children
	if (currentNode == null) {
		currentNode = $.jstree._reference(dataTree).get_container();
		var children = $.jstree._reference(dataTree)._get_children(currentNode);
		currentNode = children[0];
		performOperationAtGivenTreePath(path, currentNode, currentIndex,
				operationToPerform);
		return;
	}

	var skip = false;
	var end = false;
	$.each(path,
			function(index, value) {
				if (skip) {
					return;
				}

				if (index < currentIndex) {
					return;
				}

				if (value == "") {
					return;
				}

				/**
				 * I might have a root that is not really '/', I could have a
				 * tree root that is a node inside of the actual iRODS tree. Use
				 * the baseAbsPathAsArrayOfPathElements to account for this
				 */
				if (baseAbsPath.length > 1
						&& index < (baseAbsPathAsArrayOfPathElements.length)) {
					return;
				}

				// } else {

				// if (value > "") {
				var loaded = $.jstree._reference(dataTree)._is_loaded(
						currentNode);
				if (!loaded) {
					skip = true;
					$.jstree._reference(dataTree)
							.open_node(
									currentNode,
									function() {
										performOperationAtGivenTreePath(path,
												currentNode, index,
												operationToPerform);
									}, false);
					return;
				} else {
					$.jstree._reference(dataTree).open_node(currentNode, false,
							false);
				}

				var children = $.jstree._reference(dataTree)._get_children(
						currentNode);
				currentNode = getPathInNode(children, value);
				if (currentNode == null) {
					setMessage("Path not found in tree, please reload");
					return false;
				} else {
					if (index == path.length - 1) {
						end = true;
					}
				}
				// }
			});

	if (currentNode != null && end) {
		operationToPerform(path, dataTree, currentNode);
	}

}

function showOverwriteOptionDialog(message) {
	/*
	 * var dialogDiv = $("#efaultDialogDiv"); dialogDiv.html("");
	 * 
	 * 
	 * var messageDiv = document.createElement('div'); var message =
	 * document.createElement('h2'); messageDiv.appendChild(message);
	 * 
	 * 
	 * var a = document.createElement('applet'); appletTagDiv.appendChild(a);
	 */

}
