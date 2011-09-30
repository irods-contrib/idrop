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
var fileUploadUI = null;
var aclDialogMessageSelector = "#aclDialogMessageArea";
var aclMessageAreaSelector = "#aclMessageArea";
/**
 * presets for url's
 */

var aclUpdateUrl = '/sharing/updateAcl';
var aclAddUrl = '/sharing/addAcl';
var aclDeleteUrl = '/sharing/deleteAcl';
var aclTableLoadUrl = '/sharing/renderAclDetailsTable';
var idropLiteUrl = '/idropLite/appletLoader';
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
 * @param data
 *            ajax response from browse controller containing the JSON
 *            representation of the collections and files underneath the given
 *            path
 * @return
 */
function browserFirstViewRetrieved(data) {
	var parent = data['parent']
	dataTree = $("#dataTreeDiv").jstree({
		"core" : {
			"initially_open" : [ parent ]
		},
		"json_data" : {
			"data" : [ data ],
			"progressive_render" : true,
			"ajax" : {
				"url" : context + "/browse/ajaxDirectoryListingUnderParent",
				"data" : function(n) {
					lcClearMessage();
					return {
						dir : n.attr ? n.attr("id") : 0
					};
				},
				"error" : function(n) {
					setMessage("error loading tree");
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

	tabs.resize();

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
	lcPrepareForCall();
	var id = data[0].id;
	selectedPath = id;
	updateBrowseDetailsForPathBasedOnCurrentModel(id);

}

/**
 * On selection of a browser mode (from the top bar of the browse view), set the
 * option such that selected directories in the tree result in the given view in
 * the right hand pane
 */
function setBrowseMode() {
	lcPrepareForCall();
	browseOptionVal = $("#browseDisplayOption").val();
	updateBrowseDetailsForPathBasedOnCurrentModel(selectedPath);
}

/**
 * Upon selection of a collection or data object from the tree, display the
 * content on the right-hand side. The type of detail shown is contingent on the
 * 'browseOption' that is set in the drop-down above the browse area.
 */
function updateBrowseDetailsForPathBasedOnCurrentModel(absPath) {

	lcPrepareForCall();

	if (absPath == null) {
		return;
	}

	if (browseOptionVal === null) {
		browseOptionVal = "info";
	}

	if (browseOptionVal == "details") {

		lcSendValueAndCallbackHtmlAfterErrorCheck(
				"/browse/displayBrowseGridDetails?absPath=" + absPath,
				"#infoDiv", "#infoDiv", function(data) {
					$("#infoDiv").html(data);

				});
	} else if (browseOptionVal == "info") {
		lcSendValueAndCallbackHtmlAfterErrorCheck("/browse/fileInfo?absPath="
				+ absPath, "#infoDiv", "#infoDiv", null);
	} else if (browseOptionVal == "metadata") {
		lcSendValueAndCallbackHtmlAfterErrorCheck(
				"/metadata/showMetadataDetails?absPath=" + absPath, "#infoDiv",
				"#infoDiv", null);
	} else if (browseOptionVal == "sharing") {
		lcSendValueAndCallbackHtmlAfterErrorCheck(
				"/sharing/showAclDetails?absPath=" + absPath, "#infoDiv",
				"#infoDiv", null);
	}
}

/**
 * Show the dialog to allow upload of data
 */
function showUploadDialog() {

	if (selectedPath == null) {
		alert("No path was selected, use the tree to select an iRODS collection to upload the file to");
		return;
	}

	var url = "/file/prepareUploadDialog";
	var params = {
		irodsTargetCollection : selectedPath
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

	lcPrepareForCall();

	if (data == null) {
		return;
	}

	$('#uploadDialog').remove();

	var $dialog = $('<div id="uploadDialog"></div>').html(data).dialog({
		autoOpen : false,
		modal : true,
		width : 400,
		title : 'Upload to iRODS',
		create : function(event, ui) {
			initializeUploadDialogAjaxLoader();
		}
	});

	$dialog.dialog('open');
}

function initializeUploadDialogAjaxLoader() {
	lcPrepareForCall();

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
									+ '<td class="file_upload_cancel">'
									+ '<button class="ui-state-default ui-corner-all" title="Cancel">'
									+ '<span class="ui-icon ui-icon-cancel">Cancel<\/span>'
									+ '<\/button><\/td><\/tr>');
						},
						buildDownloadRow : function(file) {
							return $('<tr><td>' + file.name + '<\/td><\/tr>');
						},
						onError : function(event, files, index, xhr, handler) {
							$("#upload_message_area").html(
									"an error occurred:" + xhr);
							$("#upload_message_area").addClass("message");
						}
					});

}

/**
 * Called by data table upon submit of an acl change
 */
function aclUpdate(value, settings, userName) {

	lcPrepareForCall();
	lcClearDivAndDivClass(aclMessageAreaSelector);

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
		setMessageInArea(aclDialogMessageSelector, xhr.responseText);
	}).complete(
			function() {
				setMessageInArea(messageAreaSelector,
						"File sharing update successful");
			});

	return value;

}

/**
 * Prepare the dialog to allow create of ACL data
 */
function prepareAclDialog(isNew) {

	if (selectedPath == null) {
		alert("No path is selected, Share cannot be set");
		return;
	}

	lcPrepareForCall();
	lcClearDivAndDivClass(aclMessageAreaSelector);

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

	lcPrepareForCall();
	lcClearDivAndDivClass(aclMessageAreaSelector);
	$("#aclDialogArea").html(data).fadeIn('slow');
	var mySource = context + "/sharing/listUsersForAutocomplete";
	$("#userName").autocomplete({
		minLength : 3,
		source : mySource
	});

	/**
	 * $("#aclDialogArea").html(data); $("#aclDialogArea").dialog({ "width" :
	 * 400, "modal" : true, "buttons" : { "Ok" : function() { submitAclDialog(); },
	 * "Cancel" : function() { $(this).dialog("close"); } }, "title" : "Edit
	 * Share Permission" });
	 */

}

function submitAclDialog() {

	lcPrepareForCall();
	lcClearDivAndDivClass(aclMessageAreaSelector);

	var userName = $('[name=userName]').val();
	if (userName == null || userName == "") {
		setMessageInArea(aclDialogMessageSelector,
				"Please select a user to share data with");
		return false;
	}
	var permissionVal = $('[name=acl]').val();
	if (permissionVal == null || permissionVal == "" || permissionVal == "NONE") {
		setMessageInArea(aclDialogMessageSelector,
				"Please select a permission value in the drop-down");
		return false;
	}

	if (selectedPath == null) {
		throw "no collection or data object selected";
	}

	var isCreate = $('[name=isCreate]').val();

	lcShowBusyIconInDiv(aclDialogMessageSelector);

	var params = {
		absPath : selectedPath,
		acl : permissionVal,
		userName : userName
	}

	var jqxhr = $.post(context + aclAddUrl, params,
			function(data, status, xhr) {
				lcClearDivAndDivClass(aclDialogMessageSelector);
			}, "html").error(function(xhr, status, error) {
		setMessageInArea(aclDialogMessageSelector, xhr.responseText);
	}).success(
			function(data, status, xhr) {
				/*
				 * if (isCreate) { addRowToAclDetailsTable(userName, acl);
				 * alert("adding row to table"); }
				 */
				var dataJSON = jQuery.parseJSON(data);
				if (dataJSON.response.errorMessage != null) {
					setMessageInArea("#aclMessageArea",
							dataJSON.response.errorMessage);
				} else {
					closeAclAddDialog();
					setMessageInArea("#aclMessageArea",
							"Sharing permission saved successfully"); // FIXME:
																		// i18n
					reloadAclTable(selectedPath);
				}

			}).error(function(xhr, status, error) {
		setMessageInArea(aclDialogMessageSelector, xhr.responseText);
	});
}

function closeAclAddDialog() {
	$("#aclDialogArea").fadeOut('slow', new function() {
		$("#aclDialogArea").html("")
	});

}

/**
 * Retrieve the Acl information from iRODS for the given path as an HTML table,
 * this will subsequently be turned into a JTable
 * 
 * @param absPath
 */
function reloadAclTable(absPath) {
	lcPrepareForCall();

	$("#aclTableDiv").empty();
	lcShowBusyIconInDiv("#aclTableDiv");

	var params = {
		absPath : selectedPath
	}

	var jqxhr = $.get(context + aclTableLoadUrl, params,
			function(data, status, xhr) {
				$('#aclTableDiv').html(data);
			}, "html").error(function(xhr, status, error) {
		setMessageInArea("aclMessageArea", xhr.responseText);
	}).success(function() {
		buildAclTableInPlace();
	});

}

function buildAclTableInPlace() {
	lcPrepareForCall();
	dataTable = lcBuildTableInPlace("#aclDetailsTable", null, null);
	$("#infoDiv").resize();

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
	// var nNodes = aclDetailsTable.fnGetNodes( );
	alert("adding row");
	var idxs = $("#aclDetailsTable")
			.dataTable()
			.fnAddData(
					[
							"<input id=\"selectedAcl\" type=\"checkbox\" name=\"selectedAcl\">",
							userName, permission ], true);
	var newNode = $("#aclDetailsTable").dataTable().fnGetNodes()[idxs[0]];
	$(newNode).attr("id", userName);
	alert("new node=" + newNode);
}

/**
 * Delete share selected in details dialog toolbar, send the data to delete the
 * selected elements
 */
function deleteAcl() {

	lcPrepareForCall();
	lcClearDivAndDivClass(aclMessageAreaSelector);

	if (!confirm('Are you sure you want to delete?')) {
		setMessageInArea(aclMessageAreaSelector, "Delete cancelled"); // FIXME:
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
		setMessageInArea(aclMessageAreaSelector, xhr.responseText);
	}).success(function(data) {
		reloadAclTable(selectedPath);
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

function closeApplet() {
	// $("#idropLiteArea").animate({ height: 'hide', opacity: 'hide' }, 'slow');
	$("#idropLiteArea").animate({
		height : 'hide'
	}, 'slow');
	$("#toggleBrowseDataDetailsTable").show('slow');
	$("#toggleBrowseDataDetailsTable").height = "100%";
	$("#toggleBrowseDataDetailsTable").width = "100%";
	dataLayout.resizeAll();
	$("#idropLiteArea").empty();
}

/**
 * Display the iDrop lite gui, passing in the given irods base collection name
 */
function showIdropLite() {
	// alert("showing idrop lite");
	var idropLiteSelector = "#idropLiteArea";
	var myPath = selectedPath;
	if (selectedPath == null) {
		myPath = "/";
	}

	// first hide Browse Data Details table
	$("#toggleBrowseDataDetailsTable").hide('slow');
	$("#toggleBrowseDataDetailsTable").width = "0%";
	$("#toggleBrowseDataDetailsTable").height = "0%";

	lcShowBusyIconInDiv(idropLiteSelector);

	var params = {
		absPath : myPath
	}

	var jqxhr = $
			.post(context + idropLiteUrl, params, function(data, status, xhr) {
				lcClearDivAndDivClass(idropLiteSelector);
				// $(idropLiteSelector).html(data);
			}, "html")
			.error(function(xhr, status, error) {

				setMessageInArea(idropLiteSelector, xhr.responseText);

			})
			.success(
					function(data) {

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
						a.setAttribute('width', 600);
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
						appletDiv.append(appletTagDiv);

						$("#idropLiteArea").removeAttr('style');

						// $("#idropLiteArea").animate({ height: '100%',
						// opacity: '100%' }, 'slow');

					}).error(function(xhr, status, error) {
				setMessageInArea(idropLiteSelector, xhr.responseText);
			});

}
