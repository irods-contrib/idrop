<div id="dataObjectInfoToolbar" >

	<div id="dataObjectInfoToolbarMenu" class="btn-toolbar">
		
		<div id="dataObjectInfoButtonGroup1" class="btn-group">
			<button id="starFile" onclick="dibStarFile()"><img class="icon-star"/><g:message
					code="text.star.file" /></button>
			<button id="downloadFile" onclick="dibDownloadFile()"><img class="icon-download"/><g:message
					code="text.download" /></button>
			<button id="addToCart" onclick="dibAddToCart()"><img class="icon-shopping-cart"/><g:message
					code="text.add.to.cart" /></button>			
		</div>
		<div id="dataObjectInfoButtonGroup2" class="btn-group">
			<button id="renameDataObject" onclick="dibRenameFile()"><img class="icon-pencil"/><g:message
					code="text.rename" /></button>
			<button id="deleteDataObject" onclick="dibDeleteFile()"><img class="icon-trash"/><g:message
					code="text.delete" /></button>
		</div>
</div>

<script type="text/javascript">

function dibDownloadFile() {
	var path = $("#infoAbsPath").val();
	if (path == null) {
		showErrorMessage(jQuery.i18n.prop('msg.path.missing'));
		return false;
	}

	downloadViaToolbarGivenPath(path);
}

/**
* Delete the file currently displayed in the info view
*/
function dibRenameFile() {
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
function dibDeleteFile() {
	var path = $("#infoAbsPath").val();
	if (path == null) {
		showErrorMessage(jQuery.i18n.prop('msg.path.missing'));
		return false;
	}

	deleteViaToolbarGivenPath(path);
}

		
</script>