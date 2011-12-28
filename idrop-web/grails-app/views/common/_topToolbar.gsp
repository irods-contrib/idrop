<div id="topToolbar" style="height:100%;overflow:visible;margin-left:auto;margin-right:auto;">

<ul id="topToolbarMenu" class="sf-menu">
	<li id="menuRefresh"><a href="#refresh" onclick="refreshTree()"><g:message code="text.refresh"/></a></li>
	<li id="menuView"><a href="#view"><g:message code="text.view"/></a>
	<ul>
		<li id="menuBrowseView"><a href="#browseView" onclick="browseView()"><g:radio id="viewGroup" name="viewGroup" value="browse" onclick="setDefaultViewBrowse()"/><g:message code="text.browse" /></a></li>
		<li id="menuInfoView"><a href="#infoView" onclick="infoView()"><g:radio  id="viewGroup" name="viewGroup" value="info" onclick="setDefaultViewInfo()"/><g:message code="text.info" /></a></li>
		<li id="menuSharingView"><a href="#sharingView" onclick="sharingView()"><g:radio  id="viewGroup" name="viewGroup" value="Sharing" onclick="setDefaultViewSharing()"/><g:message code="text.sharing" /></a></li>
		<li id="menuMetadataView"><a href="#metadataView" onclick="metadataView()"><g:radio  id="viewGroup" name="viewGroup" value="Metadata"  onclick="setDefaultViewMetadata()"/><g:message code="text.metadata" /></a></li>
		<li id="menuGalleryView"><a href="#galleryView" onclick="galleryView()"><g:radio  id="viewGroup" name="viewGroup" value="Gallery"  onclick="setDefaultViewGallery()"/><g:message code="text.gallery" /></a></li>
	</ul>
	</li>

	</ul>
</div>
<script>
$(function() {
	$("ul.sf-menu").superfish();
});

/**
 * Set the sticky view mode to info
 */
function setDefaultViewInfo() {
	browseOptionVal = "info";
}

/**
 * Set the sticky view mode to sharing
 */
function setDefaultViewSharing() {
	browseOptionVal = "sharing";
}

/**
 * Set the sticky view mode to browse
 */
function setDefaultViewBrowse() {
	browseOptionVal = "browse";
}

/**
 * Set the sticky view mode to metadata
 */
function setDefaultViewMetadata() {
	browseOptionVal = "metadata";
}

/**
 * Set the sticky view mode to gallery
 */
function setDefaultViewGallery() {
	browseOptionVal = "gallery";
}

/**
 * browse view selected
 */
function browseView() {
	showBrowseView(selectedPath);

}

/**
 * Show the info view
 */
function infoView() {
	showInfoView(selectedPath);
}

/**
 * Show the sharing (ACL) view
 */
function sharingView() {
	showSharingView(selectedPath);
}

/**
 * Show the metadata (AVU) view
 */
function metadataView() {
	showMetadataView(selectedPath);
}

/**
 * Show the gallery (photo) view
 */
function galleryView() {
	showGalleryView(selectedPath);
}

</script>