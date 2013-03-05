<div id="topToolbar" >

	<div id="topToolbarMenu" class="btn-toolbar">
		
		<div id="menuFileDetails" class="btn-group">
			<button id="menuRefresh" onclick="refreshTree()"><img class="icon-refresh"/><g:message
					code="text.refresh" /></button>
			<button id="menuNewFolderDetails"
					onclick="newFolderViaBrowseDetailsToolbar()"><img class="icon-plus-sign"/><g:message
							code="text.new.folder" /></button>

		</div>
		
		<div id="menuView" class="btn-group">
			
				<button id="menuBrowseView"
					onclick="browseView()"><img class="icon-list"/><g:message
							code="text.browse" /></button>
				<button id="menuInfoView" onclick="infoView()"><img class="icon-info-sign"/> <g:message code="text.info" /></button>
				<button id="menuGalleryView"
					onclick="galleryView()"><img class="icon-picture"/><g:message
							code="text.gallery" /></button>
		</div>
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

		//var state = {};

		//state["browseOptionVal"] = browseOptionVal;
		//$.bbq.pushState(state);

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
	
</script>