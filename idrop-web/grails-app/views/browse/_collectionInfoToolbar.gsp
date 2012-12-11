<div id="collectionInfoToolbar" >

	<div id="collectionInfoToolbarMenu" class="btn-toolbar">
		
		<div id="collectionInfoButtonGroup1" class="btn-group">
			<button id="setCollectionAsRoot" onclick="cibSetCollectionAsRoot()"><img class="icon-hand-left"/><g:message
					code="text.set.as.root" /></button>
					
					
			<g:if  test="${irodsStarredFileOrCollection}">
				<button id="unstarCollection" onclick="cibUnstarCollection()"><img class="icon-star-empty"/><g:message
					code="text.unstar" /></button>
			</g:if>
			<g:else>
				<button id="starCollection" onclick="cibStarCollection()"><img class="icon-star"/><g:message
					code="text.star" /></button>
			</g:else>
					
			
					
					
					
					
					
					
		</div>

		<div id="collectionInfoButtonGroup2" class="btn-group">
			<button id="addCollectionToCart" onclick="cibAddToCart()"><img class="icon-shopping-cart"/><g:message
					code="text.add.to.cart" /></button>
			<button id="uploadViaBrowser" onclick="cibUploadViaBrowser()"><img class="icon-upload"/><g:message
					code="text.upload" /></button>
			<button id="bulkUploadViaBrowser" onclick="cibBulkUploadViaBrowser()"><img class="icon-upload"/><g:message
					code="text.bulk.upload" /></button>
			
		</div>
		<div id="collectionInfoButtonGroup3" class="btn-group">
			<button id="newCollection" onclick="cibNewFolder()"><img class="icon-plus-sign"/><g:message
					code="text.new.folder" /></button>
			<button id="renameCollection" onclick="cibRenameCollection()"><img class="icon-pencil"/><g:message
					code="text.rename" /></button>
			<button id="deleteCollection" onclick="cibDeleteCollection()"><img class="icon-trash"/><g:message
					code="text.delete" /></button>
		</div>
</div>


<script type="text/javascript">

	function cibSetCollectionAsRoot() {
		var path = $("#infoAbsPath").val();
		if (path == null) {
			showErrorMessage(jQuery.i18n.prop('msg.path.missing'));
			return false;
		}

		setTreeToGivenPath(path);

		
	}

	/**
	* Show the uplaod dialog using the hidden path in the info view
	*/
	function cibUploadViaBrowser() {
		var path = $("#infoAbsPath").val();
		if (path == null) {
			showErrorMessage(jQuery.i18n.prop('msg.path.missing'));
			return false;
		}

		showUploadDialogUsingPath(path);

		
	}

	/**
	* Launch iDrop lite for bulk uplaod mode
	*/
	function cibBulkUploadViaBrowser() {
		var path = $("#infoAbsPath").val();
		if (path == null) {
			showErrorMessage(jQuery.i18n.prop('msg.path.missing'));
			return false;
		}

		showIdropLiteGivenPath(path, 2);
	}

	/**
	* Create a new folder underneath the current directory
	**/
	function cibNewFolder() {
		var path = $("#infoAbsPath").val();
		if (path == null) {
			showErrorMessage(jQuery.i18n.prop('msg.path.missing'));
			return false;
		}
		newFolderViaToolbarGivenPath(path);
	}

	/**
	* Delete the collection currently displayed in the info view
	*/
	function cibRenameCollection() {
		var path = $("#infoAbsPath").val();
		if (path == null) {
			showErrorMessage(jQuery.i18n.prop('msg.path.missing'));
			return false;
		}

		renameViaToolbarGivenPath(path);
	}

	
	/**
	* call delete using the path in the info panel
	*/
	function cibDeleteCollection() {
		var path = $("#infoAbsPath").val();
		if (path == null) {
			showErrorMessage(jQuery.i18n.prop('msg.path.missing'));
			return false;
		}

		deleteViaToolbarGivenPath(path);
	}
	
</script>