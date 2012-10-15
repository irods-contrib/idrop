<div id="topToolbar" >

	<div id="topToolbarMenu" class="btn-toolbar">
		
		<div id="menuFileDetails" class="detailsToolbarMenuItem toolbarMenuItem btn-group">
			<button id="menuRefresh" onclick="refreshTree()"><img class="icon-refresh"/><g:message
					code="text.refresh" /></button>
			<button id="menuNewFolderDetails"
					onclick="newFolderViaBrowseDetailsToolbar()"><img class="icon-plus-sign"/><g:message
							code="text.new.folder" /></button>

		</div>
		
		<div id="menuView" class="detailsToolbarMenuItem toolbarMenuItem btn-group">
			
				<button id="menuBrowseView"
					onclick="browseView()"><img class="icon-list"/><g:message
							code="text.browse" /></button>
				<button id="menuInfoView" onclick="infoView()"><img class="icon-info-sign"/> <g:message code="text.info" /></button>
				<!--  <button id="menuSharingView" 
					onclick="sharingView()"><g:message
							code="text.sharing" /></button>
				<button id="menuMetadataView"
					onclick="metadataView()"><g:message
							code="text.metadata" /></button>-->
				<button id="menuGalleryView"
					onclick="galleryView()"><img class="icon-picture"/><g:message
							code="text.gallery" /></button>
			<!--	<button id="menuAuditView"
				 	onclick="auditView()"><g:message
							code="text.audit" /></button>
				<g:if test="${grailsApplication.config.idrop.config.use.tickets==true}">
				<button id="menuTicketView"
					onclick="ticketView()"><g:message
							code="text.tickets" /></button>
				</g:if>-->
		</div>

	<!--  info toolbar -->

	<li id="menuTools" class="toolbarMenuItem"><a href="#menuToolsD"><g:message code="text.tools"/></a>
				<ul>
					<li id="menuToolsMakePublicLink"><a href="#makePublicLink" onclick="makePublicLinkAtPath()"><g:message code="text.create.public.link" /></a></li>
				</ul>
			</li>
		</ul>
</div>


<script type="text/javascript">
	var showLite = false;

	$(function() {
		$(".toolbarMenuItem").hide();
		$(".detailsToolbarMenuItem").hide();
		//$("ul.sf-menu").superfish();
	});

	function setDefaultView(view) {
		if (view == null) {
			return false;
		}

		browseOptionVal = view;

		var state = {};

		state["browseOptionVal"] = browseOptionVal;
		$.bbq.pushState(state);

	}

	/**
	 * audit view selected
	 */
	function auditView() {
		setDefaultView("audit");
		showAuditView(selectedPath);

	}

	/**
	 * browse view selected
	 */
	function browseView() {
		setDefaultView("browse");
		showBrowseView(selectedPath);

	}

	/**
	 * Show the info view
	 */
	function infoView() {
		setDefaultView("info");
		showInfoView(selectedPath);
	}

	/**
	 * Show the sharing (ACL) view
	 */
	function sharingView() {
		setDefaultView("sharing");
		showSharingView(selectedPath);
	}

	/**
	 * Show the metadata (AVU) view
	 */
	function metadataView() {
		setDefaultView("metadata");
		showMetadataView(selectedPath);
	}

	/**
	 * Show the ticket view
	 */
	function ticketView() {
		setDefaultView("ticket");
		showTicketView(selectedPath);
	}

	/**
	 * Show the gallery (photo) view
	 */
	function galleryView() {
		setDefaultView("gallery");
		showGalleryView(selectedPath);
	}

	// browse details toolbar

	/**
	 * Start a bulk action to add selected files to the shopping cart
	 */
	function addSelectedToCart() {
		answer = confirm("Add the selected files to the cart?"); //FIXME: i18n
		if (!answer) {
			return false;
		}

		addToCartBulkAction();
	}

	/**
	 * Start a bulk action to delete the selected files from the shopping cart
	 */
	function deleteSelected() {
		answer = confirm("Delete the selected files?"); //FIXME: i18n
		if (!answer) {
			return false;
		}
		deleteFilesBulkAction();
	}

	function sharingSelectedFromBrowseDetailsToolbar() {
		showSharingView(selectedPath);
	}

	// browse toolbar scripts

	function showInTreeClickedFromToolbar() {
		var path = $("#infoAbsPath").val();
		$(tabs).tabs('select', 0); // switch to home tab
		splitPathAndPerformOperationAtGivenTreePath(path, null, null, function(
				path, dataTree, currentNode) {

			$.jstree._reference(dataTree).open_node(currentNode);
			$.jstree._reference(dataTree).select_node(currentNode, true);
			// updateBrowseDetailsForPathBasedOnCurrentModel(data);

		});
	}

	function downloadAction() {
		var path = $("#infoAbsPath").val();
		downloadViaToolbar(path);
	}

	function showBulkShareDialogFromToolbar() {
		var path = $("#infoAbsPath").val();
		showBulkShareDialog(path);
	}

	function showIdropLiteFromToolbar() {
		var path = $("#infoAbsPath").val();
		showBulkShareDialog(path);
	}

	/**
	 * Show the dialog to allow upload of data using the abs path in the info pane
	 */
	function showUploadDialogFromInfoToolbar() {
		var uploadPath = $("#infoAbsPath").val();
		if (uploadPath == null) {
			showErrorMessage("No path was found to upload, application error occurred");
			return;
		}

		showUploadDialogUsingPath(uploadPath);

	}

	function sharingSelectedFromToolbar() {
		var path = $("#infoAbsPath").val();
		showSharingView(path);
	}

	/*
	* Cause a dialog to appear that has a link for a public path for the current path
	*/
	function makePublicLinkAtPath() {
		$("#browseDialogArea").html();
		var path = selectedPath;
		if (selectedPath == null) {
			return false;
		}

		// show the public link dialog
		var url = "/browse/preparePublicLinkDialog";
		var params = {
			absPath : path
		}

		lcSendValueWithParamsAndPlugHtmlInDiv(url, params, "", function(data) {
			fillInPublicLinkDialog(data);
		});
		
	}

	/*
	*Given the contents of the 'create public link' dialog, 
	*/
	function fillInPublicLinkDialog(data) {
		$("#browseDialogArea").html(data);
		$("#browseDialogArea").show("slow");
		//$("#browseDialogArea").dialog({width:500, modal:true});
	}
	
</script>