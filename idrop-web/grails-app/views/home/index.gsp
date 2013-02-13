<head>
<meta name="layout" content="mainNoSidebar" />
<g:javascript library="mydrop/home" />
</head>
<div>

	<div id="uploadDialog">
		<!--  target div for creating upload dialogs -->
	</div>
	<div class="row-fluid">
		<div class="span2">
			<!--  nav for table -->
			<ul class="nav nav-list">
				<li class="nav-header"><g:message code="text.tools" /></li>
				<li id="quickUpload" class="quicknav"><a href="#"  onclick="quickUploadFromHome()"><g:message code="text.quick.upload" /></a></li>
				<li class="nav-header"><g:message code="text.starred" /></li>
				<li id="quickStarredFiles" class="quicknav"><a href="#"  onclick="quickViewShowStarredFiles()"><g:message code="text.starred.files" /></a></li>
				<li id="quickStarredFolders" class="quicknav"><a href="#" onclick="quickViewShowStarredFolders()"> <g:message code="text.starred.folders" /></a></li>
				 <g:if test="${shareSupported}">
					<li class="nav-header">Shared</li>
					<li id="quickSharedByMeFolders" class="quicknav"><a href="#" onclick="quickViewShowFoldersSharedByMe()"><g:message code="text.folders.shared.by.me" /></a></li>
					<li id="quickSharedWithMeFolders" class="quicknav"><a href="#" onclick="quickViewShowFoldersSharedWithMe()"><g:message code="text.folders.shared.with.me" /></a></li>
				</g:if>
			</ul>
		</div>
		<div id="quickViewListContainer" class="span8"></div>
	</div>
</div>
<script type="text/javascript">
	$(document).ready(function() {

		$.ajaxSetup({
			cache : false
		});
		$("#topbarHome").addClass("active");
		quickViewShowStarredFiles();

	});

	function quickViewShowStarredFolders() {
		resetQuicknav();
		$("#quickStarredFolders").addClass("active");
		var url = "/home/starredCollections";
		var params = {
				
			}
		lcSendValueWithParamsAndPlugHtmlInDiv(url, params, "#quickViewListContainer", null);
	}

	function quickViewShowStarredFiles() {
		resetQuicknav();
		$("#quickStarredFiles").addClass("active");
		var url = "/home/starredDataObjects";
		var params = {
				
			}
		lcSendValueWithParamsAndPlugHtmlInDiv(url, params, "#quickViewListContainer", null);
	}

	function quickViewShowFoldersSharedByMe() {
		resetQuicknav();
		$("#quickSharedByMeFolders").addClass("active");
		var url = "/home/sharedCollectionsByMe";
		var params = {
				
			}
		lcSendValueWithParamsAndPlugHtmlInDiv(url, params, "#quickViewListContainer", null);
	}

	function quickViewShowFoldersSharedWithMe() {
		resetQuicknav();
		$("#quickSharedWithMeFolders").addClass("active");
		var url = "/home/sharedCollectionsWithMe";
		var params = {
				
			}
		lcSendValueWithParamsAndPlugHtmlInDiv(url, params, "#quickViewListContainer", null);
	}

	function resetQuicknav() {
		$(".quicknav").removeClass("active");
	}

	function quickUploadFromHome() {
		
		showQuickUploadDialog();

		
	}

	
</script>