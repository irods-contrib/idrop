<div id="detailsToolbar" style="height:100%;overflow:visible;margin-left:auto;margin-right:auto;">
<ul id="detailsToolbarMenu" class="sf-menu">
	
	<li id="menuFileDetails"><a href="#file"><g:message code="text.file"/></a>
	<ul>
		<li id="menuNewFolderDetails"><a href="#newFolderDetails" onclick="newFolderViaBrowseDetailsToolbar()"><g:message code="text.new.folder" /></a></li>
		<li id="menuRenameDetails"><a href="#renameDetails" onclick="renameViaBrowseDetailsToolbar()"><g:message code="text.rename" /></a></li>
		<li id="menuDeleteDetails"><a href="#deleteDetails" 	onclick="deleteViaBrowseDetailsToolbar()"><g:message code="default.button.delete.label" /></a></li>
	</ul>
	</li>
	<li id="menuUploadDownloadDetails"><a href="#uploadDownloadDetails"><g:message code="text.upload.and.download"/></a>
	<ul>
		<li id="menuUploadDetails"><a href="#uploadDetails" onclick="showBrowseDetailsUploadDialog()"><g:message code="text.upload" /></a></li>
		<g:if test="${showLite}">
		<li id="menuBulkUploadDetails"><a href="#bulkuploadDetails" onclick="showBrowseDetailsIdropLite()"><g:message code="text.bulk.upload" /></a></li>
		
		<li id="menuAddToCartDetails"><a href="#addToCartDetails" onclick="addToCartViaBrowseDetailsToolbar()"><g:message code="text.add.to.cart" /></a></li>
		</g:if>
		
	</ul>
	</li>
	<li id="menuSharingDetails"><a href="#sharingDetails"><g:message code="text.sharing"/></a>
	<ul>
		<li id="menuCreateTicketDetails"><a href="#createTicketDetails"><g:message code="text.create.ticket" /></a></li>
		<li id="menuShareWithUsersDetails"><a href="#shareWithUsersDetails"><g:message code="text.share" /></a></li>
	</ul>
	</li>
	<li id="menuBulkActionDetails"><a href="#applyActionToAllDetails"><g:message code="text.apply.to.all"/></a>
	<ul>
	
	<li id="menuAddToCartDetails"><a href="#addAllToCartDetails" onclick="addSelectedToCart()"><g:message code="text.add.all.to.cart" /></a></li>
	<li id="menuDeleteDetails"><a href="#deleteAllDetails" onclick="deleteSelected()"><g:message code="text.delete.all" /></a></li>
	</ul>

	</li>
	</ul>
</div>
<script>
$(function() {
	$("ul.sf-menu").superfish();
});

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

</script>
