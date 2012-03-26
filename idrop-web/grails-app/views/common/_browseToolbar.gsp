<div id="detailsToolbar" style="height:100%;overflow:visible;margin-left:auto;margin-right:auto;">
<ul id="detailsToolbarMenu" class="sf-menu">
	
	<li id="menuFile"><a href="#file"><g:message code="text.file"/></a>
	<ul>
		<li id="menuShowInTree"><a href="#showInTree" onclick="showInTreeClickedFromToolbar()"><g:message code="text.show.in.tree" /></a></li>
		<li id="menuNewFolder"><a href="#newFolder" onclick="newFolderViaToolbar()"><g:message code="text.new.folder" /></a></li>
		<li id="menuRename"><a href="#rename" onclick="renameViaToolbar()"><g:message code="text.rename" /></a></li>
		<li id="menuDelete"><a href="#delete" 	onclick="deleteViaToolbar()"><g:message code="default.button.delete.label" /></a></li>
	</ul>
	</li>
	<li id="menuUploadDownload"><a href="#uploadDownload"><g:message code="text.upload.and.download"/></a>
	<ul>
		<li id="menuUpload"><a href="#upload" onclick="showUploadDialogFromInfoToolbar()"><g:message code="text.upload" /></a></li>
		<li id="menuDownload"><a href="#download" onclick="downloadAction()"><g:message code="text.download" /></a></li>
		<g:if test="${showLite}">
		<li id="menuBulkUpload"><a href="#bulkupload" onclick="showIdropLite()"><g:message code="text.bulk.upload" /></a></li>
		
		<li id="menuAddToCart"><a href="#addToCart" onclick="addToCartViaToolbar()"><g:message code="text.add.to.cart" /></a></li>
		<!--  <li id="menuQuickTransfer"><a href="#quickTransfers" onclick="showIdropLiteLocalAndIrods()"><g:message code="text.quick.transfers" /></a></li>-->
		
		</g:if>
		
	</ul>
	</li>
	<!-- <li id="menuSharing"><a href="#sharing"><g:message code="text.sharing"/></a>
	<ul>
		<li id="menuCreateTicket"><a href="#createTicket"><g:message code="text.create.ticket" /></a></li>
		<li id="menuShareWithUsers"><a href="#shareWithUsers" onclick="sharingSelectedFromToolbar()"><g:message code="text.share" /></a></li>
	</ul>
	</li>-->

	</ul>
</div>
<script>
$(function() {
	
	$("ul.sf-menu").superfish();
});

function showInTreeClickedFromToolbar() {
var path = $("#infoAbsPath").val();
 $(tabs).tabs('select', 0); // switch to home tab
	  splitPathAndPerformOperationAtGivenTreePath(path, null,
				null, function(path, dataTree, currentNode){

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


</script>